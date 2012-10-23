package com.dragondevelopment.yast;

import java.util.ArrayList;
import java.util.Date;

import com.yast.android.yastlib.Callback;
import com.yast.android.yastlib.Yast;
import com.yast.android.yastlib.YastProject;
import com.yast.android.yastlib.YastRecord;
import com.yast.android.yastlib.YastRecordWork;
import com.yast.android.yastlib.YastResponse;
import com.yast.android.yastlib.YastResponseProjects;
import com.yast.android.yastlib.YastResponseRecords;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ProjectViewActivity extends Activity {
	
	private EditText commentText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);
        
        this.commentText = (EditText)findViewById(R.id.commentText);

        loadProjectsIntoView(null);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.activity_projects, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		loadProjectsIntoView(null);
		return true;
	}

	private void loadProjectsIntoView(String error)
	{
		final RelativeLayout projectsView = (RelativeLayout)findViewById(R.id.projectsView);
        final LinearLayout progressView = (LinearLayout)findViewById(R.id.progressView);
        
        this.commentText.setText("");
        
		progressView.setVisibility(View.VISIBLE);
        
		projectsView.removeAllViews();
		
		if (error != null) {
			TextView errorLabel = new TextView(ProjectViewActivity.this);
			errorLabel.setText(error);
			projectsView.addView(errorLabel);
        }
        
        final Yast yastProvider = Yast.get();
        
        yastProvider.getRecords(0, new Callback() {
			@Override
			public void execute(String error, YastResponse response) {
				YastResponseRecords recordsResp = (YastResponseRecords)response;
				
				final ArrayList<Integer> activeProjects = new ArrayList<Integer>();
				final SparseArray<YastRecordWork> activeRecords = new SparseArray<YastRecordWork>();
				for (YastRecord record : recordsResp.getRecords()) {
					YastRecordWork workRecord = (YastRecordWork)record;
					if (workRecord.isRunning()) {
						activeProjects.add(workRecord.getProject());
						activeRecords.append(workRecord.getProject(), workRecord);
					}
				}
				
				yastProvider.getProjects(new Callback() {
					@Override
					public void execute(String error, YastResponse response) {
						YastResponseProjects projectsResponse = (YastResponseProjects)response;
						progressView.setVisibility(View.GONE);
						if (error != null) {
							TextView errorLabel = new TextView(ProjectViewActivity.this);
							errorLabel.setText(error);
							projectsView.addView(errorLabel);
				        } else {

							int idIndex = 20;
							
							for (final YastProject project : projectsResponse.getProjects()) {
					        	final Button projectButton = new Button(ProjectViewActivity.this);
					        	projectButton.setId(idIndex);
					        	
					        	projectButton.setText(project.getName());
					        	
					        	if (activeProjects.contains(project.getId())) {
					        		setButtonUpToStopProject(projectButton, activeRecords.get(project.getId()), project, yastProvider);
					        	} else {
					        		setButtonUpToStartProject(projectButton, project, yastProvider);
					        	}
					        	
					        	if (idIndex > 20) {
					        		RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
					        		p.addRule(RelativeLayout.BELOW, idIndex-1);
									projectsView.addView(projectButton, p);
					        	} else {
									projectsView.addView(projectButton);
					        	}
					        	idIndex++;
				        	}
				        }
					}
				});
			}
		});
        
        
	}
	
	void setButtonUpToStartProject(Button projectButton, final YastProject project, final Yast yastProvider)
	{
		Drawable img = getResources().getDrawable(R.drawable.play);
		img.setBounds(new Rect(0, 0, 60, 60));
		projectButton.setCompoundDrawables(img, null, null, null);
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
			        	loadProjectsIntoView(error);
					}
				});
			}
		});
	}
	
	void setButtonUpToStopProject(Button projectButton, final YastRecordWork workRecord, final YastProject project, final Yast yastProvider)
	{
		Drawable img = getResources().getDrawable(R.drawable.stop);
		img.setBounds(new Rect(0, 0, 60, 60));
		projectButton.setCompoundDrawables(img, null, null, null);
		projectButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				workRecord.setEndTime(new Date());
				workRecord.setRunning(false);
				workRecord.setComment(ProjectViewActivity.this.commentText.getText().toString());
				yastProvider.change(workRecord, new Callback() {
					@Override
					public void execute(String error, YastResponse response) {
			        	loadProjectsIntoView(error);
					}
				});
			}
		});
	}

}
