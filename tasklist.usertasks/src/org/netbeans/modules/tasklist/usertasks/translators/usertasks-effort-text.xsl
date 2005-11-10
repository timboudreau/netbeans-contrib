<?xml version="1.0" encoding="UTF-8" ?>

<!--
                Sun Public License Notice

The contents of this file are subject to the Sun Public License
Version 1.0 (the "License"). You may not use this file except in
compliance with the License. A copy of the License is available at
http://www.sun.com/

The Original Code is NetBeans. The Initial Developer of the Original
Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
Microsystems, Inc. All Rights Reserved.
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
        
        <xsl:if test="details != ''">
            <xsl:text> (</xsl:text>
            <xsl:value-of select="details"/>
            <xsl:text>)</xsl:text>
        </xsl:if>

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
