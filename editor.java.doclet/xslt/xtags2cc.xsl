<?xml version="1.0" encoding="UTF-8" ?>

<!--
                Sun Public License Notice

The contents of this file are subject to the Sun Public License
Version 1.0 (the "License"). You may not use this file except in
compliance with the License. A copy of the License is available at
http://www.sun.com/

The Original Code is NetBeans. The Initial Developer of the Original
Code is Leon Chiver. All Rights Reserved.
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
