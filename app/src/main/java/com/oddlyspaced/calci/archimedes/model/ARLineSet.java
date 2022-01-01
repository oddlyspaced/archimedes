package com.sparkappdesign.archimedes.archimedes.model;

import com.sparkappdesign.archimedes.archimedes.enums.ARLineSetMode;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEExpression;
import com.sparkappdesign.archimedes.mathexpression.expressions.MEVariable;
import com.sparkappdesign.archimedes.mathtype.MTIssue;
import com.sparkappdesign.archimedes.mathtype.nodes.MTNode;
import com.sparkappdesign.archimedes.mathtype.nodes.MTString;
import com.sparkappdesign.archimedes.mathtype.nodes.elements.MTVariable;
import com.sparkappdesign.archimedes.mathtype.parsers.MTParser;
import com.sparkappdesign.archimedes.mathtype.writers.MTWriter;
import com.sparkappdesign.archimedes.utilities.events.Event;
import com.sparkappdesign.archimedes.utilities.events.Observer;
import com.sparkappdesign.archimedes.utilities.events.OwnedEvent;
import com.sparkappdesign.archimedes.utilities.observables.ImmutableList;
import com.sparkappdesign.archimedes.utilities.observables.MutableObservable;
import com.sparkappdesign.archimedes.utilities.observables.Observable;
import com.sparkappdesign.archimedes.utilities.observables.ObservableChange;
import com.sparkappdesign.archimedes.utilities.observables.ObservableChangeGroup;
import com.sparkappdesign.archimedes.utilities.observables.ObservableList;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public class ARLineSet implements Serializable {
    public static final String DONT_INVALIDATE_FLAG = "DONT_INVALIDATE_FLAG";
    private MutableObservable<ARLineSetMode> mMode = new MutableObservable<>(ARLineSetMode.StringBased);
    private ObservableList<MTString> mStrings = new ObservableList<>();
    private ObservableList<MEExpression> mExpressions = new ObservableList<>();
    private ObservableList<MTIssue> mParsingIssues = new ObservableList<>();
    private ObservableList<MTIssue> mWritingIssues = new ObservableList<>();
    private OwnedEvent<MTString> mStringEditedEvent = new OwnedEvent<>();

    public MutableObservable<ARLineSetMode> getMode() {
        return this.mMode;
    }

    public ObservableList<MTString> getStrings() {
        return this.mStrings;
    }

    public ObservableList<MEExpression> getExpressions() {
        return this.mExpressions;
    }

    public Observable<ImmutableList<MTIssue>> getParsingIssues() {
        return this.mParsingIssues;
    }

    public Observable<ImmutableList<MTIssue>> getWritingIssues() {
        return this.mWritingIssues;
    }

    public Event<MTString> getStringEditedEvent() {
        return this.mStringEditedEvent;
    }

    public void handleStringsModified(MTString string) {
        ObservableChangeGroup group = new ObservableChangeGroup();
        invalidateResults(ARLineSetMode.StringBased, group);
        group.performChanges();
        this.mStringEditedEvent.raise(string);
    }

    public void invalidateResults(ARLineSetMode mode, ObservableChangeGroup group) {
        group.setNewValue(this.mMode, mode, DONT_INVALIDATE_FLAG);
        if (mode == ARLineSetMode.ExpressionBased) {
            group.setNewValue(this.mStrings, null, DONT_INVALIDATE_FLAG);
        }
        if (mode == ARLineSetMode.StringBased) {
            group.setNewValue(this.mExpressions, null, DONT_INVALIDATE_FLAG);
        }
        group.setNewValue(this.mParsingIssues, null, DONT_INVALIDATE_FLAG);
        group.setNewValue(this.mWritingIssues, null, DONT_INVALIDATE_FLAG);
    }

    public ARLineSet() {
        this.mStrings.add(new MTString());
        this.mMode.getWillChange().add(new Observer<ObservableChange<ARLineSetMode>>() { // from class: com.sparkappdesign.archimedes.archimedes.model.ARLineSet.1
            public void handle(ObservableChange<ARLineSetMode> change) {
                if (!(change.getExtraInfo() instanceof String) || !change.getExtraInfo().equals(ARLineSet.DONT_INVALIDATE_FLAG)) {
                    ARLineSet.this.invalidateResults(change.getNewValue(), change.getGroup());
                }
            }
        });
        this.mStrings.getWillChange().add(new Observer<ObservableChange<ImmutableList<MTString>>>() { // from class: com.sparkappdesign.archimedes.archimedes.model.ARLineSet.2
            public void handle(ObservableChange<ImmutableList<MTString>> change) {
                if (!(change.getExtraInfo() instanceof String) || !change.getExtraInfo().equals(ARLineSet.DONT_INVALIDATE_FLAG)) {
                    ARLineSet.this.invalidateResults(ARLineSetMode.StringBased, change.getGroup());
                }
            }
        });
        this.mExpressions.getWillChange().add(new Observer<ObservableChange<ImmutableList<MEExpression>>>() { // from class: com.sparkappdesign.archimedes.archimedes.model.ARLineSet.3
            public void handle(ObservableChange<ImmutableList<MEExpression>> change) {
                if (!(change.getExtraInfo() instanceof String) || !change.getExtraInfo().equals(ARLineSet.DONT_INVALIDATE_FLAG)) {
                    ARLineSet.this.invalidateResults(ARLineSetMode.ExpressionBased, change.getGroup());
                }
            }
        });
    }

    public void parseStringsToExpressions(MTParser parser) {
        if (this.mStrings.getValue() != null) {
            List<MEExpression> expressions = parser.parseStrings((List) this.mStrings.getValue());
            ObservableChangeGroup group = new ObservableChangeGroup();
            group.setNewValue(this.mExpressions, new ImmutableList(expressions), DONT_INVALIDATE_FLAG);
            group.setNewValue(this.mParsingIssues, new ImmutableList(), DONT_INVALIDATE_FLAG);
            group.performChanges();
            this.mMode.setValue(ARLineSetMode.StringBased);
        }
    }

    public void writeExpressionsToStrings(MTWriter writer) {
        if (this.mExpressions.getValue() != null) {
            List<MTString> strings = writer.writeExpressions(this.mExpressions.getValue());
            ObservableChangeGroup group = new ObservableChangeGroup();
            group.setNewValue(this.mStrings, new ImmutableList(strings), DONT_INVALIDATE_FLAG);
            group.setNewValue(this.mWritingIssues, new ImmutableList(), DONT_INVALIDATE_FLAG);
            group.performChanges();
        }
    }

    public boolean isEmpty() {
        if (this.mExpressions.getValue() != null && this.mExpressions.getValue().size() != 0) {
            return false;
        }
        if (this.mStrings.getValue() != null) {
            Iterator<E> it = this.mStrings.getValue().iterator();
            while (it.hasNext()) {
                if (((MTString) it.next()).isNotEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    public int getVariableCount() {
        ArrayList<Object> variables = new ArrayList<>();
        if (this.mStrings.getValue() != null) {
            Iterator<MTString> it = this.mStrings.iterator();
            while (it.hasNext()) {
                storeDescendantMTVariableInstances(it.next(), variables);
            }
        } else if (this.mExpressions.getValue() != null) {
            Iterator<MEExpression> it2 = this.mExpressions.iterator();
            while (it2.hasNext()) {
                storeDescendantMEExpressionInstances(it2.next(), variables);
            }
        }
        return variables.size();
    }

    private void storeDescendantMTVariableInstances(MTNode node, ArrayList<Object> variables) {
        if (node != null) {
            if (node instanceof MTVariable) {
                boolean alreadyPresent = false;
                Iterator<Object> it = variables.iterator();
                while (it.hasNext()) {
                    if (node.equivalentTo(it.next())) {
                        alreadyPresent = true;
                    }
                }
                if (!alreadyPresent) {
                    variables.add(node);
                }
            }
            if (node.getChildren() != null) {
                for (MTNode child : node.getChildren()) {
                    storeDescendantMTVariableInstances(child, variables);
                }
            }
        }
    }

    private void storeDescendantMEExpressionInstances(MEExpression expression, ArrayList<Object> variables) {
        if (expression != null) {
            if (expression instanceof MEVariable) {
                variables.add(expression);
            }
            Iterator<MEExpression> it = expression.children().iterator();
            while (it.hasNext()) {
                storeDescendantMEExpressionInstances(it.next(), variables);
            }
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(this.mMode.getValue());
        if (this.mMode.getValue() == ARLineSetMode.StringBased) {
            out.writeObject(this.mStrings.getValue());
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        ARLineSetMode mode = (ARLineSetMode) in.readObject();
        List list = (ImmutableList) in.readObject();
        this.mMode = new MutableObservable<>(mode);
        if (mode != ARLineSetMode.StringBased) {
            list = Arrays.asList(new MTString());
        }
        this.mStrings = new ObservableList<>(list);
        this.mExpressions = new ObservableList<>();
        this.mParsingIssues = new ObservableList<>();
        this.mWritingIssues = new ObservableList<>();
        this.mStringEditedEvent = new OwnedEvent<>();
        this.mMode.getWillChange().add(new Observer<ObservableChange<ARLineSetMode>>() { // from class: com.sparkappdesign.archimedes.archimedes.model.ARLineSet.4
            public void handle(ObservableChange<ARLineSetMode> change) {
                if (!(change.getExtraInfo() instanceof String) || !change.getExtraInfo().equals(ARLineSet.DONT_INVALIDATE_FLAG)) {
                    ARLineSet.this.invalidateResults(change.getNewValue(), change.getGroup());
                }
            }
        });
        this.mStrings.getWillChange().add(new Observer<ObservableChange<ImmutableList<MTString>>>() { // from class: com.sparkappdesign.archimedes.archimedes.model.ARLineSet.5
            public void handle(ObservableChange<ImmutableList<MTString>> change) {
                if (!(change.getExtraInfo() instanceof String) || !change.getExtraInfo().equals(ARLineSet.DONT_INVALIDATE_FLAG)) {
                    ARLineSet.this.invalidateResults(ARLineSetMode.StringBased, change.getGroup());
                }
            }
        });
        this.mExpressions.getWillChange().add(new Observer<ObservableChange<ImmutableList<MEExpression>>>() { // from class: com.sparkappdesign.archimedes.archimedes.model.ARLineSet.6
            public void handle(ObservableChange<ImmutableList<MEExpression>> change) {
                if (!(change.getExtraInfo() instanceof String) || !change.getExtraInfo().equals(ARLineSet.DONT_INVALIDATE_FLAG)) {
                    ARLineSet.this.invalidateResults(ARLineSetMode.ExpressionBased, change.getGroup());
                }
            }
        });
    }
}
