package com.example.gtacheatbubble;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final int OVERLAY_REQUEST = 44;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(28, 36, 28, 28);

        TextView title = new TextView(this);
        title.setText("GTA Cheat Bubble\n\nPermite mostrar una burbuja encima de otras aplicaciones. La app solo copia códigos: no puede inyectarlos directamente en GTA.");
        title.setTextSize(18);
        root.addView(title);

        Button permission = new Button(this);
        permission.setText("1. Permitir mostrar sobre otras apps");
        root.addView(permission);
        permission.setOnClickListener(v -> {
            if (!Settings.canDrawOverlays(this)) {
                Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
                startActivityForResult(i, OVERLAY_REQUEST);
            } else startBubble();
        });

        Button start = new Button(this);
        start.setText("2. Activar burbuja");
        root.addView(start);
        start.setOnClickListener(v -> {
            if (Settings.canDrawOverlays(this)) startBubble();
            else permission.performClick();
        });

        Button stop = new Button(this);
        stop.setText("Cerrar burbuja");
        root.addView(stop);
        stop.setOnClickListener(v -> stopService(new Intent(this, BubbleService.class)));

        setContentView(root);
    }

    private void startBubble() {
        startService(new Intent(this, BubbleService.class));
        finish();
    }
}
