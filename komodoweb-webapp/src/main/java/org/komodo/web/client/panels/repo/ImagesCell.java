/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.web.client.panels.repo;

import org.komodo.web.client.resources.AppResource;

import com.google.gwt.cell.client.AbstractSafeHtmlCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * 
 * ImagesCell - contains multiple clickable images
 * 
 */

public class ImagesCell extends AbstractSafeHtmlCell<String> {

	public static final String ADD_VDB = "ADD_VDB";
	public static final String EDIT_VDB = "EDIT_VDB";
	public static final String DELETE_VDB = "DELETE_VDB";

	/**
	 * The HTML templates used to render the cell.
	 */
	interface Templates extends SafeHtmlTemplates {

		/**
		 * 
		 * The template for this Cell, which includes styles and a value.
		 * @param styles the styles to include in the style attribute of the div
		 * @param value the safe value. Since the value type is {@link SafeHtml},
		 *            it will not be escaped before including it in the
		 *            template. Alternatively, you could make the value type
		 *            String, in which case the value would be escaped.
		 * 
		 * @return a {@link SafeHtml} instance
		 */

		@SafeHtmlTemplates.Template("<div name=\"{0}\" style=\"{1}\">{2}</div>")
		SafeHtml cell(String name, SafeStyles styles, SafeHtml value);

	}

	public ImagesCell() {
		super(SimpleSafeHtmlRenderer.getInstance(), "click", "keydown");
	}

	public ImagesCell(SafeHtmlRenderer<String> renderer) {
		super(renderer, "click", "keydown");
	}

	/**
	 * Create a singleton instance of the templates used to render the cell.
	 */

	private static Templates templates = GWT.create(Templates.class);
	private static final SafeHtml ICON_ADD = makeImage(AppResource.INSTANCE.images().addIconImage());
	private static final SafeHtml ICON_EDIT = makeImage(AppResource.INSTANCE.images().editVdbIconImage());
	private static final SafeHtml ICON_REMOVE = makeImage(AppResource.INSTANCE.images().removeIconImage());

	/**
	 * 
	 * Called when an event occurs in a rendered instance of this Cell. The
	 * parent element refers to the element that contains the rendered cell, NOT
	 * to the outermost element that the Cell rendered.
	 */

	@Override
	public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context,

	Element parent, String value, NativeEvent event,

	com.google.gwt.cell.client.ValueUpdater<String> valueUpdater) {
		// Let AbstractCell handle the keydown event.
		super.onBrowserEvent(context, parent, value, event, valueUpdater);

		// Handle the click event.
		if ("click".equals(event.getType())) {

			// Ignore clicks that occur outside of the outermost element.
			EventTarget eventTarget = event.getEventTarget();
			if (parent.isOrHasChild(Element.as(eventTarget))) {

				// if (parent.getFirstChildElement().isOrHasChild(
				// Element.as(eventTarget))) {
				// use this to get the selected element!!
				Element el = Element.as(eventTarget);

				// check if we really click on the image
				if (el.getNodeName().equalsIgnoreCase("IMG")) {
					doAction(el.getParentElement().getAttribute("name"),
					valueUpdater);
				}
			}
		}
	};

	/**
	 * onEnterKeyDown is called when the user presses the ENTER key will the
	 * Cell is selected. You are not required to override this method, but its a
	 * common convention that allows your cell to respond to key events.
	 */

	@Override
	protected void onEnterKeyDown(Context context, Element parent,
	String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
		doAction(value, valueUpdater);
	}

	/**
	 * Intern action
	 * @param value
	 *            selected value
	 * @param valueUpdater
	 *            value updater or the custom value update to be called
	 */

	private void doAction(String value, ValueUpdater<String> valueUpdater) {
		// Trigger a value updater. In this case, the value doesn't actually
		// change, but we use a ValueUpdater to let the app know that a value
		// was clicked.
		if (valueUpdater != null)
			valueUpdater.update(value);
	}

	@Override
	protected void render(com.google.gwt.cell.client.Cell.Context context,
	SafeHtml data, SafeHtmlBuilder sb) {

		/*
		 * Always do a null check on the value. Cell widgets can pass null to
		 * cells if the underlying data contains a null, or if the data arrives
		 * out of order.
		 */

		if (data == null) {
			return;
		}

		// If the value comes from the user, we escape it to avoid XSS attacks.
		// SafeHtml safeValue = SafeHtmlUtils.fromString(data.asString());
		// Use the template to create the Cell's html.
		// SafeStyles styles = SafeStylesUtils.fromTrustedString(safeValue.asString());
		// generate the image cell

		SafeStyles imgStyle = SafeStylesUtils.fromTrustedString("float:left;cursor:hand;cursor:pointer;margin-right:8px;");

		SafeHtml rendered = templates.cell(ADD_VDB, imgStyle, ICON_ADD);
		sb.append(rendered);

		rendered = templates.cell(EDIT_VDB, imgStyle, ICON_EDIT);
		sb.append(rendered);

		rendered = templates.cell(DELETE_VDB, imgStyle, ICON_REMOVE);
		sb.append(rendered);
	}
	
	/**
	 * Make icons available as SafeHtml
	 * @param resource
	 * @return
	 */

	private static SafeHtml makeImage(ImageResource resource) {
		AbstractImagePrototype proto = AbstractImagePrototype.create(resource);

		// String html = proto.getHTML().replace("style='",
		// "style='left:0px;top:0px;"); // position:absolute;
		//
		// return SafeHtmlUtils.fromTrustedString(html);

		return proto.getSafeHtml();
	}

}