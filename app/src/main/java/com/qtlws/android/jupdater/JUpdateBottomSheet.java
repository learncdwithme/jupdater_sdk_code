
/*
 * Copyright (c) 2026 JUpdater
 *
 * Licensed under the MIT License.
 *
 */

package com.qtlws.android.jupdater;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;

class JUpdateBottomSheet extends BottomSheetDialog {

    public JUpdateBottomSheet(@NonNull Context context, String apkUrl,String t,String m) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.jupdate_bottom_sheet, null, false);
        setContentView(view);

        Button updateBtn = view.findViewById(R.id.btnUpdate);
        Button laterBtn = view.findViewById(R.id.btnLater);
        TextView titleTV = view.findViewById(R.id.titleText);
        TextView messageTV = view.findViewById(R.id.descText);

        titleTV.setText(t);
        messageTV.setText(m);

        updateBtn.setOnClickListener(v -> {
            UrlLauncher.open(context, apkUrl);
            dismiss();;
        });
        laterBtn.setVisibility(View.VISIBLE);
        laterBtn.setOnClickListener(v -> dismiss());
    }

    public static void show(Context context, String apkUrl,String t,String m) {
        new JUpdateBottomSheet(context, apkUrl,t,m).show();
    }
}
