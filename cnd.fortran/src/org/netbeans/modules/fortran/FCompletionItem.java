/*
 * FCompletionItem.java
 *
 * Created on July 15, 2007, 11:40 PM
 *
 */

package org.netbeans.modules.fortran;
 
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;

import org.openide.ErrorManager;


/**
 * The class  representing a single item of completion popup.
 * @author Andrey Gubichev
 */
public class FCompletionItem implements CompletionItem{
    private static Color fieldColor = Color.decode("0x0000B2");
    private ImageIcon  _icon;
    private int _type;
    private int _carretOffset;
    private int _dotOffset;
    private String _text;

    public FCompletionItem(String text, int dotOffset, int carretOffset) {
        _text = text;
        _dotOffset = dotOffset;
        _carretOffset = carretOffset;
        _icon = null;
    }
    
    
    /**
     * Process the key pressed when this completion item was selected.
     */
    public void processKeyEvent(KeyEvent keyEvent) {
    }

    /**
     * @return preferred visual width
     */
    public int getPreferredWidth(Graphics graphics, Font font) {
        return CompletionUtilities.getPreferredWidth(_text, null, graphics, font);
    }

    /**
     * Render this item into the given graphics
     */
    public void render(Graphics g, Font defaultFont, Color defaultColor,
        Color backgroundColor, int width, int height, boolean selected) {
        CompletionUtilities.renderHtml(_icon, _text, null, g, defaultFont,
            (selected ? Color.white : fieldColor), width, height, selected);
    }
    
    
    private void doSubstitute(final JTextComponent component, final String toAdd, final int backOffset) {
          final StyledDocument doc = (StyledDocument)component.getDocument();
            int caretOffset = component.getCaretPosition();
            String value = _text;
            if (toAdd != null) {
                value += toAdd;
            }
            try {
                doc.remove(_dotOffset, _carretOffset-_dotOffset);
                doc.insertString(_dotOffset, value , null);
                component.setCaretPosition(component.getCaretPosition() - backOffset);
            } catch (BadLocationException e) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
            }
     }

    /**
      * on enter key pressed or mouse clicked
      */
    public void defaultAction(JTextComponent jTextComponent) {
       Completion.get().hideAll();
       doSubstitute(jTextComponent, null, 0);
    }
   

    public CompletionTask createDocumentationTask() {
        return null;
    }

    public CompletionTask createToolTipTask() {
        return null;
    }

    public boolean instantSubstitution(JTextComponent jTextComponent) {
        return false;
    }

    /**
     * 
     * @return the item's priority
     */
     public int getSortPriority() {
         return 0;
     }

    /**
     * 
     * @return text used to sort items alphabetically
     */
     public CharSequence getSortText() {
         return _text;
     }

    /**
     * 
     * @return text used for finding of a longest common prefix 
     */
     public CharSequence getInsertPrefix() {
         return _text;
     }
}
