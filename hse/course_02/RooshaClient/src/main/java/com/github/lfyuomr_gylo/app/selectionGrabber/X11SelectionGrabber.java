package com.github.lfyuomr_gylo.app.selectionGrabber;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.PointerByReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

import static com.github.lfyuomr_gylo.app.selectionGrabber.X11._lX11;
import static com.sun.jna.platform.unix.X11.*;

class X11SelectionGrabber extends SelectionGrabber {
    private static final com.sun.jna.platform.unix.X11 lX11 = INSTANCE;

    private Display display;
    private Window window;
    private Atom utf8Atom;

    X11SelectionGrabber() {
    }

    @Override
    public @Nullable String getSelection() {
        init();
        final String result = getSelectionText();
        reset();
        return result;
    }

    private void init() {
        display = lX11.XOpenDisplay(null); // connect to X Server
        window = createEventReceiverWindow(); // create window for receiving events
        lX11.XSelectInput(display, window, new NativeLong(PropertyChangeMask));

        utf8Atom = _lX11.XInternAtom(display, "UTF8_STRING", 1); // WARNING: jna binding crashes!
        if (utf8Atom.equals(new NativeLong(0))) {
            utf8Atom = null;
        }
    }

    private void reset() {
        lX11.XCloseDisplay(display);
    }

    private @Nullable String getSelectionText() {
        final NativeLong time = getTimeStamp();
        String result = utf8Atom == null ? null : getSelection(XA_PRIMARY, utf8Atom, time);
        if (result == null || result.equals("")) {
            result = getSelection(XA_PRIMARY, XA_STRING, time);
        }

        return result;
    }

    private @Nullable String getSelection(@NotNull Atom selection, @NotNull Atom target, @NotNull NativeLong time) {
        final Atom property = _lX11.XInternAtom(display, "XSEL_DATA", 0); // WARNING: jna binding crashes!
        _lX11.XConvertSelection(display, selection, target, property, window, time);
        lX11.XSync(display, false);

        return waitSelection(selection);
    }

    private @Nullable String waitSelection(NativeLong selection) {
        byte[] resultBytes;

        final XEvent event = new XEvent();
        final AtomByReference actualTarget = new AtomByReference();
        final IntByReference actualFormat = new IntByReference();
        final NativeLongByReference length = new NativeLongByReference();
        final NativeLongByReference bytesAfter = new NativeLongByReference();
        final PointerByReference nativeBuff = new PointerByReference();

        while (true) {
            lX11.XNextEvent(display, event);

            if (event.type != SelectionNotify) {
                continue;
            }

            final XSelectionEvent xSelection = (XSelectionEvent) event.readField("xselection");

            if (!xSelection.selection.equals(selection)) {
                continue;
            }
            if (xSelection.property == null || xSelection.property.longValue() == None) {
                return null;
            }


            lX11.XGetWindowProperty(
                    xSelection.display,
                    xSelection.requestor,
                    xSelection.property,
                    new NativeLong(0L),
                    new NativeLong(1000000L),
                    false,
                    new Atom(AnyPropertyType),
                    actualTarget,
                    actualFormat,
                    length,
                    bytesAfter,
                    nativeBuff
            );

            if (!actualTarget.getValue().equals(utf8Atom) && !actualTarget.getValue().equals(XA_STRING)) {
                resultBytes = null;
            }
            else {
                final byte[] buff = nativeBuff.getValue().getByteArray(0, length.getValue().intValue());
                resultBytes = Arrays.copyOf(buff, buff.length);
                lX11.XFree(nativeBuff.getValue());
            }


            return encodeBytes(resultBytes, xSelection.target);
        }
    }

    private @NotNull NativeLong getTimeStamp() {
        XEvent event = new XEvent();

        lX11.XChangeProperty(display, window, XA_WM_NAME, XA_STRING, 8, 2, null, 0);

        while (true) {
            lX11.XNextEvent(display, event);

            if (event.type == PropertyNotify) {
                final XPropertyEvent xProperty = (XPropertyEvent) event.readField("xproperty");
                return xProperty.time;
            }
        }
    }

    private @NotNull Window createEventReceiverWindow() {
        final Window root = lX11.XDefaultRootWindow(display);
        final int defaultScreen = lX11.XDefaultScreen(display);
        final NativeLong blackPixel = _lX11.XBlackPixel(display, defaultScreen);

        return lX11.XCreateSimpleWindow(display, root, 0, 0, 1, 1, 0, blackPixel.intValue(), blackPixel.intValue());
    }

    private @Nullable String encodeBytes(@Nullable byte[] buff, @NotNull NativeLong target) {
        if (buff == null) {
            return null;
        }
        if (target.equals(utf8Atom)) {
            return new String(buff);
        }

        return Native.toString(buff);
    }
}
