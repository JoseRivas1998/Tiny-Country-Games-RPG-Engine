package com.tcg.rpgengine.editor.dialogs;

import com.badlogic.gdx.files.FileHandle;
import com.tcg.rpgengine.common.data.assets.TiledImageAsset;
import com.tcg.rpgengine.editor.components.canvasses.SpritesheetPreviewCanvas;
import com.tcg.rpgengine.editor.components.canvasses.TiledImagePreviewCanvas;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class TiledImagePreviewDialog extends Stage {

    public TiledImagePreviewDialog(TiledImageAsset tiledImageAsset) {
        super();

        final FileHandle projectFileHandle = ApplicationContext.context().currentProject.getProjectFileHandle();
        final FileHandle imageFileHandle = projectFileHandle.sibling(tiledImageAsset.getPath());

        final TiledImagePreviewCanvas tiledImagePreviewCanvas = new TiledImagePreviewCanvas(imageFileHandle,
                tiledImageAsset.rows, tiledImageAsset.columns
        );
        final StackPane stackPane = new StackPane();
        stackPane.getChildren().setAll(tiledImagePreviewCanvas);
        tiledImagePreviewCanvas.widthProperty().bind(stackPane.widthProperty());
        tiledImagePreviewCanvas.heightProperty().bind(stackPane.heightProperty());

        final double width = ApplicationContext.Constants.EDITOR_WIDTH / 2;
        final double height = ApplicationContext.Constants.EDITOR_HEIGHT / 2;
        this.setScene(new Scene(stackPane, width, height));
        this.setTitle(tiledImageAsset.getPath());
    }

}
