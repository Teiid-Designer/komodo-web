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

import org.komodo.spi.repository.KomodoObject;
import org.komodo.spi.repository.Repository.UnitOfWork;
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
	 *
	 * @param kObj the komodo object
	 * @return the komodo object bean
	 * @throws KomodoUiException if error occurs
	 */
    public KomodoObjectBean createKomodoObjectBean(KomodoObject kObj) throws KomodoUiException {
        KomodoObjectBean objBean = new KomodoObjectBean();

        try {
            UnitOfWork transaction = kObj.getRepository().createTransaction("create-komodo-object-bean", true, null); //$NON-NLS-1$

			objBean.setName(kObj.getName(transaction));
			objBean.setPath(kObj.getAbsolutePath());
			if(kObj.hasChildren(transaction)) {
				objBean.setHasChildren(true);
			} else {
				objBean.setHasChildren(false);
			}
			objBean.setType(kObj.getTypeIdentifier(transaction));
			objBean.setIsVirtual(RelationalIdentifier.isVirtual(kObj, transaction));

			transaction.commit();

		} catch (Exception e) {
			throw new KomodoUiException(e);
		}
		
		return objBean;
	}
	
}

