package com.example.parkingslotfinder;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public final class Ui {
    public static final int NAVY = Color.rgb(7, 26, 50);
    public static final int LIGHT = Color.rgb(245, 247, 250);

    private Ui() {}

    public static TextView text(Context c, String value, float sp) {
        TextView t = new TextView(c);
        t.setText(value);
        t.setTextSize(sp);
        t.setTextColor(NAVY);
        t.setPadding(20, 14, 20, 14);
        return t;
    }

    public static Button button(Context c, String value) {
        Button b = new Button(c);
        b.setText(value);
        return b;
    }

    public static void toast(Context c, String value) {
        Toast.makeText(c, value, Toast.LENGTH_LONG).show();
    }
}
