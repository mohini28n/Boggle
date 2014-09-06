package com.example.bogglegame;

import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Boggle extends Activity implements OnClickListener {

	private static final String TAG = "Boggle";
	   public NavigableSet<String> dictionary;
	// dimensions of tiles on the board
	public static final int DICE_WIDTH = 4;
	public static final int DICE_HEIGHT = 4;
	private static final int ONGOING = 0;
	private static int STATUS;

	private static String WORD_BASE = "Word: ";
	private static String SCORE_BASE = "Score: ";

	private Tile[] tiles;
	private ArrayList<Tile> selectedTiles;
	private String selectedWords;
	private long remainingTime;
	private int tileAmount = 16;
	private int oldx;
	private int oldy;

	private TextView boggleTimerView;
	private TextView boggleScoreView;
	private TextView boggleWordView;
	private TextView displaywordsview;
	private Button quitButton, resetButton;

	private Music music;
	private BoggleGame game;
	private Clock timer;

	/*
	 * initialize the game read wordlist from resource initialize none UI
	 * parameters
	 */
	private void initialize() {
		Log.d(TAG, "Boggle game initializeing");

//	 InputStream is = getResources().openRawResource(R.raw.a11);
		Log.d(TAG, "got inputstream");
		game = BoggleGame.GetInstance();

		tiles = new Tile[tileAmount];
		selectedTiles = new ArrayList<Tile>();
		remainingTime = 60000;
		selectedWords = "";
		music = new Music(this, R.raw.audio);
	}

	
	int getGameStatus() {
		return STATUS;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.boggle_main);
		solver();
		Log.d(TAG, "onCreate");

		initialize();
		STATUS = ONGOING;
		renderTiles(game.NewGame(dictionary));
		Log.d(TAG, "possible word amount " + game.GetAnswerHistory().size());

		// set view
		setTitle(R.string.boggle_name);
		setContentView(R.layout.boggle_game);

		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();

		// teak view parameters
		int statsHeight = (int) (height * 0.1);
		Log.d(TAG, Integer.toString(statsHeight));
		boggleTimerView = (TextView) findViewById(R.id.boggle_time);

		timer = new Clock(remainingTime, 3000);
		timer.setView(this);
		timer.start();

		boggleWordView = (TextView) findViewById(R.id.boggle_word);
		boggleScoreView = (TextView) findViewById(R.id.boggle_score);
		displaywordsview = (TextView) findViewById(R.id.ListView1);
		boggleScoreView.setText(SCORE_BASE + game.GetScore());

		
		quitButton = (Button) findViewById(R.id.boggle_quit_button);
		quitButton.setOnClickListener(this);
		
		resetButton = (Button) findViewById(R.id.reset);
		resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	Intent intent = getIntent();
            	overridePendingTransition(0, 0);
            	intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            	finish();
            	overridePendingTransition(0, 0);
            	startActivity(intent);
            }
        });

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.boggle_quit_button:
			finish();
		}
	}


	

	// /////////////////////////////////////////////////////
	// /functions allowed calling from outside
	// /////////////////////////////////////////////////////
	protected void selectTile(int x, int y) {
		if (oldx != x || oldy != y) {
			Tile t = getTile(x, y);
			selectedWords += t.getLetter();
			boggleWordView.setText(WORD_BASE + selectedWords);

			if (selectedTiles.contains(t)) {
				// if meet an intersection, regard the former selected letters
				// as a whole word
				makeAGuess();
				Log.d(TAG, "REMOVING TILE: " + x + ", " + y);
			} else {
				// otherwise, add the letter to current word
				selectedTiles.add(t);
				t.setSelected(true);
				Log.d(TAG, "ADDING TILE: " + x + ", " + y);
			}
		}

		oldx = x;
		oldy = y;
	}

	/** Return the tile at the given coordinates */
	protected Tile getTile(int x, int y) {
		return tiles[y * DICE_HEIGHT + x];
	}

	/*
	 * update the timer text
	 * 
	 * @str the string to render on timer
	 */
	protected void updateTimer(String str) {
		boggleTimerView.setText(str);
		
	}

	/*
	 * update the remaining time in this game
	 * 
	 * @millisUntilFinished the value to set for remainingTime
	 */
	protected void updateRemainingTime(long millisUntilFinished) {

		remainingTime = millisUntilFinished;
	//	Log.v(TAG, "update time " + Long.toString(remainingTime));
	}

	/*
	 * time up and game over clean up the screen for new game
	 */
	protected void GameOver() {
		

		 for (String str :  game.GetWords()) 
         {
			 if(!game.triedWords.contains(str))
				displaywordsview.append(str+"\n");
             
         }
		//STATUS = NEW_GAME;
		selectedTiles.clear();
	}

	/*
	 * make a boggle guess
	 */
	protected void makeAGuess() {
		Log.d(TAG, "test with word" + selectedWords);
		BoggleGame.TestResult result = game.Test(selectedWords);
		if (BoggleGame.TestResult.HIT == result) {
			// update the score
			Log.d(TAG, "hit a word " + selectedWords);
			displaywordsview.append(selectedWords+"-"+ game.getwordscore(selectedWords)+"\n");
			boggleScoreView.setText(SCORE_BASE + game.GetScore());
		}
		if (BoggleGame.TestResult.ALREADY_TRIED == result) {

			Log.d(TAG, "already tried with " + selectedWords);
			boggleScoreView.setText(SCORE_BASE + game.GetScore());
		}
		if (BoggleGame.TestResult.NOT_EXISTS == result) {
			Log.d(TAG, "not exists " + selectedWords);
			boggleScoreView.setText(SCORE_BASE + game.GetScore());
		}
		clearSelection();
	}

	/*
	 * audio effect
	 */
	protected void playSound() {
		music.play();
	}

	// /////////////////////////////////////////////////////
	// /Helper functions
	// /////////////////////////////////////////////////////
	/*

	/*
	 * read game status from internal storage
	 */

	/*
	 * render tiles
	 * 
	 * @words the list of characters to render on tiles
	 */
	private void renderTiles(char[] words) {
		for (int i = 0; i < words.length; i++) {
			tiles[i] = new Tile(words[i], false);
		}
	}

	/*
	 * clear words on tiles
	 */
//		findViewById(R.id.boggle_row_3).postInvalidate();
	

	/*
	 * resume the words on tiles
	 * 
	 * @letters the words to be render on tiles
	 */

	public List<Tile> getSelectedTiles() {
		return selectedTiles;
	}
	public NavigableSet<String> solver()
	{
		dictionary = new TreeSet<String>();
	    try {
	    	System.out.println("hai............");
	   // 	File file = new File("file://" + "com.example.bogglegame"+ "/" +R.raw.text);
	    
	    //	fr=this.getResources().openRawResource(R.raw.text);
	    //	fr = am.open("text.txt");
	    	
	    	InputStream fr= this.getResources().openRawResource(R.raw.text);
	    	InputStreamReader ir = new InputStreamReader(fr);
	        BufferedReader br = 
	        		new BufferedReader(ir);
	        String line;
	  
	        while ((line = br.readLine()) != null) 
	        {
	            dictionary.add(line.split(":")[0]);
	        }
	        br.close();
	        return dictionary;
	}    catch (Exception e) {
        throw new RuntimeException("Error while reading dictionary");
    }
	}

	/*
	 * clear the selection
	 */
	private void clearSelection() {
		for (int i = 0; i < tileAmount; i++) {
			tiles[i].setSelected(false);
		}
		selectedTiles.clear();
		selectedWords = "";
		boggleWordView.setText(WORD_BASE);
	}
	
}
