package com.sdis.tetris.gui;

import com.badlogic.gdx.Gdx; 
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.sdis.tetris.Buttons;
import com.sdis.tetris.Tetris;
import com.sdis.tetris.audio.SFX;
import com.sdis.tetris.audio.Song;
import com.sdis.tetris.logic.Board;

public class GUIMainMenu extends GUIScreen
{
	private final Stage stage = new Stage();
	private final Table table = new Table();
	private Board myBoard = new Board();
	Sprite background = new Sprite(new Texture(Gdx.files.internal("img/main_menu.png"), false));
	Sprite title = new Sprite(new Texture(Gdx.files.internal("img/main_title.png"), false));
	private final TextButton onePButton = new TextButton("1 PLAYER", Buttons.MenuButton);
	private final TextButton prefButton = new TextButton("PREFERENCES", Buttons.MenuButton);
	private final TextButton multiButton = new TextButton("MultiPlayer", Buttons.MenuButton);
	private final TextButton credButton = new TextButton("CREDITS", Buttons.MenuButton);
	private final TextButton exitButton = new TextButton("EXIT", Buttons.MenuButton);
	private final float aspectRatio = (float)Gdx.graphics.getHeight()/(float)Gdx.graphics.getWidth();
	private final Camera camera=new OrthographicCamera(25 * aspectRatio ,25);
	private final Viewport viewport=new FitViewport(800,480,camera);
	

	private class RunGame implements Runnable
	{
		@Override
		public void run()
		{
			parent.startGame();
		}
	};

	private class RunPreferences implements Runnable
	{
		@Override
		public void run()
		{
			parent.switchTo(new GUIPref(parent));
		}
	}

	private class RunCredits implements Runnable
	{
		@Override
		public void run()
		{
			parent.switchTo(new GUICredits(parent));
		}
	}

	private class Multiplayer implements Runnable
	{
		@Override
		public void run()
		{
			parent.switchTo(new GUIServer(parent));
		}
	}

	public GUIMainMenu(final Tetris parent)
	{
		super(parent, Song.THEME_A);
		background.setPosition(0,0);
		background.setSize((float)Gdx.graphics.getWidth(),(float)Gdx.graphics.getHeight());
		title.setPosition((float)Gdx.graphics.getWidth()/3.7f,(float)Gdx.graphics.getHeight()-title.getHeight()*2);
		
		table.setPosition(0,75);
		table.add(onePButton).size((float)Gdx.graphics.getWidth()/2, (float)Gdx.graphics.getHeight()/8).padBottom(10).row();
		table.add(multiButton).size((float)Gdx.graphics.getWidth()/2, (float)Gdx.graphics.getHeight()/8).padBottom(10).row();
		table.add(prefButton).size((float)Gdx.graphics.getWidth()/2, (float)Gdx.graphics.getHeight()/8).padBottom(10).row();
		table.add(credButton).size((float)Gdx.graphics.getWidth()/2, (float)Gdx.graphics.getHeight()/8).padBottom(10).row();
		table.add(exitButton).size((float)Gdx.graphics.getWidth()/2, (float)Gdx.graphics.getHeight()/8).padBottom(10).row();
		title.scale(0.7f);
		table.bottom();
		table.setFillParent(true);
		table.setVisible(true);
		stage.addActor(table);
		//new Timer().scheduleTask(new AnimatePiece(), 1, 1);
		onePButton.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				audio.playSFX(SFX.HOVER);
				stage.addAction(Actions.sequence(Actions.moveTo(-480.0f, 0.0f, 0.5f), Actions.run(new RunGame())));
			}

			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor)
			{
				if (!onePButton.isPressed())
				{
					audio.playSFX(SFX.HOVER);
				}
			}
		});

		multiButton.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				audio.playSFX(SFX.HOVER);
				stage.addAction(Actions.sequence(Actions.moveTo(-480.0f, 0.0f, 0.5f), Actions.run(new Multiplayer())));
			}

			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor)
			{
				if (!onePButton.isPressed())
				{
					audio.playSFX(SFX.HOVER);
				}
			}
		});

		prefButton.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				audio.playSFX(SFX.HOVER);
				stage.addAction(Actions.sequence(Actions.moveTo(-480.0f, 0.0f, 0.5f), Actions.run(new RunPreferences())));
			}

			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor)
			{
				if (!prefButton.isPressed())
				{
					audio.playSFX(SFX.SELECT);
				}
			}
		});

		credButton.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				audio.playSFX(SFX.HOVER);
				stage.addAction(Actions.sequence(Actions.moveTo(-480.0f, 0.0f, 0.5f), Actions.run(new RunCredits())));
			}

			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor)
			{
				if (!prefButton.isPressed())
				{
					audio.playSFX(SFX.SELECT);
				}
			}
		});

		exitButton.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				audio.playSFX(SFX.HOVER);
				Gdx.app.exit();
			}

			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor)
			{
				if (!exitButton.isPressed())
				{
					audio.playSFX(SFX.SELECT);
				}
			}
		});
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
		title.draw(batch);
		
		batch.end();
		stage.draw();
	}

	@Override
	public void resize(int width, int height)
	{
		stage.getViewport().update(width, height);
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