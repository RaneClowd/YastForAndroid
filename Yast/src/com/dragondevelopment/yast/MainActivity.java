package com.dragondevelopment.yast;

import com.yast.android.yastlib.Yast;
import com.yast.android.yastlib.exceptions.YastLibApiException;
import com.yast.android.yastlib.exceptions.YastLibBadResponseException;

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
				
				Yast yastProvider = Yast.get();
				try {
					yastProvider.login(username, password);
					resultsTextview.setText(yastProvider.hashCode());
				} catch (YastLibBadResponseException e) {
					resultsTextview.setText("error: bad response");
				} catch (YastLibApiException e) {
					resultsTextview.setText("error: status of response " + e.getStatus());
				}
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
