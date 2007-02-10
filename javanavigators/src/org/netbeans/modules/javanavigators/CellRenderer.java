/*
 * CellRenderer.java
 *
 * Created on February 10, 2007, 12:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.javanavigators;

import java.awt.Component;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.openide.awt.HtmlRenderer;

/**
 *
 * @author Tim
 */
public class CellRenderer implements ListCellRenderer {
    private final HtmlRenderer.Renderer htmlRenderer = 
            HtmlRenderer.createRenderer();
    
    public CellRenderer() {
    }
    
    public Component getListCellRendererComponent(JList arg0, Object arg1,
                                                  int arg2, boolean arg3,
                                                  boolean arg4) {
        Component result = htmlRenderer.getListCellRendererComponent(arg0, 
                arg1, arg2, arg3, arg4);
        if (arg1 instanceof Description) {
            Description d = (Description) arg1;
            htmlRenderer.setIcon(d.icon);
        }
        return result;
    }

}
