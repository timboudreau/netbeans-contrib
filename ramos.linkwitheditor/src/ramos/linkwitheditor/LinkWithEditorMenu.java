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
 * The Original Software is Ramon Ramos. The Initial Developer of the Original
 * Software is Ramon Ramos. All rights reserved.
 *
 * Copyright (c) 2006 Ramon Ramos
 */
package ramos.linkwitheditor;

import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;


/**
 *
 * @author ramos
 */
final class LinkWithEditorMenu
  extends JMenu {
  private final static LinkWithEditorMenu INSTANCE = new LinkWithEditorMenu();
  private JRadioButtonMenuItem projects;
  private JRadioButtonMenuItem files;
  private JRadioButtonMenuItem nothing;
  private javax.swing.JRadioButtonMenuItem favorites;

  /** Creates a new instance of LinkWithEditorMenu */
  private LinkWithEditorMenu() {
    super(org.openide.util.NbBundle.getBundle(LinkWithEditorMenu.class)
                                   .getString("Link_With_Editor"));

    ButtonGroup group = new ButtonGroup();
    projects = new JRadioButtonMenuItem(LinkWithEditorActions.getLinkWithProjectsAction());
    files = new JRadioButtonMenuItem(LinkWithEditorActions.geLinkWithFilesAction());
    favorites = new JRadioButtonMenuItem(LinkWithEditorActions.getLinkWithFavoritesAction());
    nothing = new JRadioButtonMenuItem(LinkWithEditorActions.getLinkWithNothingAction());
    group.add(projects);
    group.add(files);
    group.add(favorites);
    group.add(nothing);
    group.setSelected(nothing.getModel(), true);
    add(projects);
    add(files);
    add(favorites);
    add(nothing);
    this.setMnemonic(KeyEvent.VK_L);
  }

  final static LinkWithEditorMenu getLinkWithEditorMenu() {
    return INSTANCE;
  }
}
