package com.sdis.tetris.gui;

import com.sdis.tetris.Tetris;
import com.sdis.tetris.audio.AudioHandler;
import com.sdis.tetris.audio.Song;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class GUIScreen implements Screen, InputProcessor
{
	protected final Tetris parent;
	protected final SpriteBatch batch;
	protected final AudioHandler audio;

	public GUIScreen(final Tetris paramParent)
	{
		this(paramParent, Song.THEME_NULL);
	}

	public GUIScreen(final Tetris paramParent, Song paramMusic)
	{
		bgmusic = paramMusic;
		audio = AudioHandler.getInstance();
		parent = paramParent;
		batch = new SpriteBatch();
	}

	protected Song bgmusic;

	public final Song getSong()
	{
		return bgmusic;
	}

	@Override
	public boolean keyDown(final int keycode)
	{
		return false;
	}

	@Override
	public boolean keyUp(final int keycode)
	{
		return false;
	}

	@Override
	public boolean keyTyped(final char character)
	{
		return false;
	}

	@Override
	public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button)
	{
		return false;
	}

	@Override
	public boolean touchUp(final int screenX, final int screenY, final int pointer, final int button)
	{
		return false;
	}

	@Override
	public boolean touchDragged(final int screenX, final int screenY, final int pointer)
	{
		return false;
	}

	@Override
	public boolean mouseMoved(final int screenX, final int screenY)
	{
		return false;
	}

	@Override
	public boolean scrolled(final int amount)
	{
		return false;
	}

	@Override
	public void show()
	{
	}

	@Override
	public void render(final float delta)
	{
	}

	@Override
	public void resize(final int width, final int height)
	{
	}

	@Override
	public void pause()
	{
	}

	@Override
	public void resume()
	{
	}

	@Override
	public void hide()
	{
	}

	@Override
	public void dispose()
	{
		batch.dispose();
	}
}