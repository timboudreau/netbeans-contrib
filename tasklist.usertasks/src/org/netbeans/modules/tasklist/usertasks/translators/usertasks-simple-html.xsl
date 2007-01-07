<?xml version="1.0" encoding="UTF-8" ?>

<!--
The contents of this file are subject to the terms of the Common Development
and Distribution License (the License). You may not use this file except in
compliance with the License.

You can obtain a copy of the License at http://www.netbeans.org/cddl.html
or http://www.netbeans.org/cddl.txt.

When distributing Covered Code, include this CDDL Header Notice in each file
and include the License file at http://www.netbeans.org/cddl.txt.
If applicable, add the following below the CDDL Header, with the fields
enclosed by brackets [] replaced by your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]"

The Original Software is NetBeans. The Initial Developer of the Original
Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
Microsystems, Inc. All Rights Reserved.
-->

<!--
    Description: converts user tasks xml into html
    Author: tl
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
                    ul { list-style-type: none }
                </style>
            </head>
            <body style="font-family: sans-serif">
                <h1>Task List</h1>
                <ul>
                    <xsl:apply-templates/>
                </ul>
                <p/>
                <hr/>
                <p><a href="http://validator.w3.org/check/referer">Valid XHTML 1.0!</a> This page was created by the <a href="http://www.netbeans.org">NetBeans</a> 
                        &#160;<a href="http://tasklist.netbeans.org">User Tasks Module</a></p>
            </body>
        </html>
    </xsl:template>
    
    <xsl:template match="task">
        <li>
            <xsl:apply-templates select="." mode="textonly"/>
            <xsl:if test="count(task) != 0">
                <ul>
                    <xsl:apply-templates select="task"/>
                </ul>
            </xsl:if>
        </li>
    </xsl:template>
    
    <xsl:template match="task" mode="textonly">
        <xsl:choose>
            <xsl:when test="@progress = 100">
                <img src="done.gif" alt="Done"/>
            </xsl:when>
            <xsl:otherwise>
                <img src="undone.gif" alt="Undone"/>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:value-of select="summary"/>
    </xsl:template>
</xsl:stylesheet> 
