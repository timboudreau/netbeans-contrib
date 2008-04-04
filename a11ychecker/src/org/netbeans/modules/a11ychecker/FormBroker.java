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
package org.netbeans.modules.a11ychecker;

import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.Document;
import org.netbeans.core.api.multiview.MultiViews;
import org.netbeans.modules.a11ychecker.output.ResultWindowTopComponent;
import org.openide.loaders.DataObject;
import org.openide.text.CloneableEditorSupport;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Provides support for scanning of currently editet form and their changes
 * Singleton.
 * @author Max Sauer
 */
public final class FormBroker {

    private static FormBroker instance;
    /** The top-component we're currently tracking (active one) */
    private TopComponent currentTC = null;
    /** The document we're currently tracking (active one) */
    private Document currentDocument = null;
    /** The data-object we're currently tracking (active one) */
    private DataObject currentDO = null;
    /** outer world binding :) */
    private Env env = new Env();

    private FormBroker() {
    }

    public static FormBroker getDefault() {
        if (instance == null) {
            instance = new FormBroker();
        }
        return instance;
    }

    public void startBroker() {
//	System.out.println("Starting active suggestions fetching....");  // NOI18N

        // must be removed in docStop
        WindowSystemMonitor monitor = getWindowSystemMonitor();
        monitor.enableOpenCloseEvents();
        env.addTCRegistryListener(monitor);
        env.addDORegistryListener(getDataSystemMonitor());
        prepareRescanInAWT(false);
    }

    public void stopBroker() {
//	System.out.println("Stopping active suggestions fetching....");  // NOI18N

        env.removeTCRegistryListener(getWindowSystemMonitor());
        env.removeDORegistryListener(getDataSystemMonitor());
        // Unregister previous listeners
        if (currentTC != null) {
            currentTC.removeComponentListener(getWindowSystemMonitor());
            currentTC = null;
        }
    }

    /**
     * It sends asynchronously to AWT thread (selected editor TC must be grabbed in AWT).
     * Once prepared it sends request to a background thread.
     * @param delay if true schedule later acording to user settings otherwise do immediatelly
     */
    private void prepareRescanInAWT(final boolean delay) {
        Runnable performer = new Runnable() {

            public void run() {
                prepareCurrent();

            }
        };

        if (SwingUtilities.isEventDispatchThread()) {
            performer.run();
        } else {
            // docStop() might have happen
            // in the mean time - make sure we don't do a
            // delay=true when we're not supposed to
            // be processing views
            SwingUtilities.invokeLater(performer);
        }
    }

    /**
     * Prepares current environment. Monitors
     * actual document modification state using DocumentListener
     * and CaretListener. Actual TopComponent is guarded
     * by attached ComponentListener.
     * <p>
     * Must be called from <b>AWT thread only</b>.
     */
    private boolean prepareCurrent() {

        assert SwingUtilities.isEventDispatchThread() : "This method must be run in the AWT thread"; // NOI18N

        // Unregister previous listeners
        if (currentTC != null) {
            currentTC.removeComponentListener(getWindowSystemMonitor());
            currentTC = null;
        }
        //	if (currentDocument != null) {
        //	    currentDocument.removeDocumentListener(getEditorMonitor());
        //	    handleDocHidden(currentDocument, currentDO);
        //	}
        //	removeCurrentCaretListeners();

        // Find which component is showing in it
        // Add my own component listener to it
        // When componentHidden, unregister my own component listener
        // Redo above

        // Locate source editor
        TopComponent tc = findActiveEditor();
        if (tc == null) {
            // The last editor-support window in the editor was probably
            // just closed - or was not on top

//	    System.out.println("Cannot find active source editor!");   // during startup
            return false;
        }

        // Listen for changes on this component so we know when
        // it's replaced by something else XXX looks like PROP_ACTIVATED duplication
        currentTC = tc;
        currentTC.addComponentListener(getWindowSystemMonitor());

        return true;
    }
    private DataSystemMonitor dataSystemMonitor;

    private DataSystemMonitor getDataSystemMonitor() {
        if (dataSystemMonitor == null) {
            dataSystemMonitor = new DataSystemMonitor();
        }
        return dataSystemMonitor;
    }

    public static boolean isMVCTopComponent(TopComponent tc) {
        //need to comply with MultiViewApi
//	if(tc instanceof MultiViewCloneableTopComponent)
        if (MultiViews.findMultiViewHandler(tc) != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Listener for DataObject.Registry changes.
     *
     * This class listens for modify-changes of dataobjects such that
     * it can notify files of Save operations.
     */
    private class DataSystemMonitor implements ChangeListener {

        public void stateChanged(ChangeEvent e) {
            /* Not sure what the source is, but it isn't dataobject
            and the javadoc doesn't say anything specific, so
            I guess I can't rely on that as a filter
            if (e.getSource() != dataobject) {
            // If you reinstate this in some way, make sure it
            // works for Save ALL as well!!!
            return;
            }
             */

//	    System.out.println("#### EVENT " + e.getSource() + " changed.");

            //TODO: Invoke recheck here
            TopComponent curr = findActiveEditor();
            if (isMVCTopComponent(curr)) {
//		System.out.println("#### This is instance of form");
                new FormHandler(curr).check();
            }
        }
    }

    /**
     * Locates active editor topComponent. Must be run in AWT
     * thread. Eliminates Welcome screen, JavaDoc
     * and orher non-editor stuff in EDITOR_MODE.
     * @return tc or <code>null</code> for non-editor selected topcomponent
     */
    public TopComponent findActiveEditor() {
        //ensure running in AWT
        final Object[] resultTC = new Object[1];

        if (SwingUtilities.isEventDispatchThread()) {
            resultTC[0] = findActiveEditorImpl();
        } else {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    resultTC[0] = findActiveEditor();
                }
            });
        }
        return (TopComponent) resultTC[0];
    }

    public TopComponent findActiveEditorImpl() {
        Mode mode = WindowManager.getDefault().findMode(CloneableEditorSupport.EDITOR_MODE);
        if (mode == null) {
            // The editor window was probablyjust closed
            return null;
        }
        TopComponent tc = mode.getSelectedTopComponent();

        // form files within MultiViewCloneableTopComponent contantly returns null
        // so I got suggestion to use instanceof CloneableEditorSupport.Pane workaround
        // if (tc != null && tc.getLookup().lookup(EditorCookie.class)  != null) {
        if (tc instanceof CloneableEditorSupport.Pane) {
            // Found the source editor...
            //                if (tc.isShowing()) {   // FIXME it returns false for components I can positivelly see
            // hopefully mode does not return hidden TC as selected one.
            // It happens right after startup
            return tc;
        //                }
        }
        return null;
    }
    private WindowSystemMonitor windowSystemMonitor;

    /** See note on {@link WindowSystemMonitor#enableOpenCloseEvents} */
    private WindowSystemMonitor getWindowSystemMonitor() {
        if (windowSystemMonitor == null) {
            windowSystemMonitor = new WindowSystemMonitor();
        }
        return windowSystemMonitor;
    }

    /**
     * Binding to outer world that can be changed by unit tests
     */
    static class Env {

        void addTCRegistryListener(PropertyChangeListener pcl) {
            TopComponent.getRegistry().addPropertyChangeListener(pcl);
        }

        void removeTCRegistryListener(PropertyChangeListener pcl) {
            TopComponent.getRegistry().removePropertyChangeListener(pcl);
        }

        void addDORegistryListener(ChangeListener cl) {
            DataObject.getRegistry().addChangeListener(cl);

        }

        void removeDORegistryListener(ChangeListener cl) {
            DataObject.getRegistry().removeChangeListener(cl);
        }
    }

    /**
     * Return all opened top components in editor mode.
     * @return never null
     */
    static TopComponent[] openedTopComponents() {
        final Object[] wsResult = new Object[1];
        try {


            if (SwingUtilities.isEventDispatchThread()) {
                Mode editorMode = WindowManager.getDefault().findMode(CloneableEditorSupport.EDITOR_MODE);
                if (editorMode == null) {
                    return new TopComponent[0];
                } else {
                    return editorMode.getTopComponents();
                }
            } else {
                // I just hope that we are not called from non-AWT thread
                // still holding AWTTreeLock otherwise deadlock
                SwingUtilities.invokeAndWait(new Runnable() {

                    public void run() {
                        Mode editorMode = WindowManager.getDefault().findMode(CloneableEditorSupport.EDITOR_MODE);
                        if (editorMode == null) {
                            wsResult[0] = new TopComponent[0];
                        } else {
                            wsResult[0] = editorMode.getTopComponents();
                        }
                    }
                });
                return (TopComponent[]) wsResult[0];
            }
        } catch (InterruptedException e) {
            return new TopComponent[0];
        } catch (InvocationTargetException e) {
            return new TopComponent[0];
        }
    }

    private static DataObject extractDataObject(TopComponent topComponent) {
        DataObject dobj = (DataObject) topComponent.getLookup().lookup(DataObject.class);
        if (dobj != null && dobj.isValid()) {
            return dobj;
        } else {
            return null;
        }
    }

    // The code is unnecesary comples there is pending issue #48937
    class WindowSystemMonitor implements PropertyChangeListener, ComponentListener {

        /** Previous Set&lt;TopComponent> */
        private Set openedSoFar = Collections.EMPTY_SET;

        /**
         * Must be called before adding this listener to environment if in hope that
         * it will provide (initial) open/close events.
         */
        public void enableOpenCloseEvents() {
            List list = Arrays.asList(openedTopComponents());
            openedSoFar = new HashSet(list);

            Iterator it = list.iterator();
            while (it.hasNext()) {
                TopComponent tc = (TopComponent) it.next();
                tc.addComponentListener(new ComponentAdapter() {

                    public void componentShown(ComponentEvent e) {
                        TopComponent tcomp = (TopComponent) e.getComponent();
                        tcomp.removeComponentListener(this);
                        handleTopComponentOpened(tcomp);
                    }
                });

            }
        }

        private void handleTopComponentOpened(TopComponent tc) {
//	    System.out.println("[TODO] opened: " + tc.getDisplayName());
            if (tc.isShowing()) {
                // currently selected one
                componentsChanged();
            } else {
                // it is not selected anymore, it was opened in burst
                DataObject dao = extractDataObject(tc);
                if (dao == null) {
                    return;
                }
            //            performRescanInRP(tc, dao, ManagerSettings.getDefault().getShowScanDelay());
            }
        }

        /** The set of visible top components changed */
        private void componentsChanged() {
            // We may receive "changed events" from different sources:
            // componentHidden (which is the only source which tells us
            // when you've switched between two open tabs) and
            // TopComponent.registry's propertyChange on PROP_OPENED
            // (which is the only source telling us about tabs closing).

            // However, there is some overlap - when you open a new
            // tab, we get notified by both. So coalesce these events by
            // enquing a change lookup on the next iteration through the
            // event loop; if a second notification comes in during the
            // same event processing iterationh it's simply discarded.

            prepareRescanInAWT(true);

        }

        /** Reacts to changes */
        public void propertyChange(PropertyChangeEvent ev) {
            TopComponent opened = findActiveEditor();
            FormHandler handler = new FormHandler(opened);
            String prop = ev.getPropertyName();
            if (prop.equals(TopComponent.Registry.PROP_OPENED)) {

//		System.out.println("EVENT opened top-components changed");
                if (isMVCTopComponent(opened)) {
                    handler.check();
                } else {
                    ResultWindowTopComponent.findInstance().getResultPanel().eraseAllEntries();
                }
                //                if (allOpenedClientsCount > 0) {
                // determine what components have been closed, window system does not
                // provide any other listener to do it in more smart way

                List list = Arrays.asList(openedTopComponents());
                Set actual = new HashSet(list);

                if (openedSoFar != null) {
                    Iterator it = openedSoFar.iterator();
                    while (it.hasNext()) {
                        TopComponent tc = (TopComponent) it.next();
                        if (actual.contains(tc)) {
                            continue;
                        }
                        handleTopComponentClosed(tc);
                    }

                    Iterator ita = actual.iterator();
                    while (ita.hasNext()) {
                        TopComponent tc = (TopComponent) ita.next();
                        if (openedSoFar.contains(tc)) {
                            continue;
                        }
                        // defer actual action to componentShown, We need to assure opened TC is
                        // selected one. At this moment previous one is still selected.
                        tc.addComponentListener(new ComponentAdapter() {

                            public void componentShown(ComponentEvent e) {
                                TopComponent tcomp = (TopComponent) e.getComponent();
                                tcomp.removeComponentListener(this);
                                handleTopComponentOpened(tcomp);
                            }
                        });
                    }
                }

                openedSoFar = actual;
            //              } else {
            //                    componentsChanged();
            //                  openedSoFar = null;
            //                }
            } else if (TopComponent.Registry.PROP_ACTIVATED.equals(prop)) {
//		System.out.println("EVENT top-component activated");
                if (isMVCTopComponent(opened)) {
                    handler.check();
                } else {
                    ResultWindowTopComponent.findInstance().getResultPanel().eraseAllEntries();
                    ResultWindowTopComponent.findInstance().setName(java.util.ResourceBundle.getBundle("org/netbeans/modules/a11ychecker/Bundle").getString("Win_no_form_name"));
//                    check() would do everything we need, but to be sure calling those methods explicitly
//                    new FormHandler(FormBroker.getDefault().findActiveEditor()).check();
                }
            //	    TopComponent activated = WindowManager.getDefault().getRegistry().getActivated();
            //	    if (clientCount > 0 && isSelectedEditor(activated) && currentTC == null) {
            //		prepareRescanInAWT(false);
            //	    }
            }
        }

        private void handleTopComponentClosed(TopComponent tc) {
        // TODO: consider calling rechaech here
//            new FormHandler(FormBroker.getDefault().findActiveEditor()).check();
        }

        public void componentShown(ComponentEvent e) {
        // Don't care
        }

        public void componentHidden(ComponentEvent e) {

        //	LOGGER.fine("EVENT " + e.getComponent() + " has been hidden");

        //XXX it does not support both "current file" and "all opened" clients at same time
        //	if (allOpenedClientsCount == 0) {
        //	    componentsChanged();
        //	}
        }

        public void componentResized(ComponentEvent e) {
        // Don't care
        }

        public void componentMoved(ComponentEvent e) {
        // Don't care
        }

        private boolean isSelectedEditor(Component tc) {
            Mode mode = WindowManager.getDefault().findMode(CloneableEditorSupport.EDITOR_MODE);
            TopComponent selected = null;
            if (mode != null) {
                selected = mode.getSelectedTopComponent();
            }
            return selected == tc;
        }
    }
}
