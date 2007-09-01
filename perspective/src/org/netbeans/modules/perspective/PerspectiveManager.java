/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

/*
 * PerspectiveManager.java
 */

package org.netbeans.modules.perspective;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.netbeans.modules.perspective.actions.SaveAsAction;
import org.netbeans.modules.perspective.actions.SwitchAction;
import org.netbeans.modules.perspective.persistence.PerspectivePreferences;
import org.netbeans.modules.perspective.utils.OpenedViewTracker;
import org.netbeans.modules.perspective.views.Perspective;

/**
 *
 * @author Anuradha G
 */
public class PerspectiveManager {

    private static PerspectiveManager instance;
    private JPopupMenu menu = new JPopupMenu();
    private JMenuItem saveAs;
    private List<Perspective> perspectives = new ArrayList<Perspective>();
    private Map<Perspective, JMenuItem> menuMap = new ConcurrentHashMap<Perspective, JMenuItem>();
    private ButtonGroup group = new ButtonGroup();
    private Perspective selected;
    private JButton toolbarButton;

    public static synchronized PerspectiveManager getInstance() {

        if (instance == null) {
            instance = new PerspectiveManager();
        }

        return instance;
    }

    public List<Perspective> getPerspectives() {

        return Collections.unmodifiableList(perspectives);
    }

    private PerspectiveManager() {

        menu.addPopupMenuListener(new PopupMenuListener() {

            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                if (toolbarButton != null) {
                    toolbarButton.setSelected(true);
                }
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                if (toolbarButton != null) {
                    toolbarButton.setSelected(false);
                }
            }

            public void popupMenuCanceled(PopupMenuEvent e) {
                if (toolbarButton != null) {
                    toolbarButton.setSelected(false);
                }
            }
        });
        saveAs = new JMenuItem(new SaveAsAction());
        refresh();
    }

    public void registerPerspective(int index, Perspective perspective) {
        if (perspectives.size() <= index) {
            perspectives.add(perspective);
        } else {
            perspectives.add(index, perspective);
        }
        final JCheckBoxMenuItem item = new JCheckBoxMenuItem(new SwitchAction(perspective));
        group.add(item);
        menuMap.put(perspective, item);
        arrangeIndexs();
        refresh();
    }

    public void registerPerspective(Perspective perspective, boolean arrange) {
        if (perspectives.size() > perspective.getIndex()) {
            perspectives.add(perspective.getIndex(), perspective);
        } else {
            perspectives.add(perspective);
        }

        final JCheckBoxMenuItem item = new JCheckBoxMenuItem(new SwitchAction(perspective));
        group.add(item);
        menuMap.put(perspective, item);
        if (arrange) {
            arrangeIndexs();
            refresh();
        }
    }

    public void deregisterPerspective(Perspective perspective) {
        perspectives.remove(perspective);
        group.remove(menuMap.get(perspective));
        menuMap.remove(perspective);
        arrangeIndexs();
        refresh();
    }

    public JPopupMenu getMenu() {
        return menu;
    }

    public void refresh() {
        menu.removeAll();
        for (Perspective perspective : perspectives) {
            if (perspective.isBeforeSeparator()) {
                menu.addSeparator();
            }
            menu.add(menuMap.get(perspective));
            if (perspective.isAfterSeparator()) {
                menu.addSeparator();
            }
        }
        if (menu.getComponentCount() != 0) {
            menu.addSeparator();
        }
        menu.add(saveAs);
    }


    public void setSelected(Perspective perspective) {
        if(selected!=null){
            selected.notifyClosing();
            if(PerspectivePreferences.getInstance().isTrackOpened()){
                new OpenedViewTracker(perspective);
            }
        }
        perspective.notifyOpening();
        selected = perspective;
        group.setSelected(menuMap.get(perspective).getModel(), true);
    }

    public Perspective getSelected() {
        return selected;
    }

    public Perspective findPerspectiveByID(String id) {
        for (Perspective perspective : perspectives) {
            if (perspective.getName().equals(id)) {
                return perspective;
            }
        }
        return null;
    }

    public void arrangeIndexs() {
        for (Perspective perspective : perspectives) {
            perspective.setIndex(perspectives.indexOf(perspective));
        }
        Collections.sort(perspectives);
    }

    public void clear() {
        perspectives.clear();
        menuMap.clear();
        selected = null;
        refresh();
    }

    public JButton getToolbarButton() {
        return toolbarButton;
    }

    public void setToolbarButton(JButton toolbarButton) {
        this.toolbarButton = toolbarButton;
    }

    public void arrangeIndexsToExistIndexs() {
        Collections.sort(perspectives);
        for (Perspective perspective : perspectives) {
            perspective.setIndex(perspectives.indexOf(perspective));
        }
        Collections.sort(perspectives);
    }
}