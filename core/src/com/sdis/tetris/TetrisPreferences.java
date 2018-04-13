package com.sdis.tetris;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import com.sdis.tetris.audio.Song;

public class TetrisPreferences
{
	private final static Preferences preferences = Gdx.app.getPreferences("Tetris.gdx");

	public static void save()
	{
		preferences.flush();
	}

	public static Song getTheme()
	{
		return Song.fromValue(preferences.getInteger("game.bgmusic", 0));
	}

	public static void setTheme(final Song paramSong)
	{
		preferences.putInteger("game.bgmusic", Song.toValue(paramSong));
	}

	public static float getSfxVolume()
	{
		return preferences.getFloat("audio.sfx.volume", 0.7f);
	}

	public static float getMusicVolume()
	{
		return preferences.getFloat("audio.music.volume", 0.6f);
	}

	public static void setSfxVolume(float sfxVolume)
	{
		preferences.putFloat("audio.sfx.volume", sfxVolume);
	}

	public static void setMusicVolume(float musicVolume)
	{
		preferences.putFloat("audio.music.volume", musicVolume);
	}
}