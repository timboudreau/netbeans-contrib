package org.netbeans.api.docbook;

import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.netbeans.modules.docbook.ParseJobFactory;
import org.openide.filesystems.FileObject;


/**
 * Callback implementation which provides a regexp pattern.
 */ 
public abstract class PatternCallback<T extends Pattern> extends Callback {
    public PatternCallback(Pattern pattern) {
        super (pattern);
        if (pattern == null) {
            throw new NullPointerException("Pattern null");
        }
    }

    /**
     * Callback which is invoked as the regular expression is processed.
     * Will be called once for each match to the pattern that is found.
     * 
     * @return false if no further matches are needed, true otherwise
     */ 
    public abstract boolean process(FileObject f, MatchResult match, CharSequence content);

    final boolean doCancel(FileObject ob) {
        ParseJobFactory.cancelled (this, ob);
        return true;
    }
    
    public String toString() {
        return "PatternCallback@" + System.identityHashCode(this) + "=" + getProcessor();
    }
}