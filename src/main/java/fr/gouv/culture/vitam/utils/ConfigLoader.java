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
package fr.gouv.culture.vitam.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import fr.gouv.culture.vitam.utils.VitamArgument.VitamOutputModel;

/**
 * Configuration Loader from file/environment
 * 
 * @author "Frederic Bregier"
 * 
 */
public class ConfigLoader {
	public String xmlFile = null;
	
	public int nbDocument = 0;
	public DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	// Field
	public String DOCUMENT_FIELD = "Document";
	public String ATTACHMENT_FIELD = "Attachment";
	public String FILENAME_ATTRIBUTE = "@filename";
	public String INTEGRITY_FIELD = "Integrity";
	public String ALGORITHME_ATTRIBUTE = "@algorithme";

	// XPATH //DOCUMENT_FIELD example: //Document
	// XPATH //DOCUMENT_FIELD/ATTACHMENT_FIELD example: //Document/Attachment
	// XPATH //DOCUMENT_FIELD/ATTACHMENT_FIELD/FILENAME_ATTRIBUTE example:
	// //Document/Attachment/@filename
	// XPATH //DOCUMENT_FIELD/INTEGRITY_FIELD example: //Document/Integrity
	// XPATH //DOCUMENT_FIELD/INTEGRITY_FIELD/ALGORITHME_ATTRIBUTE example:
	// //Document/Integrity/@algorithme

	// Digest
	public String DEFAULT_DIGEST = StaticValues.XML_SHA1;

	public VitamArgument argument = new VitamArgument();

	// Last directory scan
	public File lastScannedDirectory = null;
	public boolean guiProposeFileSaving = false;
	
	private static String getProperty(Properties properties, String key, String defaultValue) {
		if (properties.containsKey(key)) {
			return properties.getProperty(key);
		}
		properties.setProperty(key, defaultValue);
		return defaultValue;
	}
	private static int getProperty(Properties properties, String key, int defaultValue) {
		if (properties.containsKey(key)) {
			try {
				int value = Integer.parseInt(properties.getProperty(key));
				return value;
			} catch (NumberFormatException e) {
			}
		}
		properties.setProperty(key, Integer.toString(defaultValue));
		return defaultValue;
	}

	public void updateProperties(Properties properties) {
		String value = properties.getProperty("vitam.xsd");
		if (value != null && value.length() == 0) {
			value = null;
		}
		DOCUMENT_FIELD = getProperty(properties, "vitam.docfield", DOCUMENT_FIELD);
		ATTACHMENT_FIELD = getProperty(properties, "vitam.filefield", ATTACHMENT_FIELD);
		FILENAME_ATTRIBUTE = getProperty(properties, "vitam.fileattrib",
				FILENAME_ATTRIBUTE);
		INTEGRITY_FIELD = getProperty(properties, "vitam.digestfield", INTEGRITY_FIELD);
		ALGORITHME_ATTRIBUTE = getProperty(properties, "vitam.algoattrib",
				ALGORITHME_ATTRIBUTE);
		argument.recursive = getProperty(properties, "vitam.checkrecursive", 0) == 1;
		argument.sha1 = getProperty(properties, "vitam.sha1", 1) == 1;
		argument.sha256 = getProperty(properties, "vitam.sha256", 0) == 1;
		argument.sha512 = getProperty(properties, "vitam.sha512", 0) == 1;
		int ivalue = getProperty(properties, "vitam.output",
				VitamOutputModel.OneXML.ordinal());
		if (ivalue < VitamOutputModel.values().length) {
			argument.outputModel = VitamOutputModel.values()[ivalue];
		} else {
			argument.outputModel = VitamOutputModel.OneXML;
		}
		guiProposeFileSaving = getProperty(properties, "vitam.guisave", 0) == 1;
	}

	public void setProperties(Properties properties) {
		DOCUMENT_FIELD = getProperty(properties, "vitam.docfield", DOCUMENT_FIELD);
		ATTACHMENT_FIELD = getProperty(properties, "vitam.filefield", ATTACHMENT_FIELD);
		FILENAME_ATTRIBUTE = getProperty(properties, "vitam.fileattrib",
				FILENAME_ATTRIBUTE);
		INTEGRITY_FIELD = getProperty(properties, "vitam.digestfield", INTEGRITY_FIELD);
		ALGORITHME_ATTRIBUTE = getProperty(properties, "vitam.algoattrib",
				ALGORITHME_ATTRIBUTE);
		argument.recursive = getProperty(properties, "vitam.checkrecursive", argument.recursive ? 1 : 0) == 1;
		argument.sha1 = getProperty(properties, "vitam.sha1", argument.sha1 ? 1 : 0) == 1;
		argument.sha256 = getProperty(properties, "vitam.sha256", argument.sha256 ? 1 : 0) == 1;
		argument.sha512 = getProperty(properties, "vitam.sha512", argument.sha512 ? 1 : 0) == 1;
		int ivalue = getProperty(properties, "vitam.output", argument.outputModel.ordinal());
		if (ivalue < VitamOutputModel.values().length) {
			argument.outputModel = VitamOutputModel.values()[ivalue];
		} else {
			argument.outputModel = VitamOutputModel.OneXML;
		}
		guiProposeFileSaving = getProperty(properties, "vitam.guisave", guiProposeFileSaving ? 1 : 0) == 1;
	}

	public boolean saveConfig() {
		if (xmlFile != null) {
			// based on XML config file
			File config = new File(xmlFile);
			Properties properties = new Properties();
			try {
				setProperties(properties);
				FileOutputStream out = new FileOutputStream(config);
				properties.storeToXML(out, "Vitam Tools configuration", StaticValues.CURRENT_OUTPUT_ENCODING);
				return true;
			} catch (FileNotFoundException e) {
				return false;
			} catch (IOException e) {
				return false;
			}
		}
		return false;
	}
	public void initialize(String xmlFile) {
		boolean configured = false;
		if (xmlFile != null) {
			this.xmlFile = xmlFile;
			// based on XML config file
			File config = new File(xmlFile);
			if (config.canRead()) {
				Properties properties = new Properties();
				FileInputStream in;
				try {
					in = new FileInputStream(config);
					properties.loadFromXML(in);
					in.close();
					
					updateProperties(properties);
					
					FileOutputStream out = new FileOutputStream(config);
					properties.storeToXML(out, "Vitam Tools configuration", StaticValues.CURRENT_OUTPUT_ENCODING);
					configured = true;
				} catch (FileNotFoundException e) {
				} catch (IOException e) {
				}
			}
		}
		if (!configured) {
			// based on environment setup
			DOCUMENT_FIELD = SystemPropertyUtil.getAndSet("vitam.docfield", DOCUMENT_FIELD);
			ATTACHMENT_FIELD = SystemPropertyUtil.getAndSet("vitam.filefield", ATTACHMENT_FIELD);
			FILENAME_ATTRIBUTE = SystemPropertyUtil.getAndSet("vitam.fileattrib",
					FILENAME_ATTRIBUTE);
			INTEGRITY_FIELD = SystemPropertyUtil.getAndSet("vitam.digestfield", INTEGRITY_FIELD);
			ALGORITHME_ATTRIBUTE = SystemPropertyUtil.getAndSet("vitam.algoattrib",
					ALGORITHME_ATTRIBUTE);
			argument.recursive = SystemPropertyUtil.getAndSetInt("vitam.checkrecursive", 0) == 1;
			argument.sha1 = SystemPropertyUtil.getAndSetInt("vitam.sha1", 1) == 1;
			argument.sha256 = SystemPropertyUtil.getAndSetInt("vitam.sha256", 0) == 1;
			argument.sha512 = SystemPropertyUtil.getAndSetInt("vitam.sha512", 0) == 1;
			int value = SystemPropertyUtil.getAndSetInt("vitam.output",
					VitamOutputModel.OneXML.ordinal());
			if (value < VitamOutputModel.values().length) {
				argument.outputModel = VitamOutputModel.values()[value];
			} else {
				argument.outputModel = VitamOutputModel.OneXML;
			}
			guiProposeFileSaving = SystemPropertyUtil.getAndSetInt("vitam.guisave", 0) == 1;

			saveConfig();
		}
		Logger.getRootLogger().setLevel(Level.OFF);
	}

	/**
	 * 
	 */
	public ConfigLoader(String configfile) {
		initialize(configfile);
	}

}
