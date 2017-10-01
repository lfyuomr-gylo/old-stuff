package com.github.lfyuomr.gylo.kango.client.proto.exceptions;

@SuppressWarnings("WeakerAccess")
public abstract class InteractiveKangoException extends KangoException {
    /**
     * Show graphical representation of exception.
     */
    public abstract void
    showErrorAlert();
}
