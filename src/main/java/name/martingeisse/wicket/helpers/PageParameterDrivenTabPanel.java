/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.wicket.helpers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Shows a tabbed panel that uses a page parameter to select the
 * "active" tab, and generates {@link BookmarkablePageLink}s
 * to the current page (i.e. page class and parameters) with just
 * the tab parameter replaced.
 * 
 * Subclasses must provide the list of tabs (label and parameter
 * value) as well as a method to generate the tab contents based
 * on the current parameter value.
 * 
 * This class uses Bootstrap styles. Subclasses can override the styles.
 * 
 * This class is useful because:
 * 
 * - Wicket's {@link TabbedPanel} and {@link AjaxTabbedPanel} use stateful
 *   pages, which is dangerous if the page state can contain stale data
 *   (an indication that such state should be moved from page state to models,
 *   so using this class in such a situation is a dirty quick fix).
 * 
 * - a pure Javascript+AJAX tabbed panel would forget its current tab on
 *   reloading the page
 *   
 * - a pure Javascript (non-AJAX) tabbed panel would also forget its state AND
 *   would have to render all tab contents up-front.
 * 
 */
public abstract class PageParameterDrivenTabPanel extends Panel {

	/**
	 * The markup ID used for the tab body.
	 */
	public static final String TAB_BODY_MARKUP_ID = "tabBody";
	
	/**
	 * the parameterName
	 */
	private final String parameterName;
	
	/**
	 * the tabInfos
	 */
	private final List<AbstractTabInfo> tabInfos = new ArrayList<>();

	/**
	 * Constructor.
	 * @param id the wicket id
	 * @param parameterName the name of the page parameter that contains the tab selector
	 */
	public PageParameterDrivenTabPanel(String id, String parameterName) {
		super(id);
		this.parameterName = parameterName;
	}

	/**
	 * Constructor.
	 * @param id the wicket id
	 * @param parameterName the name of the page parameter that contains the tab selector
	 * @param model the model
	 */
	public PageParameterDrivenTabPanel(String id, String parameterName, IModel<?> model) {
		super(id, model);
		this.parameterName = parameterName;
	}

	/**
	 * Getter method for the tabInfos.
	 * @return the tabInfos
	 */
	public final List<AbstractTabInfo> getTabInfos() {
		return tabInfos;
	}

	/**
	 * Convenience method to add a tab of arbitrary type.
	 * @param tabInfo the info record for the tab
	 */
	public final void addTab(AbstractTabInfo tabInfo) {
		tabInfos.add(tabInfo);
	}

	/**
	 * Convenience method to add a simple tab.
	 * @param title the tab title
	 * @param selector the selector that is used as the parameter value
	 */
	public final void addTab(String title, String selector) {
		tabInfos.add(new TabInfo(title, selector));
	}

	/**
	 * Convenience method to add a dropdown tab. The returned tab header can be used to add
	 * entries to the dropdown menu.
	 * 
	 * @param title the tab title
	 * @return the dropdown tab info
	 */
	public final DropdownTabInfo addDropdownTab(String title) {
		DropdownTabInfo tabInfo = new DropdownTabInfo(title);
		tabInfos.add(tabInfo);
		return tabInfo;
	}
	
	/* (non-Javadoc)
	 * @see org.apache.wicket.Component#onInitialize()
	 */
	@Override
	protected void onInitialize() {
		super.onInitialize();

		// add tab headers
		add(new ListView<AbstractTabInfo>("tabHeaders", new PropertyModel<List<AbstractTabInfo>>(this, "tabInfos")) {
			@Override
			protected void populateItem(ListItem<AbstractTabInfo> item) {
				AbstractTabInfo tabInfo = item.getModelObject();
				item.add(tabInfo.createTabHeaderComponent(PageParameterDrivenTabPanel.this, "tabHeader"));
			}
		});

		// add tab body
		add(createBody(TAB_BODY_MARKUP_ID, getCurrentSelector()));

	}

	/**
	 * Convenience method to get the current selector value from the page parameters. This only works
	 * after this panel has been added to a page.
	 * @return the current selector
	 */
	public final String getCurrentSelector() {
		String explicitValue = getPage().getPageParameters().get(parameterName).toString();
		if (explicitValue != null) {
			return explicitValue;
		}
		if (tabInfos.isEmpty()) {
			return "";
		}
		AbstractTabInfo tabInfo = tabInfos.get(0);
		return tabInfo.getDefaultSelector();
	}

	/**
	 * Creates the actual tab body for the current selector. This is usually a {@link Panel}
	 * or {@link Fragment} since the component tag is empty.
	 * 
	 * @param id the wicket id to use
	 * @param selector the current tab selector
	 * @return the tab body component
	 */
	protected abstract Component createBody(String id, String selector);

	/**
	 * Creates a link that opens a tab.
	 * @param id the wicket id
	 * @param selector the selector for the tab to open
	 * @return the link
	 */
	public final Link<?> createTabLink(String id, String selector) {
		Page page = getPage();
		return new BookmarkablePageLink<>(id, page.getClass(), createTabLinkPageParameters(page, selector));
	}

	/**
	 * Creates the {@link PageParameters} for a link that opens a tab.
	 * @param selector the selector for the tab to open
	 * @return the parameters
	 */
	public final PageParameters createTabLinkPageParameters(String selector) {
		return createTabLinkPageParameters(getPage(), selector);
	}
	
	/**
	 * 
	 */
	private final PageParameters createTabLinkPageParameters(Page page, String selector) {
		PageParameters parameters = new PageParameters(page.getPageParameters());
		parameters.remove(parameterName).add(parameterName, selector);
		return parameters;
	}
	
	
	/**
	 * Base class for "tab info" records that can be added to the panel to make
	 * tabs selectable.
	 */
	public static abstract class AbstractTabInfo implements Serializable {

		/**
		 * Creates the component for the tab header.
		 * @param tabPanel the tab panel that contains this tab
		 * @param id the wicket id
		 * @return the tab header component
		 */
		protected abstract Component createTabHeaderComponent(PageParameterDrivenTabPanel tabPanel, String id);

		/**
		 * Returns the default selector, assuming that this tab is active.
		 * @return the default selector
		 */
		protected abstract String getDefaultSelector();
		
	}

	/**
	 * Describes a simple tab.
	 */
	public static final class TabInfo extends AbstractTabInfo {

		/**
		 * the title
		 */
		private final String title;

		/**
		 * the selector
		 */
		private final String selector;
		
		/**
		 * Constructor.
		 * @param title the tab title
		 * @param selector the tab selector that is used as the parameter value
		 */
		public TabInfo(String title, String selector) {
			this.title = title;
			this.selector = selector;
		}

		/**
		 * Getter method for the title.
		 * @return the title
		 */
		public String getTitle() {
			return title;
		}
		
		/**
		 * Getter method for the selector.
		 * @return the selector
		 */
		public String getSelector() {
			return selector;
		}
		
		/* (non-Javadoc)
		 * @see name.martingeisse.slave_services.common.frontend.components.PageParameterDrivenTabPanel.AbstractTabInfo#createTabHeaderComponent(name.martingeisse.slave_services.common.frontend.components.PageParameterDrivenTabPanel, java.lang.String)
		 */
		@Override
		protected Component createTabHeaderComponent(PageParameterDrivenTabPanel tabPanel, String id) {
			Link<?> link = tabPanel.createTabLink("link", selector);
			link.add(new Label("title", title));
			Fragment fragment = new Fragment(id, "simpleTabHeader", tabPanel);
			fragment.add(link);
			if (selector.equals(tabPanel.getCurrentSelector())) {
				fragment.add(new AttributeAppender("class", " active"));
			}
			return fragment;
		}

		/* (non-Javadoc)
		 * @see name.martingeisse.slave_services.common.frontend.components.PageParameterDrivenTabPanel.AbstractTabInfo#getDefaultSelector()
		 */
		@Override
		protected String getDefaultSelector() {
			return selector;
		}
		
	}

	/**
	 * Describes a sub-menu that contains a list of {@link AbstractTabInfo} objects.
	 */
	public static final class DropdownTabInfo extends AbstractTabInfo {

		/**
		 * the title
		 */
		private final String title;

		/**
		 * the tabInfos
		 */
		private final List<TabInfo> tabInfos = new ArrayList<>();

		/**
		 * Constructor.
		 * @param title the tab title
		 */
		public DropdownTabInfo(String title) {
			this.title = title;
		}

		/**
		 * Getter method for the tabInfos.
		 * @return the tabInfos
		 */
		public List<TabInfo> getTabInfos() {
			return tabInfos;
		}

		/**
		 * Convenience method to add a tab.
		 * @param tabInfo the info record for the tab
		 * @return this
		 */
		public final DropdownTabInfo addTab(TabInfo tabInfo) {
			tabInfos.add(tabInfo);
			return this;
		}

		/**
		 * Convenience method to add a tab.
		 * @param title the tab title
		 * @param selector the selector that is used as the parameter value
		 * @return this
		 */
		public final DropdownTabInfo addTab(String title, String selector) {
			tabInfos.add(new TabInfo(title, selector));
			return this;
		}
		
		/* (non-Javadoc)
		 * @see name.martingeisse.slave_services.common.frontend.components.PageParameterDrivenTabPanel.AbstractTabInfo#createTabHeaderComponent(name.martingeisse.slave_services.common.frontend.components.PageParameterDrivenTabPanel, java.lang.String)
		 */
		@Override
		protected Component createTabHeaderComponent(final PageParameterDrivenTabPanel tabPanel, String id) {
			final Fragment dropdownHeaderFragment = new Fragment(id, "dropdownTabHeader", tabPanel);
			dropdownHeaderFragment.add(new Label("title", title));
			dropdownHeaderFragment.add(new ListView<TabInfo>("entries", new PropertyModel<List<TabInfo>>(this, "tabInfos")) {
				@Override
				protected void populateItem(ListItem<TabInfo> item) {
					TabInfo tabInfo = item.getModelObject();
					Link<?> link = tabPanel.createTabLink("link", tabInfo.getSelector());
					link.add(new Label("title", tabInfo.getTitle()));
					item.add(link);
					if (tabInfo.getSelector().equals(tabPanel.getCurrentSelector().toString())) {
						item.add(new AttributeAppender("class", " active"));
						dropdownHeaderFragment.add(new AttributeAppender("class", " active"));
					}
				}
			});
			return dropdownHeaderFragment;
		}
		
		/* (non-Javadoc)
		 * @see name.martingeisse.slave_services.common.frontend.components.PageParameterDrivenTabPanel.AbstractTabInfo#getDefaultSelector()
		 */
		@Override
		protected String getDefaultSelector() {
			if (tabInfos.isEmpty()) {
				return "";
			}
			return tabInfos.get(0).getDefaultSelector();
		}

	}

}
