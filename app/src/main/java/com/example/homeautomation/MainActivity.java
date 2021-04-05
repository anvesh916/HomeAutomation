package com.example.homeautomation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner spinner;
    private int spinnerPosition;
    private HashMap<String, Integer> gestures;
    private Intent expertPreview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gestures_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        gestures = new HashMap<String, Integer>();

        expertPreview = new Intent(this, ExpertPreview.class);
        this.updateData();
    }


    public void updateData() {
        GestureModel model = new GestureModel();
        File extdir = getExternalFilesDir(Environment.getStorageDirectory().getAbsolutePath());
        File mydir = new File(extdir, "");
        Integer count = 0;
        for (File f : mydir.listFiles()) {
            if (f.isFile()) {
                String recName = f.getName();
                if (recName.contains("PRACTICE")) {
                    count = count + 1;
                }
            }
        }
        TextView recordingText = findViewById(R.id.textView2);
        recordingText.setText( "Available Recordings: "+ count+"/51");
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spinnerPosition = position;
        GestureModel model = new GestureModel();
        String fileName =  model.getVideoMap(spinnerPosition);
//        Toast.makeText(this, fileName + "", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void navToExpert(View view) {
        expertPreview.putExtra("gesture", spinnerPosition);
        startActivity(expertPreview);
    }

}