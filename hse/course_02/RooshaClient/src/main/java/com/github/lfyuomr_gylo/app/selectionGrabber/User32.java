package com.github.lfyuomr_gylo.app.selectionGrabber;

import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;

public interface User32 extends StdCallLibrary {
    User32 _user32 = (User32) Native.loadLibrary("user32", User32.class);

    void keybd_event(byte bVk, byte bScan, int dwFlahs, int dwExtraInfo);
}
