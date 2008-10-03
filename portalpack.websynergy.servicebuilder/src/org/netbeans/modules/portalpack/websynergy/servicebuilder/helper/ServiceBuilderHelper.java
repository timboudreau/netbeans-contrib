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

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Column;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.Entity;
import org.netbeans.modules.portalpack.websynergy.servicebuilder.beans.ServiceBuilder;
import org.netbeans.modules.schema2beans.BaseBean;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.util.Exceptions;

/**
 *
 * @author satyaranjan
 */
public class ServiceBuilderHelper {

    private FileObject serviceBuilderFile;
    private ServiceBuilder serviceBuilder;
    private long timestamp;
    private ServerXMLFileListener listener;

    public ServiceBuilderHelper(FileObject serviceBuilderFile) {

        this.serviceBuilderFile = serviceBuilderFile;
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
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    public boolean isDirty() {
        
        if(serviceBuilderFile == null) return false;
        
        long newTimeStamp = serviceBuilderFile.lastModified().getTime();
        if(newTimeStamp > timestamp) {
            init();
            System.out.println("Reload Service.xml *************************************************");
            return true;
        }
        return false;
    }
    
    public Entity newEntity() {
        isDirty();
        return serviceBuilder.newEntity();
    }
    public void addEntity(String entityName) {
        isDirty();
        Entity entity = serviceBuilder.newEntity();
        entity.setName(entityName);
        serviceBuilder.addEntity(entity);
        save();
    }
    
    public void addEntity(Entity entity) {
        isDirty();
        if(entity == null)
            return;
        serviceBuilder.addEntity(entity);
        save();
    }
    
    public void  removeEntity(Entity entity) {
        isDirty();
        if(entity == null)
            return;
        serviceBuilder.removeEntity(entity);
        save();
    }
    
    public Entity[] getEntity() {
        isDirty();
        return serviceBuilder.getEntity();
    }
    
    public String getPackagePath() {
        isDirty();
        return serviceBuilder.getPackagePath();
    }
    
    public void setPackagePath(String packagePath) {
        isDirty();
        serviceBuilder.setPackagePath(packagePath);
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
    
    public void removeColumn(Entity entity,Column col) {
        
        //entity.removeColumn
       
    }
    
    public String getNamespace() {
        isDirty();
        return serviceBuilder.getNamespace();
    }
    
    public void setNamespace(String namespace) {
        isDirty();
        serviceBuilder.setNamespace(namespace);
    }
    
    private ServiceBuilder getServiceBuilder() {
        return serviceBuilder;
    }

    public void save() {
        try {

            FileLock lock = serviceBuilderFile.lock();
            OutputStream out = serviceBuilderFile.getOutputStream(lock);
            
            ServiceBuilderFactory.write(serviceBuilder, out);
            try {
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            lock.releaseLock();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            timestamp = serviceBuilderFile.lastModified().getTime();
        }

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
