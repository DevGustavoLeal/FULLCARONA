package com.example.vocee;

import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        Window window = getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, false);
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(window, window.getDecorView());
        controller.hide(WindowInsetsCompat.Type.systemBars());
        controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        setContentView(R.layout.activity_main);

        Button driverButton = findViewById(R.id.driverButton);
        Button passengerButton = findViewById(R.id.passengerButton);
        driverButton.setOnClickListener(view -> Toast.makeText(this, R.string.driver_selected, Toast.LENGTH_SHORT).show());
        passengerButton.setOnClickListener(view -> Toast.makeText(this, R.string.passenger_selected, Toast.LENGTH_SHORT).show());
    }
}
