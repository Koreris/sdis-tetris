package com.sdis.tetris.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import com.sdis.tetris.Tetris;

public class DesktopLauncher {
	public static void main (String[] arg) 
	{
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.addIcon("com/sdis/tetris/res/icon-128x128.png", FileType.Internal);
        config.addIcon("com/sdis/tetris/res/icon-32x32.png", FileType.Internal);
        config.addIcon("com/sdis/tetris/res/icon-16x16.png", FileType.Internal);
        config.title = "Tetris";
		config.width = 500;
		config.height = 710;
        config.fullscreen = false;
        config.resizable = false;
        config.vSyncEnabled = true;
        new LwjglApplication(new Tetris(), config);
    }
}
