package com.tcg.rpgengine.editor.components.databasemanager;

import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.assets.TiledImageAsset;
import com.tcg.rpgengine.common.data.database.Database;
import com.tcg.rpgengine.common.data.database.entities.Element;
import com.tcg.rpgengine.common.data.database.entitycollections.Elements;
import com.tcg.rpgengine.common.data.misc.IconCell;
import com.tcg.rpgengine.common.data.misc.RowColumnPair;
import com.tcg.rpgengine.editor.components.IconButton;
import com.tcg.rpgengine.editor.components.SimpleEntityListView;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import com.tcg.rpgengine.editor.dialogs.ErrorDialog;
import com.tcg.rpgengine.editor.dialogs.TileSelectDialog;
import javafx.beans.Observable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.util.Objects;
import java.util.Optional;

public class ElementsTab extends Tab {


    private final SimpleEntityListView<Element> elementListView;
    private final Button nameBtn;
    private final IconButton iconButton;

    public ElementsTab(Window owner) {
        super("Elements");
        this.setClosable(false);

        final HBox layout = new HBox(ApplicationContext.Constants.SPACING);
        layout.setPadding(new Insets(ApplicationContext.Constants.PADDING));

        this.elementListView = this.buildListView();
        this.nameBtn = this.buildNameButton(owner);
        this.iconButton = this.buildIconButton(owner);

        final GridPane editor = new GridPane();
        editor.setHgap(ApplicationContext.Constants.SPACING);
        editor.setVgap(ApplicationContext.Constants.SPACING);

        editor.add(new Label("Name:"), 0, 0);
        editor.add(this.nameBtn, 0, 1);
        editor.add(new Label("Icon:"), 1,0);
        editor.add(this.iconButton, 1, 1);
        HBox.setHgrow(editor, Priority.ALWAYS);

        final Button removeBtn = new Button("Remove");
        removeBtn.setOnAction(event -> {
            this.getSelectedElement().ifPresent(selectedElement -> {
                try {
                    final ApplicationContext context = ApplicationContext.context();
                    final AssetLibrary assetLibrary = context.currentProject.assetLibrary;
                    final Database database = context.currentProject.database;
                    database.elements.remove(assetLibrary, selectedElement);
                    context.currentProject.saveElements();
                    final int selectedIndex = this.elementListView.getSelectionModel().getSelectedIndex();
                    this.elementListView.getItems().remove(selectedIndex);
                    this.elementListView
                            .getSelectionModel()
                            .select(Math.min(selectedIndex, this.elementListView.getItems().size() - 1));
                } catch (Exception e) {
                    ErrorDialog.showErrorDialog(e, owner);
                }
            });
        });
        removeBtn.disableProperty().bind(this.elementListView.getSelectionModel().selectedItemProperty().isNull());

        final HBox sideButtons = new HBox(5, this.buildAddBtn(owner), removeBtn);
        sideButtons.setAlignment(Pos.CENTER_RIGHT);

        final VBox sidePanel = new VBox(5, this.elementListView, sideButtons);

        layout.getChildren().addAll(sidePanel, editor);

        this.setContent(layout);

    }

    private Button buildAddBtn(Window owner) {
        final Button addBtn = new Button("Add");
        addBtn.setOnAction(event -> this.addElement(owner));
        return addBtn;
    }

    private void addElement(Window owner) {
        try {
            final ApplicationContext context = ApplicationContext.context();
            final AssetLibrary assetLibrary = context.currentProject.assetLibrary;
            final TiledImageAsset iconPage = assetLibrary.getAllIconPages().get(0);
            final IconCell iconCell = IconCell.createNewIconCell(assetLibrary, iconPage.id, 0, 0);
            final Element newElement = Element.createNewElement("New Element", iconCell);
            context.currentProject.database.elements.add(newElement);
            context.currentProject.saveElements();
            this.elementListView.getItems().add(newElement);
            this.elementListView.getSelectionModel().select(this.elementListView.getItems().size() - 1);
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, owner);
        }
    }

    private SimpleEntityListView<Element> buildListView() {
        final SimpleEntityListView<Element> listView = new SimpleEntityListView<>(Element::getName);
        listView.getItems().setAll(ApplicationContext.context().currentProject.database.elements.getAll());
        listView.getSelectionModel().select(0);
        listView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    Optional.ofNullable(newValue).ifPresent(this::updateFormValues);
                });
        VBox.setVgrow(listView, Priority.ALWAYS);
        return listView;
    }

    private void updateFormValues(Element selectedElement) {
        this.nameBtn.setText(selectedElement.getName());
        final RowColumnPair iconIndex = selectedElement.getIconIndex();
        final AssetLibrary assetLibrary = ApplicationContext.context().currentProject.assetLibrary;
        final int row = iconIndex.row;
        final int column = iconIndex.column;
        this.iconButton.updateIcon(assetLibrary, selectedElement.getIconPageId(), row, column);
    }

    private Button buildNameButton(Window owner) {
        final Button nameBtn = new Button(this.elementListView.getSelectionModel().getSelectedItem().getName());
        nameBtn.setMaxWidth(Double.MAX_VALUE);
        nameBtn.setOnAction(event -> this.updateSelectedElementName(owner, nameBtn));
        return nameBtn;
    }

    private IconButton buildIconButton(Window owner) {
        final Element selectedElement = this.elementListView.getSelectionModel().getSelectedItem();
        final RowColumnPair iconIndex = selectedElement.getIconIndex();
        final AssetLibrary assetLibrary = ApplicationContext.context().currentProject.assetLibrary;
        final int row = iconIndex.row;
        final int column = iconIndex.column;
        final IconButton iconButton = new IconButton(assetLibrary, selectedElement.getIconPageId(), row, column);
        iconButton.setOnAction(event -> this.updateSelectedElementIcon(owner, iconButton));
        return iconButton;
    }

    private void updateSelectedElementName(Window owner, Button nameBtn) {
        this.getSelectedElement().ifPresent(element -> this.updateElementName(owner, nameBtn, element));
    }

    private void updateElementName(Window owner, Button nameBtn, Element element) {
        try {
            final TextInputDialog nameDialog = new TextInputDialog(element.getName());
            nameDialog.setTitle("Element Name");
            nameDialog.setHeaderText(null);
            nameDialog.showAndWait().ifPresent(newName -> {
                this.verifyNewName(newName);
                final ApplicationContext context = ApplicationContext.context();
                final Element dbElement = context.currentProject.database.elements.get(element.id);
                dbElement.setName(newName.trim());
                context.currentProject.saveElements();
                nameBtn.setText(dbElement.getName());
                this.updateSelectedItem(dbElement);
            });
        } catch (Exception e) {
            final ErrorDialog errorDialog = new ErrorDialog(e);
            errorDialog.initOwner(owner);
            errorDialog.showAndWait();
        }
    }

    private void verifyNewName(String newName) {
        if (newName.isBlank()) {
            throw new IllegalArgumentException("Element name cannot be blank.");
        }
    }

    private void updateSelectedElementIcon(Window owner, IconButton iconButton) {
        this.getSelectedElement().ifPresent(element -> this.updateElementIcon(owner, iconButton, element));
    }

    private void updateElementIcon(Window owner, IconButton iconButton, Element element) {
        try {
            final ApplicationContext context = ApplicationContext.context();
            final AssetLibrary assetLibrary = context.currentProject.assetLibrary;
            final TileSelectDialog selectIconDialog = new TileSelectDialog(
                    "Select Icon",
                    assetLibrary.getAllIconPages(),
                    element.getIconPageId(),
                    element.getIconIndex()
            );

            selectIconDialog.initOwner(owner);
            selectIconDialog.showAndWait().ifPresent(result -> {
                final Element dbElement = context.currentProject.database.elements.get(element.id);
                final TiledImageAsset newIcon = assetLibrary.getIconPageById(result.getKey());
                final RowColumnPair newIconIndex = result.getValue();
                dbElement.setIcon(assetLibrary, newIcon.id, newIconIndex.row, newIconIndex.column);
                context.currentProject.saveElements();
                iconButton.updateIcon(assetLibrary, newIcon.id, newIconIndex.row, newIconIndex.column);
                this.updateSelectedItem(dbElement);
            });
        } catch (Exception e) {
            final ErrorDialog errorDialog = new ErrorDialog(e);
            errorDialog.initOwner(owner);
            errorDialog.showAndWait();
        }
    }

    private void updateSelectedItem(Element dbElement) {
        final int selectedIndex = this.elementListView.getSelectionModel().getSelectedIndex();
        this.elementListView.getItems().set(selectedIndex, dbElement);
        this.elementListView.getSelectionModel().select(selectedIndex);
    }

    private Optional<Element> getSelectedElement() {
        return Optional.ofNullable(this.elementListView.getSelectionModel().getSelectedItem());
    }

}
