/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.wicket.simpleform;

import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * The core builder class for form blocks other than form component decorators.
 */
public class SpecialFormBlockBuilder {

	private final SimpleFormPanel<?> panel;
	private IModel<String> textModel;

	/**
	 * Constructor.
	 * @param panel the containing panel. Subclasses should just pass this to the super constructor.
	 */
	public SpecialFormBlockBuilder(SimpleFormPanel<?> panel) {
		this.panel = panel;
	}
	
	/**
	 * Stores the specified text for use with a form block that needs text.
	 * 
	 * @param text the text to store
	 * @return this
	 */
	public final SpecialFormBlockBuilder withText(String text) {
		return withText(Model.of(text));
	}
	
	/**
	 * Stores the specified text model for use with a form block that needs text.
	 * 
	 * @param textModel the text model to store
	 * @return this
	 */
	public final SpecialFormBlockBuilder withText(IModel<String> textModel) {
		this.textModel = textModel;
		return this;
	}
	
	/**
	 * If the "really" flag is set, ensures that a text model was set. If the really flag
	 * is not set, ensures that no text model was set. On violations, this method throws
	 * an {@link IllegalStateException}.
	 */
	protected final void needsText(boolean really) {
		if (really && textModel == null) {
			throw new IllegalStateException("no text was set for a form block that needs it");
		}
		if (!really && textModel != null) {
			throw new IllegalStateException("text was set for a form block that cannot use it");
		}
	}
	
	/**
	 * Adds a form block that shows a text snippet. The content should be set using one of the withText()
	 * methods before calling this method.
	 */
	public final void addInfoText() {
		needsText(true);
		panel.createBuiltInFormBlockFragment("infoFragment").add(new Label("html", textModel));
	}
	
	/**
	 * Adds a form block that shows an HTML snippet. The content should be set using one of the withText()
	 * methods before calling this method.
	 */
	public final void addInfoHtml() {
		needsText(true);
		panel.createBuiltInFormBlockFragment("infoFragment").add(new Label("html", textModel).setEscapeModelStrings(false));
	}

	/**
	 * Adds a submit button for the form.
	 */
	public final void addSubmitButton() {
		needsText(true);
		panel.createBuiltInFormBlockFragment("submitButtonFragment").add(new Label("button", textModel));
	}

	/**
	 * Adds an AJAX submit button for the form.
	 */
	public final void addAjaxSubmitButton() {
		needsText(true);
		panel.createBuiltInFormBlockFragment("submitButtonFragment").add(new AjaxSubmitLink("button") {}.setBody(textModel));
	}

}
