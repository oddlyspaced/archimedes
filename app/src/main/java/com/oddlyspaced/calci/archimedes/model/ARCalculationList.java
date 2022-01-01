package com.oddlyspaced.calci.archimedes.model;

import com.oddlyspaced.calci.mathtype.nodes.MTString;
import com.oddlyspaced.calci.utilities.observables.ImmutableList;
import com.oddlyspaced.calci.utilities.observables.ObservableList;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
/* loaded from: classes.dex */
public class ARCalculationList implements Serializable {
    private ObservableList<ARCalculation> mCalculations = new ObservableList<>();

    public ObservableList<ARCalculation> getCalculations() {
        return this.mCalculations;
    }

    public ARCalculationList() {
        this.mCalculations.add(new ARCalculation());
    }

    public ARCalculation getCalculationContainingLine(MTString line) {
        Iterator<ARCalculation> it = this.mCalculations.iterator();
        while (it.hasNext()) {
            ARCalculation calculation = it.next();
            if (calculation.containsLine(line)) {
                return calculation;
            }
        }
        return null;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(this.mCalculations.getValue());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.mCalculations = new ObservableList<>((ImmutableList) in.readObject());
    }
}
