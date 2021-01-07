package com.tcg.rpgengine.editor.containers;

import com.badlogic.gdx.files.FileHandle;
import com.tcg.rpgengine.common.data.Project;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import com.tcg.rpgengine.editor.dialogs.ErrorDialog;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import org.json.JSONObject;

import java.io.File;
import java.util.Optional;
import java.util.stream.Stream;

public class NewProjectPage extends BorderPane {

    private final BorderPane parent;
    private final TextField titleField;
    private final TextField destinationField;

    public NewProjectPage(BorderPane parent) {
        super();
        this.parent = parent;
        this.titleField = new TextField();
        this.destinationField = new TextField();
        this.setCenter(this.buildMainForm());
        this.setBottom(this.buildButtonRow());

    }

    private HBox buildButtonRow() {
        final HBox buttonRow = new HBox(ApplicationContext.Constants.SPACING);
        buttonRow.getChildren().addAll(this.buildCreateButton(), this.buildCancelButton());
        buttonRow.setAlignment(Pos.CENTER_RIGHT);
        return buttonRow;
    }

    private Button buildCreateButton() {
        final Button create = new Button("Create");
        create.setOnAction(event -> this.validateAndGenerateProjectFile());
        return create;
    }

    private void validateAndGenerateProjectFile() {
        final ApplicationContext context = ApplicationContext.context();
        try {
            this.validateInputFields();
            final FileHandle destination = context.files.absolute(this.destinationField.getText());
            this.validateDestinationFolder(destination);
            if (this.confirmNonEmptyFolder(destination)) {
                this.generateProjectFile(this.titleField.getText(), destination);
            }
        } catch (Exception exception) {
            final ErrorDialog errorDialog = new ErrorDialog(exception);
            errorDialog.initOwner(context.primaryStage);
            errorDialog.showAndWait();
        }
    }

    private void generateProjectFile(String title, FileHandle destination) {
        final String formattedTitle = title.trim().toLowerCase().replaceAll("\\s+", "-");
        final String projectFileName = String.format("%s.%s",
                formattedTitle, ApplicationContext.Constants.PROJECT_FILE_EXTENSION);
        final FileHandle projectFile = destination.child(projectFileName);
        this.deleteProjectFileIfExists(projectFile);
        final Project project = Project.generateNewProject(title);
        projectFile.writeString(project.toString(), false, "UTF-8");
    }

    private void deleteProjectFileIfExists(FileHandle projectFile) {
        if(projectFile.exists()) {
            projectFile.delete();
        }
    }

    private void validateDestinationFolder(FileHandle destination) {
        this.verifyDestinationExists(destination);
        this.verifyDestinationIsDirectory(destination);
    }

    private void validateInputFields() {
        this.verifyTitleFieldIsNotBlank();
        this.verifyDestinationFieldIsNotBlank();
    }

    private boolean confirmNonEmptyFolder(FileHandle destination) {
        boolean shouldContinue = true;
        if (destination.list().length > 0) {
            final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Destination Not Empty");
            alert.setHeaderText(null);
            alert.setContentText("The destination folder is not empty, would you like to continue creating project?");
            alert.initOwner(ApplicationContext.context().primaryStage);
            final Optional<ButtonType> result = alert.showAndWait();
            shouldContinue = result.isPresent() && result.get() == ButtonType.OK;
        }
        return shouldContinue;
    }

    private void verifyDestinationIsDirectory(FileHandle destination) {
        if (!destination.isDirectory()) {
            throw new IllegalArgumentException("Destination must be a folder.");
        }
    }

    private void verifyDestinationExists(FileHandle destination) {
        if (!destination.exists()) {
            throw new IllegalArgumentException("Destination must exist.");
        }
    }

    private void verifyDestinationFieldIsNotBlank() {
        if (this.destinationField.getText().isBlank()) {
            throw new IllegalArgumentException("Destination field cannot be blank.");
        }
    }

    private void verifyTitleFieldIsNotBlank() {
        if (this.titleField.getText().isBlank()) {
            throw new IllegalArgumentException("Title field cannot be blank.");
        }
    }

    private Button buildCancelButton() {
        final Button cancel = new Button("Cancel");
        cancel.setOnAction(event -> this.parent.setCenter(new WelcomePage(this.parent)));
        return cancel;
    }

    private GridPane buildMainForm() {
        final GridPane mainForm = new GridPane();
        mainForm.setHgap(ApplicationContext.Constants.SPACING);
        mainForm.setVgap(ApplicationContext.Constants.SPACING);
        this.addTitleField(mainForm);
        this.addDestinationField(mainForm);
        return mainForm;
    }

    private void addDestinationField(GridPane mainForm) {
        mainForm.add(new Label("Destination:"), 0, 1);
        GridPane.setHgrow(this.destinationField, Priority.ALWAYS);
        mainForm.add(this.destinationField, 1, 1);
        mainForm.add(this.buildSelectButton(), 2, 1);
    }

    private void addTitleField(GridPane mainForm) {
        mainForm.add(new Label("Title:"), 0, 0);
        GridPane.setHgrow(this.titleField, Priority.ALWAYS);
        GridPane.setColumnSpan(this.titleField, 2);
        mainForm.add(this.titleField, 1, 0);
    }

    private Button buildSelectButton() {
        final Button select = new Button("Select");
        select.setOnAction(event -> {
            final String selectedDirectory = this.selectDirectory();
            if (this.titleField.getText().isBlank()) {
                final ApplicationContext context = ApplicationContext.context();
                final FileHandle selectedDirectoryHandle = context.files.absolute(selectedDirectory);
                this.titleField.setText(selectedDirectoryHandle.name());
            }
            this.destinationField.setText(selectedDirectory);
        });
        return select;
    }

    private String selectDirectory() {
        final ApplicationContext context = ApplicationContext.context();
        final FileHandle homeDirectory = context.files.external("");
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(homeDirectory.file());
        final File file = directoryChooser.showDialog(context.primaryStage);
        return file.getAbsolutePath();
    }
}
