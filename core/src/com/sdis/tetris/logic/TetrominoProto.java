package com.sdis.tetris.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;

public class TetrominoProto 
{
	private static Random myRandom = new Random();

	static class TetrominoI extends Tetromino
	{
		private final static boolean[][] ArotI =
					{
					{false, false, false, false},
					{true, true, true, true},
					{false, false, false, false},
					{false, false, false, false}
					};
		
		/**
		 * I Tetromino Constructor(red)
		 * Creates Tetromino of Type I 
		 * @return
		 */
		public TetrominoI()
		{
			super(Color.RED, ArotI);
			colorName="red";
		}
		
		public final Tetromino clone()
		{
			return new TetrominoI();
		}
	}

	static class TetrominoO extends Tetromino
	{
		private final static boolean[][] ArotO =
					{
					{false, true, true, false},
		            {false, true, true, false},
		            {false, false, false, false}
		            };

		/**
		 * O Tetromino Constructor(yellow)
		 * Creates Tetromino of Type O 
		 * @return
		 */
		public TetrominoO()
		{
			super(Color.YELLOW,  ArotO);
			colorName="yellow";
		}

		public final Tetromino clone()
		{
			return new TetrominoO();
		}
	}

	static class TetrominoT extends Tetromino
	{
		private final static boolean[][] ArotT =
					{
					{false, true, false},
		            {true, true, true},
		            {false, false, false}
		            };

		/**
		 * T Tetromino Constructor(cyan)
		 * Creates Tetromino of Type T 
		 * @return
		 */
		public TetrominoT()
		{
			super(Color.CYAN, ArotT);
			colorName="cyan";
		}

		public final Tetromino clone()
		{
			return new TetrominoT();
		}
	}
	
	static class TetrominoJ extends Tetromino
	{
		private final static boolean[][] ArotJ =
					{
					{true, false, false},
		            {true, true, true},
		            {false, false, false}
		            };
		
		/**
		 * J Tetromino Constructor(blue)
		 * Creates Tetromino of Type J
		 * @return
		 */
		public TetrominoJ()
		{
			super(Color.BLUE, ArotJ);
			colorName="blue";
		}

		public final Tetromino clone()
		{
			return new TetrominoJ();
		}
	}
	
	static class TetrominoL extends Tetromino
	{
		private final static boolean[][] ArotL =
					{
					{false, false, true},
		            {true, true, true},
		            {false, false, false}
		            };
		
		/**
		 * L Tetromino Constructor(orange)
		 * Creates Tetromino of Type L
		 * @return
		 */
		public TetrominoL()
		{
			super(Color.ORANGE, ArotL);
			colorName="orange";
		}

		public final Tetromino clone()
		{
			return new TetrominoL();
		}
	}
	
	static class TetrominoS extends Tetromino
	{
		private final static boolean[][] tetrominoShape =
					{
					{false, true, true},
		            {true, true, false},
		            {false, false, false}
		            };

		/**
		 * S Tetromino Constructor(magenta)
		 * Creates Tetromino of Type S 
		 * @return
		 */
		public TetrominoS()
		{
			super(Color.MAGENTA, tetrominoShape);
			colorName="magenta";
		}

		public final Tetromino clone()
		{
			return new TetrominoS();
		}
	}
	
	static class TetrominoZ extends Tetromino
	{
		private final static boolean[][] tetrominoShape =
					{
					{true, true, false},
					{false, true, true},
					{false, false, false}
					};

		/**
		 * Z Tetromino Constructor(green)
		 * Creates Tetromino of Type Z 
		 * @return
		 */
		public TetrominoZ()
		{
			super(Color.GREEN, tetrominoShape);
			colorName="green";
		}

		public final Tetromino clone()
		{
			return new TetrominoZ();
		}
	}
	
	private static ArrayList<Tetromino> tetrominoesArray = new ArrayList<>();
	private static HashMap<Character, Integer> tetrominoesIndex = new HashMap<>();
	
	static
	{
		tetrominoesArray.add(new TetrominoI());
		tetrominoesArray.add(new TetrominoO());
		tetrominoesArray.add(new TetrominoT());
		tetrominoesArray.add(new TetrominoJ());
		tetrominoesArray.add(new TetrominoL());
		tetrominoesArray.add(new TetrominoS());
		tetrominoesArray.add(new TetrominoZ());	
		tetrominoesIndex.put('I', 0);
		tetrominoesIndex.put('O', 1);
		tetrominoesIndex.put('T', 2);
		tetrominoesIndex.put('J', 3);
		tetrominoesIndex.put('L', 4);
		tetrominoesIndex.put('S', 5);
		tetrominoesIndex.put('Z', 6);
	}
	
	/**
	 * Gets a random type of Tetromino
	 * @return Tetromino
	 */
	public static Tetromino generateRandom()
	{
		int tetrominoIndex = myRandom.nextInt(tetrominoesArray.size());
		return tetrominoesArray.get(tetrominoIndex);
	}
	
	/**
	 * Generates a specific type of Tetromino
	 * @tetrominoType type to generate
	 * @return Tetromino
	 */
	public static Tetromino generateTetromino(char tetrominoType)
	{
		int tetrominoIndex = tetrominoesIndex.get(tetrominoType);
		return tetrominoesArray.get(tetrominoIndex);
	}
}