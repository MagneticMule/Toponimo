package org.toponimo.client.ui;

import java.io.IOException;

import org.toponimo.client.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.media.MediaRecorder;
import android.media.MediaPlayer;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AddAudioActivity extends Activity {
    private static final String TAG               = org.toponimo.client.ui.AddAudioActivity.class.getName();
    private static String       mFileName         = null;

    private MediaRecorder       mRecorder         = null;

    private MediaPlayer         mPlayer           = null;

    private View                mButtonView;

    private Drawable            defaultDrawable   = null;
    private Drawable            recordingDrawable = null;
    private Drawable            recordedDrawable  = null;

    private ImageView           mRecordButton     = null;
    private TextView            mStatusTextView;

    Boolean                     isRecording;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_audio);
        defaultDrawable = getResources().getDrawable(R.drawable.mic_default);
        recordingDrawable = getResources().getDrawable(R.drawable.mic_recording);
        recordedDrawable = getResources().getDrawable(R.drawable.mic_recorded);
        mRecordButton = (ImageView) findViewById(R.id.micView);
        mStatusTextView = (TextView) findViewById(R.id.recordStatusTextView);

        isRecording = false;

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";
    }

    public void onRecord(View view) {
        Log.d(TAG, "onRecord Hit");
        if (!isRecording) {
            Log.d(TAG, "not isRecording");
            startRecording();
            mRecordButton.setImageDrawable(recordingDrawable);
            mStatusTextView.setText("Recording");

        } else {
            Log.d(TAG, "isRecording");
            stopRecording();
            mRecordButton.setImageDrawable(recordedDrawable);
            mStatusTextView.setText("Audio Recorded");
        }
        isRecording = !isRecording;

    }
    
    public void onPlay(View view) {
        
    }
    
    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(TAG, "MediaPlayer preperation failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error preparing MediaRecorder");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

}
