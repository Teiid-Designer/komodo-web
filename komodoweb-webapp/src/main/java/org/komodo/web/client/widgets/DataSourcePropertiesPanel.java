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
package org.komodo.web.client.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.komodo.web.client.dialogs.UiEvent;
import org.komodo.web.client.dialogs.UiEventType;
import org.komodo.web.client.messages.ClientMessages;
import org.komodo.web.client.resources.AppResource;
import org.komodo.web.client.resources.ImageHelper;
import org.komodo.web.client.services.ApplicationStateKeys;
import org.komodo.web.client.services.ApplicationStateService;
import org.komodo.web.client.services.NotificationService;
import org.komodo.web.client.services.TeiidRpcService;
import org.komodo.web.client.services.rpc.IRpcServiceInvocationHandler;
import org.komodo.web.client.utils.UiUtils;
import org.komodo.web.share.Constants;
import org.komodo.web.share.TranslatorHelper;
import org.komodo.web.share.beans.DataSourcePropertyBean;
import org.komodo.web.share.beans.DataSourceWithVdbDetailsBean;
import org.komodo.web.share.beans.NotificationBean;
import org.komodo.web.share.beans.PropertyBeanComparator;
import org.komodo.web.share.services.StringUtils;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

@Dependent
@Templated("./DataSourcePropertiesPanel.html")
public class DataSourcePropertiesPanel extends Composite {

    @Inject
    private PlaceManager placeManager;
    @Inject
    private ClientMessages i18n;
    @Inject
    private NotificationService notificationService;
    @Inject
    private ApplicationStateService stateService;
    
	private String statusEnterName = null;
	private String statusSelectTrans = null;
	private String statusClickSave = null;
	private String statusEnterProps = null;
	private String externalError = null;
	
	private String clickedSourceType;
    
	// List of all available translators
	private List<String> allTranslators = new ArrayList<String>();
	
	// Map of server sourceName to corresponding default translator
	private Map<String,String> defaultTranslatorMap = new HashMap<String,String>();
	// Current properties
    private List<DataSourcePropertyBean> currentPropList = new ArrayList<DataSourcePropertyBean>();
	// The type of the current source
    private String selectedSourceType;
    // List of Data Source type Toggle Buttons
    private List<ToggleButton> dsTypeButtons = new ArrayList<ToggleButton>();
    // Button for adding new type
    private ToggleButton addTypeButton;
    
    // Keeps track of original type, name and translator, before any edits are made
    private String originalType;
    private String originalName;
    private String originalTranslator;
    
    @Inject
    protected TeiidRpcService teiidService;
    
    @Inject @DataField("label-dsprops-title")
    protected Label dsDetailsPanelTitle;
    
    @Inject @DataField("label-dsprops-status")
    protected Label statusLabel;
    
    @Inject @DataField("textbox-dsprops-name")
    protected TextBox nameTextBox;
    @Inject @DataField("dtypes-button-panel")
    protected FlowPanel dTypesButtonPanel;
    
    @Inject @DataField("listbox-dsprops-translator")
    protected ListBox translatorListBox;
    @Inject @DataField("editor-dsprops-core-properties")
    protected DataSourcePropertyEditor dataSourceCorePropertyEditor;
    @Inject @DataField("editor-dsprops-adv-properties")
    protected DataSourcePropertyEditor dataSourceAdvancedPropertyEditor;
    
    @Inject @DataField("btn-dsprops-save1")
    protected Button saveSourceChanges1;
    
    @Inject @DataField("btn-dsprops-save2")
    protected Button saveSourceChanges2;
    
    @Inject Event<UiEvent> statusEvent;
    
    /**
     * Called after construction.
     */
    @PostConstruct
    protected void postConstruct() {
    	AppResource.INSTANCE.css().customToggleStyle().ensureInjected();
    	
    	dsDetailsPanelTitle.setText("Data Source: [New Source]");
    	
		statusEnterName = i18n.format("ds-properties-panel.status-enter-name");
		statusSelectTrans = i18n.format("ds-properties-panel.status-select-translator");
		statusClickSave = i18n.format("ds-properties-panel.status-click-save");
		statusEnterProps = i18n.format("ds-properties-panel.status-enter-props");
		
    	doPopulateSourceTypesPanel(null);
    	
    	doPopulateTranslatorListBox();
    	
    	dataSourceCorePropertyEditor.clear();
    	dataSourceAdvancedPropertyEditor.clear();
    	
    	nameTextBox.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                updateStatus();
            }
        });
        // Change Listener for Type ListBox
        translatorListBox.addChangeHandler(new ChangeHandler()
        {
        	// Changing the Type selection will re-populate property table with defaults for that type
        	public void onChange(ChangeEvent event)
        	{
                updateStatus();
        	}
        });
        updateStatus();
        
        // Tooltips
        saveSourceChanges1.setTitle(i18n.format("ds-properties-panel.saveSourceChanges1.tooltip"));
        saveSourceChanges2.setTitle(i18n.format("ds-properties-panel.saveSourceChanges2.tooltip"));
        nameTextBox.setTitle(i18n.format("ds-properties-panel.nameTextBox.tooltip"));
        translatorListBox.setTitle(i18n.format("ds-properties-panel.translatorListBox.tooltip"));
    }
    
    /**
     * Event from properties table when property is edited
     * @param propertyName
     */
    public void onPropertyChanged(@Observes DataSourcePropertyBean propertyBean) {
    	updateStatus();
    }
    
    /**
     * Event handler that fires when the user clicks the top save button.
     * @param event
     */
    @EventHandler("btn-dsprops-save1")
    public void onSaveChangesTopButtonClick(ClickEvent event) {
    	saveChangesButtonClick(event);
    }
    
    /**
     * Event handler that fires when the user clicks the top save button.
     * @param event
     */
    @EventHandler("btn-dsprops-save2")
    public void onSaveChangesBottomButtonClick(ClickEvent event) {
    	saveChangesButtonClick(event);
    }
    
    private void saveChangesButtonClick(ClickEvent event) {
        // Only the translator changed.  No need to muck with DS - just redeploy VDB and its source
        if(!hasNameChange() && !hasPropertyChanges() && !hasDataSourceTypeChange() && hasTranslatorChange()) {
            DataSourceWithVdbDetailsBean sourceBean = getDetailsBean();
            doCreateSourceVdbWithTeiidDS(sourceBean);
        // No name change
        } else if(!hasNameChange()) {
        	showConfirmSourceRedeployDialog();
        // Name change - confirm the rename first
        } else {
        	showConfirmRenameDialog();
        }
    }
    
    /**
     * Shows the confirmation dialog for renaming a DataSource
     */
    private void showConfirmRenameDialog() {
		// Display the Confirmation Dialog for source rename
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put(Constants.CONFIRMATION_DIALOG_MESSAGE, i18n.format("ds-properties-panel.confirm-rename-dialog-message"));
		parameters.put(Constants.CONFIRMATION_DIALOG_TYPE, Constants.CONFIRMATION_DIALOG_SOURCE_RENAME);
    	placeManager.goTo(new DefaultPlaceRequest(Constants.CONFIRMATION_DIALOG,parameters));
    }
    
    /**
     * Shows the confirmation dialog for redeploy of a DataSource
     */
    private void showConfirmSourceRedeployDialog() {
		// Display the Confirmation Dialog for source redeploy
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put(Constants.CONFIRMATION_DIALOG_MESSAGE, i18n.format("ds-properties-panel.confirm-redeploy-dialog-message"));
		parameters.put(Constants.CONFIRMATION_DIALOG_TYPE, Constants.CONFIRMATION_DIALOG_SOURCE_REDEPLOY);
    	placeManager.goTo(new DefaultPlaceRequest(Constants.CONFIRMATION_DIALOG,parameters));    	
    }
    
    /**
     * Shows the confirmation dialog for changing a DataSource type
     */
    private void showConfirmChangeTypeDialog() {
		// Display the Confirmation Dialog for source type change
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put(Constants.CONFIRMATION_DIALOG_MESSAGE, i18n.format("ds-properties-panel.confirm-changetype-dialog-message"));
		parameters.put(Constants.CONFIRMATION_DIALOG_TYPE, Constants.CONFIRMATION_DIALOG_SOURCE_CHANGETYPE);
    	placeManager.goTo(new DefaultPlaceRequest(Constants.CONFIRMATION_DIALOG,parameters));    	
    }
    
    /**
     * Handles UiEvents
     * @param dEvent
     */
    public void onDialogEvent(@Observes UiEvent dEvent) {
    	// User has OK'd source rename
    	if(dEvent.getType() == UiEventType.SOURCE_RENAME_OK) {
    		onRenameConfirmed();
    	// User has OK'd source redeploy
    	} else if(dEvent.getType() == UiEventType.SOURCE_REDEPLOY_OK) {
    		onRedeployConfirmed();
    	// User has OK'd source type change
    	} else if(dEvent.getType() == UiEventType.SOURCE_CHANGETYPE_OK) {
    		onChangeTypeConfirmed();
    		setSelectedDataSourceType(this.selectedSourceType);
    	// User has cancelled source rename
    	} else if(dEvent.getType() == UiEventType.SOURCE_RENAME_CANCEL) {
    	// User has cancelled source redeploy
    	} else if(dEvent.getType() == UiEventType.SOURCE_REDEPLOY_CANCEL) {
    	// User has cancelled source type change
    	} else if(dEvent.getType() == UiEventType.SOURCE_CHANGETYPE_CANCEL) {
    		setSelectedDataSourceType(this.selectedSourceType);
    	} else if(dEvent.getType() == UiEventType.UPLOAD_DRIVER_COMPLETE) {
        	doPopulateSourceTypesPanel(selectedSourceType);
    	} 
    }
    
    private void onRenameConfirmed() {
        DataSourceWithVdbDetailsBean sourceBean = getDetailsBean();
        
        // DataSources to delete - server source and Vdb source.
    	List<String> originalDsNames = new ArrayList<String>();
    	originalDsNames.add(this.originalName);
    	originalDsNames.add(Constants.SERVICE_SOURCE_VDB_PREFIX+this.originalName);
    	
    	// Also must delete the Source Vdb
    	String srcVdbName = Constants.SERVICE_SOURCE_VDB_PREFIX+this.originalName;
    	
    	doDeleteThenCreateDataSource(originalDsNames,srcVdbName,sourceBean);
    }
    
    private void onRedeployConfirmed() {
        DataSourceWithVdbDetailsBean sourceBean = getDetailsBean();
    	doCreateDataSource(sourceBean);
    }
    
    private void onChangeTypeConfirmed() {
		// Get default translator for the selected type
    	String sourceType = clickedSourceType;
		String defaultTranslator = TranslatorHelper.getTranslator(sourceType, allTranslators);
		setSelectedTranslator(defaultTranslator);

		doPopulatePropertiesTable(sourceType);
		selectedSourceType = sourceType;
    }
    
    private DataSourceWithVdbDetailsBean getDetailsBean() {
    	DataSourceWithVdbDetailsBean resultBean = new DataSourceWithVdbDetailsBean();
    	String dsName = nameTextBox.getText();
    	resultBean.setName(dsName);
    	resultBean.setType(this.selectedSourceType);
    	resultBean.setTranslator(getSelectedTranslator());
    	resultBean.setSourceVdbName(Constants.SERVICE_SOURCE_VDB_PREFIX+dsName);

    	List<DataSourcePropertyBean> props = new ArrayList<DataSourcePropertyBean>();
    	List<DataSourcePropertyBean> coreProps = dataSourceCorePropertyEditor.getBeansWithRequiredOrNonDefaultValue();
    	List<DataSourcePropertyBean> advancedProps = dataSourceAdvancedPropertyEditor.getBeansWithRequiredOrNonDefaultValue();
    	props.addAll(coreProps);
    	props.addAll(advancedProps);

    	resultBean.setProperties(props);
    	return resultBean;
    }
    
    /**
     * Populate the Data Source Types Panel
     */
    protected void doPopulateSourceTypesPanel(final String selectedType) {
    	teiidService.getDataSourceTypes(new IRpcServiceInvocationHandler<List<String>>() {
            @Override
            public void onReturn(List<String> dsTypes) {
            	dTypesButtonPanel.clear();
            	dsTypeButtons.clear();
            	// Generates toggle buttons for each type
                for(String dType : dsTypes) {
                	ImageResource img = ImageHelper.getInstance().getDataSourceImageForType(dType);
                	Image buttonImage = null;
                	if(img!=null) {
                		buttonImage = new Image(img);
                	}
                	ToggleButton button;
                	if(!ImageHelper.getInstance().hasKnownImage(dType)) {
                    	button = new ToggleButton(dType,dType);
                	} else {
                    	button = new ToggleButton(buttonImage);
                	}
            		button.setStylePrimaryName("customToggle");
                	button.getElement().setId(dType);
                	button.addClickHandler(new ClickHandler() {
                		public void onClick(ClickEvent event) {
            				Widget sourceWidget = (Widget)event.getSource();
            				String sourceType = sourceWidget.getElement().getId();
            				
            				clickedSourceType = sourceType;
            				showConfirmChangeTypeDialog();
                		}
                	});                	
                	DOM.setStyleAttribute(button.getElement(), "cssFloat", "left");
                	DOM.setStyleAttribute(button.getElement(), "margin", "5px");
                	DOM.setStyleAttribute(button.getElement(), "padding", "0px");
                	dTypesButtonPanel.add(button);
                	dsTypeButtons.add(button);
                }
                
                // Add button for AddType
                ImageResource addTypeImg = AppResource.INSTANCE.images().dsType_addtype_Image();
                final ToggleButton addTypeButton = new ToggleButton(new Image(addTypeImg));
            	addTypeButton.setStylePrimaryName("customToggle");
                addTypeButton.addClickHandler(new ClickHandler() {
            		public void onClick(ClickEvent event) {
            			addTypeButton.setValue(false);
            			placeManager.goTo(Constants.UPLOAD_DRIVER_DIALOG);
            		}
            	});                	
            	DOM.setStyleAttribute(addTypeButton.getElement(), "cssFloat", "left");
            	DOM.setStyleAttribute(addTypeButton.getElement(), "margin", "5px");
            	DOM.setStyleAttribute(addTypeButton.getElement(), "padding", "0px");
                addTypeButton.setTitle(i18n.format("ds-properties-panel.addTypeButton.tooltip"));
            	dTypesButtonPanel.add(addTypeButton);
            	
            	if(selectedType!=null) setSelectedDataSourceType(selectedType);
            }
            @Override
            public void onError(Throwable error) {
                notificationService.sendErrorNotification(i18n.format("ds-properties-panel.error-populating-dstypes"), error); //$NON-NLS-1$
            }
        });
    }
    
    /**
     * Gets the selected Data Source type
     * @return the currently selected type
     */
    public String getSelectedDataSourceType( ) {
    	return this.selectedSourceType;
    }
    
    /**
     * Sets the selected Data Source type button to the down position.
     * This method does not fire an event, just changes toggle position
     * @param dsType the data source type
     */
    public void setSelectedDataSourceType(String dsType) {
    	if(dsType!=null) {
    		// First de-select all to clear current toggle
    		for(ToggleButton tButton : dsTypeButtons) {
   				tButton.setValue(false);
    		}
    		// Also make sure addType button is unToggled
    		if(this.addTypeButton!=null) {
    			this.addTypeButton.setValue(false);
    		}
    		
    		// Set new button toggle state down
    		for(ToggleButton tButton : dsTypeButtons) {
    			String buttonId = tButton.getElement().getId();
    			if(buttonId.equals(dsType)) {
    				tButton.setValue(true);
    				this.selectedSourceType = dsType;
    			}
    		}
    	}
    }
        
    /**
     * Populate the Data Source Type ListBox
     */
    protected void doPopulateTranslatorListBox() {
    	teiidService.getTranslators(new IRpcServiceInvocationHandler<List<String>>() {
            @Override
            public void onReturn(List<String> translators) {
            	allTranslators.clear();
            	allTranslators.addAll(translators);
                populateTranslatorListBox(translators);
            }
            @Override
            public void onError(Throwable error) {
                notificationService.sendErrorNotification(i18n.format("ds-properties-panel.error-populating-translators"), error); //$NON-NLS-1$
            }
        });
    }

    private void populateTranslatorListBox(List<String> translators) {
    	// Make sure clear first
    	translatorListBox.clear();

    	translatorListBox.insertItem(Constants.NO_TRANSLATOR_SELECTION, 0);
    	
    	// Repopulate the ListBox. The actual names 
    	int i = 1;
    	for(String translatorName: translators) {
    		translatorListBox.insertItem(translatorName, i);
    		i++;
    	}

    	// Initialize by setting the selection to the first item.
    	translatorListBox.setSelectedIndex(0);
    }
    
    /**
     * Get the selected translator from the translator dropdown
     * @return
     */
    public String getSelectedTranslator() {
    	int index = translatorListBox.getSelectedIndex();
    	return translatorListBox.getValue(index);
    }
    
	public void setSelectedTranslator(String translatorName) {
		int indx = 0;
		int nItems = translatorListBox.getItemCount();
		for(int i=0; i<nItems; i++) {
			String itemText = translatorListBox.getItemText(i);
			if(itemText.equalsIgnoreCase(translatorName)) {
				indx = i;
				break;
			}
		}
		translatorListBox.setSelectedIndex(indx);
	}
	
	public void selectTranslatorForSource(String sourceName) {
		String translator = this.defaultTranslatorMap.get(sourceName);
		if(!StringUtils.isEmpty(translator)) {
			setSelectedTranslator(translator);
		} else {
			setSelectedTranslator(Constants.NO_TRANSLATOR_SELECTION);
		}
	}

	public void setDefaultTranslatorMappings(Map<String,String> defaultTranslatorMap) {
		this.defaultTranslatorMap.clear();
		this.defaultTranslatorMap.putAll(defaultTranslatorMap);
	}

    /**
     * Populate the properties table for the supplied Source Type
     * @param selectedType the selected SourceType
     */
    protected void doPopulatePropertiesTable(String selectedType) {
        if(selectedType.equals(Constants.NO_TYPE_SELECTION)) {
        	dataSourceCorePropertyEditor.clear();
        	dataSourceAdvancedPropertyEditor.clear();
        	return;
        }

        teiidService.getDataSourceTypeProperties(selectedType, new IRpcServiceInvocationHandler<List<DataSourcePropertyBean>>() {
            @Override
            public void onReturn(List<DataSourcePropertyBean> propList) {
            	currentPropList.clear();
            	currentPropList.addAll(propList);
                populateCorePropertiesTable();
                populateAdvancedPropertiesTable();
                //updateDialogStatus();
            }
            @Override
            public void onError(Throwable error) {
                notificationService.sendErrorNotification(i18n.format("ds-properties-panel.error-populating-properties"), error); //$NON-NLS-1$
            }
        });
    }
    
    /**
     * Create a VDB and corresponding teiid source.  Used when there are no changes to the underlying source.
     * @param dsDetailsBean the data source details
     */
    private void doCreateSourceVdbWithTeiidDS(final DataSourceWithVdbDetailsBean detailsBean) {
    	final String dsName = detailsBean.getName();
        final NotificationBean notificationBean = notificationService.startProgressNotification(
                i18n.format("ds-properties-panel.creating-vdbwsource-title"), //$NON-NLS-1$
                i18n.format("ds-properties-panel.creating-vdbwsource-msg", dsName)); //$NON-NLS-1$
    	
        // fire event
        fireStatusEvent(UiEventType.DATA_SOURCE_DEPLOY_STARTING,dsName,null);

        teiidService.createSourceVdbWithTeiidDS(detailsBean, new IRpcServiceInvocationHandler<Void>() {
            @Override
            public void onReturn(Void data) {
                notificationService.completeProgressNotification(notificationBean.getUuid(),
                        i18n.format("ds-properties-panel.vdbwsource-created"), //$NON-NLS-1$
                        i18n.format("ds-properties-panel.create-vdbwsource-success-msg")); //$NON-NLS-1$

            	// fire event
                fireStatusEvent(UiEventType.DATA_SOURCE_DEPLOY_SUCCESS,dsName,null);
            }
            @Override
            public void onError(Throwable error) {
                notificationService.completeProgressNotification(notificationBean.getUuid(),
                        i18n.format("ds-properties-panel.create-vdbwsource-error"), //$NON-NLS-1$
                        error);
                
            	// fire event
                fireStatusEvent(UiEventType.DATA_SOURCE_DEPLOY_FAIL,dsName,null);
            }
        });
    }
    
    /**
     * Creates a DataSource
     * @param dsDetailsBean the data source details
     */
    private void doCreateDataSource(final DataSourceWithVdbDetailsBean detailsBean) {
    	final String dsName = detailsBean.getName();
        final NotificationBean notificationBean = notificationService.startProgressNotification(
                i18n.format("ds-properties-panel.creating-datasource-title"), //$NON-NLS-1$
                i18n.format("ds-properties-panel.creating-datasource-msg", dsName)); //$NON-NLS-1$

        // fire event
        fireStatusEvent(UiEventType.DATA_SOURCE_DEPLOY_STARTING,dsName,null);

        teiidService.createDataSourceWithVdb(detailsBean, new IRpcServiceInvocationHandler<Void>() {
            @Override
            public void onReturn(Void data) {
                notificationService.completeProgressNotification(notificationBean.getUuid(),
                        i18n.format("ds-properties-panel.datasource-created"), //$NON-NLS-1$
                        i18n.format("ds-properties-panel.create-success-msg")); //$NON-NLS-1$

            	// fire event
                fireStatusEvent(UiEventType.DATA_SOURCE_DEPLOY_SUCCESS,dsName,null);
            }
            @Override
            public void onError(Throwable error) {
                notificationService.completeProgressNotification(notificationBean.getUuid(),
                        i18n.format("ds-properties-panel.create-error"), //$NON-NLS-1$
                        error);
                
            	// fire event
                fireStatusEvent(UiEventType.DATA_SOURCE_DEPLOY_FAIL,dsName,null);
            }
        });
    }
    
    /**
     * Fire status event for a dataSource
     * @param eventType the type of event
     * @param dataSourceName the datasource name
     */
    private void fireStatusEvent(UiEventType eventType, String dataSourceName, String message) {
		UiEvent uiEvent = new UiEvent(eventType);
		uiEvent.setDataSourceName(dataSourceName);
		statusEvent.fire(uiEvent);
    }
    
    /**
     * Called when the user confirms the dataSource deletion.
     */
    private void doDeleteThenCreateDataSource(final List<String> dsNamesToDelete, final String srcVdbName, final DataSourceWithVdbDetailsBean detailsBean) {
        final NotificationBean notificationBean = notificationService.startProgressNotification(
                i18n.format("ds-properties-panel.creating-datasource-title"), //$NON-NLS-1$
                i18n.format("ds-properties-panel.creating-datasource-msg")); //$NON-NLS-1$
        
        // fire event to show in progress
        fireStatusEvent(UiEventType.DATA_SOURCE_DEPLOY_STARTING,detailsBean.getName(),null);

        teiidService.deleteSourcesAndVdbRedeployRenamed(dsNamesToDelete, srcVdbName, detailsBean, new IRpcServiceInvocationHandler<Void>() {
            @Override
            public void onReturn(Void data) {
                notificationService.completeProgressNotification(notificationBean.getUuid(),
                        i18n.format("ds-properties-panel.datasource-created"), //$NON-NLS-1$
                        i18n.format("ds-properties-panel.create-success-msg")); //$NON-NLS-1$
                
                // if the in-process edit contains the renamed source, update the state service accordingly
                List<String> updatedViewSrcs = new ArrayList<String>();
            	@SuppressWarnings("unchecked")
        		List<String> svcViewSrcs = (List<String>)stateService.get(ApplicationStateKeys.IN_PROGRESS_SVC_VIEW_SRCS);
            	for(String svcViewSrc : svcViewSrcs) {
            		if(svcViewSrc.equals(originalName)) {
            			updatedViewSrcs.add(detailsBean.getName());
            		} else {
            			updatedViewSrcs.add(svcViewSrc);
            		}
            	}
            	stateService.put(ApplicationStateKeys.IN_PROGRESS_SVC_VIEW_SRCS, updatedViewSrcs);

            	// fire event
                fireStatusEvent(UiEventType.DATA_SOURCE_DEPLOY_SUCCESS,detailsBean.getName(),null);
            }
            @Override
            public void onError(Throwable error) {
                notificationService.completeProgressNotification(notificationBean.getUuid(),
                        i18n.format("ds-properties-panel.create-error"), //$NON-NLS-1$
                        error);
            	// fire event
                fireStatusEvent(UiEventType.DATA_SOURCE_DEPLOY_FAIL,detailsBean.getName(),null);
            }
        });
    }
    
    /*
     * Populate the core properties table
     * @param dsDetailsBean the Data Source details
     */
    private void populateCorePropertiesTable( ) {
    	dataSourceCorePropertyEditor.clear();

    	List<DataSourcePropertyBean> corePropList = getPropList(this.currentPropList, true, true);
    	dataSourceCorePropertyEditor.setProperties(corePropList);
    }

    /*
     * Populate the advanced properties table
     * @param dsDetailsBean the Data Source details
     */
    private void populateAdvancedPropertiesTable() {
    	dataSourceAdvancedPropertyEditor.clear();

    	List<DataSourcePropertyBean> advPropList = getPropList(this.currentPropList, false, true);
    	dataSourceAdvancedPropertyEditor.setProperties(advPropList);
    }
    
    /*
     * Filters the supplied list by correct type and order
     * @param propList the complete list of properties
     * @param getCore if 'true', returns the core properties.  if 'false' returns the advanced properties
     * @param acending if 'true', sorts in ascending name order.  descending if 'false'
     */
    private List<DataSourcePropertyBean> getPropList(List<DataSourcePropertyBean> propList, boolean getCore, boolean ascending) {
    	List<DataSourcePropertyBean> resultList = new ArrayList<DataSourcePropertyBean>();
    	
    	// Put only the desired property type into the resultList
    	for(DataSourcePropertyBean prop : propList) {
    		if(prop.isCoreProperty() && getCore) {
    			resultList.add(prop);
    		} else if(!prop.isCoreProperty() && !getCore) {
    			resultList.add(prop);    			
    		}
    	}
    	
    	// Sort by name in the desired order
    	Collections.sort(resultList,new PropertyBeanComparator(ascending));
    	
    	return resultList;
    }
    
    public void setDataSource(String dsName) {
    	doGetDataSourceDetails(dsName);
    }
    
    /**
     * Get the Data Source With Source VDB details for the current Data Source name.
     * Populate the PropertiesPanel with it's properties
     * @param dataSourceName the data source name
     */
    protected void doGetDataSourceDetails(String dataSourceName) {

    	teiidService.getDataSourceWithVdbDetails(dataSourceName, new IRpcServiceInvocationHandler<DataSourceWithVdbDetailsBean>() {
            @Override
            public void onReturn(DataSourceWithVdbDetailsBean dsDetailsBean) {
            	String title = "Data Source : "+dsDetailsBean.getName();
            	dsDetailsPanelTitle.setText(title);
            	
            	nameTextBox.setText(dsDetailsBean.getName());
            	originalName = dsDetailsBean.getName();
            	
            	setSelectedDataSourceType(dsDetailsBean.getType());
            	originalType = dsDetailsBean.getType();
            	
            	String translator = dsDetailsBean.getTranslator();
            	if(!StringUtils.isEmpty(translator)) {
            		setSelectedTranslator(translator);
            		originalTranslator=translator;
            	} else {
        			setSelectedTranslator(Constants.NO_TRANSLATOR_SELECTION);
        			originalTranslator=Constants.NO_TRANSLATOR_SELECTION;
            	}

            	currentPropList.clear();
            	List<DataSourcePropertyBean> props = dsDetailsBean.getProperties();
            	currentPropList.addAll(props);
            	populateCorePropertiesTable();
            	populateAdvancedPropertiesTable();
            	
            	updateStatus();
            }
            @Override
            public void onError(Throwable error) {
                notificationService.sendErrorNotification(i18n.format("ds-properties-panel.details-fetch-error"), error); //$NON-NLS-1$
            }
        });       
        
    }
    
    private void updateStatus( ) {
    	String statusText = Constants.OK;
    	
    	// Warn for missing source name
    	String serviceName = nameTextBox.getText();
    	if(StringUtils.isEmpty(serviceName)) {
    		statusText = statusEnterName;
    	}
    	
		// Warn for translator not selected
    	if(statusText.equals(Constants.OK)) {
        	String translator = getSelectedTranslator();
    		if(translator!=null && translator.equals(Constants.NO_TRANSLATOR_SELECTION)) {
    			statusText = statusSelectTrans;
    		}
    	}
    	
		// Get properties message
    	if(statusText.equals(Constants.OK)) {
        	statusText = getPropertyStatus();
    	}
    	
    	// Determine if any properties were changed
    	if(statusText.equals(Constants.OK)) {
    		boolean hasNameChange = hasNameChange();
    		boolean hasTypeChange = hasDataSourceTypeChange();
    		boolean hasTranslatorChange = hasTranslatorChange();
        	boolean hasPropChanges = hasPropertyChanges();
    		if(hasNameChange || hasTypeChange || hasTranslatorChange || hasPropChanges) {
    			setInfoMessage(statusClickSave);
        		saveSourceChanges1.setEnabled(true);
        		saveSourceChanges2.setEnabled(true);
    		} else {
    			// External error was set - show it
    			if(!StringUtils.isEmpty(this.externalError)) {
    				setErrorMessage(this.externalError);
    			// Show standard 'enter props' message
    			} else {
        			setInfoMessage(statusEnterProps);
    			}
        		saveSourceChanges1.setEnabled(false);
        		saveSourceChanges2.setEnabled(false);
    		}
    	} else {
			setErrorMessage(statusText);
    		saveSourceChanges1.setEnabled(false);
    		saveSourceChanges2.setEnabled(false);
    	}
    }
    
    /**
     * Set the status info message
     */
    private void setInfoMessage(String statusMsg) {
    	statusLabel.setText(statusMsg);
    	UiUtils.setMessageStyle(statusLabel, UiUtils.MessageType.INFO);
    }
    
    /**
     * Allows externally setting an error prefix.  If error is set,
     * then it is shown as a standard error message instead of standard info message
     */
    public void setExternalError(String externalError) {
    	this.externalError = externalError;
    }
    
    /**
     * Set the status error message
     */
    private void setErrorMessage(String statusMsg) {
    	statusLabel.setText(statusMsg);
    	UiUtils.setMessageStyle(statusLabel, UiUtils.MessageType.ERROR);
    }
    
    /**
     * Returns 'true' if the name has changed, false if not
     * @return name changed status
     */
    private boolean hasNameChange() {
    	return !StringUtils.valuesAreEqual(this.originalName, this.nameTextBox.getText());
    }
    
    /**
     * Returns 'true' if the type has changed, false if not
     * @return type changed status
     */
    private boolean hasDataSourceTypeChange() {
    	return !StringUtils.valuesAreEqual(this.originalType, getSelectedDataSourceType());
    }
    
    /**
     * Returns 'true' if the translator has changed, false if not
     * @return translator changed status
     */
    private boolean hasTranslatorChange() {
    	return !StringUtils.valuesAreEqual(this.originalTranslator, getSelectedTranslator());
    }
    
    /**
     * Returns 'true' if any properties have changed, false if no changes
     * @return property changed status
     */
    private boolean hasPropertyChanges() {
    	boolean coreTableHasChanges = this.dataSourceCorePropertyEditor.anyPropertyHasChanged();
    	boolean advTableHasChanges = this.dataSourceAdvancedPropertyEditor.anyPropertyHasChanged();
    	
    	return (coreTableHasChanges || advTableHasChanges) ? true : false;
    }
    
    private String getPropertyStatus() {
    	// ------------------
    	// Set status message
    	// ------------------
    	String propStatus = this.dataSourceCorePropertyEditor.getStatus();
    	if(propStatus.equalsIgnoreCase(Constants.OK)) {
    		propStatus = this.dataSourceAdvancedPropertyEditor.getStatus();
    	}

    	return propStatus;
    }
    
}