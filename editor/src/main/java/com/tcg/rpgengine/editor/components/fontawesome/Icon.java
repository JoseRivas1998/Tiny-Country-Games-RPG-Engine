package com.tcg.rpgengine.editor.components.fontawesome;

import com.badlogic.gdx.files.FileHandle;
import com.tcg.rpgengine.editor.context.ApplicationContext;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

public class Icon extends Label {

    private static Font font;

    public Icon(Icons icon) {
        super(icon.icon);
        this.setFont(Icon.font());
    }

    private static Font font() {
        if (font == null) {
            synchronized (Icon.class) {
                if (font == null) {
                    final ApplicationContext context = ApplicationContext.context();
                    final FileHandle fontFile = context.files.internal("editor_font/fontawesome.ttf");
                    font = Font.loadFont(fontFile.read(), Font.getDefault().getSize());
                }
            }
        }
        return font;
    }

}
