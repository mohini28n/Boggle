package com.example.bogglegame;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
    TextView name;
    Button SwitchBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	private Activity mCurrentActivity = null;
    public Activity getCurrentActivity(){
          return mCurrentActivity;
    }
    public void setCurrentActivity(Activity mCurrentActivity){
          this.mCurrentActivity = mCurrentActivity;
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		SwitchBtn = (Button)findViewById(R.id.single_player_button);
        name = (TextView)findViewById(R.id.nameText);
        SwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextScreen = new Intent(getApplicationContext(),  Boggle.class);
                nextScreen.putExtra("extra",name.getText());
                startActivity(nextScreen);
            }
        });
		return true;
	}

}
