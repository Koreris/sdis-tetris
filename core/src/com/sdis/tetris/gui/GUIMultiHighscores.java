package com.sdis.tetris.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sdis.tetris.Buttons;
import com.sdis.tetris.Tetris;
import com.sdis.tetris.audio.SFX;
import com.sdis.tetris.audio.Song;
import com.sdis.tetris.network.TetrisClient;

import java.util.Map;

public class GUIMultiHighscores extends GUIScreen
{

	Sprite background = new Sprite(new Texture(Gdx.files.internal("img/hsbg.png"), false));
	private final Stage stage = new Stage();
	private final Table table = new Table();
	private final TextButton MainMenuButton = new TextButton("Lobby", Buttons.MenuButton);
	public GUIMultiHighscores(Tetris paramParent)
	{
		super(paramParent, Song.THEME_CREDITS);

		background.setPosition(0,0);
		background.setSize((float)Gdx.graphics.getWidth(),(float)Gdx.graphics.getHeight());
		table.setPosition(0,75);
		table.add(new Label("HIGHSCORES", Buttons.TitleLabel)).padBottom(32).row();
		TetrisClient cliente = new TetrisClient();
		for (Map.Entry me: cliente.multiscores.entrySet())
		{
			String highscore="i+1\n"+me.getKey() + " " + me.getValue();
			
			table.add(new Label(highscore, Buttons.TitleLabel)).padBottom(20).row();
		}
		table.add(MainMenuButton);
		table.row();
		table.bottom();
		table.setFillParent(true);
		table.setVisible(true);
		MainMenuButton.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				audio.playSFX(SFX.HOVER);
				parent.switchTo(new GUIMainMenu(parent));
			}

			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor)
			{
				audio.playSFX(SFX.SELECT);
			}
		});
		stage.addActor(table);
	}

	@Override
	public void render(float delta)
	{
		Gdx.gl.glClearColor( 1, 1, 1, 1 );
		Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );

		stage.act();
		//camera.update();
		//batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		background.draw(batch);
		batch.end();
		stage.draw();
	}

	@Override
	public void resize(int width, int height)
	{
		//stage.getViewport().update(width, height);
		//camera.position.set(camera.viewportWidth/2, camera.viewportHeight/2, 0);
	}

	@Override
	public void show()
	{
		Gdx.input.setInputProcessor(stage);
	
	}
	
	@Override
	public void dispose()
	{
		stage.dispose();
	}
}
