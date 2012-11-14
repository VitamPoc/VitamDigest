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

/**
 * Class that handles Vitam arguments among various modules
 * 
 * @author "Frederic Bregier"
 * 
 */
public class VitamArgument {
	public final static VitamArgument NOFEATURE = new VitamArgument();
	public final static VitamArgument RECUSRIVE = new VitamArgument(true, false,
			false, false, null);
	public final static VitamArgument SHAALLONLY = new VitamArgument(false, true, true,
			true, null);
	public final static VitamArgument RECUSRIVESHAALL = new VitamArgument(true, true,
			true, true, null);
	public final static VitamArgument ONEXML = new VitamArgument(false, false, false, 
			false, VitamOutputModel.OneXML);

	public static enum VitamOutputModel {
		TXT, MultipleXML, OneXML
	};

	public boolean recursive;
	public boolean sha1;
	public boolean sha256;
	public boolean sha512;
	public VitamOutputModel outputModel = VitamOutputModel.OneXML;

	/**
	 * @param recursive
	 * @param sha1
	 * @param sha256
	 * @param sha512
	 * @param model
	 */
	public VitamArgument(boolean recursive, boolean sha1, boolean sha256,
			boolean sha512, VitamOutputModel model) {
		this.recursive = recursive;
		this.sha1 = sha1;
		this.sha256 = sha256;
		this.sha512 = sha512;
		this.outputModel = model;
		if (this.outputModel == null) {
			this.outputModel = VitamOutputModel.OneXML;
		}
	}

	/**
	 * 
	 */
	public VitamArgument() {
	}

	/**
	 * 
	 * @return A VitamArgument limited to SHA properties and outputModel
	 */
	public VitamArgument getOnlySha() {
		return new VitamArgument(false, sha1, sha256, sha512, outputModel);
	}

	/**
	 * 
	 * @return A VitamArgument limited outputModel
	 */
	public VitamArgument getOnlyOutputModel() {
		return new VitamArgument(false, false, false, false, outputModel);
	}
}
