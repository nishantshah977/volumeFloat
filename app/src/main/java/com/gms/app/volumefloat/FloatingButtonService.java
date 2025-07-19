package com.gms.app.volumefloat;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class FloatingButtonService extends Service {

    private WindowManager windowManager;
    private View floatingView;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        Button volumeBtn = new Button(this);
        volumeBtn.setText("🔊");

        // Convert 40dp to pixels for smaller size
        int sizeInDp = 35;
        final float scale = getResources().getDisplayMetrics().density;
        int sizeInPx = (int) (sizeInDp * scale + 0.5f);

        // Circular translucent black background with white border
        volumeBtn.setBackground(createCircularBackground(0xAA000000));
        volumeBtn.setTextColor(0xFFFFFFFF);
        volumeBtn.setTextSize(18); // smaller emoji size to fit smaller button
        volumeBtn.setPadding(0, 0, 0, 0);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                sizeInPx,
                sizeInPx,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 200;
        params.y = 300;

        volumeBtn.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(getApplicationContext(), VolumeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } catch (Exception e) {
                // silently ignore or handle as needed
            }
        });

        volumeBtn.setOnTouchListener(new View.OnTouchListener() {
            private int initialX, initialY;
            private float initialTouchX, initialTouchY;
            private boolean isDragging = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        isDragging = false;
                        // Return false to allow click event if no drag happens
                        return false;

                    case MotionEvent.ACTION_MOVE:
                        int deltaX = (int) (event.getRawX() - initialTouchX);
                        int deltaY = (int) (event.getRawY() - initialTouchY);

                        if (!isDragging && (Math.abs(deltaX) > 5 || Math.abs(deltaY) > 5)) {
                            isDragging = true;
                        }

                        if (isDragging) {
                            params.x = initialX + deltaX;
                            params.y = initialY + deltaY;
                            windowManager.updateViewLayout(floatingView, params);
                            return true; // consumed drag event
                        }
                        return false; // not dragging yet

                    case MotionEvent.ACTION_UP:
                        if (isDragging) {
                            // Drag ended, consume event so click won't fire
                            return true;
                        }
                        // Not dragging, allow click event
                        return false;

                    default:
                        return false;
                }
            }
        });

        try {
            windowManager.addView(volumeBtn, params);
            floatingView = volumeBtn;
        } catch (Exception e) {
            // silently ignore or handle as needed
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingView != null) {
            try {
                windowManager.removeView(floatingView);
            } catch (Exception e) {
                // silently ignore or handle as needed
            }
        }
    }

    private GradientDrawable createCircularBackground(int color) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setColor(color);
        shape.setStroke(0, 0xFFFFFFFF); // white border
        return shape;
    }
}
