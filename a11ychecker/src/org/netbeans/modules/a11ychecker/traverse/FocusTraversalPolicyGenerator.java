/*
 * FocusTraversalPolicyGenerator.java
 *
 *
 * @author Michal Hapala, Pavel Stehlik
 */

package org.netbeans.modules.a11ychecker.traverse;

import java.io.StringWriter;
import java.util.Vector;

public class FocusTraversalPolicyGenerator {
    
    public FocusTraversalPolicyGenerator() {
    }
    
    public String generate(OverflowLbl first,OverflowLbl last, Vector<OverflowLbl> vecButtons) {
        StringWriter myWriter = new StringWriter();
        OverflowLbl rFirst = first;
        OverflowLbl rLast = last;
        myWriter.append("new java.awt.FocusTraversalPolicy() {\n");
        
        myWriter.append("public java.awt.Component getDefaultComponent(java.awt.Container focusCycleRoot){\n");
        
        myWriter.append("return "+ rFirst.mycomp.getName() +";\n");
        myWriter.append("}//end getDefaultComponent\n\n");
        
        myWriter.append("public java.awt.Component getFirstComponent(java.awt.Container focusCycleRoot){\n");
        myWriter.append("return "+ rFirst.mycomp.getName() +";\n");
        myWriter.append("}//end getFirstComponent\n\n");
        
        myWriter.append("public java.awt.Component getLastComponent(java.awt.Container focusCycleRoot){\n");
        myWriter.append("return "+ rLast.mycomp.getName() +";\n");
        myWriter.append("}//end getLastComponent\n\n");
        
        myWriter.append("public java.awt.Component getComponentAfter(java.awt.Container focusCycleRoot, java.awt.Component aComponent){\n");
        for (int i = 0; i < vecButtons.size(); i++) {
            OverflowLbl r = ((OverflowLbl)vecButtons.get(i));
            if(r.nextcomp == null) continue;
            myWriter.append("if(aComponent ==  "+ r.mycomp.getName() +"){\n");
            myWriter.append("return "+ r.nextcomp.getName() +";\n");
            myWriter.append("}\n");
        }
        myWriter.append("return "+ rFirst.mycomp.getName() +";//end getComponentAfter\n");
        myWriter.append("}\n");
        
        myWriter.append("public java.awt.Component getComponentBefore(java.awt.Container focusCycleRoot, java.awt.Component aComponent){\n");
        for (int i = 0; i < vecButtons.size(); i++) {
            OverflowLbl r = ((OverflowLbl)vecButtons.get(i));
            if(r.nextcomp == null) continue;
            myWriter.append("if(aComponent ==  "+ r.nextcomp.getName() +"){\n");
            myWriter.append("return "+ r.mycomp.getName() +";\n");
            myWriter.append("}\n");
        }
        myWriter.append("return "+ rLast.mycomp.getName() +";//end getComponentBefore\n\n");
        myWriter.append("}");
        
        myWriter.append("}\n");
        return myWriter.toString();
    }
}
