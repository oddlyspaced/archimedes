package com.sparkappdesign.archimedes.archimedes.model;

import com.sparkappdesign.archimedes.mathexpression.enums.MEExpressionForm;
import com.sparkappdesign.archimedes.mathtype.nodes.MTString;
import com.sparkappdesign.archimedes.mathtype.parsers.MTParser;
import com.sparkappdesign.archimedes.utilities.observables.ImmutableList;
import com.sparkappdesign.archimedes.utilities.observables.Observable;
import com.sparkappdesign.archimedes.utilities.observables.ObservableList;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
/* loaded from: classes.dex */
public class ARCalculation implements Serializable {
    public ARCalculationDelegate mDelegate;
    private ARLineSet mInputLines = new ARLineSet();
    private ObservableList<ARAnswer> mAnswers = new ObservableList<>();

    public ARLineSet getInputLines() {
        return this.mInputLines;
    }

    public Observable<ImmutableList<ARAnswer>> getAnswers() {
        return this.mAnswers;
    }

    public void setDelegate(ARCalculationDelegate delegate) {
        this.mDelegate = delegate;
    }

    public ARCalculation() {
        ARAnswer numericAnswer = new ARAnswer(this, MEExpressionForm.Numeric);
        ARAnswer exactAnswer = new ARAnswer(this, MEExpressionForm.Exact);
        this.mAnswers.add(numericAnswer);
        this.mAnswers.add(exactAnswer);
    }

    public void deinitialize() {
        Iterator<ARAnswer> it = this.mAnswers.iterator();
        while (it.hasNext()) {
            it.next().deinitialize();
        }
    }

    public void invalidateAnswers() {
        Iterator<ARAnswer> it = this.mAnswers.iterator();
        while (it.hasNext()) {
            it.next().invalidate();
        }
    }

    public ARAnswer answerForForm(MEExpressionForm form) {
        Iterator<ARAnswer> it = this.mAnswers.iterator();
        while (it.hasNext()) {
            ARAnswer answer = it.next();
            if (answer.getForm() == form) {
                return answer;
            }
        }
        return null;
    }

    public boolean containsLine(MTString line) {
        Iterator<MTString> it = this.mInputLines.getStrings().iterator();
        while (it.hasNext()) {
            if (it.next() == line) {
                return true;
            }
        }
        Iterator<ARAnswer> it2 = this.mAnswers.iterator();
        while (it2.hasNext()) {
            ARAnswer answer = it2.next();
            if (answer.getLines().getStrings().getValue() != null) {
                Iterator<MTString> it3 = answer.getLines().getStrings().iterator();
                while (it3.hasNext()) {
                    if (it3.next() == line) {
                        return true;
                    }
                }
                continue;
            }
        }
        return false;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(this.mInputLines);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.mInputLines = (ARLineSet) in.readObject();
        this.mAnswers = new ObservableList<>();
        ARAnswer numericAnswer = new ARAnswer(this, MEExpressionForm.Numeric);
        ARAnswer exactAnswer = new ARAnswer(this, MEExpressionForm.Exact);
        this.mAnswers.add(numericAnswer);
        this.mAnswers.add(exactAnswer);
        this.mInputLines.parseStringsToExpressions(new MTParser());
    }
}
