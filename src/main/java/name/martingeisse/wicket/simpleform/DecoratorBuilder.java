/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.wicket.simpleform;

import java.util.ArrayList;
import java.util.List;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.form.LabeledWebMarkupContainer;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import name.martingeisse.wicket.bootstrap.BootstrapComponentFeedbackPanel;

/**
 * <p>
 * The core builder class for form component decorators.
 * </p>
 * 
 * <p>
 * Many of the methods in this builder deal with a single form component at a time. Most of
 * them are labeled as "form component options apply". This means that any options set in this
 * builder that are useful for a single form component are applied to that component, such
 * as the data model, conversion type, requiredness and form component behaviors. It also means
 * that the form component is used as the label target and as the source for validation errors.
 * </p>
 */
public class DecoratorBuilder {

	private final SimpleFormPanel<?> panel;
	private IModel<String> labelModel;
	private IModel<?> dataModel;
	private Class<?> dataConversionType;
	private Boolean requiredness;
	private IModel<String> helpTextModel;
	private List<Behavior> formComponentBehaviors;

	/**
	 * Constructor.
	 * @param panel the containing panel. Subclasses should just pass this to the super constructor.
	 */
	public DecoratorBuilder(SimpleFormPanel<?> panel) {
		this.panel = panel;
	}

	/**
	 * Stores the label text to use.
	 * 
	 * @param labelText the label text to store
	 * @return this
	 */
	public final DecoratorBuilder withLabel(String labelText) {
		return withLabel(Model.of(labelText));
	}
	
	/**
	 * Stores the label model to use.
	 * 
	 * @param labelModel the label model to store
	 * @return this
	 */
	public final DecoratorBuilder withLabel(IModel<String> labelModel) {
		this.labelModel = labelModel;
		return this;
	}
	
	/**
	 * Builds a model for a form component.
	 * 
	 * The default implementation uses the {@link ModelBuilder} from the form.
	 * 
	 * @param modelSpecification the model specification
	 * @return the model
	 */
	protected <T> IModel<T> buildModel(String modelSpecification) {
		return panel.buildModel(modelSpecification);
	}

	/**
	 * Sets the data model to use, building a model from a specification.
	 * 
	 * @param modelSpecification the model specification
	 * @return this
	 */
	public final DecoratorBuilder withModel(String modelSpecification) {
		return withModel(buildModel(modelSpecification));
	}
	
	/**
	 * Sets the data model to use.
	 * 
	 * @param dataModel the data model
	 * @return this
	 */
	public final DecoratorBuilder withModel(IModel<?> dataModel) {
		this.dataModel = dataModel;
		return this;
	}

	/**
	 * Sets the data conversion type to use.
	 * 
	 * @param dataConversionType data conversion type
	 * @return this
	 */
	public final DecoratorBuilder withType(Class<?> dataConversionType) {
		this.dataConversionType = dataConversionType;
		return this;
	}
	
	/**
	 * Sets the requiredness for user input.
	 * 
	 * @param requiredness whether user input is required, or null to leave the form
	 * component default alone
	 * @return this
	 */
	public final DecoratorBuilder withRequiredness(Boolean requiredness) {
		this.requiredness = requiredness;
		return this;
	}

	/**
	 * Stores the help text to use.
	 * 
	 * @param helpText the help text to store
	 * @return this
	 */
	public final DecoratorBuilder withHelpText(String helpText) {
		return withHelpText(Model.of(helpText));
	}
	
	/**
	 * Stores the help text model to use.
	 * 
	 * @param helpTextModel the help text model to store
	 * @return this
	 */
	public final DecoratorBuilder withHelpText(IModel<String> helpTextModel) {
		this.helpTextModel = helpTextModel;
		return this;
	}

	/**
	 * Adds a form component behavior.
	 * 
	 * @param formComponentBehavior the form component behavior to add
	 * @return this
	 */
	public final DecoratorBuilder withBehavior(Behavior formComponentBehavior) {
		if (formComponentBehaviors == null) {
			formComponentBehaviors = new ArrayList<>();
		}
		formComponentBehaviors.add(formComponentBehavior);
		return this;
	}

	/**
	 * <p>
	 * Adds a decorator by providing all the parts yourself.
	 * </p>
	 * 
	 * <p>
	 * Form component options <b>do not apply</b> here, as this method doesn't know about form
	 * components at all. It handles the decorated body in a black-box way.
	 * </p>
	 *
	 * @param decoratedBody the decorated body to wrap
	 * @param labelTarget the component to refer to in the "for" attribute of the label.
	 * This parameter may be null; in this case, the "for" attribute will be omitted.
	 * @param errorSource the source for error messages. This parameter may be null;
	 * in this case, no error messages will be shown.
	 */
	public final void addDecorator(final Component decoratedBody, final LabeledWebMarkupContainer labelTarget, final Component errorSource) {
		final Fragment decoratorFragment = panel.createBuiltInFormBlockFragment("decoratorFragment");
		decoratorFragment.add(new BootstrapComponentFeedbackPanel("error", errorSource));
		decoratorFragment.add(new FormComponentLabel("label", labelTarget).add(new Label("text", labelModel)));
		decoratorFragment.add(new Label("helpText", helpTextModel));
		decoratorFragment.add(new Label("toolLinks").setVisible(false));
		decoratorFragment.add(decoratedBody);
		panel.addFormBlock(decoratorFragment);
	}
	
	/**
	 * <p>
	 * Adds a decorator for decorated body that contains a single form component. Form component options apply.
	 * </p>
	 * 
	 * @param decoratedBody the decorated body to wrap
	 * @param formComponent the raw form component
	 */
	public final void addDecoratorForSingleFormComponent(final Component decoratedBody, final FormComponent<?> formComponent) {
		if (dataModel != null) {
			formComponent.setDefaultModel(dataModel);
		}
		if (dataConversionType != null) {
			formComponent.setType(dataConversionType);
		}
		if (requiredness != null) {
			formComponent.setRequired(requiredness);
		}
		if (formComponentBehaviors != null) {
			for (Behavior behavior : formComponentBehaviors) {
				formComponent.add(behavior);
			}
		}
		addDecorator(decoratedBody, formComponent, formComponent);
	}
	
	/*
	 * Adds a decorator for one of the built-in form components. The form component first gets wrapped in
	 * a fragment with the specified ID to provide its markup and turn it into a decorated body, then wrapped
	 * in a decorator. Form component options apply.
	 */
	private final void addDecoratorForBuiltInFormComponent(final FormComponent<?> formComponent, String fragmentId) {
		Fragment decoratedBodyFragment = panel.createBuiltInDecoratedBodyFragment(fragmentId);
		decoratedBodyFragment.add(formComponent);
		addDecoratorForSingleFormComponent(decoratedBodyFragment, formComponent);
	}

	/**
	 * Adds a text field. Form component options apply.
	 */
	public final void addTextField() {
		addDecoratorForBuiltInFormComponent(new TextField<>("textField"), "textFieldFragment");
	}

	/**
	 * Adds an email text field. Form component options apply.
	 */
	public final void addEmailTextField() {
		addDecoratorForBuiltInFormComponent(new EmailTextField("textField"), "emailTextFieldFragment");
	}

	/**
	 * Adds a password field. Form component options apply.
	 */
	public final void addPasswordTextField() {
		addDecoratorForBuiltInFormComponent(new PasswordTextField("textField"), "passwordTextFieldFragment");
	}

	/**
	 * Adds a text area. Form component options apply.
	 * @param rows number of visible rows
	 */
	public final void addTextArea(final int rows) {
		final TextArea<String> textArea = new TextArea<String>("textArea") {
			@Override
			protected void onComponentTag(final ComponentTag tag) {
				super.onComponentTag(tag);
				tag.put("rows", rows);
			}
		};
		addDecoratorForBuiltInFormComponent(textArea, "textAreaFragment");
	}
	
}
