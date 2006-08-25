/*
 * LinkWithEditorMenu.java
 *
 * Created on 6 de agosto de 2006, 10:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
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
final class LinkWithEditorMenu extends JMenu{
   
   private final static LinkWithEditorMenu INSTANCE = new LinkWithEditorMenu();
   private JRadioButtonMenuItem projects, files;
   
   private JRadioButtonMenuItem nothing;
   
   private javax.swing.JRadioButtonMenuItem favorites;
   /** Creates a new instance of LinkWithEditorMenu */
   private LinkWithEditorMenu() {
      super(org.openide.util.NbBundle.getBundle(LinkWithEditorMenu.class).getString("Link_With_Editor"));
      ButtonGroup group = new ButtonGroup();
      projects = new JRadioButtonMenuItem(LinkWithEditorActions.getLinkWithProjectsAction());
      files = new JRadioButtonMenuItem(LinkWithEditorActions.geLinkWithFilesAction());
      favorites = new JRadioButtonMenuItem(LinkWithEditorActions.getLinkWithFavoritesAction());
      nothing = new JRadioButtonMenuItem(LinkWithEditorActions.getLinkWithNothingAction());
      group.add(projects);
      group.add(files);
      group.add(favorites);
      group.add(nothing);
      group.setSelected(nothing.getModel(),true);
      add(projects);
      add(files);
      add(favorites);
      add(nothing);
      this.setMnemonic(KeyEvent.VK_L);
   }
   final static LinkWithEditorMenu getLinkWithEditorMenu(){
      return INSTANCE;
   }
   
}
