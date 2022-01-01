package com.oddlyspaced.calci;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
/* loaded from: classes.dex */
public class PowerTips extends Activity {
    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 19) {
            enterImmersiveMode();
        }
        setContentView(R.layout.power_tips);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (Build.VERSION.SDK_INT >= 19 && hasFocus) {
            enterImmersiveMode();
        }
    }

    private void enterImmersiveMode() {
        getWindow().getDecorView().setSystemUiVisibility(5638);
    }
}
