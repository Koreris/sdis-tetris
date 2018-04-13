package com.sdis.tetris.audio;

public enum SFX
{
	HOVER("audio/hover.mp3"),
	SELECT("audio/select.mp3"),
	SFX_TETRIS("audio/tetris.mp3"),
	SFX_MOVE("audio/move.mp3"),
	SFX_CLEARLINE("audio/clearline.mp3"),
	SFX_COLL("audio/collision.mp3"),
	SFX_ROTATE("audio/rotate.mp3");
	

	private SFX(final String paramUri)
	{
		songUri = paramUri;
	}

	private String songUri;

	public String getUri()
	{
		return songUri;
	}
}