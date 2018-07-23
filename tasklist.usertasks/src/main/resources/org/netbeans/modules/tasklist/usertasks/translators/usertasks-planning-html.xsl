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
Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
    Description: converts user tasks xml into html
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
        doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"/>

    <xsl:template match="/">
        <html>
            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
                <title>Task List</title>
                <style type="text/css">
                    th { text-align : center; background-color : #222288; color : white }
                    tr.data { background-color: #eeeeee }
                    td.priority { text-align : center }
                    td.owner { text-align : center }
                    td.effort { text-align : right }
                </style>
            </head>
            <body style="font-family : sans-serif">
                <h1>Task List</h1>
                <table width="100%" border="0" cellpadding="0" cellspacing="1">
                    <col width="70%"/>
                    <col width="10%"/>
                    <col width="10%"/>
                    <col width="10%"/>
                    <thead>
                        <tr>
                            <th>Summary</th>
                            <th>Priority</th>
                            <th>Owner</th>
                            <th>Effort</th>
                        </tr>
                    </thead>
                    <tbody>
                        <xsl:apply-templates select="tasks/task">
                            <xsl:with-param name="level" select="0"/>
                        </xsl:apply-templates>
                    </tbody>
                </table>
                <p/>
                <xsl:apply-templates select="tasks/task" mode="details"/>
                <hr/>
                <p><a href="http://validator.w3.org/check/referer">Valid XHTML 1.0!</a> This page was created by the <a href="http://www.netbeans.org">NetBeans</a> 
                        &#160;<a href="http://tasklist.netbeans.org">User Tasks Module</a></p>
            </body>
        </html>
    </xsl:template>
    
    <xsl:template match="task">
        <xsl:param name="level"/>
        <tr class="data">
            <td>
                <table style="width : 100%">
                    <tbody>
                        <tr>
                            <td style="width : {$level * 30 + 1}px">
                            </td>
                            <td>&#x2022; 
                                <span title="{details}">
                                    <xsl:value-of select="summary"/>
                                    <xsl:text> </xsl:text>
                                    <xsl:if test="details != ''">
                                        <a href="#{generate-id()}">(details)</a>
                                    </xsl:if>
                                </span>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </td>
            <td class="priority">
                <xsl:choose>
                    <xsl:when test="@priority = 'high'">
                        <div style="color : #DD0000">high</div>
                    </xsl:when>
                    <xsl:when test="@priority = 'medium-high'">
                        <div style="color : #FF8000">medium-high</div>
                    </xsl:when>
                    <xsl:when test="@priority = 'medium'">
                        <div style="color : black">medium</div>
                    </xsl:when>
                    <xsl:when test="@priority = 'medium-low'">
                        <div style="color : #00BB00">medium-low</div>
                    </xsl:when>
                    <xsl:when test="@priority = 'low'">
                        <div style="color : #008000">low</div>
                    </xsl:when>
                </xsl:choose>
            </td>
            <td class="owner"><xsl:value-of select="@owner"/></td>
            <td class="effort">
                <xsl:call-template name="effort">
                    <xsl:with-param name="effort" select="@effort"/>
                </xsl:call-template>
            </td>
        </tr>
        <xsl:apply-templates select="task">
            <xsl:with-param name="level" select="$level + 1"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="task" mode="details">
        <xsl:if test="details != ''">
            <div style="font-weight : bold; font-family : sans-serif">
                &#x2022; <a name="{generate-id()}"><xsl:value-of select="summary"/></a>
                (Created:
                <xsl:call-template name="format-date">
                    <xsl:with-param name="date" select="@created"/>
                </xsl:call-template>;
                Modified:
                <xsl:call-template name="format-date">
                    <xsl:with-param name="date" select="@modified"/>
                </xsl:call-template>)
            </div>
            <pre style="font-family : sans-serif">
                <xsl:value-of select="details"/>
            </pre>
        </xsl:if>
        <xsl:apply-templates select="task" mode="details"/>
    </xsl:template>
    
    <xsl:template name="format-date">
        <xsl:param name="date"/>
        <span>
            <xsl:attribute name="title">
                <xsl:value-of select="substring-before($date, 'T')"/>
                <xsl:text> </xsl:text>
                <xsl:value-of select="substring-after($date, 'T')"/>
            </xsl:attribute>
            <xsl:value-of select="substring-before($date, 'T')"/>
        </span>
    </xsl:template>
    
    <xsl:template name="effort">
        <xsl:param name="effort"/>
        <xsl:variable name="m" select="$effort mod 60"/>
        <xsl:variable name="tmp" select="floor($effort div 60)"/>
        <xsl:variable name="h" select="$tmp mod 8"/>
        <xsl:variable name="d" select="floor($tmp div 8)"/>
        
        <xsl:if test="$d != 0">
            <xsl:value-of select="$d"/> d
        </xsl:if>
        <xsl:if test="$h != 0">
            <xsl:value-of select="$h"/> h
        </xsl:if>
        <xsl:if test="$m != 0">
            <xsl:value-of select="$m"/> m
        </xsl:if>
    </xsl:template>
</xsl:stylesheet> 
