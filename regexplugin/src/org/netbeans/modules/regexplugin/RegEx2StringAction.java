package org.netbeans.modules.regexplugin;

import java.awt.event.ActionEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.text.JTextComponent;
 
/**
 *
 * Action class to provide an action to convert a regular expression to a java string.
 *
 * @author Angad
 */
class RegEx2StringAction extends AbstractAction
{
    /**
     * Icon to displayed against this action.
     */
//    static final private ImageIcon icon =
//            new ImageIcon(ClassLoader.getSystemResource("images/arrow_right.png"));
    
    /**
     * The component the action is associated with.
     */
    protected JTextComponent comp;
    
    /**
     * Default constructor.
     * @param comp The component the action is associated with.
     */
    public RegEx2StringAction(JTextComponent comp, ImageIcon icon)
    {
        super("Convert Regular Expression to Java String" ,icon);
        this.comp = comp;
    }
    
    /**
     * Action has been performed on the component.
     * @param e ignored
     */
    public void actionPerformed(ActionEvent e)
    {
        comp.setText(comp.getText().replaceAll(Pattern.quote("\\"), Matcher.quoteReplacement("\\\\")).replaceAll(Pattern.quote("\""), Matcher.quoteReplacement("\\\"")));                                            
    }
    
    /**
     * Checks if the action can be performed.
     * @return True if the action is allowed
     */
    @Override
    public boolean isEnabled()
    {
        return comp.isEnabled()
        && comp.getText().length()>0;
    }
}