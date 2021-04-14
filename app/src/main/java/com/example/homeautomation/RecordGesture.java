package com.example.homeautomation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class RecordGesture extends AppCompatActivity {
    private Intent mainActivity;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 1996;
    public static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 112;
    private static final int VIDEO_CAPTURE = 1;
    private int gestureNumber;
    public int practiceNumber = 0;
    public String fileName = "";
    private Uri uriForFile;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_gesture);
        gestureNumber = this.getIntent().getIntExtra("gesture",0);
        mainActivity = new Intent(this, MainActivity.class);

      this.updateData();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");

    }

    public void updateData() {
        GestureModel model = new GestureModel();
        File extdir = getExternalFilesDir(Environment.getStorageDirectory().getAbsolutePath());
        File mydir = new File(extdir, "");
        Integer count = 0;
        for (File f : mydir.listFiles()) {
            if (f.isFile()) {
                String recName = f.getName();
                if (recName.contains(model.getGestures(gestureNumber))) {
                    count = count + 1;
                }

            }
        }
        TextView recordingText = findViewById(R.id.textView);
        recordingText.setText( "Available Recordings: "+ count+"/3");
        TextView gestureName = findViewById(R.id.gestureName);
        String[] gestureArray = getResources().getStringArray(R.array.gestures_array);
        gestureName.setText( gestureArray[gestureNumber]);
    }


    private void preInvokeCamera() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            this.startRecording();
        } else {
            String[] permissionReq = {Manifest.permission.CAMERA};
            requestPermissions(permissionReq, CAMERA_PERMISSION_REQUEST_CODE);

            String[] permissionReq2 = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permissionReq2, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.startRecording();
            } else {
                Toast.makeText(this, "Camera permission required.", Toast.LENGTH_LONG).show();
                this.finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void startRecording() {
        File videoPath = getExternalFilesDir(Environment.getStorageDirectory().getAbsolutePath());
        File mediaFile = new File(videoPath, getFileName(practiceNumber));
        uriForFile = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", mediaFile);

        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5);
        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
        takeVideoIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, VIDEO_CAPTURE);
        }
    }
    private void showPreview() {
        // Set video in the media player
        MediaController m = new MediaController(this);
        VideoView videoView = findViewById(R.id.videoView);
        videoView.setMediaController(m);
        videoView.setVideoURI(uriForFile);
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
    }
    public void navToMain() {
        startActivity(mainActivity);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Toast.makeText(this, getFileName(practiceNumber) + " Saved Successfully !!", Toast.LENGTH_LONG).show();
            practiceNumber = (practiceNumber + 1) % 3;
            this.showPreview();
            this.updateData();
        } else {
            Toast.makeText(this, "Recording Cancelled", Toast.LENGTH_LONG).show();
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    public String getFileName(int num) {
        GestureModel model = new GestureModel();
        return model.getGestures(gestureNumber) + "_PRACTICE_"+ (num + 1) +"_VOONA.mp4";
    }

    public void upload(View view) {
            progressDialog.show();
            progressDialog.setMessage("Uploading....");
            this.uploadMultipleFiles();
    }

    private void uploadMultipleFiles() {
        File videoPath = getExternalFilesDir(Environment.getStorageDirectory().getAbsolutePath());
        File file1 = new File(videoPath, getFileName(0));
        File file2 = new File(videoPath, getFileName(1));
        File file3 = new File(videoPath, getFileName(2));

        MultipartBody.Part fileToUpload1 = MultipartBody.Part.createFormData(file1.getName(),
                file1.getName(), RequestBody.create(MediaType.parse("*/*"), file1));
        MultipartBody.Part fileToUpload2 = MultipartBody.Part.createFormData(file2.getName(),
                file2.getName(), RequestBody.create(MediaType.parse("*/*"), file2));
        MultipartBody.Part fileToUpload3 = MultipartBody.Part.createFormData(file3.getName(),
                file3.getName(), RequestBody.create(MediaType.parse("*/*"), file3));
        ApiConfig getResponse = AppConfig.getRetrofit().create(ApiConfig.class);
        Call<ServerResponse> call = getResponse.uploadMulFile(fileToUpload1, fileToUpload2, fileToUpload3);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    if (serverResponse.getSuccess()) {
                        Toast.makeText(getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    assert serverResponse != null;
                    Log.v("Response", serverResponse.toString());
                }
                navToMain();
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                progressDialog.dismiss();
                navToMain();
            }
        });
    }

    public void uploadVideo(String name) {
        File videoPath = getExternalFilesDir(Environment.getStorageDirectory().getAbsolutePath());
        File file = new File(videoPath, name);


        // Parsing any Media type file
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData(file.getName(), file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("video/mp4"), file.getName());

        ApiConfig getResponse = AppConfig.getRetrofit().create(ApiConfig.class);
        Call<ServerResponse> call = getResponse.uploadFile(fileToUpload, filename);
        call.enqueue(new Callback<ServerResponse>() {

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Upload Failed", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }


            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    if (serverResponse.getSuccess()) {
                        Toast.makeText(getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    assert serverResponse != null;
                    Log.v("Response", serverResponse.toString());
                }
                progressDialog.dismiss();
            }

        });
    }

    public void recordVideo(View view)
    {
        this.preInvokeCamera();
    }

}