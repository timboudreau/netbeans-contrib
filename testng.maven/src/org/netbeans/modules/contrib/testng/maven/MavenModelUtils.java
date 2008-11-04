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
package org.netbeans.modules.contrib.testng.maven;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.maven.model.Activation;
import org.apache.maven.model.ActivationProperty;
import org.apache.maven.model.Build;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Profile;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.netbeans.modules.maven.embedder.writer.WriterUtils;
import org.openide.filesystems.FileObject;

/*
<profiles>
<profile>
<id>netbeans-private-xxx</id>
<activation>
<property>
<name>netbeans.testng.action</name>
</property>
</activation>
<build>
<plugins>
<plugin>
<groupId>org.apache.maven.plugins</groupId>
<artifactId>maven-surefire-plugin</artifactId>
<configuration>
<suiteXmlFiles>
<suiteXmlFile>testng.xml</suiteXmlFile>
</suiteXmlFiles>
</configuration>
</plugin>
</plugins>
</build>
</profile>
</profiles>
 */
/**
 *
 * @author lukas
 */
public class MavenModelUtils {

    private static final Logger LOGGER = Logger.getLogger(MavenModelUtils.class.getName());

    private static final String PROFILE_NAME = "netbeans-private-testng"; //NOI18N

    public static void addProfile(FileObject fo, String fileName) {
        assert fo != null;
        Model m = WriterUtils.loadModel(fo);

        for (Object o: m.getProfiles()) {
            Profile p = (Profile) o;
            if (PROFILE_NAME.equals(p.getId())) {
                return;
            }
        }
        Profile ps = new Profile();
        if (m.getProfiles() == null) {
            List l = new ArrayList();
            l.add(ps);
            m.setProfiles(l);
        } else {
            m.getProfiles().add(ps);
        }
        ps.setId(PROFILE_NAME);
        Activation a = ps.getActivation();
        if (a == null) {
            a = new Activation();
            ps.setActivation(a);
        }
        a.setActiveByDefault(false);
        ActivationProperty ap = new ActivationProperty();
        ap.setName("netbeans.testng.action"); //NOI18N
        a.setProperty(ap);
        Plugin plugin = new Plugin();
        plugin.setGroupId("org.apache.maven.plugins"); //NOI18N
        plugin.setArtifactId("maven-surefire-plugin"); //NOI18N
        plugin.setVersion("2.4.2");
        Xpp3Dom dom = (Xpp3Dom) plugin.getConfiguration();
        if (dom == null) {
            dom = new Xpp3Dom("configuration"); //NOI18N
            plugin.setConfiguration(dom);
        }
        Xpp3Dom dom2 = dom.getChild("suiteXmlFiles"); //NOI18N
        if (dom2 == null) {
            dom2 = new Xpp3Dom("suiteXmlFiles"); //NOI18N
            dom.addChild(dom2);
        }
        Xpp3Dom dom3 = dom2.getChild("suiteXmlFile"); //NOI18N
        if (dom3 == null) {
            dom3 = new Xpp3Dom("suiteXmlFile"); //NOI18N
            dom2.addChild(dom3);
        }
        dom3.setValue(fileName);
        BuildBase build = ps.getBuild();
        if (build == null) {
            build = new Build();
        }
        build.getPlugins().add(plugin);
        ps.setBuild(build);
        try {
            WriterUtils.writePomModel(fo, m);
        } catch (IOException ex) {
            LOGGER.log(Level.INFO, ex.getMessage(), ex);
        }
    }
}