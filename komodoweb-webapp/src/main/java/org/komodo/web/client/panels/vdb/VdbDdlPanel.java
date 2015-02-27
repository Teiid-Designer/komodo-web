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
package org.komodo.web.client.panels.vdb;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.komodo.web.client.services.KomodoRpcService;
import org.komodo.web.client.services.rpc.IRpcServiceInvocationHandler;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;

@Dependent
@Templated("./VdbDdlPanel.html")
public class VdbDdlPanel extends Composite {

    @Inject @DataField("lbl-vdb-ddl-message")
    protected Label messageLabel;
        
    @Inject @DataField("textarea-ddl")
    protected TextArea ddlTextArea;
    
    /**
     * Called after construction.
     */
    @PostConstruct
    protected void postConstruct() {
    	messageLabel.setText("The generated DDL for your VDB");
    }
    
    public void setVdbPath(String vdbPath) {
		KomodoRpcService.get().getVdbDDL(vdbPath, new IRpcServiceInvocationHandler<String>() {
			@Override
			public void onReturn(final String result) {	
				ddlTextArea.setText(result);
			}
			@Override
			public void onError(Throwable error) {
				Window.alert("error - "+error.getMessage());
				//errorMessage = error.getMessage();
				//refreshCompleteEvent.fire(new UiEvent(UiEventType.QUERY_RESULT_DISPLAYER_REFRESHED_ERROR));
			}
		});
    }
        
}