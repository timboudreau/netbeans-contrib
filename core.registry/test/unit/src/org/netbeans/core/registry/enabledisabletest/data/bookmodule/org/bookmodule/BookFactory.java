package org.bookmodule;

import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.Environment;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

import java.io.IOException;


public class BookFactory implements Environment.Provider {
    public BookFactory() {
    }

    public static Book createBook() {
        return new Book();
    }


    public static Book createBookWithParams(FileObject fo) {
        String author = (String) fo.getAttribute("author");
        String title = (String) fo.getAttribute("title");
        return new Book(author, title);
    }

    public Lookup getEnvironment(DataObject obj) {
        return Lookups.fixed(new Object[]{new InstanceCookie() {
            Book book = new Book();

            public String instanceName() {
                return book.getClass().getName();
            }

            public Class instanceClass()
                    throws IOException, ClassNotFoundException {
                return book.getClass();
            }

            public Object instanceCreate()
                    throws IOException, ClassNotFoundException {
                return book;
            }
        }});
    }

}

