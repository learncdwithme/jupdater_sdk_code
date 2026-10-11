
/*
 * Copyright (c) 2026 JUpdater
 *
 * Licensed under the MIT License.
 *
 */

package com.qtlws.android.jupdater;

import static com.qtlws.android.jupdater.Constants.EXTRA_MESSAGE;
import static com.qtlws.android.jupdater.Constants.EXTRA_TITLE;
import static com.qtlws.android.jupdater.Constants.EXTRA_UPDATE_URL;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UpdateBlocking extends AppCompatActivity {

    private static final int EXIT_INTERVAL = 2000;
    private static volatile boolean showing = false;

    private long lastBackPressedTime = 0;

    static boolean isShowing() {
        return showing;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showing = true;
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_blocking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        final String apkUrl = getIntent().getStringExtra(EXTRA_UPDATE_URL);
        final String title = getIntent().getStringExtra(EXTRA_TITLE);
        final String message = getIntent().getStringExtra(EXTRA_MESSAGE);
        Button updateBtn = findViewById(R.id.updateApp);
        Button closeAppBtn = findViewById(R.id.closeApp);
        TextView titleTV = findViewById(R.id.updateMessage);
        TextView messageTV = findViewById(R.id.updateExplanation);

        titleTV.setText(title);
        messageTV.setText(message);

        updateBtn.setOnClickListener(v -> UrlLauncher.open(this, apkUrl));

        closeAppBtn.setOnClickListener(v -> {
            finishAffinity();
            System.exit(0);
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                long now = System.currentTimeMillis();
                if (now - lastBackPressedTime < EXIT_INTERVAL) {
                    finishAffinity();
                } else {
                    Toast.makeText(UpdateBlocking.this, "Press again to exit", Toast.LENGTH_SHORT).show();
                    lastBackPressedTime = now;
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        showing = false;
        super.onDestroy();
    }
}