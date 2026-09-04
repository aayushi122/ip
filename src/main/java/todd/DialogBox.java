package todd;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/** Displays one message together with a label identifying its speaker. */
public class DialogBox extends HBox {
    @FXML
    private Label speaker;
    @FXML
    private Label dialog;

    private DialogBox(String speakerName, String text) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the dialog box layout", e);
        }

        speaker.setText(speakerName);
        dialog.setText(text);
    }

    /** Creates a right-aligned dialog containing a command from the user. */
    public static DialogBox getUserDialog(String text) {
        DialogBox box = new DialogBox("You", text);
        box.getStyleClass().add("user-dialog");
        return box;
    }

    /** Creates a left-aligned dialog containing Todd's response. */
    public static DialogBox getToddDialog(String text) {
        DialogBox box = new DialogBox("Todd", text);
        box.setAlignment(Pos.TOP_LEFT);
        box.getStyleClass().add("todd-dialog");
        return box;
    }
}
