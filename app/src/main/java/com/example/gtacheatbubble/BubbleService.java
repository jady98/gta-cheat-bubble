package com.example.gtacheatbubble;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BubbleService extends Service {
    private WindowManager wm;
    private View bubble, panel;
    private WindowManager.LayoutParams bubbleParams, panelParams;
    private EditText input;

    @Override public void onCreate() {
        super.onCreate();
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        showBubble();
    }

    private GradientDrawable bg(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color); d.setCornerRadius(radius);
        d.setStroke(2, Color.WHITE);
        return d;
    }

    private WindowManager.LayoutParams params(int width, int height, int flags) {
        return new WindowManager.LayoutParams(width, height,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            flags, PixelFormat.TRANSLUCENT);
    }

    private void showBubble() {
        TextView b = new TextView(this);
        b.setText("GTA");
        b.setTextColor(Color.WHITE); b.setTextSize(13); b.setTypeface(null, Typeface.BOLD);
        b.setGravity(Gravity.CENTER); b.setBackground(bg(Color.rgb(112, 35, 190), 100));
        bubble = b;
        bubbleParams = params(58, 58, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        bubbleParams.gravity = Gravity.TOP | Gravity.START;
        bubbleParams.x = 24; bubbleParams.y = 220;
        b.setOnTouchListener(new View.OnTouchListener() {
            float downX, downY; int startX, startY; boolean moved;
            @Override public boolean onTouch(View v, MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    downX=e.getRawX(); downY=e.getRawY();
                    startX=bubbleParams.x; startY=bubbleParams.y; moved=false; return true;
                }
                if (e.getAction() == MotionEvent.ACTION_MOVE) {
                    float dx=e.getRawX()-downX, dy=e.getRawY()-downY;
                    if (Math.abs(dx)>6 || Math.abs(dy)>6) moved=true;
                    bubbleParams.x=startX+(int)dx; bubbleParams.y=startY+(int)dy;
                    wm.updateViewLayout(bubble,bubbleParams); return true;
                }
                if (e.getAction() == MotionEvent.ACTION_UP) {
                    if (!moved) togglePanel(); return true;
                }
                return true;
            }
        });
        wm.addView(bubble, bubbleParams);
    }

    private void togglePanel() {
        if (panel != null) { wm.removeView(panel); panel=null; return; }
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL); box.setPadding(12,12,12,12);
        box.setBackground(bg(Color.rgb(24,27,38), 24));

        TextView heading = new TextView(this);
        heading.setText("GTA CHEATS  •  COPIAR CÓDIGO");
        heading.setTextColor(Color.WHITE); heading.setTextSize(14);
        box.addView(heading);

        input = new EditText(this);
        input.setSingleLine(true); input.setTextColor(Color.WHITE);
        input.setHintTextColor(Color.LTGRAY); input.setHint("Código...");
        box.addView(input);

        addQuickRow(box, "VIDA + DINERO", "HESOYAM", "ARMAS 1", "LXGIWYL");
        addQuickRow(box, "ARMAS 2", "PROFESSIONALSKIT", "ARMAS 3", "UZUMYMW");
        addQuickRow(box, "JETPACK", "ROCKETMAN", "TANQUE", "AIWPRTON");
        addQuickRow(box, "QUITAR POLICÍA", "ASNAEB", "MUNICIÓN", "FULLCLIP");

        LinearLayout actions = new LinearLayout(this);
        Button copy = new Button(this); copy.setText("COPIAR");
        copy.setOnClickListener(v -> copyCode());
        Button hide = new Button(this); hide.setText("MINIMIZAR");
        hide.setOnClickListener(v -> togglePanel());
        actions.addView(copy, new LinearLayout.LayoutParams(0, -2, 1));
        actions.addView(hide, new LinearLayout.LayoutParams(0, -2, 1));
        box.addView(actions);

        TextView note = new TextView(this);
        note.setText("Copiar no activa el truco por sí solo. GTA debe admitir entrada de códigos.");
        note.setTextColor(Color.LTGRAY); note.setTextSize(11);
        box.addView(note);

        panel = box;
        panelParams = params(300, WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        panelParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        panelParams.y = 100;
        wm.addView(panel, panelParams);
    }

    private void addQuickRow(LinearLayout parent, String l1, String c1, String l2, String c2) {
        LinearLayout row = new LinearLayout(this);
        Button a = new Button(this); a.setText(l1); a.setTextSize(10);
        a.setOnClickListener(v -> input.setText(c1));
        Button b = new Button(this); b.setText(l2); b.setTextSize(10);
        b.setOnClickListener(v -> input.setText(c2));
        row.addView(a, new LinearLayout.LayoutParams(0, -2, 1));
        row.addView(b, new LinearLayout.LayoutParams(0, -2, 1));
        parent.addView(row);
    }

    private void copyCode() {
        String code = input.getText().toString().trim();
        if (code.isEmpty()) { Toast.makeText(this, "Escribe o elige un truco", Toast.LENGTH_SHORT).show(); return; }
        ClipboardManager cm = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText("GTA cheat", code));
        Toast.makeText(this, "Código copiado: " + code, Toast.LENGTH_SHORT).show();
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) { return START_STICKY; }
    @Override public void onDestroy() {
        if (panel != null) wm.removeView(panel);
        if (bubble != null) wm.removeView(bubble);
        super.onDestroy();
    }
    @Override public IBinder onBind(Intent intent) { return null; }
}
