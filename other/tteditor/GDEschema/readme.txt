--------------------------------------------------------------------------
The contents of this file are subject to the terms of the Common
Development and Distribution License (the License). You may not use this
file except in compliance with the License.  You can obtain a copy of the
License at http://www.netbeans.org/cddl.html

When distributing Covered Code, include this CDDL Header Notice in each
file and include the License. If applicable, add the following below the
CDDL Header, with the fields enclosed by brackets [] replaced by your own
identifying information:
"Portions Copyrighted [year] [name of copyright owner]"

Copyright 2007 Sun Microsystems, Inc. All Rights Reserved
--------------------------------------------------------------------------

This folder contains buildscript to generate default ossj ttvalue jaxb classes.

ant -f build-defaultschemajar-files.xml -Dgde-folder=/projects/tteditor/GDEschema -Djaxb-folder=/Sun/netbeans-5.5 
-Dschema-file=/projects/tteditor/GDEschema/XmlTroubleTicketSchema.xsd


