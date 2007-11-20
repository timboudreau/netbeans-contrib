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
import javafx.ui.filter.*;

class Introduction extends CompositeNode{

}

operation Introduction.composeNode() {
    return Group{
        content: [Text {
        x: 20
        y: 20
        content: "Welcome to JavaFX"
        font: Font {face: VERDANA, style: [ITALIC, BOLD], size: 38}
        fill: LinearGradient {
            x1: 0, y1: 0, x2: 0, y2: 1
            stops: 
            [Stop {
                offset: 0
                color: blue
            },
            Stop {
                offset: 0.5
                color: dodgerblue
            },
            Stop {
                offset: 1
                color: blue
            }]
        }
        filter: 
        [Glow {
            amount: 0.1
        },
        Noise {
            monochrome: true
            distribution: 0
        }]
    }, Text{
        x:20
        y:60
        font: Font{face:VERDANA, style:ITALIC, size:14}
        content:"This tutorial provides examples of 2D vector graphics using the 
JavaFX Script language. For each example selected, the source code is 
displayed in the text area below. The source code can also be browsed 
separately in the NetBeans IDE by selecting the corresponding  Java 
package in the Projects window. Each example can also be shown separately 
in the IDE's Preview window.
"
    }]
    };
}

Introduction{}