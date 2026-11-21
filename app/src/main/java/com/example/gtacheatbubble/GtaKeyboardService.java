package com.example.gtacheatbubble;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.inputmethodservice.InputMethodService;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.HorizontalScrollView;
import android.widget.Toast;

public class GtaKeyboardService extends InputMethodService {
    public static final String ACTION_TOGGLE_KEYBOARD = "com.example.gtacheatbubble.TOGGLE_KEYBOARD";
    public static final String ACTION_SEND_CODE = "com.example.gtacheatbubble.SEND_CODE";
    public static final String EXTRA_CODE = "code";

    private boolean symbols = false;
    private boolean shifted = false;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final BroadcastReceiver commandReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_TOGGLE_KEYBOARD.equals(action)) {
                if (isInputViewShown()) requestHideSelf(0);
                else requestShowSelf(InputMethodManager.SHOW_FORCED);
            } else if (ACTION_SEND_CODE.equals(action)) {
                String code = intent.getStringExtra(EXTRA_CODE);
                requestShowSelf(InputMethodManager.SHOW_FORCED);
                if (code != null && !code.isEmpty()) {
                    handler.postDelayed(() -> typeString(code), 450);
                }
            }
        }
    };

    @Override public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_TOGGLE_KEYBOARD);
        filter.addAction(ACTION_SEND_CODE);
        if (Build.VERSION.SDK_INT >= 33) registerReceiver(commandReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        else registerReceiver(commandReceiver, filter);
    }

    @Override public void onDestroy() {
        try { unregisterReceiver(commandReceiver); } catch (Exception ignored) {}
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private int dp(float v) {
        return (int)(v * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override public View onCreateInputView() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(5), dp(6), dp(5), dp(6));
        root.setBackgroundColor(Color.rgb(18, 19, 29));

        HorizontalScrollView scroll = new HorizontalScrollView(this);
        scroll.setHorizontalScrollBarEnabled(false);
        LinearLayout cheats = new LinearLayout(this);
        cheats.setOrientation(LinearLayout.HORIZONTAL);
        String[][] codes = {
            {"VIDA","HESOYAM"}, {"ARMAS 1","LXGIWYL"},
            {"ARMAS 2","PROFESSIONALSKIT"}, {"ARMAS 3","UZUMYMW"},
            {"JETPACK","ROCKETMAN"}, {"TANQUE","AIWPRTON"},
            {"POLICÍA","ASNAEB"}, {"BALAS","FULLCLIP"}
        };
        for (String[] item : codes) {
            Button b = keyButton(item[0], true, 10);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(dp(78), dp(34));
            p.setMargins(dp(2), 0, dp(2), dp(4));
            cheats.addView(b, p);
            b.setOnClickListener(v -> typeString(item[1]));
        }
        scroll.addView(cheats);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, dp(38)));

        addKeyRow(root, new String[]{"1","2","3","4","5","6","7","8","9","0"}, false, 0);
        addKeyRow(root, symbols
            ? new String[]{"@","#","$","%","&","-","+","(",")","/"}
            : new String[]{"q","w","e","r","t","y","u","i","o","p"}, !symbols, 0);
        addKeyRow(root, symbols
            ? new String[]{"*","!","?",":",";","'","\"","_"}
            : new String[]{"a","s","d","f","g","h","j","k","l"}, !symbols, 12);

        LinearLayout third = new LinearLayout(this);
        third.setGravity(Gravity.CENTER);
        Button shift = keyButton("⇧", true, 19);
        addFixed(third, shift, 43);
        shift.setOnClickListener(v -> { shifted = !shifted; refreshKeys(); });
        String[] last = symbols
            ? new String[]{"=","[","]","{","}","<",">"}
            : new String[]{"z","x","c","v","b","n","m"};
        for (String key : last) {
            String shown = symbols ? key : (shifted ? key.toUpperCase() : key);
            Button b = keyButton(shown, false, 18);
            addWeighted(third, b);
            b.setOnClickListener(v -> typeText(symbols ? key : (shifted ? key.toUpperCase() : key)));
        }
        Button delete = keyButton("⌫", true, 19);
        addFixed(third, delete, 48);
        delete.setOnClickListener(v -> sendKey(KeyEvent.KEYCODE_DEL));
        root.addView(third);

        LinearLayout bottom = new LinearLayout(this);
        bottom.setGravity(Gravity.CENTER);
        Button mode = keyButton(symbols ? "ABC" : "?123", true, 12);
        addFixed(bottom, mode, 54);
        mode.setOnClickListener(v -> { symbols = !symbols; refreshKeys(); });
        Button comma = keyButton(",", true, 18);
        addFixed(bottom, comma, 30);
        comma.setOnClickListener(v -> typeText(","));
        Button space = keyButton("Español (US)", false, 11);
        space.setTypeface(null, Typeface.NORMAL);
        addWeighted(bottom, space);
        space.setOnClickListener(v -> typeText(" "));
        Button period = keyButton(".", true, 18);
        addFixed(bottom, period, 30);
        period.setOnClickListener(v -> typeText("."));
        Button enter = keyButton("↵", true, 20);
        addFixed(bottom, enter, 45);
        enter.setOnClickListener(v -> sendKey(KeyEvent.KEYCODE_ENTER));
        root.addView(bottom);
        return root;
    }

    private void addKeyRow(LinearLayout root, String[] keys, boolean letters, int inset) {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER);
        row.setPadding(dp(inset), 0, dp(inset), 0);
        for (String key : keys) {
            String shown = letters && shifted ? key.toUpperCase() : key;
            Button b = keyButton(shown, false, 18);
            addWeighted(row, b);
            b.setOnClickListener(v -> typeText(letters && shifted ? key.toUpperCase() : key));
        }
        root.addView(row);
    }

    private void addWeighted(LinearLayout row, View view) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, dp(48), 1);
        p.setMargins(dp(2), dp(3), dp(2), dp(3));
        row.addView(view, p);
    }

    private void addFixed(LinearLayout row, View view, int width) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(dp(width), dp(48));
        p.setMargins(dp(2), dp(3), dp(2), dp(3));
        row.addView(view, p);
    }

    private Button keyButton(String label, boolean accent, int size) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextColor(Color.rgb(250, 248, 255));
        b.setTextSize(size);
        b.setAllCaps(false);
        b.setTypeface(null, Typeface.BOLD);
        b.setPadding(0, 0, 0, 0);
        b.setMinWidth(0); b.setMinimumWidth(0);
        b.setMinHeight(0); b.setMinimumHeight(0);
        b.setIncludeFontPadding(false);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(accent ? Color.rgb(77, 42, 111) : Color.rgb(48, 51, 65));
        bg.setCornerRadius(dp(8));
        bg.setStroke(dp(1), accent ? Color.rgb(179, 103, 255) : Color.rgb(87, 91, 111));
        b.setBackground(bg);
        b.setStateListAnimator(null);
        return b;
    }

    private void refreshKeys() { setInputView(onCreateInputView()); }

    private void typeString(String value) {
        if (getCurrentInputConnection() == null) {
            Toast.makeText(this, "GTA no ofrece una conexión de teclado activa", Toast.LENGTH_SHORT).show();
            return;
        }
        for (int i = 0; i < value.length(); i++) typeText(String.valueOf(value.charAt(i)));
    }

    private void typeText(String value) {
        if (getCurrentInputConnection() == null) return;
        KeyEvent[] events = KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD).getEvents(value.toCharArray());
        if (events == null) return;
        for (KeyEvent event : events) getCurrentInputConnection().sendKeyEvent(event);
    }

    private void sendKey(int code) {
        if (getCurrentInputConnection() == null) return;
        sendDownUpKeyEvents(code);
    }
}
