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
package org.komodo.web.client.services;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.komodo.web.client.services.rpc.DelegatingErrorCallback;
import org.komodo.web.client.services.rpc.DelegatingRemoteCallback;
import org.komodo.web.client.services.rpc.IRpcServiceInvocationHandler;
import org.komodo.web.share.beans.DataSourcePageRow;
import org.komodo.web.share.beans.DataSourcePropertyBean;
import org.komodo.web.share.beans.DataSourceWithVdbDetailsBean;
import org.komodo.web.share.beans.VdbDetailsBean;
import org.komodo.web.share.beans.ViewModelRequestBean;
import org.komodo.web.share.exceptions.KomodoUiException;
import org.komodo.web.share.services.ITeiidService;
import org.uberfire.paging.PageRequest;
import org.uberfire.paging.PageResponse;

/**
 * Client-side service for making RPC calls to the remote DataSource service.
 *
 * @author mdrillin@redhat.com
 */
@ApplicationScoped
public class TeiidRpcService {

    @Inject
    private Caller<ITeiidService> remoteTeiidService;

    /**
     * Constructor.
     */
    public TeiidRpcService() {
    }

    public void getDataSourceWithVdbDetails(String dsName, final IRpcServiceInvocationHandler<DataSourceWithVdbDetailsBean> handler) {
        RemoteCallback<DataSourceWithVdbDetailsBean> successCallback = new DelegatingRemoteCallback<DataSourceWithVdbDetailsBean>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
        	remoteTeiidService.call(successCallback, errorCallback).getDataSourceWithVdbDetails(dsName);
        } catch (KomodoUiException e) {
            errorCallback.error(null, e);
        }
    }
    
    public void getVdbXml(String dsName, final IRpcServiceInvocationHandler<String> handler) {
        RemoteCallback<String> successCallback = new DelegatingRemoteCallback<String>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
        	remoteTeiidService.call(successCallback, errorCallback).getVdbXml(dsName);
        } catch (KomodoUiException e) {
            errorCallback.error(null, e);
        }
    }

    public void getDataSourceTypes(final IRpcServiceInvocationHandler<List<String>> handler) {
        RemoteCallback<List<String>> successCallback = new DelegatingRemoteCallback<List<String>>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
        	remoteTeiidService.call(successCallback, errorCallback).getDataSourceTypes();
        } catch (KomodoUiException e) {
            errorCallback.error(null, e);
        }
    }
    
    public void getDataSourceTypeProperties(String dsType, final IRpcServiceInvocationHandler<List<DataSourcePropertyBean>> handler) {
        RemoteCallback<List<DataSourcePropertyBean>> successCallback = new DelegatingRemoteCallback<List<DataSourcePropertyBean>>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
        	remoteTeiidService.call(successCallback, errorCallback).getDataSourceTypeProperties(dsType);
        } catch (KomodoUiException e) {
            errorCallback.error(null, e);
        }
    }
        
    public void getDataSources(final String filter, final String srcVdbPrefix, final IRpcServiceInvocationHandler<List<DataSourcePageRow>> handler) {
        RemoteCallback<List<DataSourcePageRow>> successCallback = new DelegatingRemoteCallback<List<DataSourcePageRow>>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
        	remoteTeiidService.call(successCallback, errorCallback).getDataSources(filter,srcVdbPrefix);
        } catch (KomodoUiException e) {
            errorCallback.error(null, e);
        }
    }
    
    public void getDataSources(final PageRequest request, final String filter, final IRpcServiceInvocationHandler<PageResponse<DataSourcePageRow>> handler) {
        RemoteCallback<PageResponse<DataSourcePageRow>> successCallback = new DelegatingRemoteCallback<PageResponse<DataSourcePageRow>>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
        	remoteTeiidService.call(successCallback, errorCallback).getDataSources(request,filter);
        } catch (KomodoUiException e) {
            errorCallback.error(null, e);
        }
    }
    
    public void getDataSourceNames(final IRpcServiceInvocationHandler<List<String>> handler) {
        RemoteCallback<List<String>> successCallback = new DelegatingRemoteCallback<List<String>>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
        	remoteTeiidService.call(successCallback, errorCallback).getDataSourceNames();
        } catch (KomodoUiException e) {
            errorCallback.error(null, e);
        }
    }
        
    public void getTranslators(final IRpcServiceInvocationHandler<List<String>> handler) {
        RemoteCallback<List<String>> successCallback = new DelegatingRemoteCallback<List<String>>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
        	remoteTeiidService.call(successCallback, errorCallback).getTranslators();
        } catch (KomodoUiException e) {
            errorCallback.error(null, e);
        }
    }
   
    public void getDefaultTranslatorMap(final IRpcServiceInvocationHandler<Map<String,String>> handler) {
        RemoteCallback<Map<String,String>> successCallback = new DelegatingRemoteCallback<Map<String,String>>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
        	remoteTeiidService.call(successCallback, errorCallback).getDefaultTranslatorMap();
        } catch (KomodoUiException e) {
            errorCallback.error(null, e);
        }
    }
    
    public void createDataSourceWithVdb(DataSourceWithVdbDetailsBean dataSourceWithVdb, final IRpcServiceInvocationHandler<Void> handler) {
        RemoteCallback<Void> successCallback = new DelegatingRemoteCallback<Void>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
        	remoteTeiidService.call(successCallback, errorCallback).createDataSourceWithVdb(dataSourceWithVdb);
        } catch (KomodoUiException e) {
            errorCallback.error(null, e);
        }
    }
    
    public void createSourceVdbWithTeiidDS(DataSourceWithVdbDetailsBean dataSourceWithVdb, final IRpcServiceInvocationHandler<Void> handler) {
        RemoteCallback<Void> successCallback = new DelegatingRemoteCallback<Void>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
        	remoteTeiidService.call(successCallback, errorCallback).createSourceVdbWithTeiidDS(dataSourceWithVdb);
        } catch (KomodoUiException e) {
            errorCallback.error(null, e);
        }
    }

    public void deleteDataSourceAndVdb(String dsName, String vdbName, final IRpcServiceInvocationHandler<List<VdbDetailsBean>> handler) {
        RemoteCallback<List<VdbDetailsBean>> successCallback = new DelegatingRemoteCallback<List<VdbDetailsBean>>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
        	remoteTeiidService.call(successCallback, errorCallback).deleteDataSourceAndVdb(dsName,vdbName);
        } catch (KomodoUiException e) {
            errorCallback.error(null, e);
        }
    }
    
    public void deleteDataSources(Collection<String> dsNames, final IRpcServiceInvocationHandler<Void> handler) {
        RemoteCallback<Void> successCallback = new DelegatingRemoteCallback<Void>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
        	remoteTeiidService.call(successCallback, errorCallback).deleteDataSources(dsNames);
        } catch (KomodoUiException e) {
            errorCallback.error(null, e);
        }
    }
    
    public void deleteDataSourcesAndVdb(Collection<String> dsNames, String vdbName, final IRpcServiceInvocationHandler<Void> handler) {
        RemoteCallback<Void> successCallback = new DelegatingRemoteCallback<Void>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
        	remoteTeiidService.call(successCallback, errorCallback).deleteDataSourcesAndVdb(dsNames,vdbName);
        } catch (KomodoUiException e) {
            errorCallback.error(null, e);
        }
    }
    
    public void deleteSourcesAndVdbRedeployRenamed(Collection<String> dsNames, String vdbName, DataSourceWithVdbDetailsBean detailsBean, final IRpcServiceInvocationHandler<Void> handler) {
        RemoteCallback<Void> successCallback = new DelegatingRemoteCallback<Void>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
        	remoteTeiidService.call(successCallback, errorCallback).deleteSourcesAndVdbRedeployRenamed(dsNames,vdbName,detailsBean);
        } catch (KomodoUiException e) {
            errorCallback.error(null, e);
        }
    }
    
    public void deployNewVDB(final String vdbName, final int vdbVersion, final Map<String,String> vdbPropMap, final ViewModelRequestBean viewModelRequest, final IRpcServiceInvocationHandler<VdbDetailsBean> handler) {
        RemoteCallback<VdbDetailsBean> successCallback = new DelegatingRemoteCallback<VdbDetailsBean>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
        	remoteTeiidService.call(successCallback, errorCallback).deployNewVDB(vdbName, vdbVersion, vdbPropMap, viewModelRequest);
        } catch (KomodoUiException e) {
            errorCallback.error(null, e);
        }
    }
    
    public void getDataServiceVdbs(final IRpcServiceInvocationHandler<List<VdbDetailsBean>> handler) {
        RemoteCallback<List<VdbDetailsBean>> successCallback = new DelegatingRemoteCallback<List<VdbDetailsBean>>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
        	remoteTeiidService.call(successCallback, errorCallback).getDataServiceVdbs( );
        } catch (KomodoUiException e) {
            errorCallback.error(null, e);
        }
    }
    
    public void getAllVdbNames(final IRpcServiceInvocationHandler<Collection<String>> handler) {
        RemoteCallback<Collection<String>> successCallback = new DelegatingRemoteCallback<Collection<String>>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
        	remoteTeiidService.call(successCallback, errorCallback).getAllVdbNames();
        } catch (KomodoUiException e) {
            errorCallback.error(null, e);
        }
    }
    
    public void getVdbDetails(String vdbName, final IRpcServiceInvocationHandler<VdbDetailsBean> handler) {
        RemoteCallback<VdbDetailsBean> successCallback = new DelegatingRemoteCallback<VdbDetailsBean>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
        	remoteTeiidService.call(successCallback, errorCallback).getVdbDetails(vdbName);
        } catch (KomodoUiException e) {
            errorCallback.error(null, e);
        }
    }
    
    public void deleteDynamicVdbsWithPrefix(String vdbPrefix, final IRpcServiceInvocationHandler<Void> handler) {
        RemoteCallback<Void> successCallback = new DelegatingRemoteCallback<Void>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
        	remoteTeiidService.call(successCallback, errorCallback).deleteDynamicVdbsWithPrefix(vdbPrefix);
        } catch (KomodoUiException e) {
            errorCallback.error(null, e);
        }
    }   

    public void cloneDynamicVdbAddSource(String vdbName, int vdbVersion, final IRpcServiceInvocationHandler<List<VdbDetailsBean>> handler) {
        RemoteCallback<List<VdbDetailsBean>> successCallback = new DelegatingRemoteCallback<List<VdbDetailsBean>>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
        	remoteTeiidService.call(successCallback, errorCallback).cloneDynamicVdbAddSource(vdbName,vdbVersion);
        } catch (KomodoUiException e) {
            errorCallback.error(null, e);
        }
    }
    
}
