package com.tcg.rpgengine.editor.components.databasemanager;

import com.tcg.rpgengine.common.data.AssetLibrary;
import com.tcg.rpgengine.common.data.assets.TiledImageAsset;
import com.tcg.rpgengine.common.data.database.Database;
import com.tcg.rpgengine.common.data.database.entities.Actor;
import com.tcg.rpgengine.common.data.misc.RowColumnPair;
import com.tcg.rpgengine.common.data.misc.SpritesheetCharacter;
import com.tcg.rpgengine.editor.components.CharacterCellButton;
import com.tcg.rpgengine.editor.components.SimpleEntityListView;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import com.tcg.rpgengine.editor.dialogs.ErrorDialog;
import com.tcg.rpgengine.editor.dialogs.TileSelectDialog;
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

import java.util.Optional;
import java.util.UUID;

public class ActorsTab extends Tab {


    private final SimpleEntityListView<Actor> actorListView;
    private final Button nameBtn;
    private final CharacterCellButton characterButton;

    public ActorsTab(Window owner) {
        super("Actors");
        this.setClosable(false);

        this.actorListView = this.buildListView();
        this.nameBtn = this.buildNameBtn(owner);
        this.characterButton = this.buildtCharacterButton(owner);

        final GridPane editor = new GridPane();
        editor.setHgap(ApplicationContext.Constants.SPACING);
        editor.setVgap(ApplicationContext.Constants.SPACING);

        editor.add(new Label("Name:"), 0, 0);
        editor.add(this.nameBtn, 0, 1);
        editor.add(new Label("Character:"), 1, 0);
        editor.add(this.characterButton, 1, 1);
        HBox.setHgrow(editor, Priority.ALWAYS);

        final Button remove = new Button("Remove");
        remove.disableProperty().bind(this.actorListView.getSelectionModel().selectedItemProperty().isNull());
        remove.setOnAction(event -> {
            this.getSelectedItem().ifPresent(selectedActor -> {
                try {
                    final ApplicationContext context = ApplicationContext.context();
                    final AssetLibrary assetLibrary = context.currentProject.assetLibrary;
                    final Database database = context.currentProject.database;
                    database.actors.remove(assetLibrary, selectedActor);
                    context.currentProject.saveActors();
                    final int selectedIndex = this.actorListView.getSelectionModel().getSelectedIndex();
                    this.actorListView.getItems().remove(selectedIndex);
                    this.actorListView.getSelectionModel()
                            .select(Math.min(selectedIndex, this.actorListView.getItems().size() - 1));
                } catch (Exception e) {
                    ErrorDialog.showErrorDialog(e, owner);
                }
            });
        });

        final HBox sideButtons = new HBox(ApplicationContext.Constants.SPACING,
                this.buildAddButton(owner),
                remove
        );
        sideButtons.setAlignment(Pos.CENTER_RIGHT);

        final VBox sidePanel = new VBox(ApplicationContext.Constants.SPACING, this.actorListView, sideButtons);

        final HBox layout = new HBox(ApplicationContext.Constants.SPACING, sidePanel, editor);
        layout.setPadding(new Insets(ApplicationContext.Constants.PADDING));

        this.setContent(layout);

    }

    private Button buildAddButton(Window owner) {
        final Button add = new Button("Add");
        add.setOnAction(event -> this.addActor(owner));
        return add;
    }

    private void addActor(Window owner) {
        try {
            final ApplicationContext context = ApplicationContext.context();
            final AssetLibrary assetLibrary = context.currentProject.assetLibrary;
            final TiledImageAsset spriteSheet = assetLibrary.getAllSpritesheetPages().get(0);
            final SpritesheetCharacter character = SpritesheetCharacter.createNewSpritesheetCharacter(
                    assetLibrary, spriteSheet.id, 0, 0
            );
            final Actor newActor = Actor.createNewActor("New Actor", character);
            context.currentProject.database.actors.add(newActor);
            context.currentProject.saveActors();
            this.actorListView.getItems().add(newActor);
            this.actorListView.getSelectionModel().select(this.actorListView.getItems().size() - 1);
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, owner);
        }
    }

    private CharacterCellButton buildtCharacterButton(Window owner) {
        final AssetLibrary assetLibrary = ApplicationContext.context().currentProject.assetLibrary;
        final Actor selectedItem = this.actorListView.getSelectionModel().getSelectedItem();
        final RowColumnPair characterIndex = selectedItem.getCharacterIndex();
        final UUID spritesheetPageId = selectedItem.getSpritesheetPageId();
        final CharacterCellButton characterButton = new CharacterCellButton(assetLibrary, spritesheetPageId,
                characterIndex.row, characterIndex.column,
                0, 1
        );
        characterButton.setOnAction(event -> this.updateSelectedActorCharacter(owner, characterButton));
        return characterButton;
    }

    private void updateSelectedActorCharacter(Window owner, CharacterCellButton characterButton) {
        this.getSelectedItem().ifPresent(actor -> this.updateActorCharacter(owner, characterButton, actor));
    }

    private void updateActorCharacter(Window owner, CharacterCellButton characterButton, Actor actor) {
        try {
            final ApplicationContext context = ApplicationContext.context();
            final AssetLibrary assetLibrary = context.currentProject.assetLibrary;
            final TileSelectDialog selectCharacterDialog = new TileSelectDialog(
                    "Select Character",
                    assetLibrary.getAllSpritesheetPages(),
                    actor.getSpritesheetPageId(),
                    actor.getCharacterIndex()
            );
            selectCharacterDialog.initOwner(owner);
            selectCharacterDialog.showAndWait().ifPresent(result -> {
                final Actor dbActor = context.currentProject.database.actors.get(actor.id);
                final TiledImageAsset spritesheet = assetLibrary.getSpritesheetPageAssetById(result.getKey());
                final RowColumnPair newCharacterIndex = result.getValue();
                dbActor.setCharacter(assetLibrary, spritesheet.id,
                        newCharacterIndex.row, newCharacterIndex.column);
                context.currentProject.saveActors();
                characterButton.updateIcon(assetLibrary, spritesheet.id,
                        newCharacterIndex.row, newCharacterIndex.column, 0, 1);
                this.updateSelectedItem(dbActor);
            });
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDialog.showErrorDialog(e, owner);
        }
    }

    private Button buildNameBtn(Window owner) {
        final Button nameBtn = new Button(this.actorListView.getSelectionModel().getSelectedItem().getName());
        nameBtn.setOnAction(event -> this.updateSelectedActorName(owner, nameBtn));
        return nameBtn;
    }

    private void updateSelectedActorName(Window owner, Button nameBtn) {
        this.getSelectedItem().ifPresent(actor -> this.updateActorName(owner, nameBtn, actor));
    }

    private void updateActorName(Window owner, Button nameBtn, Actor actor) {
        try {
            final TextInputDialog nameDialog = new TextInputDialog(actor.getName());
            nameDialog.setTitle("Actor Name");
            nameDialog.setHeaderText(null);
            nameDialog.showAndWait().ifPresent(newName -> {
                if (newName.isBlank()) {
                    throw new IllegalArgumentException("Actor name cannot be blank.");
                }
                final ApplicationContext context = ApplicationContext.context();
                final Actor dbActor = context.currentProject.database.actors.get(actor.id);
                actor.setName(newName.trim());
                context.currentProject.saveActors();
                nameBtn.setText(dbActor.getName());
                this.updateSelectedItem(dbActor);
            });
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(e, owner);
        }
    }

    private void updateSelectedItem(Actor dbActor) {
        final int selectedIndex = this.actorListView.getSelectionModel().getSelectedIndex();
        this.actorListView.getItems().set(selectedIndex, dbActor);
        this.actorListView.getSelectionModel().select(selectedIndex);
    }

    private Optional<Actor> getSelectedItem() {
        return Optional.ofNullable(this.actorListView.getSelectionModel().getSelectedItem());
    }

    private SimpleEntityListView<Actor> buildListView() {
        final SimpleEntityListView<Actor> listView = new SimpleEntityListView<>(Actor::getName);
        listView.getItems().setAll(ApplicationContext.context().currentProject.database.actors.getAll());
        listView.getSelectionModel().select(0);
        listView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    Optional.ofNullable(newValue).ifPresent(this::updateFormValues);
                });
        VBox.setVgrow(listView, Priority.ALWAYS);
        return listView;
    }

    private void updateFormValues(Actor selectedActor) {
        this.nameBtn.setText(selectedActor.getName());
        final RowColumnPair characterIndex = selectedActor.getCharacterIndex();
        final AssetLibrary assetLibrary = ApplicationContext.context().currentProject.assetLibrary;
        final int row = characterIndex.row;
        final int column = characterIndex.column;
        this.characterButton.updateIcon(assetLibrary, selectedActor.getSpritesheetPageId(),
                row, column, 0, 1);
    }

}
