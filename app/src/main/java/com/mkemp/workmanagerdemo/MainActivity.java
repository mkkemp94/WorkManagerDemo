package com.mkemp.workmanagerdemo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

public class MainActivity extends AppCompatActivity
{
    public static final String KEY_COUNT_VALUE = "count_value";
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Data data = new Data.Builder()
                .putInt(KEY_COUNT_VALUE, 1750)
                .build();
        
        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(false)
                .build();
        
        final OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(DemoWorker.class)
                .setInputData(data)
                .setConstraints(constraints)
                .build();
        
        final TextView textView = findViewById(R.id.tv_status);
        
        (findViewById(R.id.button)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                WorkManager.getInstance(MainActivity.this).enqueue(oneTimeWorkRequest);
            }
        });
        
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(oneTimeWorkRequest.getId())
                .observe(this, new Observer<WorkInfo>()
                {
                    @Override
                    public void onChanged(WorkInfo workInfo)
                    {
                        textView.setText(workInfo.getState().name());
                        
                        if ( workInfo.getState().isFinished() )
                        {
                            Data gotData = workInfo.getOutputData();
                            String message = gotData.getString(DemoWorker.KEY_WORKER);
                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}