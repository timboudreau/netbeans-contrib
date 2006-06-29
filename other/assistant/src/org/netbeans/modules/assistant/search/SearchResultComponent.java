/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.assistant.search;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.*;
import org.openide.windows.*;

import java.io.*;
import java.awt.*;
import java.net.*;
import javax.swing.*;
import javax.accessibility.*;

/**
 * Displayes Search Results
 *
 * @author  Richard Gregor
 */
public class SearchResultComponent extends TopComponent{
    static final long serialVersionUID=6021472310168514674L;
    private static SearchResultComponent component = null;

    private SearchResultComponent(){
        putClientProperty("PersistenceType", "OnlyOpened");
        setLayout(new BorderLayout());
        setName(NbBundle.getMessage(SearchResultComponent.class, "LBL_SearchTitle"));   //NOI18N
        JPanel panel = new JPanel();
        JScrollPane scroll = new JScrollPane(panel);
        setCloseOperation(TopComponent.CLOSE_EACH);
        add(scroll);
        initAccessibility();
    }
    
    public static SearchResultComponent getDefault(){
        if(component == null){
            component = new SearchResultComponent();
        }
        return component;
    }
    
    static void clearRef(){
        component = null;
    }    
     
    /**
     * Searchs in database for given expression
     * <pending>
     */
    public void find(String exp){
        this.open();
        this.requestFocus();              
    }
    
    public HelpCtx getHelpCtx(){
        return new HelpCtx("assistant");
    }

    private void initAccessibility(){
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SearchResultComponent.class, "ACS_Search_DESC")); // NOI18N
    }
}

