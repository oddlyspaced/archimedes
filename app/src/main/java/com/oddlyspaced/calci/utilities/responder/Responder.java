package com.sparkappdesign.archimedes.utilities.responder;

import java.util.HashMap;
/* loaded from: classes.dex */
public interface Responder {
    boolean canHandleMessageType(String str);

    Responder getAncestor();

    void handleMessage(String str, HashMap<String, Object> hashMap);

    boolean isChildAllowedToHandleMessage(Responder responder, ResponderMessage responderMessage);
}
