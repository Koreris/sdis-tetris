package com.sdis.tetris.gui;

import java.awt.Button; 

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import com.sdis.tetris.Buttons;
import com.sdis.tetris.Tetris;
import com.sdis.tetris.TetrisPreferences;
import com.sdis.tetris.audio.SFX;
import com.sdis.tetris.audio.Song;
import com.sdis.tetris.logic.Board;

public class GUIGame extends GUIScreen
{
	

	private GameState state;
	private final float screenWidth = Gdx.graphics.getWidth();
	private final float screenHeight = Gdx.graphics.getHeight();
	private final Stage stage = new Stage();
	
	private Board myBoard = new Board();
	Sprite lvl1 = new Sprite(new Texture(Gdx.files.internal("img/level1.png"), false));
	Sprite lvl2 = new Sprite(new Texture(Gdx.files.internal("img/level2.png"), false));
	Sprite lvl3 = new Sprite(new Texture(Gdx.files.internal("img/level3.png"), false));
	Sprite lvl4 = new Sprite(new Texture(Gdx.files.internal("img/level4.png"), false));
	Sprite lvl5 = new Sprite(new Texture(Gdx.files.internal("img/level5.png"), false));
	Sprite redBlock = new Sprite(new Texture(Gdx.files.internal("blocks/redBlock.png"), false));
	Sprite blueBlock = new Sprite(new Texture(Gdx.files.internal("blocks/blueBlock.png"), false));
	Sprite greenBlock = new Sprite(new Texture(Gdx.files.internal("blocks/greenBlock.png"), false));
	Sprite cyanBlock = new Sprite(new Texture(Gdx.files.internal("blocks/cyanBlock.png"), false));
	Sprite yellowBlock = new Sprite(new Texture(Gdx.files.internal("blocks/yellowBlock.png"), false));
	Sprite purpleBlock = new Sprite(new Texture(Gdx.files.internal("blocks/purpleBlock.png"), false));
	Sprite orangeBlock = new Sprite(new Texture(Gdx.files.internal("blocks/orangeBlock.png"), false));
	Sprite boardFrame = new Sprite(new Texture (Gdx.files.internal("img/frame.png"), false));
	Task t1;
	float minBoardWidth=(screenWidth/5.25f);
	float maxBoardWidth=myBoard.boardWidth*myBoard.scaleX;
	float minBoardHeight=0;
	float maxBoardHeight=(myBoard.boardHeight*myBoard.scaleY);
	int count=0;

	public void changeState(GameState newState)
	{
		state = newState;
	}
	
	
	public GUIGame(Tetris paramParent) 
	{
		super(paramParent, TetrisPreferences.getTheme());
		reStartGame();
		t1.cancel();
		changeState(new GameRunningState());
	}	
	
	private void reStartGame(){
		myBoard=new Board();
		myBoard.setGameOver(false);
		myBoard.setCurrentLevel(0);
		myBoard.setPlayerScore(0);
		t1=new Timer().scheduleTask(new AnimatePiece(), 1, 1);
	}
	
	private class AnimatePiece extends Task
	{
		@Override
		public void run()
		{
			myBoard.moveDown();
		}
	}
	
	@Override
	public void render(float delta)
	{
		state.draw();
	}
	
	@Override
	public boolean keyDown(final int keycode)
	{
		if (keycode == Keys.LEFT)
		{
			myBoard.moveLeft();
		}
		else if (keycode == Keys.RIGHT)
		{
			myBoard.moveRight();
		}
		else if (keycode == Keys.SPACE)
		{
			myBoard.rotatePiece();
		}
		else if (keycode == Keys.P)
		{
			t1.cancel();
			changeState(new GamePausedState());
		}
		
		return true;
	} 
	
	public boolean keyPressed()
	{
		if (Gdx.input.isKeyPressed(Keys.DOWN))
		{
			myBoard.moveDown();
			myBoard.incPlayerScore(3);
		}
		return true;
	} 

	public boolean isGamePaused()
	{
		return !(state instanceof GameRunningState);
	}
	
	public boolean isGameRunning()
	{
		return !(state instanceof GameRunningState);
	}
	

	@Override
	public void pause()
	{
		if (state instanceof GameRunningState)
		{
			changeState(new GamePausedState());
		}
	}
	

	
	private abstract class GameState
	{
		public abstract void update(float delta);
		
		public abstract void draw();
	}
	
	private class GameRunningState extends GameState
	{
		private final Stage stageGame = new Stage();
		private Table table = new Table();
		String levels="Level\n"+myBoard.getCurrentLevel();
		String scores="Score\n"+myBoard.getPlayerScore();
		Label level= new Label(levels, Buttons.SmallLabel);
		Label score= new Label(scores, Buttons.SmallLabel);
		int prevLevel=0;
		Sprite background = lvl1;
	
		
	
		public GameRunningState()
		{
			
			Gdx.input.setInputProcessor(GUIGame.this);
			
			if(isGameRunning())
			{
				t1.run();
			}
			
			table.setFillParent(true);
			level.setFontScale(0.8f,0.8f);
			score.setFontScale(0.8f,0.8f);
			table.add(level).padBottom(10);
			table.row();
			table.add(score);
			table.setPosition(minBoardWidth+115f,minBoardHeight+280f);
			
			stageGame.addActor(table);
			updateTimer();
			
		}
		void setBackground(){
			background.setPosition(0,0);
			background.setSize((float)Gdx.graphics.getWidth(),(float)Gdx.graphics.getHeight());
		}
		void updateTimer(){
			switch(prevLevel){
			case 0:
				background=lvl1;
				setBackground();
				t1.cancel();
				t1= new Timer().scheduleTask(new AnimatePiece(), 1f, 1f);
				break;
			case 1:
				background=lvl1;
				setBackground();
				t1.cancel();
				t1= new Timer().scheduleTask(new AnimatePiece(), 0.8f, 0.8f);
				break;
			case 2:
				background=lvl2;
				setBackground();
				t1.cancel();
				t1= new Timer().scheduleTask(new AnimatePiece(), 0.5f, 0.5f);
				break;
			case 3:
				background=lvl2;
				setBackground();
				t1.cancel();
				t1= new Timer().scheduleTask(new AnimatePiece(), 0.3f, 0.3f);
				break;
			case 4:
				background=lvl3;
				setBackground();
				t1.cancel();
				t1= new Timer().scheduleTask(new AnimatePiece(), 0.4f, 0.4f);
				break;
			case 5:
				background=lvl3;
				setBackground();
				t1.cancel();
				t1= new Timer().scheduleTask(new AnimatePiece(), 0.5f, 0.5f);
				break;
			case 6:
				background=lvl4;
				setBackground();
				t1.cancel();
				t1= new Timer().scheduleTask(new AnimatePiece(), 0.2f, 0.2f);
				break;
			case 7:
				background=lvl4;
				setBackground();
				t1.cancel();
				t1= new Timer().scheduleTask(new AnimatePiece(), 0.4f, 0.4f);
				break;
			case 8:
				background=lvl4;
				setBackground();
				t1.cancel();
				t1= new Timer().scheduleTask(new AnimatePiece(), 0.5f, 0.5f);
				break;
			case 9:
				background=lvl5;
				setBackground();
				t1.cancel();
				t1= new Timer().scheduleTask(new AnimatePiece(), 0.3f, 0.3f);
				break;
			default:
				background=lvl5;
				setBackground();
				t1.cancel();
				t1= new Timer().scheduleTask(new AnimatePiece(), 0.1f, 0.1f);
				break;
			}
		}
		
		public void update()
		{
			count++;
			if(count==4){
				keyPressed();
				count=0;
			}
			levels="Level\n"+myBoard.getCurrentLevel();
			scores="Score\n"+myBoard.getPlayerScore();
			
			level.setText(levels);
			score.setText(scores);
			if(prevLevel!=myBoard.getCurrentLevel()){
				prevLevel=myBoard.getCurrentLevel();
				updateTimer();	
			}
		}
		
		@Override
		public void draw()
		{
			
			update();
			Gdx.gl.glClearColor( 0.0f, 0.0f, 0.0f, 0.0f );
			Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );
			
			batch.begin();
			background.draw(batch);
			
			drawBoard();
			
			if(myBoard.isGameOver())
			{
				parent.addToHighScores(myBoard.getPlayerScore(),"Player1");
				t1.cancel();
				
				changeState(new GameOverState());
			}
			
		
			batch.end();
			stageGame.draw();
		
		}

		@Override
		public void update(float delta) 
		{	
			stageGame.act(delta);
		}
		
	}
	
	private class GamePausedState extends GameState
	{
		private final Stage stagePause = new Stage();
		private final Table tablePaused = new Table();
		private final Label labelRes = new Label("PAUSE", Buttons.SmallLabel);
		private final TextButton resumeButton = new TextButton("RESUME", Buttons.MenuButton);
		
		public GamePausedState()
		{
			tablePaused.setFillParent(true);
			tablePaused.defaults().padBottom(16);
			tablePaused.add(labelRes);
			tablePaused.row();
			tablePaused.add(resumeButton);
			stagePause.addActor(tablePaused);
			audio.playSFX(SFX.SELECT);

			resumeButton.addListener(new ClickListener()
			{
				@Override
				public void clicked(InputEvent event, float x, float y)
				{
					audio.playSFX(SFX.HOVER);
					changeState(new GameRunningState());
				}

				@Override
				public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor)
				{
					audio.playSFX(SFX.SELECT);
				}
			});
			Gdx.input.setInputProcessor(stagePause);
		}
		@Override
		public void draw()
		{
			stagePause.draw();
		}

		@Override
		public void update(float delta) 
		{	
			stagePause.act(delta);
		}
	}
	
	private class GameOverState extends GameState
	{
		private final Stage stageOver = new Stage();
		private final Table tableOver = new Table();
		private final Label labelOver = new Label("GAME OVER", Buttons.SmallLabel);
		private final TextButton hsButton = new TextButton("Highscores", Buttons.MenuButton);
		
		public GameOverState()
		{
			tableOver.setFillParent(true);
			tableOver.defaults().padBottom(32);
			tableOver.add(labelOver);
			tableOver.row();
			tableOver.add(hsButton);
			stageOver.addActor(tableOver);
			audio.playSong(Song.THEME_GAME_OVER, true);

			hsButton.addListener(new ClickListener()
			{
				@Override
				public void clicked(InputEvent event, float x, float y)
				{
					audio.playSFX(SFX.HOVER);
					reStartGame();
					parent.switchTo(new GUIHighscores(parent));
				}

				@Override
				public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor)
				{
					audio.playSFX(SFX.SELECT);
				}
			});
			Gdx.input.setInputProcessor(stageOver);
		}
		@Override
		public void draw()
		{
			stageOver.draw();
		}

		@Override
		public void update(float delta) 
		{	
			stageOver.act(delta);
		}
	}

		public void drawBoard()
		{
			//Draw Board
			
			boardFrame.setPosition(minBoardWidth-25f,minBoardHeight+45f);
			boardFrame.setSize(maxBoardWidth+50f, maxBoardHeight+50f);
			boardFrame.draw(batch);
			
			for (int y = 0; y < myBoard.boardHeight; y++) 
			{
				for (int x = 0; x <myBoard.boardWidth; x++) 
				{
					if (myBoard.gameBoard[y][x] != null) 
					{
						if(myBoard.gameBoard[y][x]==Color.GREEN){
							greenBlock.setPosition(myBoard.scaleX*x+screenWidth/5.3f, myBoard.scaleY *y+100f);
							greenBlock.setSize(myBoard.scaleX, myBoard.scaleY);
							greenBlock.draw(batch);
						}
						if(myBoard.gameBoard[y][x]==Color.RED){
							redBlock.setPosition(myBoard.scaleX*x+screenWidth/5.3f, myBoard.scaleY *y+100f);
							redBlock.setSize(myBoard.scaleX, myBoard.scaleY);
							redBlock.draw(batch);
						}
						if(myBoard.gameBoard[y][x]==Color.BLUE){
							blueBlock.setPosition(myBoard.scaleX*x+screenWidth/5.3f, myBoard.scaleY *y+100f);
							blueBlock.setSize(myBoard.scaleX, myBoard.scaleY);
							blueBlock.draw(batch);
						}
						if(myBoard.gameBoard[y][x]==Color.MAGENTA){
							purpleBlock.setPosition(myBoard.scaleX*x+screenWidth/5.3f, myBoard.scaleY *y+100f);
							purpleBlock.setSize(myBoard.scaleX, myBoard.scaleY);
							purpleBlock.draw(batch);
						}
						if(myBoard.gameBoard[y][x]==Color.ORANGE){
							orangeBlock.setPosition(myBoard.scaleX*x+screenWidth/5.3f, myBoard.scaleY *y+100f);
							orangeBlock.setSize(myBoard.scaleX, myBoard.scaleY);
							orangeBlock.draw(batch);
						}
						if(myBoard.gameBoard[y][x]==Color.YELLOW){
							yellowBlock.setPosition(myBoard.scaleX*x+screenWidth/5.3f, myBoard.scaleY *y+100f);
							yellowBlock.setSize(myBoard.scaleX, myBoard.scaleY);
							yellowBlock.draw(batch);
						}
						if(myBoard.gameBoard[y][x]==Color.CYAN){
							cyanBlock.setPosition(myBoard.scaleX*x+screenWidth/5.3f, myBoard.scaleY *y+100f);
							cyanBlock.setSize(myBoard.scaleX, myBoard.scaleY);
							cyanBlock.draw(batch);
						}
						
					}
				}
			}
			
			//Draw falling piece
			

			for (int y = 0; y < myBoard.fallingPiece.getHeight(); y++) 
			{
				for (int x = 0; x < myBoard.fallingPiece.getWidth(); x++) 
				{
					if (myBoard.fallingPiece.mMatrix[y][x] == true) 
					{
						
						switch(myBoard.fallingPiece.getColorName())
						{
						case "red":
							redBlock.setPosition(myBoard.scaleX*(myBoard.fallingPiece.getX() + x)+screenWidth/5.3f, myBoard.scaleY * (myBoard.fallingPiece.getY() + y)+100f);
							redBlock.setSize(myBoard.scaleX, myBoard.scaleY);
							redBlock.draw(batch);
							break;
						case "blue":
							blueBlock.setPosition(myBoard.scaleX*(myBoard.fallingPiece.getX() + x)+screenWidth/5.3f, myBoard.scaleY * (myBoard.fallingPiece.getY() + y)+100f);
							blueBlock.setSize(myBoard.scaleX, myBoard.scaleY);
							blueBlock.draw(batch);
							break;
						case "magenta":
							purpleBlock.setPosition(myBoard.scaleX*(myBoard.fallingPiece.getX() + x)+screenWidth/5.3f, myBoard.scaleY * (myBoard.fallingPiece.getY() + y)+100f);
							purpleBlock.setSize(myBoard.scaleX, myBoard.scaleY);
							purpleBlock.draw(batch);
							break;
						case "orange":
							orangeBlock.setPosition(myBoard.scaleX*(myBoard.fallingPiece.getX() + x)+screenWidth/5.3f, myBoard.scaleY * (myBoard.fallingPiece.getY() + y)+100f);
							orangeBlock.setSize(myBoard.scaleX, myBoard.scaleY);
							orangeBlock.draw(batch);
							break;
						case "yellow":
							yellowBlock.setPosition(myBoard.scaleX*(myBoard.fallingPiece.getX() + x)+screenWidth/5.3f, myBoard.scaleY * (myBoard.fallingPiece.getY() + y)+100f);
							yellowBlock.setSize(myBoard.scaleX, myBoard.scaleY);
							yellowBlock.draw(batch);
							break;
						case "cyan":
							cyanBlock.setPosition(myBoard.scaleX*(myBoard.fallingPiece.getX() + x)+screenWidth/5.3f, myBoard.scaleY * (myBoard.fallingPiece.getY() + y)+100f);
							cyanBlock.setSize(myBoard.scaleX, myBoard.scaleY);
							cyanBlock.draw(batch);
							break;
						case "green":
							greenBlock.setPosition(myBoard.scaleX*(myBoard.fallingPiece.getX() + x)+screenWidth/5.3f, myBoard.scaleY * (myBoard.fallingPiece.getY() + y)+100);
							greenBlock.setSize(myBoard.scaleX, myBoard.scaleY);
							greenBlock.draw(batch);
							break;
						}
						
					}
				}
			}
			
			
		}
}