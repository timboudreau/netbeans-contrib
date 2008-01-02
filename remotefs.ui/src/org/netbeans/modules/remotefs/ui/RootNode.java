/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.remotefs.ui;

import java.awt.Image;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import org.netbeans.modules.remotefs.ftpfs.FTPFileSystem;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.Repository;
import org.openide.filesystems.RepositoryEvent;
import org.openide.filesystems.RepositoryListener;
import org.openide.filesystems.RepositoryReorderedEvent;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

public class RootNode extends AbstractNode {

    private List<FTPFileSystem> ftpFileSystems;
    private static final Image ICON = Utilities.loadImage("org/netbeans/modules/remotefs/ui/resources/entire-network-16x16.png");

    public RootNode(List<FTPFileSystem> sites) throws DataObjectNotFoundException {
        super(new RootNodeChildren(sites));
        this.ftpFileSystems = sites;
    }

    @Override
    public boolean canCopy() {
        return false;
    }

    @Override
    public Image getIcon(int type) {
        return ICON;
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public Action[] getActions(boolean context) {
        DataFolder df = getLookup().lookup(DataFolder.class);
        return new Action[]{AddFTPSiteAction.getInstance()};
    }

    @Override
    public String getHtmlDisplayName() {
        return getName();
    }

    @Override
    public String getName() {
        return "FTP Sites";
    }

    public static class RootNodeChildren extends Children.Keys {

        private List<FTPFileSystem> ftpFileSystems;
        private final transient Logger logger = Logger.getLogger(RootNodeChildren.class.getName());

        private RootNodeChildren(List<FTPFileSystem> sites) {
            this.ftpFileSystems = sites;
            setKeys(sites.toArray(new FTPFileSystem[0]));
  /*          Repository.getDefault().addRepositoryListener(new RepositoryListener() {

                        public void fileSystemAdded(RepositoryEvent ev) {
                            logger.log(Level.INFO, "FileSystem added!");
                            FileSystem newFs = ev.getFileSystem();
                            if (newFs instanceof FTPFileSystem) {
                                ftpFileSystems.add((FTPFileSystem)newFs);
                                setKeys(ftpFileSystems.toArray(new FTPFileSystem[0]));
                            }
                        }

                        public void fileSystemRemoved(RepositoryEvent ev) {
                            logger.log(Level.INFO, "FileSystem removed!");
                            FileSystem newFs = ev.getFileSystem();
                            if (newFs instanceof FTPFileSystem) {
                                ftpFileSystems.remove((FTPFileSystem)newFs);
                                setKeys(ftpFileSystems.toArray(new FTPFileSystem[0]));
                            }
                        }

                        public void fileSystemPoolReordered(RepositoryReorderedEvent ev) {
                        //ignore
                        }
                    });
   */     }

        @Override
        protected Node[] createNodes(Object key) {
            try {
                return new Node[]{new SiteNode((FTPFileSystem) key)};
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
                return new Node[]{};
            }
        }

        public void add(FTPFileSystem fsToAdd) {
            ftpFileSystems.add(fsToAdd);
            this.setKeys(ftpFileSystems);
        }
        
        public void remove(FTPFileSystem fsToRemove) {
            ftpFileSystems.remove(fsToRemove);
            this.setKeys(ftpFileSystems);
        }
    }
}
