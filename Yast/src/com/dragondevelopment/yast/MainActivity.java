package com.dragondevelopment.yast;

import com.yast.android.yastlib.Callback;
import com.yast.android.yastlib.Yast;
import com.yast.android.yastlib.YastResponse;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        final LinearLayout progressWindow = (LinearLayout)findViewById(R.id.progressView);
        final TextView resultsTextview = (TextView)findViewById(R.id.resultsTextview);
        final EditText userTextbox = (EditText)findViewById(R.id.usernameTextbox);
        final EditText passwordTextbox = (EditText)findViewById(R.id.passwordTextbox);
        
        Button loginButton = (Button)findViewById(R.id.logInButton);
        loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String username = userTextbox.getText().toString();
				String password = passwordTextbox.getText().toString();
				
				progressWindow.setVisibility(View.VISIBLE);
				
				final Yast yastProvider = Yast.get();
				yastProvider.login(username, password, new Callback() {
					@Override
					public void execute(String error, YastResponse response) {
						progressWindow.setVisibility(View.GONE);
						if (error != null) {
							resultsTextview.setText(error);
						} else {
							Intent intent = new Intent(MainActivity.this, ProjectViewActivity.class);
							startActivity(intent);
						}
					}
				});
			}
		});
    }
}
