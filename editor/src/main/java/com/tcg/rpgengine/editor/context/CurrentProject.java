package com.tcg.rpgengine.editor.context;

import com.badlogic.gdx.files.FileHandle;
import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.Project;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Optional;

public class CurrentProject {

    private String projectFilePath;
    private Project project;
    public final AssetLibrary assetLibrary;

    private CurrentProject(String projectFilePath, Project project) {
        this.projectFilePath = projectFilePath;
        this.project = project;
        this.assetLibrary = this.loadAssetLibrary();
    }

    private AssetLibrary loadAssetLibrary() {
        return AssetLibrary.fromJSON(this.getAssetLibFileHandle().readString());
    }

    private FileHandle getAssetLibFileHandle() {
        return this.getProjectFileHandle().sibling(ApplicationContext.Constants.ASSET_LIB_FILE_NAME);
    }

    public FileHandle getProjectFileHandle() {
        return ApplicationContext.context().files.absolute(this.projectFilePath);
    }

    public String getTitle() {
        return this.project.title;
    }

    public String getProjectFilePath() {
        return this.projectFilePath;
    }

    public void saveAssetLibrary() {
        this.getAssetLibFileHandle().writeString(this.assetLibrary.jsonString(), false);
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
        return new CurrentProject(projectFile.file().getAbsolutePath(),
                Project.generateFromJSON(projectFile.readString()));
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
