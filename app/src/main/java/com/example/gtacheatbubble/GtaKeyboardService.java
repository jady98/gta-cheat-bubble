package com.example.gtacheatbubble;

import android.inputmethodservice.InputMethodService;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.KeyEvent;
import android.view.View;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.HorizontalScrollView;

public class GtaKeyboardService extends InputMethodService {
    private int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }

    @Override public View onCreateInputView() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(4), dp(5), dp(4), dp(5));
        root.setBackgroundColor(Color.rgb(24, 25, 34));

        HorizontalScrollView scroll = new HorizontalScrollView(this);
        LinearLayout cheats = new LinearLayout(this);
        cheats.setOrientation(LinearLayout.HORIZONTAL);
        String[][] codes = {
            {"VIDA + DINERO","HESOYAM"}, {"ARMAS 1","LXGIWYL"},
            {"ARMAS 2","PROFESSIONALSKIT"}, {"ARMAS 3","UZUMYMW"},
            {"JETPACK","ROCKETMAN"}, {"TANQUE","AIWPRTON"},
            {"QUITAR POLICÍA","ASNAEB"}, {"MUNICIÓN","FULLCLIP"}
        };
        for (String[] item : codes) {
            Button b = keyButton(item[0]);
            b.setTextSize(10);
            b.setMinWidth(dp(94));
            b.setOnClickListener(v -> typeString(item[1]));
            cheats.addView(b, new LinearLayout.LayoutParams(dp(100), dp(43)));
        }
        scroll.addView(cheats);
        root.addView(scroll);

        addRow(root, new String[]{"Q","W","E","R","T","Y","U","I","O","P"});
        addRow(root, new String[]{"A","S","D","F","G","H","J","K","L"});
        addRow(root, new String[]{"Z","X","C","V","B","N","M"});
        LinearLayout bottom = new LinearLayout(this);
        bottom.setGravity(Gravity.CENTER);
        Button delete = keyButton("⌫");
        delete.setOnClickListener(v -> sendKey(KeyEvent.KEYCODE_DEL));
        Button space = keyButton("ESPACIO");
        space.setOnClickListener(v -> sendKey(KeyEvent.KEYCODE_SPACE));
        Button hide = keyButton("OCULTAR");
        hide.setOnClickListener(v -> requestHideSelf(0));
        bottom.addView(delete, new LinearLayout.LayoutParams(0, dp(43), 1));
        bottom.addView(space, new LinearLayout.LayoutParams(0, dp(43), 3));
        bottom.addView(hide, new LinearLayout.LayoutParams(0, dp(43), 1));
        root.addView(bottom);
        return root;
    }

    private void addRow(LinearLayout root, String[] letters) {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER);
        for (String letter : letters) {
            Button b = keyButton(letter);
            b.setOnClickListener(v -> typeChar(letter.charAt(0)));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dp(43), 1);
            lp.setMargins(dp(1), dp(2), dp(1), dp(2));
            row.addView(b, lp);
        }
        root.addView(row);
    }

    private Button keyButton(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextColor(Color.WHITE);
        b.setTextSize(15);
        b.setAllCaps(false);
        b.setTypeface(null, Typeface.BOLD);
        b.setPadding(0, 0, 0, 0);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.rgb(58, 48, 83));
        bg.setCornerRadius(dp(7));
        bg.setStroke(dp(1), Color.rgb(115, 83, 170));
        b.setBackground(bg);
        return b;
    }

    private void typeString(String value) {
        for (int i=0; i<value.length(); i++) typeChar(value.charAt(i));
    }

    private void typeChar(char c) {
        int code = KeyEvent.KEYCODE_UNKNOWN;
        if (c >= 'A' && c <= 'Z') code = KeyEvent.KEYCODE_A + (c - 'A');
        else if (c == ' ') code = KeyEvent.KEYCODE_SPACE;
        if (code != KeyEvent.KEYCODE_UNKNOWN) sendKey(code);
    }

    private void sendKey(int code) {
        if (getCurrentInputConnection() == null) return;
        long now = System.currentTimeMillis();
        getCurrentInputConnection().sendKeyEvent(new KeyEvent(now, now, KeyEvent.ACTION_DOWN, code, 0));
        getCurrentInputConnection().sendKeyEvent(new KeyEvent(now, now, KeyEvent.ACTION_UP, code, 0));
    }
}