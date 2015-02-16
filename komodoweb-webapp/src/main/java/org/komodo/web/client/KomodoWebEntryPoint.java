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
package org.komodo.web.client;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.BusErrorCallback;
import org.jboss.errai.bus.client.api.ClientMessageBus;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.jboss.errai.ui.shared.api.annotations.Bundle;
import org.komodo.web.client.messages.ClientMessages;
import org.komodo.web.client.services.KomodoRpcService;
import org.komodo.web.client.services.NotificationService;
import org.komodo.web.client.services.rpc.IRpcServiceInvocationHandler;
import org.komodo.web.share.Constants;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ApplicationReadyEvent;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBar;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.Menus;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * GWT's Entry-point for the Komodo WebApp
 */
@EntryPoint
@Bundle("messages/messages.json")
public class KomodoWebEntryPoint {

    @Inject
    private ClientMessages i18n;
    
    @Inject
    private SyncBeanManager manager;

    @Inject
    private WorkbenchMenuBar menubar;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private ActivityManager activityManager;

    @Inject
    private ClientMessageBus bus;

    @Inject
    private Caller<AuthenticationService> authService;
    
    @Inject
    private NotificationService notificationService;
    
    @Inject
    private KomodoRpcService komodoService;
    
    @PostConstruct
    public void startApp() {
    	//StyleInjector.inject(AppResource.INSTANCE.css().rcueCss().getText());
        hideLoadingPopup();
    }

    private void setupMenu( @Observes final ApplicationReadyEvent event ) {
      Menus menus =
        MenuFactory.newTopLevelMenu("Workspace")
         .position(MenuPosition.LEFT)
         .respondsWith(new Command() {
            public void execute() {
               	placeManager.goTo(Constants.KOMODO_WORKSPACE_SCREEN);
            }
         })
        .endMenu()
        .newTopLevelMenu("Logout")
        .position(MenuPosition.RIGHT)
        .respondsWith(new LogoutCommand())
        .endMenu()
      .build();

      menubar.addMenus(menus);
    }

    //Fade out the "Loading application" pop-up
    private void hideLoadingPopup() {
        final Element e = RootPanel.get( "loading" ).getElement();

        new Animation() {

            @Override
            protected void onUpdate( double progress ) {
                e.getStyle().setOpacity( 1.0 - progress );
            }

            @Override
            protected void onComplete() {
                e.getStyle().setVisibility( Style.Visibility.HIDDEN );
            }
        }.run( 500 );
    }

    public static native void redirect( String url )/*-{
        $wnd.location = url;
    }-*/;
    
    private Command makeGoToPlaceCommand(final String placeId) {
      return new Command() {
        @Override
        public void execute() {
          placeManager.goTo(placeId);
        }
      };
    }
    
    private class LogoutCommand implements Command {
    	@Override
    	public void execute() {
    		// Shutdown KEngine
			shutdownKEngine();
    	}
    }
    
    /**
     * Shutdown the KEngine
     */
    protected void shutdownKEngine( ) {
    	komodoService.shutdownKEngine(new IRpcServiceInvocationHandler<Void>() {
    		@Override
    		public void onReturn( Void data ) {
    			authService.call(new RemoteCallback<Void>() {
    				@Override
    				public void callback(Void response) {
    					redirect(GWT.getHostPageBaseURL() + "login.jsp");
    				}
    			}, new BusErrorCallback() {
    				@Override
    				public boolean error(Message message, Throwable throwable) {
    					Window.alert("Logout failed: " + throwable);
    					return true;
    				}
    			}).logout();
    		}
    		@Override
    		public void onError(Throwable error) {
    			notificationService.sendErrorNotification(i18n.format("createdataservice.getvdbnames-error"), error); //$NON-NLS-1$
    		}
    	});
    }
    
}