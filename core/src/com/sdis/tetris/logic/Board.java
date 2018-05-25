package com.sdis.tetris.logic;

import java.util.ArrayList;
import java.util.Arrays;

import com.badlogic.gdx.graphics.Color;
import com.sdis.tetris.audio.AudioHandler;
import com.sdis.tetris.audio.SFX;
import com.sdis.tetris.network.ColorJSON;

public class Board {
	public int boardWidth = 10;
	public int boardHeight = 20;
	public int scaleX = 30;
	public int scaleY = 30;
	public final Color gameBoard[][] = new Color[boardHeight][boardWidth];
	public volatile Color cloneBoard[][] = new Color[boardHeight][boardWidth];
	public String playerName;
	public Tetromino fallingPiece;
	private int playerScore;
	private int currentLevel;
	private Bounds gameBounds;
	private int levelRowsRemoved;
	boolean gameOver;
	
	
	/**
	 * Moves the fallingPiece downand plays move sound effect
	 * Sticks the Piece onto the board if conditions are met
	 * GameOver check if the Piece is on a certain position of the board
	 * Checks if rows are filled
	 * Generates new Pieces after the previous one gets Stuck
	 * @return
	 */
	public int moveDown() 
	{
		if(gameOver)
			return -1;
		fallingPiece.moveDown(this);
		AudioHandler.getInstance().playSFX(SFX.SFX_MOVE);
		if (fallingPiece.isStuck()) 
		{
			stuckPiece();
			if (fallingPiece.getY() >= boardHeight-4) 
			{
				gameOver=true;
			}
			int rowsDeleted = checkRows();
			fallingPiece = TetrominoProto.generateRandom();
			fallingPiece.setPosition(boardWidth/2, boardHeight-3);
			fallingPiece.mFalling=true;
			return rowsDeleted;
		}
		return 0;
	}
	
	/**
	 * Moves the fallingPiece into the left and plays move sound effect
	 * Sticks the Piece onto the board if conditions are met
	 * @return
	 */
	public void moveLeft() 
	{
		fallingPiece.moveLeft(this);
		AudioHandler.getInstance().playSFX(SFX.SFX_MOVE);
		if (fallingPiece.isStuck()) 
		{
			stuckPiece();
		}

	}
	
	
	/**
	 * Moves the fallingPiece into the right and plays move sound effect
	 * Sticks the Piece onto the board if conditions are met
	 * @return
	 */
	public void moveRight() 
	{
		fallingPiece.moveRight(this);
		AudioHandler.getInstance().playSFX(SFX.SFX_MOVE);
		if (fallingPiece.isStuck()) 
		{
			stuckPiece();
		}

	}
	
	/**
	 * Getter for the board
	 * @return gameBoard
	 */
	public Color[][] getBoard() 
	{
		return gameBoard;
	}
	
	/**
	 * Board constructor
	 * Will initialize the game by generating a piece and placing it on the board
	 * @return
	 */
	public Board() 
	{
		fallingPiece = TetrominoProto.generateRandom();
		fallingPiece.setPosition(boardWidth/2, boardHeight-3);
		gameBounds = new Bounds(0, 0, boardWidth, boardHeight);
		gameOver=false;
	}
	
	/**
	 * Board constructor
	 * @return
	 */
	public Board(int scX, int scY) 
	{
		scaleX = scX;
		scaleY = scY;
		cloneBoard=new Color[boardHeight][boardWidth];
	}
	
	
	/**
	 * This function will check if the current falling Piece has collided or reached the bottom 
	 * If any of the above conditions are true, the Piece will be glued onto the board with it's color
	 * @return
	 */
	public void stuckPiece() 
	{
		final boolean[][] currentShape = fallingPiece.getShape();

		for (int y = 0; y < fallingPiece.getHeight(); y++) 
		{
			for (int x = 0; x < fallingPiece.getWidth(); x++) 
			{
				if (currentShape[y][x] == true) 
				{
					if((fallingPiece.getX()+x < this.boardWidth) && (fallingPiece.getY()+y < this.boardHeight))
					{
						gameBoard[fallingPiece.getY() + y][fallingPiece.getX() + x] = fallingPiece.getColor();
						AudioHandler.getInstance().playSFX(SFX.SFX_COLL);
					}
				}
			}
		}

		
	}
	
	public ArrayList<ColorJSON> screenshotBoard() 
	{
		for(int i=0;i<cloneBoard.length;i++) {
			cloneBoard[i] = Arrays.copyOf(gameBoard[i],gameBoard[i].length);
		}
		
		final boolean[][] currentShape = fallingPiece.getShape();
		
		for (int y = 0; y < fallingPiece.getHeight(); y++) 
		{
			for (int x = 0; x < fallingPiece.getWidth(); x++) 
			{
				if (currentShape[y][x] == true) 
				{
					if((fallingPiece.getX()+x < cloneBoard[0].length) && (fallingPiece.getY()+y < cloneBoard.length))
					{
						cloneBoard[fallingPiece.getY() + y][fallingPiece.getX() + x] = fallingPiece.getColor();
					}
				}
			}
		}
		
		ArrayList<ColorJSON> screenshot = new ArrayList<>();

		for(int h=0;h<boardHeight;h++) {
			for(int w=0;w<boardWidth;w++) {
				if(cloneBoard[h][w]!=null)
					screenshot.add(new ColorJSON(cloneBoard[h][w].r,cloneBoard[h][w].g,cloneBoard[h][w].b,cloneBoard[h][w].a,h,w));
			}	
		}
		
		return screenshot;
	}
	
	public void updateCloneBoard(Color[][] screenshot) {
		cloneBoard=screenshot;
	}

	/**
	 * This function is used to check if any row is filled with blocks and will call the removeRow
	 * function to clear those blocks, adding score and updating the current level of the game
	 * Depending on the number of rows cleared, a sound effect will play
	 * @return
	 */
	private int checkRows() 
	{

		int rowsRemoved = 0;
			for (int i = 0; i < gameBoard.length; i++) //rows
			{
				boolean full = true;

				for (int j = 0; j < gameBoard[0].length; j++) 
				{
					if (gameBoard[i][j] == null) 
					{
						full = false;
					}
				}

				if (full) 
				{
					removeRow(i);
					i--;
					rowsRemoved++;
					levelRowsRemoved++;
				} 
			}



		if (rowsRemoved > 0 && rowsRemoved!=4) 
		{
			AudioHandler.getInstance().playSFX(SFX.SFX_CLEARLINE);
		}
		else if (rowsRemoved==4)
		{
			AudioHandler.getInstance().playSFX(SFX.SFX_TETRIS);
		}

		switch (rowsRemoved) 
		{
		case 1:
			playerScore += 40 * (currentLevel + 1);
			break;
		case 2:
			playerScore += 100 * (currentLevel + 1);
			break;
		case 3:
			playerScore += 300 * (currentLevel + 1);
			break;
		case 4:
			playerScore += 1200 * (currentLevel + 1);
			break;
		}

		if (levelRowsRemoved >= 5) 
		{
			currentLevel++;
			levelRowsRemoved = 0;
			AudioHandler.getInstance().playSFX(SFX.HOVER);

		}
		
		return rowsRemoved;
	}

	
	/**
	 * This function is used to clear a line in the board after the player fills it with blocks
	 * It will also move the pieces above to the now empty spaces of the row index in parameter
	 * @param row row index of row to clear
	 * @return
	 */
	private void removeRow(int row) 
	{

		for (int j = 0; j < boardWidth; j++) 
		{
			gameBoard[row][j] = null;
		}
		
		for (int i = row+1; i < boardHeight; i++) 
		{
			for (int j = 0; j < boardWidth; j++) 
			{
				gameBoard[i-1][j] = gameBoard[i][j];
				gameBoard[i][j] = null;
			}
		}
	}
	
	/**
	 * Calls the rotate function of the current Piece and plays rotation sound effect
	 * Yellow blocks do not rotate and the function will return prematurely in that case
	 * @return
	 */
	public void rotatePiece() 
	{
		if(fallingPiece instanceof TetrominoProto.TetrominoO)
		return;
		fallingPiece.rotatePiece(this, gameBounds);
		AudioHandler.getInstance().playSFX(SFX.SFX_ROTATE);
	}

	/**
	 * Checks if the block is within the board bounds
	 * @param rowIndex is the row index of the falling Piece
	 * @param columnIndex is the column index of the falling Piece
	 * @return checks collision between blocks
	 */
	public boolean checkCollision(int rowIndex, int columnIndex) 
	{
		return gameBoard[rowIndex][columnIndex] != null;
	}

	/**
	 * Checks if the block is within the board bounds
	 * @param blockRow is the row index of the falling Piece
	 * @param blockCol is the column index of the falling Piece
	 * @return bounds check result
	 */
	public boolean checkBounds(int blockRow, int blockCol) 
	{
		return gameBounds.checkBounds(blockRow, blockCol);
	}
	
	/**
	 * gets gameover boolean
	 * @return gameOver boolean
	 */
	public boolean isGameOver(){
		return gameOver;
	}
	
	/**
	 * gets player score
	 * @return playerScore
	 */
	public int getPlayerScore(){
		return playerScore;
	}

	/**
	 * gets currentLevel
	 * @return currentLevel
	 */
	public int getCurrentLevel(){
		return currentLevel;
	}
	
	/**
	 * sets playerScore
	 * @param val 
	 * @return
	 */
	public void setPlayerScore(int val){
		playerScore=val;
	}
	
	/**
	 * sets level
	 * @param val 
	 * @return
	 */
	public void setCurrentLevel(int val){
		currentLevel=val;
	}
	
	/**
	 * sets gameover boolean
	 * @return
	 */
	public void setGameOver(boolean c){
		gameOver=c;
	}
	
	/**
	 * increments player score
	 * @param inc value to increment
	 * @return
	 */
	public void incPlayerScore(int inc){
		playerScore+=inc;
	}
	

}