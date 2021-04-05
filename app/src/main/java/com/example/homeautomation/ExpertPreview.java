package com.example.homeautomation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;

public class ExpertPreview extends AppCompatActivity {
    private int gestureNumber;
    public static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 112;
    public static final String DIR = "/DCIM/ExpertGesture/";
    private Intent recordGesture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_preview);
        gestureNumber = this.getIntent().getIntExtra("gesture",0);
        recordGesture = new Intent(this, RecordGesture.class);
        this.requestPermission();
    }


    private void requestPermission() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            this.showPreview();
        } else {
            String[] permissionReq = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permissionReq, READ_EXTERNAL_STORAGE_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        // Ensure that this result is for the external storage permission request
        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            // Check if the request was granted or denied
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.showPreview();
            } else {
                // The request was denied -> tell the user and exit the application
                Toast.makeText(this, "External Storage permission required.", Toast.LENGTH_LONG).show();
                this.finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showPreview() {
        GestureModel model = new GestureModel();
        String fileName =  model.getVideoMap(gestureNumber);
//        Toast.makeText(this, fileName, Toast.LENGTH_LONG).show();
        Uri videoFileUri = Uri.parse(Environment.getExternalStorageDirectory()+ DIR + fileName +".mp4");
        // Set video in the media player
        MediaController m = new MediaController(this);
        VideoView videoView = findViewById(R.id.video_preview);
        videoView.setMediaController(m);
        videoView.setVideoURI(videoFileUri);
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
    }
    public void navToRecord(View view) {
        recordGesture.putExtra("gesture", gestureNumber);
        startActivity(recordGesture);
    }

    @Override
    protected void onDestroy() {
        VideoView videoView = findViewById(R.id.video_preview);
        videoView.stopPlayback();
        super.onDestroy();
    }
}