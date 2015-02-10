/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.komodo.web.backend.server.services.util;

import org.komodo.core.KomodoLexicon;
import org.komodo.spi.KException;
import org.komodo.spi.repository.Descriptor;
import org.komodo.spi.repository.KomodoObject;
import org.komodo.spi.repository.Property;
import org.komodo.web.share.beans.KomodoObjectBean;
import org.komodo.web.share.exceptions.KomodoUiException;
import org.modeshape.sequencer.ddl.dialect.teiid.TeiidDdlLexicon.CreateProcedure;
import org.modeshape.sequencer.ddl.dialect.teiid.TeiidDdlLexicon.CreateTable;
import org.modeshape.sequencer.teiid.lexicon.CoreLexicon;
import org.modeshape.sequencer.teiid.lexicon.CoreLexicon.ModelType;
import org.modeshape.sequencer.teiid.lexicon.VdbLexicon;


/**
 * Contains methods for working with VDBs
 */
public class Utils {

	// ============================================
	// Static Variables

	private static Utils instance = new Utils();
	
	public static String VDB = "VDB";
	public static String VIEW_MODEL = "VIEW_MODEL";
	public static String SOURCE_MODEL = "SOURCE_MODEL";
	public static String TABLE = "TABLE";
	public static String VIEW = "VIEW";
	public static String COLUMN = "COLUMN";
	public static String PARAMETER = "PARAMETER";
	public static String PROCEDURE = "PROCEDURE";
	public static String VIRTUAL_PROCEDURE = "VIRTUAL_PROCEDURE";
	public static String UNKNOWN = "UNKNOWN";
	
	// ============================================
	// Static Methods
	/**
	 * Get the singleton instance
	 *
	 * @return instance
	 */
	public static Utils getInstance() {
		return instance;
	}

	/*
	 * Create a VdbHelper
	 */
	private Utils() {
	}

	/**
	 * Create a VDB object
	 * @param vdbName the name of the VDB
	 * @param vdbVersion the vdb version
	 * @return the VDBMetadata
	 */
	public KomodoObjectBean createKomodoObjectBean(KomodoObject kObj) throws KomodoUiException {
		KomodoObjectBean objBean = new KomodoObjectBean();

		try {
			objBean.setName(kObj.getName(null));
			objBean.setPath(kObj.getAbsolutePath());
			if(kObj.hasChildren(null)) {
				objBean.setHasChildren(true);
			} else {
				objBean.setHasChildren(false);
			}
			objBean.setType(getType(kObj));
		} catch (KException e) {
			throw new KomodoUiException(e);
		}
		
		return objBean;
	}
	
	public String getType(KomodoObject kObj) throws KomodoUiException {
			
			try {

				// check primary type
				Descriptor pType = kObj.getPrimaryType(null);
				if( pType != null ) {
					if( pType.getName().equalsIgnoreCase(VdbLexicon.Vdb.VIRTUAL_DATABASE)) {
						return VDB;
					}
					if( pType.getName().equalsIgnoreCase(KomodoLexicon.VdbModel.NODE_TYPE)) {
						try {
							Property type = (kObj.getParent(null)).getProperty(null, CoreLexicon.JcrId.MODEL_TYPE);
							if( type != null && type.getStringValue(null).equalsIgnoreCase(ModelType.VIRTUAL)) {
								return VIEW_MODEL;
							} else {
								return SOURCE_MODEL;
							}
						} catch (KException e) {
							e.printStackTrace();
						}
					}
				}
				
				Descriptor[] descriptors = kObj.getDescriptors(null);
				if( descriptors.length > 0 ) {
					for( Descriptor des : descriptors ) {
						if( des.getName().equalsIgnoreCase(KomodoLexicon.Vdb.NODE_TYPE)) return VDB;
						else if( des.getName().equalsIgnoreCase(KomodoLexicon.VdbModel.NODE_TYPE)) return VIEW_MODEL;
						else if( des.getName().equalsIgnoreCase(CreateTable.TABLE_STATEMENT)) return TABLE;
						else if( des.getName().equalsIgnoreCase(CreateTable.VIEW_STATEMENT)) return VIEW;
						else if( des.getName().equalsIgnoreCase(CreateProcedure.PROCEDURE_STATEMENT)) {
							try {
								Property type = (kObj.getParent(null)).getProperty(null, CoreLexicon.JcrId.MODEL_TYPE);
								if( type != null && type.getStringValue(null).equalsIgnoreCase(ModelType.VIRTUAL)) {
									return VIRTUAL_PROCEDURE;
								}
							} catch (KException e) {
								e.printStackTrace();
							}
							return PROCEDURE;
						}
						else if( des.getName().equalsIgnoreCase(CreateTable.TABLE_ELEMENT)) return COLUMN;
						else if( des.getName().equalsIgnoreCase(CreateProcedure.PARAMETER)) return PARAMETER;
					}
				}
			} catch (KException e) {
				e.printStackTrace();
			}
			
			
			return UNKNOWN;
		
	}

}

