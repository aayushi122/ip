package todd;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/** Controls Todd's main chat window. */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Todd todd;

    /** Keeps the most recent dialog visible as the conversation grows. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Supplies the application instance that processes commands. */
    public void setTodd(Todd todd) {
        this.todd = todd;
        dialogContainer.getChildren().add(DialogBox.getToddDialog(todd.getWelcomeMessage()));
    }

    /** Sends the typed command to Todd and displays both sides of the exchange. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        String response = todd.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getToddDialog(response));
        userInput.clear();
    }
}
