/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.
 */

package org.netbeans.modules.debugger.callstackviewenhancements.ui;

/**
 *
 * Author: Sandip Chitale (Sandip.Chitale@Sun.Com)
 */
import java.awt.*;
import java.awt.event.*;

/**
 *
 * The class <code>GridBagHelper</code>
 * For example:
 *
 * The following table is based on the Scott's (Swing course instructor)
 * tip for taming the GridBagLayout. The only difference is that the row
 * and column from his tip are exchanged. i.e. the constraints are in a row
 * and components go downwords
 *
 * <code>
 * <pre>
Label     entryLabel  = new Label("Entry");
Button    browse      = new Button("Browse...");
TextField entry       = new TextField();
Button    add         = new Button("Add");
Label     entriesLabel= new Label("Entries");
Button    delete      = new Button("Delete");
List      entries     = new List();
Button    modify      = new Button("Modify");
Button    moveUp      = new Button("Move Up");
Button    moveDown    = new Button("Move Down");

// Non last rows insets
Insets    i  = new Insets(5,5,0,0);
// last row insets
Insets    ir = new Insets(5,5,5,0);
// last col insets
Insets    ic = new Insets(5,5,0,5);
// last row,col insets
Insets    irc= new Insets(5,5,5,5);

GridBagHelper gbh[] = {
//____________________________________________________________________________________________________________________________________
//                            |    |    |     |      |   |   |                            |                             |     |   |
//                  Component | col| row|     |      |wt |wt |        anchor              |            fill             |insts|pad|pad 
//                            |  x |  y |width|height| x | y |                            |                             |     | x | y
//____________________________|____|____|_____|______|___|___|____________________________|_____________________________|_____|___|___
new GridBagHelper(entryLabel  , 0  , 0  , 1   , 1    , 0 , 0 ,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE      , i   , 0 ,0),
//____________________________|____|____|_____|______|___|___|____________________________|_____________________________|_____|___|___
new GridBagHelper(browse      , 1  , 0  , 1   , 1    , 0 , 0 ,GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL, ic  , 0 ,0),
//____________________________|____|____|_____|______|___|___|____________________________|_____________________________|_____|___|___
new GridBagHelper(entry       , 0  , 1  , 1   , 1    , 1 , 0 ,GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL, i   , 0 ,0),
//____________________________|____|____|_____|______|___|___|____________________________|_____________________________|_____|___|___
new GridBagHelper(add         , 1  , 1  , 1   , 1    , 0 , 0 ,GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL, ic  , 0 ,0),
//____________________________|____|____|_____|______|___|___|____________________________|_____________________________|_____|___|___
new GridBagHelper(entriesLabel, 0  , 2  , 1   , 1    , 1 , 0 ,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE      , i   , 0 ,0),
//____________________________|____|____|_____|______|___|___|____________________________|_____________________________|_____|___|___
new GridBagHelper(delete      , 1  , 2  , 1   , 1    , 0 , 0 ,GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL, ic  , 0 ,0),
//____________________________|____|____|_____|______|___|___|____________________________|_____________________________|_____|___|___
new GridBagHelper(entries     , 0  , 3  , 1   , 3    , 0 , 1 ,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH      , ir  , 0 ,0),
//____________________________|____|____|_____|______|___|___|____________________________|_____________________________|_____|___|___
new GridBagHelper(modify      , 1  , 3  , 1   , 1    , 0 , 0 ,GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL, ic  , 0 ,0),
//____________________________|____|____|_____|______|___|___|____________________________|_____________________________|_____|___|___
new GridBagHelper(moveUp      , 1  , 4  , 1   , 1    , 0 , 0 ,GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL, ic  , 0 ,0),
//____________________________|____|____|_____|______|___|___|____________________________|_____________________________|_____|___|___
new GridBagHelper(moveDown    , 1  , 5  , 1   , 1    , 0 , 1 ,GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL, irc , 0 ,0),
//____________________________|____|____|_____|______|___|___|____________________________|_____________________________|_____|___|___
};

Panel mainPanel = new Panel();

GridBagHelper.createGUI(mainPanel, gbh); 
 * </pre>
 * </code>
 * <p>
 * @author Sandip Chitale (Sandip.Chitale@Sun.Com)
 * @see GridBagLayout
 * @see GridBagConstraints
 *
 */
public class GridBagHelper
{
    private Component         component;
    private GridBagConstraints gbc;

    /**
     * Create an instance for a component and its constraints
     *
     * @param component  the component for which the following constraints apply
     * @param gridx      the cell column
     * @param gridy      the cell row
     * @param gridwidth  the number of columns spanned  
     * @param gridheight the number of rows spanned
     * @param weightx    the ratio of horzontal expansion
     * @param weighty    the ratio of vertical expansion 
     * @param anchor     the point in the cell where the component gravitates N, NE, E, SE, S, SW, W, NW, C
     * @param fill       the direction in which the component fills the cell NONE, HORIZONTAL, VERTICAL, BOTH
     * @param insets     the margins around the component
     * @param ipadx      the horizontal padding between cells
     * @param ipady      the vertical padding between cells
     */
    public GridBagHelper(Component component
                         ,int gridx
                         ,int gridy
                         ,int gridwidth
                         ,int gridheight
                         ,double weightx
                         ,double weighty
                         ,int anchor
                         ,int fill
                         ,Insets insets
                         ,int ipadx
                         ,int ipady
                         )
    {
        this.component = component;
        gbc = new GridBagConstraints();
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        gbc.anchor = anchor;
        gbc.fill = fill;
        gbc.insets = insets;
        gbc.ipadx = ipadx;
        gbc.ipady = ipady;    
    }

    /**
     * createGUI - adds the components with suitable constraints.
     *
     * @param c a value of type 'Container'
     * @param gbh[] a value of type 'GridBagHelper'
     */
    public static void createGUI(Container c, GridBagHelper gbh[])
    {
        c.setLayout(new GridBagLayout());
        for (int i = 0; i < gbh.length;i++){
            c.add(gbh[i].component, gbh[i].gbc);
        }
    }
}
