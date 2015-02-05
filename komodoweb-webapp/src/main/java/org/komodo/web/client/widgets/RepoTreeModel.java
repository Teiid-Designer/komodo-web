package org.komodo.web.client.widgets;

import java.util.ArrayList;
import java.util.List;

import org.komodo.web.share.beans.KomodoObjectBean;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;

/**
 * The {@link TreeViewModel} used to organize contacts into a hierarchy.
 */
public class RepoTreeModel implements TreeViewModel {

  private final SelectionModel<KomodoObjectBean> selectionModel;

  public RepoTreeModel( ) {
	  // defining
	  selectionModel = new SingleSelectionModel<KomodoObjectBean>();

	  // inside TreeViewModel ctor (or in a better place)
	  selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {

		  public void onSelectionChange(SelectionChangeEvent event) {
			  // fire rpc, a place change or something else
			  // event.getSelectedObject() contains the selected element
		  }
	  });
  }

  public <T> NodeInfo<?> getNodeInfo(T value) {
	  
    if (value == null) {
      // Return top level categories.
      return new DefaultNodeInfo<KomodoObjectBean>(createDataProvider(null), new KomodoCell());
      
    } else if (value instanceof KomodoObjectBean) {
      return new DefaultNodeInfo<KomodoObjectBean>(createDataProvider((KomodoObjectBean)value), new KomodoCell());
    }

    // Unhandled type.
    String type = value.getClass().getName();
    throw new IllegalArgumentException("Unsupported object type: " + type);
  }

  public boolean isLeaf(Object value) {
    return value instanceof KomodoCell;
  }
      
  /**
   * Create DataProvider for the KomodoObjectBean
   * @return the data provider
   */
  private AsyncDataProvider<KomodoObjectBean> createDataProvider(final KomodoObjectBean selectedNode) {
	  AsyncDataProvider<KomodoObjectBean> dataProvider = new AsyncDataProvider<KomodoObjectBean>() {
		  protected void onRangeChanged( HasData<KomodoObjectBean> display ) {
			  final Range range = display.getVisibleRange();
		      final int start = range.getStart();
		      List<KomodoObjectBean> result = new ArrayList<KomodoObjectBean>();
		      KomodoObjectBean test = new KomodoObjectBean();
		      test.setName("test");
		      result.add(test);
		      updateRowData(start, result);
		          
//			  queryService.getQueryResults(request, source, sql, new IRpcServiceInvocationHandler<PageResponse<QueryResultPageRow>>() {
//				  @Override
//				  public void onReturn(final PageResponse<QueryResultPageRow> response) {
//					  updateRowCount( response.getTotalRowSize(), response.isTotalRowSizeExact() );
//					  updateRowData( response.getStartRowIndex(), response.getPageRowList() );
//				  }
//				  @Override
//				  public void onError(Throwable error) {
//					  //errorMessage = error.getMessage();
//					  //refreshCompleteEvent.fire(new UiEvent(UiEventType.QUERY_RESULT_DISPLAYER_REFRESHED_ERROR));
//				  }
//			  });
		  }
	  };

	  return dataProvider;
  }
  
  /**
   * The cell used to render categories.
   */
  private static class KomodoCell extends AbstractCell<KomodoObjectBean> {

    /**
     * The html of the image used for contacts.
     */
    private String imageHtml = null;

    public KomodoCell( ) {
    }
    
    public KomodoCell(ImageResource image) {
        this.imageHtml = AbstractImagePrototype.create(image).getHTML();
    }

    @Override
    public void render(Context context, KomodoObjectBean value, SafeHtmlBuilder sb) {
      if (value != null) {
    	if(this.imageHtml!=null) {
    		sb.appendHtmlConstant(imageHtml).appendEscaped(" ");
    	}
        sb.appendEscaped(value.getName());
      }
    }
  }
  
}
