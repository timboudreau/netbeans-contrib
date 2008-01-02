package org.netbeans.modules.remotefs.ui;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;
import org.netbeans.modules.remotefs.ftpclient.FTPLogInfo;
import org.netbeans.modules.remotefs.ftpfs.FTPFileSystem;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.util.Utilities;
import org.openide.util.actions.SystemAction;

/**
 * Top component which displays something.
 */
final class ExplorerTopComponent extends TopComponent implements ExplorerManager.Provider {

    private static ExplorerTopComponent instance;
    /** path to the icon used by the component and its open action */
    static final String ICON_PATH = "org/netbeans/modules/remotefs/ui/resources/globe-sextant-16x16.png";
    private static final String PREFERRED_ID = "ExplorerTopComponent";
    private transient ExplorerManager manager;
    private Logger logger = Logger.getLogger(ExplorerTopComponent.class.getName());

    private ExplorerTopComponent() {
        {
            try {
                ObjectInputStream is = null;
                initComponents();
                setName(NbBundle.getMessage(ExplorerTopComponent.class, "CTL_ExplorerTopComponent"));
                setToolTipText(NbBundle.getMessage(ExplorerTopComponent.class, "HINT_ExplorerTopComponent"));
                setIcon(Utilities.loadImage(ICON_PATH, true));
                this.manager = new ExplorerManager();
                DataObject find = DataObject.find(Repository.getDefault().getDefaultFileSystem().getRoot().getFileObject("FTPSites"));
                FileObject[] files = find.getPrimaryFile().getChildren();
                List<FTPLogInfo> siteInfos = new ArrayList<FTPLogInfo>();
                for (int i = 0; i < files.length; i++) {
                    try {
                        is = new ObjectInputStream(new FileInputStream(FileUtil.toFile(files[i])));
                        siteInfos.add((FTPLogInfo) is.readObject());
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (ClassNotFoundException ex) {
                        Exceptions.printStackTrace(ex);
                    } finally {
                        if (is != null) {
                            try {
                                is.close();
                            } catch (IOException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                        }
                    }
                }
                List<FTPFileSystem> sites = new ArrayList<FTPFileSystem>();
                for (FTPLogInfo info : siteInfos) {
                    sites.add(new FTPFileSystem(info));
                }
                Node root = new RootNode(sites);
                manager.setRootContext(root);
                ((BeanTreeView) view).setRootVisible(true);
                ((BeanTreeView) view).setDragSource(true);
                ActionMap map = getActionMap();
                map.put(DefaultEditorKit.copyAction, ExplorerUtils.actionCopy(manager));
                map.put(DefaultEditorKit.cutAction, ExplorerUtils.actionCut(manager));
                map.put(DefaultEditorKit.pasteAction, ExplorerUtils.actionPaste(manager));
                map.put("delete", ExplorerUtils.actionDelete(manager, true)); // or false
                map.put("filesystem", getAction(org.openide.actions.FileSystemAction.class));
                InputMap keys = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
                keys.put(KeyStroke.getKeyStroke("control C"), DefaultEditorKit.copyAction);
                keys.put(KeyStroke.getKeyStroke("control X"), DefaultEditorKit.cutAction);
                keys.put(KeyStroke.getKeyStroke("control V"), DefaultEditorKit.pasteAction);
                keys.put(KeyStroke.getKeyStroke("DELETE"), "delete");

                associateLookup(ExplorerUtils.createLookup(manager, map));
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private SystemAction getAction(Class clazz) {
        return (SystemAction) org.openide.util.SharedClassObject.findObject(clazz, true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents

    private void initComponents() {



        view = new BeanTreeView();



        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);

        this.setLayout(layout);

        layout.setHorizontalGroup(

            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)

            .add(view, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)

        );

        layout.setVerticalGroup(

            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)

            .add(view, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)

        );

    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables

    private javax.swing.JScrollPane view;

    // End of variables declaration//GEN-END:variables
    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link findInstance}.
     */
    public static synchronized ExplorerTopComponent getDefault() {
        if (instance == null) {
            instance = new ExplorerTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the ExplorerTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized ExplorerTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(ExplorerTopComponent.class.getName()).warning("Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof ExplorerTopComponent) {
            return (ExplorerTopComponent) win;
        }
        Logger.getLogger(ExplorerTopComponent.class.getName()).warning("There seem to be multiple components with the '" + PREFERRED_ID +
                "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    public 
    @Override
    int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    public 
    @Override
    void componentOpened() {
        ExplorerUtils.activateActions(manager, true);
        view.requestFocusInWindow();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        ExplorerUtils.activateActions(manager, true);
    }

    @Override
    public void removeNotify() {
        ExplorerUtils.activateActions(manager, false);
        super.removeNotify();
    }

    public 
    @Override
    void componentClosed() {
        ExplorerUtils.activateActions(manager, false);
    }

    /** replaces this in object stream */
    public 
    @Override
    Object writeReplace() {
        return new ResolvableHelper();
    }

    protected 
    @Override
    String preferredID() {
        return PREFERRED_ID;
    }

    @Override
    public Action[] getActions() {
        return super.getActions();
    }

    public ExplorerManager getExplorerManager() {
        return manager;
    }

    final static class ResolvableHelper implements Serializable {

        private static final long serialVersionUID = 1L;

        public Object readResolve() {
            return ExplorerTopComponent.getDefault();
        }
    }
}
