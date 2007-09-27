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
import java.util.HashMap;
import java.util.Vector;
import java.util.Iterator;

/**
 *
 * MapProcessor.java
 *
 * Created on February 21, 2003
 *
 * @author  Richard Gregor
 */
public class MapProcessor implements HelpProcessor{
    private static MapProcessor processor = null;
    private Vector maps;
    private static Vector pages;
    private static int id = 0;
    private static final String DEFAULT_MAP="map";
    
    /**
     * Header part of xml file
     */
    public static final String HEADER="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
    "<!DOCTYPE map\n PUBLIC \""+javax.help.FlatMap.publicIDString+
    "\"\n        \"http://java.sun.com/products/javahelp/map_1_0.dtd\">\n"+
    "\n<map version=\"1.0\">\n\n";
    
    private MapProcessor(){
        maps = new Vector();
        pages = new Vector();        
    }
    
    public static MapProcessor getDefault(){
        if(processor == null)
            processor = new MapProcessor();
        return processor;
    }
    
    public void addMap(Map map){
        maps.addElement(map);
    }
    
    public void removeMap(Map map){
        maps.remove(map);
    }
    
    public void removeMap(String target, String url){
        Iterator it = maps.iterator();
        while(it.hasNext()){
            MapProcessor.Map map = (MapProcessor.Map)it.next();
            if(map.getTarget().equals(target)&&map.getURL().equals(url)){
                removeMap(map);
                break;
            }
        }               
    }
    
    /**
     *If for given page exists map target then return it.
     *If not then generate one.
     */
    public String getMapTarget(String url){
        if((url == null)||(url.length() == 0))
            return null;
       Iterator it = maps.iterator();
       while(it.hasNext()){
           MapProcessor.Map map = (MapProcessor.Map)it.next();
           if(map.getURL().equals(url))
               return map.getTarget();
       }
       return DEFAULT_MAP + (id++);
    }
    
    public void export(OutputStream out) throws IOException{ 
        OutputStreamWriter writer = new OutputStreamWriter(out,"UTF-8");
        writer.write(HEADER);   
        writeMaps(writer);
        writer.write("</map>");
        
        try{
            writer.close();
        }catch(IOException e){
            //
        }
    } 
    
    private void writeMaps(OutputStreamWriter writer) throws IOException{
        Iterator it = maps.iterator();
        MapProcessor.Map map;
        while(it.hasNext()){
            map = (MapProcessor.Map)it.next();
            writer.write("    <mapID target=\""+map.getTarget()+"\" url=\""+map.getURL()+"\" />\n");
        }
    }
    
    public void clear(){
        maps.clear();
        pages.clear();
    }
    
    public Vector getPages(){
        return pages;
    }
    
        
    public static class Map{
        
        private String url;
        private String target;
        
        public Map(String target, String url){
            setTarget(target);
            setURL(url);
        }
        
        public void setURL(String url){
            this.url = url;
            if(!pages.contains(url))
                pages.addElement(url);            
        }
        
        public String getURL(){
            return url;
        }
        
        public void setTarget(String target){
            this.target = target;
        }
        public String getTarget(){
            return target;
        }
    }    

}
