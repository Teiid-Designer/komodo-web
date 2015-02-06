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

import org.komodo.spi.KException;
import org.komodo.spi.repository.KomodoObject;
import org.komodo.web.share.beans.KomodoObjectBean;
import org.komodo.web.share.exceptions.KomodoUiException;

/**
 * Contains methods for working with VDBs
 */
public class Utils {

	// ============================================
	// Static Variables

	private static Utils instance = new Utils();
	
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
		} catch (KException e) {
			throw new KomodoUiException(e);
		}
		
		return objBean;
	}

}

