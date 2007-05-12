<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:project="http://www.netbeans.org/ns/project/1"
                xmlns:scalaProject="http://www.netbeans.org/ns/scala-project/1"
                xmlns:xalan="http://xml.apache.org/xslt"
                exclude-result-prefixes="xalan project scalaProject">

    <xsl:output method="xml" indent="yes" encoding="UTF-8" xalan:indent-amount="4"/>
    
    <xsl:template match="/">
        <xsl:variable name="name" select="/project:project/project:configuration/scalaProject:data/scalaProject:name"/>
        <xsl:variable name="codename" select="translate($name, ' ', '_')"/>
        <project name="{$codename}">
            <xsl:attribute name="default">default</xsl:attribute>
            <xsl:attribute name="basedir">.</xsl:attribute>
            <description>Builds, tests, and runs the project <xsl:value-of select="$name"/>.</description>
            <import file="nbproject/build-impl.xml"/>
        </project>
    </xsl:template>
    
</xsl:stylesheet> 
