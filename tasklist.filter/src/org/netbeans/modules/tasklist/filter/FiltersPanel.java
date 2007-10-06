/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.tasklist.filter;

import java.awt.BorderLayout;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JComponent;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import org.openide.awt.Mnemonics;

/**
 * Panel with a list of filters.
 *
 * @author  or141057
 */
public class FiltersPanel extends javax.swing.JPanel implements java.awt.event.ActionListener {

    /** Name of client property holding name of selected filter */
    public static final String SELECTED_FILTER = "fp-selected-filter";

    private HashMap panels = new HashMap(10); // Filter.ListModelElement -> FilterPanel
    private FilteredTopComponent view; // initialized in constructor

    /** Reference to orginal filterRepository this dialog act upon.
     * It is not changed until ok or apply is pressed */
    private FilterRepository filterRepository; 

    /**
     * Contains temporary data (cloned filters) for the list and also selection 
     * model for the list. 
     */
    private FilterModel filterModel;
    
    /** 
     * Creates new form FiltersPanel 
     *
     * @param view a filtered TC
     */
    public FiltersPanel(FilteredTopComponent view) {
        this.view = view;
        this.filterRepository = view.getFilters();
        this.filterModel = new FilterModel(filterRepository);
        // init hash-map of panels
        Iterator it = filterModel.filters.iterator();
        while (it.hasNext()) { panels.put(it.next(), null);}
        
        myInitComponents();
    }
    
    private static class FilterModel extends AbstractListModel {

        public DefaultListSelectionModel selection = new DefaultListSelectionModel();
        public Vector filters;
        
        public FilterModel(FilterRepository rep) {
            filters = new Vector(rep.size() * 2);
            Iterator it = rep.iterator();
            int selectedi = 0;
            while (it.hasNext()) {
                Filter f = ((Filter)it.next());
                if (f == rep.getActive()) selection.setSelectionInterval(selectedi, selectedi);
                filters.add(f.clone());
                selectedi++;
            }
        }
        
        public Iterator iterator() {
            return filters.iterator();
        }
        
        public Object getElementAt(int index) {
            return ((Filter)filters.get(index)).getName();
        }
        
        public int getSize() {
            return filters.size();
        }

        public Filter getSelectedFilter() {
            if (getSelectedIndex() > -1) {
                return (Filter)filters.get(getSelectedIndex());
            } else 
                return null;
        }
        
        public int getSelectedIndex() {
	  int i1 = selection.getMinSelectionIndex(), i2 = selection.getMaxSelectionIndex();
	  if (i1 == i2 && i1 >= 0 && i1 < filters.size()) {
	    return i1; 
	  } else {
	    return -1;
	  }
        }
        
        public void remove(int i) {
            int s = getSelectedIndex();
	    if (s != -1) {
	      filters.remove(i);
	      fireIntervalRemoved(this, i, i);
            
	      if (i < s) { 
                selection.setSelectionInterval(s-1, s-1);
	      } if (i == s) {
                selection.setSelectionInterval(100,0);
	      }
	    }
        }
        
        public Filter get(int i ) {
            return (Filter)filters.get(i);
        }
        
        public boolean add(Filter f) {
            if (filters.add(f)) {
                fireIntervalAdded(this, filters.size()-1, filters.size()-1);
                return true;
            } else 
                return false;
        }
        
        public int getIndexOf(Filter f) {
            return filters.indexOf(f);
        }
    
        public void setFilterName(int i, String name) {
            if (get(i) != null) {
                get(i).setName(name);
                fireContentsChanged(this, i, i);
            }
        }
    }

    public FilterRepository getFilterRepository() {
        return filterRepository;
    }
    
    private void myInitComponents() {
        initComponents();

        // init filters-listbox model
        filtersList.setModel(filterModel);
        filtersList.setSelectionModel(filterModel.selection);
        filtersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // try to select just applied filter

        Object selected = null; 
        if (view instanceof JComponent) {
            selected = ((JComponent) view).getClientProperty(SELECTED_FILTER);
        }
        if (selected != null && selected instanceof String) {
            Iterator it = filterModel.iterator();
            int i = 0;
            while (it.hasNext()) {
                Filter filter = (Filter) it.next();
                if (selected.equals(filter.getName())) {
                    filtersList.setSelectedIndex(i);
                    break;
                }
                i++;
            }
        }
        
        if (filterModel.getSelectedIndex() == -1) {
            if (filterModel.getSize() > 0) {
                filtersList.setSelectedIndex(0);
            }
        }

        // hook list selection
        filtersList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
	      if (!e.getValueIsAdjusting()) {
		showFilter(filterModel.getSelectedFilter());
		DeleteButton.setEnabled(filterModel.getSelectedIndex() != -1);
	      }
            }
        });
        
        showFilter(filterModel.getSelectedFilter());
	DeleteButton.setEnabled(filterModel.getSelectedIndex() != -1);

        Mnemonics.setLocalizedText(filtersLabel, 
                                   org.openide.util.NbBundle.getMessage(FiltersPanel.class, "LBL_Filters")); // NOI18N

        
    }
    
    /**
     * Initializes the editor to the state when <filter> is selected in the list
     * and it is shown on the right side. It can be used to propagate values in 
     * both directions - from list to pane and opposite and also to both at once.
     */
    private void showFilter(final Filter filter) {
        FilterPanel panel = (FilterPanel)panels.get(filter);
        if (panel == null) {
            panel = new FilterPanel(view, filter);
            panels.put(filter, panel);
            panel.getFilterNameField().getDocument().addDocumentListener(
                new DocumentListener() {
		  public void changedUpdate(DocumentEvent e) { update(e);}
		  public void insertUpdate(DocumentEvent e) { update(e);}
		  public void removeUpdate(DocumentEvent e) { update(e);}
                        
		  private void update(DocumentEvent e) { 
		    try {
		      filterModel.setFilterName(filterModel.getIndexOf(filter), 
						e.getDocument().getText(0, e.getDocument().getLength()));
		    } catch (BadLocationException ex) { System.err.println("EXXXX"); }
		  }
		});
	}
            
	if (filterEditorPlaceholder.getComponentCount()>0) filterEditorPlaceholder.remove(0);
	filterEditorPlaceholder.add(panel,BorderLayout.CENTER);
	panel.setVisible(true);   
	panel.requestFocus();

        // select the active filter
        if (filterModel.getSelectedFilter() != filter) { // check to prevent cycle in notifications
            filtersList.setSelectedIndex(filterModel.getIndexOf(filter));
        }    
        
        filterEditorPlaceholder.validate();
        filterEditorPlaceholder.repaint();
    }
      
       
    /**
     * Lift of isValueValid to FiltersPanel
     */
    public boolean isValueValid() {
        Iterator it = panels.values().iterator();
        while (it.hasNext()) {
            FilterPanel fp = (FilterPanel)it.next();
            if (!fp.isValueValid()) return false;
        }
        return true;
    }
    
    /**
     * Reads data from the form into the filter repository 
     * that was passed-in in the constructor (returned by {@link #getFilterRepository})
     */
    public void updateFilters() {
      filterRepository.clear();             // throw away all original filters
        
      Iterator filterIt = filterModel.iterator();
      while (filterIt.hasNext()) {
          Filter f = (Filter)filterIt.next();
          if (panels.get(f) !=null ) 
            f = ((FilterPanel)panels.get(f)).getFilter(); // has panel, was touched

          filterRepository.add(f);
      }
      if (filterModel.getSelectedFilter()!= null) {
          filterRepository.setActive(filterModel.getSelectedFilter());
      }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 0, 11));
        setMinimumSize(new java.awt.Dimension(0, 0));
        setPreferredSize(new java.awt.Dimension(800, 500));
        setRequestFocusEnabled(false);
        setLayout(new java.awt.GridBagLayout());

        filterEditorPlaceholder.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 20, 0, 0));
        filterEditorPlaceholder.setAlignmentX(0.0F);
        filterEditorPlaceholder.setMinimumSize(new java.awt.Dimension(0, 0));
        filterEditorPlaceholder.setPreferredSize(new java.awt.Dimension(32767, 32767));
        filterEditorPlaceholder.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(filterEditorPlaceholder, gridBagConstraints);

        buttons1Panel.setLayout(new java.awt.GridBagLayout());

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/tasklist/filter/Bundle"); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(newButton, bundle.getString("BTN_New_Filter")); // NOI18N
        newButton.setToolTipText(bundle.getString("BTN_New_Filter_Hint")); // NOI18N
        newButton.addActionListener(this);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        buttons1Panel.add(newButton, gridBagConstraints);
        newButton.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(FiltersPanel.class, "ACSN_NewFilter")); // NOI18N
        newButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(FiltersPanel.class, "ACSD_NewFilter")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(DeleteButton, bundle.getString("BTN_Delete_Filter")); // NOI18N
        DeleteButton.setToolTipText(bundle.getString("BTN_Delete_Filter_Hint")); // NOI18N
        DeleteButton.addActionListener(this);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        buttons1Panel.add(DeleteButton, gridBagConstraints);
        DeleteButton.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(FiltersPanel.class, "ACSN_DeleteFilter")); // NOI18N
        DeleteButton.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(FiltersPanel.class, "ACSD_DeleteFilter")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        add(buttons1Panel, gridBagConstraints);

        buttons2Panel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        add(buttons2Panel, gridBagConstraints);

        filterListScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        filterListScrollPane.setAlignmentX(0.0F);
        filterListScrollPane.setAlignmentY(0.0F);
        filterListScrollPane.setMinimumSize(new java.awt.Dimension(0, 0));
        filterListScrollPane.setPreferredSize(new java.awt.Dimension(32767, 32767));
        filterListScrollPane.setAutoscrolls(true);

        filtersList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        filtersList.setAlignmentX(0.0F);
        filtersList.setMaximumSize(new java.awt.Dimension(32767, 32767));
        filterListScrollPane.setViewportView(filtersList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        add(filterListScrollPane, gridBagConstraints);
        filterListScrollPane.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(FiltersPanel.class).getString("ACSN_Filters")); // NOI18N
        filterListScrollPane.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(FiltersPanel.class).getString("ACSD_Filters")); // NOI18N

        filtersLabel.setLabelFor(filtersList);
        org.openide.awt.Mnemonics.setLocalizedText(filtersLabel, org.openide.util.NbBundle.getBundle(FiltersPanel.class).getString("LBL_Filters")); // NOI18N
        filtersLabel.setToolTipText(org.openide.util.NbBundle.getBundle(FiltersPanel.class).getString("HNT_Filters")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(filtersLabel, gridBagConstraints);
        filtersLabel.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getBundle(FiltersPanel.class).getString("ACSN_Filters")); // NOI18N
        filtersLabel.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getBundle(FiltersPanel.class).getString("ACSD_Filters")); // NOI18N
    }

    // Code for dispatching events from components to event handlers.

    public void actionPerformed(java.awt.event.ActionEvent evt) {
        if (evt.getSource() == newButton) {
            FiltersPanel.this.newButtonActionPerformed(evt);
        }
        else if (evt.getSource() == DeleteButton) {
            FiltersPanel.this.DeleteButtonActionPerformed(evt);
        }
    }// </editor-fold>//GEN-END:initComponents

    private void previewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previewButtonActionPerformed
      

    }//GEN-LAST:event_previewButtonActionPerformed

    private void DeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteButtonActionPerformed
        // TODO add your handling code here:
        int i = filterModel.getSelectedIndex();
        if (i != -1) {
            Filter f = filterModel.get(i);
            filterModel.remove(i);
            panels.remove(f);
        }
    }//GEN-LAST:event_DeleteButtonActionPerformed

    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newButtonActionPerformed
        // TODO add your handling code here:
        Filter f = view.createFilter();
        filterModel.add(f);
        panels.put(f,null);
        showFilter(f);
    }//GEN-LAST:event_newButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    final javax.swing.JButton DeleteButton = new javax.swing.JButton();
    final javax.swing.JPanel buttons1Panel = new javax.swing.JPanel();
    final javax.swing.JPanel buttons2Panel = new javax.swing.JPanel();
    final javax.swing.JPanel filterEditorPlaceholder = new javax.swing.JPanel();
    final javax.swing.JScrollPane filterListScrollPane = new javax.swing.JScrollPane();
    final javax.swing.JLabel filtersLabel = new javax.swing.JLabel();
    final javax.swing.JList filtersList = new javax.swing.JList();
    final javax.swing.JButton newButton = new javax.swing.JButton();
    // End of variables declaration//GEN-END:variables
 
    
    
}
