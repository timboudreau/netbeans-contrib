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

package org.netbeans.modules.groovy.groovyproject.ui.customizer;

import java.io.File;
import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.api.java.platform.Specification;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.libraries.LibraryManager;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.api.project.ant.AntArtifact;
import org.netbeans.api.queries.CollocationQuery;
import org.netbeans.modules.groovy.groovyproject.GroovyProjectType;
import org.netbeans.spi.project.SubprojectProvider;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.netbeans.spi.project.support.ant.ReferenceHelper;
import org.openide.ErrorManager;
import org.openide.filesystems.FileUtil;
import org.openide.modules.SpecificationVersion;
import org.openide.util.MutexException;
import org.openide.util.Mutex;
import org.openide.util.NbBundle;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/** Helper class. Defines constants for properties. Knows the proper
 *  place where to store the properties.
 * 
 * @author Petr Hrebejk
 */
public class GroovyProjectProperties {
    
    // Special properties of the project
    public static final String J2SE_PROJECT_NAME = "j2se.project.name"; // NOI18N
    public static final String JAVA_PLATFORM = "platform.active"; // NOI18N
    
    // Properties stored in the PROJECT.PROPERTIES    
    public static final String DIST_DIR = "dist.dir"; // NOI18N
    public static final String DIST_JAR = "dist.jar"; // NOI18N
    public static final String JAVAC_CLASSPATH = "javac.classpath"; // NOI18N
    public static final String RUN_CLASSPATH = "run.classpath"; // NOI18N
    public static final String RUN_JVM_ARGS = "run.jvmargs"; // NOI18N
    public static final String RUN_WORK_DIR = "work.dir"; // NOI18N
    public static final String DEBUG_CLASSPATH = "debug.classpath"; // NOI18N
    public static final String JAR_COMPRESS = "jar.compress"; // NOI18N
    public static final String MAIN_SCRIPT = "main.script"; // NOI18N
    public static final String JAVAC_SOURCE = "javac.source"; // NOI18N
    public static final String JAVAC_TARGET = "javac.target"; // NOI18N
    public static final String JAVAC_DEBUG = "javac.debug"; // NOI18N
    public static final String JAVAC_DEPRECATION = "javac.deprecation"; // NOI18N
    public static final String JAVAC_COMPILER_ARG = "javac.compilerargs";    //NOI18N
    public static final String SRC_DIR = "src.dir"; // NOI18N
    public static final String BUILD_DIR = "build.dir"; // NOI18N
    public static final String BUILD_CLASSES_DIR = "build.classes.dir"; // NOI18N
    public static final String BUILD_CLASSES_EXCLUDES = "build.classes.excludes"; // NOI18N
    public static final String NO_DEPENDENCIES="no.dependencies"; // NOI18N
            
    // Properties stored in the PRIVATE.PROPERTIES
    public static final String APPLICATION_ARGS = "application.args"; // NOI18N
    
    // Shortcuts 
    private static final String PROJECT = AntProjectHelper.PROJECT_PROPERTIES_PATH;
    private static final String PRIVATE = AntProjectHelper.PRIVATE_PROPERTIES_PATH;
    
    private static final int IDX_PROJECT = 0;
    private static final int IDX_PRIVATE = 1;

    private static final PropertyParser STRING_PARSER = new StringParser();
    private static final BooleanParser BOOLEAN_PARSER = new BooleanParser();
    private static final InverseBooleanParser INVERSE_BOOLEAN_PARSER = new InverseBooleanParser();
    private static final PropertyParser PATH_PARSER = new PathParser();
    private static final PlatformParser PLATFORM_PARSER = new PlatformParser();
    
    // Info about the property destination
    private PropertyDescriptor PROPERTY_DESCRIPTORS[] = {
        new PropertyDescriptor( J2SE_PROJECT_NAME, null, STRING_PARSER ),
                
        new PropertyDescriptor( DIST_DIR, PROJECT, STRING_PARSER ),
        new PropertyDescriptor( DIST_JAR, PROJECT, STRING_PARSER ),
        new PropertyDescriptor( JAVAC_CLASSPATH, PROJECT, PATH_PARSER ),
        new PropertyDescriptor( RUN_CLASSPATH, PROJECT, PATH_PARSER ),
        new PropertyDescriptor( RUN_JVM_ARGS, PROJECT, STRING_PARSER ),
        new PropertyDescriptor( RUN_WORK_DIR, PRIVATE, STRING_PARSER ),
        new PropertyDescriptor( DEBUG_CLASSPATH, PROJECT, PATH_PARSER ),
        new PropertyDescriptor( JAR_COMPRESS, PROJECT, BOOLEAN_PARSER ),
        new PropertyDescriptor( MAIN_SCRIPT, PROJECT, STRING_PARSER ),
        new PropertyDescriptor( JAVAC_DEBUG, PRIVATE, BOOLEAN_PARSER ),       
        new PropertyDescriptor( JAVAC_DEPRECATION, PROJECT, BOOLEAN_PARSER ),
        new PropertyDescriptor( JAVAC_COMPILER_ARG, PROJECT, STRING_PARSER),
        new PropertyDescriptor( SRC_DIR, PROJECT, STRING_PARSER ),
        new PropertyDescriptor( BUILD_DIR, PROJECT, STRING_PARSER ),
        new PropertyDescriptor( BUILD_CLASSES_DIR, PROJECT, STRING_PARSER ),
        new PropertyDescriptor( BUILD_CLASSES_EXCLUDES, PROJECT, STRING_PARSER ),
        new PropertyDescriptor( APPLICATION_ARGS, PRIVATE, STRING_PARSER ),          
        new PropertyDescriptor( NO_DEPENDENCIES, PROJECT, INVERSE_BOOLEAN_PARSER ),
        new PropertyDescriptor( JAVA_PLATFORM, PROJECT, PLATFORM_PARSER ),
        
    };
    
    
    // Private fields ----------------------------------------------------------
    
    private Project project;
    private HashMap properties;    
    private AntProjectHelper antProjectHelper;
    private PropertyEvaluator evaluator;
    private ReferenceHelper refHelper;
    
    public GroovyProjectProperties( Project project, AntProjectHelper antProjectHelper, PropertyEvaluator evaluator, ReferenceHelper refHelper ) {
        this.project = project;
        this.properties = new HashMap();
        this.antProjectHelper = antProjectHelper;
        this.evaluator = evaluator;
        this.refHelper = refHelper;
        read();                                
    }
    
    /** XXX to be deleted when introduced in AntPropertyHeleper API
     */    
    static String getAntPropertyName( String property ) {
        if ( property != null && 
             property.startsWith( "${" ) && // NOI18N
             property.endsWith( "}" ) ) { // NOI18N
            return property.substring( 2, property.length() - 1 ); 
        }
        else {
            return property;
        }
    }
    
    static boolean isAntProperty (String string) {
        return string != null && string.startsWith( "${" ) && string.endsWith( "}" ); //NOI18N
    }
    
    public void put( String propertyName, Object value ) {
        PropertyInfo pi = (PropertyInfo)properties.get( propertyName );
        assert propertyName != null : "Unknown property " + propertyName; // NOI18N
        pi.setValue( value );
    }
    
    public Object get( String propertyName ) {
        PropertyInfo pi = (PropertyInfo)properties.get( propertyName );
        assert propertyName != null : "Unknown property " + propertyName; // NOI18N
        return pi.getValue();
    }
    
    public boolean isModified( String propertyName ) {
        PropertyInfo pi = (PropertyInfo)properties.get( propertyName );
        assert propertyName != null : "Unknown property " + propertyName; // NOI18N
        return pi.isModified();
    }
    
    public List getSortedSubprojectsList() {
             
        ArrayList subprojects = new ArrayList( 5 );
        addSubprojects( project, subprojects ); // Find the projects recursively
         
        // Replace projects in the list with formated names
        for ( int i = 0; i < subprojects.size(); i++ ) {
            Project p = (Project)subprojects.get( i );           
            subprojects.set(i, ProjectUtils.getInformation(p).getDisplayName());
        }
        
        // Sort the list
        Collections.sort( subprojects, Collator.getInstance() );
        
        return subprojects;
    }
    
    Project getProject() {
        return project;
    }
    
    /** Gets all subprojects recursively
     */
    private void addSubprojects( Project project, List result ) {
        
        SubprojectProvider spp = (SubprojectProvider)project.getLookup().lookup( SubprojectProvider.class );
        
        if ( spp == null ) {
            return;
        }
        
        for( Iterator/*<Project>*/ it = spp.getSubprojects().iterator(); it.hasNext(); ) {
            Project sp = (Project)it.next(); 
            if ( !result.contains( sp ) ) {
                result.add( sp );
            }
            addSubprojects( sp, result );            
        }
        
    }

    /** Reads all the properties of the project and converts them to objects
     * suitable for usage in the GUI controls.
     */    
    private void read() {
        
        // Read the properties from the project             
        HashMap eProps = new HashMap( 2 );
        eProps.put( PROJECT, antProjectHelper.getProperties( PROJECT ) ); 
        eProps.put( PRIVATE, antProjectHelper.getProperties( PRIVATE ) );        
        
        // Initialize the property map with objects
        for ( int i = 0; i < PROPERTY_DESCRIPTORS.length; i++ ) {
            PropertyDescriptor pd = PROPERTY_DESCRIPTORS[i];
            if ( pd.dest == null ) {
                // Specialy handled properties
                if ( J2SE_PROJECT_NAME.equals( pd.name ) ) {
                    String projectName = ProjectUtils.getInformation(project).getDisplayName();
                    properties.put( pd.name, new PropertyInfo( pd, projectName, projectName ) );            
                }
            }
            else {
                // Standard properties
                String raw = ((EditableProperties)eProps.get( pd.dest )).getProperty( pd.name );                
                if ( pd.dest == PRIVATE && raw == null ) {
                    // Can still be found in the project properties
                    raw = ((EditableProperties)eProps.get( PROJECT )).getProperty( pd.name );
                }                
                String eval = evaluator.getProperty(pd.name);
                properties.put( pd.name, new PropertyInfo( pd, raw, eval ) );            
            }
        }
    }
    
    /** Transforms all the Objects from GUI controls into String Ant 
     * properties and stores them in the project
     */    
    public void store() {
        
        try {
            ProjectManager.mutex().writeAccess(new Mutex.ExceptionAction() {
                public Object run() throws IOException {
          
                    resolveProjectDependencies();
                    
                    Boolean defaultPlatform = null;
                    
                    // Some properties need special handling e.g. if the 
                    // property changes the project.xml files                   
                    for( Iterator it = properties.values().iterator(); it.hasNext(); ) {
                        PropertyInfo pi = (PropertyInfo)it.next();
                        PropertyDescriptor pd = pi.getPropertyDescriptor();
                        pi.encode();
                        String newValueEncoded = pi.getNewValueEncoded();
                        if( pd.dest == null && newValueEncoded != null ) {
                            // Specialy handled properties
                            if ( J2SE_PROJECT_NAME.equals( pd.name ) ) {
                                String newName = newValueEncoded;
                                assert false : "No support yet for changing name of J2SEProject; cf. J2SEProject.setName"; // NOI18N
                            }
                            
                        }   
                        if ( JAVA_PLATFORM.equals( pd.name) && newValueEncoded != null ) {
                            defaultPlatform = Boolean.valueOf(pi.getNewValueEncoded().equals(
                                    JavaPlatformManager.getDefault().getDefaultPlatform().getProperties().get("platform.ant.name"))); // NOI18N
                             setPlatform(defaultPlatform.booleanValue(), pi.getNewValueEncoded());
                        }
                    }
                    
                    // Reread the properties. It may have changed when
                    // e.g. when setting references to another projects
                    HashMap eProps = new HashMap( 2 );
                    eProps.put( PROJECT, antProjectHelper.getProperties( PROJECT ) ); 
                    eProps.put( PRIVATE, antProjectHelper.getProperties( PRIVATE ) );
                    
                    // Set the changed properties
                    for( Iterator it = properties.values().iterator(); it.hasNext(); ) {
                        PropertyInfo pi = (PropertyInfo)it.next();
                        PropertyDescriptor pd = pi.getPropertyDescriptor();                        
                        String newValueEncoded = pi.getNewValueEncoded();
                        if ( newValueEncoded != null ) {                            
                            if ( pd.dest != null ) {
                                // Standard properties
                                EditableProperties ep = (EditableProperties)eProps.get(pd.dest);
                                if (PATH_PARSER.equals(pd.parser)) {
                                    // XXX: perhaps PATH_PARSER could return List of paths so that
                                    // tokenizing could be omitted here:
                                    String[] items = PropertyUtils.tokenizePath(newValueEncoded);
                                    for (int i=0; i<items.length-1; i++) {
                                        // XXX use ':' on all platforms
                                        items[i] += File.pathSeparatorChar;
                                    }
                                    ep.setProperty(pd.name, items);
                                } else {
                                    
                                    // update javac.source and javac.target
                                    if (JAVA_PLATFORM.equals(pd.name)) {
                                        assert defaultPlatform != null;
                                        updateSourceLevel(defaultPlatform.booleanValue(), newValueEncoded, ep);
                                    }
                                    
                                    if (NO_DEPENDENCIES.equals(pd.name) && newValueEncoded.equals("false")) { // NOI18N
                                        ep.remove(pd.name);
                                        continue;
                                    }
                                    
                                    if (RUN_WORK_DIR.equals(pd.name) && newValueEncoded.equals("")) { // NOI18N
                                        ep.remove(pd.name);
                                        continue;
                                    }
                                    
                                    ep.setProperty( pd.name, newValueEncoded );
                                }
                            }
                        }
                    }
                    
                    // Store the property changes into the project
                    antProjectHelper.putProperties( PROJECT, (EditableProperties)eProps.get( PROJECT ) );
                    antProjectHelper.putProperties( PRIVATE, (EditableProperties)eProps.get( PRIVATE ) );
                    
                    return null;
                }
            });
        } 
        catch (MutexException e) {
            ErrorManager.getDefault().notify((IOException)e.getException());
        }
        
    }

    private void updateSourceLevel(boolean defaultPlatform, String platform, EditableProperties ep) {
        if (defaultPlatform) {
            ep.setProperty(JAVAC_SOURCE, "${default.javac.source}"); //NOI18N
            ep.setProperty(JAVAC_TARGET, "${default.javac.target}"); //NOI18N
        } else {
            JavaPlatform[] platforms = JavaPlatformManager.getDefault().getInstalledPlatforms();
            for( int i = 0; i < platforms.length; i++ ) {
                Specification spec = platforms[i].getSpecification();
                if (!("j2se".equalsIgnoreCase(spec.getName()))) { // NOI18N
                    continue;
                }
                if (platform.equals(platforms[i].getProperties().get("platform.ant.name"))) { //NOI18N
                    String ver = platforms[i].getSpecification().getVersion().toString();
                    ep.setProperty(JAVAC_SOURCE, ver);
                    ep.setProperty(JAVAC_TARGET, ver);
                    return;
                }
            }
            // The platform does not exist. Perhaps this is project with broken references?
            // Do not update target and source because nothing is known about the platform.
        }
    }
    
    private final SpecificationVersion JDKSpec13 = new SpecificationVersion("1.3"); // NOI18N
    
    private void setPlatform(boolean isDefault, String platformAntID) {
        Element pcd = antProjectHelper.getPrimaryConfigurationData( true );
        NodeList sps = pcd.getElementsByTagNameNS(GroovyProjectType.PROJECT_CONFIGURATION_NAMESPACE, "explicit-platform"); // NOI18N
        if (isDefault && sps.getLength() > 0) {
            pcd.removeChild(sps.item(0));
        } else if (!isDefault) {
            Element el;
            if (sps.getLength() == 0) {
                el = pcd.getOwnerDocument().createElementNS(GroovyProjectType.PROJECT_CONFIGURATION_NAMESPACE, "explicit-platform"); // NOI18N
                pcd.appendChild(el);
            } else {
                el = (Element)sps.item(0);
            }
            boolean explicitSource = true;
            JavaPlatform platform = findPlatform(platformAntID);
            if ((platform != null && platform.getSpecification().getVersion().compareTo(JDKSpec13) <= 0) || platform == null) {
                explicitSource = false;
            }
            el.setAttribute("explicit-source-supported", explicitSource ? "true" : "false"); // NOI18N
        }
        antProjectHelper.putPrimaryConfigurationData(pcd, true);
    }
    
    /** Finds out what are new and removed project dependencies and 
     * applyes the info to the project
     */
    private void resolveProjectDependencies() {
    
        String allPaths[] = { JAVAC_CLASSPATH,  RUN_CLASSPATH, DEBUG_CLASSPATH };
        
        // Create a set of old and new artifacts.
        Set oldArtifacts = new HashSet();
        Set newArtifacts = new HashSet();
        for ( int i = 0; i < allPaths.length; i++ ) {            
            PropertyInfo pi = (PropertyInfo)properties.get( allPaths[i] );

            // Get original artifacts
            List oldList = (List)pi.getOldValue();
            if ( oldList != null ) {
                oldArtifacts.addAll(oldList);
            }
            
            // Get artifacts after the edit
            List newList = (List)pi.getValue();
            if ( newList != null ) {
                newArtifacts.addAll(newList);
            }
                        
        }

        // Create set of removed artifacts and remove them
        Set removed = new HashSet( oldArtifacts );
        removed.removeAll( newArtifacts );
        Set added = new HashSet(newArtifacts);
        added.removeAll(oldArtifacts);
        
        // 1. first remove all project references. The method will modify
        // project property files, so it must be done separately
        for( Iterator it = removed.iterator(); it.hasNext(); ) {
            VisualClassPathItem vcpi = (VisualClassPathItem)it.next();
            if ( vcpi.getType() == VisualClassPathItem.TYPE_ARTIFACT ||
                    vcpi.getType() == VisualClassPathItem.TYPE_JAR ) {
                refHelper.destroyReference(vcpi.getRaw());
            }
        }
        
        // 2. now read project.properties and modify rest
        EditableProperties ep = antProjectHelper.getProperties( PROJECT );
        boolean changed = false;
        
        for( Iterator it = removed.iterator(); it.hasNext(); ) {
            VisualClassPathItem vcpi = (VisualClassPathItem)it.next();
            if (vcpi.getType() == VisualClassPathItem.TYPE_LIBRARY) {
                // remove helper property pointing to library jar if there is any
                String prop = vcpi.getRaw();
                prop = prop.substring(2, prop.length()-1);
                ep.remove(prop);
                changed = true;
            }
        }
        File projDir = FileUtil.toFile(antProjectHelper.getProjectDirectory());
        for( Iterator it = added.iterator(); it.hasNext(); ) {
            VisualClassPathItem vcpi = (VisualClassPathItem)it.next();
            if (vcpi.getType() == VisualClassPathItem.TYPE_LIBRARY) {
                // add property to project.properties pointing to relativized 
                // library jar(s) if possible
                String prop = vcpi.getRaw();
                prop = prop.substring(2, prop.length()-1); // XXX make a PropertyUtils method for this!
                String value = relativizeLibraryClasspath(prop, projDir);
                if (value != null) {
                    ep.setProperty(prop, value);
                    ep.setComment(prop, new String[]{
                        // XXX this should be I18N! Not least because the English is wrong...
                        "# Property "+prop+" is set here just to make sharing of project simpler.",
                        "# The library definition has always preference over this property."}, false);
                    changed = true;
                }
            }
        }
        if (changed) {
            antProjectHelper.putProperties(PROJECT, ep);
        }
    }
    
    /**
     * Tokenize library classpath and try to relativize all the jars.
     * @param property library property name ala "libs.someLib.classpath"
     * @param projectDir project dir for relativization
     * @return relativized library classpath or null if some jar is not collocated
     */
    private String relativizeLibraryClasspath(String property, File projectDir) {
        String value = PropertyUtils.getGlobalProperties().getProperty(property);
        // bugfix #42852, check if the classpath property is set, otherwise return null
        if (value == null) {
            return null;
        }
        String[] paths = PropertyUtils.tokenizePath(value);
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<paths.length; i++) {
            File f = antProjectHelper.resolveFile(paths[i]);
            if (CollocationQuery.areCollocated(f, projectDir)) {
                sb.append(PropertyUtils.relativizeFile(projectDir, f));
            } else {
                return null;
            }
            if (i+1<paths.length) {
                sb.append(File.pathSeparatorChar);
            }
        }
        if (sb.length() == 0) {
            return null;
        } else {
            return sb.toString();
        }
    }
        
    private class PropertyInfo {
        
        private PropertyDescriptor propertyDesciptor;
        private String rawValue;
        private String evaluatedValue;
        private Object value;
        private Object newValue;
        private String newValueEncoded;
        
        public PropertyInfo( PropertyDescriptor propertyDesciptor, String rawValue, String evaluatedValue ) {
            this.propertyDesciptor = propertyDesciptor;
            this.rawValue = rawValue;
            this.evaluatedValue = evaluatedValue;
            this.value = propertyDesciptor.parser.decode( rawValue, antProjectHelper, evaluator, refHelper );
            this.newValue = null;
        }
        
        public PropertyDescriptor getPropertyDescriptor() {
            return propertyDesciptor;
        }
        
        public void encode() {            
            if ( isModified() ) {
                newValueEncoded = propertyDesciptor.parser.encode( newValue, antProjectHelper, refHelper);                
            }
            else {
                newValueEncoded = null;
            }
        }

        public Object getValue() {
            return isModified() ? newValue : value; 
        }
        
        public void setValue( Object value ) {
            newValue = value;
        }
        
        public String getNewValueEncoded() {
            return newValueEncoded;
        }
        
        public boolean isModified() {
            return newValue != null;
        }
        
        public Object getOldValue() {
            return value;
        }
    }
    
    private static class PropertyDescriptor {
        
        final PropertyParser parser;
        final String name;
        final String dest;
        
        PropertyDescriptor( String name, String dest, PropertyParser parser ) {
            this.name = name;
            this.dest = dest;
            this.parser = parser;
        }
        
    }
    
    
    private static abstract class PropertyParser {
        
        public abstract Object decode( String raw, AntProjectHelper antProjectHelper, PropertyEvaluator evaluator, ReferenceHelper refHelper );
        
        public abstract String encode( Object value, AntProjectHelper antProjectHelper, ReferenceHelper refHelper );
        
    }
    
    private static class StringParser extends PropertyParser {
        
        public Object decode(String raw, AntProjectHelper antProjectHelper, PropertyEvaluator evaluator, ReferenceHelper refHelper) {
            return raw;
        }        
        
        public String encode(Object value, AntProjectHelper antProjectHelper, ReferenceHelper refHelper ) {
            return (String)value;
        }
        
    }
    
    private static class BooleanParser extends PropertyParser {
        
        public Object decode(String raw, AntProjectHelper antProjectHelper, PropertyEvaluator evaluator, ReferenceHelper refHelper) {
            
            if ( raw != null ) {
               String lowecaseRaw = raw.toLowerCase();
               
               if ( lowecaseRaw.equals( "true") || // NOI18N
                    lowecaseRaw.equals( "yes") || // NOI18N
                    lowecaseRaw.equals( "enabled") ) // NOI18N
                   return Boolean.TRUE;                   
            }
            
            return Boolean.FALSE;
        }
        
        public String encode(Object value, AntProjectHelper antProjectHelper, ReferenceHelper refHelper ) {
            return ((Boolean)value).booleanValue() ? "true" : "false"; // NOI18N
        }
        
    }
    
    private static class InverseBooleanParser extends BooleanParser {
        
        public Object decode(String raw, AntProjectHelper antProjectHelper, PropertyEvaluator evaluator, ReferenceHelper refHelper ) {                    
            return ((Boolean)super.decode(raw, antProjectHelper, evaluator, refHelper)).booleanValue() ? Boolean.FALSE : Boolean.TRUE;           
        }
        
        public String encode(Object value, AntProjectHelper antProjectHelper, ReferenceHelper refHelper ) {
            return super.encode( ((Boolean)value).booleanValue() ? Boolean.FALSE : Boolean.TRUE, antProjectHelper, refHelper );
        }
        
    }
    
    public static class PathParser extends PropertyParser {
        
        // XXX Define in the LibraryManager
        private static final String LIBRARY_PREFIX = "${libs."; // NOI18N

        private static final String ANT_ARTIFACT_PREFIX = "${reference."; // NOI18N
        
        // Contains well known paths in the GroovyProject
        private static final String[][] WELL_KNOWN_PATHS = new String[][] {
            { JAVAC_CLASSPATH, NbBundle.getMessage( GroovyProjectProperties.class, "LBL_JavacClasspath_DisplayName" ) },
            { RUN_CLASSPATH, NbBundle.getMessage( GroovyProjectProperties.class, "LBL_RunClasspath_DisplayName" ) },
            { BUILD_CLASSES_DIR, NbBundle.getMessage( GroovyProjectProperties.class, "LBL_BuildClassesDir_DisplayName" ) },            
        };
        
        
        public Object decode(String raw, AntProjectHelper antProjectHelper, PropertyEvaluator evaluator, ReferenceHelper refHelper ) {
            
            String pe[] = PropertyUtils.tokenizePath( raw == null ? "": raw ); // NOI18N
            List cpItems = new ArrayList( pe.length );
            for( int i = 0; i < pe.length; i++ ) {
                VisualClassPathItem cpItem;
                
                // First try to find out whether the item is well known classpath
                // in the J2SE project type
                int wellKnownPathIndex = -1;
                for( int j = 0; j < WELL_KNOWN_PATHS.length; j++ ) {
                    if ( WELL_KNOWN_PATHS[j][0].equals( getAntPropertyName( pe[i] ) ) )  {
                        wellKnownPathIndex = j;
                        break;
                    }
                }
                
                if ( wellKnownPathIndex != - 1 ) {
                    cpItem = new VisualClassPathItem( pe[i], VisualClassPathItem.TYPE_CLASSPATH, pe[i], WELL_KNOWN_PATHS[wellKnownPathIndex][1] );
                } else if ( pe[i].startsWith( LIBRARY_PREFIX ) ) {
                    // Library from library manager
                    //String eval = antProjectHelper.evaluate( getAntPropertyName( pe[i] ) );
                    String eval = pe[i].substring( LIBRARY_PREFIX.length(), pe[i].lastIndexOf('.') ); //NOI18N
                    Library lib = LibraryManager.getDefault().getLibrary (eval);
                    if (lib != null) {
                        cpItem = new VisualClassPathItem( lib, VisualClassPathItem.TYPE_LIBRARY, pe[i], eval );
                    } else {
                        cpItem = new VisualClassPathItem(null, VisualClassPathItem.TYPE_LIBRARY, pe[i], eval);
                    }
                } else if (pe[i].startsWith(ANT_ARTIFACT_PREFIX)) {
                    AntArtifact artifact = (AntArtifact)refHelper.findArtifactAndLocation(pe[i])[0];
                    if ( artifact != null ) {
                        // Sub project artifact
                        String eval = artifact.getArtifactLocations()[0].toString();
                        cpItem = new VisualClassPathItem( artifact, VisualClassPathItem.TYPE_ARTIFACT, pe[i], eval );
                    } else {
                        cpItem = new VisualClassPathItem(null, VisualClassPathItem.TYPE_ARTIFACT, pe[i], null);
                    }
                } else {
                    // Standalone jar or property
                    String eval;
                    if (isAntProperty (pe[i])) {
                        eval = evaluator.getProperty(getAntPropertyName(pe[i]));
                    }
                    else {
                        eval = pe[i];
                    }                    
                    File f = null;
                    if (eval != null) {
                        f = antProjectHelper.resolveFile(eval);
                    }                    
                    cpItem = new VisualClassPathItem( f, VisualClassPathItem.TYPE_JAR, pe[i], eval );
                }
                if (cpItem!=null) {
                    cpItems.add( cpItem );
                }
            }
            
            return cpItems;
        }
        
        public String encode( Object value, AntProjectHelper antProjectHelper, ReferenceHelper refHelper ) {
            
            StringBuffer sb = new StringBuffer();
                        
            for ( Iterator it = ((List)value).iterator(); it.hasNext(); ) {
                
                VisualClassPathItem vcpi = (VisualClassPathItem)it.next();
                
                switch( vcpi.getType() ) {
                                        
                    case VisualClassPathItem.TYPE_JAR:
                        String raw = vcpi.getRaw();
                        if (raw == null) {
                            // New file
                            File file = (File)vcpi.getObject();
                            // pass null as expected artifact type to always get file reference
                            String reference = refHelper.createForeignFileReference(file, null);
                            sb.append(reference);
                        } else {
                            // Existing property
                            sb.append( raw );
                        }
                        break;
                    case VisualClassPathItem.TYPE_LIBRARY:
                        sb.append(vcpi.getRaw());
                        break;    
                    case VisualClassPathItem.TYPE_ARTIFACT:
                        if (vcpi.getObject() != null) {
                            AntArtifact aa = (AntArtifact)vcpi.getObject();
                            String reference = refHelper.addReference(aa, aa.getArtifactLocations()[0]);
                            sb.append( reference );
                        } else {
                            sb.append(vcpi.getRaw());
                        }
                        break;
                    case VisualClassPathItem.TYPE_CLASSPATH:
                        sb.append( vcpi.getRaw() );
                        break;
                }
                
                if ( it.hasNext() ) {
                    sb.append( File.pathSeparatorChar );
                }
            }
            
            return sb.toString();
        }
        
    }

    private static JavaPlatform findPlatform(String platformAntID) {
        JavaPlatform[] platforms = JavaPlatformManager.getDefault().getInstalledPlatforms();            
        for(int i = 0; i < platforms.length; i++) {
            String normalizedName = (String)platforms[i].getProperties().get("platform.ant.name"); // NOI18N
            if (normalizedName != null && normalizedName.equals(platformAntID)) {
                return platforms[i];
            }
        }
        return null;
    }
    
    private static class PlatformParser extends PropertyParser {
        
        public Object decode(String raw, AntProjectHelper antProjectHelper, PropertyEvaluator evaluator, ReferenceHelper refHelper) {
            JavaPlatform platform = findPlatform(raw);
            if (platform != null) {
                return platform.getDisplayName();
            }
            // if platform does not exist then return raw reference.
            return raw;
        }
        
        public String encode(Object value, AntProjectHelper antProjectHelper, ReferenceHelper refHelper) {
            JavaPlatform[] platforms = JavaPlatformManager.getDefault().getPlatforms ((String)value,
                    new Specification ("j2se",null)); // NOI18N
            if (platforms.length == 0) {
                // platform for this project does not exist. broken reference? its displayname should 
                // correspond to platform ID. so just return it:
                return (String)value;
            } else {
                return (String) platforms[0].getProperties().get("platform.ant.name");  //NOI18N
            }
        }
        
    }
    
}
