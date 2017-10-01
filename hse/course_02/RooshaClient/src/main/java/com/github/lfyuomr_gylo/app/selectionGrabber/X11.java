package com.github.lfyuomr_gylo.app.selectionGrabber;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;

import static com.sun.jna.platform.unix.X11.*;

@SuppressWarnings({"UnusedDeclaration", "WeakerAccess"})
public interface X11 extends Library {
    X11 _lX11 = (X11) Native.loadLibrary("X11", X11.class);

    NativeLong XBlackPixel(Display display, int screen_number);

    int XConvertSelection(
            Display display,
            Atom selection,
            Atom target,
            Atom property,
            Window window,
            NativeLong time
    );

    Atom XInternAtom(Display display, String atom_name, int only_if_exists);
}
