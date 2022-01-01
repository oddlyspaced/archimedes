package com.oddlyspaced.calci;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.oddlyspaced.calci.archimedes.model.ARCalculation;
import com.oddlyspaced.calci.archimedes.model.ARCalculationList;
import com.oddlyspaced.calci.archimedes.model.ARSettings;
import com.oddlyspaced.calci.archimedes.views.ARCalculationListView;
import com.oddlyspaced.calci.archimedes.views.AROverlayView;
import com.oddlyspaced.calci.archimedes.views.ARRootView;
import com.oddlyspaced.calci.mathtype.nodes.MTString;
import com.oddlyspaced.calci.mathtype.views.selection.MTSelection;
import com.oddlyspaced.calci.testing.MERealT;
import com.oddlyspaced.calci.utilities.FloatingOptionsMenu;
import com.oddlyspaced.calci.utilities.observables.ObservableList;
import com.oddlyspaced.calci.utilities.responder.ResponderManager;
import java.io.Serializable;
/* loaded from: classes.dex */
public class Archimedes extends Activity {
    private ARRootView mRootView;

    static {
        System.loadLibrary("tommath");
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 19) {
            enterImmersiveMode();
        }
        ARSettings.sharedSettings(this);
        this.mRootView = new ARRootView(this);
        setContentView(this.mRootView);
        if (savedInstanceState == null) {
            ARCalculationListView calculationListView = this.mRootView.getCalculationListView();
            calculationListView.setCalculationList(new ARCalculationList());
            calculationListView.setSelection(MTSelection.cursorAtEndOfString(calculationListView.getCalculationList().getCalculations().get(0).getInputLines().getStrings().get(0)), false);
        }
        showTutorialOnFirstLaunch();
    }

    private void runTests() {
        MERealT.testUnitsWithRandomValues();
    }

    private void showTutorialOnFirstLaunch() {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("Archimedes Should Show Tutorial", true)) {
            Intent intent = new Intent(this, Tutorial.class);
            intent.putExtra("SHOULD_SHOW_EXIT_BUTTON", true);
            startActivity(intent);
        }
    }

    @Override // android.app.Activity
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("Archimedes Should Show Tutorial", true)) {
            preferences.edit().putBoolean("Archimedes Should Show Tutorial", false).apply();
            return;
        }
        FloatingOptionsMenu.getInstance(this).hide(false);
        AROverlayView.getInstance(this).drawDrawable(null);
        State state = new State();
        ARCalculationListView calculationListView = this.mRootView.getCalculationListView();
        if (calculationListView.getCalculationList() != null) {
            state.mCalculationList = calculationListView.getCalculationList();
        }
        if (calculationListView.getSelection() != null) {
            state.mSelection = calculationListView.getSelection();
        }
        state.mActiveCalculationIndex = calculationListView.getActiveCalculationIndex();
        outState.putSerializable("SAVED_STATE", state);
    }

    @Override // android.app.Activity
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        MTString activeString;
        ARCalculationListView calculationListView = this.mRootView.getCalculationListView();
        calculationListView.getAutoScrollView().setAutoScrollEnabled(false);
        State state = (State) savedInstanceState.getSerializable("SAVED_STATE");
        Serializable savedCalculationList = state.mCalculationList;
        Serializable savedSelection = state.mSelection;
        int savedActiveCalculationIndex = state.mActiveCalculationIndex;
        if (savedCalculationList != null) {
            try {
                calculationListView.setCalculationList((ARCalculationList) savedCalculationList);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        if (savedSelection != null && calculationListView.getCalculationList().getCalculationContainingLine(((MTSelection) savedSelection).getString()) != null) {
            calculationListView.setSelection((MTSelection) savedSelection, false);
        } else if (savedActiveCalculationIndex != -1) {
            ARCalculation activeCalculation = calculationListView.getCalculationList().getCalculations().get(savedActiveCalculationIndex);
            ObservableList<MTString> answerStrings = activeCalculation.getAnswers().getValue().get(0).getLines().getStrings();
            if (answerStrings.getValue() == null || answerStrings.isEmpty()) {
                activeString = activeCalculation.getInputLines().getStrings().get(0);
            } else {
                activeString = answerStrings.get(0);
            }
            ResponderManager.setFirstResponder(calculationListView.getMathTypeViewForLine(activeString));
        }
        calculationListView.getAutoScrollView().setAutoScrollEnabled(true);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        this.mRootView.deinitialize();
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

    @Override // android.app.Activity
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    /* loaded from: classes.dex */
    static class State implements Serializable {
        int mActiveCalculationIndex;
        ARCalculationList mCalculationList;
        MTSelection mSelection;

        State() {
        }
    }
}
