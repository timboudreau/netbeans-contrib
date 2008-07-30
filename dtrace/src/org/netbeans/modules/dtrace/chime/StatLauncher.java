/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.dtrace.chime;


/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at usr/src/OPENSOLARIS.LICENSE
 * or http://www.opensolaris.org/os/licensing.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at usr/src/OPENSOLARIS.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright 2007 Sun Microsystems, Inc.  All rights reserved.
 * Use is subject to license terms.
 *
 * ident	"@(#)StatLauncher.java	1.61	08/01/16 SMI"
 */

import java.util.*;
import java.text.*;
import java.io.*;
import java.beans.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.management.JMException;
import org.opensolaris.chime.util.resources.*;
import org.opensolaris.chime.resources.*;
import org.opensolaris.chime.swingx.*;
import org.opensolaris.chime.swingx.event.*;
import org.opensolaris.chime.util.*;
import org.opensolaris.chime.wizard.*;
import org.opensolaris.os.dtrace.*;
import org.opensolaris.dtracex.*;
import gnu.getopt.Getopt;
import java.util.logging.*;
import javax.swing.SwingConstants;
import org.opensolaris.chime.AboutChimeDisplay;
import org.opensolaris.chime.AggregationDisplay;
import org.opensolaris.chime.ChimeUtilities;
import org.opensolaris.chime.ColumnDescription;
import org.opensolaris.chime.DirectoryDescription;
import org.opensolaris.chime.DisplayDescription;
import org.opensolaris.chime.DisplayGenerator;
import org.opensolaris.chime.ListProbes;
import org.opensolaris.chime.Preferences;
import org.opensolaris.chime.ProgramDisplay;
import org.opensolaris.chime.ProgramMacroVariables;
import org.opensolaris.chime.Programs;
import org.opensolaris.chime.RenderingType;
import org.opensolaris.chime.TipsDisplay;
import org.opensolaris.chime.TotalType;
import org.opensolaris.chime.TotallerDescription;
import org.opensolaris.chime.ValueType;

/**
 * @author Tom Erickson
 * @author Bill Rushmore
 */
public class StatLauncher {
    // Any change checked in for this file updates the SCCS date field.
    // Chime parses that date and displays it in the main window to
    // indicate how current your version of Chime is.  The DUMMY
    // variable is just a convenient place to make a change to this file
    // by incrementing the unused value.
    //
    // Also update the minor version number in
    // chime/resources/org/opensolaris/chime/text/about.html and
    // pkgdefs/OSOL0chime/pkginfo (Help | About box and package info).
    // Finally, update
    // http://opensolaris.org/os/project/dtrace-chime/changelog/
    // (Version History on OpenSolaris project page).
    //
    static final int DUMMY = 21;

    static Logger logger = Logger.getLogger(StatLauncher.class.getName());
    static final Date DATE;
    static final String OPTSTR = "ac:C:d:D:F:gGh:kl:m:M:n:p:P:s:t:TwWx:Z";
    static final String CLASSNAME = StatLauncher.class.getSimpleName();
    static final String RECORDING_OFF_ACTION = "RECORDING_OFF";
    static final String RECORDING_SERIALIZATION_ACTION =
	    "RECORDING_SERIALIZATION";
    static final String RECORDING_XML_ACTION = "RECORDING_XML";
    static final String DESKTOP_LAF_ACTION = "DESKTOP_LAF";
    static final String JAVA_LAF_ACTION = "JAVA_LAF";
    public static final String DIRFILE = "description.xml";
    public static final String CHIME_DIR = ".chime";
    public static final String NEW_DISPLAYS_DIRNAME = "new";
    public static final File CHIME_HOME;

    static {
	String home = System.getProperty("CHIME_HOME");
	if (home == null) {
	    home = ".";
	}
	CHIME_HOME = new File(home);
    }

    static {
	DateFormat f = new SimpleDateFormat("yy/MM/dd");
	Date d;
	try {
	    d = f.parse("08/01/16");
	    if (d == null) {
		d = new Date();
	    }
	} catch (ParseException e) {
	    d = new Date();
	}
	DATE = d;
    }
    


    private static String hostname;
    private static int port;
    static final int DEFAULT_PORT = 5088; // Set in startup script

    private static boolean testMode = false;
    private static boolean protoMode = false;
    private static boolean xmlMode = false;
    private static boolean recordingMode = false;

    public static final File CURRENT_DIR =
	    new File(System.getProperty("user.dir"));
    static final String RECORDING_DIRNAME = "recordings";
    static final String DISPLAY_DIRNAME = "displays";
    static final File DISPLAY_DIR = new File(CHIME_HOME, DISPLAY_DIRNAME);
    static final File RECORDING_DIR = new File(CHIME_HOME, RECORDING_DIRNAME);
    
    static {
	if (!RECORDING_DIR.exists()) {
	    try {
		RECORDING_DIR.mkdir();
	    } catch (SecurityException e) {
		System.err.println(e.getMessage());
	    }
	}
    }
    static File recordingDirectory = RECORDING_DIR;
    public static File displayDirectory = DISPLAY_DIR;
    static File defaultDirectory = DISPLAY_DIR;
    static JMenuItem browseDisplaysMenuItem;
    static JMenuItem loadDefaultDirectoryMenuItem;
    static JPanel chimePanel;
    static XList tracesList;
    static JTextPane descTextPane;
    static XComboBox traceGroupCombo;
    static java.util.List <DisplayDescription> descriptions =
	    Collections. <DisplayDescription> emptyList();
    static String displayGroupDescription;
    static String defaultDisplaysName;
    static String newDisplaysName;
    static String previousDisplayGroupName;

    static final Comparator <DisplayDescription> DISPLAY_CMP;
    static final ElementMatcher <DisplayDescription, String>
	    DISPLAY_TITLE_MATCHER;
    static {
	DISPLAY_CMP = new Comparator <DisplayDescription> () {
	    public int compare(DisplayDescription d1, DisplayDescription d2) {
		String t1 = d1.getTitle();
		String t2 = d2.getTitle();
		return t1.compareTo(t2);
	    }
	};
	DISPLAY_TITLE_MATCHER = new ElementMatcher
		<DisplayDescription, String> () {
	    public boolean
	    match(DisplayDescription display, String title)
	    {
		return title.equals(display.getTitle());
	    }
	};
    }

    static Map <String, File> displayGroups;
    static HistoryList <String> historyList;
    static {
	displayGroups = new LinkedHashMap <String, File> ();
	historyList = new HistoryList <String> ();
    }

    public static Preferences preferences;
    static String lafOption;
    static boolean consoleOption;

    private static boolean done;
    private static boolean lookAndFeelSet;
    private static JPanel contentPane;
    private static ActionListener lookAndFeelItemListener;

    static void
    initResources()
    {
	// preload resources
	Text.init();
	FText.init();
	ValidationText.init();
	Images.init();
	ChimeText.init();
	ChimeFText.init();
	ChimeEnumText.init();
	ChimeImages.init();
    }

    static {
       
        initResources();

	Persistence.setApplicationDirectory(CHIME_DIR);
	File preferencesFile = Persistence.getFile("preferences.xml");
	if (preferencesFile.exists()) {
	    try {
		ExceptionListener listener = new ExceptionListener() {
		    public void exceptionThrown(Exception e) {
			Logging.warning(logger, e);
		    }
		};
		XMLDecoder decoder = Persistence.getXMLDecoder(
			preferencesFile, listener);
		Object object = decoder.readObject();
		decoder.close();
		if (object instanceof Preferences) {
		    preferences = (Preferences)object;
		}
	    } catch (Exception e) {
		Logging.warning(logger, e);
                e.printStackTrace();
	    }
	}
	if (preferences == null) {
	    preferences = new Preferences();
	}
        org.opensolaris.chime.StatLauncher.preferences = preferences;
    }

    static void
    monitorMemory()
    {
	new Thread(new Runnable() {
	    public void run() {
		int q = 0;
		while (!done) {
		    if (!logger.isLoggable(Level.INFO)) {
			break;
		    }

		    try {
			Thread.sleep(1000L);
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		    System.out.println("used memory (bytes): " +
			    (Runtime.getRuntime().totalMemory() -
			    Runtime.getRuntime().freeMemory()));
		    if (q % 5 == 0) {
			System.gc();
		    }
		    ++q;
		}
	    }
	}, "profiler").start();
    }
    
    public static void setHostname(String h) {
        hostname = h;
        org.opensolaris.chime.StatLauncher.hostname = h;  
    }
    
    public static void setPort(int p) {
        port = p;
        org.opensolaris.chime.StatLauncher.port = p;
    } 
    
    public static JPanel
    getContentPane() {
        return contentPane;
    }
    
    /*
    public static String
    getExecutablePathName()
    {
	// $CHIME_HOME/bin/chime
	File chime = new File(StatLauncher.CHIME_HOME, "bin");
	chime = new File(chime, "chime");
	String pathName;
	try {
	    pathName = chime.getCanonicalPath();
	} catch (Exception e) {
	    pathName = chime.getPath();
	}
	return pathName;
    }
     */

    /**
     * In test mode display is suppressed and data is printed to the
     * console.  It's up to each display to check this mode and set
     * behavior accordingly in the {@code launch()} and {@code
     * launchClient()} methods.  This class only reports the mode and
     * does not guarantee that every display honors it.
     */
    public static boolean
    isTestMode()
    {
	return testMode;
    }

    public static boolean
    isProtoMode()
    {
	return protoMode;
    }

    public static boolean
    isXMLMode()
    {
	return xmlMode;
    }

    public static boolean
    isRecordingMode()
    {
	return recordingMode;
    }

    private static void
    usage()
    {
	System.err.println(ChimeStartupText.USAGE);
	System.exit(2);
    }

    /*
    private static void
    packFrame()
    {
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		frame.pack();
	    }
	});
    }
     */

    public static Consumer
    getConsumer() throws IOException, ClassNotFoundException,
	    VersionMismatchException, JMException
    {
	Consumer consumer;
	if (hostname == null) {
	    consumer = new LocalConsumer();
	} else {
	    consumer = new RemoteConsumer(hostname, port);
	}

	return consumer;
    }

    public static String
    getHostname()
    {
	return hostname;
    }

    public static int
    getPort()
    {
	return port;
    }

    private static String
    getListText(DisplayDescription d)
    {
	String text;
	if (AggregationDisplay.isPrompt(d)) {
	    text = FText.format(FText.BUTTON_ELLIPSIS, d.getTitle());
	} else {
	    text = d.getTitle();
	}

	return text;
    }

    public static void
    reload()
    {
	// Save current selection
	int i = tracesList.getSelectedIndex();
	String selectedTitle = null;
	DisplayDescription description;
	if (i >= 0) {
	    description = descriptions.get(i);
	    selectedTitle = description.getTitle();
	}

	loadDisplayList(displayDirectory);

	// Restore selection (if any)
	if (selectedTitle != null) {
	    for (i = 0; i < descriptions.size(); ++i) {
		description = descriptions.get(i);
		if (description.getTitle().equals(selectedTitle)) {
		    tracesList.setSelectedIndex(i);
		    return;
		}
	    }
	}
    }

    private static void
    updateTraceGroupCombo()
    {
	String[] history = historyList.toArray(new String[historyList.size()]);
	traceGroupCombo.load(history);
	if (traceGroupCombo.getItemCount() > 0) {
	    traceGroupCombo.setSelectedIndexWithoutAction(0);
	}
    }

    private static void
    initHistory()
    {
	assert (!displayGroups.isEmpty() && (traceGroupCombo != null));

	java.util.List <String> names =
		new ArrayList <String> (displayGroups.size());
	for (String name : displayGroups.keySet()) {
	    names.add(name);
	}

	// First, order by load menu tree.  Add in reverse order so the
	// top levels of the menu are at the top of the list (most
	// recent).
	ListIterator <String> itr = names.listIterator(names.size());
	while (itr.hasPrevious()) {
	    historyList.add(itr.previous());
	}

	// Second, order by most recently used during the last Chime
	// session.
	String[] history = preferences.getHistory();
	for (int i = history.length - 1; i >= 0; --i) {
	    if (displayGroups.containsKey(history[i])) {
		historyList.add(history[i]);
	    }
	}

	// Finally, put the default directory and the new displays
	// directory at the top of the list.
	if (newDisplaysName != null) {
	    historyList.add(newDisplaysName);
	}
	historyList.add(defaultDisplaysName);

	updateTraceGroupCombo();
    }

    private static void
    updateHistory(File dir, DirectoryDescription d)
    {
	String name;
	if (dir.equals(defaultDirectory)) {
	    name = defaultDisplaysName;
	} else {
	    name = ((d == null || Strings.blank(d.getName()))
		    ? dir.getName() : d.getName());
	}
	historyList.add(name);
	String[] h = new String[historyList.size()];
	for (int i = 0; i < historyList.size(); ++i) {
	    h[i] = historyList.get(i);
	}
	preferences.setHistory(h);

	updateTraceGroupCombo();
    }

    private static void
    loadDisplayList(File dir)
    {
	assert (SwingUtilities.isEventDispatchThread());
	DefaultListModel model = (DefaultListModel)tracesList.getModel();
	model.clear();
	displayDirectory = dir;
        org.opensolaris.chime.StatLauncher.displayDirectory = displayDirectory;
        
	descriptions = AggregationDisplay.getDescriptions();
	Collections.sort(descriptions, DISPLAY_CMP);
	File dirdescFile = new File(dir, DIRFILE);
	DirectoryDescription dirdesc =
		getDirectoryDescriptionFromFile(dirdescFile);
	updateHistory(dir, dirdesc);
	java.util.List <String> order = null;
	if (dirdesc == null) {
	    displayGroupDescription = Strings.EMPTY_STRING;
	} else {
	    displayGroupDescription = dirdesc.getDescription();
	    order = dirdesc.getOrder();
	}
	setDescriptionText(displayGroupDescription);
	Components.resetScrollPositionLater(descTextPane);
	if (!Lists.nullOrEmpty(order)) {
	    LinkedHashMap <String, DisplayDescription> orderedDescriptions =
		    new LinkedHashMap <String, DisplayDescription> ();
	    int i;
	    for (String title : order) {
		i = Lists.linearSearch(descriptions, title,
			DISPLAY_TITLE_MATCHER);
		if (i >= 0) {
		    orderedDescriptions.put(title, descriptions.get(i));
		}
	    }
	    // Append unmatched descriptions in order by title
	    for (DisplayDescription d : descriptions) {
		orderedDescriptions.put(d.getTitle(), d);
	    }
	    descriptions.clear();
	    for (DisplayDescription d : orderedDescriptions.values()) {
		descriptions.add(d);
	    }
	    orderedDescriptions = null;
	}
	for (DisplayDescription d : descriptions) {
	    model.addElement(getListText(d));
	}

	loadDefaultDirectoryMenuItem.setEnabled(
		!displayDirectory.equals(defaultDirectory));
    }

    private static void
    setDescriptionText(String text)
    {
	if (Strings.blank(text)) {
	    descTextPane.setText(ChimeText.NO_DESC);
	} else {
	    descTextPane.setText(text);
	    Components.resetScrollPositionLater(descTextPane);
	}
    }

    private static void
    layoutChimeComponents(JPanel p)
    {
	JScrollPane tracesScrollPane;
	JScrollPane descScrollPane;
	JLabel descLabel;
	JLabel tracesLabel;
	JLabel traceGroupLabel;
	final JButton runButton;
	tracesList = new XList();

        tracesScrollPane = new JScrollPane();
        descScrollPane = new JScrollPane();
        descTextPane = new JTextPane();
        runButton = new JButton();
	traceGroupLabel = new JLabel();
        tracesLabel = new JLabel();
        descLabel = new JLabel();

	tracesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tracesList.setModel(new DefaultListModel());
        tracesScrollPane.setViewportView(tracesList);
	tracesList.addListSelectionListener(new ListSelectionListener() {
	    public void valueChanged(final ListSelectionEvent e) {
		int i = tracesList.getSelectedIndex();
		if (i < 0) {
		    setDescriptionText(displayGroupDescription);
		    Components.resetScrollPositionLater(descTextPane);
		    runButton.setEnabled(false);
		    return;
		}

		runButton.setEnabled(true);
		DisplayDescription description = descriptions.get(i);
		String longDescription = description.getLongDescription();
		setDescriptionText(longDescription);
	    }
	});
	tracesList.addRowDoubleClickListener(new RowDoubleClickListener() {
            public void rowDoubleClicked(RowDoubleClickEvent e) {
		int i = tracesList.getSelectedIndex();
		if (i >= 0) {
		    //showDisplay(descriptions.get(i));
                    Window w = SwingUtilities.getWindowAncestor(tracesList);
		    if (w instanceof JFrame) {
			JFrame f = (JFrame)w;
			DisplayDescription d = descriptions.get(i);
			ProgramDisplay.launch(f, d.getTitle(),
				AggregationDisplay.createCommand(
				descriptions.get(i)));
		    }
		}
	    }
	});

	descTextPane.setEditable(false);
	descTextPane.setEditorKit(new HTMLEditorKit());
	descTextPane.setBorder(new EmptyBorder(2, 2, 2, 2));
	descTextPane.setBackground(Color.white);
        descScrollPane.setViewportView(descTextPane);
	ClipboardMenuItemSupplier.addPopupMenu(descTextPane);

        runButton.setText(ChimeText.RUN);
	runButton.setEnabled(false);
	runButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		int i = tracesList.getSelectedIndex();
		showDisplay(descriptions.get(i));
	    }
	});

        tracesLabel.setText(ChimeText.TRACES);
	tracesLabel.setLabelFor(tracesList);
	traceGroupLabel.setText(ChimeText.TRACE_GROUP);
	traceGroupLabel.setLabelFor(traceGroupCombo);

        descLabel.setText(Text.DESCRIPTION);

        org.jdesktop.layout.GroupLayout layout = new
		org.jdesktop.layout.GroupLayout(p);
        p.setLayout(layout);
        layout.setHorizontalGroup(layout.createSequentialGroup()
	    .add(28, 28, 28)
	    .add(layout.createParallelGroup(
		    org.jdesktop.layout.GroupLayout.LEADING)
		.add(layout.createSequentialGroup()
		    .add(traceGroupLabel)
		    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		    .add(traceGroupCombo))
		.add(tracesLabel)
		.add(tracesScrollPane,
			org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 164,
			Short.MAX_VALUE)
		.add(runButton))
	    .add(24, 24, 24)
	    .add(layout.createParallelGroup(
		    org.jdesktop.layout.GroupLayout.LEADING)
		.add(descLabel)
		.add(descScrollPane,
			org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
			249, Short.MAX_VALUE))
	    .add(29, 29, 29));
        layout.setVerticalGroup(layout.createSequentialGroup()
	    .add(25, 25, 25)
	    .add(layout.createParallelGroup(
		    org.jdesktop.layout.GroupLayout.BASELINE)
		.add(traceGroupLabel)
		.add(traceGroupCombo)
		.add(descLabel))
	    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
	    .add(layout.createParallelGroup(
		    org.jdesktop.layout.GroupLayout.BASELINE)
		.add(layout.createSequentialGroup()
		    .add(tracesLabel)
		    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		    .add(tracesScrollPane,
			    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 192,
			    Short.MAX_VALUE)
		    .add(17, 17, 17)
		    .add(runButton))
		.add(descScrollPane,
			org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
			234, Short.MAX_VALUE))
	    .add(30, 30, 30));

	char[] reserved = new char[] {
	    ChimeText.FILE_MN,
	    ChimeText.OPTIONS_MN,
	    ChimeText.HELP_MN,
	    ChimeText.TRACE_GROUP_MN
	};
	traceGroupLabel.setDisplayedMnemonic(ChimeText.TRACE_GROUP_MN);
	Components.generateMnemonics(p, reserved, true,
		runButton, tracesLabel);
    }

    static void
    showDisplay(final DisplayDescription description)
    {
	showDisplay(description, null, null);
    }

    static void
    showDisplay(final DisplayDescription description, final Point location)
    {
	showDisplay(description, location, null);
    }

    static void
    showDisplay(final DisplayDescription description, final Point location,
	    final NotifyingReference <JFrame> ref)
    {
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		launchDisplay(description, location, ref);
	    }
	});
    }

    static boolean
    containsDisplayDescription(File directory)
    {
	DisplayDescription d = null;
	for (String s : directory.list()) {
	    if (s.equals("SCCS")) {
		continue;
	    }

	    if (s.endsWith("xml") && !s.equals(DIRFILE)) {
		File descriptionFile = new File(directory, s);
		d = AggregationDisplay.getDescriptionFromFile(descriptionFile);
		if (d != null) {
		    return true;
		}
	    }
	}
	return false;
    }

    static DirectoryDescription
    getDirectoryDescriptionFromFile(File file)
    {
	DirectoryDescription d = null;
	if (file.canRead()) {
	    XMLDecoder decoder = Persistence.getXMLDecoder(file);
	    if (decoder != null) {
		Object object = decoder.readObject();
		decoder.close();
		if (object instanceof DirectoryDescription) {
		    d = (DirectoryDescription)object;
		    if (logger.isLoggable(Level.INFO)) {
			logger.info(d.toString());
		    }
		}
	    }
	}
	return d;
    }

    static JMenuItem
    createLoadMenu(final File directory)
    {
	JMenuItem loadMenuItem = null;
	String name = null;
	Character mnemonic = null;
	ImageIcon icon = null;
	DirectoryDescription dirdesc = null;
	java.util.List <File> subdirs = null;
	File file;

	for (String s : directory.list()) {
	    if (s.equals("SCCS")) {
		continue;
	    }

	    file = new File(directory, s);
	    if (file.isDirectory()) {
		// subdirectory
		if (file.getName().equals(NEW_DISPLAYS_DIRNAME) ||
			containsDisplayDescription(file)) {
		    // subdirectory contains at least one display
		    // description; but include the special directory
		    // for new displays even if it is empty
		    if (subdirs == null) {
			subdirs = new ArrayList <File> ();
		    }
		    subdirs.add(file);
		}
	    } else if (s.equals(DIRFILE)) {
		// directory description
		dirdesc = getDirectoryDescriptionFromFile(file);
		if (dirdesc != null) {
		    name = dirdesc.getName();
		    mnemonic = dirdesc.getMnemonic();
		    if (directory.getName().equals(NEW_DISPLAYS_DIRNAME)) {
			newDisplaysName = name;
			icon = Images.get(Images.Standard.NEW);
		    }
		}
	    }
	}

	if (directory.equals(defaultDirectory)) {
	    // initial displays
	    defaultDisplaysName = (name == null ?
		    ChimeText.DEFAULT_DISPLAYS : name);
	    displayGroups.put(defaultDisplaysName, directory);

	    if (subdirs == null) {
		// no subdirectories: make "Load" a leaf item instead of
		// a submenu and give it the "Browse..." action
		loadMenuItem = new JMenuItem(ChimeText.LOAD_DIRECTORY);
		browseDisplaysMenuItem = loadMenuItem;
	    } else {
		loadMenuItem = new JMenu(ChimeText.LOAD);
		for (File subdir : subdirs) {
		    loadMenuItem.add(createLoadMenu(subdir));
		}
		browseDisplaysMenuItem = new JMenuItem(
			ChimeText.BROWSE_DIRECTORIES);
		browseDisplaysMenuItem.setMnemonic(
			ChimeText.BROWSE_DIRECTORIES_MN);
		((JMenu)loadMenuItem).addSeparator();
		loadMenuItem.add(browseDisplaysMenuItem);
	    }
	    loadMenuItem.setMnemonic(ChimeText.LOAD_MN);
	} else {
	    // subdirectory
	    String menuItemName = (name == null ? directory.getName() : name);
	    displayGroups.put(menuItemName, directory);

	    if (subdirs == null) {
		loadMenuItem = new JMenuItem(menuItemName);
		if (mnemonic != null) {
		    loadMenuItem.setMnemonic(mnemonic);
		}
		loadMenuItem.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			loadDisplayList(directory);
		    }
		});
	    } else {
		loadMenuItem = new JMenu(menuItemName);
		if (mnemonic != null) {
		    loadMenuItem.setMnemonic(mnemonic);
		}
		JMenuItem displaysMenuItem =
			new JMenuItem(ChimeText.TOP_LEVEL_DISPLAYS);
		displaysMenuItem.setMnemonic(ChimeText.TOP_LEVEL_DISPLAYS_MN);
		displaysMenuItem.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			loadDisplayList(directory);
		    }
		});
		loadMenuItem.add(displaysMenuItem);
		((JMenu)loadMenuItem).addSeparator();
		for (File subdir : subdirs) {
		    loadMenuItem.add(createLoadMenu(subdir));
		}
	    }
	}

	if (icon != null) {
	    loadMenuItem.setIcon(icon);
	}

	return loadMenuItem;
    }

    private static void
    savePreferences()
    {
	try {
	    File preferencesFile = Persistence.getFile(
		    "preferences.xml");
	    XMLEncoder encoder = Persistence.getXMLEncoder(
		    preferencesFile);
	    if (encoder != null) {
		encoder.writeObject(preferences);
		encoder.close();
	    }
	} catch (Exception x) {
	    x.printStackTrace();
	}
    }

    static String
    getMatchingLookAndFeelClassName(String s)
    {
	if (s == null) {
	    return null;
	}

	s = s.toLowerCase();
	String laf;
	for (UIManager.LookAndFeelInfo lafInfo :
		UIManager.getInstalledLookAndFeels()) {
	    laf = lafInfo.getName();
	    laf = laf.toLowerCase();
	    if (laf.indexOf(s) >= 0) {
		return lafInfo.getClassName();
	    }
	}
	return null;
    }

    /*
    static void
    setLookAndFeel()
    {
	assert (SwingUtilities.isEventDispatchThread());

	String laf = null;
	if (lafOption != null) {
	    laf = getMatchingLookAndFeelClassName(lafOption);
	} else {
	    laf = preferences.getLookAndFeelClassName();
	}

	if (laf == null) {
	    // partial work-around for bug 6507452: set initial L&F to metal
	    //
	    // laf = UIManager.getSystemLookAndFeelClassName();
	    laf = UIManager.getCrossPlatformLookAndFeelClassName();
	}

	try {
	    UIManager.setLookAndFeel(laf);
	} catch (Exception e) {
	    try {
		UIManager.setLookAndFeel(
			UIManager.getSystemLookAndFeelClassName());
	    } catch (Exception eUI) {
		eUI.printStackTrace();
	    }
	}

	lookAndFeelSet = true;
    }
     */

    static void
    launchNewDisplayWizard()
    {
	launchNewDisplayWizard(null, null);
    }

    static void
    launchNewDisplayWizard(File file, DisplayDescription display)
    {
	assert (SwingUtilities.isEventDispatchThread());
        /*
	if (!lookAndFeelSet) {
	    setLookAndFeel();
	}
         */
	JFrame f = NewDisplayWizard.launch(file, display);
	f.addWindowListener(new WindowAdapter() {
	    public void windowClosing(WindowEvent e) {
		Thread t = new Thread(new Runnable() {
		    public void run() {
			savePreferences();
		    }
		}, "Close New Display Wizard");
		t.start();
	    }
	});
        /*
	if (StatLauncher.frame == null) {
	    StatLauncher.frame = f;
	}
         */
    }

    static void
    launchListProbes()
    {
	assert (SwingUtilities.isEventDispatchThread());
	if (hostname == null) {
	    ListProbes.launch();
	} else {
	    try {
		ListProbes.launchClient(hostname, port);
	    } catch (Exception x) {
		x.printStackTrace();
	    }
	}
    }

    static void
    launchDisplay(DisplayDescription display, Point location,
	    NotifyingReference <JFrame> ref)
    {
	assert (SwingUtilities.isEventDispatchThread());
        /*
	if (!lookAndFeelSet) {
	    setLookAndFeel();
	}
         */
	if (hostname == null) {
	    AggregationDisplay.launch(display, location, ref);
	} else {
	    try {
		AggregationDisplay.launchClient(display, location,
			hostname, port, ref);
	    } catch (Exception x) {
		x.printStackTrace();
	    }
	}
    }

    static void
    launchPlayback(String fileName)
    {
	assert (SwingUtilities.isEventDispatchThread());
	/* setLookAndFeel(); */
	AggregationDisplay.launchPlayback(fileName);
    }

    static void
    updateTraceGroupComboUI()
    {
	traceGroupCombo.getTextField().setValidatesOnFocusLost(true);
	traceGroupCombo.getTextField().addTextValidationListener(
		new TextValidationListener() {
	    public void textValid(TextValidationEvent e) {
		XTextField historyField = traceGroupCombo.getTextField();
		historyField.setForeground(Color.black);
		String name = (String)traceGroupCombo.getSelectedItem();
		if (!Strings.equals(name, previousDisplayGroupName)) {
		    File displayGroup = displayGroups.get(name);
		    if (displayGroup != null) {
			loadDisplayList(displayGroup);
		    }
		}
		previousDisplayGroupName = name;
	    }
	    public void textInvalid(TextValidationEvent e) {
		traceGroupCombo.getTextField().setForeground(Color.red);
	    }
	});
	traceGroupCombo.getTextField().addTextChangeListener(
		new TextChangeListener() {
	    public void textChanged(TextChangeEvent e) {
		traceGroupCombo.getTextField().setForeground(Color.black);
	    }
	});
    }

    static MacroArgument[]
    generateMacroArguments(String macroArgs, String macroArgLabels,
	    Reference <String[][]> groups)
    {
	String[] labels = Strings.EMPTY_ARRAY;
	String[] values = Strings.EMPTY_ARRAY;
	if (macroArgs != null) {
	    values = macroArgs.split(",", -1); // include trailing empty string
	    for (int i = 0; i < values.length; ++i) {
		values[i] = values[i].trim();
	    }
	}
	if (macroArgLabels != null) {
	    labels = ChimeUtilities.parseGroups(macroArgLabels, groups);
	    labels = A.remove(labels, ProgramMacroVariables.VAR_TARGET);
	}

	int n = Math.max(values.length, labels.length);
	MacroArgument[] macroArguments = new MacroArgument[n];
	String label;
	String value;
	for (int i = 0; i < n; ++i) {
	    label = (i < labels.length ? labels[i] : null);
	    value = (i < values.length ? values[i] : null);
	    macroArguments[i] = new MacroArgument(label, value);
	}

	return macroArguments;
    }

    static boolean
    checkWriteFile(File file)
    {
	if (file.exists() && !file.canWrite()) {
	    System.err.println(
		    FText.format(ChimeFText.CANNOT_OVERWRITE_MSG,
		    file.getPath()));
	    return false;
	} else if (!file.exists() &&
		(file.getParentFile() != null) &&
		!file.getParentFile().canWrite()) {
	    System.err.println(
		    FText.format(ChimeFText.CANNOT_WRITE_MSG,
		    file.getPath()));
	    return false;
	}

	if (file.exists()) {
	    int response = 0;
	    while ((response != (int)'y') && (response != (int)'n')) {
		System.out.print(FText.format(
			ChimeFText.OVERWRITE_FILE_PROMPT,
			file.getPath()) + " ");
		try {
		    response = System.in.read();
		    if (response != (int)'\n') {
			while (System.in.read() != (int)'\n');
		    }
		} catch (IOException e) {
		    System.err.println(e.getMessage());
		    return false;
		}
		if (Character.isLetter(response)) {
		    response = (int)Character.toLowerCase((char)response);
		}
	    }
	    if (response != (int)'y') {
		return false;
	    }
	}

	return true;
    }

    public static File
    getFile(File displayDir, String fileName)
    {
	File file = new File(fileName);
	if (!file.isAbsolute()) {
	    file = new File(displayDir, fileName);
	    if (!file.exists()) {
		file = new File(StatLauncher.DISPLAY_DIR, fileName);
	    }
	    if (!file.exists()) {
		File parentDir = displayDir.getParentFile();
		while ((parentDir != null) &&
			!parentDir.equals(StatLauncher.DISPLAY_DIR) &&
			!file.exists()) {
		    file = new File(parentDir, fileName);
		    parentDir = parentDir.getParentFile();
		}
	    }
	    if (!file.exists()) {
		file = new File(StatLauncher.displayDirectory, fileName);
	    }
	    if (!file.exists()) {
		file = new File(StatLauncher.CURRENT_DIR, fileName);
	    }
	}
	return file;
    }

    static boolean
    writeGeneratedDisplay(DisplayDescription d)
    {
	String title = d.getTitle();
	String filename = title.toLowerCase();
	filename = filename.replaceAll("[\\s\\W]+", "_");
	filename += ".xml";

	File newDisplaysDir = NewDisplayWizard.getDisplayDirectory();
	File targetFile = new File(newDisplaysDir, filename);

	if (!checkWriteFile(targetFile)) {
	    return false;
	}

	String programFileName = d.getProgramFileName();
	if (programFileName != null) {
	    File programFile = new File(programFileName);
	    File copiedProgramFile = new File(newDisplaysDir,
		    programFile.getName());
	    if (!checkWriteFile(copiedProgramFile)) {
		return false;
	    }
	    PrintWriter writer;
	    try {
		writer = new PrintWriter(copiedProgramFile);
	    } catch (Exception e) {
		System.err.println(e.getMessage());
		return false;
	    }
	    writer.print(Command.getProgramString(programFile));
	    writer.close();
	    System.out.println(FText.format(
		    ChimeFText.WROTE_FILE_MSG, copiedProgramFile));
	    d.setProgramFileName(programFile.getName());
	}

	XMLEncoder encoder = Persistence.getXMLEncoder(targetFile);
	if (encoder == null) {
	    System.err.println(FText.format(ChimeFText.FAILED_WRITE_MSG,
		    targetFile.getPath()));
	    return false;
	}
	encoder.writeObject(d);
	encoder.close();
	String file;
	try {
	    file = targetFile.getCanonicalPath();
	} catch (IOException x) {
	    file = targetFile.getPath();
	}
	System.out.println(FText.format(
		ChimeFText.WROTE_FILE_MSG, file));
        /*
	System.out.println(FText.format(
		ChimeFText.RUN_NEW_DISPLAY_MSG,
		StatLauncher.getExecutablePathName(), file));
         */
	return true;
    }

    static void
    addTotalRow(DisplayDescription d)
    {
	ColumnDescription cd;
	TotallerDescription td;
	UniqueKey key = AggregationDisplay.getUniqueKey(d);
	int n = key.getFieldCount();
	ColumnDescription[] columns = d.getColumns();
	int len = columns.length;
	for (int i = 0; i < len; ++i) {
	    cd = columns[i];
	    td = new TotallerDescription();
	    if (key.isFieldIndex(i)) {
		if (i == key.getFieldIndex(n - 1)) {
		    td.setTotalType(TotalType.COUNT.name());
		} else {
		    td.setTotalType(TotalType.UNIQUE_COUNT.name());
		}
		td.setDisplayFormat("{0, number, integer} {0, choice, " +
			"0#keys|1#key|2#keys}");
	    } else {
		td.setTotalType(TotalType.SUM.name());
		td.setDisplayFormat(cd.getDisplayFormat());
	    }
	    cd.setTotallerDescription(td);
	}
    }

    static void
    addSparkline(DisplayDescription d)
    {
	ColumnDescription firstPlottableColumn = null;
	ColumnDescription[] columns = d.getColumns();
	UniqueKey uniqueKey = AggregationDisplay.getUniqueKey(d);
	UniqueKey displayKey = AggregationDisplay.getDisplayKey(d, uniqueKey);
	int plottableColumns = 0;
	for (ColumnDescription cd : columns) {
	    if (cd.isPlottable()) {
		if (plottableColumns == 0) {
		    firstPlottableColumn = cd;
		}
		++plottableColumns;
	    }
	}
	if (firstPlottableColumn == null) {
	    return;
	}
	ColumnDescription sparklineColumn = new ColumnDescription();
	String name = (plottableColumns == 1
		? ChimeText.DEFAULT_SPARKLINE_COLUMN_NAME
		: FText.format(FText.SPACED_PAIR,
			firstPlottableColumn.getName(),
			ChimeText.DEFAULT_SPARKLINE_COLUMN_NAME));
	sparklineColumn.setName(name);
	sparklineColumn.setSourceColumnName(firstPlottableColumn.getName());
	sparklineColumn.setRenderingType(RenderingType.SPARKLINE.name());
	sparklineColumn.setPlottable(true);
	sparklineColumn.setPlottableName(firstPlottableColumn.getName());
	firstPlottableColumn.setPlottable(false);
	sparklineColumn.setAggregationName(
		firstPlottableColumn.getAggregationName());
	firstPlottableColumn.setAlignmentHint(
		ColumnPropertiesPanel.LEFT_ALIGN);
	if (displayKey.getFieldCount() == 1) {
	    ColumnDescription keyColumn = columns[displayKey.getFieldIndex(0)];
	    keyColumn.setAlignmentHint(ColumnPropertiesPanel.RIGHT_ALIGN);
	}
	java.util.List <ColumnDescription> list =
		new ArrayList <ColumnDescription> (columns.length + 1);
	for (ColumnDescription cd : columns) {
	    if (cd == firstPlottableColumn) {
		list.add(sparklineColumn);
	    }
	    list.add(cd);
	}
	d.setColumns(list.toArray(new ColumnDescription[list.size()]));
    }

    static void
    groupKeyColumns(DisplayDescription d)
    {
	ColumnDescription cd;
	java.util.List <ColumnDescription> list =
	    new ArrayList <ColumnDescription> ();
	UniqueKey key = AggregationDisplay.getUniqueKey(d);
	int n = key.getFieldCount();
	ColumnDescription[] columns = d.getColumns();
	ColumnDescription groupColumn;
	String name;
	int len = columns.length;
	for (int i = 0; i < len; ++i) {
	    cd = columns[i];
	    list.add(cd);
	    /* Exclude the final key column, since it cannot repeat. */
	    if (key.isFieldIndex(i) && i != key.getFieldIndex(n - 1)) {
		name = cd.getName();
		cd.setName(name + "Hidden");
		cd.setHidden(true);
		groupColumn = new ColumnDescription();
		groupColumn.setSourceColumnName(name + "Hidden");
		groupColumn.setName(name);
		groupColumn.setValueType(ValueType.GROUP.name());
		groupColumn.setTotallerDescription(cd.getTotallerDescription());
		list.add(groupColumn);
	    }
	}
	d.setColumns(list.toArray(new ColumnDescription[list.size()]));
    }

    static void
    setAccumulatedValues(DisplayDescription d)
    {
	d.setClearedAggregations(Strings.EMPTY_ARRAY);
    }

    static void
    generateDisplay(DisplayDescription display,
	    String displayTitle, String columnHeaders,
	    boolean nonRepeatingKeys, boolean writeGeneratedDisplay)
    {
	display.setTitle(displayTitle == null ? ChimeText.DISPLAY :
		displayTitle);
	if (columnHeaders != null) {
	    ColumnDescription[] columns = display.getColumns();
	    ColumnDescription column;
	    int len = columns.length;
	    // include trailing empty string
	    String[] headers = columnHeaders.split(",", -1);
	    int h = 0;
	    for (int i = 0; i < len && h < headers.length; ++i) {
		column = columns[i];
		if (column.isHidden()) {
		    continue;
		}

		column.setName(headers[h++].trim());
	    }
	}

	if (nonRepeatingKeys) {
	    groupKeyColumns(display);
	}

	if (writeGeneratedDisplay) {
	    if (writeGeneratedDisplay(display)) {
		System.exit(0);
	    } else {
		System.err.println(ChimeText.DISPLAY_NOT_GENERATED_MSG);
		System.exit(1);
	    }
	}

	try {
	    AggregationDisplay.prepareDescription(display);
	} catch (Exception x) {
	    System.err.println(FText.format(ChimeFText.CANNOT_RUN_MSG,
		    Programs.getProgramLabel(display)));
	    System.err.println(x.toString());
	    System.exit(1);
	}

	display.setChimeContext(true);
	Point location = preferences.getGeneratedDisplayLocation();
	final ComponentListener moveListener =
		new ComponentAdapter() {
	    public void componentMoved(ComponentEvent e) {
		Component source = (Component)e.getSource();
		Point location = source.getLocation();
		StatLauncher.preferences.setGeneratedDisplayLocation(
			location);
	    }
	};
	final WindowListener displayListener =
		new WindowAdapter() {
	    public void windowClosing(WindowEvent e) {
		Thread t = new Thread(new Runnable() {
		    public void run() {
			savePreferences();
		    }
		}, "Close Generated Display");
		t.start();
	    }
	};
	NotifyingReference <JFrame> ref =
		new NotifyingReference <JFrame> ();
	ref.addListener(new NotifyingReference.Listener <JFrame> () {
	    public void
	    valueChanged(NotifyingReference.Event <JFrame> e)
	    {
		if (e.getOldValue() == null &&
			e.getNewValue() != null) {
		    JFrame f = e.getNewValue();
		    f.addComponentListener(moveListener);
		    f.addWindowListener(displayListener);
		}
	    }
	});

	showDisplay(display, location, ref);
    }

    static void
    generateDisplay(DisplayDescription display, String programString,
	    String displayTitle, String columnHeaders,
	    boolean nonRepeatingKeys, boolean writeGeneratedDisplay)
    {
	if (display == null) {
	    System.err.println(FText.format(ChimeFText.CANNOT_RUN_MSG,
		    programString));
	    System.exit(1);
	}

	display.setProgramString(programString);
	generateDisplay(display, displayTitle, columnHeaders,
		nonRepeatingKeys, writeGeneratedDisplay);
    }

    static void
    generateDisplay(DisplayDescription display, File programFile,
	    String displayTitle, String columnHeaders,
	    boolean nonRepeatingKeys, boolean writeGeneratedDisplay)
    {
	if (display == null) {
	    System.err.println(FText.format(ChimeFText.CANNOT_RUN_MSG,
		    programFile.getPath()));
	    System.exit(1);
	}

	display.setProgramFileName(programFile.getPath());
	generateDisplay(display, displayTitle, columnHeaders,
		nonRepeatingKeys, writeGeneratedDisplay);
    }

    @SuppressWarnings("serial")
    public static void
    launch()
    {
	assert (SwingUtilities.isEventDispatchThread());
	/* setLookAndFeel(); */

	final String info = FText.format(ChimeFText.CHIME_VERSION,
		ChimeText.TITLE, ChimeText.VERSION, DATE);
	// Use PaintedPanel as work-around for gtk+ plaf, which does not
	// respect background property.
	//chimePanel = new PaintedPanel(Color.white) {
        chimePanel = new PaintedPanel(new GradientPaint(0, 0, Color.white,
 		0, 1500, Color.blue)) {
	    @Override
	    public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g.create();
		//Font font = XTextField.getDefaultFont().deriveFont(10.f);
                Font font = XTextField.getDefaultFont().deriveFont(12.f);                
		g2.setFont(font);
		FontMetrics m = getFontMetrics(font);
		Dimension size = getSize();
		int w = size.width - (m.stringWidth(info) + 2);
		int h = size.height - (m.getDescent() + 2);
		//g2.setPaint(Color.gray);
                g2.setPaint(Color.black);
		//g2.drawString(info, w, h);
	    }
	};

	final BorderLayout contentLayout = new BorderLayout();
	//final JPanel contentPane = new JPanel(contentLayout);
        contentPane = new JPanel(contentLayout);
	//frame.setContentPane(contentPane);
        JPanel chimeContentPane = new JPanel(new BorderLayout());
        
        if (!displayDirectory.exists()) {
            ImageIcon icon = new ImageIcon(StatLauncher.class.getResource("/org/netbeans/modules/dtrace/resources/stop.gif"));
            JLabel errorLabel = 
                    new JLabel("ERROR: Rename or delete the DTraceScripts directory and reinstall the DTrace GUI plugin.", 
                    icon, SwingConstants.LEFT );
            contentPane.add(errorLabel, BorderLayout.CENTER);
            return;
        }
        
	final JToolBar toolBar = new JToolBar();
	toolBar.setBackground(Color.white);
	Integer orientation = preferences.getToolBarOrientation();
	if (orientation == null) {
	    orientation = JToolBar.HORIZONTAL;
	}
	toolBar.setOrientation(orientation);
	Object constraints = preferences.getToolBarConstraints();
	if (constraints == null) {
	    constraints = BorderLayout.PAGE_START;
	}
	chimeContentPane.add(toolBar, constraints);
	chimeContentPane.addContainerListener(new ContainerAdapter() {
	    public void componentAdded(ContainerEvent e) {
		preferences.setToolBarConstraints(
			contentLayout.getConstraints(toolBar));
		preferences.setToolBarOrientation(toolBar.getOrientation());
	    }
	});
	chimeContentPane.add(chimePanel, BorderLayout.CENTER);
        contentPane.add(chimeContentPane, BorderLayout.CENTER);
        contentPane.setBackground(Color.black);

	final JFileChooser fileChooser = new JFileChooser();

	AbstractAction newDisplayAction = new AbstractAction(
		ChimeText.NEW_DISPLAY, Images.get(Images.Standard.NEW)) {
	    public void actionPerformed(ActionEvent e) {
		launchNewDisplayWizard();
	    }
	};
	newDisplayAction.putValue(Action.MNEMONIC_KEY,
		(int)ChimeText.NEW_DISPLAY_MN);
	newDisplayAction.putValue(Action.ACCELERATOR_KEY,
		KeyStroke.getKeyStroke("control N"));
	newDisplayAction.putValue(Action.SHORT_DESCRIPTION,
		ChimeText.NEW_DISPLAY_TOOLTIP);

	AbstractAction aboutChimeAction = new AbstractAction(
		ChimeText.ABOUT_CHIME, Images.get(Images.Standard.ABOUT)) {
	    public void actionPerformed(ActionEvent e) {
		AboutChimeDisplay.launch(SwingUtilities.windowForComponent(getContentPane()), ChimeText.ABOUT_CHIME);
	    }
	};
	aboutChimeAction.putValue(Action.MNEMONIC_KEY,
		(int)ChimeText.ABOUT_CHIME_MN);

	AbstractAction listProbesAction = new AbstractAction(
		ChimeText.LIST_PROBES, Images.get(Images.Standard.SEARCH)) {
	    public void actionPerformed(ActionEvent e) {
		launchListProbes();
	    }
	};
	listProbesAction.putValue(Action.MNEMONIC_KEY,
		(int)ChimeText.LIST_PROBES_MN);
	listProbesAction.putValue(Action.SHORT_DESCRIPTION,
		ChimeText.LIST_PROBES_TOOLTIP);

	AbstractAction reloadAction = new AbstractAction(
		ChimeText.RELOAD, Images.get(Images.Standard.REFRESH)) {
	    public void actionPerformed(ActionEvent e) {
		reload();
	    }
	};
	reloadAction.putValue(Action.MNEMONIC_KEY,
		(int)ChimeText.RELOAD_MN);
	reloadAction.putValue(Action.SHORT_DESCRIPTION,
		ChimeText.RELOAD_TOOLTIP);

	AbstractAction loadDefaultDirectoryAction = new AbstractAction(
		ChimeText.LOAD_DEFAULT, ChimeImages.CLEAR_BLUE_CIRCLE) {
	    public void actionPerformed(ActionEvent e) {
		loadDisplayList(defaultDirectory);
	    }
	};
	loadDefaultDirectoryAction.putValue(Action.MNEMONIC_KEY,
		(int)ChimeText.LOAD_DEFAULT_MN);

	JMenuBar menuBar = new JMenuBar();
	JMenu fileMenu = new JMenu(ChimeText.FILE);
	JMenu optionsMenu = new JMenu(ChimeText.OPTIONS);
	JMenu helpMenu = new JMenu(ChimeText.HELP);
	JMenu recordingMenu = new JMenu(ChimeText.RECORDING);
	JMenu lookAndFeelMenu = new JMenu(ChimeText.LOOK_AND_FEEL);
	JMenuItem newDisplayMenuItem = new JMenuItem(newDisplayAction);
	JMenuItem listProbesMenuItem = new JMenuItem(listProbesAction);
	loadDefaultDirectoryMenuItem = new JMenuItem(
		loadDefaultDirectoryAction);
	JMenuItem loadDirectoryMenuItem = createLoadMenu(defaultDirectory);
	JMenuItem reloadMenuItem = new JMenuItem(reloadAction);
	JMenuItem playbackMenuItem = new JMenuItem(ChimeText.PLAYBACK);
	JMenuItem closeMenuItem = new JMenuItem(ChimeText.CLOSE_CHIME);
	JRadioButtonMenuItem noRecordingItem = new
		JRadioButtonMenuItem(ChimeText.RECORDING_OFF);
	JRadioButtonMenuItem serializationRecordingItem =
		new JRadioButtonMenuItem(ChimeText.RECORDING_SERIALIZATION);
	JRadioButtonMenuItem xmlRecordingItem =
		new JRadioButtonMenuItem(ChimeText.RECORDING_XML);
	final JCheckBoxMenuItem consoleItem = new
		JCheckBoxMenuItem(ChimeText.CONSOLE);
	playbackMenuItem.setMnemonic(ChimeText.PLAYBACK_MN);
	closeMenuItem.setMnemonic(ChimeText.CLOSE_CHIME_MN);
	closeMenuItem.setAccelerator(KeyStroke.getKeyStroke(
		ChimeText.CLOSE_CHIME_ACCELERATOR));
	fileMenu.setMnemonic(ChimeText.FILE_MN);
	optionsMenu.setMnemonic(ChimeText.OPTIONS_MN);
	helpMenu.setMnemonic(ChimeText.HELP_MN);
	recordingMenu.setMnemonic(ChimeText.RECORDING_MN);
	lookAndFeelMenu.setMnemonic(ChimeText.LOOK_AND_FEEL_MN);
	consoleItem.setMnemonic(ChimeText.CONSOLE_MN);
	noRecordingItem.setMnemonic(ChimeText.RECORDING_OFF_MN);
	serializationRecordingItem.setMnemonic(
		ChimeText.RECORDING_SERIALIZATION_MN);
	xmlRecordingItem.setMnemonic(ChimeText.RECORDING_XML_MN);
	noRecordingItem.setActionCommand(RECORDING_OFF_ACTION);
	serializationRecordingItem.setActionCommand(
		RECORDING_SERIALIZATION_ACTION);
	xmlRecordingItem.setActionCommand(RECORDING_XML_ACTION);
	recordingMenu.add(noRecordingItem);
	recordingMenu.add(serializationRecordingItem);
	recordingMenu.add(xmlRecordingItem);
	fileMenu.add(newDisplayMenuItem);
	fileMenu.add(listProbesMenuItem);
	fileMenu.add(loadDefaultDirectoryMenuItem);
	fileMenu.add(loadDirectoryMenuItem);
	fileMenu.add(reloadMenuItem);
	fileMenu.add(playbackMenuItem);
	fileMenu.addSeparator();
	/* fileMenu.add(closeMenuItem); */
	optionsMenu.add(recordingMenu);
	/* optionsMenu.add(lookAndFeelMenu); */
	optionsMenu.add(consoleItem);
	menuBar.add(fileMenu);
	menuBar.add(optionsMenu);
	menuBar.add(helpMenu);
	final ButtonGroup recordingGroup = new ButtonGroup();
	recordingGroup.add(noRecordingItem);
	recordingGroup.add(serializationRecordingItem);
	recordingGroup.add(xmlRecordingItem);
	toolBar.add(newDisplayAction);
	toolBar.add(listProbesAction);
	toolBar.add(reloadAction);

	ActionListener recordingItemListener = new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		ButtonModel selectedButtonModel =
			recordingGroup.getSelection();
		String actionCommand = selectedButtonModel.getActionCommand();
		xmlMode = actionCommand.equals(RECORDING_XML_ACTION);
                org.opensolaris.chime.StatLauncher.xmlMode = xmlMode;
		recordingMode = !actionCommand.equals(RECORDING_OFF_ACTION);
                org.opensolaris.chime.StatLauncher.recordingMode = recordingMode;
	    }
	};
	noRecordingItem.addActionListener(recordingItemListener);
	serializationRecordingItem.addActionListener(recordingItemListener);
	xmlRecordingItem.addActionListener(recordingItemListener);
	recordingGroup.setSelected(noRecordingItem.getModel(), true);

        /*
	final ButtonGroup lookAndFeelGroup = new ButtonGroup();
	ActionListener lookAndFeelItemListener = new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		ButtonModel lookAndFeelButtonModel =
			lookAndFeelGroup.getSelection();
		String actionCommand =
			lookAndFeelButtonModel.getActionCommand();
		try {
		    UIManager.setLookAndFeel(actionCommand);
		    preferences.setLookAndFeelClassName(actionCommand);
		    SwingUtilities.updateComponentTreeUI(frame);
		    updateTraceGroupComboUI();
		    if (preferences.getSize() == null) {
			packFrame();
		    } else {
			frame.setSize(preferences.getSize());
		    }
		} catch (Exception eUI) {
		    eUI.printStackTrace();
		}
	    }
	};
	JMenuItem lafItem;
	JMenuItem initialLAFItem = null;
	LookAndFeel laf = UIManager.getLookAndFeel();
	String initialLAFCommand = laf.getClass().getName();
	String lafCommand;
	for (UIManager.LookAndFeelInfo lafInfo :
		UIManager.getInstalledLookAndFeels()) {
	    lafItem = new JRadioButtonMenuItem(lafInfo.getName());
	    lafCommand = lafInfo.getClassName();
	    if (lafCommand.equals(initialLAFCommand)) {
		initialLAFItem = lafItem;
	    }
	    lafItem.setActionCommand(lafCommand);
	    lafItem.addActionListener(lookAndFeelItemListener);
	    lookAndFeelMenu.add(lafItem);
	    lookAndFeelGroup.add(lafItem);
	}
	if (initialLAFItem != null) {
	    lookAndFeelGroup.setSelected(initialLAFItem.getModel(), true);
	}
         */

	ActionListener consoleItemListener = new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		consoleOption = consoleItem.isSelected();
                org.opensolaris.chime.StatLauncher.consoleOption = consoleOption;
	    }
	};
	consoleItem.addActionListener(consoleItemListener);

	menuBar.setBackground(Color.white);
	contentPane.add(menuBar, BorderLayout.NORTH);

	browseDisplaysMenuItem.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		fileChooser.resetChoosableFileFilters();
		fileChooser.setFileSelectionMode(
			JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setCurrentDirectory(displayDirectory);
		int rc = fileChooser.showOpenDialog(SwingUtilities.windowForComponent(getContentPane()));
		if (rc == JFileChooser.APPROVE_OPTION) {
		    String fname = fileChooser.getSelectedFile().getPath();
		    File dir = fileChooser.getSelectedFile();
		    if (!dir.isDirectory()) {
			System.err.println(FText.format(
				ChimeStartupText.NOT_DIRECTORY_MSG,
				dir.getPath()));
			return;
		    } else if (!dir.canRead()) {
			System.err.println(FText.format(
				ChimeStartupText.CANNOT_READ_MSG,
				dir.getPath()));
			return;
		    }
		    displayDirectory = dir;
		    loadDisplayList(dir);
		}
	    }
	});

	playbackMenuItem.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		fileChooser.resetChoosableFileFilters();
		fileChooser.setFileFilter(
			new javax.swing.filechooser.FileFilter() {
		    public boolean
		    accept(File f)
		    {
			String name = f.getName();
			if (name.equals(DIRFILE)) {
			    return false;
			}
			return (name.endsWith(".xml") ||
				name.endsWith(".obj") ||
				f.isDirectory());
		    }

		    public String
		    getDescription()
		    {
			return ChimeText.PLAYBACK_FILE_FILTER_DESC;
		    }
		});
		fileChooser.setFileSelectionMode(
			JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setCurrentDirectory(recordingDirectory);
		int rc = fileChooser.showOpenDialog(SwingUtilities.windowForComponent(getContentPane()));
		if (rc == JFileChooser.APPROVE_OPTION) {
		    String fname = fileChooser.getSelectedFile().getPath();
		    recordingDirectory = fileChooser.getCurrentDirectory();
                    org.opensolaris.chime.StatLauncher.recordingDirectory = recordingDirectory;
		    AggregationDisplay.launchPlayback(fname);
		}
	    }
	});

        /*
	closeMenuItem.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		frame.dispatchEvent(new WindowEvent(frame,
			WindowEvent.WINDOW_CLOSING));
	    }
	});
         */

	JMenuItem tipsMenuItem = new JMenuItem(ChimeText.TIPS);
	JMenuItem aboutChimeMenuItem = new JMenuItem(aboutChimeAction);
	tipsMenuItem.setMnemonic(ChimeText.TIPS_MN);
	helpMenu.add(tipsMenuItem);
	helpMenu.addSeparator();
	helpMenu.add(aboutChimeMenuItem);

	tipsMenuItem.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		TipsDisplay.launch(SwingUtilities.windowForComponent(getContentPane()), ChimeText.TIPS);
	    }
	});

	traceGroupCombo = new XComboBox();
	traceGroupCombo.setEditable(true);
	traceGroupCombo.setSorted(false);
	traceGroupCombo.setMatchingEnabled(true);
	traceGroupCombo.setIndependentPopupWidth(true);
	traceGroupCombo.setRequired(true);
	traceGroupCombo.setValidatesInList(true);
	traceGroupCombo.setValidatesOnPopupClosed(true);
	// resets text field listeners when L&F changes
	updateTraceGroupComboUI();

	layoutChimeComponents(chimePanel);
	// must call after creating the load menu tree and history
	// pulldown
	initHistory();
	loadDisplayList(displayDirectory);

	/*
        if (preferences.getSize() == null) {
	    packFrame();
	} else {
	    frame.setSize(preferences.getSize());
	}
	if (preferences.getLocation() == null) {
	    Application.centerWindow(frame);
	} else {
	    frame.setLocation(preferences.getLocation());
	}
	frame.setVisible(true);
         */
    }

    static File
    getDisplayFile(String displayFileName)
    {
	File displayFile = new File(displayFileName);
	if (!displayFile.canRead()) {
	    displayFile = new File(displayDirectory, displayFileName);
	    if (!displayFile.canRead()) {
		System.err.println(FText.format(
			ChimeStartupText.CANNOT_READ_MSG, displayFileName));
		return null;
	    }
	}
	if (!displayFile.isFile()) {
	    System.err.println(FText.format(FText.NOT_FILE_MSG,
		    displayFileName));
	    return null;
	}
	return displayFile;
    }

    public static void
    main(String[] args)
    {
	ChimeStartupText.init();
	Logging.init();

	String displayFileName = null;
	String playbackFileName = null;
	StringBuilder programBuffer = null;
	String programString = null;
	String programFileName = null;
	File programFile = null;
	String targetCommand = null;
	Integer targetPID = null;
	String displayTitle = null;
	String columnHeaders = null;
	boolean newDisplayWizard = false;
	boolean logLocalConsumer = false;
	boolean writeGeneratedDisplay = false;
	boolean nonRepeatingKeys = false;
	boolean demarcatedKeys = false;
	boolean totalRow = false;
	boolean sparkline = false;
	boolean accumulatedValues = false;
	String[] pair;
	java.util.List <Option> options = new ArrayList <Option> ();
	String macroArgs = null;
	String macroArgLabels = null;

	Getopt g = new Getopt(CLASSNAME, args, OPTSTR);
	int c = 0;

	while ((c = g.getopt()) != -1) {
	    switch (c) {
		case 'a':
		    accumulatedValues = true;
		    break;
		case 'c':
		    if (targetPID != null) {
			usage();
		    }
		    targetCommand = g.getOptarg();
		    break;
		case 'C':
		    if (displayFileName != null) {
			usage();
		    }
		    displayFileName = g.getOptarg();
		    break;
		case 'd':
		    String displayDescriptionPath = g.getOptarg();
		    defaultDirectory = new File(displayDescriptionPath);
		    displayDirectory = defaultDirectory;
		    break;
		case 'D':
		    pair = g.getOptarg().split("=", 2);
		    if (pair != null && pair.length == 2) {
			System.setProperty(pair[0], pair[1]);
		    }
		    break;
		case 'F':
		    if (playbackFileName != null) {
			usage();
		    }
		    playbackFileName = g.getOptarg();
		    break;
		case 'g':
		    nonRepeatingKeys = true;
		    break;
		case 'G':
		    nonRepeatingKeys = true;
		    demarcatedKeys = true;
		    break;
		case 'h':
		    columnHeaders = g.getOptarg();
		    break;
		case 'k':
		    sparkline = true;
		    break;
		case 'l':
		    pair = g.getOptarg().split("=", 2);
		    if (pair != null && pair.length == 2) {
			logLocalConsumer = pair[0].equals(
				LocalConsumer.class.getName());
			Logger l = Logger.getLogger(pair[0]);
			Level level = Level.parse(pair[1]);
			l.setLevel(level);
		    }
		    break;
		case 'm':
		    macroArgs = g.getOptarg();
		    break;
		case 'M':
		    macroArgLabels = g.getOptarg();
		    break;
		case 'n':
		    if (programBuffer == null) {
			programBuffer = new StringBuilder();
		    } else {
			programBuffer.append('\n');
		    }
		    programBuffer.append(g.getOptarg());
		    break;
		case 'p':
		    if (targetCommand != null) {
			usage();
		    }
		    try {
			targetPID = Integer.parseInt(g.getOptarg());
		    } catch (NumberFormatException e) {
			usage();
		    }
		    break;
		case 'P':
		    lafOption = g.getOptarg();
		    break;
		case 's':
		    programFileName = g.getOptarg();
		    break;
		case 't':
		    displayTitle = g.getOptarg();
		    break;
		case 'T':
		    totalRow = true;
		    break;
		case 'w':
		    writeGeneratedDisplay = true;
		    break;
		case 'W':
		    newDisplayWizard = true;
		    break;
		case 'x':
		    String[] xarg = g.getOptarg().split("=", 2);
		    if (xarg.length > 1) {
			options.add(new Option(xarg[0], xarg[1]));
		    } else if (xarg.length == 1) {
			options.add(new Option(xarg[0]));
		    }
		    break;
		case 'Z':
		    options.add(new Option(Option.zdefs));
		    break;
		case '?':
		    usage(); // getopt() already printed an error
		    break;
		default:
		    System.err.println(FText.format(
			    ChimeStartupText.GETOPT_RETURNED_MSG, c));
		    c = 0;
	    }
	}
	c = 0;
	java.util.List <String> argList = new LinkedList <String> ();
	for (int i = g.getOptind(); i < args.length; ++i) {
	    argList.add(args[i]);
	}

	int arglen = argList.size();
	if (arglen > 2) {
	    usage();
	}

	// No program specified with -n or -s
	if (programBuffer == null && programFileName == null) {
	    if (columnHeaders != null) {
		usage(); // interpret -h as "help"
	    }
	    if (displayTitle != null || totalRow || sparkline ||
		    targetCommand != null || targetPID != null ||
		    macroArgs != null || macroArgLabels != null ||
		    nonRepeatingKeys || demarcatedKeys ||
		    accumulatedValues || !options.isEmpty()) {
		usage();
	    }
	}

	hostname = null;
	port = DEFAULT_PORT;
	if (arglen > 0) {
	    hostname = argList.get(0);
	    if (arglen > 1) {
		try {
		    port = Integer.parseInt(argList.get(1));
		} catch (NumberFormatException e) {
		    usage();
		}
	    }
	}

	if (!displayDirectory.isDirectory()) {
	    System.err.println(FText.format(ChimeStartupText.NOT_DIRECTORY_MSG,
		    displayDirectory));
	    System.exit(1);
	} else if (!displayDirectory.canRead()) {
	    System.err.println(FText.format(ChimeStartupText.CANNOT_READ_MSG,
		    displayDirectory));
	    System.exit(1);
	}

	if (programBuffer != null) {
	    programString = programBuffer.toString();
	}
	if (programString != null && programFileName != null) {
	    System.err.println(ChimeStartupText.PROGRAM_STRING_AND_FILE_MSG);
	    System.exit(2);
	}

	if (programFileName != null) {
	    programFile = new File(programFileName);
	    if (!programFile.isFile()) {
		System.err.println(FText.format(ChimeStartupText.NOT_FILE_MSG,
			programFileName));
		System.exit(1);
	    } else if (!programFile.canRead()) {
		System.err.println(FText.format(
			ChimeStartupText.CANNOT_READ_MSG,
			programFileName));
		System.exit(1);
	    }
	}

        /*
	initResources();

	Persistence.setApplicationDirectory(CHIME_DIR);
	File preferencesFile = Persistence.getFile("preferences.xml");
	if (preferencesFile.exists()) {
	    try {
		ExceptionListener listener = new ExceptionListener() {
		    public void exceptionThrown(Exception e) {
			Logging.warning(logger, e);
		    }
		};
		XMLDecoder decoder = Persistence.getXMLDecoder(
			preferencesFile, listener);
		Object object = decoder.readObject();
		decoder.close();
		if (object instanceof Preferences) {
		    preferences = (Preferences)object;
		}
	    } catch (Exception e) {
		Logging.warning(logger, e);
	    }
	}

	if (preferences == null) {
	    preferences = new Preferences();
	}
        */

	if (logLocalConsumer) {
	    // Enable LocalConsumer logging by causing its static
	    // initializer to run.
	    try {
		Consumer dummy = new LocalConsumer();
		dummy = null;
	    } catch (UnsatisfiedLinkError e) {
		// Remote consumer doesn't need to create a
		// LocalConsumer, so we don't expect to log it.
		Logging.warning(logger, e);
	    }
	}

	if (newDisplayWizard) {
	    File displayFile = null;
	    DisplayDescription displayDescription = null;
	    if (displayFileName != null) {
		displayFile = getDisplayFile(displayFileName);
		if (displayFile == null) {
		    System.exit(1); // err msg already printed
		}
		displayDescription = AggregationDisplay.
			getDescriptionFromFile(displayFile);
		if (displayDescription == null) {
		    System.err.println(FText.format(ChimeFText.CANNOT_LOAD_MSG,
			    displayFileName));
		    System.exit(1);
		}
	    }
	    final File file = displayFile;
	    final DisplayDescription display = displayDescription;
	    SwingUtilities.invokeLater(new Runnable() {
		public void run() {
		    launchNewDisplayWizard(file, display);
		}
	    });
	} else if (displayFileName != null) {
	    File displayFile = getDisplayFile(displayFileName);
	    if (displayFile == null) {
		System.exit(1);
	    }
	    displayDirectory = displayFile.getParentFile();

	    DisplayDescription display = AggregationDisplay.
		    getDescriptionFromFile(displayFile);
	    if (display == null) {
		System.err.println(FText.format(ChimeFText.CANNOT_RUN_MSG,
			displayFileName));
		System.exit(1);
	    }

	    try {
		AggregationDisplay.prepareDescription(display);
	    } catch (Exception x) {
		System.err.println(FText.format(ChimeFText.CANNOT_RUN_MSG,
			displayFileName));
		System.err.println(x.toString());
		System.exit(1);
	    }

	    display.setChimeContext(true);
	    showDisplay(display);
	} else if (playbackFileName != null) {
	    final String fileName = playbackFileName;
	    SwingUtilities.invokeLater(new Runnable() {
		public void run() {
		    launchPlayback(fileName);
		}
	    });
	} else if (programString != null) {
	    Reference <String[][]> macroArgGroups =
		    new Reference <String[][]> ();
	    DisplayDescription display =
		    DisplayGenerator.getDisplayDescription(programString,
		    options.toArray(new Option[options.size()]),
		    (targetPID != null ? targetPID.toString() :
			    targetCommand),
		    generateMacroArguments(macroArgs, macroArgLabels,
			    macroArgGroups));
	    if (display != null) {
		display.setMacroArgGroups(macroArgGroups.get());
		if (demarcatedKeys) {
		    display.setDemarcated(true);
		}
		if (totalRow) {
		    addTotalRow(display);
		}
		if (sparkline) {
		    addSparkline(display);
		}
		if (accumulatedValues) {
		    setAccumulatedValues(display);
		}
		generateDisplay(display, programString, displayTitle,
			columnHeaders, nonRepeatingKeys,
			writeGeneratedDisplay);
	    }
	} else if (programFile != null) {
	    Reference <String[][]> macroArgGroups =
		    new Reference <String[][]> ();
	    DisplayDescription display =
		    DisplayGenerator.getDisplayDescription(programFile,
		    options.toArray(new Option[options.size()]),
		    (targetPID != null ? targetPID.toString() :
			    targetCommand),
		    generateMacroArguments(macroArgs, macroArgLabels,
			    macroArgGroups));

	    if (display != null) {
		display.setMacroArgGroups(macroArgGroups.get());
		if (demarcatedKeys) {
		    display.setDemarcated(true);
		}
		if (totalRow) {
		    addTotalRow(display);
		}
		if (sparkline) {
		    addSparkline(display);
		}
		if (accumulatedValues) {
		    setAccumulatedValues(display);
		}
		generateDisplay(display, programFile, displayTitle,
			columnHeaders, nonRepeatingKeys,
			writeGeneratedDisplay);
	    }
	} else {
	    SwingUtilities.invokeLater(new Runnable() {
		public void run() {
		    launch();
		}
	    });
	}
    }
}   
