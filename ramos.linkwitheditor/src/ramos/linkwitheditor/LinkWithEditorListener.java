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
 * The Original Software is Ramon Ramos. The Initial Developer of the Original
 * Software is Ramon Ramos. All rights reserved.
 *
 * Copyright (c) 2006 Ramon Ramos
 */

package ramos.linkwitheditor;

import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Registry;

import org.netbeans.modules.editor.NbEditorUtilities;

import org.openide.cookies.InstanceCookie;

import org.openide.filesystems.Repository;

import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;

import org.openide.nodes.Node;

import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.IOException;

import javax.swing.Action;
import javax.swing.SwingUtilities;


/**
 *
 * @author ramos
 */
public class LinkWithEditorListener
  implements PropertyChangeListener {
  public final static LinkWithEditorListener INSTANCE = new LinkWithEditorListener();
  private static BaseDocument doc = null;
  private static Action action;
  private static PropertyChangeListener linkWithEditorListener = new LinkWithEditorListener();
  protected static TopComponent tc;
  private static String SLIDING = "Sliding";

  private LinkWithEditorListener() {
    try {
      if (action == null) {
        action = (Action) getTheObject(
            "Actions/Window/SelectDocumentNode/org-netbeans-modules-project-ui-SelectInProjects.instance");
      }
    } catch (final DataObjectNotFoundException ex) {
      ex.printStackTrace();
    } catch (final ClassNotFoundException ex) {
      ex.printStackTrace();
    } catch (final IOException ex) {
      ex.printStackTrace();
    }
  }

  public static LinkWithEditorListener getInstance() {
    return INSTANCE;
  }

  void attach() {
    TopComponent.getRegistry().addPropertyChangeListener(this);
  }

  void detach() {
    TopComponent.getRegistry().removePropertyChangeListener(this);
  }

  public static Object getTheObject(final String pathInSystemFilesystem)
    throws DataObjectNotFoundException, IOException, ClassNotFoundException {
    InstanceCookie ck;
    ck = (InstanceCookie) DataObject.find(Repository.getDefault()
                                                    .getDefaultFileSystem()
                                                    .getRoot()
                                                    .getFileObject(pathInSystemFilesystem))
                                    .getCookie(InstanceCookie.class);

    return ck.instanceCreate();
  }

  /**
   * DOCUMENT ME!
   *
   * @param evt DOCUMENT ME!
   */
  public void propertyChange(final PropertyChangeEvent evt) {
    if (evt.getPropertyName().equals(TopComponent.Registry.PROP_ACTIVATED)) {
      String modeName = WindowManager.getDefault().findMode(tc).getName();

      //tc.close();
      //System.out.println(tc.isOpened());
      if (modeName.contains(SLIDING) || !tc.isVisible() || !tc.isOpened()) {
        return;
      }

      //System.out.println(WindowManager.getDefault().findTopComponent("projectTabLogical_tc"));
      BaseDocument selectedDoc = Registry.getMostActiveDocument();

      if ((selectedDoc == null) || selectedDoc.equals(doc)) {
        return;
      }

      Node[] selectedNodes = TopComponent.getRegistry().getCurrentNodes();

      if ((selectedNodes == null) || (selectedNodes.length == 0)) {
        return;
      }

      DataObject dob1 = (DataObject) selectedNodes[0].getLookup()
                                                     .lookup(DataObject.class);

      DataObject dob2 = NbEditorUtilities.getDataObject(selectedDoc);

      if ((dob1 != null) && dob1.equals(dob2)) {
        doc = selectedDoc;
        action.actionPerformed(null);
        Registry.getMostActiveComponent().requestFocus();
      }
    }
  }
}
