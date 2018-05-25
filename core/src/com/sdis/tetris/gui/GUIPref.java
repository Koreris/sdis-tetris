package com.sdis.tetris.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import com.sdis.tetris.Buttons;
import com.sdis.tetris.Tetris;
import com.sdis.tetris.TetrisPreferences;
import com.sdis.tetris.audio.SFX;
import com.sdis.tetris.audio.Song;

public class GUIPref extends GUIScreen
{
	private float oldMusicVolume;
	private float oldSFXVolume;
	private Song currentSong = null;
	private final Stage stage = new Stage();
	private final Table table = new Table();
	private final ButtonGroup<TextButton> btnGroup = new ButtonGroup<TextButton>();
	private final Label lblBGM = new Label("BGM Options", Buttons.SmallLabel);
	private final Label lblMusicVolume = new Label("Music Volume", Buttons.SmallLabel);
	private final Label lblSFXVolume = new Label("SFX Volume", Buttons.SmallLabel);
	private final Label lblTitle = new Label("PREFERENCES", Buttons.TitleLabel);
	private final Slider sliderMusicVolume = new Slider(0.0f, 1.0f, 0.1f, false, Buttons.DefaultSlider);
	private final Slider sliderSFXVolume = new Slider(0.0f, 1.0f, 0.1f, false, Buttons.DefaultSlider);
	private final ImageButton previewSong = new ImageButton(Buttons.VolumeButton);
	private final TextButton saveButton = new TextButton("SAVE", Buttons.MenuButton);
	private final TextButton exitButton = new TextButton("EXIT", Buttons.MenuButton);
	private final TextButton AthemeButton = new TextButton("THEME A", Buttons.ToggleButton);
	private final TextButton BthemeButton = new TextButton("THEME B", Buttons.ToggleButton);
	private final TextButton CthemeButton = new TextButton("THEME C", Buttons.ToggleButton);
	private final TextButton DthemeButton = new TextButton("THEME D", Buttons.ToggleButton);
	Sprite background = new Sprite(new Texture(Gdx.files.internal("img/preferences_menu.png"), false));
	
	
	public GUIPref(final Tetris parent)
	{
		super(parent, Song.THEME_MAIN_MENU);
		
		background.setPosition(0,0);
		background.setSize((float)Gdx.graphics.getWidth(),(float)Gdx.graphics.getHeight());
		btnGroup.add(AthemeButton);
		btnGroup.add(BthemeButton);
		btnGroup.add(CthemeButton);
		btnGroup.add(DthemeButton);
		btnGroup.setMaxCheckCount(1);
		btnGroup.setMinCheckCount(0);
		btnGroup.setUncheckLast(true);
		table.setFillParent(true);
		table.padLeft(32).padRight(48);
		table.add(lblTitle).padBottom(48).colspan(2);
		table.row();
		table.defaults().right().padBottom(16);
		table.add(lblMusicVolume).left();
		table.add(sliderMusicVolume).width(180);
		table.row();
		table.add(lblSFXVolume).left();
		table.add(sliderSFXVolume).width(180);
		table.row();
		table.add(lblBGM).left();
		table.add(previewSong).left();
		table.row().center();
		table.add(AthemeButton).width(180);
		table.row().center();
		table.add(BthemeButton).width(180);
		table.row().center();
		table.right();
		table.add(CthemeButton).width(180);
		table.row().center();
		table.right();
		table.add(DthemeButton).width(180);
		table.row().center();
		table.right();
		table.add(saveButton).padTop(48).width(160).center();
		table.add(exitButton).padTop(48).width(160).center();
		table.row();
		stage.addActor(table);

		sliderSFXVolume.addListener(new ChangeListener()
		{
			@Override
			public void changed(final ChangeEvent event, final Actor actor)
			{
				audio.setSFXVolume(sliderSFXVolume.getValue());
			}
		});

		sliderMusicVolume.addListener(new ChangeListener()
		{
			@Override
			public void changed(final ChangeEvent event, final Actor actor)
			{
				audio.setMusicVolume(sliderMusicVolume.getValue());
			}
		});

		exitButton.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				audio.playSFX(SFX.HOVER);
				audio.setMusicVolume(oldMusicVolume);
				audio.setSFXVolume(oldSFXVolume);

				parent.switchTo(new GUIMainMenu(parent));
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

		saveButton.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				audio.playSFX(SFX.HOVER);
				TetrisPreferences.setTheme(currentSong);
				TetrisPreferences.setMusicVolume(sliderMusicVolume.getValue());
				TetrisPreferences.setSfxVolume(sliderSFXVolume.getValue());
				parent.switchTo(new GUIMainMenu(parent));
			}

			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor)
			{
				if (!saveButton.isPressed())
				{
					audio.playSFX(SFX.SELECT);
				}
			}
		});

		AthemeButton.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				audio.playSFX(SFX.HOVER);
				currentSong = Song.THEME_A;
			}

			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor)
			{
				if (!AthemeButton.isPressed() && !AthemeButton.isChecked())
				{
					audio.playSFX(SFX.SELECT);
				}
			}
		});

		BthemeButton.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				audio.playSFX(SFX.HOVER);
				currentSong = Song.THEME_B;
			}

			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor)
			{
				if (!BthemeButton.isPressed() && !BthemeButton.isChecked())
				{
					audio.playSFX(SFX.SELECT);
				}
			}
		});
		
		CthemeButton.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				audio.playSFX(SFX.HOVER);
				currentSong = Song.THEME_C;
			}

			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor)
			{
				if (!CthemeButton.isPressed() && !CthemeButton.isChecked())
				{
					audio.playSFX(SFX.SELECT);
				}
			}
		});
		
		DthemeButton.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				audio.playSFX(SFX.HOVER);
				currentSong = Song.THEME_D;
			}

			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor)
			{
				if (!DthemeButton.isPressed() && !DthemeButton.isChecked())
				{
					audio.playSFX(SFX.SELECT);
				}
			}
		});

		
//
		previewSong.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				audio.playSFX(SFX.HOVER);
				audio.playSong(currentSong, false);
			}

			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor)
			{
				if (!previewSong.isPressed())
				{
					audio.playSFX(SFX.SELECT);
				}
			}
		});
	}

	@Override
	public void render(float delta)
	{
		Gdx.gl.glClearColor(0.100f, 0.100f, 0.100f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
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
		sliderSFXVolume.setValue(TetrisPreferences.getSfxVolume());
		sliderMusicVolume.setValue(TetrisPreferences.getMusicVolume());
		currentSong = TetrisPreferences.getTheme();
		AthemeButton.setChecked(currentSong == Song.THEME_A);
		BthemeButton.setChecked(currentSong == Song.THEME_B);
		CthemeButton.setChecked(currentSong == Song.THEME_C);
		DthemeButton.setChecked(currentSong == Song.THEME_D);
		oldMusicVolume = audio.getMusicVolume();
		oldSFXVolume = audio.getSFXVolume();
	}

	@Override
	public void dispose()
	{
		stage.dispose();
	}
}