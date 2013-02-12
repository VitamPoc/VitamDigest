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
package fr.gouv.culture.vitam.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

import fr.gouv.culture.vitam.digest.CommandExecutionException;
import fr.gouv.culture.vitam.digest.DigestCompute;
import fr.gouv.culture.vitam.utils.ConfigLoader;
import fr.gouv.culture.vitam.utils.StaticValues;
import fr.gouv.culture.vitam.utils.VitamArgument;
import fr.gouv.culture.vitam.utils.VitamArgument.VitamOutputModel;
import fr.gouv.culture.vitam.utils.XmlDom.AllTestsItems;
import fr.gouv.culture.vitam.utils.VitamResult;
import fr.gouv.culture.vitam.utils.XmlDom;

/**
 * Command Line interface main class<br>
 * <br>
 * Need at least one of the following arguments:<br>
 * Mandatory one of (-m,--xml filename | -4,--checkdigest filename | -5,--createdigest ...)<br>
 * where -5,--createdigest source target (-notar | tarfile) (-noglobal | globaldir/prefix) (-noperfile | -perfile) [5 mandatory arguments]<br> 
 * [-ff,--filefield field (default=CONFIG)]<br>
 * [-fa,--fileattrib attribut (default=CONFIG)]<br>
 * [-df,--digestfield field (default=CONFIG)]<br>
 * [-aa,--algoattrib attribut (default=CONFIG)]<br>
 * [-4,--checkdigest filename (default=false)]<br>
 * [-v,--checkrecursive (default=CONFIG) | -nv,--notcheckrecursive]<br>
 * [-h,--computesha algo (where algo=SHA-1,SHA-256,SHA-512 or subset default=CONFIG) | -nh,--notcomputesha]<br>
 * [-e,--extensionrecur filter_in_comma_separated_list (default=no extension filter)]<br>
 * [-x,--formatoutput format (in TXT|XML|XMLS, default=CONFIG)]<br>
 * [-1,--outputfile filename (default=STDOUT)]<br>
 * [--help] to print help<br>
 * [-0,--config configurationFile (default=vitam-config.xml)]<br>
 * 
 * @author Frederic Bregier
 * 
 */
public class VitamCommand {
	public static String XMLarg;
	public static String FILEarg;
	public static String ATTCHfield;
	public static String FILEattr;
	public static String IDENTfield;
	public static String ALGOattr;
	public static String checkDigest = null;
	public static String[] extensions;
	public static String outputformat = null;
	public static String outputfile = null;
	public static String digSource = null;
	public static String digTarget = null;
	public static String digGlobal = null;
	public static String digTar = null;
	public static boolean digPerFile = false;
	public static PrintStream outputStream = System.out;

	public static final String regexAllAlphaNumSpace = "[^\\w\\s&\"#'{([-|`_\\@)\\]°=+}$£%*,?;.:/!§<>-]";
	/**
	 * Enum only to check no double options setup
	 * @author "Frederic Bregier"
	 *
	 */
	static enum VerifOptions {
		m,xml,
		ff,filefield,
		fa,fileattrib,
		df,digestfield,
		aa,algoattrib,
		checkdigest,
		v,checkrecursive,nv,notcheckrecursive,
		h,computesha,nh,notcomputesha,
		e,extensionrecur,
		x,formatoutput,
		outputfile,
		help,
		config
	}
	
	public static void printHelp(ConfigLoader config) {
		System.err.println(StaticValues.HELP_COMMAND);
		System.err.println("\n" + StaticValues.ABOUT);
	}

	/**
	 * Check args and construct the options
	 * 
	 * @param args
	 * @param config
	 * @return True if correctly initiated
	 */
	public static boolean checkArgs(String[] args, ConfigLoader config) {
		if (args.length == 0) {
			return false;
		}
		for (int i = 0; i < args.length; i++) {
			if (args[i].equalsIgnoreCase("-0") || args[i].equalsIgnoreCase("--config")) {
				i++;
				if (i >= args.length) {
					System.err.println(StaticValues.LBL.error_notenough.get() + args[i - 1]);
					return false;
				}
				// load configuration again
				File configFile = new File(args[i]);
				if (! configFile.canRead()) {
					System.err.println(StaticValues.LBL.error_filenotfile.get() + args[i] + "/" + args[i-1]);
					return false;
				}
				StaticValues.config = new ConfigLoader(configFile.getAbsolutePath());
			} else if (args[i].equalsIgnoreCase("--help")) {
				return false;
			}
		}
		for (int i = 0; i < args.length; i++) {
			if (args[i].equalsIgnoreCase("-m") || args[i].equalsIgnoreCase("--xml")) {
				i++;
				if (i >= args.length) {
					System.err.println(StaticValues.LBL.error_notenough.get() + args[i - 1]);
					return false;
				}
				XMLarg = args[i];
			} else if (args[i].equalsIgnoreCase("-ff") || args[i].equalsIgnoreCase("--filefield")) {
				i++;
				if (i >= args.length) {
					System.err.println(StaticValues.LBL.error_notenough.get() + args[i - 1]);
					return false;
				}
				ATTCHfield = args[i];
			} else if (args[i].equalsIgnoreCase("-fa") || args[i].equalsIgnoreCase("--fileattrib")) {
				i++;
				if (i >= args.length) {
					System.err.println(StaticValues.LBL.error_notenough.get() + args[i - 1]);
					return false;
				}
				FILEattr = args[i];
			} else if (args[i].equalsIgnoreCase("-df") || args[i].equalsIgnoreCase("--digestfield")) {
				i++;
				if (i >= args.length) {
					System.err.println(StaticValues.LBL.error_notenough.get() + args[i - 1]);
					return false;
				}
				IDENTfield = args[i];
			} else if (args[i].equalsIgnoreCase("-aa") || args[i].equalsIgnoreCase("--algoattrib")) {
				i++;
				if (i >= args.length) {
					System.err.println(StaticValues.LBL.error_notenough.get() + args[i - 1]);
					return false;
				}
				ALGOattr = args[i];
			} else if (args[i].equalsIgnoreCase("-v")
					|| args[i].equalsIgnoreCase("--checkrecursive")) {
				config.argument.recursive = true;
			} else if (args[i].equalsIgnoreCase("-e")
					|| args[i].equalsIgnoreCase("--extensionrecur")) {
				i++;
				if (i >= args.length) {
					System.err.println(StaticValues.LBL.error_notenough.get() + args[i - 1]);
					return false;
				}
				extensions = args[i].split(",");
			} else if (args[i].equalsIgnoreCase("-h") || args[i].equalsIgnoreCase("--computesha")) {
				i++;
				if (i >= args.length) {
					System.err.println(StaticValues.LBL.error_notenough.get() + args[i - 1]);
					return false;
				}
				String[] temp = args[i].split(",");
				for (String string : temp) {
					if (string.equalsIgnoreCase("sha-1"))
						config.argument.sha1 = true;
					if (string.equalsIgnoreCase("sha-256"))
						config.argument.sha256 = true;
					if (string.equalsIgnoreCase("sha-512"))
						config.argument.sha512 = true;
				}
			} else if (args[i].equalsIgnoreCase("-x") || args[i].equalsIgnoreCase("--formatoutput")) {
				i++;
				if (i >= args.length) {
					System.err.println(StaticValues.LBL.error_notenough.get() + args[i - 1]);
					return false;
				}
				outputformat = args[i];
			} else if (args[i].equalsIgnoreCase("-1") || args[i].equalsIgnoreCase("--outputfile")) {
				i++;
				if (i >= args.length) {
					System.err.println(StaticValues.LBL.error_notenough.get() + args[i - 1]);
					return false;
				}
				outputfile = args[i];
			} else if (args[i].equalsIgnoreCase("-4") || args[i].equalsIgnoreCase("--checkdigest")) {
				i++;
				if (i >= args.length) {
					System.err.println(StaticValues.LBL.error_notenough.get() + args[i - 1]);
					return false;
				}
				checkDigest = args[i];
			} else if (args[i].equalsIgnoreCase("-5") || args[i].equalsIgnoreCase("--createdigest")) {
				i++;
				if (i+4 >= args.length) {
					System.err.println(StaticValues.LBL.error_notenough.get() + args[i - 1]);
					return false;
				}
				digSource = args[i];
				i++;
				digTarget = args[i];
				i++;
				digTar = args[i];
				if (digTar.equalsIgnoreCase("--notar")) {
					digTar = null;
				}
				i++;
				digGlobal = args[i];
				if (digGlobal.equalsIgnoreCase("--noglobal")) {
					digGlobal = null;
				}
				i++;
				digPerFile = args[i].equalsIgnoreCase("--perfile");
				// From here negation of options
			} else if (args[i].equalsIgnoreCase("-nv") || args[i].equalsIgnoreCase("--notcheckrecursive")) {
				config.argument.recursive = false;
			} else if (args[i].equalsIgnoreCase("-nh") || args[i].equalsIgnoreCase("--notcomputesha")) {
				config.argument.sha1 = false;
				config.argument.sha256 = false;
				config.argument.sha512 = false;
			}
		}
		if (XMLarg == null && checkDigest == null && digSource == null) {
			return false;
		}
		if (ATTCHfield != null) {
			config.ATTACHMENT_FIELD = ATTCHfield;
		}
		if (FILEattr != null) {
			config.FILENAME_ATTRIBUTE = FILEattr;
		}
		if (IDENTfield != null) {
			config.INTEGRITY_FIELD = IDENTfield;
		}
		if (ALGOattr != null) {
			config.ALGORITHME_ATTRIBUTE = ALGOattr;
		}
		if (outputfile != null) {
			File file = new File(outputfile);
			try {
				outputStream = new PrintStream(file);
			} catch (FileNotFoundException e) {
				System.err.println(StaticValues.LBL.error_wrongoutput.get());
				outputStream = System.out;
			}
		}
		if (outputformat != null) {
			if (outputformat.equalsIgnoreCase("txt")) {
				config.argument.outputModel = VitamOutputModel.TXT;
			} else if (outputformat.equalsIgnoreCase("xml")) {
				config.argument.outputModel = VitamOutputModel.OneXML;
			} else if (outputformat.equalsIgnoreCase("xmls")) {
				config.argument.outputModel = VitamOutputModel.MultipleXML;
			} else {
				System.err.println(StaticValues.LBL.error_wrongformat.get());
			}
		}
		return true;
	}

	/**
	 * Main command line method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		StaticValues.initialize();
		if (!checkArgs(args, StaticValues.config)) {
			printHelp(StaticValues.config);
			System.exit(1);
		}
		if (XMLarg != null) {
			checkOneXmlFile();
		}
		if (checkDigest != null) {
			computeDigest();
		}
		if (outputfile != null) {
			outputStream.flush();
			outputStream.close();
		}
		if (digSource != null) {
			createDigest();
		}
		System.out.println("\n" + StaticValues.LBL.action_fin.get());
	}
	
	public static void createDigest() {
		if (digSource == null || digTarget == null) {
			System.err.println(
					"Source & Destination invalides");
			return;
		}
		File src = new File(digSource);
		File dst = new File(digTarget);
		if (!src.exists() || !dst.exists()) {
			System.err.println(
					"Source & Destination invalides");
			return;
		}
		boolean oneDigestPerFile = digPerFile;
		File fglobal = null;
		if (digGlobal != null) {
			fglobal = new File(digGlobal).getParentFile();
			if (!fglobal.exists()) {
				System.err.println(
						"Global invalide");
				fglobal = null;
			} else {
				File fout = new File(digGlobal + "_all_digests.xml");
				fglobal = fout;
			}
		}
		File ftar = null;
		if (digTar != null) {
			ftar = new File(digTar);
			if (!ftar.exists()) {
				System.err.println(
						"TAR/ZIP invalide");
				ftar = null;
			}
		}
		int currank = DigestCompute.createDigest(src, dst, ftar, fglobal, oneDigestPerFile, extensions);
		if (currank > 0) {
			System.out
					.println(StaticValues.LBL.action_digest.get() +
							" [ " + currank  + " ]");
		}
	}
	
	public static void computeDigest() {
		XMLWriter writer = null;
		try {
			writer = new XMLWriter(outputStream, StaticValues.defaultOutputFormat);
		} catch (UnsupportedEncodingException e1) {
			System.err.println(StaticValues.LBL.error_writer.get() + ": " + e1.toString());
			return;
		}
		File basedir = new File(checkDigest);
		List<File> files;
		try {
			files = DigestCompute.matchedFiled(new File[] { basedir },
					extensions,
					StaticValues.config.argument.recursive);
		} catch (CommandExecutionException e1) {
			System.err.println(StaticValues.LBL.error_error.get() + e1.toString());
			return;
		}
		System.out.println("Digest...");
		Element root = null;
		VitamResult vitamResult = new VitamResult();
		if (basedir.isFile()) {
			basedir = basedir.getParentFile();
		}
		if (StaticValues.config.argument.outputModel == VitamOutputModel.OneXML) {
			root = XmlDom.factory.createElement("digests");
			root.addAttribute("source", basedir.getAbsolutePath());
			vitamResult.unique = XmlDom.factory.createDocument(root);
		}
		int currank = 0;
		int error = 0;
		for (File file : files) {
			currank++;
			String shortname;
			shortname = StaticValues.getSubPath(file, basedir);
			FileInputStream inputstream;
			try {
				inputstream = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				System.err.println(StaticValues.LBL.error_computedigest.get() + ": " + shortname);
				continue;
			}
			String []shas = DigestCompute.computeDigest(inputstream, StaticValues.config.argument);
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
			if ((shas[0] == null && StaticValues.config.argument.sha1) || 
					(shas[1] == null && StaticValues.config.argument.sha256) ||
					(shas[2] == null && StaticValues.config.argument.sha512)) {
				result.addAttribute("status", "error");
				error ++;
			} else {
				result.addAttribute("status", "ok");
			}
			XmlDom.addDate(StaticValues.config.argument, StaticValues.config, result);
			if (root != null) {
				root.add(result);
			} else {
				// multiple
				root = XmlDom.factory.createElement("digests");
				root.addAttribute("source", basedir.getAbsolutePath());
				root.add(result);
				try {
					writer.write(root);
				} catch (IOException e) {
					System.err.println(StaticValues.LBL.error_error.get() + e.toString());
				}
				root = null;
			}
		}
		if (root != null) {
			if (error == 0) {
				root.addAttribute("status", "ok");
			} else {
				root.addAttribute("status", "error on " + error + " / " + currank + " file checks");
			}
			XmlDom.addDate(StaticValues.config.argument, StaticValues.config, root);
			try {
				writer.write(vitamResult.unique);
			} catch (IOException e) {
				System.err.println(StaticValues.LBL.error_analysis.get() + e);
			}
		}
		System.out
				.println(StaticValues.LBL.action_digest.get() +
						" [ " + currank + (error > 0 ? " (" + StaticValues.LBL.error_error.get() + error + " ) " : "" ) + " ]");
	}


	/**
	 * in order: 1) check XML as XML, 2) check XML with XSD, 3) check SCHEMATRON<br>
	 * 4) check File and Digest<br>
	 * 5) Transform from XSL, 6) optional action check against profile<br>
	 * 7) optional check format 8) optional show format<br>
	 * 
	 */
	public static void checkOneXmlFile() {
		File xml = new File(XMLarg);
		if (!xml.canRead()) {
			System.err.println(StaticValues.LBL.error_filenotfound.get() + " XML: " + XMLarg);
			System.exit(2);
		}
		System.out.println("\nCheck: " + XMLarg + "\n");

		VitamArgument argument = new VitamArgument(
				StaticValues.config.argument.recursive, false, false, false,
				StaticValues.config.argument.outputModel);
		VitamResult result = XmlDom.all_tests_in_one(xml, null, StaticValues.config,
				argument, true);
		int[] iresult = result.values;
		switch (StaticValues.config.argument.outputModel) {
			case TXT:
				break;
			case MultipleXML:
				try {
					XMLWriter writer = null;
					writer = new XMLWriter(outputStream, StaticValues.defaultOutputFormat);
					for (Document document : result.multiples) {
						writer.write(document);
						writer.flush();
					}
				} catch (UnsupportedEncodingException e2) {
					System.err.println(StaticValues.LBL.error_error.get() + e2.toString());
				} catch (IOException e) {
					System.err.println(StaticValues.LBL.error_error.get() + e.toString());
				}
				break;
			case OneXML:
				XMLWriter writer = null;
				try {
					writer = new XMLWriter(outputStream, StaticValues.defaultOutputFormat);
					writer.write(result.unique);
					writer.flush();
				} catch (UnsupportedEncodingException e2) {
					System.err.println(StaticValues.LBL.error_error.get() + e2.toString());
				} catch (IOException e) {
					System.err.println(StaticValues.LBL.error_error.get() + e.toString());
				}
				break;
		}
		int error = iresult[AllTestsItems.SystemError.ordinal()] +
				iresult[AllTestsItems.GlobalError.ordinal()];
		if (error == 0) {
			System.out.print("\n\t" + StaticValues.LBL.action_digest.get() + "[");
			for (int i = AllTestsItems.FileError.ordinal(); i < iresult.length; i += 3) {
				if (iresult[i + 1] > 0) {
					System.out.print(" (" + result.labels[i + 1] + "=" + iresult[i + 1] +
							" " + result.labels[i + 2] + "=" + iresult[i + 2] + ")");
				} else {
					System.out.print(" (" + result.labels[i + 2] + "=" + iresult[i + 2] + ")");
				}
			}
			System.out.println(" ]");
		} else {
			System.out.print("\n\t" + StaticValues.LBL.error_digest.get() + "[");
			if (iresult[AllTestsItems.SystemError.ordinal()] > 0) {
				int i = AllTestsItems.SystemError.ordinal();
				System.out.print(" (" + result.labels[i] + "=" + iresult[i] + ")");
			}
			for (int i = AllTestsItems.FileError.ordinal(); i < iresult.length; i += 3) {
				System.out.print(" (");
				if (iresult[i] > 0) {
					System.out.print(result.labels[i] + "=" + iresult[i] + " ");
				}
				if (iresult[i + 1] > 0) {
					System.out.print(result.labels[i + 1] + "=" + iresult[i + 1] +
							" " + result.labels[i + 2] + "=" + iresult[i + 2] + ")");
				} else {
					System.out.print(result.labels[i + 2] + "=" + iresult[i + 2] + ")");
				}
			}
			if (iresult[9] > 0) {
				int i = AllTestsItems.GlobalWarning.ordinal();
				System.out.print(" (" + result.labels[i] + "=" + iresult[i] + ")");
			}
			System.out.println(" ]");
		}
	}

}
