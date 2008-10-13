/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.portalpack.websynergy.servicebuilder.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Column;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Entity;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Finder;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.ServiceBuilder;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.loader.ServiceBuilderDataObject;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileAlreadyLockedException;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author satyaranjan
 */
public class ServiceBuilderHelper {

    private FileObject serviceBuilderFile;
    private ServiceBuilderDataObject dataObj;
    private ServiceBuilder serviceBuilder;
    private long timestamp;
    private boolean validXML;
    private String errorMsg = "";
            
    private ServerXMLFileListener listener;

    public ServiceBuilderHelper(ServiceBuilderDataObject dbObj) {

        this.dataObj = dbObj;
        if(dbObj == null)
            return;
        this.serviceBuilderFile = dbObj.getPrimaryFile();
        init();
        listener = new ServerXMLFileListener();
        serviceBuilderFile.addFileChangeListener(listener);
    }
    
    private void init() {
        
        if(serviceBuilderFile != null) {
            timestamp = serviceBuilderFile.lastModified().getTime();
        }
        try {
            serviceBuilder = ServiceBuilderFactory.createGraph(serviceBuilderFile);
            validXML = true;
            errorMsg = "";
        } catch (Exception ex) {
            validXML = false;
            errorMsg = ex.getMessage();
            ex.printStackTrace();
        }
    }
    
    public void init(InputStream input) {
        
        timestamp = serviceBuilderFile.lastModified().getTime();
        try {
            serviceBuilder = ServiceBuilderFactory.createGraph(input);
            validXML = true;
            errorMsg = "";
        } catch (Exception ex) {
            validXML = false;
            errorMsg = ex.getMessage();
            ex.printStackTrace();
        }
    }
    
    public boolean isValidXML() {
        return validXML;
    }
    
    public String getErrorMessage() {
        return errorMsg;
    }
    public boolean isDirty() {
        
        if(serviceBuilderFile == null) return false;
        
        if(isEditingMode())
            return true;
        
        long newTimeStamp = serviceBuilderFile.lastModified().getTime();
        if(newTimeStamp > timestamp) {
            init();
            System.out.println("Reload Service.xml *************************************************");
            return true;
        }
        return false;
    }
    
    private boolean isEditingMode() {
        
        if ((dataObj.getEditorSupport() != null) && (dataObj.getEditorSupport().isDocumentLoaded())
                && dataObj.getEditorSupport().isModified()) {
            try {
                // loading from the memory (Document)
                InputStream io = dataObj.getEditorSupport().getInputStream();
                System.out.println("Document is modified in memory...so reload plz............");
                init(io);
                return true;
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        
        return false;
    }
    
    public void forceReload() {
        init();
    }
    
    public Entity newEntity() {
        isDirty();
        return serviceBuilder.newEntity();
    }
    public boolean addEntity(String entityName) {
        isDirty();
        Entity entity = serviceBuilder.newEntity();
        entity.setName(entityName);
        serviceBuilder.addEntity(entity);
        
        return save();
    }
    
    public boolean addEntity(Entity entity) {
        isDirty();
        if(entity == null)
            return false;
        serviceBuilder.addEntity(entity);
        
        return save();
    }
    
    public boolean removeEntity(Entity entity) {
        isDirty();
        if(entity == null)
            return false;
        serviceBuilder.removeEntity(entity);
        
        return save();
    }
    
    public Entity[] getEntity() {
        isDirty();
        return serviceBuilder.getEntity();
    }
    
    public Entity getEntity(String name) {
        
        Entity[] ens = getEntity();
        for(Entity en:ens) {
            
            if(en.getName().equals(name))
                return en;
        }
        return null;
    }
    
    public String getPackagePath() {
        isDirty();
        return serviceBuilder.getPackagePath();
    }
    
    public boolean setPackagePath(String packagePath) {
        isDirty();
        serviceBuilder.setPackagePath(packagePath);
        return save();
    }
    
    public List<Column> getColumns(Entity entity) {

        if (entity == null) {
            return new ArrayList();
        }
        
        Column[] cls = entity.getColumn();
        return Arrays.asList(cls);
        /*
        List<Column> cols = new ArrayList();
        
        for(int i=0;i<entity.sizeColumn();i++) {
            Column col = new Column();
            col.setName(entity.getColumnName(i));
            col.setDbName(entity.getColumnDbName(i));
            col.setPrimaryKey(entity.getColumnPrimary(i));
            col.setType(entity.getColumnType(i));
            cols.add(col);
        }
      //  String[] name = entity.getColumnName();
        /*BaseBean bean = (BaseBean)entity;
        
        BaseBean[] childBeans = bean.childBeans(false);
        
        List<Column> cols = new ArrayList();
        for (int i = 0; i < childBeans.length; i++) {
            String elmName = childBeans[i].name();
            if(!elmName.equalsIgnoreCase("column"))
                continue;
            
            Column col = new Column();
            String  name = childBeans[i].getAttributeValue("name");
            col.setName(name);
            
            String db_name = childBeans[i].getAttributeValue("db_name");
            if(db_name != null && db_name.trim().length() != 0)
                col.setDbName(db_name);
            
            String type = childBeans[i].getAttributeValue("type");
            if(type != null && type.trim().length() != 0)
                col.setType(type);
            
            String pk = childBeans[i].getAttributeValue("primary_key");
            if(pk != null && pk.trim().length() != 0)
                col.setPrimaryKey(pk);
            
            cols.add(col);
        }*/
        //return cols;
    }
    
    public Column getColumn(Entity entity, String column) {
        
        if(entity == null || column == null)
            return null;
        Column[] cols = entity.getColumn();
        for(Column col:cols) {
            
            if(column.equals(col.getName()))
                return col;
        }
        return null;
    }
    
    public void removeColumn(Entity entity,Column col) {
        
        //entity.removeColumn
        entity.removeColumn(col);
       
    }
    
    public void removeFinders(Entity entity,String[] finderNames) {
        
        Finder[] finders = entity.getFinder();
        
        for(Finder finder:finders) {
            
            for(String name:finderNames) {
                if(!finder.getName().equals(name))
                    continue;
                entity.removeFinder(finder);
            }
        }
    }
    
    public Finder getFinder(Entity entity,String finderName) {
        Finder[] finders = entity.getFinder();
        
        for(Finder finder:finders) {
            
            if(finder.getName().equals(finderName))
                return finder;
        }
        return null;
    }
    public String getNamespace() {
        isDirty();
        return serviceBuilder.getNamespace();
    }
    
    public boolean setNamespace(String namespace) {
        isDirty();
        serviceBuilder.setNamespace(namespace);
        return save();
    }
    
    private ServiceBuilder getServiceBuilder() {
        return serviceBuilder;
    }
    
    public Project getProject() {
        
        return FileOwnerQuery.getOwner(serviceBuilderFile);
    }

    public boolean save() {
        try {
            FileLock lock = null;
            try{

                lock = serviceBuilderFile.lock();
            }catch(FileAlreadyLockedException fle) {
                NotifyDescriptor nd = new NotifyDescriptor.Message(
                        NbBundle.getMessage(ServiceBuilderHelper.class, "MSG_MODIFIED_OUTSIDE"), NotifyDescriptor.WARNING_MESSAGE);
                DialogDisplayer.getDefault().notifyLater(nd);
                return false;
            }
            OutputStream out = serviceBuilderFile.getOutputStream(lock);
            
            ServiceBuilderFactory.write(serviceBuilder, out);
            try {
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            lock.releaseLock();
            
            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            timestamp = serviceBuilderFile.lastModified().getTime();
        }
        
        return false;
        
    }
    
    private static class ServerXMLFileListener implements FileChangeListener {

        public void fileFolderCreated(FileEvent fe) {
            
        }

        public void fileDataCreated(FileEvent fe) {
            
        }

        public void fileChanged(FileEvent fe) {
            
        }

        public void fileDeleted(FileEvent fe) {
            
        }

        public void fileRenamed(FileRenameEvent fe) {
            
        }

        public void fileAttributeChanged(FileAttributeEvent fe) {
            
        }    
    }
}
