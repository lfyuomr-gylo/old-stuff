package com.github.lfyuomr.gylo.kango.client.proto.exceptions;

import com.github.lfyuomr.gylo.kango.client.util.Alerts;

public class OfflineUserInConversationCreationKangoException extends InteractiveKangoException {
    /**
     * Show graphical representation of exception.
     */
    @Override
    public void
    showErrorAlert() {
        Alerts.showErrorAlert(this);
    }
}
