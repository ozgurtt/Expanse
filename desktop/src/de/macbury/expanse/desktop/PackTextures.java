package de.macbury.expanse.desktop;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

/**
 * Created on 29.01.16.
 */
public class PackTextures {
  public static void main (String[] arg) {
    TexturePacker.Settings settings = new TexturePacker.Settings();
    settings.grid = true;
    settings.square = true;
    settings.paddingX = 2;
    settings.paddingY = 2;
    TexturePacker.process(settings, "./raw/gui", "./android/assets/ui", "ui.atlas");
  }
}
