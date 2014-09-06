package com.example.bogglegame;

import android.os.CountDownTimer;

public class Clock extends CountDownTimer{
	private Boggle boggle;
	
	/*
	 * set update target
	 */
	public void setView(Boggle boggle){
		this.boggle=boggle;
	}
	

	/*
	 * constructor
	 */
	public Clock(long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);
	}

	@Override
	/*
	 * time up
	 */
	public void onFinish() {
		boggle.updateTimer("Time up,bang!");
		
		boggle.GameOver();
	}
	

	@Override
	/*
	 * every second update the timer on screen and update the remaining time
	 */
	public void onTick(long millisUntilFinished) {
		boggle.updateTimer("Seconds remaining: " + millisUntilFinished / 330);
		boggle.updateRemainingTime(millisUntilFinished);
	}
	

}
