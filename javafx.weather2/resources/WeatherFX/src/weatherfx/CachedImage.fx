/*
 * CachedImage.fx
 *
 * Created on Apr 17, 2008, 2:23:23 PM
 */

package weatherfx;

import javafx.gui.*;
import java.lang.*;
import javafx.lang.*;

/**
 * @author breh
 */

public class CachedImage {
    
    private static attribute urls:String[];
    private static attribute images:Image[];
    
    public static function getCachedImage(url:String):Image {
        var img:Image = null;
        if (url <> null) {
            var imgIndex =  Sequences.indexOf(urls, url); 
            if (imgIndex >=0) {
                //System.out.println("Getting cached {url}");
                img = images[imgIndex];
            } else {
                //System.out.println("Getting new {url}");
                img = Image {url:url};
                insert url into urls;
                insert img into images;
                //System.out.println("Size of cache {sizeof urls}");
            }
        } 
        return img;
    }

}