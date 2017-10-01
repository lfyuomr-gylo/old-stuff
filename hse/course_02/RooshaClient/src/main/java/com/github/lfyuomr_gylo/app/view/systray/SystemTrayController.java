package com.github.lfyuomr_gylo.app.view.systray;

import com.github.lfyuomr_gylo.app.config.LangBundles;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.github.lfyuomr_gylo.app.RooshaApp.INSTANCE;
import static com.github.lfyuomr_gylo.app.config.Configuration.CONFIG;


public final class SystemTrayController {
    private SystemTrayController() {
    }

    private static final Object lock = new Object();
    private static boolean isMinimized = CONFIG().isLaunchedMinimized();

    private static TrayIcon icon = null;
    private static PopupMenu popup = null;

    private static MenuItem showItem = null;
    private static MenuItem minimizeItem = null;
    private static MenuItem exitItem = null;

    /**
     * Create app tray icon, if system tray is supported.
     * @return app tray icon if system tray supported, {@code null} otherwise
     */
    public static @Nullable TrayIcon getTrayIcon() {
        if (icon != null) {
            return icon;
        }

        if (!SystemTray.isSupported()) {
            return null;
        }

        synchronized (lock) {
            if (icon != null) {
                return icon;
            }

            icon = createIcon();
            initPopup();
            icon.setPopupMenu(popup);
            return icon;
        }
    }

    private static @NotNull TrayIcon createIcon() {
        BufferedImage iconImage;
        try {
            iconImage = ImageIO.read(SystemTrayController.class.getResource("/icon.png"));
        } catch (IOException e) {
            e.printStackTrace();
            iconImage = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
        }

        final int trayIconWidth = new TrayIcon(iconImage).getSize().width;
        return icon = new TrayIcon(iconImage.getScaledInstance(trayIconWidth, -1, Image.SCALE_SMOOTH));
    }

    private static void initPopup() {
        popup = new PopupMenu();
        showItem = createMenuItem("showTrayMenuButton");
        minimizeItem = createMenuItem("minimizeTrayMenuButton");
        exitItem = createMenuItem("exitTrayMenuButton");

        showItem.addActionListener(e -> INSTANCE.show());
        minimizeItem.addActionListener(e -> INSTANCE.hide());
        //TODO: implement more sensible way to exit app
        exitItem.addActionListener(e -> System.exit(0));

        popup.add(isMinimized ? showItem : minimizeItem);
        popup.add(exitItem);
    }

    public static void toggleMinimizationState() {
        isMinimized = !isMinimized;
        popup.remove(0);
        popup.insert(isMinimized ? showItem : minimizeItem, 0);
    }

    private static MenuItem createMenuItem(String itemName) {
        final MenuItem item = new MenuItem();
        LangBundles.getSystemTrayBundleObservable()
                   .map(bundle -> bundle.getString(itemName))
                   .subscribe(item::setLabel);
        return item;
    }
}
