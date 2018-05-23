package com.sdis.tetris;

import com.sdis.tetris.audio.AudioHandler;
import com.sdis.tetris.audio.LRUCache;
import com.sdis.tetris.gui.GUIGame;
import com.sdis.tetris.gui.GUIMainMenu;
import com.sdis.tetris.gui.GUIMultiGame;
import com.sdis.tetris.gui.GUIScreen;
import com.sdis.tetris.logic.HighScores;
import com.sdis.tetris.network.TetrisClient;

import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class Tetris extends Game
{
	private GUIScreen currentScreen;

	public int opponentNr=0;
	public TetrisClient networkClient = new TetrisClient();
	public String playerName = "Player";
	public String serverName;
	public InetAddress serverAddress;
	public int serverPort;
	public ConcurrentHashMap<String,String> other_servers = new ConcurrentHashMap<>();
	private AudioHandler audioHandler = AudioHandler.getInstance();
	HighScores scores= new HighScores();
	

	private final LRUCache<String, GUIScreen> menuArray = new LRUCache<>(4, new LRUCache.CacheEntryRemovedListener<String, GUIScreen>()
	{
		@Override
		public void notifyEntryRemoved(final String key, final GUIScreen value)
		{
			value.dispose();
		}
	});

	public Tetris()
	{
		gameMode = 1;
	}

	private int gameMode;

	public int getMode()
	{
		return gameMode;
	}

	public void setMode(int paramMode)
	{
		gameMode = paramMode;
	}
	
	public void addToHighScores(int score, String name)
	{
		scores.addScore(score,name);
	}
	
	public HighScores getHighScores()
	{
		return scores;
	}
	
	public void startGame()
	{
		switchTo(new GUIGame(this));
	}

	@Override
	public void create()
	{
		audioHandler.setMusicVolume(TetrisPreferences.getMusicVolume());
		audioHandler.setSFXVolume(TetrisPreferences.getSfxVolume());
		switchTo(new GUIMainMenu(this));
	}

	public void switchTo(GUIScreen screen)
	{
		if (currentScreen != null)
		{
			currentScreen.hide();
		}
		
		//currentScreen = menuArray.get(screenName);
		currentScreen = screen;
		Gdx.input.setInputProcessor(currentScreen);
		setScreen(currentScreen);
		audioHandler.playSong(currentScreen.getSong(), true);
		TetrisPreferences.save();
	}

	@Override
	public void render()
	{
		super.render();
	}
}