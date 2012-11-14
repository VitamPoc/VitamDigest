/**
 * This file is part of Vitam Project.
 * 
 * Copyright 2010, Frederic Bregier, and individual contributors by the @author
 * tags. See the COPYRIGHT.txt in the distribution for a full listing of individual contributors.
 * 
 * All Vitam Project is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * Vitam is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Vitam. If not, see
 * <http://www.gnu.org/licenses/>.
 */

package fr.gouv.culture.vitam.gui;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

import fr.gouv.culture.vitam.digest.CommandExecutionException;
import fr.gouv.culture.vitam.digest.DigestCompute;
import fr.gouv.culture.vitam.utils.ConfigLoader;
import fr.gouv.culture.vitam.utils.FileExtensionFilter;
import fr.gouv.culture.vitam.utils.StaticValues;
import fr.gouv.culture.vitam.utils.Version;
import fr.gouv.culture.vitam.utils.XmlDom;
import fr.gouv.culture.vitam.utils.VitamArgument.VitamOutputModel;
import fr.gouv.culture.vitam.utils.XmlDom.AllTestsItems;
import fr.gouv.culture.vitam.utils.VitamResult;
import static fr.gouv.culture.vitam.utils.XmlDom.*;
import java.awt.ComponentOrientation;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.UIManager;

/**
 * Main class for the GUI tool
 * 
 * @author "Frederic Bregier"
 * 
 */
public class VitamGui extends JFrame implements PropertyChangeListener {
	protected static final String VITAM_PNG = "/resources/img/vitam.png";
	private static final String VITAM64_PNG = "/resources/img/vitam64.png";
	private static final String VITAM32_PNG = "/resources/img/vitam32.png";
	protected static final String RESOURCES_IMG_VALID_PNG = "/resources/img/valid.png";
	private static final String RESOURCES_IMG_CLEAR_PNG = "/resources/img/edit-clear.png";
	private static final String RESOURCES_IMG_COPY_PNG = "/resources/img/copy.png";
	private static final String RESOURCES_IMG_XML_PNG = "/resources/img/openxml.png";
	private static final String RESOURCES_IMG_DIGEST_PNG = "/resources/img/empreinte.png";
	private static final String RESOURCES_IMG_EXIT_PNG = "/resources/img/exit.png";
	private static final String RESOURCES_IMG_HELP_PNG = "/resources/img/help.png";
	private static final String RESOURCES_IMG_CONFIG_PNG = "/resources/img/settings.png";
	protected static final String RESOURCES_IMG_CHECKFILES_PNG = "/resources/img/find-files.png";
	private static final long serialVersionUID = -8010724089786663345L;

	protected File current_file;
	protected File tempFile;
	protected JTextPane texteOut;
	protected JTextPane texteErr;
	protected JToolBar toolBar;
	protected JMenuBar mb;
	protected List<JMenuItem> listMenuItem;
	protected List<JButton> listButton;
	protected List<JMenuItem> listAllMenuItem;
	protected List<JButton> listAllButton;
	protected static VitamGui vitamGui;
	protected static JProgressBar progressBar;
	public ConfigLoader config;
	protected JFrame frameDialog;
	protected VitamConfigDialog configDialog;
	protected JFrame frameDigestDialog;
	protected VitamDigestDialog vitamDigestDialog;
	protected VitamResult vitamResult;
	protected XMLWriter writer;
	protected List<File> filesToScan;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				vitamGui = new VitamGui();
				vitamGui.setVisible(true);
			}
		});
	}

	/**
	 * Main GUI constructor
	 */
	public VitamGui() {
		StaticValues.initialize();
		List<Image> images = new ArrayList<Image>();
		images.add(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource(VITAM64_PNG)));
		images.add(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource(VITAM32_PNG)));
		setIconImages(images);
		try {
			writer = new XMLWriter(System.out, StaticValues.defaultOutputFormat);
		} catch (UnsupportedEncodingException e1) {
			System.err.println(StaticValues.LBL.error_writer.get() + e1.toString());
			quit();
			return;
		}
		listMenuItem = new ArrayList<JMenuItem>();
		listButton = new ArrayList<JButton>();
		listAllMenuItem = new ArrayList<JMenuItem>();
		listAllButton = new ArrayList<JButton>();
		getContentPane().setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		this.current_file = null;
		this.config = StaticValues.config;

		BorderLayout borderLayout = new BorderLayout();
		borderLayout.setVgap(5);
		getContentPane().setLayout(borderLayout);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				quit();
			}
		});
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		Hashtable<String, String> listMenuItems = new Hashtable<String, String>();
		listMenuItems.put("menu", "menu.file/menu.edit/menu.tools/menu.help");
		listMenuItems.put("menu.file", "file.open/-/file.quit");
		listMenuItems.put("menu.edit", "edit.copy/edit.clear");
		listMenuItems
				.put("menu.tools",
								"tools.attachment_test/tools.hashcode_test/"
								+
								"-/tools.dir_digest/-/tools.digest");
		listMenuItems.put("menu.help", "help.about/help.config");

		setTitle(StaticValues.LBL.appName.get());
		setBackground(Color.white);

		/*
		 * Toolbars
		 */
		toolBar = new JToolBar("Toolbar");
		toolBar.setFloatable(false);
		mb = new JMenuBar();
		createMenuBar(listMenuItems);
		getContentPane().add(toolBar, BorderLayout.NORTH);
		setJMenuBar(mb);
		changeButtonMenu(false);

		Dimension screenSize = new Dimension(java.awt.Toolkit.getDefaultToolkit().getScreenSize());
		int width = screenSize.width / 2;
		if (width < 700) {
			width = 700;
		}
		int height = (int) (screenSize.getHeight() / 2);
		if (height < 640) {
			height = 640;
		}
		screenSize.setSize(width, height);
		setSize(screenSize);

		texteOut = new JTextPane();
		texteOut.setEditable(false);
		texteErr = new JTextPane();
		texteErr.setEditable(false);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
		splitPane.setDividerSize(2);
		splitPane.setAutoscrolls(true);
		JScrollPane outPane = new JScrollPane(texteOut);
		outPane.setViewportBorder(UIManager.getBorder("TextPane.border"));
		JScrollPane errPane = new JScrollPane(texteErr);

		splitPane.setLeftComponent(outPane);
		splitPane.setRightComponent(errPane);
		splitPane.setDividerLocation((screenSize.height - 100) / 2);

		getContentPane().add(splitPane, BorderLayout.CENTER);

		// Redirection of System.out and System.err
		ConsoleOutputStream cos = new ConsoleOutputStream(texteOut, null);
		System.setOut(new PrintStream(cos, true));
		ConsoleOutputStream coserr = new ConsoleOutputStream(texteErr, Color.RED);
		System.setErr(new PrintStream(coserr, true));

		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		getContentPane().add(progressBar, BorderLayout.PAGE_END);
		endProgressBar();

		frameDialog = new JFrame("Vitam Configuration");
		frameDialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				vitamGui.setEnabled(true);
				vitamGui.requestFocus();
				frameDialog.setVisible(false);
			}
		});
		configDialog = new VitamConfigDialog(frameDialog, this);
		configDialog.setOpaque(true); // content panes must be opaque
		frameDialog.setContentPane(configDialog);
		frameDialog.pack();
		frameDialog.setVisible(false);

		frameDigestDialog = new JFrame("Vitam Digest");
		frameDigestDialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				vitamGui.setEnabled(true);
				vitamGui.requestFocus();
				frameDigestDialog.setVisible(false);
			}
		});
		vitamDigestDialog = new VitamDigestDialog(frameDigestDialog, this);
		vitamDigestDialog.setOpaque(true); // content panes must be opaque
		frameDigestDialog.setContentPane(vitamDigestDialog);
		frameDigestDialog.pack();
		frameDigestDialog.setVisible(false);
	}

	/**
	 * Quit application
	 */
	private void quit() {
		dispose();
		System.exit(0);
	}

	/**
	 * This method allows or disallows some button/menu items
	 * 
	 * @param valid
	 */
	protected final void changeButtonMenu(boolean valid) {
		for (JButton button : listButton) {
			button.setEnabled(valid);
		}
		for (JMenuItem menu : listMenuItem) {
			menu.setEnabled(valid);
		}
	}

	/**
	 * This method allows or disallows all button/menu items
	 * 
	 * @param valid
	 */
	protected final void changeAllButtonMenu(boolean valid) {
		for (JButton button : listAllButton) {
			button.setEnabled(valid);
		}
		for (JMenuItem menu : listAllMenuItem) {
			menu.setEnabled(valid);
		}
	}

	/**
	 * create MenuBar
	 * 
	 * @param hash
	 */
	protected final void createMenuBar(Hashtable<String, String> hash) {
		String liste = hash.get("menu");
		StringTokenizer menuKeys = new StringTokenizer(liste, "/");
		while (menuKeys.hasMoreTokens()) {
			String name = menuKeys.nextToken();
			JMenu m = createMenu(name, hash);
			if (m != null) {
				mb.add(m);
				toolBar.addSeparator();
			}
		}
	}

	/**
	 * Create one MenuItem
	 * 
	 * @param cmd
	 * @return the MenuItem
	 */
	protected final JMenuItem createMenuItem(String cmd) {
		JMenuItem mi = new JMenuItem(StaticValues.LABELS.get(cmd));
		return mi;
	}

	/**
	 * Create one MenuItem with an icon
	 * 
	 * @param cmd
	 * @param icon
	 * @return the MenuItem
	 */
	protected final JMenuItem createMenuItem(String cmd, Icon icon) {
		JMenuItem mi = createMenuItem(cmd);
		mi.setIcon(icon);
		return mi;
	}

	/**
	 * Create sub Menu
	 * 
	 * @param key
	 * @param hash
	 * @return the sub Menu
	 */
	private final JMenu createMenu(String key, Hashtable<String, String> hash) {
		final JMenu menu = new JMenu(StaticValues.LABELS.get(key));
		String s = (String) hash.get(key);
		StringTokenizer menuItems = new StringTokenizer(s, "/");

		while (menuItems.hasMoreTokens()) {
			String name = menuItems.nextToken();
			String newname = name.replaceFirst("\\.", "_");
			try {
				final StaticValues.LBL label = StaticValues.LBL.valueOf(newname);
				JMenuItem mi = null;
				JButton button = null;
				ImageIcon img = null;
				ActionListener actionListener = null;
				switch (label) {
					case edit_clear:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_CLEAR_PNG));
						button = new JButton(img);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								texteOut.setText("");
								texteErr.setText("");
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBar.add(button);
						menu.add(mi);
						break;
					case edit_copy:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_COPY_PNG));
						button = new JButton(img);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								texteOut.copy();
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBar.add(button);
						menu.add(mi);
						break;
					case file_open:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_XML_PNG));
						button = new JButton(img);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								openXmlMessage();
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBar.add(button);
						menu.add(mi);
						listAllButton.add(button);
						listAllMenuItem.add(mi);
						break;
					case file_quit:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_EXIT_PNG));
						button = new JButton(img);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								quit();
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBar.add(button);
						menu.add(mi);
						break;
					case help_config:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_CONFIG_PNG));
						button = new JButton(img);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								config();
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBar.add(button);
						menu.add(mi);
						listAllButton.add(button);
						listAllMenuItem.add(mi);
						break;
					case help_about:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_HELP_PNG));
						button = new JButton(img);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								about();
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBar.add(button);
						menu.add(mi);
						listAllButton.add(button);
						listAllMenuItem.add(mi);
						break;
					case tools_attachment_test:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_CHECKFILES_PNG));
						button = new JButton(img);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								initIndeterminateProgressBar();
								runCommand(label);
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBar.add(button);
						menu.add(mi);
						listButton.add(button);
						listMenuItem.add(mi);
						listAllButton.add(button);
						listAllMenuItem.add(mi);
						break;
					case tools_hashcode_test:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_DIGEST_PNG));
						button = new JButton(img);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								initProgressBar();
								runCommand(label);
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBar.add(button);
						menu.add(mi);
						listButton.add(button);
						listMenuItem.add(mi);
						listAllButton.add(button);
						listAllMenuItem.add(mi);
						break;
					case tools_dir_digest:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_DIGEST_PNG));
						button = new JButton(img);
						button.setBackground(Color.CYAN);
						button.setToolTipText(StaticValues.LBL.tools_dir_digest.get());
						mi = createMenuItem(name, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								File directory = choixDirectory("(empreinte)");
								if (directory != null) {
									int nb = initDirectoryListing(directory);
									if (nb > 0) {
										config.nbDocument = nb;
										if (directory.isFile()) {
											config.lastScannedDirectory = directory.getParentFile();
										} else {
											config.lastScannedDirectory = directory;
										}
										initProgressBar();
										runCommand(label);
									} else {
										filesToScan = null;
									}
								}
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBar.add(button);
						menu.add(mi);
						listAllButton.add(button);
						listAllMenuItem.add(mi);
						break;
					case tools_digest:
						img = new ImageIcon(getClass().getResource(RESOURCES_IMG_DIGEST_PNG));
						button = new JButton(img);
						button.setBackground(Color.GREEN);
						button.setToolTipText(label.get());
						mi = createMenuItem(label.label, img);
						actionListener = new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								computeDigest();
							}
						};
						mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,
								ActionEvent.CTRL_MASK));
						mi.addActionListener(actionListener);
						button.addActionListener(actionListener);
						toolBar.add(button);
						menu.add(mi);
						listAllButton.add(button);
						listAllMenuItem.add(mi);
						break;
					default:
						set_ExtraMenu(menu, name);
						break;
				}
			} catch (Exception e) {
				if (name.equals("-")) {
					toolBar.addSeparator();
					menu.addSeparator();
				}

			}
		}
		return menu;
	}

	/**
	 * Method to enable other menu creation if extended
	 * 
	 * @param menu
	 * @param name
	 */
	protected void set_ExtraMenu(JMenu menu, String name) {
	}

	/**
	 * 
	 * @param directory
	 * @return the number of potential files to check
	 */
	protected int initDirectoryListing(File directory) {
		try {
			filesToScan = DigestCompute.matchedFiled(
					new File[] { directory },
					null,
					config.argument.recursive);
		} catch (CommandExecutionException e1) {
			System.err.println(StaticValues.LBL.error_error.get() + " " + e1.toString());
			return 0;
		}
		return filesToScan.size();
	}

	/**
	 * Init the progressBar from config.nbDocument
	 */
	protected void initProgressBar() {
		if (config.nbDocument == 0) {
			int result = countDocument(current_file, config);
			if (result > 0) {
				config.nbDocument = result;
			} else if (result == 0) {
				System.err.println("\n" + StaticValues.LBL.error_digest.get() +
						" [ no file ]");
				return;
			}
		}
		if (config.nbDocument > 0) {
			progressBar.setValue(0);
			progressBar.setMaximum(config.nbDocument);
			progressBar.setVisible(true);
		}
	}

	/**
	 * Init the progressBar in indeterminate mode
	 */
	protected void initIndeterminateProgressBar() {
		progressBar.setIndeterminate(true);
		progressBar.setVisible(true);
	}

	/**
	 * Finalize the progressBar
	 */
	protected void endProgressBar() {
		progressBar.setIndeterminate(false);
		progressBar.setVisible(false);
	}

	/**
	 * Ask for the configuration window
	 * 
	 */
	private void config() {
		this.setEnabled(false);
		frameDialog.setVisible(true);
		configDialog.initValue();
	}

	/**
	 * @param message
	 * @return the directory chosen or null if cancel
	 */
	private File choixDirectory(String message) {
		String fullmessage = StaticValues.LBL.tools_dir.get();
		if (message != null) {
			fullmessage += " " + message;
		}
		return openFile(config.lastScannedDirectory == null ?
				null : config.lastScannedDirectory.getAbsolutePath(),
				fullmessage, null, false);
	}

	/**
	 * 
	 * @param currentValue
	 *            current path where the parent path will be used
	 * @param text
	 *            text to show
	 * @param extension
	 *            extension filter (null if no filter)
	 * @return the chosen file or null if cancel
	 */
	private File openFile(String currentValue, String text, String extension) {
		return openFile(currentValue, text, extension, true);
	}

	/**
	 * 
	 * @param currentValue
	 *            current path where the exact path will be used
	 * @param text
	 *            text to show
	 * @param extension
	 *            extension filter (null if no filter)
	 * @param parent
	 *            if true currentValue will be changed to parent, else no change
	 * @return the chosen file or null if cancel
	 */
	private File openFile(String currentValue, String text, String extension, boolean parent) {
		JFileChooser chooser = null;
		if (currentValue != null) {
			String file = StaticValues.resourceToFile(currentValue);
			if (file != null) {
				File ffile = new File(file);
				if (parent) {
					ffile = ffile.getParentFile();
				}
				chooser = new JFileChooser(ffile);
			}
		}
		if (chooser == null) {
			if (current_file != null) {
			chooser = new JFileChooser(current_file.getParentFile());
			} else {
				chooser = new JFileChooser(System.getProperty("user.dir"));
			}
		}
		if (extension == null) {
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		} else {
			FileExtensionFilter filter = new FileExtensionFilter(extension, text);
			chooser.setFileFilter(filter);
		}
		chooser.setDialogTitle(text);
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		}
		return null;
	}
	/**
	 * 
	 * @param currentValue
	 *            current path where the exact path will be used
	 * @param text
	 *            text to show
	 * @param extension
	 *            extension filter (null if no filter)
	 * @param parent
	 *            if true currentValue will be changed to parent, else no change
	 * @return the chosen file or null if cancel
	 */
	private File saveFile(String currentValue, String text, String extension, boolean parent) {
		JFileChooser chooser = null;
		if (currentValue != null) {
			String file = StaticValues.resourceToFile(currentValue);
			if (file != null) {
				File ffile = new File(file);
				if (parent) {
					ffile = ffile.getParentFile();
				}
				chooser = new JFileChooser(ffile);
			}
		}
		if (chooser == null) {
			if (current_file != null) {
				chooser = new JFileChooser(current_file.getParentFile());
			} else {
				chooser = new JFileChooser(System.getProperty("user.dir"));
			}
		}
		if (extension == null) {
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		} else {
			FileExtensionFilter filter = new FileExtensionFilter(extension, text);
			chooser.setFileFilter(filter);
		}
		chooser.setDialogTitle(text);
		if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			if (extension != null) {
				String extfile =  FileExtensionFilter.getExtension(file);
				if (extfile == null || ! extfile.equalsIgnoreCase(extension)) {
					file = new File(file.getAbsolutePath()+"."+extension);
				}
			}
			return file;
		}
		return null;
	}

	/**
	 * Open a XML message
	 */
	private void openXmlMessage() {
		File file = null;
		file = openFile(null, StaticValues.LBL.file_open.get(), "xml");
		if (file != null) {
			current_file = file;
			vitamResult = null;
			config.nbDocument = 0;
			changeButtonMenu(true);
			long size = file.length();
			if (size < Integer.MAX_VALUE) {
				StringBuilder fileData = new StringBuilder();
		        BufferedReader reader = null;
				try {
					reader = new BufferedReader(new FileReader(file));
			        char[] buf = new char[8096];
			        int numRead=0;
			        while((numRead=reader.read(buf)) != -1){
			            fileData.append(buf, 0, numRead);
			        }
			        reader.close();
			        reader = null;
			        String text = fileData.toString();
					JTextArea textArea = new JTextArea(text);
					final JPanel panel = new JPanel(new BorderLayout());
					panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
					panel.setPreferredSize(new Dimension(500, 500));
					// TIP: Make the JOptionPane resizable using the HierarchyListener
			        panel.addHierarchyListener(new HierarchyListener() {
			            public void hierarchyChanged(HierarchyEvent e) {
			                Window window = SwingUtilities.getWindowAncestor(panel);
			                if (window instanceof Dialog) {
			                    Dialog dialog = (Dialog)window;
			                    if (!dialog.isResizable()) {
			                        dialog.setResizable(true);
			                    }
			                }
			            }
			        });
					JOptionPane.showConfirmDialog(this, panel,
							"Xml File",
							JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
				} catch (FileNotFoundException e1) {
				} catch (IOException e1) {
					if (reader != null) {
						try {
							reader.close();
						} catch (IOException e) {
						}
					}
				}
			}
		}
	}

	/**
	 * About and license
	 */
	public void about() {
		StringBuffer content = new StringBuffer();
		BufferedReader reader = new BufferedReader(new InputStreamReader(getClass()
				.getResourceAsStream(StaticValues.RESOURCES_LICENSE_TXT)));
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				content.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		final JPanel panel = new JPanel(new BorderLayout());
		String credits = "<html><p><center>Copyright (c) 2012 Ministere de la Culture et de la Communication<br>"
				+
				"Sous-Direction du Systeme d'Information<br>Projet Vitam</center></p><p></p><p><center>Version: "
				+ Version.ID
				+
				"</center></p><p></p><p>Contributeurs: <br><i>Frederic Bregier</i></p><p></p>"
				+
				"<p>Site web: <a href='http://www.archivesnationales.culture.gouv.fr/'>http://www.archivesnationales.culture.gouv.fr/</a></p></html>";
		panel.add(new JLabel(credits), BorderLayout.NORTH);
		JTextArea textArea = new JTextArea(content.toString());
		panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
		panel.setPreferredSize(new Dimension(550, 500));
		// TIP: Make the JOptionPane resizable using the HierarchyListener
        panel.addHierarchyListener(new HierarchyListener() {
            public void hierarchyChanged(HierarchyEvent e) {
                Window window = SwingUtilities.getWindowAncestor(panel);
                if (window instanceof Dialog) {
                    Dialog dialog = (Dialog)window;
                    if (!dialog.isResizable()) {
                        dialog.setResizable(true);
                    }
                }
            }
        });
		JOptionPane.showConfirmDialog(this, panel,
				StaticValues.LBL.label_about.get(),
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Validation of existence of Attachments
	 */
	private void attachmentValid() {
		if (current_file != null) {
			config.nbDocument = 0;
			vitamResult = all_tests_in_one(current_file, null, config, config.argument,
					false, false, false);
			if (vitamResult.values[AllTestsItems.SystemError.ordinal()] +
					vitamResult.values[AllTestsItems.GlobalError.ordinal()] > 0) {
				// error
				System.err.println(StaticValues.LBL.error_attachment.get() +
						" [ " + vitamResult.labels[AllTestsItems.SystemError.ordinal()] + "=" +
						vitamResult.values[AllTestsItems.SystemError.ordinal()] + " " +
						vitamResult.labels[AllTestsItems.FileError.ordinal()] + "=" +
						vitamResult.values[AllTestsItems.FileError.ordinal()] + " " +
						vitamResult.labels[AllTestsItems.FileSuccess.ordinal()] + "=" +
						vitamResult.values[AllTestsItems.FileSuccess.ordinal()] + " ]");
				vitamResult = null;
			} else {
				// OK
				texteOut.insertIcon(new ImageIcon(getClass().getResource(
						RESOURCES_IMG_VALID_PNG)));
				System.out
						.println(StaticValues.LBL.action_attachment.get() +
								" [ " + vitamResult.values[AllTestsItems.FileSuccess.ordinal()]
								+ " ]");
				config.nbDocument = vitamResult.values[AllTestsItems.FileSuccess.ordinal()];
			}
		} else {
			vitamResult = null;
			System.err.println(StaticValues.LBL.action_nofile.get());
		}
	}

	/**
	 * Validation of existence of Attachments and the corresponding digests
	 */
	private void digestValid(RunnerLongTask task) {
		if (current_file != null) {
			vitamResult = all_tests_in_one(current_file, task, config, config.argument,
					true, false, false);
			if (vitamResult.values[AllTestsItems.SystemError.ordinal()] +
					vitamResult.values[AllTestsItems.GlobalError.ordinal()] > 0) {
				// error
				System.err.println(StaticValues.LBL.error_digest.get() +
						" [ " + vitamResult.labels[AllTestsItems.SystemError.ordinal()] + "=" +
						vitamResult.values[AllTestsItems.SystemError.ordinal()] + " " +
						vitamResult.labels[AllTestsItems.FileError.ordinal()] + "=" +
						vitamResult.values[AllTestsItems.FileError.ordinal()] + " " +
						vitamResult.labels[AllTestsItems.FileSuccess.ordinal()] + "=" +
						vitamResult.values[AllTestsItems.FileSuccess.ordinal()] + " " +
						vitamResult.labels[AllTestsItems.DigestError.ordinal()] + "=" +
						vitamResult.values[AllTestsItems.DigestError.ordinal()] + " " +
						vitamResult.labels[AllTestsItems.DigestSuccess.ordinal()] + "=" +
						vitamResult.values[AllTestsItems.DigestSuccess.ordinal()] +
						" ]");
				if (vitamResult.values[AllTestsItems.SystemError.ordinal()] > 0) {
					vitamResult = null;
				}
			} else {
				// OK
				texteOut.insertIcon(new ImageIcon(getClass().getResource(
						RESOURCES_IMG_VALID_PNG)));
				System.out
						.println(StaticValues.LBL.action_digest.get() +
								" [ " + vitamResult.values[AllTestsItems.DigestSuccess.ordinal()]
								+ " ]");
				config.nbDocument = vitamResult.values[AllTestsItems.DigestSuccess.ordinal()];
			}
		} else {
			System.err.println(StaticValues.LBL.action_nofile.get());
			vitamResult = null;
		}
	}

	/**
	 * Digest for one directory in detail
	 * 
	 */
	private void computeDigest() {
		this.setEnabled(false);
		frameDigestDialog.setVisible(true);
		vitamDigestDialog.initValue();
	}
	/**
	 * Check one directory against digest
	 * 
	 * @param task
	 */
	private void oneDirDigest(RunnerLongTask task) {
		List<File> files = filesToScan;
		filesToScan = null;
		System.out.println("Digest...");
		Element root = null;
		vitamResult = new VitamResult();
		if (config.argument.outputModel == VitamOutputModel.OneXML) {
			root = XmlDom.factory.createElement("digests");
			root.addAttribute("source", config.lastScannedDirectory.getAbsolutePath());
			vitamResult.unique = XmlDom.factory.createDocument(root);
		} else {
			// force multiple
			vitamResult.multiples = new ArrayList<Document>();
		}
		int currank = 0;
		int error = 0;
		for (File file : files) {
			currank++;
			String shortname;
			if (config.lastScannedDirectory.isDirectory()) {
				shortname = StaticValues.getSubPath(file, config.lastScannedDirectory);
			} else {
				shortname = config.lastScannedDirectory.getName();
			}
			FileInputStream inputstream;
			try {
				inputstream = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				System.err.println(StaticValues.LBL.error_computedigest.get() + ": " + shortname);
				continue;
			}
			String []shas = DigestCompute.computeDigest(inputstream, config.argument);
			//SEDA type since already configured
			Element result = XmlDom.factory.createElement(StaticValues.config.DOCUMENT_FIELD);
			Element attachment = XmlDom.factory.createElement(StaticValues.config.ATTACHMENT_FIELD);
			attachment.addAttribute(StaticValues.config.FILENAME_ATTRIBUTE.substring(1), shortname);
			result.add(attachment);
			if (shas[0] != null) {
				Element integrity = XmlDom.factory.createElement(StaticValues.config.INTEGRITY_FIELD);
				integrity.addAttribute(StaticValues.config.ALGORITHME_ATTRIBUTE.substring(1), StaticValues.XML_SHA1);
				integrity.setText(shas[0]);
				result.add(integrity);
			}
			if (shas[1] != null) {
				Element integrity = XmlDom.factory.createElement(StaticValues.config.INTEGRITY_FIELD);
				integrity.addAttribute(StaticValues.config.ALGORITHME_ATTRIBUTE.substring(1), StaticValues.XML_SHA256);
				integrity.setText(shas[1]);
				result.add(integrity);
			}
			if (shas[2] != null) {
				Element integrity = XmlDom.factory.createElement(StaticValues.config.INTEGRITY_FIELD);
				integrity.addAttribute(StaticValues.config.ALGORITHME_ATTRIBUTE.substring(1), StaticValues.XML_SHA512);
				integrity.setText(shas[2]);
				result.add(integrity);
			}
			if ((shas[0] == null && config.argument.sha1) || 
					(shas[1] == null && config.argument.sha256) ||
					(shas[2] == null && config.argument.sha512)) {
				result.addAttribute("status", "error");
				error ++;
			} else {
				result.addAttribute("status", "ok");
			}
			XmlDom.addDate(config.argument, config, result);
			if (root != null) {
				root.add(result);
			} else {
				// multiple
				root = XmlDom.factory.createElement("digests");
				root.addAttribute("source", config.lastScannedDirectory.getAbsolutePath());
				Document document = XmlDom.factory.createDocument(root);
				root.add(result);
				vitamResult.multiples.add(document);
				root = null;
			}
			if (task != null) {
				task.setProgressExternal(currank);
			}
		}
		if (root != null) {
			if (error == 0) {
				root.addAttribute("status", "ok");
			} else {
				root.addAttribute("status", "error on " + error + " / " + currank + " file checks");
			}
			XmlDom.addDate(config.argument, config, root);
		}

		texteOut.insertIcon(new ImageIcon(getClass().getResource(
				RESOURCES_IMG_VALID_PNG)));
		System.out
				.println(StaticValues.LBL.action_digest.get() +
						" [ " + currank + (error > 0 ? " (" + StaticValues.LBL.error_error.get() + error + " ) " : "" ) + " ]");
	}

	/**
	 * Run a defined command according to label through RunnerLongTask
	 * 
	 * @param label
	 */
	protected final void runCommand(StaticValues.LBL label) {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		changeAllButtonMenu(false);
		RunnerLongTask task = new RunnerLongTask(label, this);
		task.addPropertyChangeListener(this);
		task.execute();
	}

	/**
	 * Used to extend model
	 * 
	 * @param label
	 */
	protected void runSwingWorkerExtraCommand(StaticValues.LBL label) {
		System.err.println("SWExtraCommand: " + label.label);
	}

	/**
	 * Used to extend model
	 * 
	 * @param label
	 */
	protected void runEndExtraCommand(StaticValues.LBL label) {
		System.err.println("ExtraCommand: " + label.label);
	}

	/**
	 * Class to allow asynchronous execution of tasks
	 * 
	 * @author "Frederic Bregier"
	 * 
	 */
	public static class RunnerLongTask extends SwingWorker<Object, Void> {
		StaticValues.LBL label;
		VitamGui gui;

		private RunnerLongTask(StaticValues.LBL label, VitamGui gui) {
			super();
			this.label = label;
			this.gui = gui;
		}

		@Override
		protected void done() {
			@SuppressWarnings("unused")
			Object object;
			boolean checkxml = false;
			try {
				object = get();
				gui.setCursor(null); // turn off the wait cursor
				switch (label) {
					case tools_dir_digest:
						this.gui.config.nbDocument = 0;
						checkxml = false;
						break;
					case file_open:
					case tools_attachment_test:
					case tools_hashcode_test:
						checkxml = true;
						break;
					default:
						this.gui.runEndExtraCommand(label);
						break;
				}
			} catch (InterruptedException e) {
			} catch (ExecutionException e) {
			} finally {
				gui.endProgressBar();
				gui.changeAllButtonMenu(true);
				if (gui.current_file == null) {
					gui.changeButtonMenu(false);
				}
			}
			if (gui.vitamResult != null) {
				if (gui.vitamResult.unique != null) {
					String text = null;
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					try {
						gui.writer.setOutputStream(out);
						gui.writer.write(gui.vitamResult.unique);
						gui.writer.flush();
						text = out.toString(StaticValues.CURRENT_OUTPUT_ENCODING);
					} catch (UnsupportedEncodingException e) {
						gui.vitamResult = null;
						return;
					} catch (IOException e) {
						gui.vitamResult = null;
						return;
					} finally {
						try {
							out.close();
						} catch (IOException e) {
						}
					}
					JTextArea textArea = new JTextArea(text);
					final JPanel panel = new JPanel(new BorderLayout());
					panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
					panel.setPreferredSize(new Dimension(500, 500));
					// TIP: Make the JOptionPane resizable using the HierarchyListener
			        panel.addHierarchyListener(new HierarchyListener() {
			            public void hierarchyChanged(HierarchyEvent e) {
			                Window window = SwingUtilities.getWindowAncestor(panel);
			                if (window instanceof Dialog) {
			                    Dialog dialog = (Dialog)window;
			                    if (!dialog.isResizable()) {
			                        dialog.setResizable(true);
			                    }
			                }
			            }
			        });
					JOptionPane.showConfirmDialog(gui, panel,
							"Xml Result",
							JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
					if (gui.config.guiProposeFileSaving) {
						// XML write to file
						String current = null;
						if (checkxml) {
							current = (gui.current_file == null ? null : gui.current_file.getAbsolutePath());
						} else {
							current = (gui.config.lastScannedDirectory == null ? null : gui.config.lastScannedDirectory.getAbsolutePath());
						}
						File file = gui.saveFile(current, "XML output", "xml", true);
						if (file != null) {
							FileOutputStream out2 = null;
							try {
								out2 = new FileOutputStream(file);
								gui.writer.setOutputStream(out2);
								gui.writer.write(gui.vitamResult.unique);
								gui.writer.flush();
							} catch (UnsupportedEncodingException e) {
								gui.vitamResult = null;
								return;
							} catch (IOException e) {
								gui.vitamResult = null;
								return;
							} finally {
								try {
									if (out2 != null) {
										out2.close();
									}
								} catch (IOException e) {
								}
							}
						}
					}
				} else if (gui.vitamResult.multiples != null
						&& !gui.vitamResult.multiples.isEmpty()) {
					String text = null;
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					try {
						gui.writer.setOutputStream(out);
						for (Document document : gui.vitamResult.multiples) {
							gui.writer.write(document);
							gui.writer.flush();
							out.write("\n".getBytes());
						}
						text = out.toString(StaticValues.CURRENT_OUTPUT_ENCODING);
					} catch (UnsupportedEncodingException e) {
						gui.vitamResult = null;
						return;
					} catch (IOException e) {
						gui.vitamResult = null;
						return;
					} finally {
						try {
							out.close();
						} catch (IOException e) {
						}
					}
					JTextArea textArea = new JTextArea(text);
					JPanel panel = new JPanel(new BorderLayout());
					panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
					panel.setPreferredSize(new Dimension(550, 500));
					JOptionPane.showConfirmDialog(gui, panel,
							"Xml Result",
							JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
					if (gui.config.guiProposeFileSaving) {
						// XMLs write to file
						String current = (gui.current_file == null ? null : gui.current_file.getAbsolutePath());
						File file = gui.saveFile(current, "XML output", "xml", true);
						if (file != null) {
							FileOutputStream out2 = null;
							try {
								out2 = new FileOutputStream(file);
								gui.writer.setOutputStream(out2);
								for (Document document : gui.vitamResult.multiples) {
									gui.writer.write(document.getRootElement());
									gui.writer.flush();
								}
							} catch (UnsupportedEncodingException e) {
								gui.vitamResult = null;
								return;
							} catch (IOException e) {
								gui.vitamResult = null;
								return;
							} finally {
								try {
									if (out2 != null) {
										out2.close();
									}
								} catch (IOException e) {
								}
							}
						}
					}
				}
				gui.vitamResult = null;
			}
		}

		public void setProgressExternal(final int progress) {
			setProgress(progress);
		}

		@Override
		protected Object doInBackground() throws Exception {
			setProgress(0);
			switch (label) {
				case file_open:
					break;
				case tools_attachment_test:
					gui.attachmentValid();
					break;
				case tools_hashcode_test:
					gui.digestValid(this);
					break;
				case tools_dir_digest:
					gui.oneDirDigest(this);
					break;
				default:
					gui.runSwingWorkerExtraCommand(label);
					break;
			}
			return true;
		}

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
			int progress = (Integer) evt.getNewValue();
			progressBar.setValue(progress);
		}
	}
}
