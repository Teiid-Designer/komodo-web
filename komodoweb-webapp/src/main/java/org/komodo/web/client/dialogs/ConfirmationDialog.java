package org.komodo.web.client.dialogs;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.Button;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.komodo.web.client.messages.ClientMessages;
import org.komodo.web.share.Constants;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * A generic confirmation dialog, used to confirm actions
 */
@Dependent
@WorkbenchPopup(identifier = "ConfirmationDialog")
public class ConfirmationDialog {

	@Inject
    private ClientMessages i18n;
	
	@Inject Event<ConfirmationDialogEvent> buttonEvent;

    @Inject
	private PlaceManager placeManager;
	private PlaceRequest place;
	private final FlowPanel view = new FlowPanel();
	private HTMLPanel messagePanel;
	private Button okButton;
	private Button closeButton;
	private String dialogType;
	private String dialogArg; 
	
	@PostConstruct
	public void setup() {
		messagePanel = new HTMLPanel("<p>Click to close</p>");
		closeButton = new Button(Constants.CANCEL_BUTTON_TXT);
		closeButton.addClickHandler( new ClickHandler() {
			@Override
			public void onClick( final ClickEvent event ) {
				fireCancelEvent();
				placeManager.closePlace( place );
			}
		} );
		okButton = new Button(Constants.OK_BUTTON_TXT);
		okButton.addClickHandler( new ClickHandler() {
			@Override
			public void onClick( final ClickEvent event ) {
				fireOkEvent();
				placeManager.closePlace( place );
			}
		} );
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.add(okButton);
		hPanel.add(closeButton);
		view.add( messagePanel );
		view.add( hPanel );
		view.setHeight("200px");
	}
	
	@OnStartup
	public void onStartup( final PlaceRequest place ) {
		this.place = place;
		
    	// Parameter is passed in for the confirmation dialog type
    	dialogType = place.getParameter(Constants.CONFIRMATION_DIALOG_TYPE_KEY, Constants.NONE);
    	dialogArg = place.getParameter(Constants.CONFIRMATION_DIALOG_ARG_KEY, Constants.NONE);
    	
    	view.remove(messagePanel);
    	String message = getMessage(dialogType);
    	messagePanel = new HTMLPanel("<p>"+message+"</p>");
    	view.insert(messagePanel, 0);
    	
		okButton.setFocus( true );
	}
	
	@WorkbenchPartTitle
	public String getTitle() {
		return getTitle(dialogType);
	}
	
	@WorkbenchPartView
	public IsWidget getView() {
		return view;
	}
	
	/**
	 * Get the dialog title for the specified ConfirmationDialog type
	 * @param dialogType the dialog type
	 * @return the title
	 */
	private String getTitle(String dialogType) {
		String dialogTitle = "Confirm the Operation";
		
		if(dialogType!=null) {
	    	if(Constants.CONFIRMATION_DIALOG_CREATE_VDB.equals(dialogType)) {
	    		dialogTitle = i18n.format("confirmation-dialog.confirm-vdb-create-title");
	    	} else if(Constants.CONFIRMATION_DIALOG_DELETE_VDB.equals(dialogType)) {
	    		dialogTitle = i18n.format("confirmation-dialog.confirm-vdb-delete-title");
			}
		}
		return dialogTitle;
	}
	
	/**
	 * Get the dialog message for the specified ConfirmationDialog type
	 * @param dialogType the dialog type
	 * @return the message
	 */
	private String getMessage(String dialogType) {
		String dialogMessage = "Confirm the Operation";
		
		if(dialogType!=null) {
	    	if(Constants.CONFIRMATION_DIALOG_CREATE_VDB.equals(dialogType)) {
	    		dialogMessage = i18n.format("confirmation-dialog.confirm-vdb-create-msg");
	    	} else if(Constants.CONFIRMATION_DIALOG_DELETE_VDB.equals(dialogType)) {
	    		String vdbName = hasValidArg(dialogArg) ? dialogArg : Constants.UNKNOWN;
	    		dialogMessage = i18n.format("confirmation-dialog.confirm-vdb-delete-msg",vdbName);
			}
		}
		return dialogMessage;
	}
	
	/**
	 * Determine if a valid arg was supplied
	 * @return 'true' if arg was supplied, 'false' if not.
	 */
	private boolean hasValidArg(String dialogArg) {
		if(dialogArg!=null && !dialogArg.equals(Constants.NONE)) {
			return true;
		}
		return false;
	}

	/*
	 * Fires different Cancel events, depending on the type of confirmation
	 */
	private void fireCancelEvent() {
    	if(Constants.CONFIRMATION_DIALOG_CREATE_VDB.equals(dialogType)) {
    		buttonEvent.fire(new ConfirmationDialogEvent(ConfirmationDialogEventType.CREATE_VDB_CANCEL));
    	} else if(Constants.CONFIRMATION_DIALOG_DELETE_VDB.equals(dialogType)) {
    		buttonEvent.fire(new ConfirmationDialogEvent(ConfirmationDialogEventType.DELETE_VDB_CANCEL));
    	}
	}

	/*
	 * Fires different OK events, depending on the type of confirmation
	 */
	private void fireOkEvent() {
    	if(Constants.CONFIRMATION_DIALOG_CREATE_VDB.equals(dialogType)) {
    		buttonEvent.fire(new ConfirmationDialogEvent(ConfirmationDialogEventType.CREATE_VDB_OK));
    	} else if(Constants.CONFIRMATION_DIALOG_DELETE_VDB.equals(dialogType)) {
    		buttonEvent.fire(new ConfirmationDialogEvent(ConfirmationDialogEventType.DELETE_VDB_OK));
    	}
	}

}
