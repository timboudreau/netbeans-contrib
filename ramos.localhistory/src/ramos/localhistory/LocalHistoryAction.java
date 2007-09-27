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
package ramos.localhistory;

import java.io.File;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

import org.openide.loaders.DataObject;

import org.openide.nodes.Node;

import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;

import ramos.localhistory.ui.LocalHistoryTopComponent;

import java.awt.event.ActionEvent;

import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public final class LocalHistoryAction
  extends CookieAction {
  /**
   * DOCUMENT ME!
   *
   * @param actionContext DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public Action createContextAwareInstance(final Lookup actionContext) {
    return new ContextAction(actionContext);
  }

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public HelpCtx getHelpCtx() {
    return HelpCtx.DEFAULT_HELP;
  }

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public String getName() {
    return NbBundle.getMessage(LocalHistoryAction.class,
      "CTL_LocalHistoryAction");
  }

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  protected boolean asynchronous() {
    return false;
  }

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  protected Class[] cookieClasses() {
    return new Class[] { DataObject.class };
  }

  /**
   * DOCUMENT ME!
   */
  protected void initialize() {
    super.initialize();
    // see org.openide.util.actions.SystemAction.iconResource() 
    //javadoc for more details
    putValue("noIconInMenu", Boolean.TRUE);
  }

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  protected int mode() {
    return CookieAction.MODE_EXACTLY_ONE;
  }

  /**
   * DOCUMENT ME!
   *
   * @param activatedNodes DOCUMENT ME!
   */
  protected void performAction(final Node[] activatedNodes) {
    DataObject c = (DataObject) activatedNodes[0].getCookie(DataObject.class);
    performAction(c);
  }

  /**
   * DOCUMENT ME!
   *
   * @param c DOCUMENT ME!
   */
  protected void performAction(final DataObject c) {
    final FileObject fileObject = c.getPrimaryFile();

    final LocalHistoryTopComponent localHistoryTopComponent =
      LocalHistoryTopComponent.findInstance();
    File file = FileUtil.toFile(fileObject);
    Collection<VersionNode> versionNodesCollection =
      LocalHistoryRepository.getInstance().fillRevisionsList(file);

    if (versionNodesCollection.size() != 0) {
      localHistoryTopComponent.setFileForHistory(file, versionNodesCollection);
      localHistoryTopComponent.open();
      localHistoryTopComponent.requestActive();
      localHistoryTopComponent.setDisplayName(
          "Local History of " + fileObject.getNameExt());
    } else {
      JOptionPane.showMessageDialog(null, "No revisions in local history");
    }
  }

  /**
   * DOCUMENT ME!
   *
   * @param dataObject DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  private boolean enable(final DataObject dataObject) {
    assert dataObject != null;
    return ((dataObject != null) && 
       dataObject.getPrimaryFile().isData()) && 
       !LocalHistoryRepository.blackList(dataObject.getPrimaryFile());
  }

  /**
   * DOCUMENT ME!
   *
   * @author $author$
   * @version $Revision$
   */
  private class ContextAction
    extends AbstractAction {
    DataObject classNode = null;

    /**
     * Creates a new ContextAction object.
     *
     * @param context DOCUMENT ME!
     */
    public ContextAction(final Lookup context) {
      //System.out.println("init CA");
      //Node _classNode = context.getDefault().lookup(Node.class);
      DataObject _classNode = (DataObject) context.lookup(DataObject.class);
      //System.out.println("_classNode = "+_classNode);
      classNode =
        ((_classNode != null) && enable(_classNode))
        ? _classNode
        : null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void actionPerformed(final ActionEvent e) {
      performAction(classNode);
    }

    /**
     * Gets the <code>Object</code> associated with the specified
     * key.
     *
     * @param key a string containing the specified <code>key</code>
     *
     * @return the binding <code>Object</code> stored with this key; if there
     *         are no keys, it will return <code>null</code>
     *
     * @see Action#getValue
     */
    public Object getValue(final String key) {
      if (key.equals(AbstractAction.NAME)) {
        return getName();
      }

      return super.getValue(key);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isEnabled() {
      //         System.out.println("CA isEnabled");
      //         System.out.println("classNode = "+classNode);
      return classNode != null;
    }
  }
}
