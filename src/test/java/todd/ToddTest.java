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
}
