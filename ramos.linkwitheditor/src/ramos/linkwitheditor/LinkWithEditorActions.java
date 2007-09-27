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

//import org.netbeans.editor.Registry;
import org.netbeans.modules.editor.NbEditorUtilities;

import org.openide.cookies.InstanceCookie;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;

import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;

import org.openide.nodes.Node;

import org.openide.util.HelpCtx;

import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

import java.awt.event.ActionEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;


/**
 *
 * @author ramos
 */
public final class LinkWithEditorActions {
  private static PropertyChangeListener currentListener;
  private static Action linkWithProjectsAction;
  private static Action linkWithFilesAction;
  private static Action linkWithFavoritesAction;
  private static Action linkWithNothingAction;

  protected static Action getLinkWithProjectsAction() {
    if (linkWithProjectsAction == null) {
      linkWithProjectsAction = new LinkWithProjectsAction();
    }

    return linkWithProjectsAction;
  }

  protected static Action geLinkWithFilesAction() {
    if (linkWithFilesAction == null) {
      linkWithFilesAction = new LinkWithFilesAction();
    }

    return linkWithFilesAction;
  }

  protected static Action getLinkWithFavoritesAction() {
    if (linkWithFavoritesAction == null) {
      linkWithFavoritesAction = new LinkWithFavoritesAction();
    }

    return linkWithFavoritesAction;
  }

  protected static Action getLinkWithNothingAction() {
    if (linkWithNothingAction == null) {
      linkWithNothingAction = new LinkWithNothingAction();
    }

    return linkWithNothingAction;
  }

  private static Object getTheObject(final String pathInSystemFilesystem)
    throws DataObjectNotFoundException, IOException, ClassNotFoundException {
    InstanceCookie ck;
    FileObject fo = Repository.getDefault().getDefaultFileSystem().getRoot()
                              .getFileObject(pathInSystemFilesystem);

    if (fo == null) {
      return null;
    }

    ck = (InstanceCookie) DataObject.find(fo).getCookie(InstanceCookie.class);

    return ck.instanceCreate();
  }

  protected static void detachCurrentListener() {
    TopComponent.getRegistry().removePropertyChangeListener(currentListener);
  }

  private static void attachListener(
    final PropertyChangeListener linkWithAction) {
    TopComponent.getRegistry().addPropertyChangeListener(linkWithAction);
    currentListener = linkWithAction;
  }

  public static abstract class AbstractLinkWithEditorAction
    extends AbstractAction
    implements PropertyChangeListener {
    private static String SLIDING = "Sliding";
    Action selectInAction;
    protected TopComponent tc;
    private BaseDocument mydoc = null;

    /**
     * Creates a new instance of AbstractLinkWithEditorAction
     */
    public AbstractLinkWithEditorAction() {
      try {
        if (selectInAction == null) {
          selectInAction = (Action) getTheObject(getPathToAction());
          //System.out.println("selectInAction here"+selectInAction);
          this.tc = WindowManager.getDefault().findTopComponent(getTCId());
        }
      } catch (final DataObjectNotFoundException ex) {
        ex.printStackTrace();
      } catch (final ClassNotFoundException ex) {
        ex.printStackTrace();
      } catch (final IOException ex) {
        ex.printStackTrace();
      }
    }

    public void actionPerformed(final ActionEvent e) {
      //System.out.println("actionPerformed "+this);
      detachCurrentListener();
      attachListener(this);
    }

    abstract String getPathToAction();

    abstract String getTCId();

    public void propertyChange(final PropertyChangeEvent evt) {
      //System.out.println("propertyChange "+this);
      if (evt.getPropertyName().equals(TopComponent.Registry.PROP_ACTIVATED)) {
        //System.out.println("PROP_ACTIVATED "+this);
        String modeName = WindowManager.getDefault().findMode(tc).getName();

        //tc.close();
        ////System.out.println(tc.isOpened());
        if (modeName.contains(SLIDING) || !tc.isVisible() || !tc.isOpened()) {
          //System.out.println("returning1? "+this);
          //System.out.println("tc "+tc);
          return;
        }

        ////System.out.println(WindowManager.getDefault().findTopComponent("projectTabLogical_tc"));
        BaseDocument selectedDoc = Registry.getMostActiveDocument();

        if ((selectedDoc == null) || selectedDoc.equals(mydoc)) {
          //System.out.println("returning2? "+this);
          return;
        }

        Node[] selectedNodes = TopComponent.getRegistry().getCurrentNodes();

        if ((selectedNodes == null) || (selectedNodes.length == 0)) {
          //System.out.println("returning3? "+this);
          return;
        }

        DataObject selectedDataObject = (DataObject) selectedNodes[0].getLookup()
                                                                     .lookup(DataObject.class);

        DataObject workingDocDataObject = NbEditorUtilities.getDataObject(selectedDoc);

        if ((selectedDataObject != null) &&
              selectedDataObject.equals(workingDocDataObject)) {
          mydoc = selectedDoc;
          selectInAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, getClass().getSimpleName()));
          org.netbeans.editor.Registry.getMostActiveComponent().requestFocus();
        } else {
          //System.out.println("returning4? "+this);
        }
      }
    }
  }

  private static class LinkWithProjectsAction
    extends AbstractLinkWithEditorAction {
    String getPathToAction() {
      return "Actions/Window/SelectDocumentNode/org-netbeans-modules-project-ui-SelectInProjects.instance";
    }

    String getTCId() {
      return "projectTabLogical_tc";
    }

    public Object getValue(final String key) {
      if (key.equals(AbstractAction.NAME)) {
        return org.openide.util.NbBundle.getBundle(LinkWithEditorActions.class)
                                        .getString("Projects");
      } else {
        return super.getValue(key);
      }
    }
  }

  private static class LinkWithFilesAction
    extends AbstractLinkWithEditorAction {
    String getPathToAction() {
      return "Actions/Window/SelectDocumentNode/org-netbeans-modules-project-ui-SelectInFiles.instance";
    }

    String getTCId() {
      return "projectTab_tc";
    }

    public Object getValue(final String key) {
      if (key.equals(AbstractAction.NAME)) {
        return org.openide.util.NbBundle.getBundle(LinkWithEditorActions.class)
                                        .getString("Files");
      } else {
        return super.getValue(key);
      }
    }
  }

  private static class LinkWithFavoritesAction
    extends AbstractLinkWithEditorAction {
    String getPathToAction() {
      return "Actions/Window/SelectDocumentNode/org-netbeans-modules-favorites-Select.instance";
    }

    String getTCId() {
      return "favorites";
    }

    public Object getValue(final String key) {
      if (key.equals(AbstractAction.NAME)) {
        return org.openide.util.NbBundle.getBundle(LinkWithEditorActions.class)
                                        .getString("Favorites");
      } else {
        return super.getValue(key);
      }
    }

    public HelpCtx getHelpCtx() {
      return HelpCtx.DEFAULT_HELP;
    }
  }

  private static class LinkWithNothingAction
    extends AbstractAction {
    public void actionPerformed(final ActionEvent e) {
      //detach current
      detachCurrentListener();
    }

    /**
     *
     * Gets the <code>Object</code> associated with the specified key.
     *
     * @param key a string containing the specified <code>key</code>
     * @return the binding <code>Object</code> stored with this key; if there
     *         are no keys, it will return <code>null</code>
     * @see Action#getValue
     */
    public Object getValue(final String key) {
      if (key.equals(AbstractAction.NAME)) {
        return org.openide.util.NbBundle.getBundle(LinkWithEditorActions.class)
                                        .getString("Nothing");
      } else {
        return super.getValue(key);
      }
    }
  }
}
