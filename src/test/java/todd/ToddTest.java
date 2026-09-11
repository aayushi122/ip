package todd;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
