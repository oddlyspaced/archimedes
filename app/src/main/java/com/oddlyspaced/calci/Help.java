package com.oddlyspaced.calci;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
/* loaded from: classes.dex */
public class Help extends Activity {
    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 19) {
            enterImmersiveMode();
        }
        setContentView(R.layout.help);
        try {
            ((TextView) findViewById(R.id.version_name)).setText("Archimedes " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException exception) {
            exception.printStackTrace();
        }
        ((Button) findViewById(R.id.tutorial_button)).setOnClickListener(new View.OnClickListener() { // from class: com.oddlyspaced.calci.Help.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Help.this.startActivity(new Intent(Help.this, Tutorial.class));
            }
        });
        ((Button) findViewById(R.id.power_tips_button)).setOnClickListener(new View.OnClickListener() { // from class: com.oddlyspaced.calci.Help.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Help.this.startActivity(new Intent(Help.this, PowerTips.class));
            }
        });
        ((Button) findViewById(R.id.email)).setOnClickListener(new View.OnClickListener() { // from class: com.oddlyspaced.calci.Help.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Help.this.startActivity(Intent.createChooser(new Intent("android.intent.action.SENDTO", Uri.fromParts("mailto", "android@archimedescalculator.com", null)), "Send Email"));
            }
        });
        ((Button) findViewById(R.id.rate_review)).setOnClickListener(new View.OnClickListener() { // from class: com.oddlyspaced.calci.Help.4
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Help.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + Help.this.getPackageName())));
            }
        });
        ((Button) findViewById(R.id.share)).setOnClickListener(new View.OnClickListener() { // from class: com.oddlyspaced.calci.Help.5
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("text/plain");
                intent.putExtra("android.intent.extra.SUBJECT", "Archimedes");
                intent.putExtra("android.intent.extra.TEXT", "http://archimedescalculator.com/android/");
                Help.this.startActivity(Intent.createChooser(intent, "Share"));
            }
        });
        ((Button) findViewById(R.id.legal)).setOnClickListener(new View.OnClickListener() { // from class: com.oddlyspaced.calci.Help.6
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setData(Uri.parse("http://archimedescalculator.com/android/legal/"));
                Help.this.startActivity(intent);
            }
        });
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
