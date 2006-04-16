/*
 * LibGenMain.java
 *
 * Created on April 7, 2006, 3:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package beans2nbm;

import beans2nbm.ui.AuthorInfoPage;
import beans2nbm.ui.LibDataPage;
import beans2nbm.ui.LocateJarPage;
import beans2nbm.ui.OutputLocationPage;

/**
 *
 * @author Tim Boudreau
 */
public class LibGenMain {
    
    /** Creates a new instance of LibGenMain */
    public LibGenMain() {
    }
    
    public static void main(String[] args) {
        Main.setLookAndFeel();
        Main.go (getPageList());
    }

    static Class[] getPageList() {
        Class[] pages = 
            new Class[] {
                LocateJarPage.class, 
                LibDataPage.class, 
                AuthorInfoPage.class,
                OutputLocationPage.class,
        };
        return pages;
    }
    
}
