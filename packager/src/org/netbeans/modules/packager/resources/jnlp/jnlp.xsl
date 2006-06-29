<?xml version="1.0" encoding="UTF-8"?>
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
Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
Microsystems, Inc. All Rights Reserved.
-->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:project="http://www.netbeans.org/ns/project/1"
                xmlns:xalan="http://xml.apache.org/xslt"
                exclude-result-prefixes="xalan project">
    <xsl:output method="xml" indent="yes" encoding="UTF-8" xalan:indent-amount="4"/>
    <xsl:template match="/">
<xsl:comment>
  Tags in this JNLP file are substituted by the build script.  You may
  edit this file freely;  if it has been edited, it will not be replaced by
  the Packager module if you upgrade.  If you have made changes in this file
  and want to replace it with a pristine generated copy, simply rename or delete
  it and reopen the project in NetBeans.
</xsl:comment>
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
<!-- XXX do acc. to configuration -->
<!-- XXX all-permissions does not work; need signature; see apisupport/harness/release/jnlp.xml for ideas -->
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
