package com.sdis.tetris.audio;

public enum Song
{
	THEME_MAIN_MENU("audio/MainMenu.mp3"),
	THEME_GAME_OVER("audio/GameOver.ogg"),
	THEME_A("audio/TetremixA.ogg"),
	THEME_B("audio/ThemeB.mp3"),
	THEME_C("audio/ThemeC.mp3"),
	THEME_D("audio/ThemeD.mp3"),
	THEME_CREDITS("audio/CommunisticInvasion.ogg"),
	THEME_NULL(null);

	private Song(final String paramUri)
	{
		songUri = paramUri;
	}

	private String songUri;

	public String getUri()
	{
		return songUri;
	}
	
	public static final Song fromValue(int songId)
	{
		switch (songId)
		{
		case 1: return THEME_B;
		case 2: return THEME_C;
		case 3: return THEME_D;
		default: return THEME_A;
		}		
	}
	
	public static int toValue(final Song paramSong)
	{
		switch (paramSong)
		{
		case THEME_B: return 1;
		case THEME_C: return 2;
		case THEME_D: return 3;
		default: return 0;
		}	
	}
}