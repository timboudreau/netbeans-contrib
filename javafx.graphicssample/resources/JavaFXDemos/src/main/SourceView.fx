/*
 * Copyright (c) 2007, Sun Microsystems, Inc.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  * Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright 
 *    notice, this list of conditions and the following disclaimer in 
 *    the documentation and/or other materials provided with the distribution.
 *  * Neither the name of Sun Microsystems, Inc. nor the names of its 
 *    contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT 
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED 
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */

package main;

import javafx.ui.*;
import javafx.ui.canvas.*;

import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.lang.Exception;
import java.lang.StringBuffer;

class SourceView extends CompositeNode{
    attribute sourcePath: String;
    private attribute source:String;
    attribute rows:Number;
    attribute columns:Number;
    attribute background:AbstractColor;
    attribute title:String;
    attribute width:Number;
    attribute height:Number;
    private attribute baseURL:String;
    private operation updateSource(fromJar:boolean);
    private attribute fromJar:boolean;
}
attribute SourceView.width=500;
attribute SourceView.height=200;
attribute SourceView.baseURL= "{__DIR__}{File.separator}..".replaceAll("%20"," ");
attribute SourceView.title="{__FILE__}";
attribute SourceView.sourcePath ="events{File.separator}InputEvents.fx";
attribute SourceView.rows = 10;
attribute SourceView.columns = 58;
attribute SourceView.background = lightblue;

trigger on new SourceView{
    if (baseURL.substring(0,9)=="jar:file:"){
        baseURL = baseURL.substring(9);
        fromJar = true;
    }
    if(baseURL.substring(0,5)=="file:"){
        baseURL = baseURL.substring(5);
        fromJar = false;
    }
}

trigger on SourceView.sourcePath = newvalue{
    if (baseURL.substring(0,9)=="jar:file:"){
        baseURL = baseURL.substring(9);
        fromJar = true;
    }
    if(baseURL.substring(0,5)=="file:"){
        baseURL = baseURL.substring(5);
        fromJar = false;
    }
    updateSource(fromJar);
}

operation SourceView.updateSource(fromJar:boolean) {
        try{
            var result = new StringBuffer();
            if (fromJar==true){
                var jarStr = baseURL.substring(0,baseURL.indexOf("!"));
                var jarFile = new JarFile(jarStr);
                var jarEntry = jarFile.getJarEntry(sourcePath);
                var is = jarFile.getInputStream(jarEntry);
                var line;
                var intChar;
                var buffer = new byte[1];
                while(true) {
                    intChar = is.read();
                    if(intChar==-1){break;}
                    buffer[0] = intChar;
                    result.append(new String(buffer));
                }
            } else {
                var sourceFile = new File("{baseURL}{File.separator}{sourcePath}");
                var fileReader = new FileReader(sourceFile);
                var reader = new BufferedReader(fileReader);
                var line;
                var lineNumber = 0;
                while(true){
                    line = reader.readLine();
                    lineNumber++;
                    if (line==null) {
                        break;}
                    result.append(line);
                    result.append("\n");
                }
            }
            source = result.toString();
        }catch(e:Exception){
            source = e.getMessage();
        }

}
operation SourceView.composeNode() {
    updateSource(fromJar);
    return Group{
        content: View{content: BorderPanel{
                preferredSize:{width:bind width, height: bind height}
                background:bind background
                center: TextArea{
                    background: bind background
                    //rows: bind height/15
                    editable:false
                    text: bind source
                    font:Font{face:ARIAL, size:12}
            }
        }}
    };
}
Canvas{
        content: SourceView{}
}
