package com.tcg.rpgengine.editor.components;

import com.tcg.rpgengine.editor.context.ApplicationContext;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Pair;

public class RecentProjectsListView extends ListView<Pair<String, String>> {

    public RecentProjectsListView() {
        super();
        this.setCellFactory(param -> new RecentProjectCell());
        this.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private static class RecentProjectCell extends ListCell<Pair<String, String>> {
        @Override
        protected void updateItem(Pair<String, String> item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                final Label projectTitle = new Label(item.getKey());
                projectTitle.setStyle("-fx-font-size: 2em;");
                final Label projectPath = new Label(item.getValue());
                final VBox vBox = new VBox(ApplicationContext.Constants.SPACING);
                vBox.getChildren().addAll(projectTitle, projectPath);
                this.setGraphic(vBox);
            }
        }
    }

}
