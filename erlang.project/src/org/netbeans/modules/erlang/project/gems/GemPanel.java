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

package org.netbeans.modules.erlang.project.gems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.api.options.OptionsDisplayer;
import org.openide.DialogDescriptor;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 * 
 * @todo Use a table instead of a list for the gem lists, use checkboxes to choose
 *   items to be uninstalled, and show the installation date (based
 *   on file timestamps)
 * @todo Find a way to execute both gem commands (local and remote listing) in the 
 *   same Ruby VM so it's faster to perform updates. Does
 *      gem list --both 
 *   work?
 * @todo Split error output
 *
 * @author  Tor Norbye
 */
public class GemPanel extends javax.swing.JPanel implements Runnable {
    private static final int UPDATED_TAB_INDEX  = 0;
    private static final int INSTALLED_TAB_INDEX  = 1;
    private static final int NEW_TAB_INDEX  = 2;
    
    private GemManager gemManager;
    
    private List<Gem> installedGems;
    private List<Gem> availableGems;
    private List<Gem> newGems;
    private List<Gem> updatedGems;
    private boolean installedModified;
    private boolean gemsModified;
    private boolean fetchingLocal;
    private boolean fetchingRemote;
    private List<String> remoteFailure;
    
    /** Creates new form GemPanel */
    public GemPanel(GemManager gemManager, String availableFilter) {
        initComponents();
       
        this.gemManager = gemManager;
        
        installedList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        installedList.getSelectionModel().addListSelectionListener(new MyListSelectionListener(installedList, installedDesc, uninstallButton));

        newList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        newList.getSelectionModel().addListSelectionListener(new MyListSelectionListener(newList, newDesc, installButton));

        updatedList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        updatedList.getSelectionModel().addListSelectionListener(new MyListSelectionListener(updatedList, updatedDesc, updateButton));

        installedModified = true;

        if (availableFilter != null) {
            searchNewText.setText(availableFilter);
            gemsTab.setSelectedIndex(NEW_TAB_INDEX);
        }

        RequestProcessor.getDefault().post(this, 300);
    }
    
    public void run() {
        // This will also update the New and Installed lists because Update depends on these
        refreshUpdated();
    }
    
    private void updateGemDescription(JTextPane pane, Gem gem) {
        if (gem == null) {
            pane.setText("");
            return;
        }

        String htmlMimeType = "text/html"; // NOI18N
        pane.setContentType(htmlMimeType);

        StringBuilder sb = new StringBuilder();
        sb.append("<html>"); // NOI18N
        sb.append("<h2>"); // NOI18N
        sb.append(gem.getName());
        sb.append("</h2>\n"); // NOI18N

        if (gem.getInstalledVersions() != null && gem.getAvailableVersions() != null) {
            // It's an update gem
            sb.append("<h3>"); // NOI18N
            sb.append(NbBundle.getMessage(GemPanel.class, "InstalledVersion"));
            sb.append("</h3>"); // NOI18N
            sb.append(gem.getInstalledVersions());

            sb.append("<h3>"); // NOI18N
            sb.append(NbBundle.getMessage(GemPanel.class, "AvailableVersion"));
            sb.append("</h3>"); // NOI18N
            sb.append(gem.getAvailableVersions());
            sb.append("<br>"); // NOI18N
        } else {
            sb.append("<h3>"); // NOI18N
            String version = gem.getInstalledVersions();
            if (version == null) {
                version = gem.getAvailableVersions();
            }
            if (version.indexOf(',') == -1) {
            // TODO I18N
                sb.append(NbBundle.getMessage(GemPanel.class, "Version"));
            } else {
                sb.append(NbBundle.getMessage(GemPanel.class, "Versions"));
            }
            sb.append("</h3>"); // NOI18N
            sb.append(version);
        }

        if (gem.getDescription() != null) {
            sb.append("<h3>"); // NOI18N
            sb.append(NbBundle.getMessage(GemPanel.class, "Description"));
            sb.append("</h3>"); // NOI18N
            sb.append(gem.getDescription());
        }
        
        sb.append("</html>"); // NOI18N
        
        pane.setText(sb.toString());
    }

    /** Called when installedList or newList is refreshed; recompute the updated list
     * @return True iff we're done with the updates
     */
    private synchronized boolean updateGems() {
        if (!(fetchingRemote || fetchingLocal)) {
            updatedProgress.setVisible(false);
            updatedProgressLabel.setVisible(false);
        }
        if (!fetchingRemote) {
            newProgress.setVisible(false);
            newProgressLabel.setVisible(false);
        }
        if (!fetchingLocal) {
            installedProgress.setVisible(false);
            installedProgressLabel.setVisible(false);
        }
    
        if (installedGems != null && availableGems != null) {
            Map<String,Gem> nameMap = new HashMap<String,Gem>();
            for (Gem gem : installedGems) {
                nameMap.put(gem.getName(), gem);
            }
            Set<String> installedNames = nameMap.keySet();
            
            updatedGems = new ArrayList<Gem>();
            newGems = new ArrayList<Gem>();
            for (Gem gem : availableGems) {
                if (installedNames.contains(gem.getName())) {
                    // We have this gem; let's see if we have the latest version
                    String available = gem.getAvailableVersions();
                    Gem installedGem = nameMap.get(gem.getName());
                    String installed = installedGem.getInstalledVersions(); 
                    // Gem always lists the most recent version first...
                    int firstVer = available.indexOf(',');
                    if (firstVer == -1) {
                        firstVer = available.indexOf(')');
                        if (firstVer == -1) {
                            firstVer = available.length();
                        }
                    }
                    if (!installed.regionMatches(0, available, 0, firstVer)) {
                        Gem update = new Gem(gem.getName(), installed, available.substring(0, firstVer));
                        update.setDescription(installedGem.getDescription());
                        updatedGems.add(update);
                    }
                } else {
                    newGems.add(gem);
                }
            }
            
            updateList(NEW_TAB_INDEX, true);
            updateList(UPDATED_TAB_INDEX, true);
        }
        
        return !(fetchingRemote || fetchingLocal);
    }
    
    private void updateList(int tab, boolean showCount) {
        Pattern pattern = null;
        String filter = getGemFilter(tab);
        if ((filter != null) && (filter.indexOf('*') != -1 || filter.indexOf('^') != -1 || filter.indexOf('$') != -1)) {
            try {
                pattern = Pattern.compile(filter);
            } catch (PatternSyntaxException pse) {
                // Don't treat the filter as a regexp
            }
        }
        List<Gem> gems;
        JList list;
        
        switch (tab) {
        case NEW_TAB_INDEX:
            gems = newGems;
            list = newList;
            break;
        case UPDATED_TAB_INDEX:
            gems = updatedGems;
            list = updatedList;
            break;
        case INSTALLED_TAB_INDEX:
            gems = installedGems;
            list = installedList;
            break;
        default:
            throw new IllegalArgumentException();
        }
        
        if (gems == null) {
            // attempting to filter before the list has been fetched - ignore
            return;
        }
        
        DefaultListModel model = new DefaultListModel();
        for (Gem gem : gems) {
            if (filter == null || filter.length() == 0) {
                model.addElement(gem);
            } else if (pattern == null) {
               if ((gem.getName().indexOf(filter) != -1) || 
                       (gem.getDescription() != null && gem.getDescription().indexOf(filter) != -1)) {
                    model.addElement(gem);
               }
            } else if (pattern.matcher(gem.getName()).find() || 
                    (gem.getDescription() != null && pattern.matcher(gem.getDescription()).find())) {
                model.addElement(gem);
            }
        }
        if (remoteFailure != null && (tab == UPDATED_TAB_INDEX || tab == NEW_TAB_INDEX)) {
            model.addElement(NbBundle.getMessage(GemPanel.class, "NoNetwork"));
            for (String line : remoteFailure) {
                model.addElement("<html><span color=\"red\">" + line + "</span></html>");
            }
        }
        list.clearSelection();
        list.setModel(model);
        list.invalidate();
        list.repaint();
        // This sometimes gives NPEs within setSelectedIndex...
        //        if (gems.size() > 0) {
        //            list.setSelectedIndex(0);
        //        }

        if (showCount) {
            String tabTitle = gemsTab.getTitleAt(tab);
            String originalTabTitle = tabTitle;
            int index = tabTitle.lastIndexOf('(');
            if (index != -1) {
                tabTitle = tabTitle.substring(0, index);
            }
            String count;
            if (model.size() < gems.size()) {
                count = model.size() + "/" + gems.size();
            } else {
                count = Integer.toString(gems.size());
            }
            tabTitle = tabTitle + "(" + count + ")";
            if (!tabTitle.equals(originalTabTitle)) {
                gemsTab.setTitleAt(tab, tabTitle);
            }
        }
    }
    
    /** Return whether any gems were modified - roots should be recomputed after panel is taken down */
    public boolean isModified() {
        return gemsModified;
    }

    private synchronized void refreshInstalled(boolean fetch) {
        if (installedList.getSelectedIndex() != -1) {
            updateGemDescription(installedDesc, null);
        }
        installedProgress.setVisible(true);
        installedProgressLabel.setVisible(true);
        fetchingLocal = true;
        if (fetch) {
            refreshGemList(installedList, INSTALLED_TAB_INDEX);
        }
        installedModified = false;
    }
    
    private synchronized void refreshNew(boolean fetch) {
        if (newList.getSelectedIndex() != -1) {
            updateGemDescription(newDesc, null);
        }
        newProgress.setVisible(true);
        newProgressLabel.setVisible(true);
        fetchingRemote = true;
        if (fetch) {
            refreshGemList(newList, NEW_TAB_INDEX);
        }
    }

    private void refreshUpdated() {
        if (updatedList.getSelectedIndex() != -1) {
            updateGemDescription(updatedDesc, null);
        }
        updatedProgress.setVisible(true);
        updatedProgressLabel.setVisible(true);
        refreshInstalled(false);
        refreshNew(false);
        refreshGemLists();
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        gemsTab = new javax.swing.JTabbedPane();
        updatedPanel = new javax.swing.JPanel();
        searchUpdatedText = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        reloadReposButton = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        updatedList = new javax.swing.JList();
        updateButton = new javax.swing.JButton();
        updateAllButton = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        updatedDesc = new javax.swing.JTextPane();
        updatedProgress = new javax.swing.JProgressBar();
        updatedProgressLabel = new javax.swing.JLabel();
        installedPanel = new javax.swing.JPanel();
        instSearchText = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        reloadInstalledButton = new javax.swing.JButton();
        uninstallButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        installedList = new javax.swing.JList();
        jScrollPane5 = new javax.swing.JScrollPane();
        installedDesc = new javax.swing.JTextPane();
        installedProgress = new javax.swing.JProgressBar();
        installedProgressLabel = new javax.swing.JLabel();
        newPanel = new javax.swing.JPanel();
        searchNewText = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        reloadNewButton = new javax.swing.JButton();
        installButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        newList = new javax.swing.JList();
        jScrollPane4 = new javax.swing.JScrollPane();
        newDesc = new javax.swing.JTextPane();
        newProgress = new javax.swing.JProgressBar();
        newProgressLabel = new javax.swing.JLabel();
        settingsPanel = new javax.swing.JPanel();
        proxyButton = new javax.swing.JButton();

        FormListener formListener = new FormListener();

        searchUpdatedText.setColumns(14);
        searchUpdatedText.setText(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.searchUpdatedText.text")); // NOI18N
        searchUpdatedText.addActionListener(formListener);

        jLabel3.setText(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.jLabel3.text")); // NOI18N

        reloadReposButton.setText(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.reloadReposButton.text")); // NOI18N
        reloadReposButton.addActionListener(formListener);

        jScrollPane3.setViewportView(updatedList);

        updateButton.setText(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.updateButton.text")); // NOI18N
        updateButton.setEnabled(false);
        updateButton.addActionListener(formListener);

        updateAllButton.setText(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.updateAllButton.text")); // NOI18N
        updateAllButton.addActionListener(formListener);

        jScrollPane6.setViewportView(updatedDesc);

        updatedProgress.setIndeterminate(true);

        updatedProgressLabel.setText(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.updatedProgressLabel.text")); // NOI18N

        org.jdesktop.layout.GroupLayout updatedPanelLayout = new org.jdesktop.layout.GroupLayout(updatedPanel);
        updatedPanel.setLayout(updatedPanelLayout);
        updatedPanelLayout.setHorizontalGroup(
            updatedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(updatedPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(updatedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, updatedPanelLayout.createSequentialGroup()
                        .add(reloadReposButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 389, Short.MAX_VALUE)
                        .add(jLabel3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(searchUpdatedText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 156, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(updatedPanelLayout.createSequentialGroup()
                        .add(updateButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(updateAllButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 322, Short.MAX_VALUE)
                        .add(updatedProgressLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(updatedProgress, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, updatedPanelLayout.createSequentialGroup()
                        .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE)
                        .add(18, 18, 18)
                        .add(jScrollPane6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 283, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        updatedPanelLayout.setVerticalGroup(
            updatedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(updatedPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(updatedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(searchUpdatedText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(reloadReposButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(updatedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
                    .add(jScrollPane6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(updatedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(updatedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(updateButton)
                        .add(updateAllButton))
                    .add(updatedProgress, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(updatedProgressLabel))
                .addContainerGap())
        );

        gemsTab.addTab(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.updatedPanel.TabConstraints.tabTitle"), updatedPanel); // NOI18N

        instSearchText.setColumns(14);
        instSearchText.setText(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.instSearchText.text")); // NOI18N
        instSearchText.addActionListener(formListener);

        jLabel1.setText(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.jLabel1.text")); // NOI18N

        reloadInstalledButton.setText(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.reloadInstalledButton.text")); // NOI18N
        reloadInstalledButton.addActionListener(formListener);

        uninstallButton.setText(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.uninstallButton.text")); // NOI18N
        uninstallButton.setEnabled(false);
        uninstallButton.addActionListener(formListener);

        jScrollPane1.setViewportView(installedList);

        jScrollPane5.setViewportView(installedDesc);

        installedProgress.setIndeterminate(true);

        installedProgressLabel.setText(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.installedProgressLabel.text")); // NOI18N

        org.jdesktop.layout.GroupLayout installedPanelLayout = new org.jdesktop.layout.GroupLayout(installedPanel);
        installedPanel.setLayout(installedPanelLayout);
        installedPanelLayout.setHorizontalGroup(
            installedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(installedPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(installedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, installedPanelLayout.createSequentialGroup()
                        .add(reloadInstalledButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 389, Short.MAX_VALUE)
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(instSearchText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 156, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(installedPanelLayout.createSequentialGroup()
                        .add(uninstallButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 412, Short.MAX_VALUE)
                        .add(installedProgressLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(installedProgress, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, installedPanelLayout.createSequentialGroup()
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE)
                        .add(18, 18, 18)
                        .add(jScrollPane5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 283, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        installedPanelLayout.setVerticalGroup(
            installedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(installedPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(installedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(reloadInstalledButton)
                    .add(instSearchText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(installedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
                    .add(jScrollPane5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(installedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(uninstallButton)
                    .add(installedProgress, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(installedProgressLabel))
                .addContainerGap())
        );

        gemsTab.addTab(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.installedPanel.TabConstraints.tabTitle"), installedPanel); // NOI18N

        searchNewText.setColumns(14);
        searchNewText.setText(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.searchNewText.text")); // NOI18N
        searchNewText.addActionListener(formListener);

        jLabel2.setText(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.jLabel2.text")); // NOI18N

        reloadNewButton.setText(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.reloadNewButton.text")); // NOI18N
        reloadNewButton.addActionListener(formListener);

        installButton.setText(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.installButton.text")); // NOI18N
        installButton.setEnabled(false);
        installButton.addActionListener(formListener);

        jScrollPane2.setViewportView(newList);

        jScrollPane4.setViewportView(newDesc);

        newProgress.setIndeterminate(true);

        newProgressLabel.setText(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.newProgressLabel.text")); // NOI18N

        org.jdesktop.layout.GroupLayout newPanelLayout = new org.jdesktop.layout.GroupLayout(newPanel);
        newPanel.setLayout(newPanelLayout);
        newPanelLayout.setHorizontalGroup(
            newPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(newPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(newPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, newPanelLayout.createSequentialGroup()
                        .add(reloadNewButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 389, Short.MAX_VALUE)
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(searchNewText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 156, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(newPanelLayout.createSequentialGroup()
                        .add(installButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 421, Short.MAX_VALUE)
                        .add(newProgressLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(newProgress, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, newPanelLayout.createSequentialGroup()
                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE)
                        .add(18, 18, 18)
                        .add(jScrollPane4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 283, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        newPanelLayout.setVerticalGroup(
            newPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(newPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(newPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(reloadNewButton)
                    .add(searchNewText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(newPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
                    .add(jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(newPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(installButton)
                    .add(newProgress, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(newProgressLabel))
                .addContainerGap())
        );

        gemsTab.addTab(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.newPanel.TabConstraints.tabTitle"), newPanel); // NOI18N

        proxyButton.setText(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.proxyButton.text")); // NOI18N
        proxyButton.addActionListener(formListener);

        org.jdesktop.layout.GroupLayout settingsPanelLayout = new org.jdesktop.layout.GroupLayout(settingsPanel);
        settingsPanel.setLayout(settingsPanelLayout);
        settingsPanelLayout.setHorizontalGroup(
            settingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(settingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(proxyButton)
                .addContainerGap(580, Short.MAX_VALUE))
        );
        settingsPanelLayout.setVerticalGroup(
            settingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(settingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(proxyButton)
                .addContainerGap(376, Short.MAX_VALUE))
        );

        gemsTab.addTab(org.openide.util.NbBundle.getMessage(GemPanel.class, "GemPanel.settingsPanel.TabConstraints.tabTitle"), settingsPanel); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(gemsTab, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 770, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(gemsTab, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE)
                .addContainerGap())
        );
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements java.awt.event.ActionListener {
        FormListener() {}
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (evt.getSource() == searchUpdatedText) {
                GemPanel.this.searchUpdatedTextActionPerformed(evt);
            }
            else if (evt.getSource() == reloadReposButton) {
                GemPanel.this.reloadReposButtonActionPerformed(evt);
            }
            else if (evt.getSource() == updateButton) {
                GemPanel.this.updateButtonActionPerformed(evt);
            }
            else if (evt.getSource() == updateAllButton) {
                GemPanel.this.updateAllButtonActionPerformed(evt);
            }
            else if (evt.getSource() == instSearchText) {
                GemPanel.this.instSearchTextActionPerformed(evt);
            }
            else if (evt.getSource() == reloadInstalledButton) {
                GemPanel.this.reloadInstalledButtonActionPerformed(evt);
            }
            else if (evt.getSource() == uninstallButton) {
                GemPanel.this.uninstallButtonActionPerformed(evt);
            }
            else if (evt.getSource() == searchNewText) {
                GemPanel.this.searchNewTextActionPerformed(evt);
            }
            else if (evt.getSource() == reloadNewButton) {
                GemPanel.this.reloadNewButtonActionPerformed(evt);
            }
            else if (evt.getSource() == installButton) {
                GemPanel.this.installButtonActionPerformed(evt);
            }
            else if (evt.getSource() == proxyButton) {
                GemPanel.this.proxyButtonActionPerformed(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents

private void reloadNewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadNewButtonActionPerformed
    refreshNew(true);
}//GEN-LAST:event_reloadNewButtonActionPerformed

private void proxyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_proxyButtonActionPerformed
    OptionsDisplayer.getDefault().open("General"); // NOI18Nd
}//GEN-LAST:event_proxyButtonActionPerformed

private void searchNewTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchNewTextActionPerformed
    updateList(NEW_TAB_INDEX, true);
}//GEN-LAST:event_searchNewTextActionPerformed

private void searchUpdatedTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchUpdatedTextActionPerformed
    updateList(UPDATED_TAB_INDEX, true);
}//GEN-LAST:event_searchUpdatedTextActionPerformed

private void reloadReposButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadReposButtonActionPerformed
    refreshUpdated();
}//GEN-LAST:event_reloadReposButtonActionPerformed

private void installButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_installButtonActionPerformed
    int[] indices = newList.getSelectedIndices();
    List<Gem> gems = new ArrayList<Gem>();
    for (int index : indices) {
        Object o = newList.getModel().getElementAt(index);
        if (o instanceof Gem) { // Could be error or please wait string
            Gem gem = (Gem)o;
            gems.add(gem);
        }
    }
    
    if (gems.size() > 0) {
        for (Gem chosen : gems) {
            // Get some information about the chosen gem
            InstallationSettingsPanel panel = new InstallationSettingsPanel(chosen);
            DialogDescriptor dd = new DialogDescriptor(panel, NbBundle.getMessage(GemPanel.class, "ChooseGemSettings"));
            dd.setOptionType(NotifyDescriptor.OK_CANCEL_OPTION);
            dd.setModal(true);
            dd.setHelpCtx(new HelpCtx(GemPanel.class));
            Object result = DialogDisplayer.getDefault().notify(dd);
            if (result.equals(NotifyDescriptor.OK_OPTION)) {
                Gem gem = new Gem(panel.getGemName(), null, null);
                // XXX Do I really need to refresh it right way?
                GemListRefresher completionTask = new GemListRefresher(newList, INSTALLED_TAB_INDEX);
                boolean changed = gemManager.install(new Gem[] { gem }, this, null, false, false, panel.getVersion(), 
                        panel.getIncludeDepencies(), true, completionTask);
                gemsModified = gemsModified || changed;
                installedModified = installedModified || changed;
            }
        }
    }

}//GEN-LAST:event_installButtonActionPerformed

private void instSearchTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_instSearchTextActionPerformed
    updateList(INSTALLED_TAB_INDEX, true);
}//GEN-LAST:event_instSearchTextActionPerformed

private void updateAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateAllButtonActionPerformed
    Runnable completionTask = new GemListRefresher(installedList, INSTALLED_TAB_INDEX);
    gemManager.update(null, this, null, false, false, true, completionTask);
    gemsModified = true;
    installedModified = true; 
}//GEN-LAST:event_updateAllButtonActionPerformed

private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
    int[] indices = updatedList.getSelectedIndices();
    List<Gem> gems = new ArrayList<Gem>();
    if (indices != null) {
        for (int index : indices) {
            assert index >= 0;
            Object o = updatedList.getModel().getElementAt(index);
            if (o instanceof Gem) { // Could be error or please wait string
                Gem gem = (Gem)o;
                gems.add(gem);
            }            
        }
    }
    if (gems.size() > 0) {
        Runnable completionTask = new GemListRefresher(updatedList, INSTALLED_TAB_INDEX);
        gemManager.update(gems.toArray(new Gem[gems.size()]), this, null, false, false, true, completionTask);
        gemsModified = true;
        installedModified = true;
    }
}//GEN-LAST:event_updateButtonActionPerformed

private void uninstallButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uninstallButtonActionPerformed
    int[] indices = installedList.getSelectedIndices();
    List<Gem> gems = new ArrayList<Gem>();
    if (indices != null) {
        for (int index : indices) {
            assert index >= 0;
            Object o = installedList.getModel().getElementAt(index);
            if (o instanceof Gem) { // Could be error or please wait string
                Gem gem = (Gem)o;
                gems.add(gem);
            }            
        }
    }
    if (gems.size() > 0) {
        Runnable completionTask = new GemListRefresher(installedList, INSTALLED_TAB_INDEX);
        gemManager.uninstall(gems.toArray(new Gem[gems.size()]), this, null, true, completionTask);
        gemsModified = true;
        installedModified = true;
    }
}//GEN-LAST:event_uninstallButtonActionPerformed

private void reloadInstalledButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadInstalledButtonActionPerformed
    refreshInstalled(true);
}//GEN-LAST:event_reloadInstalledButtonActionPerformed

    /** Refresh the list of displayed gems. If refresh is true, refresh the list from the gem manager, otherwise just refilter list */
    private void refreshGemList(final JList list, final int tab) {        
        Runnable runner = new Runnable() {
            public void run() {
                synchronized(this) {
                    List<String> lines = new ArrayList<String>(500);
                    if (tab == INSTALLED_TAB_INDEX) {
                        installedGems = gemManager.getInstalledGems(true, lines);
                        fetchingLocal = false;
                    } else if (tab == NEW_TAB_INDEX) {
                        remoteFailure = null;
                        availableGems = newGems = gemManager.getAvailableGems(lines);
                        if (availableGems.size() == 0 && lines.size() > 0) {
                            remoteFailure = lines;
                        }
                        fetchingRemote = false;
                    }
                    
                    // Recompute lists
                    boolean done = updateGems();
                    
                    if (!done) {
                        // Just filter
                        updateList(tab, false);
                    } else if (tab == INSTALLED_TAB_INDEX) {
                        updateList(tab, true);
                    }
                }
            }
        };
        
        RequestProcessor.getDefault().post(runner, 50);
    }

    private void refreshGemLists() {        
        Runnable runner = new Runnable() {
            public void run() {
                synchronized(this) {
                    List<String> lines = new ArrayList<String>(500);
                    remoteFailure = null;
                    installedGems = new ArrayList<Gem>(100);
                    availableGems = new ArrayList<Gem>(2000);
                    gemManager.getGems(installedGems, availableGems, lines);
                    newGems = availableGems;
                    fetchingLocal = false;
                    fetchingRemote = false;
                    if (availableGems.size() == 0 && lines.size() > 0) {
                        remoteFailure = lines;
                    }
                    
                    // Recompute lists
                    updateGems();
                    updateList(INSTALLED_TAB_INDEX, true);
                    
                    if (remoteFailure != null && !fetchingLocal) {
                        // Update the local list which shouldn't have any errors
                        refreshInstalled(true);
                    }
                }
            }
        };
        
        RequestProcessor.getDefault().post(runner, 50);
    }
    
    private String getGemFilter(int tab) {
        String filter = null;
        JTextField tf;
        if (tab == INSTALLED_TAB_INDEX) {
            tf = instSearchText;
        } else if (tab == UPDATED_TAB_INDEX) {
            tf = searchUpdatedText;
        } else {
            assert tab == NEW_TAB_INDEX;
            tf = searchNewText;
        }
        filter = tf.getText().trim();
        if (filter.length() == 0) {
            filter = null;
        }
        
        return filter;
    }

    private class MyListSelectionListener implements ListSelectionListener {
        private JButton button;
        private JTextPane pane;
        private JList list;
        
        private MyListSelectionListener(JList list, JTextPane pane, JButton button) {
            this.list = list;
            this.pane = pane;
            this.button = button;
        }
        public void valueChanged(ListSelectionEvent ev) {
            if (ev.getValueIsAdjusting()) {
                return;
            }
            int index = list.getSelectedIndex();
            if (index != -1) {
                Object o = list.getModel().getElementAt(index);
                if (o instanceof Gem) { // Could be "Please Wait..." String
                    button.setEnabled(true);
                    if (pane != null) {
                        updateGemDescription(pane, (Gem)o);
                    }
                    return;
                }
            }
            button.setEnabled(index != -1);
        }
    }
            
    private class GemListRefresher implements Runnable {
        private JList list;
        private int tab;
        
        public GemListRefresher(JList list, int tab) {
            this.list = list;
            this.tab = tab;
        }

        public void run() {
            refreshGemList(list, tab);
            if (list == installedList) {
                installedModified= false;
            }
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane gemsTab;
    private javax.swing.JTextField instSearchText;
    private javax.swing.JButton installButton;
    private javax.swing.JTextPane installedDesc;
    private javax.swing.JList installedList;
    private javax.swing.JPanel installedPanel;
    private javax.swing.JProgressBar installedProgress;
    private javax.swing.JLabel installedProgressLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTextPane newDesc;
    private javax.swing.JList newList;
    private javax.swing.JPanel newPanel;
    private javax.swing.JProgressBar newProgress;
    private javax.swing.JLabel newProgressLabel;
    private javax.swing.JButton proxyButton;
    private javax.swing.JButton reloadInstalledButton;
    private javax.swing.JButton reloadNewButton;
    private javax.swing.JButton reloadReposButton;
    private javax.swing.JTextField searchNewText;
    private javax.swing.JTextField searchUpdatedText;
    private javax.swing.JPanel settingsPanel;
    private javax.swing.JButton uninstallButton;
    private javax.swing.JButton updateAllButton;
    private javax.swing.JButton updateButton;
    private javax.swing.JTextPane updatedDesc;
    private javax.swing.JList updatedList;
    private javax.swing.JPanel updatedPanel;
    private javax.swing.JProgressBar updatedProgress;
    private javax.swing.JLabel updatedProgressLabel;
    // End of variables declaration//GEN-END:variables
    
}
