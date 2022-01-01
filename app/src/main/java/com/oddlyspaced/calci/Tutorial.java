package com.oddlyspaced.calci;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
/* loaded from: classes.dex */
public class Tutorial extends FragmentActivity {
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 19) {
            enterImmersiveMode();
        }
        setContentView(R.layout.tutorial);
        ((ViewPager) findViewById(R.id.pager)).setAdapter(new TutorialPagerAdapter(getSupportFragmentManager()));
        boolean showExitButton = getIntent().getBooleanExtra("SHOULD_SHOW_EXIT_BUTTON", false);
        ImageButton exitButton = (ImageButton) findViewById(R.id.exit_button);
        if (showExitButton) {
            exitButton.setOnClickListener(new View.OnClickListener() { // from class: com.oddlyspaced.calci.Tutorial.1
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    Tutorial.this.finish();
                }
            });
        } else {
            exitButton.setVisibility(8);
        }
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

    /* loaded from: classes.dex */
    public static class TutorialPage extends Fragment {
        @Override // android.support.v4.app.Fragment
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            int position = getArguments().getInt("position");
            ImageView tutorialPage = (ImageView) inflater.inflate(R.layout.tutorial_page, container, false);
            switch (position) {
                case 0:
                    tutorialPage.setImageBitmap(getBitmap(R.drawable.tutorial_1));
                    break;
                case 1:
                    tutorialPage.setImageBitmap(getBitmap(R.drawable.tutorial_2));
                    break;
                case 2:
                    tutorialPage.setImageBitmap(getBitmap(R.drawable.tutorial_3));
                    break;
                case 3:
                    tutorialPage.setImageBitmap(getBitmap(R.drawable.tutorial_4));
                    break;
            }
            return tutorialPage;
        }

        private Bitmap getBitmap(int id) {
            return BitmapFactory.decodeResource(getActivity().getResources(), id);
        }
    }

    /* loaded from: classes.dex */
    private class TutorialPagerAdapter extends FragmentStatePagerAdapter {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public TutorialPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            Tutorial.this = r1;
        }

        @Override // android.support.v4.app.FragmentStatePagerAdapter
        public Fragment getItem(int position) {
            TutorialPage newPage = new TutorialPage();
            Bundle arguments = new Bundle();
            arguments.putInt("position", position);
            newPage.setArguments(arguments);
            return newPage;
        }

        @Override // android.support.v4.view.PagerAdapter
        public int getCount() {
            return 4;
        }
    }
}
