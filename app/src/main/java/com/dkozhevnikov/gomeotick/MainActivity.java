package com.dkozhevnikov.gomeotick;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.dmitr.myapplication.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    private Date startDate;
    private Button button;
    private TextView statusBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        button = findViewById(R.id.testBtn);
        statusBar = findViewById(R.id.status);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startDate = new Date();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Calendar calendar = new GregorianCalendar();
        statusBar.setText(new Date().toString());
    }

    public void onClick(View view) {
        button.setText(R.string.new_label);
    }

}
