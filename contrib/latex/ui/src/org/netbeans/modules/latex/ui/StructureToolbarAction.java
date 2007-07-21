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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2007.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.netbeans.modules.latex.model.command.CommandNode;
import org.netbeans.modules.latex.model.command.Node;
import org.netbeans.modules.latex.ui.ToolbarEnvironmentAction.EnvironmentDescription;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.util.HelpCtx;
import org.openide.util.actions.Presenter;

/**
 *
 * @author Jan Lahoda
 */
public class StructureToolbarAction extends    ToolbarCommandAction
                                    implements ToolbarStatusChangeListener,
                                               Presenter.Toolbar,
                                               HelpCtx.Provider {
    
    private static final boolean debug = Boolean.getBoolean("netbeans.latex.toolbar.status.change");
    
    protected CommandDescription findCorrespondingCommandDescription(CommandNode node) {
        if (node == null)
            return getNormal();
        
        CommandNode  cn = (CommandNode) node;
        String       command = cn.getCommand().getCommand();
        
        for (Iterator i = getDescriptions().iterator(); i.hasNext(); ) {
            Object item = i.next();
            
            if (!(item instanceof CommandDescription))
                continue;
            
            CommandDescription d = (CommandDescription) item;
            
            if (!(d instanceof CommandDescription))
                continue;
            
            if (command.equals(d.getCommand()))
                return d;
        }
        
        return getNormal();
    }
    
    private CommandDescription getNormal() {
        return (CommandDescription) getDescriptions().get(normalIndex);
    }
    
    public void enableChange(final boolean enable) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getCombo().setEnabled(enable);
            }
        });
    }
    
    public void statusChange(Node currentNode) {
        if (debug)
            System.err.println("statusChange");
        
        CommandNode cn = findCorrespondingNode(currentNode);
        
        if (debug)
            System.err.println("cn = " + cn );
        
        CommandDescription cd = findCorrespondingCommandDescription(cn);
        
        if (debug)
            System.err.println("cd = " + cd );
        
        getCombo().setSelectedItem(cd);
    }
    
    private static final int FIXED_WITH = 150;
    
    private JComboBox combo;
    
    private static Map<String, List<Object>> type2Descriptions = null;
    
    private static synchronized Map getType2Descriptions() {
        if (type2Descriptions == null) {
            type2Descriptions = new HashMap();
            
            type2Descriptions.put("structure", 
                Arrays.asList(new Object[] {
                    new CommandDescription("Normal", "", true),
                    new JSeparator(),
                    new CommandDescription("1. Part", "\\part", false),
                    new CommandDescription("1. Chapter", "\\chapter", false),
                    new CommandDescription("1. Section", "\\section", false),
                    new CommandDescription("1. Subsection", "\\subsection", false),
                    new CommandDescription("1. Subsubsection", "\\subsubsection", false),
                    new CommandDescription("1. Paragraph", "\\paragraph", false),
                    new CommandDescription("1. Subparagraph", "\\subparagraph", false),
                    new JSeparator(),
                    new CommandDescription("   Part", "\\part*", false),
                    new CommandDescription("   Chapter", "\\chapter*", false),
                    new CommandDescription("   Section", "\\section*", false),
                    new CommandDescription("   Subsection", "\\subsection*", false),
                    new CommandDescription("   Subsubsection", "\\subsubsection*", false),
                    new CommandDescription("   Paragraph", "\\paragraph*", false),
                    new CommandDescription("   Subparagraph", "\\subparagraph*", false),
                }
            ));
            type2Descriptions.put("font",
                Arrays.asList(new Object[] {
                    new CommandDescription("Normal", "", true),
                    new JSeparator(),
                    new CommandDescription("tiny","\\tiny", false),
                    new CommandDescription("scriptsize", "\\scriptsize", false),
                    new CommandDescription("footnotesize", "\\footnotesize", false),
                    new CommandDescription("small", "\\small", false),
                    new CommandDescription("normalsize", "\\normalsize", false),
                    new CommandDescription("large","\\large", false),
                    new CommandDescription("Large","\\Large", false),
                    new CommandDescription("LARGE","\\LARGE", false),
                    new CommandDescription("huge","\\huge", false),
                    new CommandDescription("Huge","\\Huge", false),
                }
            ));

        }
        
        return type2Descriptions;
////            "Footnote",
////            "Enumeration",
////            "List",
////            "Description"
    }
    
    public static StructureToolbarAction createInstance(FileObject fo) {
        String type = (String) fo.getAttribute("type");
        
        if (type == null) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, new IllegalArgumentException("StructureToolbarAction: type not defined: " + fo));
            return null;
        }
            
        List descriptions = (List) getType2Descriptions().get(type);
        
        if (descriptions == null) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, new IllegalArgumentException("StructureToolbarAction: type not known: " + type + ", file=" + fo));
            return null;
        }
        
        String name = (String) fo.getAttribute("name");

        if (type == null) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, new IllegalArgumentException("StructureToolbarAction: name not defined: " + fo));
            return null;
        }

        return new StructureToolbarAction(name, descriptions);
    }

    /** Creates a new instance of StructureToolbarAction */
    private StructureToolbarAction(String name, List<Object> descriptions) {
        this.descriptions = descriptions;
        
        putValue(NAME, name);
        
        combo = new JIgnoringComboBox(new DefaultComboBoxModel(descriptions.toArray()));
        combo.addPopupMenuListener(new PopupMenuListener() {
            private Object saved = null;
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
//                System.err.println("popupMenuWillBecomeInvisible: " + e);
                CommandDescription cd = (CommandDescription) getCombo().getSelectedItem();
                doUpdate(cd);
            }
            public void popupMenuCanceled(PopupMenuEvent e) {
//                System.err.println("popupMenuCanceled: " + e);
                if (saved != null) {
                    combo.setSelectedItem(saved);
                    saved = null;
                }
            }
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                saved = combo.getSelectedItem();
            }
        });

        combo.setRenderer(new SeparatorListCellRenderer());
        
        Dimension dim = combo.getPreferredSize();
        Dimension prefered = new Dimension(FIXED_WITH, (int) dim.getHeight());
        
        combo.setPreferredSize(prefered);
        combo.setMinimumSize(prefered);
        combo.setMaximumSize(prefered);

        ToolbarUpdater.addToolbarStatusChangeListener(this);
        
        for (Object o : descriptions) {
            if (o instanceof EnvironmentDescription) {
                ToolbarUpdater.addToUpdate((EnvironmentDescription) o);
            }
        }
    }
    
    protected JComboBox getCombo() {
        return combo;
    }
    
    public synchronized Component getToolbarPresenter() {
        return combo;
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    private List<Object> descriptions = null;
    private int normalIndex = 0;

    public synchronized List getDescriptions() {
        return descriptions;
    }
    
    public void actionPerformed(ActionEvent e) {
    }
    
    public static class SeparatorListCellRenderer extends DefaultListCellRenderer {

        public Component getListCellRendererComponent(JList list,
                                                      Object value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
            if (value instanceof JSeparator) {
                return (Component) value;
            } else {
                Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                if (value instanceof CommandDescription && !((CommandDescription) value).isEnabled()) {
                    comp.setForeground(UIManager.getColor("Label.disabledForeground"));
                }
                
                return comp;
            }
        }
    }
    
    public static class JIgnoringComboBox extends JComboBox {
        
        public JIgnoringComboBox(ComboBoxModel model) {
            super(model);
        }
        
        private boolean shouldBeIgnored(Object item) {
            return !(item instanceof CommandDescription) || !((CommandDescription) item).isEnabled();
        }
        
        private Object getNext(Object obj) {
            //find item:
            int index = 0;
            
            while (getModel().getElementAt(index) != obj && index < getModel().getSize()) {
                index++;
            }
            
            if (index >= getModel().getSize())
                return null; //not found...
            
            int delta = index - getSelectedIndex() > 0 ? 1 : (-1);

            while (shouldBeIgnored(getModel().getElementAt(index)) && index < getModel().getSize() && index >= 0)
                index += delta;
            
            if (index >= getModel().getSize() || index < 0)
                return null;
            else
                return getModel().getElementAt(index);
        }
        
        public void setSelectedItem(Object item) {
            if (shouldBeIgnored(item)) {
                item = getNext(item);
                //TODO:
            }
            
            if (item != null)
                super.setSelectedItem(item);
        }
        
    }
    
}
