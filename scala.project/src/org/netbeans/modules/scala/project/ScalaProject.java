package org.netbeans.modules.scala.project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.modules.scala.project.ui.ScalaLogicalView;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.GeneratedFilesHelper;
import org.netbeans.spi.project.support.ant.ProjectXmlSavedHook;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.netbeans.spi.project.support.ant.PropertyProvider;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.netbeans.spi.project.support.ant.SourcesHelper;
import org.netbeans.spi.project.ui.ProjectOpenedHook;
import org.netbeans.spi.project.ui.RecommendedTemplates;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/**
 * @author Martin Krauskopf
 */
public final class ScalaProject implements Project {
    
    private final AntProjectHelper helper;
    private final Lookup lookup;
    private final PropertyEvaluator eval;
    private final GeneratedFilesHelper genFilesHelper;
    
    private final Map<String, FileObject> directoryCache = new WeakHashMap<String, FileObject>();
    
    public static final String DEFAULT_SRC_DIR = "src"; // NOI18N
    
    public static final String SCALA_PROJECT_ICON_PATH =
            "org/netbeans/modules/scala/project/ui/resources/scala16x16.png"; // NOI18N
    
    ScalaProject(AntProjectHelper helper) {
        this.helper = helper;
        eval = createEvaluator();
        genFilesHelper = new GeneratedFilesHelper(helper);
        SourcesHelper sourcesHelper = new SourcesHelper(helper, eval);
        sourcesHelper.addPrincipalSourceRoot("${src.dir}", // NOI18N
                NbBundle.getMessage(ScalaProject.class, "LBL_source_packages"), null, null);
        sourcesHelper.addTypedSourceRoot("${src.dir}", JavaProjectConstants.SOURCES_TYPE_JAVA,
                NbBundle.getMessage(ScalaProject.class, "LBL_source_packages"), null, null);
        this.lookup = Lookups.fixed(new Object[] {
            new ScalaLogicalView(this),
            sourcesHelper.createSources(),
            new SavedHook(),
            new OpenedHook(),
            new ScalaActions(this),
            new RecommendedAndPrivilegedTemplatesImpl(),
        });
    }
    
    public FileObject getProjectDirectory() {
        return helper.getProjectDirectory();
    }
    
    public Lookup getLookup() {
        return lookup;
    }
    
    private PropertyEvaluator createEvaluator() {
        List<PropertyProvider> providers = new ArrayList<PropertyProvider>();
        Map<String,String> defaults = new HashMap<String,String>();
        defaults.put("src.dir", "src"); // NOI18N
        providers.add(helper.getPropertyProvider(AntProjectHelper.PRIVATE_PROPERTIES_PATH));
        providers.add(helper.getPropertyProvider(AntProjectHelper.PROJECT_PROPERTIES_PATH));
        providers.add(PropertyUtils.fixedPropertyProvider(Collections.unmodifiableMap(defaults)));
        return PropertyUtils.sequentialPropertyEvaluator(null,
                providers.toArray(new PropertyProvider[providers.size()]));
    }
    
    public AntProjectHelper getHelper() {
        return helper;
    }
    
    public PropertyEvaluator getEvaluator() {
        return eval;
    }
    
    private FileObject getDir(final String prop) {
        // XXX also add a PropertyChangeListener to eval and clear the cache of changed props
        if (directoryCache.containsKey(prop)) {
            return (FileObject) directoryCache.get(prop);
        } else {
            String val = getEvaluator().getProperty(prop);
            assert val != null : "No value for " + prop;
            FileObject fo = helper.resolveFileObject(val);
            directoryCache.put(prop, fo);
            return fo;
        }
    }
    
    public FileObject getSourceDirectory() {
        return getDir("src.dir"); // NOI18N
    }
    
    final class OpenedHook extends ProjectOpenedHook {
        
        OpenedHook() {}
        
        protected void projectOpened() {
            try {
                refreshBuildScripts(true);
            } catch (IOException e) {
                Util.LOG.log(Level.INFO, "Exception during scripts regeneration for " + this, e);
            }
        }
        
        protected void projectClosed() {
            try {
                ProjectManager.getDefault().saveProject(ScalaProject.this);
            } catch (IOException e) {
                Util.LOG.log(Level.INFO, "Exception during saving project " + this, e);
            }
        }
        
    }
    
    private final class SavedHook extends ProjectXmlSavedHook {
        
        SavedHook() {}
        
        protected void projectXmlSaved() throws IOException {
            refreshBuildScripts(false);
        }
        
    }
    
    private void refreshBuildScripts(final boolean checkForProjectXmlModified) throws IOException {
        genFilesHelper.refreshBuildScript(
                GeneratedFilesHelper.BUILD_IMPL_XML_PATH,
                ScalaProject.class.getResource("resources/build-impl.xsl"), // NOI18N
                checkForProjectXmlModified);
        genFilesHelper.refreshBuildScript(
                GeneratedFilesHelper.BUILD_XML_PATH,
                ScalaProject.class.getResource("resources/build.xsl"), // NOI18N
                checkForProjectXmlModified);
    }
    
    private static final class RecommendedAndPrivilegedTemplatesImpl implements RecommendedTemplates {
        
        private static final String[] RECOMMENDED_TYPES = new String[] {
            "scala-classes", // NOI18N
            "oasis-XML-catalogs", // NOI18N
            "XML", // NOI18N
            "ant-script", // NOI18N
            "ant-task", // NOI18N
            "simple-files", // NOI18N
        };
        
        public String[] getRecommendedTypes() {
            return RECOMMENDED_TYPES;
        }
        
    }
    
}
