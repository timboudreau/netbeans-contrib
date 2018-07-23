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

package org.netbeans.spi.looks;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.StringSelection;
import java.beans.*;
import java.util.*;
import javax.swing.JPanel;


import org.openide.nodes.*;
import org.openide.util.actions.SystemAction;
import org.openide.util.actions.CallableSystemAction;
import org.openide.util.HelpCtx;
import org.openide.util.datatransfer.NewType;
import org.openide.util.datatransfer.PasteType;

/** Usefull class for testin Node events.
 */    
public class GoldenEvent {

    private String name;
    private Object oldValue, newValue;
    private Node source;
    private boolean isAdd;
    private Node[] delta;
    private int[] indices;
    private int[] permutation;

    
    /** Proprerty change event
    */
    public GoldenEvent( Node source, String name, Object oldValue, Object newValue ) {
        this.source = source;
        this.name = name;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    /** Children event
     */
    public GoldenEvent( Node source, boolean isAdd, Node[] delta, int[] indices ) {
        this.source = source;
        this.isAdd = isAdd;
        this.delta = delta;
        this.indices = indices;
    }

    /** Children event
     */
    public GoldenEvent( Node source, int[] permutation ) {
        this.source = source;
        this.permutation = permutation;
    }
    
    public Class getRepresentedClass() {
        if ( name == null ) {
            if ( permutation == null ) {
                return NodeMemberEvent.class;
            }
            else {
                return NodeReorderEvent.class;
            }
        }
        else {
            return PropertyChangeEvent.class;
        }
    }

    /* Compares the GoldenEvent against another event
     */

    public boolean compareTo( Object ev ) {
        if ( getRepresentedClass() != ev.getClass() ) {
            return false;
        }

        if ( getRepresentedClass() == PropertyChangeEvent.class ) {
            PropertyChangeEvent pe = (PropertyChangeEvent)ev;
            return source.equals( pe.getSource() ) &&
                   name.equals( pe.getPropertyName() ) &&
                   oldValue == null ? pe.getOldValue() == null : oldValue.equals( pe.getOldValue() ) &&
                   newValue == null ? pe.getNewValue() == null : newValue.equals( pe.getNewValue() );
        }

        else if ( getRepresentedClass() == NodeMemberEvent.class  ) {
            NodeMemberEvent nme = (NodeMemberEvent) ev;

            return source.equals( nme.getNode() ) &&
                   isAdd == nme.isAddEvent() &&
                   Arrays.equals( delta, nme.getDelta() ) &&
                   Arrays.equals( indices, nme.getDeltaIndices() );
        }
        
        else if ( getRepresentedClass() == NodeReorderEvent.class  ) {
            NodeReorderEvent nre = (NodeReorderEvent) ev;

            return source.equals( nre.getNode() ) &&
                   Arrays.equals( permutation, nre.getPermutation() );
        }
        
        else {
            return false;
        }
    }

    /** Compares list of event with array of GoldenEvents. If the 
     * parameter. If the eventClass param is not null only events of 
     * given class are compared.
     */
    public static boolean compare( List events, GoldenEvent[] goldenEvents, Class eventClass ) {

        List filteredEvents = new ArrayList();
        if ( eventClass != null ) {
            for ( Iterator it = events.iterator(); it.hasNext(); ) {
                Object e = it.next();
                if ( e.getClass() == eventClass ) {
                    filteredEvents.add( e );
                }
            }
        }
        else { 
            filteredEvents = events;
        }

        if ( filteredEvents.size() != goldenEvents.length ) {
            return false;
        }

        for ( int i = 0; i < filteredEvents.size(); i++ ) {
            if ( !goldenEvents[i].compareTo( filteredEvents.get( i ) ) ) {
                return false;
            }
        }

        return true;
    }

    public static void printEvents( List events ) {

        for ( Iterator it = events.iterator(); it.hasNext(); ) {
            Object e = it.next();

            if ( e instanceof PropertyChangeEvent ) {
                System.out.println("PCHG : " + ((PropertyChangeEvent)e).getPropertyName() + " : " + ((PropertyChangeEvent)e).getSource() );
                System.out.println(" new : " + ((PropertyChangeEvent)e).getOldValue() );
                System.out.println(" old : " + ((PropertyChangeEvent)e).getNewValue() );
            }

            if ( e instanceof NodeMemberEvent ) {
                NodeMemberEvent ne = (NodeMemberEvent) e;
                System.out.println( ( ne.isAddEvent() ? "cADD : " : "cRMV : " ) + ne.getNode().getName() );

                Node[] delta = ne.getDelta();
                if ( delta == null ) {
                    System.out.println("d    : " + null );
                }
                else {
                    System.out.println("d    : "  );
                    for( int i = 0; i < delta.length; i++ ) {
                        System.out.println("      " + delta[i].getName() );
                    }
                }

                int[] deltaIdx = ne.getDeltaIndices();                
                if ( deltaIdx == null ) {
                    System.out.println("di   : " + null );
                }
                else {
                    System.out.println("di   : " );
                    for( int i = 0; i < deltaIdx.length; i++ ) {
                        System.out.println("      " + deltaIdx[i] );
                    }
                }

            }

            if ( e instanceof NodeReorderEvent ) {
                NodeReorderEvent ne = (NodeReorderEvent) e;
                System.out.println( ( "RORD: " ) + ne.getNode().getName() );

                int[] perm = ne.getPermutation();                
                if ( perm == null ) {
                    System.out.println("d    : " + null );
                }
                else {
                    System.out.println("d    : "  );
                    for( int i = 0; i < perm.length; i++ ) {
                        System.out.println("      " + perm[i] );
                    }
                }

            }
            
        }    
    }
    
    
    public static class Listener implements NodeListener {
        
        private List events = new ArrayList();
        
        
        public void propertyChange(PropertyChangeEvent evt) {
            events.add( evt );
        }        
                
        public void nodeDestroyed(NodeEvent evt) {
            events.add( evt );
        }        
                
        public void childrenReordered(NodeReorderEvent evt) {
            events.add( evt );
        }
                
        public void childrenRemoved(NodeMemberEvent evt) {
            events.add( evt );
        }
                
        public void childrenAdded(NodeMemberEvent evt) {
            events.add( evt );            
        }
        
        public List getEvents() {
            return events;
        }
        
    }

}
    
