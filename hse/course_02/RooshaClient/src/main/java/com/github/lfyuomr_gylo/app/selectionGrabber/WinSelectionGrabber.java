package com.github.lfyuomr_gylo.app.selectionGrabber;

import com.sun.jna.platform.win32.WinDef;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static com.github.lfyuomr_gylo.app.selectionGrabber.User32._user32;

class WinSelectionGrabber extends SelectionGrabber {
    private static final com.sun.jna.platform.win32.User32 user32 = com.sun.jna.platform.win32.User32.INSTANCE;

    @Override
    public @Nullable String getSelection() {
        String result = null;
        try {
            final WinDef.HWND activeWindow = user32.GetForegroundWindow();
            final Map<DataFormat, Object> clipboardBefore = getClipboardContents();

            emulateCopyKeystroke();
            Thread.sleep(100);
            result = Clipboard.getSystemClipboard().getString();

            Clipboard.getSystemClipboard().setContent(clipboardBefore);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }

    private @NotNull Map<DataFormat, Object> getClipboardContents() {
        final Map<DataFormat, Object> result = new HashMap<>();
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        clipboard.getContentTypes().forEach(format -> result.put(format, clipboard.getContent(format)));

        return result;
    }

    private void emulateCopyKeystroke() {
        _user32.keybd_event((byte) 0x11, (byte) 0, 0, 0);
        _user32.keybd_event((byte) 0x43, (byte) 0, 0, 0);
        _user32.keybd_event((byte) 0x43, (byte) 0, 2, 0);
        _user32.keybd_event((byte) 0x11, (byte) 0, 2, 0);
    }
}
