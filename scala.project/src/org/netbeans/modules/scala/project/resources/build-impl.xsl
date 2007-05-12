<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:p="http://www.netbeans.org/ns/project/1"
                xmlns:xalan="http://xml.apache.org/xslt"
                xmlns:scalaProject="http://www.netbeans.org/ns/scala-project/1"
                xmlns:projdeps="http://www.netbeans.org/ns/ant-project-references/1"
                xmlns:projdeps2="http://www.netbeans.org/ns/ant-project-references/2"
                exclude-result-prefixes="xalan p projdeps projdeps2">
    <xsl:output method="xml" indent="yes" encoding="UTF-8" xalan:indent-amount="4"/>
    <xsl:template match="/">
        
<xsl:comment><![CDATA[
*** GENERATED FROM project.xml - DO NOT EDIT  ***
***         EDIT ../build.xml INSTEAD         ***
]]></xsl:comment>

        <xsl:variable name="name" select="/p:project/p:configuration/scalaProject:data/scalaProject:name"/>
        <!-- Synch with build-impl.xsl: -->
        <xsl:variable name="codename" select="translate($name, ' ', '_')"/>
        <project name="{$codename}-impl">
            <xsl:attribute name="default">default</xsl:attribute>
            <xsl:attribute name="basedir">..</xsl:attribute>

            <target name="default">
                <xsl:attribute name="depends">compile</xsl:attribute>
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
            <target name="-init-properties" depends="-pre-init">
                <property file="nbproject/private/private.properties"/>
                <property file="nbproject/project.properties"/>
                <property environment="env"/>
                <property name="scala.home" value="${{env.SCALA_HOME}}"/>
                <fail unless="scala.home">
            You must set SCALA_HOME or environment property or scala.home
            property in nbproject/private/private.properties to point to
            Scala installation directory.
        </fail>
                <property name="build.dir" value="build"/>
            </target>
            <target name="init" depends="-pre-init,-init-properties"/>

    <xsl:comment> 
    ===================
    COMPILATION SECTION
    ===================
    </xsl:comment>
            <target name="-pre-pre-compile" depends="init">
                <mkdir dir="${{build.dir}}"/>
            </target>
            <target name="compile" depends="init,-pre-pre-compile">
                <taskdef resource="scala/tools/ant/antlib.xml">
                    <classpath>
                        <pathelement location="${{scala.home}}/lib/scala-compiler.jar"/>
                        <pathelement location="${{scala.home}}/lib/scala-library.jar"/>
                    </classpath>
                </taskdef>
                <scalac srcdir="src" destdir="${{build.dir}}">
                    <classpath>
                        <pathelement location="${{scala.home}}/lib/scala-library.jar"/>
                    </classpath>
                </scalac>
            </target>
            <target name="compile-single" depends="init,-pre-pre-compile">
                <taskdef resource="scala/tools/ant/antlib.xml">
                    <classpath>
                        <pathelement location="${{scala.home}}/lib/scala-compiler.jar"/>
                        <pathelement location="${{scala.home}}/lib/scala-library.jar"/>
                    </classpath>
                </taskdef>
                <scalac srcdir="src" destdir="${{build.dir}}">
                    <classpath>
                        <pathelement location="${{scala.home}}/lib/scala-library.jar"/>
                    </classpath>
                    <patternset includes="${{javac.includes}}"/>
                </scalac>
            </target>
            
    <xsl:comment>
    ===============
    EXECUTION SECTION
    ===============
    </xsl:comment>
            <target name="run-single" depends="init,compile-single">
                <fail unless="run.class">Must select one file in the IDE or set run.class</fail>
                <java fork="true" classname="${{run.class}}">
                    <classpath>
                        <path path="${{build.dir}}"/>
                        <pathelement location="${{scala.home}}/lib/scala-library.jar"/>
                    </classpath>
                </java>
            </target>
            
    <xsl:comment>
    ===============
    CLEANUP SECTION
    ===============
    </xsl:comment>
            <target name="clean" depends="init">
                <delete dir="${{build.dir}}"/>
            </target>
            
        </project>
    </xsl:template>

</xsl:stylesheet>
