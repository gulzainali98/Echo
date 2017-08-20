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
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.app.NotificationManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.graphics.drawable.Icon;

import android.media.RingtoneManager;
import android.media.Image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

import static android.widget.Toast.makeText;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener,RecognitionListener {

    private int count=0;
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
    final Handler layoutHandler = new Handler();


    public Queue<File> fileQueue;



    private MediaPlayer mPlayer = null;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE
    ,Manifest.permission.READ_EXTERNAL_STORAGE};



    int times = 0;
    /* Named searches allow to quickly reconfigure the decoder */
    private static final String KWS_SEARCH = "wakeup";
    private static final String PHONE_SEARCH = "phones";
    private static final String MENU_SEARCH = "menu";

    /* Keyword we are looking for to activate menu */
    private static final String KEYPHRASE = "start";

    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    Assets assets;
    private SpeechRecognizer recognizer;
    private HashMap<String, Integer> captions;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
            case PERMISSIONS_REQUEST_RECORD_AUDIO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    runRecognizerSetup();
                } else {
                    finish();
                }
                break;

        }
        if (!permissionToRecordAccepted ) finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 0);
        captions = new HashMap<String, Integer>();
        captions.put(KWS_SEARCH, R.string.kws_caption);
        captions.put(MENU_SEARCH, R.string.menu_caption);
        captions.put(PHONE_SEARCH, R.string.phone_caption);

        setContentView(R.layout.activity_home);



        // Check if user has given permission to record audio
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }
        runRecognizerSetup();

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath().toString();

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

                if(count>0){
                    mFileName = Environment.getExternalStorageDirectory().getAbsolutePath().toString();

                }
                FirebaseDatabase.getInstance().getReference().child("Crime").addValueEventListener(mCrimeListener);

                mFileName += "/audiorecordtest"+String.valueOf(counter)+".3gp";
                startRecording();
                start_listening();
                counter=counter+1;
                mStartButton.setText("Stop");
                handler.postDelayed(r,4000);
                count=count+1;
            }
            else{
                FirebaseDatabase.getInstance().getReference().child("Crime").removeEventListener(mCrimeListener);
                mButtonLayout.setBackgroundColor(Color.parseColor("#0066cc"));
                stop_listening();
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
            stopRecording();
            postString="/audiorecordtest"+String.valueOf(counter)+".3gp";
            readAndSendFile();
            counter= counter+1;
            mFileName=null;

            mFileName =Environment.getExternalStorageDirectory().getAbsolutePath().toString()+ "/audiorecordtest"+String.valueOf(counter)+".3gp";
            startRecording();
            handler.postDelayed(this,4000);

        }
    };
    public void readAndSendFile(){

         File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath().toString(), postString);
         addQueue(file);
        //run asynchronous job to end the queue
    }
    private void addQueue(File file){

        fileQueue.add(file);

    }

    public void start_listening(){
        if(recognizer!=null) {
            recognizer.stop();
            switchSearch(KWS_SEARCH);
            //switchSearch(FORECAST_SEARCH);
            // switchSearch(MENU_SEARCH);
        }

    }
    public void stop_listening( ){

        if(recognizer!=null) {
            recognizer.stop();
        }
    }
    public void check_phones(View view){

        if(recognizer!=null) {
            switchSearch(PHONE_SEARCH);

            // recognizer.stop();
            // recognizer.startListening(PHONE_SEARCH);
        }
    }
    private void runRecognizerSetup() {
        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task
        System.out.print( "IN RUNRECOGNIZER");

        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    assets = new Assets(HomeActivity.this);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {

                } else {
                    switchSearch(KWS_SEARCH);
                }
            }
        }.execute();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }
    /**
     * In partial result we get quick updates about current hypothesis. In
     * keyword spotting mode we can react here, in other modes we need to wait
     * for final result in onResult.
     */
    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        //System.out.print( "IN partial\n");
        String old_text="";


        if (hypothesis == null)
            return;

        String text = hypothesis.getHypstr();

        if (recognizer!=null) {

            Log.e("no hello",text);

            if (recognizer.getSearchName().equals(KWS_SEARCH)) {




                System.out.print(recognizer.getSearchName() + "\n \n");

                String arr[] = text.split(" ", 2);
                Log.e("hello",arr[0]);

                generate_notificaton(arr[0]);

                System.out.print(text + "\n");

                recognizer.stop();
                recognizer.startListening(KWS_SEARCH);







            } else {

            }


        }

    }
    /**
     * This callback is called when we stop the recognizer.
     */
    @Override
    public void onResult(Hypothesis hypothesis) {

        if (hypothesis != null) {
            String text = hypothesis.getHypstr();

            //Result on popup
            makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();


        }
    }
    @Override
    public void onBeginningOfSpeech() {
        System.out.print( "IN beginer\n");

    }
    /**
     * We stop recognizer here to get a final result
     */
    @Override
    public void onEndOfSpeech() {
        // if (!recognizer.getSearchName().equals(KWS_SEARCH))
        // switchSearch(KWS_SEARCH);
        //  recognizer.stop();
    }
    private void switchSearch(String searchName) {
        recognizer.stop();

        // If we are not spotting, start listening with timeout (10000 ms or 10 seconds).
        if (searchName.equals(KWS_SEARCH)) {
            recognizer.startListening(searchName);
        } else {
            recognizer.startListening(searchName );
        }

        String caption = getResources().getString(captions.get(searchName));
        //caption in textbox

    }
    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them
        System.out.print("SETUPRECOGNIZER");
        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))

                .setRawLogDir(assetsDir) // To disable logging of raw audio comment out this call (takes a lot of space on the device)

                .getRecognizer();
        recognizer.addListener(this);

        /** In your application you might not need to add all those searches.
         * They are added here for demonstration. You can leave just one.
         */


        // Create keyword-activation search.
        File file = new File(assetsDir,"keyphrase.gram");

        //  recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);
        recognizer.addKeywordSearch(KWS_SEARCH,file);


        // Phonetic search
        File phoneticModel = new File(assetsDir, "en-phone.dmp");
        recognizer.addAllphoneSearch(PHONE_SEARCH, phoneticModel);
        recognizer.stop();


    }
    @Override
    public void onError(Exception error) {

    }
    @Override
    public void onTimeout() {
        //switchSearch(KWS_SEARCH);
        recognizer.stop();
    }
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    public void onStart() {
        System.out.print( "IN START\n");

        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.

    }
    @Override
    public void onStop() {
        super.onStop();
        System.out.print( "ON STOP\n");


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

    }


    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    public void generate_notificaton(String name){


        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, HomeActivity.class), 0);
        Resources r = getResources();
        Notification notification = new NotificationCompat.Builder(this)
                .setTicker("sdfsdfdsf")
                .setSmallIcon(android.R.drawable.arrow_down_float)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle("Earshot")
                .setContentText("Someone is calling you!")
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setSound(alarmSound)

                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);

    }


    ValueEventListener mCrimeListener=new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                Long crime= (Long) dataSnapshot.getValue();
                if(crime==1){
                    layoutHandler.removeCallbacks(runnable);
                    mButtonLayout.setBackgroundColor(Color.parseColor("#ea253c"));
                    layoutHandler.postDelayed(runnable,2000);
                }

            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mButtonLayout.setBackgroundColor(Color.parseColor("#0066cc"));
            FirebaseDatabase.getInstance().getReference().child("Crime").setValue(0);
        }
    };





}
