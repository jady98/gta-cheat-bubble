package com.example.gtacheatbubble;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 30, 24, 24);

        TextView title = new TextView(this);
        title.setText("GTA HACKER KEYBOARD\n\nTeclado QWERTY con botones de trucos. Actívalo en Android y selecciónalo cuando GTA permita abrir el teclado.");
        title.setTextSize(18);
        root.addView(title);

        Button settings = new Button(this);
        settings.setText("1. Activar GTA Hacker Keyboard");
        root.addView(settings);
        settings.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)));

        Button picker = new Button(this);
        picker.setText("2. Elegir teclado GTA");
        root.addView(picker);
        picker.setOnClickListener(v -> ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).showInputMethodPicker());

        Button overlay = new Button(this);
        overlay.setText("Permitir burbuja flotante");
        root.addView(overlay);
        overlay.setOnClickListener(v -> {
            if (!Settings.canDrawOverlays(this)) {
                startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    android.net.Uri.parse("package:" + getPackageName())));
            } else {
                startService(new Intent(this, BubbleService.class));
            }
        });

        TextView note = new TextView(this);
        note.setText("\nImportante: el teclado envía pulsaciones reales al campo o juego que tenga el foco. Si GTA 2.11.311 no abre/acepta entrada de teclado, ninguna app de teclado puede forzarlo sin modificar el juego o usar root.");
        note.setTextSize(13);
        root.addView(note);
        setContentView(root);
    }
}