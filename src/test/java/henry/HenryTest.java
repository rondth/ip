package henry;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import henry.parser.CommandType;

/**
 * Tests Henry's end-to-end command processing API used by the GUI.
 */
public class HenryTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getResponse_addAndListTask_returnsExpectedResponses() {
        Henry henry = new Henry(temporaryDirectory.resolve("henry.txt"));

        String addResponse = henry.getResponse("todo borrow book");
        String listResponse = henry.getResponse("list");

        assertEquals(" Got it. I've added this task:\n"
                + "   [T][ ] borrow book\n"
                + " Now you have 1 tasks in the list.", addResponse);
        assertEquals(" Here are the tasks in your list:\n"
                + " 1.[T][ ] borrow book", listResponse);
        assertEquals(CommandType.LIST, henry.getLastCommandType());
    }

    @Test
    public void getResponse_invalidCommand_returnsErrorWithoutThrowing() {
        Henry henry = new Henry(temporaryDirectory.resolve("henry.txt"));

        String response = henry.getResponse("dance");

        assertEquals("I don't recognise that command. Try todo, deadline, event, list, find, "
                + "mark, unmark, delete, or bye.", response);
        assertEquals(CommandType.UNKNOWN, henry.getLastCommandType());
    }

    @Test
    public void getResponse_saveFailures_rollsBackAllTaskChanges() throws IOException {
        Path dataFile = temporaryDirectory.resolve("henry.txt");
        Henry henry = new Henry(dataFile);
        henry.getResponse("todo existing task");
        Files.delete(dataFile);
        Files.createDirectory(dataFile);

        String addResponse = henry.getResponse("todo unsaved task");
        String markResponse = henry.getResponse("mark 1");
        String deleteResponse = henry.getResponse("delete 1");
        String listResponse = henry.getResponse("list");

        String saveError = "I couldn't save your tasks. Your last change was not applied.";
        assertEquals(saveError, addResponse);
        assertEquals(saveError, markResponse);
        assertEquals(saveError, deleteResponse);
        assertEquals(" Here are the tasks in your list:\n"
                + " 1.[T][ ] existing task", listResponse);
    }
}
