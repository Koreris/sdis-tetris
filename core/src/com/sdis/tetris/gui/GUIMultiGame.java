package com.sdis.tetris.gui;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.sdis.tetris.Buttons;
import com.sdis.tetris.Tetris;
import com.sdis.tetris.TetrisPreferences;
import com.sdis.tetris.audio.Song;
import com.sdis.tetris.logic.Board;
import com.sdis.tetris.network.TetrisClient;

public class GUIMultiGame extends GUIScreen
{


	private GameState state;
	private final float screenWidth = Gdx.graphics.getWidth();
	private final float screenHeight = Gdx.graphics.getHeight();
	private final Stage stage = new Stage();

	private Board myBoard = new Board();
	protected int opponentNr;
	public Board smallBoard1 = new Board(15,15);
	public Board smallBoard2 = new Board(15,15);
	public Board smallBoard3 = new Board(15,15);
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
	Sprite smallFrame1 = new Sprite(new Texture (Gdx.files.internal("img/frame.png"), false));
	Sprite smallFrame2 = new Sprite(new Texture (Gdx.files.internal("img/frame.png"), false));
	Sprite smallFrame3 = new Sprite(new Texture (Gdx.files.internal("img/frame.png"), false));
	Task t1;
	float minBoardWidth=(screenWidth/5.25f);
	float maxBoardWidth=myBoard.boardWidth*myBoard.scaleX;
	float minBoardHeight=0;
	float maxBoardHeight=(myBoard.boardHeight*myBoard.scaleY);
	int count=0;
	int sendStateCount=0;
	String playerName;
	private TetrisClient client;
	ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 5, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	public void changeState(GameState newState)
	{
		state = newState;
	}


	public GUIMultiGame(Tetris paramParent) 
	{
		super(paramParent, TetrisPreferences.getTheme());
		opponentNr=paramParent.opponentNr;
		client = paramParent.networkClient;
		playerName = paramParent.playerName;
		reStartGame();
		t1.cancel();
		changeState(new GameRunningState());
		new Thread() {
			public void run() {
				while(true) {
					int result = client.listen_lobby_socket(GUIMultiGame.this);
					if(result==0)
						break;
				}
			}
		}.start();
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



	public boolean isGameRunning()
	{
		return !(state instanceof GameRunningState);
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
			Gdx.input.setInputProcessor(GUIMultiGame.this);

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
			table.setPosition(0,minBoardHeight+250f);
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
			sendStateCount++;
			
			if(sendStateCount>=5) {
				
				executor.execute(new Runnable() {
					public void run() {
						try {
							client.send_game_state(parent.playerName, parent.serverName, myBoard.screenshotBoard(), myBoard.getPlayerScore());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
				sendStateCount=0;
			}

			if(myBoard.isGameOver()){
				executor.execute(new Runnable() {
					public void run() {
						try {
							client.send_game_over(parent.playerName);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
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

			drawBoard(9.1f);
		
			switch(opponentNr)
			{
			case 3:
				drawSmallBoard(smallBoard1.cloneBoard, smallBoard1, smallFrame1, 350f, 250f, 2.35f);
				drawSmallBoard(smallBoard2.cloneBoard, smallBoard2, smallFrame2, 600f, 250f, 1.64f);
				drawSmallBoard(smallBoard3.cloneBoard, smallBoard3, smallFrame3, 850f, 250f, 1.26f);
				break;
			case 2:
				drawSmallBoard(smallBoard1.cloneBoard, smallBoard1, smallFrame1, 350f, 250f, 2.35f);
				drawSmallBoard(smallBoard2.cloneBoard, smallBoard2, smallFrame2, 600f, 250f, 1.64f);
				break;
			default:
				
				drawSmallBoard(smallBoard1.cloneBoard, smallBoard1, smallFrame1, 350f, 250f, 2.35f);
				break;	
			}
			

			if(myBoard.isGameOver())
			{
				parent.addToHighScores(myBoard.getPlayerScore(), playerName);
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

	

	private class GameOverState extends GameState
	{
		private final Stage stageOver = new Stage();

		public GameOverState()
		{
			audio.playSong(Song.THEME_GAME_OVER, true);

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

	public void drawBoard(float delta)
	{
		//Draw Board
		boardFrame.setPosition(minBoardWidth-135f,minBoardHeight+47f);
		boardFrame.setSize(maxBoardWidth+50f, maxBoardHeight+50f);
		boardFrame.draw(batch);

		for (int y = 0; y < myBoard.boardHeight; y++) 
		{
			for (int x = 0; x <myBoard.boardWidth; x++) 
			{
				if (myBoard.gameBoard[y][x] != null) 
				{
					if(myBoard.gameBoard[y][x]==Color.GREEN){
						greenBlock.setPosition(myBoard.scaleX*x+screenWidth/delta, myBoard.scaleY *y+100f);
						greenBlock.setSize(myBoard.scaleX, myBoard.scaleY);
						greenBlock.draw(batch);
					}
					if(myBoard.gameBoard[y][x]==Color.RED){
						redBlock.setPosition(myBoard.scaleX*x+screenWidth/delta, myBoard.scaleY *y+100f);
						redBlock.setSize(myBoard.scaleX, myBoard.scaleY);
						redBlock.draw(batch);
					}
					if(myBoard.gameBoard[y][x]==Color.BLUE){
						blueBlock.setPosition(myBoard.scaleX*x+screenWidth/delta, myBoard.scaleY *y+100f);
						blueBlock.setSize(myBoard.scaleX, myBoard.scaleY);
						blueBlock.draw(batch);
					}
					if(myBoard.gameBoard[y][x]==Color.MAGENTA){
						purpleBlock.setPosition(myBoard.scaleX*x+screenWidth/delta, myBoard.scaleY *y+100f);
						purpleBlock.setSize(myBoard.scaleX, myBoard.scaleY);
						purpleBlock.draw(batch);
					}
					if(myBoard.gameBoard[y][x]==Color.ORANGE){
						orangeBlock.setPosition(myBoard.scaleX*x+screenWidth/delta, myBoard.scaleY *y+100f);
						orangeBlock.setSize(myBoard.scaleX, myBoard.scaleY);
						orangeBlock.draw(batch);
					}
					if(myBoard.gameBoard[y][x]==Color.YELLOW){
						yellowBlock.setPosition(myBoard.scaleX*x+screenWidth/delta, myBoard.scaleY *y+100f);
						yellowBlock.setSize(myBoard.scaleX, myBoard.scaleY);
						yellowBlock.draw(batch);
					}
					if(myBoard.gameBoard[y][x]==Color.CYAN){
						cyanBlock.setPosition(myBoard.scaleX*x+screenWidth/delta, myBoard.scaleY *y+100f);
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
						redBlock.setPosition(myBoard.scaleX*(myBoard.fallingPiece.getX() + x)+screenWidth/delta, myBoard.scaleY * (myBoard.fallingPiece.getY() + y)+100f);
						redBlock.setSize(myBoard.scaleX, myBoard.scaleY);
						redBlock.draw(batch);
						break;
					case "blue":
						blueBlock.setPosition(myBoard.scaleX*(myBoard.fallingPiece.getX() + x)+screenWidth/delta, myBoard.scaleY * (myBoard.fallingPiece.getY() + y)+100f);
						blueBlock.setSize(myBoard.scaleX, myBoard.scaleY);
						blueBlock.draw(batch);
						break;
					case "magenta":
						purpleBlock.setPosition(myBoard.scaleX*(myBoard.fallingPiece.getX() + x)+screenWidth/delta, myBoard.scaleY * (myBoard.fallingPiece.getY() + y)+100f);
						purpleBlock.setSize(myBoard.scaleX, myBoard.scaleY);
						purpleBlock.draw(batch);
						break;
					case "orange":
						orangeBlock.setPosition(myBoard.scaleX*(myBoard.fallingPiece.getX() + x)+screenWidth/delta, myBoard.scaleY * (myBoard.fallingPiece.getY() + y)+100f);
						orangeBlock.setSize(myBoard.scaleX, myBoard.scaleY);
						orangeBlock.draw(batch);
						break;
					case "yellow":
						yellowBlock.setPosition(myBoard.scaleX*(myBoard.fallingPiece.getX() + x)+screenWidth/delta, myBoard.scaleY * (myBoard.fallingPiece.getY() + y)+100f);
						yellowBlock.setSize(myBoard.scaleX, myBoard.scaleY);
						yellowBlock.draw(batch);
						break;
					case "cyan":
						cyanBlock.setPosition(myBoard.scaleX*(myBoard.fallingPiece.getX() + x)+screenWidth/delta, myBoard.scaleY * (myBoard.fallingPiece.getY() + y)+100f);
						cyanBlock.setSize(myBoard.scaleX, myBoard.scaleY);
						cyanBlock.draw(batch);
						break;
					case "green":
						greenBlock.setPosition(myBoard.scaleX*(myBoard.fallingPiece.getX() + x)+screenWidth/delta, myBoard.scaleY * (myBoard.fallingPiece.getY() + y)+100);
						greenBlock.setSize(myBoard.scaleX, myBoard.scaleY);
						greenBlock.draw(batch);
						break;
					}

				}
			}
		}
	}

	public void drawSmallBoard(Color[][] receivedScreenshot, Board smallBoard,Sprite smallFrame, float framePosX, float framePosY, float delta)
	{

		//Draw Board
		smallFrame.setPosition(minBoardWidth+framePosX,minBoardHeight+framePosY);
		smallFrame.setScale(1.85f, 1.75f);
		smallFrame.draw(batch);
		
		for (int y = 0; y < smallBoard.boardHeight; y++) 
		{
			for (int x = 0; x < smallBoard.boardWidth; x++) 
			{
				if (receivedScreenshot[y][x] != null) 
				{
					if(receivedScreenshot[y][x].equals(Color.GREEN))
					{
						greenBlock.setPosition(smallBoard.scaleX*x+screenWidth/delta, smallBoard.scaleY *y+208f);
						greenBlock.setSize(smallBoard.scaleX, smallBoard.scaleY);
						greenBlock.draw(batch);
					}
					else if(receivedScreenshot[y][x].equals(Color.RED))
					{
						redBlock.setPosition(smallBoard.scaleX*x+screenWidth/delta, smallBoard.scaleY *y+208f);
						redBlock.setSize(smallBoard.scaleX, smallBoard.scaleY);
						redBlock.draw(batch);
					}
					else if(receivedScreenshot[y][x].equals(Color.BLUE))
					{
						blueBlock.setPosition(smallBoard.scaleX*x+screenWidth/delta, smallBoard.scaleY *y+208f);
						blueBlock.setSize(smallBoard.scaleX, smallBoard.scaleY);
						blueBlock.draw(batch);
					}
					else if(receivedScreenshot[y][x].equals(Color.MAGENTA))
					{
						purpleBlock.setPosition(smallBoard.scaleX*x+screenWidth/delta, smallBoard.scaleY *y+208f);
						purpleBlock.setSize(smallBoard.scaleX, smallBoard.scaleY);
						purpleBlock.draw(batch);
					}
					else if(receivedScreenshot[y][x].equals(Color.ORANGE)) 
					{
						orangeBlock.setPosition(smallBoard.scaleX*x+screenWidth/delta, smallBoard.scaleY *y+208f);
						orangeBlock.setSize(smallBoard.scaleX, smallBoard.scaleY);
						orangeBlock.draw(batch);
					}
					else if(receivedScreenshot[y][x].equals(Color.YELLOW)){
						yellowBlock.setPosition(smallBoard.scaleX*x+screenWidth/delta, smallBoard.scaleY *y+208f);
						yellowBlock.setSize(smallBoard.scaleX, smallBoard.scaleY);
						yellowBlock.draw(batch);
					}
					else if(receivedScreenshot[y][x].equals(Color.CYAN)){
						cyanBlock.setPosition(smallBoard.scaleX*x+screenWidth/delta, smallBoard.scaleY *y+208f);
						cyanBlock.setSize(smallBoard.scaleX, smallBoard.scaleY);
						cyanBlock.draw(batch);
					}
					else System.out.println("NOT Drawing color "+ y + "," + x+ " "+receivedScreenshot[y][x]);
				}
			}
		}

	}
}
