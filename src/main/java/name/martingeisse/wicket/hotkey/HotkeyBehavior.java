/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.wicket.hotkey;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

/**
 * Adds hotkey functionality to a component.
 * 
 * If more than one component tries to react to the same hotkey,
 * then that key will be disabled for all of them.
 */
public class HotkeyBehavior extends Behavior {

	/* (non-Javadoc)
	 * @see org.apache.wicket.behavior.Behavior#renderHead(org.apache.wicket.Component, org.apache.wicket.markup.head.IHeaderResponse)
	 */
	@Override
	public void renderHead(Component component, IHeaderResponse response) {
		super.renderHead(component, response);
		response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(HotkeyBehavior.class, "jquery.hotkeys.js")));
		
		// store common data to detect duplicates:
		// use behavior
		// Component.getMetaData
		// Component.getString
	}
	
}
