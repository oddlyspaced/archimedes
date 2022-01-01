package com.sparkappdesign.archimedes.mathtype;

import com.sparkappdesign.archimedes.mathtype.nodes.MTString;
/* loaded from: classes.dex */
public class MTMEVariableIdentifier {
    private MTString mName;

    public MTString getName() {
        return this.mName;
    }

    public MTMEVariableIdentifier() {
        this.mName = new MTString();
    }

    public MTMEVariableIdentifier(MTString name) {
        this.mName = name;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof MTMEVariableIdentifier) {
            return this.mName.equivalentTo(((MTMEVariableIdentifier) other).mName);
        }
        return false;
    }

    public int hashCode() {
        return 0;
    }

    public String toString() {
        return this.mName.toString();
    }
}
