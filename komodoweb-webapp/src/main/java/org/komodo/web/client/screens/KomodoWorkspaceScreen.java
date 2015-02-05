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
package org.komodo.web.client.screens;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.komodo.web.client.messages.ClientMessages;
import org.komodo.web.client.services.KomodoRpcService;
import org.komodo.web.client.services.NotificationService;
import org.komodo.web.client.services.rpc.IRpcServiceInvocationHandler;
import org.komodo.web.share.Constants;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.TextBox;

/**
 * KomodoWorkspaceScreen - used for Komodo Workspace demo
 *
 */
@Dependent
@Templated("./KomodoWorkspaceScreen.html#page")
@WorkbenchScreen(identifier = "KomodoWorkspaceScreen")
public class KomodoWorkspaceScreen extends Composite {

    @Inject
    private ClientMessages i18n;

    @Inject
    private NotificationService notificationService;
    
    @Inject
    private KomodoRpcService komodoService;
        
    @Inject @DataField("textbox-komodo-workspace-name")
    protected TextBox workspaceNameTextBox;
        
    @Override
    @WorkbenchPartTitle
    public String getTitle() {
      return Constants.BLANK;
    }
    
    @WorkbenchPartView
    public IsWidget getView() {
        return this;
    }
    
    /**
     * Called after construction.
     */
    @PostConstruct
    protected void postConstruct() {
    	
    	// Tooltips
    	workspaceNameTextBox.setTitle(i18n.format("komodoworkspace-screen.workspaceNameTextBox.tooltip"));
    }
    
    @OnStartup
    public void onStartup( final PlaceRequest place ) {   	    	
        
    	// Start the KEngine
    	startKEngine();
    }
    
    /**
     * Populate list of all current VDB names
     */
    protected void startKEngine( ) {
    	komodoService.startKEngine(new IRpcServiceInvocationHandler<Void>() {
    		@Override
    		public void onReturn( Void data ) {
    		}
    		@Override
    		public void onError(Throwable error) {
                notificationService.sendErrorNotification(i18n.format("komodoworkspace-screen.startengine-error"), error); //$NON-NLS-1$
    		}
    	});
    }
    
        
}
