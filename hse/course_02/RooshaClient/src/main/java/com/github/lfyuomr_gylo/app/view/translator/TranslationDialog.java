package com.github.lfyuomr_gylo.app.view.translator;


import com.github.lfyuomr_gylo.app.config.LangBundles;
import com.github.lfyuomr_gylo.app.controller.translator.TranslationManager;
import com.github.lfyuomr_gylo.app.model.translator.Case;
import com.github.lfyuomr_gylo.app.model.translator.Meaning;
import com.github.lfyuomr_gylo.app.model.translator.Translation;
import io.reactivex.subjects.BehaviorSubject;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.github.lfyuomr_gylo.app.config.DefaultConstants.*;
import static com.github.lfyuomr_gylo.app.config.LangBundles.getTranslationDialogBundleObservable;
import static com.github.lfyuomr_gylo.app.util.Asserts.assertNotNull;
import static com.github.lfyuomr_gylo.app.util.PlatformWrapper.onFX;
import static javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE;
import static javafx.scene.control.ButtonBar.ButtonData.OTHER;

public class TranslationDialog extends Dialog<TranslationDialog.Choice> {
    /**
     * When class loaded for the first time, Hot Spot retards so for first translation timeout exception occurs while
     * waiting for translation service response. Therefore
     * {@link TranslationDialog#processTranslationError(Throwable) error handler} instead of
     * {@link TranslationDialog#setTranslation(Translation)} is called.
     * To fix this bug for the first translation timeout is multiplied by 5 and this field indicates whether that
     * occasion already happened or not.
     */
    private static boolean firstClassLoad = true;

    private final BehaviorSubject<String> inputText = BehaviorSubject.create();
    private final @NotNull Property<TranslationView> translationViewProperty = new SimpleObjectProperty<>(null);

    private final ButtonType cancelButton;
    private final ButtonType laterButton;
    private final ButtonType nowButton;

    public TranslationDialog() {
        this(null);
    }

    public TranslationDialog(@Nullable String text) {
        super();
        ((Stage) getDialogPane().getScene().getWindow()).setAlwaysOnTop(true); // to not be hidden in second
        configureSize();

        initHeader(text == null ? "" : text);

        nowButton = addButtonType("createCardNowButton", true, false, false);
        laterButton = addButtonType("createCardLaterButton", true, false, false);
        cancelButton = addButtonType("cancelButton", false, true, true);
        setResultConverter(this::convertResult);

        if (text != null) {
            processInput(text);
        }

        inputText.debounce(INPUT_DEBOUNCE_TIMEOUT, INPUT_DEBOUNCE_TIME_UNIT)
                 .subscribe(onFX(this::processInput));

    }

    private void configureSize() {
        setResizable(true);
        getDialogPane().setPrefSize(400, 300);
    }

    /**
     * This method is responsible for appearance of this dialog's {@link DialogPane#content content}. Currently it adds
     * to content a label with a proposal to create card.
     * <p>
     * <b>Note:</b> only this method should call {@link DialogPane#setContent(Node) getDialogPane().setContent()}
     * </p>
     *
     * @param content meaningful dialog content.
     * @throws ClassCastException if {@code content} is not {@link TranslationView} or {@link ProgressIndicator} instance.
     */
    private void setDialogPaneContent(@Nullable Node content) {
        if (content == null) {
            getDialogPane().setContent(null);
            return;
        }

        final Node dialogPaneContent = getDialogPane().getContent();
        if (dialogPaneContent != null && dialogPaneContent instanceof VBox) {
            ((VBox) dialogPaneContent).getChildren().set(0, content);
        }
        else {
            final String proposal = getTranslationDialogBundleObservable().blockingFirst()
                                                                          .getString("cardCreationProposal");
            final Label proposalLabel = new Label(proposal);
            final VBox actualContent = new VBox(10, content, proposalLabel);
            actualContent.setAlignment(Pos.CENTER);
            getDialogPane().setContent(actualContent);
        }
    }

    /**
     * Configure header text input and sign {@link TranslationDialog#inputText} on input text updates.
     *
     * @param text initial value of text input
     */
    private void initHeader(@NotNull String text) {
        final TextField header = new TextField(text);
        header.setAlignment(Pos.CENTER);
        getTranslationDialogBundleObservable().map(bundle -> bundle.getString("inputPrompt"))
                                              .subscribe(onFX(header::setPromptText));

        header.textProperty().addListener((observable, oldValue, newValue) -> inputText.onNext(newValue));

        getDialogPane().setHeader(header);
    }

    private void processInput(@NotNull String input) {
        setTranslation(null);
        TranslationManager.translate(input) //this multiplication described in firstClassLoad javadoc
                          .timeout(MAX_TRANSLATION_TIME * (firstClassLoad ? 5 : 1), MAX_TRANSLATION_TIME_UNIT)
                          .subscribe(onFX(this::setTranslation), onFX(this::processTranslationError));
        firstClassLoad = false;
    }

    /**
     * Set {@link TranslationDialog#translationViewProperty} and update dialog content to graphical
     * representation of translation.
     * <b>Note:</b> only this method should be used to set content.
     *
     * @param translation translation value
     */
    private void setTranslation(@Nullable Translation translation) {
        if (translation == null) {
            final ProgressIndicator progressIndicator = new ProgressIndicator();
            setDialogPaneContent(progressIndicator);
            translationViewProperty.setValue(null);
            return;
        }

        final TranslationView translationView = TranslationView.createTranslationView(translation);
        translationViewProperty.setValue(translationView);
        setDialogPaneContent(translationView);
        if (translationView == null) {
            final LangBundles.LanguageBundle languageBundle = getTranslationDialogBundleObservable().blockingFirst();
            setContentText(languageBundle.getString("NoTranslationFound"));
        }
    }

    /**
     * Set dialog content to translationViewProperty error message.
     *
     * @param error error occurred in {@link TranslationManager#translate(String)}
     */
    private void processTranslationError(@NotNull Throwable error) {
        getDialogPane().setGraphic(null);
        ((TextField) getDialogPane().getHeader()).setText("");
        setDialogPaneContent(null);
        getDialogPane().setExpandableContent(null);

        final LangBundles.LanguageBundle languageBundle = getTranslationDialogBundleObservable().blockingFirst();
        setContentText(languageBundle.getString("TranslationErrorMessage"));
    }

    /**
     * Create buttonType with specified name and add this button to {@link TranslationDialog#dialogPane dialogPane}'s
     * {@link javafx.scene.control.DialogPane#buttons button list}
     *
     * @param buttonName         name of button's text in {@link LangBundles#translationDialogBundleObservable resource bundle}
     * @param signToSelectedCase if set to {@code true}, make this button disabled always when no {@link Case case} in
     *                           {@link TranslationDialog#translationViewProperty translation view} is selected.
     */
    private @NotNull ButtonType addButtonType(
            @NotNull String buttonName,
            boolean signToSelectedCase,
            boolean isDefault,
            boolean isCancel) {
        final String buttonText = getTranslationDialogBundleObservable().blockingFirst().getString(buttonName);
        final ButtonBar.ButtonData buttonData = isCancel ? CANCEL_CLOSE : OTHER;
        final ButtonType buttonType = new ButtonType(buttonText, buttonData);
        getDialogPane().getButtonTypes().add(buttonType);
        ((Button) getDialogPane().lookupButton(buttonType)).setDefaultButton(isDefault);

        if (signToSelectedCase) {
            final Node buttonRepresentation = getDialogPane().lookupButton(buttonType);
            final ChangeListener<Case> selectionListener = (observable, oldValue, newValue) ->
                    buttonRepresentation.setDisable(newValue == null);

            buttonRepresentation.setDisable(getSelectedCase() == null);
            translationViewProperty.addListener((observable, oldValue, newValue) -> {
                if (oldValue != null) {
                    oldValue.getSelectionModel().selectedItemProperty().removeListener(selectionListener);
                }
                if (newValue == null) {
                    buttonRepresentation.setDisable(true);
                }
                else {
                    buttonRepresentation.setDisable(newValue.getSelectionModel().getSelectedItem() == null);
                    newValue.getSelectionModel().selectedItemProperty().addListener(selectionListener);
                }
            });
        }
        return buttonType;
    }

    private @Nullable Choice convertResult(ButtonType clickedButton) {
        if (clickedButton == cancelButton) {
            return null;
        }
        else if (clickedButton == nowButton || clickedButton == laterButton) {
            assertNotNull(getSelectedCase());
            return new Choice(
                    clickedButton == nowButton,
                    translationViewProperty.getValue().getTranslation().getText(),
                    getSelectedCase().getMeaning()
            );
        }
        else {
            throw new IllegalStateException("Illegal value of 'clickedButton'.");
        }
    }

    private @Nullable Case getSelectedCase() {
        return (translationViewProperty.getValue() == null) ? null :
                translationViewProperty.getValue().getSelectionModel().getSelectedItem();
    }

    @SuppressWarnings("WeakerAccess")
    public static class Choice {
        public final boolean createNow;
        public final String text;
        public final Meaning meaning;

        Choice(boolean createNow, String text, Meaning meaning) {
            this.createNow = createNow;
            this.text = text;
            this.meaning = meaning;
        }

        @Override
        public String toString() {
            return "Choice:\n" +
                    "createNow: " + createNow + ",\n" +
                    "text: \"" + text + "\",\n" +
                    "meaning: " + meaning.getDescription();
        }
    }
}
