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
-->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:p="http://www.netbeans.org/ns/project/1"
                xmlns:xalan="http://xml.apache.org/xslt"
                xmlns:projdeps="http://www.netbeans.org/ns/ant-project-references/1"
                exclude-result-prefixes="xalan p projdeps">
    <xsl:output method="xml" indent="yes" encoding="UTF-8" xalan:indent-amount="4"/>
    <xsl:template match="/">

        <xsl:variable name="name" select="/p:project/p:name"/>
        <project name="{$name}-impl">
            <xsl:attribute name="default">build</xsl:attribute>
            <xsl:attribute name="basedir">..</xsl:attribute>

            <target name="pre-init">
                <xsl:comment> Empty placeholder for easier customization. </xsl:comment>
                <xsl:comment> You can override this target in the ../build.xml file. </xsl:comment>
            </target>

            <target name="init-private">
                <xsl:attribute name="depends">pre-init</xsl:attribute>
                <property file="nbproject/private/private.properties"/>
            </target>

            <target name="init-user">
                <xsl:attribute name="depends">pre-init,init-private</xsl:attribute>
                <property file="${{user.properties.file}}"/>
            </target>

            <target name="init-project">
                <xsl:attribute name="depends">pre-init,init-private,init-user</xsl:attribute>
                <property file="nbproject/project.properties"/>
            </target>

            <target name="do-init"/>

            <target name="post-init">
                <xsl:comment> Empty placeholder for easier customization. </xsl:comment>
                <xsl:comment> You can override this target in the ../build.xml file. </xsl:comment>
            </target>

            <target name="init-check">
                <xsl:attribute name="depends">pre-init,init-private,init-user,init-project,do-init</xsl:attribute>
            </target>


            <target name="init">
                <xsl:attribute name="depends">pre-init,init-private,init-user,init-project,do-init,post-init,init-check</xsl:attribute>
            </target>
            
            <xsl:call-template name="deps.target">
                <xsl:with-param name="targetname" select="'deps-build'"/>
                <xsl:with-param name="type" select="'jar'"/>
                <xsl:with-param name="copyfiles" select="'true'"/>
            </xsl:call-template>            

            <target name="build" depends="init,deps-build">
                <mkdir dir="dist/{$name}.app/Contents/MacOS"/>
                <copy file="start.sh" todir="dist/{$name}.app/Contents/MacOS">
                    <filterset>
                        <filter token="MAIN-CLASS" value="${{main.class}}"/>
                        <filter token="APPNAME" value="{$name}"/>
                    </filterset>
                </copy>
                <chmod file="dist/{$name}.app/Contents/MacOS/start.sh" perm="ugo+rx"/>
                <copy file="Info.plist" todir="dist/{$name}.app/Contents">
                    <filterset>
                        <filter token="APPNAME" value="{$name}"/>
                        <filter token="VERSION" value="1.0"/>
                        <filter token="APPVERSION" value="1.0 hey"/>
                        <filter token="ICONFILENAME" value="{$name}.icns"/>
                    </filterset>
                </copy>
                <copy file="{$name}.icns" tofile="dist/{$name}.app/Contents/Resources/{$name}.icns" failonerror="false"/>
            </target>
            
            <target name="run" depends="build">
                <exec executable="open">
                    <arg file="dist"/>
                </exec>
                <exec executable="open">
                    <arg file="dist/{$name}.app"/>
                </exec>
            </target>

            <xsl:comment>
    ===============
    CLEANUP SECTION
    ===============
    </xsl:comment>

            <xsl:call-template name="deps.target">
                <xsl:with-param name="targetname" select="'deps-clean'"/>
                <xsl:with-param name="copyfiles" select="'false'"/>
            </xsl:call-template>
            
            <target name="do-clean">
                <xsl:attribute name="depends">init</xsl:attribute>
                <delete dir="dist"/>
            </target>

            <target name="post-clean">
                <xsl:comment> Empty placeholder for easier customization. </xsl:comment>
                <xsl:comment> You can override this target in the ../build.xml file. </xsl:comment>
            </target>

            <target name="clean">
                <xsl:attribute name="depends">init,deps-clean,do-clean,post-clean</xsl:attribute>
                <xsl:attribute name="description">Clean build products.</xsl:attribute>
            </target>

        </project>

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
        <xsl:param name="copyfiles"/>
        
        <xsl:variable name="projname" select="/p:project/p:name"/>
        <target name="{$targetname}">
            <xsl:attribute name="depends">init</xsl:attribute>
            <xsl:attribute name="unless">${no.dependencies}</xsl:attribute>
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
                <xsl:variable name="scriptdir" select="substring-before($script, '/')"/>
                <xsl:variable name="scriptdirslash">
                    <xsl:choose>
                        <xsl:when test="$scriptdir = ''"/>
                        <xsl:otherwise>
                            <xsl:text>/</xsl:text>
                            <xsl:value-of select="$scriptdir"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:variable name="scriptfileorblank" select="substring-after($script, '/')"/>
                <xsl:variable name="scriptfile">
                    <xsl:choose>
                        <xsl:when test="$scriptfileorblank != ''">
                            <xsl:value-of select="$scriptfileorblank"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$script"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <ant target="{$subtarget}" inheritall="false">
                    <!-- XXX #43624: cannot use inline attr on JDK 1.5 -->
                    <xsl:attribute name="dir">${project.<xsl:value-of select="$subproj"/>}<xsl:value-of select="$scriptdirslash"/></xsl:attribute>
                    <xsl:if test="$scriptfile != 'build.xml'">
                        <xsl:attribute name="antfile">
                            <xsl:value-of select="$scriptfile"/>
                        </xsl:attribute>
                    </xsl:if>
                </ant>
                <xsl:if test="$copyfiles='true'">
                    <mkdir dir="dist/{$projname}.app/Contents/Resources"/>
                    <copy todir="dist/{$projname}.app/Contents/Resources">
                        <xsl:attribute name="file">${reference.<xsl:value-of select="$subproj"/>.jar}</xsl:attribute>
                    </copy>
                </xsl:if>
            </xsl:for-each>
        </target>
    </xsl:template>

</xsl:stylesheet>
