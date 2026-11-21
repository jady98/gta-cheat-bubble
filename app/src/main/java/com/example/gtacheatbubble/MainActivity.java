package com.example.gtacheatbubble;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(dp(22), dp(20), dp(22), dp(24));
        root.setBackgroundColor(Color.rgb(15, 16, 24));

        ImageView cover = new ImageView(this);
        cover.setImageResource(R.drawable.ic_launcher);
        cover.setScaleType(ImageView.ScaleType.FIT_CENTER);
        root.addView(cover, new LinearLayout.LayoutParams(-1, dp(190)));

        TextView title = new TextView(this);
        title.setText("JADYKEYS");
        title.setTextColor(Color.WHITE);
        title.setTextSize(30);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("TU TECLADO. TUS TRUCOS. TU PARTIDA.");
        subtitle.setTextColor(Color.rgb(184, 145, 226));
        subtitle.setTextSize(11);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, dp(8), 0, dp(24));
        root.addView(subtitle);

        TextView description = new TextView(this);
        description.setText("Tu teclado gamer con accesos rápidos a los códigos de GTA San Andreas.");
        description.setTextColor(Color.rgb(210, 212, 222));
        description.setTextSize(15);
        description.setGravity(Gravity.CENTER);
        description.setPadding(dp(8), dp(10), dp(8), dp(24));
        root.addView(description);

        Button settings = makeButton("1. ACTIVAR TECLADO JADYKEYS");
        root.addView(settings, fullWidth());
        settings.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)));

        Button picker = makeButton("2. CAMBIAR ENTRE TECLADOS");
        root.addView(picker, fullWidth());
        picker.setOnClickListener(v -> ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).showInputMethodPicker());

        Button overlay = makeButton("PERMITIR BURBUJA FLOTANTE");
        root.addView(overlay, fullWidth());
        overlay.setOnClickListener(v -> {
            if (!Settings.canDrawOverlays(this)) {
                startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    android.net.Uri.parse("package:" + getPackageName())));
            } else {
                startService(new Intent(this, BubbleService.class));
            }
        });

        TextView note = new TextView(this);
        note.setText("\nAviso: los accesos rápidos escriben en campos que aceptan teclado. GTA San Andreas puede no aceptar códigos enviados por un teclado Android.");
        note.setTextColor(Color.rgb(150, 153, 166));
        note.setTextSize(12);
        note.setGravity(Gravity.CENTER);
        note.setPadding(dp(4), dp(18), dp(4), 0);
        root.addView(note);
        setContentView(root);
    }

    private LinearLayout.LayoutParams fullWidth() {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, dp(52));
        p.setMargins(0, dp(6), 0, dp(6));
        return p;
    }

    private Button makeButton(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextColor(Color.WHITE);
        b.setTextSize(13);
        b.setTypeface(null, Typeface.BOLD);
        b.setAllCaps(false);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.rgb(49, 38, 69));
        bg.setCornerRadius(dp(12));
        bg.setStroke(dp(1), Color.rgb(132, 84, 190));
        b.setBackground(bg);
        return b;
    }
}
