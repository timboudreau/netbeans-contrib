/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.a11y;

import java.awt.*;
import javax.swing.*;
import javax.accessibility.*;

/**
 * AccComponent is used only for internal presentation of components after test finished,
 * for AWT-tree. After each seelction of thic component Panel of parameters is refreshed.
 * 
 * @author  Marian.Mirilovic@Sun.Com
 */
public class AccComponent extends Object{
    
    public final String YES = "YES";
    public final String NO = "NO";
    public final String NO_MNEMONIC = "no mnemonic";
    public final String NO_CHILDREN = "no children";
    public final String NULL = "null";
    
    public String implementAccessible;
    public String accessibleName;
    public String accessibleDescription;
    
    public String componentLabelFor;
    public String mnemonic;
    public String layout;
    public String childrens;
    
    public String enabled;
    public String visible;
    public String showing;
    public String focusable;
    
    private String className;
    
    /** Creates new AccComponent */
    public AccComponent(Component component) {
        Class cl = component.getClass();
        if(cl != null)
            className = cl.getName();
        else
            className = NULL;
        
        if (component instanceof Accessible){
            implementAccessible = YES;
            
            AccessibleContext ac = component.getAccessibleContext();
            if (ac != null){
                accessibleName = ac.getAccessibleName();
                accessibleDescription = ac.getAccessibleDescription();
            }else{
                accessibleName = null;
                accessibleDescription = null;
            }
        }else {
            implementAccessible = NO;
            accessibleName = null;
            accessibleDescription = null;
        }
        
        if (component instanceof JLabel){
            Component componentLabelF = ((JLabel)component).getLabelFor();
            if(componentLabelF != null) {
                componentLabelFor = componentLabelF.getClass().getName();
                mnemonic = makeString(((JLabel)component).getDisplayedMnemonic(), true);
            }else {
                componentLabelFor = " ";
                mnemonic = " ";
            }
        } else if(component instanceof AbstractButton) {
            AbstractButton button = (AbstractButton)component;
            mnemonic = makeString(button.getMnemonic(), true);
        } else {
            componentLabelFor = " ";
            mnemonic = " ";
        }
        
        
        showing = " " + component.isShowing();
        focusable = " " + component.isFocusTraversable();
        enabled = " " + component.isEnabled();
        visible = " " + component.isVisible();
        
        
        if(component instanceof Container) {
            Container cont = (Container) component;
            
            if(cont.getLayout()!=null){
                if(cont.getLayout() != null)
                    layout = cont.getLayout().getClass().getName();
                else
                    layout = null;
            }else
                layout = null;
            
            childrens = makeString(cont.getComponents().length, false);
        }else {
            childrens = " ";
            layout = " ";
        }
    }
    
    private String makeString(int number,boolean is_mnemonic) {
        StringBuffer text;
        String text_good, text_bad;
        
        if(is_mnemonic){
            text_good = new String(" '"+(char)number+"'");
            text_bad = NO_MNEMONIC;
            text = new StringBuffer(NO_MNEMONIC.length());
        }else{
            text_good = new String(" "+number);
            text_bad = NO_CHILDREN;
            text = new StringBuffer(NO_CHILDREN.length());
        }
        
        if(number>0){
            text.replace(0,text_good.length(),text_good);
            return text.toString();
        } else
            return text_bad;
    }

    public String toString() {
        return className;
    }
    
}



