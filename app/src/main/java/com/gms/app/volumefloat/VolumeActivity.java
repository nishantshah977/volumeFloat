package com.gms.app.volumefloat;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;

public class VolumeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.adjustVolume(AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);
        }

        finish(); // Auto-close
    }
}
