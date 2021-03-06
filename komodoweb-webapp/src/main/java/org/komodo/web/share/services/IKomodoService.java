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
package org.komodo.web.share.services;

import java.util.List;
import org.jboss.errai.bus.server.annotations.Remote;
import org.komodo.web.share.beans.KomodoObjectBean;
import org.komodo.web.share.beans.KomodoObjectPropertyBean;
import org.komodo.web.share.exceptions.KomodoUiException;

/**
 * Provides interface for the Komodo remote services
 *
 * @author mdrillin@redhat.com
 */
@Remote
public interface IKomodoService {

    /**
     * Start the Komodo Engine
     *
     * @throws KomodoUiException if error occurs
     */
    void startKEngine( ) throws KomodoUiException;

    /**
     * Shutdown the Komodo Engine
     *
     * @throws KomodoUiException if error occurs
     */
    void shutdownKEngine( ) throws KomodoUiException;

    /**
     * Retrieve the komodo node at the given path
     *
     * @param path the path of the node to retrieve
     * @return komodo node at the given path
     *
     * @throws KomodoUiException if error occurs
     */
    KomodoObjectBean getKomodoNode(final String path) throws KomodoUiException;

    /**
     * Retrieve the komodo nodes at the given path
     *
     * @param path the path of the children to retrieve
     * @return list of komodo objects
     *
     * @throws KomodoUiException if error occurs
     */
    List<KomodoObjectBean> getKomodoNodes(final String path) throws KomodoUiException;

    /**
     * Create a vdb
     *
     * @param vdbName the name of the vdb
     * @return new vdb
     *
     * @throws KomodoUiException if error occurs
     */
    KomodoObjectBean createVdb(final String vdbName) throws KomodoUiException;

    /**
     * Delete the vdb with the given name
     *
     * @param vdbName the vdb name
     * @return deleted vdb
     *
     * @throws KomodoUiException if error occurs
     */
    List<KomodoObjectBean> deleteVdb(final String vdbName) throws KomodoUiException;

    /**
     * Get the DDL of the given vdb
     *
     * @param vdbPath the name path
     * @return the DDL
     * @throws KomodoUiException if error occurs
     */
    String getVdbDDL(final String vdbPath) throws KomodoUiException;

    /**
     * @param parentPath the path of the property parent
     * @param name the name
     * @param value the value
     * @return the update komodo object
     * @throws KomodoUiException if error occurs
     */
    KomodoObjectBean addProperty(String parentPath, String name, String value) throws KomodoUiException;

    /**
     * @param propertyBean the property to be removed
     * @return the updated komodo object
     * @throws KomodoUiException if error occurs
     */
    KomodoObjectBean removeProperty(KomodoObjectPropertyBean propertyBean) throws KomodoUiException;

    /**
     * @param propertyBean the property to be updated
     * @param newValue the new value
     * @return propertyBean that has been updated
     * @throws KomodoUiException if error occurs
     */
    KomodoObjectPropertyBean updateProperty(KomodoObjectPropertyBean propertyBean, Object newValue) throws KomodoUiException;

    /**
     * @param kObjectPath path of object
     * @return json tree representation of the object and its children
     *
     * @throws KomodoUiException
     */
    String deriveJsonTree(String kObjectPath) throws KomodoUiException;

}
