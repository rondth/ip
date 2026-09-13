package henry.gui;

import java.io.IOException;

import henry.parser.CommandType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Represents one chat message together with the speaker's avatar.
 */
public class DialogBox extends HBox {
    private static final double HENRY_DIALOG_HORIZONTAL_SPACE = 52.0;
    private static final double USER_DIALOG_WIDTH_RATIO = 0.78;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        FXMLLoader fxmlLoader = new FXMLLoader(
                MainWindow.class.getResource("/view/DialogBox.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the dialog box layout.", e);
        }

        dialog.setText(text);
        displayPicture.setImage(image);
    }

    /**
     * Creates a dialog displayed on the user's side of the conversation.
     *
     * @param text message text.
     * @param image user's avatar.
     * @return user dialog box.
     */
    public static DialogBox createUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.dialog.maxWidthProperty().bind(
                dialogBox.widthProperty().multiply(USER_DIALOG_WIDTH_RATIO));
        return dialogBox;
    }

    /**
     * Creates a dialog displayed on Henry's side of the conversation.
     *
     * @param text message text.
     * @param image Henry's avatar.
     * @param commandType command type used to style the response.
     * @return Henry dialog box.
     */
    public static DialogBox createHenryDialog(String text, Image image, CommandType commandType) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.dialog.maxWidthProperty().bind(
                dialogBox.widthProperty().subtract(HENRY_DIALOG_HORIZONTAL_SPACE));
        dialogBox.flip();
        dialogBox.changeDialogStyle(commandType);
        return dialogBox;
    }

    private void flip() {
        getChildren().setAll(displayPicture, dialog);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    private void changeDialogStyle(CommandType commandType) {
        switch (commandType) {
            case TODO:
            case DEADLINE:
            case EVENT:
                dialog.getStyleClass().add("add-label");
                break;
            case MARK:
            case UNMARK:
                dialog.getStyleClass().add("marked-label");
                break;
            case DELETE:
                dialog.getStyleClass().add("delete-label");
                break;
            default:
                break;
        }
    }
}
