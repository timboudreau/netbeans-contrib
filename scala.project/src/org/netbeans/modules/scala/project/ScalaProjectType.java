package org.netbeans.modules.scala.project;

import java.io.IOException;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.support.ant.AntBasedProjectType;
import org.netbeans.spi.project.support.ant.AntProjectHelper;

/**
 * @author Martin Krauskopf
 */
public final class ScalaProjectType implements AntBasedProjectType {
    
    static final String TYPE = "org.netbeans.modules.scala.project"; // NOI18N
    private static final String NAME_SHARED = "data"; // NOI18N
    static final String NAMESPACE_SHARED = "http://www.netbeans.org/ns/scala-project/1"; // NOI18N
    private static final String NAME_PRIVATE = "data"; // NOI18N
    private static final String NAMESPACE_PRIVATE = "http://www.netbeans.org/ns/scala-project-private/1"; // NOI18N
    
    public String getType() {
        return TYPE;
    }
    
    public Project createProject(final AntProjectHelper helper) throws IOException {
        return new ScalaProject(helper);
    }
    
    public String getPrimaryConfigurationDataElementName(boolean shared) {
        return shared ? NAME_SHARED : NAME_PRIVATE;
    }
    
    public String getPrimaryConfigurationDataElementNamespace(boolean shared) {
        return shared ? NAMESPACE_SHARED : NAMESPACE_PRIVATE;
    }
    
}
