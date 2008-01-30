/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.

 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.clearcase.client.status;

import java.io.File;


/**
 * Holds detailed information about status of a managed file, ie repository URL, remote path, branch, etc.
 * 
 * @author Maros Sandor
 */
public class FileStatus {
   
    private String stringValue;
    
    static String TYPE_VERSION = "version";
    static String TYPE_DIRECTORY_VERSION = "directory version";
    static String TYPE_FILE_ELEMENT = "file element";
    static String TYPE_DIRECTORY_ELEMENT = "directory element";
    static String TYPE_VIEW_PRIVATE_OBJECT = "view private object";
    static String TYPE_DERIVED_OBJECT = "derived object";
    static String TYPE_DERIVED_OBJECT_VERSION = "derived object version";
    static String TYPE_SYMBOLIC_LINK = "symbolic link";       
    static String TYPE_UNKNOWN = null;
    
    private static String ANNOTATION_ECLIPSED = "eclipsed";           
    private static String ANNOTATION_HIJACKED = "hijacked";        
    private static String ANNOTATION_LOADED_BUT_MISSING = "loaded but missing";       
    private static String ANNOTATION_CHECKEDOUT_BUT_REMOVED = "checkedout but removed";       
    
    static String CHECKEDOUT = "CHECKEDOUT";                   
        
    public enum ClearcaseStatus {        
        REPOSITORY_STATUS_UNKNOWN,
        REPOSITORY_STATUS_VIEW_PRIVATE,                      
        REPOSITORY_STATUS_FILE_CHECKEDOUT_RESERVED,         
        REPOSITORY_STATUS_FILE_CHECKEDOUT_UNRESERVED,                               
        REPOSITORY_STATUS_FILE_CHECKEDOUT_BUT_REMOVED,                               
        REPOSITORY_STATUS_FILE_LOADED_BUT_MISSING,                               
        REPOSITORY_STATUS_FILE_HIJACKED,        
        REPOSITORY_STATUS_FILE_ECLIPSED
    } 
    
    final private String type;    
    final private File file;    
    final private FileVersionSelector originVersion;
    final private FileVersionSelector version;
    final private String annotation;
    final boolean modified;
    
    private ClearcaseStatus status;

    
    public FileStatus(String type, File file, FileVersionSelector originVersion, FileVersionSelector version, String annotation, boolean modified) {
        this.type = type;
        this.file = file;
        this.originVersion = originVersion;
        this.version = version;
        this.annotation = annotation;
        this.modified = modified;
        this.status = getRepositoryStatus(type, annotation, version, modified);
    }

    public FileStatus() { 
        this.type = null;
        this.file = null;
        this.originVersion = null;
        this.version = null;
        this.annotation = null;  
        this.modified = false;
        status = ClearcaseStatus.REPOSITORY_STATUS_UNKNOWN;
    }
    
    private ClearcaseStatus getRepositoryStatus(String type, String annotation, FileVersionSelector version, boolean reserved) {
                
        if(type.equals(TYPE_VIEW_PRIVATE_OBJECT)) {
            return ClearcaseStatus.REPOSITORY_STATUS_VIEW_PRIVATE;
        } else if (version != null && version.getVersionNumber() == FileVersionSelector.CHECKEDOUT_VERSION) {
            if (annotation != null && annotation.indexOf(ANNOTATION_CHECKEDOUT_BUT_REMOVED) > -1) {            
                return ClearcaseStatus.REPOSITORY_STATUS_FILE_CHECKEDOUT_BUT_REMOVED;                       
            }
            return reserved ? ClearcaseStatus.REPOSITORY_STATUS_FILE_CHECKEDOUT_RESERVED :                               ClearcaseStatus.REPOSITORY_STATUS_FILE_CHECKEDOUT_UNRESERVED;                        
        } else if (annotation != null && annotation.indexOf(ANNOTATION_LOADED_BUT_MISSING) > -1) {  
            // XXX now think carefully !!! - could it be there are more of the ANNOTATOIN_ string in one annotation? - e.g. [eclipsed, hijacked] ?            
            return ClearcaseStatus.REPOSITORY_STATUS_FILE_LOADED_BUT_MISSING;                                               
        } else if (annotation != null && annotation.indexOf(ANNOTATION_HIJACKED) > -1) {                                                
            return ClearcaseStatus.REPOSITORY_STATUS_FILE_HIJACKED;                    
        } else if (annotation != null && annotation.indexOf(ANNOTATION_ECLIPSED) > -1) {            
            return ClearcaseStatus.REPOSITORY_STATUS_FILE_ECLIPSED;            
        }
        return ClearcaseStatus.REPOSITORY_STATUS_UNKNOWN;
    }
    
    public File getFile() {
        return file;
    }

    public String getType() {
        return type;
    }

    // XXX do we need this?
    public long getOriginVersionNumber() {
        if(version == null) {
            return -1;
        }                
        if(originVersion != null) {
            return originVersion.getVersionNumber();   
        } else {
            return version.getVersionNumber();            
        }        
    }

    // XXX do we need this?
    public String getOriginPath() {
        if(version == null) {
            return null;
        }
        if(originVersion != null) {
            return originVersion.getPath();   
        } else {
            return version.getPath();            
        }        
    }
    
    // XXX do we need this?    
    public FileVersionSelector getVersion() {
        return version;
    }    

    // XXX do we need this?    
    public FileVersionSelector getOriginVersion() {
        return originVersion;
    }
    
    public boolean isCheckedout() {
        if(version == null) {
            return false;
        }        
        return version.getVersionNumber() == FileVersionSelector.CHECKEDOUT_VERSION;
    }
    
    public String getVersionSelector() {
        if(version == null) {
            return null;
        }
        if(isCheckedout() && originVersion != null) {
            return originVersion.getVersionSelector();
        } else {
            return version.getVersionSelector();
        }
    }
    
    public ClearcaseStatus getStatus() {
        return status;
    }
    
    @Override
    public String toString() {
        if(stringValue == null) {
            StringBuffer sb = new StringBuffer();
            sb.append("[");        
            sb.append(file.getAbsolutePath());
            sb.append(",");
            sb.append(type);
            sb.append(",");
            if(version != null) {
                sb.append(",");
                sb.append(version.getPath());
                sb.append(File.pathSeparator);
                sb.append(version.getVersionNumber());
            }              
            if(originVersion != null) {
                sb.append(",");
                sb.append(originVersion.getPath());
                sb.append(File.pathSeparator);
                sb.append(originVersion.getVersionNumber());
            }                        
            if(modified) {
                sb.append(",modified");
            }            
            sb.append("]");        
            stringValue = sb.toString();
        }
        return stringValue;
    }

    @Override
    public int hashCode() {        
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FileStatus other = (FileStatus) obj;
        if (this.stringValue != other.stringValue && (this.stringValue == null || !this.stringValue.equals(other.stringValue))) {
            return false;
        }
        return true;
    }
    
}
