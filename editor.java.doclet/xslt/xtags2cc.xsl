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
Software is Leon Chiver. All Rights Reserved.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:output method="xml" indent="2"/>

    <xsl:template match="/">
        <doclet-completion>
            <class-tags>
                <xsl:apply-templates select="xdoclet/namespace/tags/tag[level/text()='class']"/>
            </class-tags>
            <method-tags>
                <xsl:apply-templates select="xdoclet/namespace/tags/tag[level/text()='method']"/>
            </method-tags>
            <field-tags>
                <xsl:apply-templates select="xdoclet/namespace/tags/tag[level/text()='field']"/>
            </field-tags>
        </doclet-completion>
    </xsl:template>
    
    <xsl:template match="tag">
        <tag>
            <xsl:attribute name="name">
                <xsl:value-of select="name"/>
            </xsl:attribute>
            <xsl:apply-templates select="parameter"/>
        </tag>
    </xsl:template>
    
    <xsl:template match="parameter">
        <attribute>
            <xsl:attribute name="name"><xsl:value-of select="name"/></xsl:attribute>
            <xsl:attribute name="required"><xsl:value-of select="mandatory"/></xsl:attribute>
            <xsl:if test="@type">
                <xsl:attribute name="type">
                    <xsl:value-of select="@type"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:apply-templates select="option-sets/option-set/options"/>
        </attribute>
    </xsl:template>
    
    <xsl:template match="option">
        <value><xsl:value-of select="."/></value>
    </xsl:template>
    
</xsl:stylesheet>
