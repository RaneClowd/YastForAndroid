package com.dragondevelopment.yast;

import java.util.Calendar;
import java.util.Date;

import com.yast.android.yastlib.Callback;
import com.yast.android.yastlib.Yast;
import com.yast.android.yastlib.YastProject;
import com.yast.android.yastlib.YastRecordWork;
import com.yast.android.yastlib.YastResponse;
import com.yast.android.yastlib.YastResponseProjects;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class ProjectViewActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);
        
        final LinearLayout projectsView = (LinearLayout)findViewById(R.id.projectsView);
        
        final Yast yastProvider = Yast.get();
        yastProvider.getProjects(new Callback() {
			@Override
			public void execute(String error, YastResponse response) {
				YastResponseProjects projectsResponse = (YastResponseProjects)response;
				if (error != null) {
					TextView projectLabel = new TextView(ProjectViewActivity.this);
					projectLabel.setText(error);
					projectsView.addView(projectLabel, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		        } else {
		        	for (final YastProject project : projectsResponse.getProjects()) {
			        	final ImageButton projectButton = new ImageButton(ProjectViewActivity.this);
			        	projectButton.setImageDrawable(getResources().getDrawable(R.drawable.play));
			        	projectButton.setContentDescription(project.getName());
			        	projectButton.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								YastRecordWork workRecord = new YastRecordWork();
								workRecord.setProject(project);
								workRecord.setRunning(true);
								workRecord.setStartTime(new Date());
								yastProvider.add(workRecord, new Callback() {
									@Override
									public void execute(String error, YastResponse response) {
										projectButton.setImageDrawable(getResources().getDrawable(R.drawable.play));
									}
								});
							}
						});
						projectsView.addView(projectButton, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		        	}
		        }
			}
		});
	}

}
