package com.echo.echo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {


    private RelativeLayout mButtonLayout;
    private Button mStartButton;
    private Boolean recordingStarted;
    private Integer counter=0;
    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String mFileName = null;
    private MediaRecorder mRecorder = null;
    private String postString="";
    private Handler handler;


    public Queue<File> fileQueue;



    private MediaPlayer mPlayer = null;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 0);


        setContentView(R.layout.activity_home);



        mFileName = Environment.getExternalStorageDirectory().toString();

        handler= new Handler();

        fileQueue=new LinkedList<File>();
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        mButtonLayout= (RelativeLayout)findViewById(R.id.button_layout);
        mStartButton=(Button)findViewById(R.id.start);
        mStartButton.setOnClickListener(this);
        recordingStarted=false;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        ViewGroup.LayoutParams params = mButtonLayout.getLayoutParams();
        params.height=height/2;
        params.width=width;
        mButtonLayout.setLayoutParams(params);



    }


    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }
    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    public void onClick(View v){
        if(v.getId()==R.id.start){

            if(!recordingStarted) {
                mFileName += "/audiorecordtest"+String.valueOf(counter)+".3gp";
                startRecording();
                counter=counter+1;
                mStartButton.setText("Stop");
                handler.postDelayed(r,4000);
            }
            else{
                handler.removeCallbacks(r);
                stopRecording();
                mStartButton.setText("Start");

            }
            recordingStarted=!recordingStarted;
        }
    }

    Runnable r= new Runnable() {
        @Override
        public void run() {
            Toast.makeText(HomeActivity.this,String.valueOf(counter),Toast.LENGTH_SHORT).show();
            stopRecording();
            postString="/audiorecordtest"+String.valueOf(counter)+".3gp";
            readAndSendFile();
            counter= counter+1;
            mFileName=null;

            mFileName =Environment.getExternalStorageDirectory().toString()+ "/audiorecordtest"+String.valueOf(counter)+".3gp";
            Toast.makeText(HomeActivity.this, mFileName,Toast.LENGTH_SHORT).show();
            startRecording();
            handler.postDelayed(this,4000);

        }
    };
    public void readAndSendFile(){

         File file = new File(Environment.getExternalStorageDirectory().toString(), postString);
         addQueue(file);
        //run asynchronous job to end the queue
    }
    private void addQueue(File file){

        fileQueue.add(file);

    }


}
