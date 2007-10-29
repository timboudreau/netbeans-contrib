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
/*
 * AnimatedImage.fx
 * 
 * Created on Oct 8, 2007, 11:54:12 AM
 */

package weatherfx;

import javafx.ui.canvas.*;
import javafx.ui.*;
import java.lang.Thread;
import java.lang.System;

/**
 * Animates a set of images using specified frame delay
 * 
 * @author breh
 */

public class AnimatedImage extends ImageView {

    public attribute baseURL: String;    
    public attribute baseName: String;
    public attribute extension: String;
    
    public attribute imagesCount: Integer;    
    public attribute frameDelay: Number;
    
    public attribute images: Image*;
    private attribute animating: Boolean;
    private attribute animate: Boolean;
    
    public operation playAnimation(doit:Boolean);
    private operation animateImages();
    private operation loadImages();
}


attribute AnimatedImage.frameDelay = 100;
attribute AnimatedImage.animating = false;
attribute AnimatedImage.imagesCount = 0;


trigger on AnimatedImage.parentCanvasElement[oldValue] = newValue {
    //System.out.println("Just a test: {oldValue} : {newValue}");
    if (newValue == null) {
        //System.out.println("Stopping animation {baseName}");
        playAnimation(false);
    } else {
        //System.out.println("Playing animation {baseName}");
        playAnimation(true);
    }
}

operation AnimatedImage.playAnimation(doit:Boolean) {
    if (animate <> doit)  {        
        animate = doit;
        if (animate) {
            if (images == null) {
                    loadImages();
                }
            do later {    
                animateImages();
            }
        }
    }
}



operation AnimatedImage.loadImages() {
    //System.out.println("Loading images: {baseName}");
    if (baseURL <> null) {
        //System.out.println("Loading images :{baseName}:{extension}");
        var count = imagesCount - 1;
        images = foreach (i in [0..count]) Image {
            var idx = i format as <<%03d>>
            url: "{baseURL}/{baseName}{idx}.{extension}" 
        }; 
        if (sizeof images > 0) {
            image = images[0];
        }
        //System.out.println("Images loaded: {baseName}:{sizeof images}");
    }
}


operation AnimatedImage.animateImages() {
    do {
        if (not animating) {
            animating = true;
            while (animate) {
                var imgs = sizeof images - 1;
                if (imgs > 0) {
                    for (i in [0..imgs]) {
                        do later {                    
                            image = images[i];
                            //System.out.println("Animating {baseURL}:{image}");
                        }
                        Thread.sleep(frameDelay);
                        if (not animate) {
                            break;
                        }
                    }
                }
            }
        animating = false;
        }
    }
}