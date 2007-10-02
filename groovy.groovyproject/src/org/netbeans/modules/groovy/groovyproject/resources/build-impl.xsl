<?xml version="1.0" encoding="UTF-8"?>
<!--
                Sun Public License Notice

The contents of this file are subject to the Sun Public License
Version 1.0 (the "License"). You may not use this file except in
compliance with the License. A copy of the License is available at
http://www.sun.com/

The Original Code is NetBeans. The Initial Developer of the Original
Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
Microsystems, Inc. All Rights Reserved.

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.
-->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:p="http://www.netbeans.org/ns/project/1"
                xmlns:xalan="http://xml.apache.org/xslt"
                xmlns:groovy="http://www.netbeans.org/ns/groovy-project/1"
                xmlns:groovyproject="http://www.netbeans.org/ns/groovy-project/1"
                xmlns:projdeps="http://www.netbeans.org/ns/ant-project-references/1"
                exclude-result-prefixes="xalan p groovy projdeps">
<!-- XXX should use namespaces for NB in-VM tasks from ant/browsetask and debuggerjpda/ant (Ant 1.6.1 and higher only) -->
    <xsl:output method="xml" indent="yes" encoding="UTF-8" xalan:indent-amount="4"/>
    <xsl:template match="/">

        <xsl:comment><![CDATA[
*** GENERATED FROM project.xml - DO NOT EDIT  ***
***         EDIT ../build.xml INSTEAD         ***

For the purpose of easier reading the script
is divided into following sections:

  - initialization
  - compilation
  - jar
  - execution
  - cleanup

]]></xsl:comment>

        <xsl:variable name="name" select="/p:project/p:configuration/groovyproject:data/groovyproject:name"/>
        <!-- Synch with build-impl.xsl: -->
        <xsl:variable name="codename" select="translate($name, ' ', '_')"/>
        <project name="{$codename}-impl">
            <xsl:attribute name="default">build</xsl:attribute>
            <xsl:attribute name="basedir">..</xsl:attribute>

            <target name="default">
                <xsl:attribute name="depends">jar</xsl:attribute>
                <xsl:attribute name="description">Build the whole project.</xsl:attribute>
            </target>

            <xsl:comment> 
    ======================
    INITIALIZATION SECTION 
    ======================
    </xsl:comment>
    
            <target name="-pre-init">
                <xsl:comment> Empty placeholder for easier customization. </xsl:comment>
                <xsl:comment> You can override this target in the ../build.xml file. </xsl:comment>
            </target>

            <target name="-init-private">
                <xsl:attribute name="depends">-pre-init</xsl:attribute>
                <property file="nbproject/private/private.properties"/>
            </target>

            <target name="-init-user">
                <xsl:attribute name="depends">-pre-init,-init-private</xsl:attribute>
                <property file="${{user.properties.file}}"/>
                <xsl:comment> The two properties below are usually overridden </xsl:comment>
                <xsl:comment> by the active platform. Just a fallback. </xsl:comment>
                <property name="default.javac.source" value="1.4"/>
                <property name="default.javac.target" value="1.4"/>
            </target>

            <target name="-init-project">
                <xsl:attribute name="depends">-pre-init,-init-private,-init-user</xsl:attribute>
                <property file="nbproject/project.properties"/>
            </target>

            <target name="-do-init">
                <xsl:attribute name="depends">-pre-init,-init-private,-init-user,-init-project,-init-macrodef-property</xsl:attribute>
                <xsl:if test="/p:project/p:configuration/groovy:data/groovy:explicit-platform">
                    <groovyproject:property name="platform.home" value="platforms.${{platform.active}}.home"/>
                    <groovyproject:property name="platform.bootcp" value="platforms.${{platform.active}}.bootclasspath"/>
                    <groovyproject:property name="platform.compiler" value="platforms.${{platform.active}}.compile"/>
                    <groovyproject:property name="platform.javac.tmp" value="platforms.${{platform.active}}.javac"/>
                    <condition property="platform.javac" value="${{platform.home}}/bin/javac">
                        <equals arg1="${{platform.javac.tmp}}" arg2="$${{platforms.${{platform.active}}.javac}}"/>
                    </condition>
                    <property name="platform.javac" value="${{platform.javac.tmp}}"/>
                    <groovyproject:property name="platform.java.tmp" value="platforms.${{platform.active}}.java"/>
                    <condition property="platform.java" value="${{platform.home}}/bin/java">
                        <equals arg1="${{platform.java.tmp}}" arg2="$${{platforms.${{platform.active}}.java}}"/>
                    </condition>
                    <property name="platform.java" value="${{platform.java.tmp}}"/>
                    <condition property="platform.invalid" value="true">
                        <or>
                            <contains string="${{platform.javac}}" substring="$${{platforms."/>
                            <contains string="${{platform.java}}" substring="$${{platforms."/>
                        </or>
                    </condition>
                    <fail unless="platform.home">Must set platform.home</fail>
                    <fail unless="platform.bootcp">Must set platform.bootcp</fail>                        
                    <fail unless="platform.java">Must set platform.java</fail>
                    <fail unless="platform.javac">Must set platform.javac</fail>
                    <fail if="platform.invalid">Platform is not correctly set up</fail>
                </xsl:if>
                <available file="${{manifest.file}}" property="manifest.available"/>
                <condition property="manifest.available+main.script">
                    <and>
                        <isset property="manifest.available"/>
                        <isset property="main.script"/>
                        <not>
                            <equals arg1="${{main.script}}" arg2="" trim="true"/>
                        </not>
                    </and>
                </condition>
                <condition property="netbeans.home">
                    <and>
                        <isset property="netbeans.home"/>
                    </and>
                </condition>
                <property name="run.jvmargs" value=""/>
                <property name="javac.compilerargs" value=""/>
                <property name="work.dir" value="${{basedir}}"/>
                <condition property="no.deps">
                    <and>
                        <istrue value="${{no.dependencies}}"/>
                    </and>
                </condition>
            </target>

            <target name="-post-init">
                <xsl:comment> Empty placeholder for easier customization. </xsl:comment>
                <xsl:comment> You can override this target in the ../build.xml file. </xsl:comment>
            </target>

            <target name="-init-check">
                <xsl:attribute name="depends">-pre-init,-init-private,-init-user,-init-project,-do-init</xsl:attribute>
                <!-- XXX XSLT 2.0 would make it possible to use a for-each here -->
                <!-- Note that if the properties were defined in project.xml that would be easy -->
                <!-- But required props should be defined by the AntBasedProjectType, not stored in each project -->
                <fail unless="src.dir">Must set src.dir</fail>
                <fail unless="build.dir">Must set build.dir</fail>
                <fail unless="dist.dir">Must set dist.dir</fail>
                <fail unless="build.classes.dir">Must set build.classes.dir</fail>
                <fail unless="build.classes.excludes">Must set build.classes.excludes</fail>
                <fail unless="dist.jar">Must set dist.jar</fail>
            </target>

            <target name="-init-macrodef-property">
                <macrodef>
                    <xsl:attribute name="name">property</xsl:attribute>
                    <xsl:attribute name="uri">http://www.netbeans.org/ns/groovy-project/1</xsl:attribute>
                    <attribute>
                        <xsl:attribute name="name">name</xsl:attribute>
                    </attribute>
                    <attribute>
                        <xsl:attribute name="name">value</xsl:attribute>
                    </attribute>
                    <sequential>
                        <property name="@{{name}}" value="${{@{{value}}}}"/>
                    </sequential>
                  </macrodef>
            </target>
            
            <target name="-init-macrodef-groovyc">
                <taskdef name="groovyc" classname="org.codehaus.groovy.ant.Groovyc">
                    <classpath>
                        <fileset dir="${{groovy.home}}/lib">
                            <include name="*.jar"/>
                        </fileset>
                    </classpath>
                </taskdef>
                <macrodef>
                    <xsl:attribute name="name">groovyc</xsl:attribute>
                    <xsl:attribute name="uri">http://www.netbeans.org/ns/groovy-project/1</xsl:attribute>
                    <attribute>
                        <xsl:attribute name="name">srcdir</xsl:attribute>
                        <xsl:attribute name="default">${src.dir}</xsl:attribute>
                    </attribute>
                    <attribute>
                        <xsl:attribute name="name">destdir</xsl:attribute>
                        <xsl:attribute name="default">${build.classes.dir}</xsl:attribute>
                    </attribute>
                    <attribute>
                        <xsl:attribute name="name">classpath</xsl:attribute>
                        <xsl:attribute name="default">${javac.classpath}</xsl:attribute>
                    </attribute>
                    <attribute>
                        <xsl:attribute name="name">debug</xsl:attribute>
                        <xsl:attribute name="default">${javac.debug}</xsl:attribute>
                    </attribute>
                    <element>
                        <xsl:attribute name="name">customize</xsl:attribute>
                        <xsl:attribute name="optional">true</xsl:attribute>
                    </element>
                    <sequential>
                        <groovyc>
                            <xsl:attribute name="srcdir">@{srcdir}</xsl:attribute>
                            <xsl:attribute name="destdir">@{destdir}</xsl:attribute>
                            <classpath>
                                <path path="@{{classpath}}"/>
                            </classpath>
                            <customize/>
                        </groovyc>
                    </sequential>
                 </macrodef>
            </target>
            
            <target name="-init-macrodef-groovy">
                <macrodef>
                    <xsl:attribute name="name">groovy</xsl:attribute>
                    <xsl:attribute name="uri">http://www.netbeans.org/ns/groovy-project/1</xsl:attribute>
                    <attribute>
                        <xsl:attribute name="name">script</xsl:attribute>
                        <xsl:attribute name="default">${main.script}</xsl:attribute>
                    </attribute>
                    <element>
                        <xsl:attribute name="name">customize</xsl:attribute>
                        <xsl:attribute name="optional">true</xsl:attribute>
                    </element>
                    <sequential>
                        <java fork="true" classname="groovy.lang.GroovyShell">
                            <xsl:attribute name="dir">${work.dir}</xsl:attribute>
                            <xsl:if test="/p:project/p:configuration/groovy:data/groovy:explicit-platform">
                                <xsl:attribute name="jvm">${platform.java}</xsl:attribute>
                            </xsl:if>
                            <arg line="${{run.jvmargs}} ${{src.dir}}/@{{script}}"/>
                            <sysproperty key="groovy.home" value="${{groovy.home}}"/>
                            <classpath>
                                <path path="${{run.classpath}}"/>
                                <fileset dir="${{groovy.home}}/lib">
                                    <include name="*.jar"/>
                                </fileset>
                            </classpath>
                            <syspropertyset>
                                <propertyref prefix="run-sys-prop."/>
                                <mapper type="glob" from="run-sys-prop.*" to="*"/>
                            </syspropertyset>
                            <customize/>
                        </java>
                    </sequential>
                </macrodef>
            </target>

            <target name="-init-presetdef-jar">
                <presetdef>
                    <xsl:attribute name="name">jar</xsl:attribute>
                    <xsl:attribute name="uri">http://www.netbeans.org/ns/groovy-project/1</xsl:attribute>
                    <jar jarfile="${{dist.jar}}" compress="${{jar.compress}}">
                        <fileset dir="${{build.classes.dir}}"/>
                        <!-- XXX should have a property serving as the excludes list -->
                    </jar>
                </presetdef>
            </target>

            <target name="init">
                <xsl:attribute name="depends">-pre-init,-init-private,-init-user,-init-project,-do-init,-post-init,-init-check,-init-macrodef-property,-init-macrodef-groovyc,-init-macrodef-groovy,-init-presetdef-jar</xsl:attribute>
            </target>

            <xsl:comment>
    ===================
    COMPILATION SECTION
    ===================
    </xsl:comment>

            <xsl:call-template name="deps.target">
                <xsl:with-param name="targetname" select="'deps-jar'"/>
                <xsl:with-param name="type" select="'jar'"/>
            </xsl:call-template>

            <target name="-pre-pre-compile">
                <xsl:attribute name="depends">init,deps-jar</xsl:attribute>
                <mkdir dir="${{build.classes.dir}}"/>
            </target>

            <target name="-pre-compile">
                <xsl:comment> Empty placeholder for easier customization. </xsl:comment>
                <xsl:comment> You can override this target in the ../build.xml file. </xsl:comment>
            </target>

            <target name="-do-compile">
                <xsl:attribute name="depends">init,deps-jar,-pre-pre-compile,-pre-compile</xsl:attribute>
                <groovyproject:groovyc/>
                <copy todir="${{build.classes.dir}}">
                    <fileset dir="${{src.dir}}" excludes="${{build.classes.excludes}}"/>
                </copy>
            </target>

            <target name="-post-compile">
                <xsl:comment> Empty placeholder for easier customization. </xsl:comment>
                <xsl:comment> You can override this target in the ../build.xml file. </xsl:comment>
            </target>

            <target name="compile">
                <xsl:attribute name="depends">init,deps-jar,-pre-pre-compile,-pre-compile,-do-compile,-post-compile</xsl:attribute>
                <xsl:attribute name="description">Compile project.</xsl:attribute>
            </target>

            <target name="-pre-compile-single">
                <xsl:comment> Empty placeholder for easier customization. </xsl:comment>
                <xsl:comment> You can override this target in the ../build.xml file. </xsl:comment>
            </target>

            <target name="-do-compile-single">
                <xsl:attribute name="depends">init,deps-jar,-pre-pre-compile</xsl:attribute>
                <fail unless="groovyc.includes">Must select some files in the IDE or set groovyc.includes</fail>
                <groovyproject:groovyc>
                    <customize>
                        <patternset includes="${{groovyc.includes}}"/>
                    </customize>
               </groovyproject:groovyc>
            </target>

            <target name="-post-compile-single">
                <xsl:comment> Empty placeholder for easier customization. </xsl:comment>
                <xsl:comment> You can override this target in the ../build.xml file. </xsl:comment>
            </target>

            <target name="compile-single">
                <xsl:attribute name="depends">init,deps-jar,-pre-pre-compile,-pre-compile-single,-do-compile-single,-post-compile-single</xsl:attribute>
            </target>

            <xsl:comment>
    ====================
    JAR BUILDING SECTION
    ====================
    </xsl:comment>

            <target name="-pre-pre-jar">
                <xsl:attribute name="depends">init</xsl:attribute>
                <dirname property="dist.jar.dir" file="${{dist.jar}}"/>
                <mkdir dir="${{dist.jar.dir}}"/>
            </target>

            <target name="-pre-jar">
                <xsl:comment> Empty placeholder for easier customization. </xsl:comment>
                <xsl:comment> You can override this target in the ../build.xml file. </xsl:comment>
            </target>

            <target name="-do-jar-without-manifest">
                <xsl:attribute name="depends">init,compile,-pre-pre-jar,-pre-jar</xsl:attribute>
                <xsl:attribute name="unless">manifest.available</xsl:attribute>
                <groovyproject:jar/>
            </target>

            <target name="-do-jar-with-manifest">
                <xsl:attribute name="depends">init,compile,-pre-pre-jar,-pre-jar</xsl:attribute>
                <xsl:attribute name="if">manifest.available</xsl:attribute>
                <xsl:attribute name="unless">manifest.available+main.script</xsl:attribute>
                <groovyproject:jar manifest="${{manifest.file}}"/>
            </target>

            <target name="-do-jar-with-mainclass">
                <xsl:attribute name="depends">init,compile,-pre-pre-jar,-pre-jar</xsl:attribute>
                <xsl:attribute name="if">manifest.available+main.script</xsl:attribute>
                <groovyproject:jar manifest="${{manifest.file}}">
                    <manifest>
                        <attribute name="Main-Class" value="${{main.script}}"/>
                    </manifest>
                </groovyproject:jar>
            </target>

            <target name="-post-jar">
                <xsl:comment> Empty placeholder for easier customization. </xsl:comment>
                <xsl:comment> You can override this target in the ../build.xml file. </xsl:comment>
            </target>

            <target name="jar">
                <xsl:attribute name="depends">init,compile,-pre-jar,-do-jar-with-manifest,-do-jar-without-manifest,-do-jar-with-mainclass,-post-jar</xsl:attribute>
                <xsl:attribute name="description">Build JAR.</xsl:attribute>
            </target>

            <xsl:comment>
    =================
    EXECUTION SECTION
    =================
    </xsl:comment>

            <target name="run">
                <xsl:attribute name="depends">init,deps-jar</xsl:attribute>
                <xsl:attribute name="description">Run a main script.</xsl:attribute>
                <groovyproject:groovy>
                    <customize>
                        <arg line="${{application.args}}"/>
                    </customize>
                </groovyproject:groovy>
            </target>

            <target name="run-single">
                <xsl:attribute name="depends">init,deps-jar</xsl:attribute>
                <fail unless="run.script">Must select one file in the IDE or set run.script</fail>
                <groovyproject:groovy script="${{run.script}}"/>
            </target>

            <xsl:comment>
    ===============
    CLEANUP SECTION
    ===============
    </xsl:comment>

            <xsl:call-template name="deps.target">
                <xsl:with-param name="targetname" select="'deps-clean'"/>
            </xsl:call-template>

            <target name="-do-clean">
                <xsl:attribute name="depends">init</xsl:attribute>
                <delete dir="${{build.dir}}"/>
                <delete dir="${{dist.dir}}"/>
                <delete dir="jpywork"/>
                <!-- XXX explicitly delete all build.* and dist.* dirs in case they are not subdirs -->
            </target>

            <target name="-post-clean">
                <xsl:comment> Empty placeholder for easier customization. </xsl:comment>
                <xsl:comment> You can override this target in the ../build.xml file. </xsl:comment>
            </target>

            <target name="clean">
                <xsl:attribute name="depends">init,deps-clean,-do-clean,-post-clean</xsl:attribute>
                <xsl:attribute name="description">Clean build products.</xsl:attribute>
            </target>

        </project>

        <!-- TBD items:

        Could pass <propertyset> to run, debug, etc. under Ant 1.6,
        optionally, by doing e.g.

          <propertyset>
            <propertyref prefix="sysprop."/>
            <mapper type="glob" from="sysprop.*" to="*"/>
          </propertyset>

        Now user can add to e.g. project.properties e.g.:
          sysprop.org.netbeans.modules.javahelp=0
        to simulate
          -Dorg.netbeans.modules.javahelp=0

        -->

    </xsl:template>

    <!---
    Generic template to build subdependencies of a certain type.
    Feel free to copy into other modules.
    @param targetname required name of target to generate
    @param type artifact-type from project.xml to filter on; optional, if not specified, uses
                all references, and looks for clean targets rather than build targets
    @return an Ant target which builds (or cleans) all known subprojects
    -->
    <xsl:template name="deps.target">
        <xsl:param name="targetname"/>
        <xsl:param name="type"/>
        <target name="{$targetname}">
            <xsl:attribute name="depends">init</xsl:attribute>
            <xsl:attribute name="unless">no.deps</xsl:attribute>
            <xsl:variable name="references" select="/p:project/p:configuration/projdeps:references"/>
            <xsl:for-each select="$references/projdeps:reference[not($type) or projdeps:artifact-type = $type]">
                <xsl:variable name="subproj" select="projdeps:foreign-project"/>
                <xsl:variable name="subtarget">
                    <xsl:choose>
                        <xsl:when test="$type">
                            <xsl:value-of select="projdeps:target"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="projdeps:clean-target"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:variable name="script" select="projdeps:script"/>
                <ant target="{$subtarget}" inheritall="false" antfile="${{project.{$subproj}}}/{$script}"/>
            </xsl:for-each>
        </target>
    </xsl:template>

</xsl:stylesheet>
