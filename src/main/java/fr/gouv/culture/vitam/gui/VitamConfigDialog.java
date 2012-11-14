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

import java.awt.BorderLayout;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import javax.swing.JLabel;

import fr.gouv.culture.vitam.utils.FileExtensionFilter;
import fr.gouv.culture.vitam.utils.StaticValues;
import fr.gouv.culture.vitam.utils.VitamArgument.VitamOutputModel;

import javax.swing.JTextField;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.border.CompoundBorder;

/**
 * Dialog to handle configuration change in the GUI
 * 
 * @author "Frederic Bregier"
 * 
 */
public class VitamConfigDialog extends JPanel {
	private static final long serialVersionUID = 5129887729538501977L;
	JFrame frame;
	private VitamGui vitamGui;
	private static boolean fromMain = false;
	private JTextField filefield;
	private JTextField fileattr;
	private JTextField digestfield;
	private JTextField algoattr;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JRadioButton rdbtnCsvOutput;
	private JRadioButton rdbtnMultipleXmlOutput;
	private JRadioButton rdbtnSingleXmlOutput;
	private JCheckBox chckbxRecursiveChecking;
	private JCheckBox chckbxShaDigest1;
	private JCheckBox chckbxShaDigest256;
	private JCheckBox chckbxShaDigest512;
	private JTextField docField;
	private JCheckBox chckbxProposeFileSave;
	private JCheckBox chckbxUpdateConfigurationFile;
	
	/**
	 * @param frame the parent frame
	 * @param vitamGui the VitamGui associated
	 */
	public VitamConfigDialog(JFrame frame, VitamGui vitamGui) {
		super(new BorderLayout());
		this.vitamGui = vitamGui;
		this.frame = frame;
		setBorder(new CompoundBorder());

		JPanel buttonPanel = new JPanel();
		GridBagLayout buttons = new GridBagLayout();
		buttons.columnWidths = new int[] { 194, 124, 0, 0, 0 };
		buttons.rowHeights = new int[] { 0, 0 };
		buttons.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		buttons.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		buttonPanel.setLayout(buttons);
		add(buttonPanel, BorderLayout.SOUTH);

		JButton btnSaveConfig = new JButton(StaticValues.LBL.button_save.get());
		btnSaveConfig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveConfig();
			}
		});
		GridBagConstraints gbc_btnSaveConfig = new GridBagConstraints();
		gbc_btnSaveConfig.insets = new Insets(0, 0, 0, 5);
		gbc_btnSaveConfig.gridx = 0;
		gbc_btnSaveConfig.gridy = 0;
		buttonPanel.add(btnSaveConfig, gbc_btnSaveConfig);

		String text = StaticValues.LBL.button_cancel.get();
		if (fromMain) {
			text += " " + StaticValues.LBL.button_exit.get();
		}
		JButton btnCancel = new JButton(text);
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});
		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.insets = new Insets(0, 0, 0, 5);
		gbc_btnCancel.gridx = 1;
		gbc_btnCancel.gridy = 0;
		buttonPanel.add(btnCancel, gbc_btnCancel);
		
		chckbxUpdateConfigurationFile = new JCheckBox(StaticValues.LBL.button_update.get());
		GridBagConstraints gbc_chckbxUpdateConfigurationFile = new GridBagConstraints();
		gbc_chckbxUpdateConfigurationFile.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxUpdateConfigurationFile.gridx = 2;
		gbc_chckbxUpdateConfigurationFile.gridy = 0;
		chckbxUpdateConfigurationFile.setSelected(true);
		buttonPanel.add(chckbxUpdateConfigurationFile, gbc_chckbxUpdateConfigurationFile);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPane, BorderLayout.CENTER);
		xmlPanel(tabbedPane);
		toolPanel(tabbedPane);
		outputPanel(tabbedPane);
	}
	
	private void xmlPanel(JTabbedPane tabbedPane) {
		JPanel xmlFilePanel = new JPanel();
		tabbedPane.addTab("XML Context", null, xmlFilePanel, null);
		GridBagLayout gbl_xmlFilePanel = new GridBagLayout();
		gbl_xmlFilePanel.columnWidths = new int[] { 21, 38, 86, 0, 45, 86, 72, 34, 0 };
		gbl_xmlFilePanel.rowHeights = new int[] { 0, 20, 0, 0, 0, 0, 0, 0, 0 };
		gbl_xmlFilePanel.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 1.0,
				Double.MIN_VALUE };
		gbl_xmlFilePanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		xmlFilePanel.setLayout(gbl_xmlFilePanel);

		JLabel lblDocumentField = new JLabel("Document field");
		GridBagConstraints gbc_lblDocumentField = new GridBagConstraints();
		gbc_lblDocumentField.anchor = GridBagConstraints.EAST;
		gbc_lblDocumentField.insets = new Insets(0, 0, 5, 5);
		gbc_lblDocumentField.gridx = 1;
		gbc_lblDocumentField.gridy = 2;
		xmlFilePanel.add(lblDocumentField, gbc_lblDocumentField);

		docField = new JTextField();
		GridBagConstraints gbc_textLibreOffice = new GridBagConstraints();
		gbc_textLibreOffice.gridwidth = 2;
		gbc_textLibreOffice.insets = new Insets(0, 0, 5, 5);
		gbc_textLibreOffice.fill = GridBagConstraints.HORIZONTAL;
		gbc_textLibreOffice.gridx = 2;
		gbc_textLibreOffice.gridy = 2;
		xmlFilePanel.add(docField, gbc_textLibreOffice);
		docField.setColumns(10);

		JButton btnHelp = new JButton("?");
		btnHelp.setToolTipText("Xpath resolution");
		btnHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				showHelp(arg0);
			}
		});
		GridBagConstraints gbc_btnHelp = new GridBagConstraints();
		gbc_btnHelp.insets = new Insets(0, 0, 5, 5);
		gbc_btnHelp.gridx = 5;
		gbc_btnHelp.gridy = 2;
		xmlFilePanel.add(btnHelp, gbc_btnHelp);

		JLabel lblFileField = new JLabel("File field");
		GridBagConstraints gbc_lblFileField = new GridBagConstraints();
		gbc_lblFileField.anchor = GridBagConstraints.EAST;
		gbc_lblFileField.insets = new Insets(0, 0, 5, 5);
		gbc_lblFileField.gridx = 1;
		gbc_lblFileField.gridy = 3;
		xmlFilePanel.add(lblFileField, gbc_lblFileField);

		filefield = new JTextField();
		GridBagConstraints gbc_filefield = new GridBagConstraints();
		gbc_filefield.gridwidth = 2;
		gbc_filefield.insets = new Insets(0, 0, 5, 5);
		gbc_filefield.fill = GridBagConstraints.HORIZONTAL;
		gbc_filefield.gridx = 2;
		gbc_filefield.gridy = 3;
		xmlFilePanel.add(filefield, gbc_filefield);
		filefield.setColumns(10);

		JLabel lblFileAttribut = new JLabel("File attribut");
		GridBagConstraints gbc_lblFileAttribut = new GridBagConstraints();
		gbc_lblFileAttribut.anchor = GridBagConstraints.EAST;
		gbc_lblFileAttribut.insets = new Insets(0, 0, 5, 5);
		gbc_lblFileAttribut.gridx = 4;
		gbc_lblFileAttribut.gridy = 3;
		xmlFilePanel.add(lblFileAttribut, gbc_lblFileAttribut);

		fileattr = new JTextField();
		GridBagConstraints gbc_fileattr = new GridBagConstraints();
		gbc_fileattr.insets = new Insets(0, 0, 5, 5);
		gbc_fileattr.fill = GridBagConstraints.HORIZONTAL;
		gbc_fileattr.gridx = 5;
		gbc_fileattr.gridy = 3;
		xmlFilePanel.add(fileattr, gbc_fileattr);
		fileattr.setColumns(10);

		JLabel lblDigestField = new JLabel("Digest field");
		GridBagConstraints gbc_lblDigestField = new GridBagConstraints();
		gbc_lblDigestField.anchor = GridBagConstraints.EAST;
		gbc_lblDigestField.insets = new Insets(0, 0, 5, 5);
		gbc_lblDigestField.gridx = 1;
		gbc_lblDigestField.gridy = 5;
		xmlFilePanel.add(lblDigestField, gbc_lblDigestField);

		digestfield = new JTextField();
		GridBagConstraints gbc_digestfield = new GridBagConstraints();
		gbc_digestfield.gridwidth = 2;
		gbc_digestfield.insets = new Insets(0, 0, 5, 5);
		gbc_digestfield.fill = GridBagConstraints.HORIZONTAL;
		gbc_digestfield.gridx = 2;
		gbc_digestfield.gridy = 5;
		xmlFilePanel.add(digestfield, gbc_digestfield);
		digestfield.setColumns(10);

		JLabel lblAlgorithmAttribut = new JLabel("Algorithm attribut");
		GridBagConstraints gbc_lblAlgorithmAttribut = new GridBagConstraints();
		gbc_lblAlgorithmAttribut.anchor = GridBagConstraints.EAST;
		gbc_lblAlgorithmAttribut.insets = new Insets(0, 0, 5, 5);
		gbc_lblAlgorithmAttribut.gridx = 4;
		gbc_lblAlgorithmAttribut.gridy = 5;
		xmlFilePanel.add(lblAlgorithmAttribut, gbc_lblAlgorithmAttribut);

		algoattr = new JTextField();
		GridBagConstraints gbc_algoattr = new GridBagConstraints();
		gbc_algoattr.insets = new Insets(0, 0, 5, 5);
		gbc_algoattr.fill = GridBagConstraints.HORIZONTAL;
		gbc_algoattr.gridx = 5;
		gbc_algoattr.gridy = 5;
		xmlFilePanel.add(algoattr, gbc_algoattr);
		algoattr.setColumns(10);

	}
	private void toolPanel(JTabbedPane tabbedPane) {
		JPanel toolsPanel = new JPanel();
		tabbedPane.addTab("Tools", null, toolsPanel, null);
		GridBagLayout gbl_toolsPanel = new GridBagLayout();
		gbl_toolsPanel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gbl_toolsPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_toolsPanel.columnWeights = new double[] { 0.0, 0.0, 1.0, 1.0 };
		gbl_toolsPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
		toolsPanel.setLayout(gbl_toolsPanel);

		JLabel lblCheck = new JLabel("Check");
		GridBagConstraints gbc_lblCheck = new GridBagConstraints();
		gbc_lblCheck.anchor = GridBagConstraints.EAST;
		gbc_lblCheck.insets = new Insets(0, 0, 5, 5);
		gbc_lblCheck.gridx = 1;
		gbc_lblCheck.gridy = 3;
		toolsPanel.add(lblCheck, gbc_lblCheck);

		chckbxRecursiveChecking = new JCheckBox("Recursive checking");
		GridBagConstraints gbc_chckbxRecursiveChecking = new GridBagConstraints();
		gbc_chckbxRecursiveChecking.anchor = GridBagConstraints.WEST;
		gbc_chckbxRecursiveChecking.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxRecursiveChecking.gridx = 4;
		gbc_chckbxRecursiveChecking.gridy = 3;
		toolsPanel.add(chckbxRecursiveChecking, gbc_chckbxRecursiveChecking);

		chckbxShaDigest1 = new JCheckBox("SHA-1 Digest");
		GridBagConstraints gbc_chckbxShaDigest = new GridBagConstraints();
		gbc_chckbxShaDigest.anchor = GridBagConstraints.WEST;
		gbc_chckbxShaDigest.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxShaDigest.gridx = 3;
		gbc_chckbxShaDigest.gridy = 5;
		toolsPanel.add(chckbxShaDigest1, gbc_chckbxShaDigest);

		chckbxShaDigest256 = new JCheckBox("SHA-256 Digest");
		GridBagConstraints gbc_chckbxShaDigest_1 = new GridBagConstraints();
		gbc_chckbxShaDigest_1.anchor = GridBagConstraints.WEST;
		gbc_chckbxShaDigest_1.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxShaDigest_1.gridx = 4;
		gbc_chckbxShaDigest_1.gridy = 5;
		toolsPanel.add(chckbxShaDigest256, gbc_chckbxShaDigest_1);

		chckbxShaDigest512 = new JCheckBox("SHA-512 Digest");
		GridBagConstraints gbc_chckbxShaDigest_2 = new GridBagConstraints();
		gbc_chckbxShaDigest_2.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxShaDigest_2.anchor = GridBagConstraints.WEST;
		gbc_chckbxShaDigest_2.gridx = 5;
		gbc_chckbxShaDigest_2.gridy = 5;
		toolsPanel.add(chckbxShaDigest512, gbc_chckbxShaDigest_2);
		
	}
	private void outputPanel(JTabbedPane tabbedPane) {
		JPanel outputPanel = new JPanel();
		tabbedPane.addTab("Output", null, outputPanel, null);
		GridBagLayout gbl_outputPanel = new GridBagLayout();
		gbl_outputPanel.columnWidths = new int[] { 0, 0, 0, 0 };
		gbl_outputPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_outputPanel.columnWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_outputPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		outputPanel.setLayout(gbl_outputPanel);

		JLabel lblFormatOutput = new JLabel("Format Output");
		GridBagConstraints gbc_lblFormatOutput = new GridBagConstraints();
		gbc_lblFormatOutput.insets = new Insets(0, 0, 5, 5);
		gbc_lblFormatOutput.gridx = 1;
		gbc_lblFormatOutput.gridy = 0;
		outputPanel.add(lblFormatOutput, gbc_lblFormatOutput);

		rdbtnCsvOutput = new JRadioButton("TXT output");
		buttonGroup.add(rdbtnCsvOutput);
		GridBagConstraints gbc_rdbtnCsvOutput = new GridBagConstraints();
		gbc_rdbtnCsvOutput.anchor = GridBagConstraints.WEST;
		gbc_rdbtnCsvOutput.insets = new Insets(0, 0, 5, 0);
		gbc_rdbtnCsvOutput.gridx = 2;
		gbc_rdbtnCsvOutput.gridy = 1;
		outputPanel.add(rdbtnCsvOutput, gbc_rdbtnCsvOutput);

		rdbtnMultipleXmlOutput = new JRadioButton("Multiple XML output");
		buttonGroup.add(rdbtnMultipleXmlOutput);
		GridBagConstraints gbc_rdbtnMultipleXmlOutput = new GridBagConstraints();
		gbc_rdbtnMultipleXmlOutput.anchor = GridBagConstraints.WEST;
		gbc_rdbtnMultipleXmlOutput.insets = new Insets(0, 0, 5, 0);
		gbc_rdbtnMultipleXmlOutput.gridx = 2;
		gbc_rdbtnMultipleXmlOutput.gridy = 2;
		outputPanel.add(rdbtnMultipleXmlOutput, gbc_rdbtnMultipleXmlOutput);

		rdbtnSingleXmlOutput = new JRadioButton("Single XML output");
		buttonGroup.add(rdbtnSingleXmlOutput);
		GridBagConstraints gbc_rdbtnSingleXmlOutput = new GridBagConstraints();
		gbc_rdbtnSingleXmlOutput.anchor = GridBagConstraints.WEST;
		gbc_rdbtnSingleXmlOutput.insets = new Insets(0, 0, 5, 0);
		gbc_rdbtnSingleXmlOutput.gridx = 2;
		gbc_rdbtnSingleXmlOutput.gridy = 3;
		outputPanel.add(rdbtnSingleXmlOutput, gbc_rdbtnSingleXmlOutput);
		
		chckbxProposeFileSave = new JCheckBox("Propose File Save (XML only and best on Single XML)");
		GridBagConstraints gbc_chckbxProposeFileSave = new GridBagConstraints();
		gbc_chckbxProposeFileSave.gridwidth = 2;
		gbc_chckbxProposeFileSave.insets = new Insets(0, 0, 0, 5);
		gbc_chckbxProposeFileSave.gridx = 1;
		gbc_chckbxProposeFileSave.gridy = 4;
		outputPanel.add(chckbxProposeFileSave, gbc_chckbxProposeFileSave);

		JLabel lblTitle = new JLabel("Vitam Configuration");
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblTitle, BorderLayout.NORTH);
		initValue();
	}

	public void initValue() {
		docField.setText(StaticValues.config.DOCUMENT_FIELD);
		filefield.setText(StaticValues.config.ATTACHMENT_FIELD);
		fileattr.setText(StaticValues.config.FILENAME_ATTRIBUTE);
		digestfield.setText(StaticValues.config.INTEGRITY_FIELD);
		algoattr.setText(StaticValues.config.ALGORITHME_ATTRIBUTE);
		chckbxRecursiveChecking.setSelected(StaticValues.config.argument.recursive);
		chckbxShaDigest1.setSelected(StaticValues.config.argument.sha1);
		chckbxShaDigest256.setSelected(StaticValues.config.argument.sha256);
		chckbxShaDigest512.setSelected(StaticValues.config.argument.sha512);
		chckbxProposeFileSave.setSelected(StaticValues.config.guiProposeFileSaving);
		rdbtnCsvOutput
				.setSelected(StaticValues.config.argument.outputModel == VitamOutputModel.TXT);
		rdbtnMultipleXmlOutput
				.setSelected(StaticValues.config.argument.outputModel == VitamOutputModel.MultipleXML);
		rdbtnSingleXmlOutput
				.setSelected(StaticValues.config.argument.outputModel == VitamOutputModel.OneXML);
	}

	public void saveConfig() {
		if (!StaticValues.config.DOCUMENT_FIELD.equals(docField.getText())) {
			StaticValues.config.DOCUMENT_FIELD = docField.getText();
		}
		if (!StaticValues.config.ATTACHMENT_FIELD.equals(filefield.getText())) {
			StaticValues.config.ATTACHMENT_FIELD = filefield.getText();
		}
		if (!StaticValues.config.FILENAME_ATTRIBUTE.equals(fileattr.getText())) {
			StaticValues.config.FILENAME_ATTRIBUTE = fileattr.getText();
		}
		if (!StaticValues.config.INTEGRITY_FIELD.equals(digestfield.getText())) {
			StaticValues.config.INTEGRITY_FIELD = digestfield.getText();
		}
		if (!StaticValues.config.ALGORITHME_ATTRIBUTE.equals(algoattr.getText())) {
			StaticValues.config.ALGORITHME_ATTRIBUTE = algoattr.getText();
		}
		StaticValues.config.argument.recursive = chckbxRecursiveChecking.isSelected();
		StaticValues.config.argument.sha1 = chckbxShaDigest1.isSelected();
		StaticValues.config.argument.sha256 = chckbxShaDigest256.isSelected();
		StaticValues.config.argument.sha512 = chckbxShaDigest512.isSelected();
		StaticValues.config.guiProposeFileSaving = chckbxProposeFileSave.isSelected();
		if (rdbtnCsvOutput.isSelected()) {
			StaticValues.config.argument.outputModel = VitamOutputModel.TXT;
		} else if (rdbtnMultipleXmlOutput.isSelected()) {
			StaticValues.config.argument.outputModel = VitamOutputModel.MultipleXML;
		} else if (rdbtnSingleXmlOutput.isSelected()) {
			StaticValues.config.argument.outputModel = VitamOutputModel.OneXML;
		}
		if (chckbxUpdateConfigurationFile.isSelected()) {
			StaticValues.config.saveConfig();
		}
		if (fromMain) {

		} else {
			this.vitamGui.setEnabled(true);
			this.vitamGui.requestFocus();
			this.frame.setVisible(false);
		}
	}

	public void cancel() {
		if (fromMain) {
			this.frame.dispose();
			System.exit(0);
		} else {
			this.vitamGui.setEnabled(true);
			this.vitamGui.requestFocus();
			this.frame.setVisible(false);
		}
	}

	public File openFile(String currentValue, String text, String extension) {
		JFileChooser chooser = null;
		if (currentValue != null) {
			String file = StaticValues.resourceToFile(currentValue);
			if (file != null) {
				File ffile = new File(file).getParentFile();
				chooser = new JFileChooser(ffile);
			}
		}
		if (chooser == null) {
			chooser = new JFileChooser(System.getProperty("user.dir"));
		}
		if (extension == null) {
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
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
	 * @param arg0
	 */
	public void showHelp(ActionEvent arg0) {
		JPanel panel = new JPanel(new BorderLayout());
		String help = "<html><p><center>Logique des chemins</center></p>"
				+
				"<p><table><tr><td>XPATH //DOCUMENT_FIELD</td><td>ex: Document</td><td>=> //Document</td></tr>"
				+
				"<tr><td>XPATH //DOCUMENT_FIELD/ATTACHMENT_FIELD</td><td>ex: Attachment</td><td>=> //Document/Attachment</td></tr>"
				+
				"<tr><td>XPATH //DOCUMENT_FIELD/ATTACHMENT_FIELD/FILENAME_ATTRIBUTE</td><td>ex: @filename</td><td>=> //Document/Attachment/@filename</td></tr>"
				+
				"<tr><td>XPATH //DOCUMENT_FIELD/INTEGRITY_FIELD</td><td>ex: Integrity</td><td>=> //Document/Integrity</td></tr>"
				+
				"<tr><td>XPATH //DOCUMENT_FIELD/INTEGRITY_FIELD/ALGORITHME_ATTRIBUTE</td><td>ex: @algorithme</td><td>=> //Document/Integrity/@algorithme</td></tr>"
				+
				"</table></p></html>";
		panel.add(new JLabel(help), BorderLayout.CENTER);
		JOptionPane.showConfirmDialog(((JButton) arg0.getSource()).getTopLevelAncestor(), panel,
				"Help",
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be invoked from the
	 * event-dispatching thread.
	 */
	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("Vitam Configuration");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Create and set up the content pane.
		fromMain = true;
		VitamConfigDialog newContentPane = new VitamConfigDialog(frame, null);
		newContentPane.setOpaque(true); // content panes must be opaque
		frame.setContentPane(newContentPane);

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		StaticValues.initialize();
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

}
