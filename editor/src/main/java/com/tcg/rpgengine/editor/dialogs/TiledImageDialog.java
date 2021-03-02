package com.tcg.rpgengine.editor.dialogs;

import com.badlogic.gdx.files.FileHandle;
import com.tcg.rpgengine.editor.components.IntegerField;
import com.tcg.rpgengine.editor.components.canvasses.TiledImagePreviewCanvas;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.util.Pair;

public class TiledImageDialog extends Dialog<Pair<Integer, Integer>> {

    private final TiledImagePreviewCanvas tiledImagePreviewCanvas;
    private final IntegerField rowsField;
    private final IntegerField columnsField;

    public TiledImageDialog(FileHandle imageFile) {
        super();
        this.setHeaderText(null);
        this.getDialogPane().getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        this.tiledImagePreviewCanvas = new TiledImagePreviewCanvas(imageFile, 1, 1);
        this.rowsField = this.buildRowsField();
        this.columnsField = this.buildColumnsField();

        final HBox layout = new HBox(ApplicationContext.Constants.SPACING);
        layout.getChildren().setAll(this.buildTiledImagePreviewCanvasHolder(), this.buildIntFieldsLayout());

        this.getDialogPane().setMinHeight(ApplicationContext.Constants.EDITOR_HEIGHT / 2.0);
        this.getDialogPane().setMinWidth(ApplicationContext.Constants.EDITOR_WIDTH / 2.0);
        this.getDialogPane().setContent(layout);

        this.setResultConverter(param -> {
            if (param != ButtonType.OK) return null;
            return new Pair<>(this.rowsField.getIntValue(), this.columnsField.getIntValue());
        });

    }

    private StackPane buildTiledImagePreviewCanvasHolder() {
        final StackPane stackPane = new StackPane();
        stackPane.getChildren().setAll(this.tiledImagePreviewCanvas);
        this.tiledImagePreviewCanvas.widthProperty().bind(stackPane.widthProperty());
        this.tiledImagePreviewCanvas.heightProperty().bind(stackPane.heightProperty());
        HBox.setHgrow(stackPane, Priority.ALWAYS);
        return stackPane;
    }

    private GridPane buildIntFieldsLayout() {
        final GridPane intFields = new GridPane();
        intFields.setVgap(ApplicationContext.Constants.SPACING);
        intFields.setHgap(ApplicationContext.Constants.SPACING);
        intFields.add(new Label("Rows:"), 0, 0);
        intFields.add(this.rowsField, 1, 0);
        intFields.add(new Label("Columns:"), 0, 1);
        intFields.add(this.columnsField, 1, 1);
        return intFields;
    }

    protected IntegerField buildColumnsField() {
        final IntegerField columnsField = new IntegerField(1);
        columnsField.integerProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() >= 0) {
                this.tiledImagePreviewCanvas.setColumns(newValue.intValue());
            } else {
                columnsField.setIntValue(oldValue.intValue());
            }
        });
        return columnsField;
    }

    private IntegerField buildRowsField() {
        final IntegerField rowsField = new IntegerField(1);
        rowsField.integerProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() >= 0) {
                this.tiledImagePreviewCanvas.setRows(newValue.intValue());
            } else {
                rowsField.setIntValue(oldValue.intValue());
            }
        });
        return rowsField;
    }

}
