package com.tcg.rpgengine.editor.containers;

import com.badlogic.gdx.files.FileHandle;
import com.tcg.rpgengine.editor.components.RecentProjectsListView;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.lwjgl.openal.AL;

public class WelcomePage extends GridPane {

    private final BorderPane parent;

    public WelcomePage(BorderPane parent) {
        super();
        this.setHgap(ApplicationContext.Constants.SPACING);
        this.setVgap(ApplicationContext.Constants.SPACING);
        this.parent = parent;


        final Button newProject = new Button("New Project");
        newProject.setOnAction(event -> this.parent.setCenter(new NewProjectPage(this.parent)));

        final Button open = new Button("Open");
        open.setOnAction(event -> ApplicationContext.context().selectAndOpenProject());

        final HBox buttonRow = new HBox(ApplicationContext.Constants.SPACING);
        buttonRow.getChildren().addAll(newProject, open);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);
        GridPane.setHgrow(buttonRow, Priority.ALWAYS);
        this.add(buttonRow, 0, 0);


        final RecentProjectsListView recentProjectsListView = new RecentProjectsListView();
        recentProjectsListView.getItems().setAll(ApplicationContext.context().appData.getRecentlyOpenProjects());
        recentProjectsListView.getSelectionModel()
                .selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            ApplicationContext context = ApplicationContext.context();
            final FileHandle filePath = context.files.absolute(newValue.getValue());
            context.openProject(filePath);
        });
        GridPane.setHgrow(recentProjectsListView, Priority.ALWAYS);
        GridPane.setVgrow(recentProjectsListView, Priority.ALWAYS);
        this.add(recentProjectsListView, 0, 1);


    }

}
