/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */
package org.netbeans.modules.scala.editor.refactoring;

import java.awt.Color;
import java.io.CharConversionException;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.api.editor.settings.FontColorSettings;
import org.netbeans.editor.BaseDocument;
import org.netbeans.api.java.classpath.ClassPath
import org.netbeans.api.language.util.ast.{AstDfn, AstScope}
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.modules.csl.api.ElementKind
import org.netbeans.modules.scala.editor.{ScalaMimeResolver, ScalaParserResult}
import org.netbeans.modules.scala.editor.lexer.ScalaTokenId
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.indexing.support.QuerySupport;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.xml.XMLUtil;
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashSet

// * @Note CloneableEditorSupport cause highlighting disappear
import org.openide.text.CloneableEditorSupport

/**
 * Various utilies related to Scala refactoring; the generic ones are based
 * on the ones from the Java refactoring module.
 *
 * @author Jan Becicka
 * @author Tor Norbye
 */
object RetoucheUtils {

  def isScalaFile(fo: FileObject): Boolean = {
    fo.getMIMEType == "text/x-scala"
  }

  def getDocument(info: Parser.Result): BaseDocument = {
    if (info != null) {
      info.getSnapshot.getSource.getDocument(true).asInstanceOf[BaseDocument]
    } else null
  }


  /** Compute the names (full and simple, e.g. Foo::Bar and Bar) for the given node, if any, and return as
   * a String[2] = {name,simpleName} */
  /* public static String[] getNodeNames(Node node) {
   String name = null;
   String simpleName = null;
   int type = node.getType();
   if (type == org.mozilla.nb.javascript.Token.CALL || type == org.mozilla.nb.javascript.Token.NEW) {
   name = AstUtilities.getCallName(node, true);
   simpleName = AstUtilities.getCallName(node, false);
   } else if (node instanceof Node.StringNode) {
   name = node.getString();
   } else if (node.getType() == org.mozilla.nb.javascript.Token.FUNCTION) {
   name = AstUtilities.getFunctionFqn(node, null);
   if (name != null && name.indexOf('.') != -1) {
   name = name.substring(name.indexOf('.')+1);
   }
   } else {
   return new String[] { null, null};
   }
   // TODO - FUNCTION - also get full name!

   if (simpleName == null) {
   simpleName = name;
   }

   return new String[] { name, simpleName };
   } */

  def findCloneableEditorSupport(info: Parser.Result): CloneableEditorSupport = {
    try {
      val dob = DataObject.find(info.getSnapshot.getSource.getFileObject)
      findCloneableEditorSupport(dob)
    } catch {case ex: DataObjectNotFoundException => Exceptions.printStackTrace(ex); null}
  }

  def findCloneableEditorSupport(dob: DataObject): CloneableEditorSupport = {
    dob.getCookie(classOf[org.openide.cookies.OpenCookie]) match {
      case x: CloneableEditorSupport => x
      case _ => dob.getCookie(classOf[org.openide.cookies.EditorCookie]) match {
          case x: CloneableEditorSupport => x
          case _ => null
        }
    }
  }

  def htmlize(input: String): String = {
    try {
      XMLUtil.toElementContent(input)
    } catch {case cce: CharConversionException => Exceptions.printStackTrace(cce); input}
  }

//    /** Return the most distant method in the hierarchy that is overriding the given method, or null */
//    public static IndexedMethod getOverridingMethod(JsElementCtx element, CompilationInfo info) {
//        JsIndex index = JsIndex.get(info.getIndex());
//        String fqn = AstUtilities.getFqnName(element.getPath());
//
//        return index.getOverridingMethod(fqn, element.getName());
//    }

  def getHtml(text: String): String = {
    val sb = new StringBuilder
    // TODO - check whether we need Js highlighting or rhtml highlighting
    val th = TokenHierarchy.create(text, ScalaTokenId.language)
    val lookup = MimeLookup.getLookup(MimePath.get(ScalaMimeResolver.MIME_TYPE))
    val settings = lookup.lookup(classOf[FontColorSettings])
    val ts = th.tokenSequence.asInstanceOf[TokenSequence[TokenId]]
    while (ts.moveNext) {
      val token = ts.token
      var category = token.id.name
      var set = settings.getTokenFontColors(category) match {
        case null =>
          category = token.id.primaryCategory match {
            case null => "whitespace" //NOI18N
            case x => x
          }
          settings.getTokenFontColors(category)
        case x => x
      }
      val tokenText = htmlize(token.text.toString)
      sb.append(color(tokenText, set))
    }
    sb.toString
  }

  private def color(string: String, set: AttributeSet): String = {
    if (set == null) {
      return string
    }
    if (string.trim.length == 0) {
      return string.replace(" ", "&nbsp;").replace("\n", "<br>") //NOI18N
    }
    val sb = new StringBuilder(string)
    if (StyleConstants.isBold(set)) {
      sb.insert(0, "<b>") //NOI18N
      sb.append("</b>")   //NOI18N
    }
    if (StyleConstants.isItalic(set)) {
      sb.insert(0, "<i>") //NOI18N
      sb.append("</i>")   //NOI18N
    }
    if (StyleConstants.isStrikeThrough(set)) {
      sb.insert(0, "<s>") //NOI18N
      sb.append("</s>")   //NOI18N
    }
    sb.insert(0, "<font color=" + getHTMLColor(StyleConstants.getForeground(set)) + ">") //NOI18N
    sb.append("</font>")  //NOI18N

    sb.toString
  }

  private def getHTMLColor(c: Color): String = {
    var colorR = "0" + Integer.toHexString(c.getRed)   //NOI18N
    colorR = colorR.substring(colorR.length - 2)

    var colorG = "0" + Integer.toHexString(c.getGreen) //NOI18N
    colorG = colorG.substring(colorG.length - 2)

    var colorB = "0" + Integer.toHexString(c.getBlue)  //NOI18N
    colorB = colorB.substring(colorB.length - 2)

    "#" + colorR + colorG + colorB //NOI18N
  }

  def isFileInOpenProject(file: FileObject): Boolean = {
    assert(file != null)
    val p = FileOwnerQuery.getOwner(file)
    OpenProjects.getDefault.isProjectOpen(p)
  }

  def isOnSourceClasspath(fo: FileObject): boolean = {
    val p = FileOwnerQuery.getOwner(fo)
    if (p == null) {
      return false
    }
    val opened = OpenProjects.getDefault.getOpenProjects
    for (i <- 0 until opened.length) {
      if (p.equals(opened(i)) || opened(i).equals(p)) {
        val gr = ProjectUtils.getSources(p).getSourceGroups(Sources.TYPE_GENERIC)
        for (j <- 0 until gr.length) {
          if (fo == gr(j).getRootFolder) {
            return true
          }
          if (FileUtil.isParentOf(gr(j).getRootFolder, fo)) {
            return true
          }
        }
        return false
      }
    }
    false
  }

  def isRefactorable(file: FileObject): boolean = {
    isScalaFile(file) && isFileInOpenProject(file) && isOnSourceClasspath(file)
  }

// XXX: parsingapi
//    public static boolean isClasspathRoot(FileObject fo) {
//        ClassPath cp = ClassPath.getClassPath(fo, ClassPath.SOURCE);
//        if (cp != null) {
//            FileObject f = cp.findOwnerRoot(fo);
//            if (f != null) {
//                return fo.equals(f);
//            }
//        }
//
//        return false;
//    }

  def getPackageName(folder: FileObject): String = {
    assert(folder.isFolder, "argument must be folder") //NOI18N
    val p = FileOwnerQuery.getOwner(folder)
    if (p != null) {
      val s = ProjectUtils.getSources(p)
      for (g <- s.getSourceGroups(Sources.TYPE_GENERIC)) {
        val relativePath = FileUtil.getRelativePath(g.getRootFolder(), folder)
        if (relativePath != null) {
          return relativePath.replace('/', '.') //NOI18N
        }
      }
    }
    
    "" //NOI18N
  }

// XXX: parsingapi
//    public static FileObject getClassPathRoot(URL url) throws IOException {
//        FileObject result = URLMapper.findFileObject(url);
//        File f = FileUtil.normalizeFile(new File(url.getPath()));
//        while (result==null) {
//            result = FileUtil.toFileObject(f);
//            f = f.getParentFile();
//        }
//        return ClassPath.getClassPath(result, ClassPath.SOURCE).findOwnerRoot(result);
//    }
//
//    public static ClasspathInfo getClasspathInfoFor(FileObject ... files) {
//        assert files.length >0;
//        Set<URL> dependentRoots = new HashSet<URL>();
//        for (FileObject fo: files) {
//            Project p = null;
//            if (fo!=null) {
//                p = FileOwnerQuery.getOwner(fo);
//            }
//            if (p!=null) {
//                ClassPath classPath = ClassPath.getClassPath(fo, ClassPath.SOURCE);
//                if (classPath == null) {
//                    return null;
//                }
//                FileObject ownerRoot = classPath.findOwnerRoot(fo);
//                if (ownerRoot != null) {
//                    URL sourceRoot = URLMapper.findURL(ownerRoot, URLMapper.INTERNAL);
//                    dependentRoots.addAll(SourceUtils.getDependentRoots(sourceRoot));
//                    //for (SourceGroup root:ProjectUtils.getSources(p).getSourceGroups(JsProject.SOURCES_TYPE_Js)) {
//                    for (SourceGroup root:ProjectUtils.getSources(p).getSourceGroups(Sources.TYPE_GENERIC)) {
//                        dependentRoots.add(URLMapper.findURL(root.getRootFolder(), URLMapper.INTERNAL));
//                    }
//                } else {
//                    dependentRoots.add(URLMapper.findURL(fo.getParent(), URLMapper.INTERNAL));
//                }
//            } else {
//                for(ClassPath cp: GlobalPathRegistry.getDefault().getPaths(ClassPath.SOURCE)) {
//                    for (FileObject root:cp.getRoots()) {
//                        dependentRoots.add(URLMapper.findURL(root, URLMapper.INTERNAL));
//                    }
//                }
//            }
//        }
//
//        ClassPath rcp = ClassPathSupport.createClassPath(dependentRoots.toArray(new URL[dependentRoots.size()]));
//        ClassPath nullPath = ClassPathSupport.createClassPath(new FileObject[0]);
//        ClassPath boot = files[0]!=null?ClassPath.getClassPath(files[0], ClassPath.BOOT):nullPath;
//        ClassPath compile = files[0]!=null?ClassPath.getClassPath(files[0], ClassPath.COMPILE):nullPath;
//
//        if (boot == null || compile == null) { // 146499
//            return null;
//        }
//
//        ClasspathInfo cpInfo = ClasspathInfo.create(boot, compile, rcp);
//        return cpInfo;
//    }
//
//    public static ClasspathInfo getClasspathInfoFor(JsElementCtx ctx) {
//        return getClasspathInfoFor(ctx.getFileObject());
//    }
//
  def getScalaFilesInProject(fileInProject: FileObject): Set[FileObject] = {
    getScalaFilesInProject(fileInProject, false)
  }

  def getScalaFilesInProject(fileInProject: FileObject, excludeReadOnlySourceRoots: Boolean): Set[FileObject] = {
    val files = new HashSet[FileObject] // 100
    val sourceRoots = QuerySupport.findRoots(fileInProject,
                                             null,
                                             java.util.Collections.singleton(ClassPath.BOOT),
                                             java.util.Collections.emptySet[String])
    val itr = sourceRoots.iterator
    while (itr.hasNext) {
      val root = itr.next
      if (excludeReadOnlySourceRoots && !root.canWrite) {
        // skip read only source roots
      } else {
        val name = root.getName
        if (name.equals("vendor") || name.equals("script")) { // NOI18N
          // skip non-refactorable parts in renaming
        } else {
          addScalaFiles(files, root)
        }
      }
    }

    files.toSet
  }

  private def addScalaFiles(files: HashSet[FileObject], f: FileObject) {
    if (f.isFolder) {
      for (child <- f.getChildren) {
        addScalaFiles(files, child)
      }
    } else if (isScalaFile(f)) {
      files.add(f)
    }
  }

  def getTopTemplates(scopes: Seq[AstScope], result: ArrayBuffer[AstDfn]) {
    for (scope <- scopes) {
      result ++= scope.dfns filter {dfn => dfn.kind match {
          case ElementKind.CLASS | ElementKind.MODULE => true
          case _ => false
        }
      }

      scope.bindingDfn match {
        case Some(x) if x.kind == ElementKind.PACKAGE => getTopTemplates(scope.subScopes, result)
        case _ =>
      }
    }
  }

}
