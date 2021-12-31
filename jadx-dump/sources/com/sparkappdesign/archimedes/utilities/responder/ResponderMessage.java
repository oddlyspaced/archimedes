package com.sparkappdesign.archimedes.utilities.responder;

import java.util.HashMap;
/* loaded from: classes.dex */
public class ResponderMessage {
    private HashMap<String, Object> mContents;
    private String mType;

    public String getType() {
        return this.mType;
    }

    public ResponderMessage(String type, HashMap<String, Object> contents) {
        this.mType = type;
        this.mContents = contents;
    }

    public boolean send() {
        Responder responder = findResponder();
        if (responder == null) {
            return false;
        }
        responder.handleMessage(this.mType, this.mContents);
        return true;
    }

    public Responder findResponder() {
        for (Responder responder = ResponderManager.getFirstResponder().getValue(); responder != null; responder = responder.getAncestor()) {
            if (responder.canHandleMessageType(this.mType)) {
                boolean hasPermission = true;
                Responder ancestor = responder.getAncestor();
                while (true) {
                    if (ancestor == null) {
                        break;
                    } else if (!ancestor.isChildAllowedToHandleMessage(responder, this)) {
                        hasPermission = false;
                        break;
                    } else {
                        ancestor = ancestor.getAncestor();
                    }
                }
                if (hasPermission) {
                    return responder;
                }
            }
        }
        return null;
    }
}
