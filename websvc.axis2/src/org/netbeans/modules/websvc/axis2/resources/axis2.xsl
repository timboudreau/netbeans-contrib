<?xml version="1.0" encoding="UTF-8"?>
<!--
DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.


The contents of this file are subject to the terms of either the GNU
General Public License Version 2 only ("GPL") or the Common
Development and Distribution License("CDDL") (collectively, the
"License"). You may not use this file except in compliance with the
License. You can obtain a copy of the License at
http://www.netbeans.org/cddl-gplv2.html
or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
specific language governing permissions and limitations under the
License.  When distributing the software, include this License Header
Notice in each file and include the License file at
nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
particular file as subject to the "Classpath" exception as provided
by Sun in the GPL Version 2 section of the License file that
accompanied this code. If applicable, add the following below the
License Header, with the fields enclosed by brackets [] replaced by
your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]"

Contributor(s):

The Original Software is NetBeans. The Initial Developer of the Original
Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
                xmlns:j2seproject3="http://www.netbeans.org/ns/j2se-project/3"
                xmlns:xalan="http://xml.apache.org/xslt"
                xmlns:axis2="http://www.netbeans.org/ns/axis2/1"> 
    <xsl:output method="xml" indent="yes" encoding="UTF-8" xalan:indent-amount="4"/>
    <xsl:template match="/">
        
        <project>

            
            <xsl:comment>
                ===================
                JAX-WS WSIMPORT SECTION
                ===================
            </xsl:comment>
            
            <!-- java2wsdl task initialization -->
            <xsl:if test="/axis2:axis2/axis2:service/axis2:generate-wsdl">
                <path id="axis2.classpath">
                    <fileset dir="${{axis2.home}}/lib">
                        <include name="*.jar"/>
                    </fileset>
                </path>
                <target name="java2wsdl-init" depends="init">
                    <mkdir dir="${{basedir}}/xml-resources/axis2/META-INF"/>
                    <taskdef name="java2wsdl" classname="org.apache.ws.java2wsdl.Java2WSDLTask">
                         <classpath>
                            <fileset dir="${{axis2.home}}/lib">
                                <include name="*.jar"/>
                            </fileset>
                         </classpath>
                    </taskdef>
                </target>
            </xsl:if>
            
            <!-- java2wsdl targets - one for each axis2:service -->
            <xsl:for-each select="/axis2:axis2/axis2:service">
                <xsl:if test="axis2:generate-wsdl">
                    <xsl:variable name="wsname" select="@name"/>               
                    <xsl:variable name="service_class" select="axis2:service-class"/>
                    <xsl:variable name="target_namespace" select="axis2:generate-wsdl/@targetNamespace"/>
                    <xsl:variable name="schema_namespace" select="axis2:generate-wsdl/@schemaNamespace"/>
                    
                    <target name="java2wsdl-check-{$wsname}" depends="java2wsdl-init">
                        <condition property="java2wsdl-check-{$wsname}.notRequired">
                            <available file="${{basedir}}/xml-resources/axis2/META-INF/{$wsname}.wsdl" type="file"/>
                        </condition>
                    </target>                    
                    <target name="java2wsdl-{$wsname}" depends="java2wsdl-check-{$wsname}, compile" unless="java2wsdl-check-{$wsname}.notRequired" >
                        <java2wsdl
                            className="{$service_class}"
                            outputLocation="${{basedir}}/xml-resources/axis2/META-INF"
                            targetNamespace="{$target_namespace}"
                            schemaTargetNamespace="{$schema_namespace}">
                                <classpath>
                                    <pathelement location="${{build.dir}}/classes"/>
                                </classpath>
                        </java2wsdl>
                    </target>
                    <target name="java2wsdl-clean-{$wsname}" depends="init" >
                        <delete file="${{basedir}}/xml-resources/axis2/META-INF/{$wsname}.wsdl"/>
                    </target>
                </xsl:if>
            </xsl:for-each>
            
            <!-- generate aar -->
            <xsl:if test="/axis2:axis2/axis2:service">
                <xsl:variable name="wsname" select="/axis2:axis2/axis2:service/@name"/>
                <target name="axis2-aar">
                    <xsl:attribute name="depends">
                        <xsl:for-each select="/axis2:axis2/axis2:service/axis2:generate-wsdl">
                            <xsl:if test="position()!=1"><xsl:text>, </xsl:text></xsl:if>
                            <xsl:text>java2wsdl-</xsl:text><xsl:value-of select="../@name"/>
                        </xsl:for-each>
                    </xsl:attribute>
                    <copy toDir="${{build.dir}}/classes" failonerror="false">
                        <fileset dir="${{basedir}}/xml-resources/axis2">
                            <include name="**/*.wsdl"/>
                            <include name="**/*.xml"/>
                        </fileset>
                    </copy>
                    <xsl:if test="/axis2:axis2/axis2:service/axis2:generate-wsdl">
                        <xsl:attribute name="depends">
                            <xsl:for-each select="/axis2:axis2/axis2:service/axis2:generate-wsdl">
                                <xsl:if test="position()!=1"><xsl:text>, </xsl:text></xsl:if>
                                <xsl:text>java2wsdl-</xsl:text><xsl:value-of select="../@name"/>
                            </xsl:for-each>
                        </xsl:attribute>
                    </xsl:if>
                    <jar destfile="${{build.dir}}/{$wsname}.aar">
                        <fileset excludes="**/Test.class" dir="${{build.dir}}/classes"/>
                    </jar>
                </target>           
            </xsl:if>
            
        </project>
        
    </xsl:template>

</xsl:stylesheet>
