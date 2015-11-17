package org.netbeans.modules.php.opencart.editor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.modules.php.opencart.OpenCart;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;
import org.openide.util.Exceptions;

public class OpenCartCompletionItem implements CompletionItem {

    private static Color fieldColor = Color.decode("0x8304D7"); // NOI18N
    private static Color createNewColor = Color.decode("0x8304D7"); // NOI18N
    private String text;
    private int startOffset;
    private int removeLength;
    private boolean isCtrlDown = false;
    private boolean isExist = true;

    OpenCartCompletionItem(String text, int startOffset, int removeLength) {
        this.text = text;
        this.startOffset = startOffset;
        this.removeLength = removeLength;
    }

    OpenCartCompletionItem(String text, int startOffset, int removeLength, boolean isExist) {
        this(text, startOffset, removeLength);
        this.isExist = isExist;
    }

    @Override
    public void defaultAction(JTextComponent jtc) {
        try {
            StyledDocument doc = (StyledDocument) jtc.getDocument();
            doc.remove(startOffset, removeLength);
            doc.insertString(startOffset, text, null);
            Completion.get().hideAll();
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void processKeyEvent(KeyEvent ke) {
        isCtrlDown = ke.isControlDown();
    }

    @Override
    public int getPreferredWidth(Graphics grphcs, Font font) {
        return CompletionUtilities.getPreferredWidth(getLeftText(), getRightText(), grphcs, font);
    }

    @Override
    public void render(Graphics grphcs, Font font, Color color, Color color1, int width, int height, boolean selected) {
        CompletionUtilities.renderHtml(OpenCart.IMAGE_ICON_16, getLeftText(), getRightText(), grphcs, font, (isExist ? (selected ? Color.white : fieldColor) : createNewColor), width, height, selected);
    }

    private String getLeftText() {
        return "<b>" + text + "</b>";
    }

    private String getRightText() {
        String rightText = null;
        if (!isExist) {
            rightText = ":new file[Ctrl+Enter]";
        }
        return rightText;
    }

    @Override
    public CompletionTask createDocumentationTask() {
        return null;
    }

    @Override
    public CompletionTask createToolTipTask() {
        return null;
    }

    @Override
    public boolean instantSubstitution(JTextComponent jtc) {
        return false;
    }

    @Override
    public int getSortPriority() {
        return 0;
    }

    @Override
    public CharSequence getSortText() {
        return text;
    }

    @Override
    public CharSequence getInsertPrefix() {
        return text;
    }

    public String getText() {
        return text;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public int getRemoveLength() {
        return removeLength;
    }

    public boolean isIsCtrlDown() {
        return isCtrlDown;
    }
}
