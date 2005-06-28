package org.netbeans.modules.codeinfo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

public class CodeInfoComponent extends TopComponent {
    private static final String ID = "codeInfo"; //NOI18N
    JEditorPane src = new JEditorPane();
    JEditorPane doc = new JEditorPane();
    
    /** Creates a new instance of CodeInfoComponent */
    public CodeInfoComponent() {
        src.setEditable(false);
        doc.setEditable(false);
        src.setBackground (new Color (255, 255, 222));
        doc.setBackground (new Color (238, 238, 255));
        
        src.setContentType("text/x-java");
        doc.setContentType("text/html");
        
        setLayout (new BorderLayout());
        
        JSplitPane split = new JSplitPane();
        JScrollPane sscroll = new JScrollPane(src);
        sscroll.setBorder (null);
        sscroll.setViewportBorder(null);
        split.setRightComponent (sscroll);
        JScrollPane dscroll = new JScrollPane (doc);
        dscroll.setBorder (null);
        dscroll.setViewportBorder(null);
        split.setLeftComponent (dscroll);
        split.setDividerLocation(0.5d);
        add (split, BorderLayout.CENTER);
        setDisplayName (NbBundle.getMessage (CodeInfoComponent.class,
                "LBL_CodeInfo")); //NOI18N
    }
    
    public void setContent (String source, String docs) {
        src.setText (source);
        doc.setText (docs);
        revalidate();
    }
    
    protected String preferredID() {
        return ID;
    }
    
    public int getPersistenceType() {
        return PERSISTENCE_ONLY_OPENED;
    }
    
    public void requestActive() {
        src.requestFocus();
    }
    
    public void componentShowing() {
        listener = new Listener(this);
        listener.attach();
    }
    
    private Listener listener = null;
    
    private static Reference ref = null;
    
    public void componentHidden() {
        listener.detach();
        listener = null;
    }
    
    public void open() {
        Mode m = WindowManager.getDefault().findMode("output"); //NOI18N
        if (m != null) {
            m.dockInto(this);
        }
        super.open();
    }
    
    public static final CodeInfoComponent getDefault () {
        CodeInfoComponent result = null;
        if ( ref != null ) {
            result = (CodeInfoComponent) ref.get ();
        }
        if ( result == null ) {
            result = new CodeInfoComponent ();
            ref = new WeakReference ( result );
        }
        return result;
    }

    public static final CodeInfoComponent findDefault () {
        CodeInfoComponent nav = (CodeInfoComponent)
                WindowManager.getDefault ().findTopComponent ( ID ); //NOI18N
        if ( nav == null ) {
            nav = getDefault ();
            ErrorManager.getDefault ().log ( ErrorManager.INFORMATIONAL,
                    "Could not locate the navigator component via its winsys id" ); //NOI18N
        }
        return nav;
    }
    
    
}
