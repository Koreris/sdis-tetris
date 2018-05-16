package com.sdis.tetris.desktop;

import java.awt.Dimension;
import java.awt.Toolkit;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import com.sdis.tetris.Tetris;

public class DesktopLauncher {
	public static void main (String[] arg) 
	{
		System.setProperty("javax.net.ssl.keyStore", "client.keys");
	    System.setProperty("javax.net.ssl.keyStorePassword", "123456");
	    System.setProperty("javax.net.ssl.trustStore", "truststore");
	    System.setProperty("javax.net.ssl.trustStorePassword", "123456");
	   
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.addIcon("com/sdis/tetris/res/icon-128x128.png", FileType.Internal);
        config.addIcon("com/sdis/tetris/res/icon-32x32.png", FileType.Internal);
        config.addIcon("com/sdis/tetris/res/icon-16x16.png", FileType.Internal);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        config.title = "Tetris";
		config.width = (int) width;
		config.height = (int) height;
        config.fullscreen = false;
        config.resizable = false;
        config.vSyncEnabled = true;
        new LwjglApplication(new Tetris(), config);
    }
}
