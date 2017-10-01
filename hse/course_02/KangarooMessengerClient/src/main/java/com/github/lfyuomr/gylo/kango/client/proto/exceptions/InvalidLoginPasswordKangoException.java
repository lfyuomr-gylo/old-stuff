package com.github.lfyuomr.gylo.kango.client.proto.exceptions;

import com.github.lfyuomr.gylo.kango.client.util.Alerts;

/**
 * Is thrown if login/password pair is incorrect when trying to sign in.
 */
public class InvalidLoginPasswordKangoException extends InteractiveKangoException {
    /**
     * Show graphical representation of exception.
     */
    @Override
    public void
    showErrorAlert() {
        Alerts.showErrorAlert(this);
    }
}
