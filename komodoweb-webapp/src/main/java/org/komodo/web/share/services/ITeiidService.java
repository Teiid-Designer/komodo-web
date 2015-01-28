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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jboss.errai.bus.server.annotations.Remote;
import org.komodo.web.share.beans.DataSourcePageRow;
import org.komodo.web.share.beans.DataSourcePropertyBean;
import org.komodo.web.share.beans.DataSourceWithVdbDetailsBean;
import org.komodo.web.share.beans.VdbDetailsBean;
import org.komodo.web.share.beans.ViewModelRequestBean;
import org.komodo.web.share.exceptions.KomodoUiException;
import org.uberfire.paging.PageRequest;
import org.uberfire.paging.PageResponse;

/**
 * Provides interface for the Teiid remote services
 *
 * @author mdrillin@redhat.com
 */
@Remote
public interface ITeiidService {

    public List<DataSourcePageRow> getDataSources( final String filters, final String sourceVdbPrefix) throws KomodoUiException;

    public PageResponse<DataSourcePageRow> getDataSources( final PageRequest pageRequest, final String filters) throws KomodoUiException;
    		    
    public DataSourceWithVdbDetailsBean getDataSourceWithVdbDetails(String dsName) throws KomodoUiException;

    public List<String> getDataSourceTypes( ) throws KomodoUiException;

    public List<DataSourcePropertyBean> getDataSourceTypeProperties(String dsType) throws KomodoUiException;

    public String getVdbXml(String vdbName) throws KomodoUiException;
    
    public List<String> getDataSourceNames( ) throws KomodoUiException;

    public List<String> getTranslators( ) throws KomodoUiException;

    public Map<String,String> getDefaultTranslatorMap() throws KomodoUiException;
        
    public void createDataSourceWithVdb(DataSourceWithVdbDetailsBean dataSourceWithVdb) throws KomodoUiException;
    
    public void createSourceVdbWithTeiidDS(DataSourceWithVdbDetailsBean bean) throws KomodoUiException;

    public List<VdbDetailsBean> deleteDataSourceAndVdb(String dsName, String vdbName) throws KomodoUiException;

    public void deleteDataSources(Collection<String> dsNames) throws KomodoUiException;

    public void deleteDataSourcesAndVdb(Collection<String> dsNames, String vdbName) throws KomodoUiException;
    
    public void deleteSourcesAndVdbRedeployRenamed(Collection<String> dsNames, String vdbName, DataSourceWithVdbDetailsBean bean) throws KomodoUiException;

    public void deleteTypes(Collection<String> dsTypes) throws KomodoUiException;

    public VdbDetailsBean getVdbDetails(String vdbName) throws KomodoUiException;
    
    public VdbDetailsBean deployNewVDB(final String vdbName, final int vdbVersion, final Map<String,String> vdbPropMap, final ViewModelRequestBean viewModelRequest) throws KomodoUiException;

    public List<VdbDetailsBean> getDataServiceVdbs( ) throws KomodoUiException;

    public Collection<String> getAllVdbNames( ) throws KomodoUiException;
    
    public void deleteDynamicVdbsWithPrefix(String vdbPrefix) throws KomodoUiException;
    
    public List<VdbDetailsBean> cloneDynamicVdbAddSource(String vdbName, int vdbVersion) throws KomodoUiException;

}
