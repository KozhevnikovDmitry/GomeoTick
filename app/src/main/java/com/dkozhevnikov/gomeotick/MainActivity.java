package com.dkozhevnikov.gomeotick;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView statusBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        statusBar = findViewById(R.id.currentTime);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onLeftButtonClick(View view) {
        Intent intent = new Intent(this, TicksService.class);
        startService(intent);
    }

    public void onRightButtonClick(View view) {
        Button button = findViewById(R.id.rightBtn);
        button.setText(R.string.new_label);
    }


}
