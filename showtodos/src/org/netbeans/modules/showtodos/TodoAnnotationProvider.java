/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */
package org.netbeans.modules.showtodos;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.text.BadLocationException;
import static org.netbeans.modules.showtodos.TodoSourceParsing.parseForAnnotatable;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.netbeans.editor.BaseDocument;
import org.netbeans.lib.editor.util.swing.DocumentListenerPriority;
import org.netbeans.lib.editor.util.swing.DocumentUtilities;
import org.netbeans.modules.tasklist.todo.settings.Settings;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.text.Annotatable;
import org.openide.text.Annotation;
import org.openide.text.AnnotationProvider;
import org.openide.text.Line;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.ServiceProvider;

/**
 * Show TODOs items in the editor error stripe
 * (This was based on the wicketsupport module by Tim Boundreau)
 *
 * @author Michel Graciano
 */
@ServiceProvider(service = AnnotationProvider.class)
public class TodoAnnotationProvider implements AnnotationProvider {
   private static final RequestProcessor requestProcessor =
         new RequestProcessor("TODO Annotation Parser", 2);
   private final Map<FileObject, Collection<Annotation>> file2annotation =
         new WeakHashMap<FileObject, Collection<Annotation>>();

   public void annotate(Line.Set lines, Lookup context) {
      final DataObject dataObj = context.lookup(DataObject.class);
      if (dataObj == null) {
         // Nothing to do
         return;
      }
      new ScanningListener().attachTo(dataObj);
      annotate(dataObj);
   }

   private void annotate(final DataObject dataObj) {
      try {
         final FileObject fo = dataObj.getPrimaryFile();
         if (file2annotation.containsKey(fo)) {
            for (Annotation annotation : file2annotation.get(fo)) {
               annotation.detach();
            }
         }
         final List<Annotation> annotations =
               new ArrayList<Annotation>();
         final BaseDocument doc = documentFor(dataObj);
         for (Annotatable annotatable : parse(doc)) {
            final TodoAnnotation ann =
                  new TodoAnnotation(annotatable.getText());
            ann.attach(annotatable);
            annotations.add(ann);
         }
         file2annotation.put(fo, annotations);
      } catch (BadLocationException ex) {
         Exceptions.printStackTrace(ex);
      }
   }

   private List<Annotatable> parse(final BaseDocument doc) throws
         BadLocationException {
      return parseForAnnotatable(doc);
   }

   private static BaseDocument documentFor(final DataObject ob) {
      try {
         EditorCookie ck = ob.getCookie(EditorCookie.class);
         if (ck != null) {
            return (BaseDocument)ck.openDocument();
         }
      } catch (IOException ioe) {
         Exceptions.printStackTrace(ioe);
      }
      return null;
   }

   private class ScanningListener implements DocumentListener,
         PropertyChangeListener, Runnable {
      private static final int AUTO_SCANNING_DELAY = 1000; //ms
      private final RequestProcessor.Task parseTask;
      private Reference<DataObject> dobref;

      ScanningListener() {
         parseTask = requestProcessor.create(this);
      }

      public void insertUpdate(DocumentEvent e) {
         change();
      }

      public void changedUpdate(DocumentEvent e) {
      }

      public void removeUpdate(DocumentEvent e) {
         change();
      }

      public void propertyChange(PropertyChangeEvent evt) {
         if (Settings.PROP_SCAN_COMMENTS_ONLY.equals(evt.getPropertyName())) {
            change();
         }
      }

      private void attachTo(Document document) {
         DocumentUtilities.addDocumentListener(document, this,
               DocumentListenerPriority.AFTER_CARET_UPDATE);
         Settings.getDefault().addPropertyChangeListener(WeakListeners.
               propertyChange(this, Settings.getDefault()));
         restartTimer();
      }

      private void attachTo(DataObject dob) {
         dobref = new WeakReference<DataObject>(dob);
         attachTo(documentFor(dob));
      }

      private void change() {
         restartTimer();
      }

      private void restartTimer() {
         parseTask.schedule(AUTO_SCANNING_DELAY);
      }

      public void run() {
         DataObject dob = dobref.get();
         if (dob != null) {
            annotate(dob);
         }
      }
   }
}
