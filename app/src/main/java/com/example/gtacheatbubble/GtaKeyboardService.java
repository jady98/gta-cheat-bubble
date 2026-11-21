package com.example.gtacheatbubble;

import android.inputmethodservice.InputMethodService;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.KeyEvent;
import android.view.View;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.HorizontalScrollView;

public class GtaKeyboardService extends InputMethodService {
    private boolean symbols = false;
    private boolean shifted = false;
    private int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }

    @Override public View onCreateInputView() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(4), dp(5), dp(4), dp(5));
        root.setBackgroundColor(Color.rgb(17, 18, 25));

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
            Button b = keyButton(item[0], true);
            b.setTextSize(10);
            b.setMinWidth(dp(94));
            b.setOnClickListener(v -> typeString(item[1]));
            cheats.addView(b, new LinearLayout.LayoutParams(dp(100), dp(39)));
        }
        scroll.addView(cheats);
        root.addView(scroll);

        addRow(root, new String[]{"1","2","3","4","5","6","7","8","9","0"}, false);
        addRow(root, symbols ? new String[]{"@","#","$","%","&","-","+","(",")","/"} :
            new String[]{"Q","W","E","R","T","Y","U","I","O","P"}, !symbols);
        addRow(root, symbols ? new String[]{"*","!","?",":",";","'","\"","_"} :
            new String[]{"A","S","D","F","G","H","J","K","L"}, !symbols);

        LinearLayout third = new LinearLayout(this);
        third.setGravity(Gravity.CENTER);
        Button shift = keyButton("⇧", true);
        shift.setOnClickListener(v -> shifted = !shifted);
        third.addView(shift, new LinearLayout.LayoutParams(dp(42), dp(42)));
        String[] last = symbols ? new String[]{"=","[","]","{","}","<",">"} :
            new String[]{"Z","X","C","V","B","N","M"};
        for (String s : last) {
            Button b = keyButton(s, false);
            b.setOnClickListener(v -> typeText(shifted ? s.toUpperCase() : s.toLowerCase()));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dp(42), 1);
            lp.setMargins(dp(1), dp(2), dp(1), dp(2));
            third.addView(b, lp);
        }
        Button delete = keyButton("⌫", true);
        delete.setOnClickListener(v -> sendKey(KeyEvent.KEYCODE_DEL));
        third.addView(delete, new LinearLayout.LayoutParams(dp(48), dp(42)));
        root.addView(third);

        LinearLayout bottom = new LinearLayout(this);
        bottom.setGravity(Gravity.CENTER);
        Button mode = keyButton(symbols ? "ABC" : "?123", true);
        mode.setOnClickListener(v -> { symbols = !symbols; refreshKeys(); });
        Button globe = keyButton("🌐", true);
        globe.setOnClickListener(v -> {
            try {
                if (!switchToNextInputMethod(false)) showPicker();
            } catch (Exception e) { showPicker(); }
        });
        Button lang = keyButton("ES / EN", true);
        lang.setOnClickListener(v -> showPicker());
        Button space = keyButton("", false);
        space.setOnClickListener(v -> typeText(" "));
        Button enter = keyButton("↵", true);
        enter.setOnClickListener(v -> sendKey(KeyEvent.KEYCODE_ENTER));
        bottom.addView(mode, new LinearLayout.LayoutParams(dp(54), dp(43)));
        bottom.addView(globe, new LinearLayout.LayoutParams(dp(43), dp(43)));
        bottom.addView(lang, new LinearLayout.LayoutParams(dp(64), dp(43)));
        bottom.addView(space, new LinearLayout.LayoutParams(0, dp(43), 1));
        bottom.addView(enter, new LinearLayout.LayoutParams(dp(48), dp(43)));
        root.addView(bottom);
        return root;
    }

    private void refreshKeys() {
        setInputView(onCreateInputView());
    }

    private void showPicker() {
        ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).showInputMethodPicker();
    }

    private void addRow(LinearLayout root, String[] keys, boolean letters) {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER);
        for (String key : keys) {
            Button b = keyButton(key, false);
            b.setOnClickListener(v -> typeText(letters ? (shifted ? key : key.toLowerCase()) : key));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dp(42), 1);
            lp.setMargins(dp(1), dp(2), dp(1), dp(2));
            row.addView(b, lp);
        }
        root.addView(row);
    }

    private Button keyButton(String label, boolean accent) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextColor(Color.rgb(245, 246, 250));
        b.setTextSize(14);
        b.setAllCaps(false);
        b.setTypeface(null, Typeface.BOLD);
        b.setPadding(0, 0, 0, 0);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(accent ? Color.rgb(49, 38, 69) : Color.rgb(43, 45, 55));
        bg.setCornerRadius(dp(7));
        bg.setStroke(dp(1), accent ? Color.rgb(130, 82, 190) : Color.rgb(72, 75, 88));
        b.setBackground(bg);
        return b;
    }

    private void typeString(String value) {
        for (int i=0; i<value.length(); i++) typeText(String.valueOf(value.charAt(i)));
    }

    private void typeText(String value) {
        if (getCurrentInputConnection() != null) getCurrentInputConnection().commitText(value, 1);
    }

    private void sendKey(int code) {
        if (getCurrentInputConnection() == null) return;
        long now = System.currentTimeMillis();
        getCurrentInputConnection().sendKeyEvent(new KeyEvent(now, now, KeyEvent.ACTION_DOWN, code, 0));
        getCurrentInputConnection().sendKeyEvent(new KeyEvent(now, now, KeyEvent.ACTION_UP, code, 0));
    }
}
