package com.sdis.tetris.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import com.sdis.tetris.Buttons;
import com.sdis.tetris.Tetris;
import com.sdis.tetris.audio.SFX;
import com.sdis.tetris.audio.Song;

public class GUICredits extends GUIScreen
{
	//private Camera camera;
	//private Viewport viewport;
	//private final float height=20;
	//private final float ppu=(float)Gdx.graphics.getHeight()/height;
	//private final float width = (float)Gdx.graphics.getWidth()/ppu;
	private final Stage stage = new Stage();
	private final Table table = new Table();
	private final Sprite background = new Sprite(new Texture(Gdx.files.internal("img/credits.png")));
	private final TextButton backButton = new TextButton("< BACK", Buttons.MenuButton);

	public GUICredits(final Tetris parent)
	{
		super(parent, Song.THEME_CREDITS);

		background.setPosition(0,0);
		background.setSize((float)Gdx.graphics.getWidth(),(float)Gdx.graphics.getHeight());
		table.add(new Label("CREDITS", Buttons.TitleLabel)).padBottom(32).row();
		
		table.add(new Label("Music", Buttons.TitleLabel)).padBottom(20).row();
		
		table.add(new Label("--SEGA Tetris Soundtrack (1988)--", Buttons.SmallLabel)).padBottom(8).row();
		table.add(new Label("Yasuhiro Kawakami", Buttons.SmallLabel)).padBottom(20).row();
		table.add(new Label("--TYPE A (1990)--", Buttons.SmallLabel)).padBottom(8).row();
		table.add(new Label("Pop. Russian", Buttons.SmallLabel)).padBottom(8).row();
		table.add(new Label("Arranged by Hirokazu Tanaka", Buttons.SmallLabel)).padBottom(20).row();
		table.add(new Label("--Music 1--", Buttons.SmallLabel)).padBottom(8).row();
		table.add(new Label("Pyotr Tchaikovsky", Buttons.SmallLabel)).padBottom(8).row();
		table.add(new Label("Arranged by Hirokazu Tanaka", Buttons.SmallLabel)).padBottom(20).row();
		table.add(new Label("--Tong Poo (1978)--", Buttons.SmallLabel)).padBottom(8).row();
		table.add(new Label("Yellow  Magic  Orchestra", Buttons.SmallLabel)).padBottom(30).row();
		
		table.add(new Label("--Sound Effects--", Buttons.TitleLabel)).padBottom(25).row();
		table.add(new Label("NES Tetris", Buttons.SmallLabel)).padBottom(8).row();
		table.add(new Label("Hirokazu Tanaka", Buttons.SmallLabel)).padBottom(8).row();
		table.add(new Label("soundeffectsplus", Buttons.SmallLabel)).padBottom(30).row();
		
		table.add(new Label("Inspired by", Buttons.TitleLabel)).padBottom(8).row();
		table.add(new Label("Sega Arcade Tetris", Buttons.TitleLabel)).padBottom(8).row();
		
		table.setFillParent(true);

		backButton.setPosition(48, 30);
		backButton.addListener(new ClickListener()
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
				if (!backButton.isPressed())
				{
					audio.playSFX(SFX.SELECT);
				}
			}
		});

		stage.addActor(table);
		stage.addActor(backButton);
	}

	@Override
	public void render(float delta)
	{
		if (table.getY() >= 1200.0f)
		{
			show();
		}

		stage.act();
		//camera=new OrthographicCamera(width,height);
		//camera.position.set(width / 2, height / 2, 0);
		//camera.update();
		//viewport = new ExtendViewport(2, 200, camera);

		//batch.setProjectionMatrix(camera.combined);
		batch.begin();
		background.draw(batch);
		batch.end();
		stage.draw();
	}

	@Override
	public void resize(int width, int height)
	{
		stage.getViewport().update(width, height);
	}

	@Override
	public void show()
	{
		Gdx.input.setInputProcessor(stage);
		table.getActions().clear();
		table.setPosition(0.0f, -700.0f);
		table.addAction(Actions.moveBy(0.0f, 2500.0f, 40));
	}

	@Override
	public void dispose()
	{
		stage.dispose();
	}
}