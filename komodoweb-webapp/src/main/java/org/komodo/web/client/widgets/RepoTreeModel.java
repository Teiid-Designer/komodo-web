package org.komodo.web.client.widgets;

import javax.inject.Inject;

import org.komodo.web.share.beans.KomodoObjectBean;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;

/**
 * The {@link TreeViewModel} used to organize contacts into a hierarchy.
 */
public class RepoTreeModel implements TreeViewModel {

	@Inject
	private RepoTreeDataProvider repoTreeProvider;
	
	private final SelectionModel<KomodoObjectBean> selectionModel;
	private final KomodoCell komodoCell = new KomodoCell();

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
			// Return top level nodes.
			return new DefaultNodeInfo<KomodoObjectBean>(repoTreeProvider, komodoCell, selectionModel, null);
		} else if (value instanceof KomodoObjectBean) {
			//AsyncDataProvider<KomodoObjectBean> provider = createDataProvider(nodePath);
			return new DefaultNodeInfo<KomodoObjectBean>(repoTreeProvider, komodoCell, selectionModel, null);
		}

		// Unhandled type.
		String type = value.getClass().getName();
		throw new IllegalArgumentException("Unsupported object type: " + type);
	}

	public boolean isLeaf(Object value) {
		return false;
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
