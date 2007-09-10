/*
 * ToolbarProvider.java
 * 
 * Created on Sep 8, 2007, 10:07:02 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.perspective.utils;

import java.awt.Component;
import org.netbeans.modules.perspective.ui.ToolbarStyleSwitchUI;
import org.openide.awt.StatusLineElementProvider;

/**
 *
 * @author Anurdha
 */
public class ToolbarProvider implements StatusLineElementProvider{

    public Component getStatusLineElement() {
        return ToolbarStyleSwitchUI.getInstance();
    }

}
