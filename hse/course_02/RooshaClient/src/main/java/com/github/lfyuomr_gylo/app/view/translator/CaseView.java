package com.github.lfyuomr_gylo.app.view.translator;

import com.github.lfyuomr_gylo.app.model.translator.Case;
import com.github.lfyuomr_gylo.app.model.translator.Meaning;
import javafx.scene.control.ListCell;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.jetbrains.annotations.NotNull;

import static javafx.scene.text.Font.font;
import static javafx.scene.text.FontPosture.ITALIC;

/**
 * Graphical representation of {@link Case} for {@link TranslationView} usage.
 */
class CaseView extends ListCell<Case> {
    @Override
    protected void updateItem(Case item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
            return;
        }

        setGraphic(renderCase(item));
    }

    private static TextFlow renderCase(@NotNull Case item) {
        final Meaning meaning = item.getMeaning();
        final Text meanings = new Text(meaning.getMeaning() + "\n");
        final Text description = new Text(meaning.getDescription() + "\n");
        description.setFont(font(null, ITALIC, -1));
        final Text usage = new Text(meaning.getDescription() + "\n");

        return new TextFlow(meanings, description, usage);
    }
}
