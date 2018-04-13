package com.sdis.tetris.logic;

public class Bounds {

	private int x0;
	private int y0;
	private int xLength;
	private int yLength;
	
	/**
	 * Bounds Constructor
	 * @param x0
	 * @param y0
	 * @param xLength
	 * @param yLength
	 * @return
	 */
	public Bounds(int x0, int y0, int xLength, int yLength)
	{
		this.x0 = x0;
		this.y0 = y0;
		this.xLength = xLength;
		this.yLength = yLength;
	}
	
	/**
	 * Check if object is within bounds
	 * @param x
	 * @param y
	 * @return true if within bounds otherwise false
	 */
	public boolean checkBounds(int y, int x)
	{
		return x >= getMinimumX() && x < getMaximumX() && y >= getMinimumY() && y < getMaximumY();
	}
	
	/**
	 * Get minimum X
	 * @return x0
	 */
	public int getMinimumX()
	{
		return x0;
	}
	
	/**
	 * Get minimum Y
	 * @return y0
	 */
	public int getMinimumY()
	{
		return y0;
	}
	
	/**
	 * Get maximum Y
	 * @return maxY
	 */
	public int getMaximumY()
	{
		return y0 + yLength;
	}
	
	/**
	 * Get maximum X
	 * @return maxX
	 */
	public int getMaximumX()
	{
		return x0 + xLength;
	}
}
