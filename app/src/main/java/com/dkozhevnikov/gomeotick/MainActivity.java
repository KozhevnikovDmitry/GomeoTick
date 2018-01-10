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
    private TextView statusBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
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

    public void onLeftButtonClick(View view) {
        Button button = findViewById(R.id.leftBtn);
        button.setText(R.string.new_label);
    }

    public void onRightButtonClick(View view) {
        Button button = findViewById(R.id.rightBtn);
        button.setText(R.string.new_label);
    }

}
