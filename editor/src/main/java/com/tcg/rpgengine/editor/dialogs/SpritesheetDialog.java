package com.tcg.rpgengine.editor.dialogs;

import com.badlogic.gdx.files.FileHandle;
import com.tcg.rpgengine.common.data.assets.TiledImageAsset;
import com.tcg.rpgengine.editor.components.IntegerField;
import com.tcg.rpgengine.editor.components.canvasses.SpritesheetPreviewCanvas;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.util.Pair;

public class SpritesheetDialog extends Dialog<Pair<Integer, Integer>> {

    private final SpritesheetPreviewCanvas spritesheetPreviewCanvas;
    private final IntegerField rowField;
    private final IntegerField columnField;

    public SpritesheetDialog(FileHandle fileHandle, TiledImageAsset tiledImageAsset) {
        this(fileHandle);
        this.rowField.setIntValue(tiledImageAsset.rows);
        this.columnField.setIntValue(tiledImageAsset.columns);
    }

    public SpritesheetDialog(FileHandle imageFile) {
        super();
        this.setTitle("Spritesheet");
        this.setHeaderText(null);
        this.getDialogPane().getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        this.spritesheetPreviewCanvas = new SpritesheetPreviewCanvas(imageFile, 1, 1);
        this.rowField = this.buildRowField();
        this.columnField = this.buildColumnField();

        final HBox layout = new HBox(ApplicationContext.Constants.SPACING);
        layout.getChildren().addAll(this.buildPreviewCanvasHolder(), this.buildIntFieldsLayout());

        this.getDialogPane().setMinHeight(ApplicationContext.Constants.EDITOR_HEIGHT / 2.0);
        this.getDialogPane().setMinWidth(ApplicationContext.Constants.EDITOR_WIDTH / 2.0);
        this.getDialogPane().setContent(layout);

        this.setResultConverter(param -> {
            if (param != ButtonType.OK) return null;
            return new Pair<>(this.rowField.getIntValue(), this.columnField.getIntValue());
        });

    }

    private GridPane buildIntFieldsLayout() {
        final GridPane intFields = new GridPane();
        intFields.setVgap(ApplicationContext.Constants.SPACING);
        intFields.setHgap(ApplicationContext.Constants.SPACING);
        intFields.add(new Label("Rows:"), 0, 0);
        intFields.add(this.rowField, 1, 0);
        intFields.add(new Label("Columns:"), 0, 1);
        intFields.add(this.columnField, 1, 1);
        return intFields;
    }

    private IntegerField buildColumnField() {
        final IntegerField columnField = new IntegerField(1);
        columnField.integerProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() >= 0) {
                this.spritesheetPreviewCanvas.setColumns(newValue.intValue());
            } else {
                columnField.setIntValue(oldValue.intValue());
            }
        });
        return columnField;
    }

    private IntegerField buildRowField() {
        final IntegerField rowField = new IntegerField(1);
        rowField.integerProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() >= 0) {
                this.spritesheetPreviewCanvas.setRows(newValue.intValue());
            } else {
                rowField.setIntValue(oldValue.intValue());
            }
        });
        return rowField;
    }

    private StackPane buildPreviewCanvasHolder() {
        final StackPane previewCanvasHolder = new StackPane();
        previewCanvasHolder.getChildren().addAll(this.spritesheetPreviewCanvas);
        this.spritesheetPreviewCanvas.widthProperty().bind(previewCanvasHolder.widthProperty());
        this.spritesheetPreviewCanvas.heightProperty().bind(previewCanvasHolder.heightProperty());
        HBox.setHgrow(previewCanvasHolder, Priority.ALWAYS);
        return previewCanvasHolder;
    }

}
