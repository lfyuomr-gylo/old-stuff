package com.github.lfyuomr.gylo.kango.client.proto.exceptions;

import com.github.lfyuomr.gylo.kango.client.util.Alerts;

/**
 * Is thrown when received incorrect message from server or server notified about some errors.
 */
public class ServerSideKangoException extends InteractiveKangoException {
    /**
     * Show graphical representation of exception.
     */
    @Override
    public void
    showErrorAlert() {
        Alerts.showErrorAlert(this);
    }
}
