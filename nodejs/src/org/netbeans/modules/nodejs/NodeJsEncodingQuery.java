package org.netbeans.modules.nodejs;

import java.nio.charset.Charset;
import org.netbeans.spi.queries.FileEncodingQueryImplementation;
import org.openide.filesystems.FileObject;

public class NodeJsEncodingQuery extends FileEncodingQueryImplementation {

    @Override
    public Charset getEncoding(FileObject file) {
        return Charset.forName("UTF-8");
    }
}
