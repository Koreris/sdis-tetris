package com.sdis.tetris.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AudioHandler
{
	private static AudioHandler instance;

	public static AudioHandler getInstance()
	{
		if (instance == null)
		{
			instance = new AudioHandler();
		}

		return instance;
	}

	private float musicVolume = 0.7f;
	private float sfxVolume = 0.7f;

	private final LRUCache.CacheEntryRemovedListener<Song, Music> songCacheListener = new LRUCache.CacheEntryRemovedListener<Song, Music>()
	{
		@Override
		public void notifyEntryRemoved(final Song key, final Music value)
		{
			value.dispose();
		}
	};

	private final LRUCache.CacheEntryRemovedListener<SFX, Sound> soundCacheListener = new LRUCache.CacheEntryRemovedListener<SFX, Sound>()
	{
		@Override
		public void notifyEntryRemoved(final SFX key, final Sound value)
		{
			value.dispose();
		}
	};

	private final LRUCache<Song, Music> songCache = new LRUCache<>(4, songCacheListener);
	private final LRUCache<SFX, Sound> soundCache = new LRUCache<>(16, soundCacheListener);

	private Music currentSong = null;
	private Song currentSongName = Song.THEME_NULL;

	public float getSFXVolume()
	{
		return sfxVolume;
	}

	public float getMusicVolume()
	{
		return musicVolume;
	}

	public void setSFXVolume(final float paramSfxVolume)
	{
		sfxVolume = paramSfxVolume;
	}

	public void setMusicVolume(final float paramMusicVolume)
	{
		musicVolume = paramMusicVolume;

		if (currentSong != null && currentSong.isPlaying())
		{
			currentSong.setVolume(musicVolume);
		}
	}

	public void playSong(final Song songName, final boolean looping)
	{
		if (currentSongName == songName && currentSong != null)
		{
			if (currentSong.isPlaying())
			{
				currentSong.setVolume(musicVolume);
			}
			else
			{
				currentSong.play();
			}
		}
		else
		{
			if (currentSong != null && currentSong.isPlaying())
			{
				currentSong.stop();
			}

			final Music currentMusic = songCache.get(songName);

			if (currentMusic == null)
			{
				if (songName.getUri() == null)
				{
					currentSong = null;
				}
				else
				{
					songCache.add(songName, Gdx.audio.newMusic(Gdx.files.internal(songName.getUri())));
					currentSong = songCache.get(songName);
				}
			}
			else
			{
				currentSong = songCache.get(songName);
			}

			if (currentSong != null)
			{
				currentSongName = songName;
				currentSong.setLooping(looping);
				currentSong.setVolume(musicVolume);
				currentSong.play();
			}
		}
	}

	public final void playSFX(SFX soundName)
	{
		final Sound currentSound = soundCache.get(soundName);

		if (currentSound == null)
		{
			soundCache.add(soundName, Gdx.audio.newSound(Gdx.files.internal(soundName.getUri())));
		}

		soundCache.get(soundName).play(sfxVolume);
	}
}