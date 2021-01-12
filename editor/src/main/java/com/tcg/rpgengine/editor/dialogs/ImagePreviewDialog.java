package com.tcg.rpgengine.editor.dialogs;

import com.badlogic.gdx.files.FileHandle;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ImagePreviewDialog extends Stage {

    public ImagePreviewDialog(FileHandle imageFile) {
        super();
        final StackPane stackPane = new StackPane();
        stackPane.setPadding(new Insets(ApplicationContext.Constants.PADDING));

        final double width = ApplicationContext.Constants.EDITOR_WIDTH / 2;
        final double height = ApplicationContext.Constants.EDITOR_HEIGHT / 2;
        final Scene scene = new Scene(stackPane, width, height);

        final Image image = new Image(imageFile.read());
        final ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        final double doublePadding = ApplicationContext.Constants.PADDING * 2;
        imageView.fitWidthProperty().bind(scene.widthProperty().subtract(doublePadding));
        imageView.fitHeightProperty().bind(scene.heightProperty().subtract(doublePadding));
        stackPane.getChildren().addAll(imageView);

        this.setScene(scene);
    }

}
