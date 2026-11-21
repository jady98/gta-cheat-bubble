package com.example.gtacheatbubble;

import android.app.Service;
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
import android.widget.TextView;

public class BubbleService extends Service {
    private WindowManager wm;
    private View bubble;
    private WindowManager.LayoutParams bubbleParams;

    @Override public void onCreate() {
        super.onCreate();
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        showBubble();
    }

    private GradientDrawable bg(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radius);
        d.setStroke(2, Color.rgb(195, 139, 255));
        return d;
    }

    private WindowManager.LayoutParams params(int width, int height, int flags) {
        return new WindowManager.LayoutParams(width, height,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            flags, PixelFormat.TRANSLUCENT);
    }

    private void showBubble() {
        TextView b = new TextView(this);
        b.setText("J");
        b.setTextColor(Color.WHITE);
        b.setTextSize(23);
        b.setTypeface(null, Typeface.BOLD);
        b.setGravity(Gravity.CENTER);
        b.setBackground(bg(Color.rgb(105, 39, 178), 100));
        bubble = b;
        bubbleParams = params(58, 58, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        bubbleParams.gravity = Gravity.TOP | Gravity.START;
        bubbleParams.x = 24;
        bubbleParams.y = 220;
        b.setOnTouchListener(new View.OnTouchListener() {
            float downX, downY;
            int startX, startY;
            boolean moved;
            @Override public boolean onTouch(View v, MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    downX = e.getRawX(); downY = e.getRawY();
                    startX = bubbleParams.x; startY = bubbleParams.y;
                    moved = false;
                    return true;
                }
                if (e.getAction() == MotionEvent.ACTION_MOVE) {
                    float dx = e.getRawX() - downX, dy = e.getRawY() - downY;
                    if (Math.abs(dx) > 6 || Math.abs(dy) > 6) moved = true;
                    bubbleParams.x = startX + (int)dx;
                    bubbleParams.y = startY + (int)dy;
                    wm.updateViewLayout(bubble, bubbleParams);
                    return true;
                }
                if (e.getAction() == MotionEvent.ACTION_UP) {
                    if (!moved) {
                        Intent intent = new Intent(GtaKeyboardService.ACTION_TOGGLE_KEYBOARD);
                        intent.setPackage(getPackageName());
                        sendBroadcast(intent);
                    }
                    return true;
                }
                return true;
            }
        });
        wm.addView(bubble, bubbleParams);
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) { return START_STICKY; }

    @Override public void onDestroy() {
        if (bubble != null) wm.removeView(bubble);
        super.onDestroy();
    }

    @Override public IBinder onBind(Intent intent) { return null; }
}
