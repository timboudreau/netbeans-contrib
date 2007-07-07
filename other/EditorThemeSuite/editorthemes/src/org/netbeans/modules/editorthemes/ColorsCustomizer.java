/*
 * ColorsCustomizer.java
 *
 * Created on Jul 2, 2007, 2:45:23 PM
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.editorthemes;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.swing.AbstractButton;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;

/**
 *
 * @author Tim Boudreau
 */
public class ColorsCustomizer extends JPanel implements ActionListener {
    private final JTabbedPane pane = new JTabbedPane();
    private final ColorModel mdl = new ColorModel();
    private final JPanel upperPanel = new JPanel(new GridBagLayout());
    private final JComboBox profileChooser = new JComboBox();
    private final JComboBox languageChooser = new JComboBox();
    private final JLabel languageLabel = new JLabel();
    private final JLabel profileLabel = new JLabel();
    private final JButton newProfileButton = new JButton();
    private final JButton delProfileButton = new JButton();
    private final JButton transformButton = new JButton();
    public ColorsCustomizer() {
        setLayout (new BorderLayout());
        UIKind[] kinds = UIKind.values();
        for (int i=0; i < kinds.length; i++) {
            RevisedHighlightingPanel pnl = new RevisedHighlightingPanel(kinds[i]);
            pnl.update(mdl);
            pane.add (pnl);
        }
        add (pane, BorderLayout.CENTER);
        add (upperPanel, BorderLayout.NORTH);
        languageLabel.setLabelFor (languageChooser);
        profileLabel.setLabelFor (profileChooser);
        loc (languageLabel, "LBL_LANGUAGES"); //NOI18N
        loc (profileLabel, "LBL_PROFILES"); //NOI18N
        loc (newProfileButton, "LBL_NEW_PROFILE"); //NOI18N
        loc (delProfileButton, "LBL_DELETE_PROFILE"); //NOI18N
        loc (transformButton, "LBL_TRANSFORM");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.ipadx = 20;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridwidth = 1;
        upperPanel.add (profileLabel, gbc);
        gbc.gridy = 1;
        upperPanel.add (languageLabel, gbc);
        gbc.gridy = 0;
        gbc.gridx = 3;
        gbc.anchor = GridBagConstraints.LINE_END;
        upperPanel.add (newProfileButton, gbc);
        gbc.gridx = 4;
        upperPanel.add (delProfileButton, gbc);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0D;
        upperPanel.add (profileChooser, gbc);
        gbc.gridy = 1;
        upperPanel.add (languageChooser, gbc);

        JPanel bottomPanel = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.weightx = 0.5D;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        bottomPanel.add (transformButton, gbc);

        add (bottomPanel, BorderLayout.SOUTH);

        List <String> languages = new ArrayList<String>(mdl.getLanguages());
        Collections.sort (languages, new LanguagesComparator());

        languageChooser.setModel(new DefaultComboBoxModel(languages.toArray()));
        Set <String> profiles = mdl.getProfiles();
        profileChooser.setModel(new DefaultComboBoxModel(profiles.toArray()));

        profileChooser.setSelectedItem(mdl.getCurrentProfile());
        languageChooser.setSelectedItem(ColorModel.ALL_LANGUAGES);

        newProfileButton.addActionListener(this);
        delProfileButton.addActionListener(this);
        languageChooser.addActionListener(this);
        profileChooser.addActionListener(this);
        transformButton.addActionListener(this);
    }

    public void requestFocus() {
        pane.requestFocus();
    }

    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o == newProfileButton) {
            createNewProfile ();
        } else if (o == delProfileButton) {
            deleteSelectedProfile();
        } else if (o == languageChooser) {
            setSelectedLanguage (languageChooser.getSelectedItem().toString());
        } else if (o == profileChooser) {
            setSelectedProfile (profileChooser.getSelectedItem().toString());
        } else if (o == transformButton) {
            transform();
        } else {
            throw new AssertionError();
        }
    }

    static String loc (JComponent jc, String key) {
        String txt = NbBundle.getMessage (ColorsCustomizer.class, key);
        if (jc instanceof AbstractButton) {
            AbstractButton b = (AbstractButton) jc;
            Mnemonics.setLocalizedText(b, txt);
        } else if (jc instanceof JLabel) {
            JLabel l = (JLabel) jc;
            Mnemonics.setLocalizedText(l, txt);
        }
        return txt;
    }

    private void createNewProfile() {
        System.err.println("Customizer create new profile");
        NotifyDescriptor.InputLine in = new
                NotifyDescriptor.InputLine(loc(null, "MSG_NEW_PROFILE"),
                loc(null, "TTL_NEW_PROFILE"));
        if (DialogDisplayer.getDefault().notify(in) != NotifyDescriptor.CANCEL_OPTION) {
            String nm = in.getInputText().trim();
            if (nm.length() > 0 && !mdl.getProfiles().contains(nm)) {
                String currentProfile = profileChooser.getSelectedItem().toString();
                if (!mdl.getProfiles ().contains (nm)) {
                    // clone profile
                    System.err.println("Creating profile " + nm);
                    mdl.createNewProfile (nm, currentProfile);
                } else {
                    System.err.println("Profile already exists");
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }
                DefaultComboBoxModel cmdl = (DefaultComboBoxModel) profileChooser.getModel();
                cmdl.addElement(nm);
                profileChooser.setSelectedItem(nm);
//                setSelectedProfile (nm);
            } else {
                System.err.println("Name " + nm + " no good");
                Toolkit.getDefaultToolkit().beep();
            }
        } else {
            System.err.println("cancelled");
        }
    }

    private void deleteSelectedProfile() {
        if (profileChooser.getModel().getSize() == 0) return;
        String selProfile = profileChooser.getSelectedItem().toString();
        if (mdl.isCustomProfile(selProfile)) {
            //XXX
        }
    }

    private void setSelectedLanguage(String s) {
        Component[] c = pane.getComponents();
        for (int i=0; i < c.length; i++) {
            if (c[i] instanceof RevisedHighlightingPanel) {
                System.err.println("Set language to " + s + " on " + c[i]);
                RevisedHighlightingPanel r = (RevisedHighlightingPanel) c[i];
                r.setLanguage(s);
            }
        }
    }

    private void setSelectedProfile(String s) {
        Component[] c = pane.getComponents();
        for (int i=0; i < c.length; i++) {
            if (c[i] instanceof RevisedHighlightingPanel) {
                RevisedHighlightingPanel r = (RevisedHighlightingPanel) c[i];
                r.setCurrentProfile(s);
            }
        }
        mdl.setCurrentProfile(s);
    }

    private void transform() {
        TransformCustomizer cust = new TransformCustomizer();
        DialogDescriptor dlg = new DialogDescriptor (cust, NbBundle.getMessage(ColorsCustomizer.class,
                "LBL_TRANSFORM")); //NOI18N
        if (DialogDisplayer.getDefault().notify(dlg) == DialogDescriptor.OK_OPTION) {
            Transformation transform = cust.getTransformation();
            if (transform != null) {
                transform.process(mdl, profileChooser.getSelectedItem().toString());
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        }
        Component[] c = pane.getComponents();
        for (int i=0; i < c.length; i++) {
            if (c[i] instanceof RevisedHighlightingPanel) {
                RevisedHighlightingPanel r = (RevisedHighlightingPanel) c[i];
                r.refreshUI();
                r.updatePreview();
            }
        }
    }

    private static final class LanguagesComparator implements Comparator<String> {
        public int compare(String o1, String o2) {
            if (o1.equals(ColorModel.ALL_LANGUAGES))
                return o2.equals(ColorModel.ALL_LANGUAGES) ? 0 : -1;
            return o1.compareTo(o2);
        }
    }
}
