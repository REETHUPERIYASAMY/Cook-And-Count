package com.example.cookandcount;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private EditText targetInput;
    private TextView countDisplay;
    private Button startBtn, stopBtn;

    private int targetCount = 0;
    private int currentCount = 0;

    private MediaRecorder recorder;
    private Handler handler = new Handler();
    private boolean isDetecting = false;

    private final int THRESHOLD = 2000; // Basic sound threshold

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        targetInput = findViewById(R.id.targetInput);
        countDisplay = findViewById(R.id.countDisplay);
        startBtn = findViewById(R.id.startBtn);
        stopBtn = findViewById(R.id.stopBtn);

        startBtn.setOnClickListener(v -> startDetection());
        stopBtn.setOnClickListener(v -> stopDetection());

        checkAudioPermission();
    }

    private void checkAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 101);
        }
    }

    private void startDetection() {
        try {
            targetCount = Integer.parseInt(targetInput.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
            return;
        }

        currentCount = 0;
        isDetecting = true;
        setupRecorder();
        handler.post(updateTask);
    }

    private void stopDetection() {
        isDetecting = false;
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
        handler.removeCallbacks(updateTask);
    }

    private void setupRecorder() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile("/dev/null");
        try {
            recorder.prepare();
            recorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Runnable updateTask = new Runnable() {
        @Override
        public void run() {
            if (isDetecting && recorder != null) {
                int amplitude = recorder.getMaxAmplitude();
                if (amplitude > THRESHOLD) {
                    currentCount++;
                    countDisplay.setText("Count: " + currentCount);
                    if (currentCount >= targetCount) {
                        Toast.makeText(MainActivity.this, "Cooking complete!", Toast.LENGTH_LONG).show();
                        stopDetection();
                        return;
                    }
                }
                handler.postDelayed(this, 1500); // Check every 1.5 seconds
            }
        }
    };
}

