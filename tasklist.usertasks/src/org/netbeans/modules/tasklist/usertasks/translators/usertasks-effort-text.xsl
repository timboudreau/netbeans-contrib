<?xml version="1.0" encoding="UTF-8" ?>

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
Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

<!--
    Description: converts user tasks xml into text
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="text"/>

    <xsl:template match="/">
<xsl:text>Task List
---------
</xsl:text>
        <xsl:apply-templates select="tasks/task">
            <xsl:with-param name="level" select="0"/>
        </xsl:apply-templates>
<xsl:text>
---------------------------------------------------------------
This file was created by the NetBeans (http://www.netbeans.org)
User Tasks Module (http://tasklist.netbeans.org)
</xsl:text>
    </xsl:template>
    
    <xsl:template match="task">
        <xsl:param name="level"/>
        
        <!-- indentation -->
        <xsl:call-template name="indent">
            <xsl:with-param name="level" select="$level"/>
        </xsl:call-template>
        
        <xsl:text>* </xsl:text>
        <xsl:value-of select="summary"/> 

        <xsl:variable name="eff">
            <xsl:call-template name="effort">
                <xsl:with-param name="effort" select="@effort"/>
            </xsl:call-template>
        </xsl:variable>
        
        <xsl:text> [</xsl:text>
        <xsl:choose>
            <xsl:when test="starts-with($eff, ' ')">
                <xsl:value-of select="substring-after($eff, ' ')"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$eff"/>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:text>]</xsl:text>
        
<xsl:text>
</xsl:text>

        <xsl:apply-templates select="task">
            <xsl:with-param name="level" select="$level + 1"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template name="indent">
        <xsl:param name="level"/>
        <xsl:if test="$level != 0">
            <xsl:text>  </xsl:text>
            <xsl:call-template name="indent">
                <xsl:with-param name="level" select="$level - 1"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="effort">
        <xsl:param name="effort"/>
        <xsl:variable name="m" select="$effort mod 60"/>
        <xsl:variable name="tmp" select="floor($effort div 60)"/>
        <xsl:variable name="h" select="$tmp mod 8"/>
        <xsl:variable name="d" select="floor($tmp div 8)"/>
        
        <xsl:if test="$d != 0">
            <xsl:value-of select="$d"/>
            <xsl:text>d</xsl:text>
        </xsl:if>
        <xsl:if test="$h != 0">
            <xsl:text> </xsl:text>
            <xsl:value-of select="$h"/>
            <xsl:text>h</xsl:text>
        </xsl:if>
        <xsl:if test="$m != 0">
            <xsl:text> </xsl:text>
            <xsl:value-of select="$m"/>
            <xsl:text>m</xsl:text>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet> 
