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
                xmlns:project="http://www.netbeans.org/ns/project/1"
                xmlns:xalan="http://xml.apache.org/xslt"
                exclude-result-prefixes="xalan project">
    <xsl:output method="xml" indent="yes" encoding="UTF-8" xalan:indent-amount="4"/>
    <xsl:template match="/">
<xsl:text disable-output-escaping="yes">
&lt;!--
  Tags in this JNLP file are substituted by the build script.  You may 
  edit this file freely;  if it has been edited, it will not be replaced by
  the Packager module if you upgrade.  If you have made changes in this file
  and want to replace it with a pristine generated copy, simply rename or delete
  it and reopen the project in NetBeans.
  
--&gt;</xsl:text>
<jnlp spec="1.0+"
  codebase="@CODEBASE@"
>
<information>
  <title>@NAME@</title>
  <vendor>@VENDOR@</vendor>
  <homepage href="@HOMEPAGE@" />
  <description>@DESCRIPTION@</description>
  <description kind="short">@LONG-DESCRIPTION@</description>
</information>
<offline-allowed/>
<security/>
 <!-- <j2ee-application-client-permissions/> 
</security>-->
<resources>
  <j2se version="1.2+" /> <!--XXX derive from dependent modules-->
  <jar href="@JAR@"/> <!--XXX-->
</resources>
<application-desc main-class="@MAIN-CLASS@" />
</jnlp>
</xsl:template>
    
</xsl:stylesheet> 
