/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.helpbuilder.processors;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

/**
 *
 * HelpSetProcessor.java
 *
 * Created on February 21, 2003
 *
 * @author  Richard Gregor
 */
public class HelpSetProcessor implements HelpProcessor{

    private static HelpSetProcessor processor = null;

    /**
     * Header part of xml file
     */
    public static final String HEADER="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
    "<!DOCTYPE helpset\n PUBLIC \""+javax.help.HelpSet.publicIDString+
    "\"\n        \"http://java.sun.com/products/javahelp/helpset_1_0.dtd\">\n"+
    "\n<helpset version=\"1.0\">\n\n";
    
    private String title;
    private String homeID;
    private String mapRef;
    private Vector views;
    
    private HelpSetProcessor(){
        debug("init");
        views = new Vector();
    }
    
    public static HelpSetProcessor getDefault(){
        if(processor == null)
            processor = new HelpSetProcessor();
        return processor;
    }
    
    public void addView(HelpSetProcessor.View view){
        views.addElement(view);
    }
    
    public void export(OutputStream out) throws IOException{ 
        OutputStreamWriter writer = new OutputStreamWriter(out,"UTF-8");
        writer.write(HEADER);
        writeMap(writer);
        writeViews(writer);        
        writer.write("</helpset>");
        try{
            writer.close();
        }catch(IOException e){
            //
        }
    }    
     
    private void writeViews(OutputStreamWriter writer) throws IOException{
        Iterator it = views.iterator();
        HelpSetProcessor.View view;
        String value;
        while(it.hasNext()){
            writer.write("  <view>\n");
            view = (HelpSetProcessor.View)it.next();
            writeValue(writer,view.getName(), "name");
            writeValue(writer,view.getLabel(), "label");
            writeValue(writer,view.getType(),  "type");
            writeValue(writer, view.getImage(), "image");
            if(view.getType().equals("javax.help.SearchView"))
                writeSearchValue(writer,view.getData(),view.getSearchEngine());
            else
                writeValue(writer, view.getData(),  "data");
            writer.write("  </view>\n\n");    
            
        }
    }
    
    private void writeValue(OutputStreamWriter writer,String value, String tag) throws IOException{
        if((value != null)&&(value.length() > 0))
            writer.write("    <"+tag+">"+value+"</"+tag+">\n");
    }
    
    private void writeSearchValue(OutputStreamWriter writer,String value, String engine) throws IOException{
        if((value != null)&&(value.length() > 0))
            writer.write("    <data engine=\""+engine+"\">"+value+"</data>\n");
    }
               
        
    private void writeMap(OutputStreamWriter writer) throws IOException{
        writer.write("  <maps>\n");
        debug("homeID in map: "+homeID);
        writeValue(writer,homeID,"homeID");
        if((mapRef != null)&&(mapRef.length() > 0))
            writer.write("    <mapref location=\""+mapRef+"\" />\n");
        writer.write("  </maps>\n\n");
    }
    
    public void setHomeID(String homeID){
        debug("setHomeID: "+homeID);
        this.homeID = homeID;
    }        
     
    public void setMapRef(String mapRef){
        this.mapRef = mapRef;
    }
        
        
    public void clear(){
        views.clear();
    }
    
    public static class View {
        private String name;
        private String label;
        private String type;
        private String data;
        private String image;
        private String searchEngine;
        final static String DEFAULT_ENGINE="com.sun.java.help.search.DefaultSearchEngine";
        
        public View(){
        }
        
        public View(String name, String label, String type, String data, String searchEngine, String image){
            this.name = name;
            this.label = label;
            this.type = type;
            this.data = data;
            this.image = image;
            if(type.equals("javax.help.SearchView")){
                if(searchEngine != null)
                    this.searchEngine = searchEngine;
                else
                    this.searchEngine = DEFAULT_ENGINE; 
            }
            
        }
        
        public void setName(String name){
            this.name = name;
        }
        
        public String getName(){
            return name;
        }
        
        public void setLabel(String label){
            this.label = label;
        }
        
        public String getLabel(){
            return label;
        }
        
        public void setType(String type){
            this.type = type;
        }
        
        public String getType(){
            return type;
        }
        
        public void setData(String data){
            this.data = data;
        }
        
        public String getData(){
            return data;
        }
        
        public void setSearchEngine(String engine){
            this.searchEngine = engine;
        }
        
        public String getSearchEngine(){
            return searchEngine;
        }
        
        public void setImage(String image){
            this.image = image;
        }
        
        public String getImage(){
            return image;
        }
    }
    
    private boolean DEBUG = false;
    private void debug(String msg){
        if(DEBUG)
            System.err.println("HelpSetProcessor: "+msg);
    }
}
