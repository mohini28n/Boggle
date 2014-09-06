package com.example.bogglegame;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

@SuppressLint("DefaultLocale")
public class BoggleGame extends Activity{

	//private static Trie trie;
	private static BoggleGame game;
	private char[] letters;
	private Random rand;
	private int score;
	private Map<String,Boolean> possibleWords;
	public List<String> triedWords;
	//private Map<String,DAWG> tries;
	public List<String> list;
	public Context context;
	  public NavigableSet<String> dictionary;
	public enum TestResult{
		NOT_EXISTS,			/* word not in dictionary */
		HIT,				/* hit a valid entry in dictionary */
		ALREADY_TRIED		/* word already been scored */
	}
	
	/*
	 * get an instance of BoggleGame
	 * @return instance of BoggleGame
	 */
	
	public static BoggleGame GetInstance(){
		if(null==game){
			game=new BoggleGame();
		}
		return game;
	}
	
	
	private BoggleGame(){
		Log.d("BoggleGame", "constructor");
//		Log.d("file path",BoggleGame.class.);
		
		letters = new char[16];
		rand = new Random();
		possibleWords = new HashMap<String, Boolean>();
		//tries = new HashMap<String,DAWG>();
		triedWords = new ArrayList<String>();
		
	}	
	/*
	 * Start a new boggle game
	 */
	public char[] NewGame(NavigableSet<String> dictionary){
		score = 0;
		
		for(int i=0;i<16;i++){
			int num=rand.nextInt(26);
			letters[i]=(char) (num+65);
			char[][] board = { {letters[0], letters[1], letters[2], letters[3] },
		                             {letters[4], letters[5], letters[6], letters[7] },
		                             {letters[8], letters[9], letters[10], letters[11] },
		                             {letters[12], letters[13], letters[14], letters[15] },
		                          };
		     BoggleSolver BoggleSolver= new BoggleSolver(dictionary);
		
		     list = BoggleSolver.boggleSolve(board);
	         int count =0;
		     for (String str :  list) 
		     {
		              Log.i ("Strings", "" + str);
		              count++;
		          }
		     //Log.i ("info", "" + letters[i]);
		     System.out.println(count);
			
		}
		for(int n=0;n<16;n++){
			System.out.print(letters[n]+" ");
		}
		System.out.println();
		possibleWords.clear();
	//	computePossibleWords();		
		return letters;
	}
	
	public List<String> GetWords()
	{
		return list;
	}
	
	/*
	 * get standard solution and user's answer history in this round
	 * @return answer history and the standard solution
	 */
	public Map<String,Boolean> GetAnswerHistory(){
		return possibleWords;
	}

	/*
	 * get letters in the 4 by 4 grid
	 * @return letters
	 */
	public char[] GetLetters(){
		return letters;
	}	
	
	public GameStatus GetStatus(){
		GameStatus stat = new GameStatus();
		stat.words=new ArrayList<String>();
	//	stat.map=this.possibleWords;
		stat.letters=this.letters;
		stat.score=this.score;
		return stat;
	}
	
	/*
	 * resume the game by supplying game status
	 */
	public void Resume(GameStatus stat){
		
		this.letters=stat.letters;
		this.score=stat.score;
		this.possibleWords=new HashMap<String,Boolean>();
//		this.possibleWords=stat.map;
		
	}
	public int getwordscore(String word)
	{
		int wordscore = 0;
		switch(word.length()){
		case 3: 
			wordscore=1;
			break;
		case 4: 
			wordscore=1;
			break;
		case 5: 
			wordscore=2;
			break;
		case 6: 
			wordscore=3;
			break;
		case 7: 
			wordscore=5;
			break;
		default:
			wordscore=11;
		}

			
		return wordscore;
	}
	
	/*
	 * Test whether word is in dictionary and not being tried
	 * at the mean time also computing the score
	 * @word the word to be test
	 * @return TestResult
	 */
	public TestResult Test(String word){
		String prefix = Integer.toString(word.length())+word.charAt(0);
		Log.v("Boggle GAME","test prefix" +prefix.toLowerCase());
		List<String> trie= list;
	//	DAWG trie=tries.get(prefix.toLowerCase());
		if(trie==null)
			return TestResult.NOT_EXISTS;
		else{
			if(trie.contains(word))
			{
				System.out.println(word);
				if(triedWords.contains(word)){
					return TestResult.ALREADY_TRIED;
				}
				
				triedWords.add(word);
				score += getwordscore(word);
				return TestResult.HIT;
			}
			return TestResult.NOT_EXISTS;
			
		}

	}
	
	/*
	 * get user's score
	 * return score
	 */
	public int GetScore(){
		return score;
	}
	
	
}
