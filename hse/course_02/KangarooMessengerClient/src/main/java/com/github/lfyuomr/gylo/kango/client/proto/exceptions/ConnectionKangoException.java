package com.github.lfyuomr.gylo.kango.client.proto.exceptions;

import com.github.lfyuomr.gylo.kango.client.util.Alerts;

/**
 * Is thrown whenever attempt to connect with server failed
 */
public class ConnectionKangoException extends InteractiveKangoException {
    /**
     * Show graphical representation of exception.
     */
    @Override
    public void
    showErrorAlert() {
        Alerts.showErrorAlert(this);
    }
}
