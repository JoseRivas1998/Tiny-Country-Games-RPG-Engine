package com.tcg.rpgengine.editor.context;

import com.badlogic.gdx.files.FileHandle;
import com.tcg.rpgengine.common.data.Project;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Optional;

public class CurrentProject {

    private String projectFilePath;
    private Project project;

    private CurrentProject(String projectFilePath, Project project) {
        this.projectFilePath = projectFilePath;
        this.project = project;
    }

    public String getTitle() {
        return this.project.title;
    }

    public String getProjectFilePath() {
        return this.projectFilePath;
    }

    static Optional<CurrentProject> selectAndOpenProject() {
        final Optional<File> optionalProjectFile = selectFile();
        if (optionalProjectFile.isPresent()) {
            ApplicationContext context = ApplicationContext.context();
            final File projectFile = optionalProjectFile.get();
            return Optional.of(openProject(context.files.absolute(projectFile.getAbsolutePath())));
        } else {
            return Optional.empty();
        }
    }

    private static Optional<File> selectFile() {
        ApplicationContext context = ApplicationContext.context();
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(context.files.external("").file());
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TCG Project File",
                "*." + ApplicationContext.Constants.PROJECT_FILE_EXTENSION));
        return Optional.ofNullable(fileChooser.showOpenDialog(context.primaryStage));
    }

    static CurrentProject openProject(FileHandle projectFile) {
        validateProjectFile(projectFile);
        return new CurrentProject(projectFile.path(), Project.generateFromJSON(projectFile.readString()));
    }

    private static void validateProjectFile(FileHandle projectFile) {
        if (!projectFile.exists()) {
            throw new IllegalArgumentException("The provided file does not exist.");
        }
        if(!projectFile.extension().equalsIgnoreCase(ApplicationContext.Constants.PROJECT_FILE_EXTENSION)) {
            throw new IllegalArgumentException("You must select a .tcgproj file.");
        }
    }

}
