/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.helpbuilder.processors;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashSet;
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
    private HashSet maps;
    
    /**
     * Header part of xml file
     */
    public static final String HEADER="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
    "<!DOCTYPE map\n PUBLIC \""+javax.help.FlatMap.publicIDString+
    "\"\n        \"http://java.sun.com/products/javahelp/map_1_0.dtd\">\n"+
    "\n<map version=\"1.0\">\n\n";
    
    private MapProcessor(){
        maps = new HashSet();
    }
    
    public static MapProcessor getDefault(){
        if(processor == null)
            processor = new MapProcessor();
        return processor;
    }
    
    public void addMap(Map map){
        maps.add(map);
    }
    
    public void removeMap(Map map){
        maps.remove(map);
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
    
    public static class Map{
        
        private String url;
        private String target;
        
        public Map(String target, String url){
            setTarget(target);
            setURL(url);
        }
        
        public void setURL(String url){
            this.url = url;
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
