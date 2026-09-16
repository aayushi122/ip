package todd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests command processing shared by Todd's text and graphical interfaces. */
public class ToddTest {
    @TempDir
    private Path tempDirectory;

    @Test
    public void getResponse_addThenList_persistsAndDisplaysTask() {
        Path dataPath = tempDirectory.resolve("todd.txt");
        Todd todd = new Todd(dataPath);

        String addedResponse = todd.getResponse("todo read JavaFX guide");
        String listResponseAfterReload = new Todd(dataPath).getResponse("list");

        assertTrue(addedResponse.contains("read JavaFX guide"));
        assertTrue(listResponseAfterReload.contains("[T][ ] read JavaFX guide"));
    }

    @Test
    public void getResponse_invalidCommand_returnsHelpfulError() {
        Todd todd = new Todd(tempDirectory.resolve("todd.txt"));

        String response = todd.getResponse("dance");

        assertTrue(response.contains("What does that mean dawg"));
    }

    @Test
    public void getResponse_markThenDelete_updatesStoredTaskList() {
        Path dataPath = tempDirectory.resolve("todd.txt");
        Todd todd = new Todd(dataPath);
        todd.getResponse("todo read JavaFX guide");

        String markedResponse = todd.getResponse("mark 1");
        String savedMarkedTask = new Todd(dataPath).getResponse("list");
        String deletedResponse = todd.getResponse("delete 1");
        String savedEmptyList = new Todd(dataPath).getResponse("list");

        assertTrue(markedResponse.contains("[T][X] read JavaFX guide"));
        assertTrue(savedMarkedTask.contains("[T][X] read JavaFX guide"));
        assertTrue(deletedResponse.contains("read JavaFX guide"));
        assertTrue(savedEmptyList.contains("Your list is empty"));
    }

    @Test
    public void getResponse_help_returnsCommandGuide() {
        Todd todd = new Todd(tempDirectory.resolve("todd.txt"));

        String response = todd.getResponse("help");

        assertTrue(response.contains("deadline DESCRIPTION /by DATE [HHmm]"));
        assertTrue(response.contains("reminders | help | bye"));
        assertTrue(response.contains("today, tomorrow, or a weekday"));
    }
    @Test
    public void getResponse_blankOrUnexpectedArguments_returnsGuidance() {
        Todd todd = new Todd(tempDirectory.resolve("todd.txt"));
        assertTrue(todd.getResponse("  ").contains("Type a command first"));
        assertTrue(todd.getResponse(null).contains("Type a command first"));
        for (String command : new String[]{"list", "help", "reminders", "bye"}) {
            assertTrue(todd.getResponse(command + " extra").contains("Use just " + command));
        }
    }

    @Test
    public void getResponse_invalidAdd_doesNotChangeMemoryOrFile() throws IOException {
        Path dataPath = tempDirectory.resolve("todd.txt");
        Todd todd = new Todd(dataPath);
        todd.getResponse("todo original");
        String original = Files.readString(dataPath);
        todd.getResponse("todo a | b");
        todd.getResponse("deadline report /by 2026-02-30");
        todd.getResponse("event meeting /from today /to today");
        assertEquals(original, Files.readString(dataPath));
        assertEquals(new Todd(dataPath).getResponse("list"), todd.getResponse("list"));
    }

    @Test
    public void getResponse_corruptSave_preservesFileAndBlocksChanges() throws IOException {
        Path dataPath = tempDirectory.resolve("todd.txt");
        String original = "T | 0 | original\ninvalid data\n";
        Files.writeString(dataPath, original);
        Todd todd = new Todd(dataPath);
        assertTrue(todd.getWelcomeMessage().contains("line 2"));
        for (String command : new String[]{"todo new", "mark 1", "unmark 1", "delete 1",
            "deadline report /by today", "event meeting /from today /to tomorrow"}) {
            assertTrue(todd.getResponse(command).contains("changes are disabled"));
        }
        assertEquals(original, Files.readString(dataPath));
    }

    @Test
    public void getResponse_failedSaves_rollBackAllMutationsAndAllowRetry() throws IOException {
        Path dataPath = tempDirectory.resolve("todd.txt");
        Todd todd = new Todd(dataPath);
        todd.getResponse("todo first");
        todd.getResponse("todo second");
        todd.getResponse("mark 2");
        String originalList = todd.getResponse("list");
        Path backup = tempDirectory.resolve("backup.txt");
        Files.move(dataPath, backup);
        // A nonempty directory at the save path causes a deterministic failure on every OS.
        Files.createDirectory(dataPath);
        Files.writeString(dataPath.resolve("keep.txt"), "keep");
        for (String command : new String[]{"todo new", "mark 1", "unmark 2", "delete 1"}) {
            assertTrue(todd.getResponse(command).contains("couldn't save"));
            assertEquals(originalList, todd.getResponse("list"));
            assertEquals("keep", Files.readString(dataPath.resolve("keep.txt")));
        }
        Files.delete(dataPath.resolve("keep.txt"));
        Files.delete(dataPath);
        Files.move(backup, dataPath);
        assertTrue(todd.getResponse("todo retry").contains("I've added"));
        assertTrue(new Todd(dataPath).getResponse("list").contains("retry"));
        assertFalse(new Todd(dataPath).getResponse("list").contains("[T][ ] new"));
    }
}
