package com.github.lfyuomr_gylo.app;

import com.github.lfyuomr_gylo.app.selectionGrabber.SelectionGrabber;
import com.github.lfyuomr_gylo.app.view.systray.SystemTrayController;
import com.github.lfyuomr_gylo.app.view.translator.TranslationDialog;
import com.tulskiy.keymaster.common.Provider;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.awt.*;

import static com.github.lfyuomr_gylo.app.config.Configuration.CONFIG;
import static com.github.lfyuomr_gylo.app.view.systray.SystemTrayController.getTrayIcon;
import static javafx.application.Platform.runLater;

public class RooshaApp extends Application {
    public static RooshaApp INSTANCE;

    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Platform.setImplicitExit(false);

        this.primaryStage = primaryStage;
        final GridPane root = new GridPane();
        final Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> hide());

        registerTrayIcon();
        if (!CONFIG().isLaunchedMinimized()) {
            primaryStage.show();
        }

        registerGlobalHotKey();
    }

    @Override
    public void init() throws Exception {
        super.init();
        INSTANCE = this;
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    /**
     * Show application primary stage and replace in system tray 'show' button with 'minimize' button.
     * Exclusively this function should be used to show primary stage,
     * except initial show in {@link #start(Stage)} function.
     */
    public void show() {
        SystemTrayController.toggleMinimizationState();
        runLater(primaryStage::show);
    }

    /**
     * Hide application primary stage and replace in system tray 'minimize' button with 'show' button.
     * Exclusively this function should be used to hide primary stage.
     */
    public void hide() {
        SystemTrayController.toggleMinimizationState();
        runLater(primaryStage::hide);
    }

    /**
     * Create and add app icon to system tray, if it's supported.
     */
    private void registerTrayIcon() {
        TrayIcon trayIcon = getTrayIcon();
        if (trayIcon != null) {
            try {
                SystemTray.getSystemTray().add(trayIcon);
            } catch (AWTException e) {
                System.err.println("error when adding icon to tray");
                // TODO: add print to log
            }
        }
    }

    private void registerGlobalHotKey() {
        final Provider provider = Provider.getCurrentProvider(false);
        CONFIG().getGlobalHotKeyActivatorSubject().subscribe(keyStroke -> {
            provider.reset();
            provider.register(keyStroke, e -> {
                final String selection = SelectionGrabber.getGrabber().getSelection();
                if (selection != null) {
                    runLater(() ->
                            new TranslationDialog(selection).showAndWait()
                    );
                }
                else {
                    //TODO: show 'no selection' alert
                }
            });
        });
    }
}
