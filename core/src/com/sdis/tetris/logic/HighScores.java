package com.sdis.tetris.logic;

import java.util.ArrayList;
import java.util.Collections;

public class HighScores 
{
	public class Score implements Comparable<Object>
	{
		double score;
		String name;
		/**
		 * Score Constructor
		 * @param s value of score
		 * @param n name of player
		 * @return
		 */
		public Score(int s,String n) 
		{
			score=s;
			name=n;
		}
		
		/**
		 * Score compareTo function
		 * @param o Object to compare
		 * @return comparison in ascending order
		 */
		@Override
		public int compareTo(Object o) 
		{
			return (int) (this.score-((Score)o).score);
		}
		
		public String toString(){
			return ""+name+":  "+score+"";
		}
	}
	
	ArrayList<Score> scores;
	
	/**
	 * HighScores Constructor
	 * @return
	 */
	public HighScores()
	{
		scores=new ArrayList<Score>();
	}
	
	/**
	 * get sorted ArrayList of scores
	 * @return sorted list of scores
	 */
	public ArrayList<Score> getSortedScores()
	{
		Collections.sort(scores);
		return scores;
	}
	
	public void addScore(int score,String Name)
	{
		scores.add(new Score(score,Name));
		
	}
}
