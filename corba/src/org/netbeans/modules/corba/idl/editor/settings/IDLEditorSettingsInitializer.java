/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package com.netbeans.enterprise.modules.corba.idl.editor.settings;

import java.util.Map;
import java.util.HashMap;

import java.awt.Color;
import java.awt.Font;
import com.netbeans.editor.Settings;
import com.netbeans.editor.SettingsUtil;
import com.netbeans.editor.SettingsNames;
import com.netbeans.editor.SettingsDefaults;
import com.netbeans.editor.Coloring;
import com.netbeans.editor.Syntax;
import com.netbeans.enterprise.modules.corba.idl.editor.coloring.IDLKit;
import com.netbeans.enterprise.modules.corba.idl.editor.coloring.IDLSyntax;

public class IDLEditorSettingsInitializer implements Settings.Initializer {

  public Map updateSettingsMap (Class kitClass, Map settingsMap) {

    if (kitClass == IDLKit.class) { 

      if (settingsMap == null) {
	settingsMap = new HashMap();
      }

      settingsMap.put (SettingsNames.ABBREV_MAP, getIDLAbbrevMap());

      Font boldFont = SettingsDefaults.defaultFont.deriveFont(Font.BOLD);
      Font italicFont = SettingsDefaults.defaultFont.deriveFont(Font.ITALIC);
      Settings.Evaluator boldSubst = new SettingsUtil.FontStylePrintColoringEvaluator(Font.BOLD);
      Settings.Evaluator italicSubst = new SettingsUtil.FontStylePrintColoringEvaluator(Font.ITALIC);
      Settings.Evaluator lightGraySubst = new SettingsUtil.ForeColorPrintColoringEvaluator(Color.lightGray);

      // Colorings
      SettingsUtil.setColoring(settingsMap, IDLSyntax.TEXT.getName(),
          new Coloring(null, null, null)
      );
      SettingsUtil.setColoring(settingsMap, IDLSyntax.ERROR.getName(),
          new Coloring(null, Color.white, Color.red)
      );
      SettingsUtil.setColoring(settingsMap, IDLSyntax.KEYWORD.getName(),
          new Coloring(boldFont, Coloring.FONT_MODE_APPLY_STYLE,
              Color.blue, Coloring.COLOR_MODE_DEFAULT,
              null, Coloring.COLOR_MODE_DEFAULT
          )
      );
      SettingsUtil.setColoring(settingsMap, IDLSyntax.IDENTIFIER.getName(),
          new Coloring(null, null, null)
      );
      SettingsUtil.setColoring(settingsMap, IDLSyntax.METHOD.getName(),
          new Coloring(boldFont, null, null),
          italicSubst // print font will be italic
      );
      SettingsUtil.setColoring(settingsMap, IDLSyntax.OPERATOR.getName(),
          new Coloring(null, null, null)
      );
      SettingsUtil.setColoring(settingsMap, IDLSyntax.LINE_COMMENT.getName(),
          new Coloring(italicFont, Coloring.FONT_MODE_APPLY_STYLE,
              Color.gray, Coloring.COLOR_MODE_DEFAULT,
              null, Coloring.COLOR_MODE_DEFAULT
          ),
          lightGraySubst // print fore color will be gray
      );
      SettingsUtil.setColoring(settingsMap, IDLSyntax.BLOCK_COMMENT.getName(),
          new Coloring(italicFont, Coloring.FONT_MODE_APPLY_STYLE,
              Color.gray, Coloring.COLOR_MODE_DEFAULT,
              null, Coloring.COLOR_MODE_DEFAULT
          ),
          lightGraySubst // print fore color will be gray
      );
      SettingsUtil.setColoring(settingsMap, IDLSyntax.CHAR.getName(),
          new Coloring(null, Color.green.darker(), null)
      );
      SettingsUtil.setColoring(settingsMap, IDLSyntax.STRING.getName(),
          new Coloring(null, Color.magenta, null)
      );
      SettingsUtil.setColoring(settingsMap, IDLSyntax.INT.getName(),
          new Coloring(null, Color.red, null)
      );
      SettingsUtil.setColoring(settingsMap, IDLSyntax.HEX.getName(),
          new Coloring(null, Color.red, null)
      );
      SettingsUtil.setColoring(settingsMap, IDLSyntax.OCTAL.getName(),
          new Coloring(null, Color.red, null)
      );
      SettingsUtil.setColoring(settingsMap, IDLSyntax.LONG.getName(),
          new Coloring(null, Color.red, null)
      );
      SettingsUtil.setColoring(settingsMap, IDLSyntax.FLOAT.getName(),
          new Coloring(null, Color.red, null)
      );
   
      SettingsUtil.setColoring (settingsMap, IDLSyntax.DIRECTIVE.getName(),
	 new Coloring(null, Color.green.darker().darker(), null)
      );

      SettingsUtil.updateListSetting(settingsMap, SettingsNames.COLORING_NAME_LIST,
        new String[] {
          IDLSyntax.TEXT.getName(),
          IDLSyntax.ERROR.getName(),
          IDLSyntax.KEYWORD.getName(),
          IDLSyntax.IDENTIFIER.getName(),
          IDLSyntax.METHOD.getName(),
          IDLSyntax.OPERATOR.getName(),
          IDLSyntax.LINE_COMMENT.getName(),
          IDLSyntax.BLOCK_COMMENT.getName(),
          IDLSyntax.CHAR.getName(),
          IDLSyntax.STRING.getName(),
          IDLSyntax.INT.getName(),
          IDLSyntax.HEX.getName(),
          IDLSyntax.OCTAL.getName(),
          IDLSyntax.LONG.getName(),
          IDLSyntax.FLOAT.getName(),
          IDLSyntax.DIRECTIVE.getName()
        }
      );

    }
    return settingsMap;
  }  
  
  Map getIDLAbbrevMap() {
    Map idlAbbrevMap = new HashMap();
    idlAbbrevMap.put("#d", "#define ");
    idlAbbrevMap.put("#i", "#include ");
    idlAbbrevMap.put("#if", "#ifdef ");
    idlAbbrevMap.put("#ifn", "#ifndef ");
    idlAbbrevMap.put("#e", "#endif");
    idlAbbrevMap.put("#p", "#pragma ");

    idlAbbrevMap.put("at", "attribute ");
    idlAbbrevMap.put("bo", "boolean ");
    idlAbbrevMap.put("ca", "case ");
    idlAbbrevMap.put("co", "const ");
    idlAbbrevMap.put("de", "default");
    idlAbbrevMap.put("do", "double ");
    idlAbbrevMap.put("en", "enum ");
    idlAbbrevMap.put("ex", "exception ");
    idlAbbrevMap.put("FA", "FALSE");
    idlAbbrevMap.put("fa", "FALSE");
    idlAbbrevMap.put("fi", "fixed");
    idlAbbrevMap.put("fl", "float ");
    idlAbbrevMap.put("int", "interface ");
    idlAbbrevMap.put("lo", "long ");
    idlAbbrevMap.put("mo", "module ");
    idlAbbrevMap.put("Ob", "Object");
    idlAbbrevMap.put("ob", "Object");
    idlAbbrevMap.put("oc", "octet ");
    idlAbbrevMap.put("on", "oneway ");
    idlAbbrevMap.put("ra", "raises (");
    idlAbbrevMap.put("re", "readonly ");
    idlAbbrevMap.put("se", "sequence ");
    idlAbbrevMap.put("sh", "short ");
    idlAbbrevMap.put("stu", "struct ");
    idlAbbrevMap.put("str", "string ");
    idlAbbrevMap.put("sw", "switch ");
    idlAbbrevMap.put("TR", "TRUE");
    idlAbbrevMap.put("tr", "TRUE");
    idlAbbrevMap.put("ty", "typedef ");
    idlAbbrevMap.put("uns", "unsigned ");
    idlAbbrevMap.put("uni", "union ");
    idlAbbrevMap.put("wc", "wchar ");
    idlAbbrevMap.put("ws", "wstring ");

    return idlAbbrevMap;
  }
}

/*
 * <<Log>>
 *  3    Jaga      1.2         3/24/00  Miloslav Metelka renaming
 *  2    Jaga      1.1         3/22/00  Miloslav Metelka fix
 *  1    Jaga      1.0         3/15/00  Miloslav Metelka 
 * $
 */
