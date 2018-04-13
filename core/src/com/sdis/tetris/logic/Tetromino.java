package com.sdis.tetris.logic;

import java.util.Arrays;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public abstract class Tetromino 
{
	private Color color;
	private int mWidth;
	private int mHeight;
	private int currentX = 0;
	private int currentY = 0;
	public boolean mFalling;
	String colorName;
	public boolean[][] mMatrix;

	
	/**
	 * Tetromino Constructor
	 * Initializes class variables
	 * @param tetColor color of tetromino
	 * @param tetShape shape of tetromino
	 * @return
	 */
	public Tetromino(final Color tetColor, final boolean[][] tetShape) 
	{
		color = tetColor;
		mMatrix = tetShape;
		mFalling = true;
		mWidth = tetShape[0].length;
		mHeight = tetShape.length;
	}


	/**
	 * Moves the Tetromino down if possible
	 * Checks for collision and bounds
	 * updates mFalling boolean used to stick the Piece onto the board
	 * @param gameBoard board of the game
	 * @return
	 */
	public void moveDown(final Board gameBoard) 
	{
		if (!mFalling) 
		{
			return;
		}
		boolean possible = true;

		for (int i = 0; i < mMatrix.length; i++) 
		{
			for (int j = 0; j < mMatrix[0].length; j++) 
			{
				if (mMatrix[i][j]) 
				{
					int blockRow = currentY + i;
					int blockCol = currentX + j;
					if (!gameBoard.checkBounds(blockRow - 1, blockCol)) 
					{
						possible = false;
					} 
					else if (gameBoard.checkCollision(blockRow - 1, blockCol)) 
					{
						possible = false;
					}
				}
			}
		}
		if (possible) 
		{
			currentY--;
		} 
		else
		{
			mFalling = false;
		}

	}

	
	/**
	 * Checks if piece is stuck to the board
	 * @return mFalling
	 */
	public boolean isStuck() 
	{
		return !mFalling;
	}
	
	/**
	 * Moves the Tetromino to the left
	 * @param gameBoard board of the game
	 * @return
	 */
	public void moveLeft(final Board gameBoard) 
	{
		move(-1, gameBoard);
	}
	
	/**
	 * Moves the Tetromino to the right
	 * @param gameBoard board of the game
	 * @return
	 */
	public void moveRight(final Board gameBoard) 
	{
		move(1, gameBoard);
	}

	/**
	 * Moves the Tetromino in the X axis if possible
	 * @param deltaX move offset
	 * @param gameBoard board of the game
	 * @return
	 */
	private void move(int deltaX, final Board gameBoard) 
	{

		if (!mFalling) 
		{
			return;
		}

		int newPosX = currentX + deltaX;
		boolean possible = true;

		for (int i = 0; i < mMatrix.length; i++) 
		{
			for (int j = 0; j < mMatrix[0].length; j++) 
			{
				if (mMatrix[i][j]) 
				{
					int blockRow = currentY + i;
					int blockCol = newPosX + j;

					if (!gameBoard.checkBounds(blockRow, blockCol)) 
					{
						possible = false;
					} 
					else if (gameBoard.checkCollision(blockRow, blockCol)) 
					{
						possible = false;
					}
				}
			}
		}

		if (possible) 
		{
			currentX = newPosX;
		}
	}

	/**
	 * rotates the Tetromino if possible
	 * @param gameBoard board of the game
	 * @param myRectable bounds of the piece
	 * @return
	 */
	public void rotatePiece(final Board gameBoard, final Bounds myRectangle) 
	{

		if (!mFalling) 
		{
			return;
		}

		int size = mHeight;

		boolean[][] rotatedGrid = new boolean[size][size];

		for (int i = 0; i < size; i++) 
		{

			for (int j = 0; j < size; j++) 
			{
				rotatedGrid[j][size - 1 - i] = mMatrix[i][j];
			}
		}

		boolean validMove = true;

		for (int i = 0; i < size; i++) 
		{
			for (int j = 0; j < size; j++) 
			{
				if (rotatedGrid[i][j]) 
				{
					int blockRow = currentY + i;
					int blockCol = currentX + j;

					if (!myRectangle.checkBounds(blockRow, blockCol)) 
					{
						validMove = false;
					} 
					else if (gameBoard.checkCollision(blockRow, blockCol)) 
					{
						validMove = false;
					}
				}
			}
		}

		if (validMove) {
			mMatrix = Arrays.copyOf(rotatedGrid, rotatedGrid.length);
		}
	}
	
	/**
	 * get Width of the Tetromino
	 * @return mWidth
	 */
	public int getWidth() {
		return mWidth;
	}
	
	/**
	 * get Height of the Tetromino
	 * @return mHeight
	 */
	public int getHeight() {
		return mHeight;
	}
	
	/**
	 * get Length of the Tetromino
	 * @return Length
	 */
	public int getLength() {
		return mWidth * mHeight;
	}

	/**
	 * get Matrix of the Tetromino
	 * @return Shape of the Tetromino
	 */
	public boolean[][] getShape() {
		return mMatrix;
	}
	
	/**
	 * get current Y value
	 * @return currentY
	 */
	public int getY() {
		return currentY;
	}
	
	/**
	 * get current X value
	 * @return currentX
	 */
	public int getX() {
		return currentX;
	}
	

	/**
	 * get color 
	 * @return color
	 */
	public Color getColor() 
	{
		return color;
	}
	
	/**
	 * get color name 
	 * @return colorName
	 */
	public String getColorName(){
		return colorName;
	}
	
	/**
	 * set tetromino Position 
	 * @param x value
	 * @oaram y value
	 * @return
	 */
	public void setPosition(int x, int y) 
	{
		currentX = x;
		currentY = y;
	}
	public abstract Tetromino clone();
}