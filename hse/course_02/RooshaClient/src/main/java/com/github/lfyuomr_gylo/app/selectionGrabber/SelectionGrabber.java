package com.github.lfyuomr_gylo.app.selectionGrabber;

import com.sun.jna.Platform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SelectionGrabber {
    private static SelectionGrabber INSTANCE = null;

    public static @NotNull SelectionGrabber getGrabber() throws PlatformIsNotSupportedException {
        if (INSTANCE != null) {
            return INSTANCE;
        }

        if (Platform.isX11()) {
            return INSTANCE = new X11SelectionGrabber();
        }
        else if (Platform.isWindows()) {
            return INSTANCE = new WinSelectionGrabber();
        }
        else if (Platform.isMac()) {
            throw new PlatformIsNotSupportedException();
        }
        else {
            throw new PlatformIsNotSupportedException();
        }
    }

    public abstract @Nullable String getSelection();
}
