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
import ramos.localhistory.ui.VersionNode;

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
    // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
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
    Collection<VersionNode> collection =
      localHistoryTopComponent.fillRevisionsList(file);

    if (collection.size() != 0) {
      localHistoryTopComponent.setFile(file, collection);
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

    //DataObject dataObject = n.getLookup().lookup(DataObject.class);
    //System.out.println("dataObject ="+dataObject);
    return ((dataObject != null) && dataObject.getPrimaryFile()
                                              .isData());
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
