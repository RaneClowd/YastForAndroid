package com.dragondevelopment.yast;

import com.yast.android.*;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        final TextView resultsTextview = (TextView)findViewById(R.id.resultsTextview);

        final EditText userTextbox = (EditText)findViewById(R.id.usernameTextbox);
        final EditText passwordTextbox = (EditText)findViewById(R.id.passwordTextbox);
        
        Button loginButton = (Button)findViewById(R.id.logInButton);
        loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String username = userTextbox.getText().toString();
				String password = passwordTextbox.getText().toString();
				
				resultsTextview.setText("signing in...");
				
				String results = "<company><division>one</division><division>two</division></company>";
				XMLParser.parseXML(results);
				
				/*YastServiceProvider.getInstance().logIn(username, password, new Callback() {
					
					@Override
					public void execute(Object data) {
						resultsTextview.setText((String)data);
					}
				});*/
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
