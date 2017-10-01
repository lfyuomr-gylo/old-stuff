package com.github.lfyuomr_gylo.app.view.translator;

import com.github.lfyuomr_gylo.app.model.translator.Case;
import com.github.lfyuomr_gylo.app.model.translator.Translation;
import javafx.scene.control.ListView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A graphical representation of {@link Translation} for {@link TranslationDialog}.
 */
class TranslationView extends ListView<Case> {
    private final @NotNull Translation translation;
    /**
     * Creates graphical representation of translation.
     *
     * @param translation to be graphically represented.
     * @return created representation or {@code null} if {@code translation} contains no cases
     */
    static @Nullable TranslationView createTranslationView(@NotNull Translation translation) {
        return translation.getCases().isEmpty() ? null : new TranslationView(translation);
    }

    private TranslationView(@NotNull Translation translation) {
        super();
        this.translation = translation;

        getItems().addAll(translation.getCases());

        setCellFactory(list -> new CaseView());
    }

    public @NotNull Translation getTranslation() {
        return translation;
    }
}
