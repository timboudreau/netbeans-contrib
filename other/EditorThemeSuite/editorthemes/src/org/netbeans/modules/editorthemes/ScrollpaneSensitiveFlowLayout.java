/*
 * AutoGridLayout.java
 * 
 * Created on Jul 2, 2007, 1:09:13 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.editorthemes;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author Tim Boudreau
 */
public class ScrollpaneSensitiveFlowLayout implements LayoutManager {

    public ScrollpaneSensitiveFlowLayout() {
    }

    public void addLayoutComponent(String arg0, Component arg1) {
    }

    public void removeLayoutComponent(Component arg0) {
    }

    public Dimension preferredLayoutSize(Container c) {
        Dimension result = new Dimension();
        int availWidth;
        if (c.isDisplayable()) {
            JScrollPane scroll = (JScrollPane) 
                    SwingUtilities.getAncestorOfClass(JScrollPane.class, c);
            if (scroll != null) {
                availWidth = scroll.getVisibleRect().width;
            } else {
                availWidth = c.getWidth();
            }
            int maxHeight = Integer.MIN_VALUE;
            int totalWidth = 0;
            Component[] comps = c.getComponents();
            if (comps.length > 0) {
                int[] widths = new int[comps.length];
                for (int i=0; i < comps.length; i++) {
                    if (!comps[i].isVisible()) continue;
                    Dimension d = comps[i].getPreferredSize();
                    maxHeight = Math.max (maxHeight, d.height);
                    widths[i] = d.width;
                    totalWidth += widths[i];
                }
                if (totalWidth <= availWidth) {
                    result.width = totalWidth;
                    result.height = maxHeight;
                } else if (availWidth > 0) {
                    int rows = (totalWidth / availWidth) + 1;
                    result.width = availWidth;
                    result.height = maxHeight * rows;
                }
            }
        }
        Insets ins = c.getInsets();
        result.width += ins.left + ins.right;
        result.height += ins.top + ins.bottom;
        return result;
    }

    public Dimension minimumLayoutSize(Container c) {
        return preferredLayoutSize(c);
    }

    public void layoutContainer(Container c) {
        Dimension size = new Dimension();
        int availWidth;
        Insets ins = c.getInsets();
        if (c.isDisplayable()) {
            JScrollPane scroll = (JScrollPane) 
                    SwingUtilities.getAncestorOfClass(JScrollPane.class, c);
            if (scroll != null) {
                availWidth = scroll.getVisibleRect().width - (ins.left + ins.right);
            } else {
                availWidth = c.getWidth() - (ins.left + ins.right);
            }
            int maxHeight = Integer.MIN_VALUE;
            int totalWidth = ins.left + ins.right;
            Component[] comps = c.getComponents();
            if (comps.length > 0) {
                int[] widths = new int[comps.length];
                int visibleCompCount = 1;
                for (int i=0; i < comps.length; i++) {
                    if (!comps[i].isVisible()) continue;
                    visibleCompCount ++;
                    Dimension d = comps[i].getPreferredSize();
                    maxHeight = Math.max (maxHeight, d.height);
                    widths[i] = d.width;
                    totalWidth += widths[i];
                }
                if (totalWidth <= availWidth) {
                    size.width = totalWidth;
                    size.height = maxHeight;
                    int space = (availWidth - totalWidth) / visibleCompCount;
                    int x = ins.left;
                    int y = ins.top;
                    for (int i=0; i < comps.length; i++) {
                        if (!comps[i].isVisible()) continue;
                        Dimension d = comps[i].getPreferredSize();
                        comps[i].setBounds (x, y, d.width, maxHeight);
                        x += d.width + space;
                    }
                } else {
                    int rows = (totalWidth / availWidth) + 1;
                    size.width = availWidth;
                    size.height = maxHeight * rows;
                    int x = ins.left;
                    int y = ins.top;
                    for (int i=0; i < comps.length; i++) {
                        if (!comps[i].isVisible()) continue;
                        Dimension d = comps[i].getPreferredSize();
                        if (x + d.width > availWidth) {
                            y += maxHeight;
                            x = ins.left;
                        }
                        comps[i].setBounds (x, y, d.width, maxHeight);
                        x += d.width;
                    }
                }
            }
        }
    }
}
