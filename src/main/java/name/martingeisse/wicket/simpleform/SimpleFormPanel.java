/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.wicket.simpleform;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import name.martingeisse.wicket.helpers.AjaxRequestUtil;

/**
 * <p>
 * This panel implements a "simple form", using a very regular layout.
 * It is intended to quickly build forms, usually to edit data records,
 * without any special layout or content.
 * </p>
 *
 * <p>
 * This panel provides:
 * <ul>
 * <li>a FORM element and corresponding {@link Form} or {@link StatelessForm}</li>
 * <li>built-in "decorator" markup for form components containing a label, error message (using a {@link FeedbackPanel}), etc.</li>
 * <li>markup for typical form components such as text fields, password fields, etc.</li>
 * </ul>
 * </p>
 *
 * <p>
 * In the default theme, a Bootstrap-based markup is used for styling.
 * </p>
 *
 * <p><b>Usage</b></p>
 *
 * <p>
 * Here is a simple example for a registration form:
 * </p>
 * 
 * <pre>
 * SimpleFormPanel<BasicUsageData> formPanel = new SimpleFormPanel<>("basicUsageForm");
 * formPanel.prepareDecorator().withLabel("Choose a username").withModel("username").withRequiredness(true).addTextField();
 * formPanel.prepareDecorator().withLabel("Choose a password").withModel("password").withRequiredness(true).addPasswordTextField();
 * formPanel.prepareDecorator().withLabel("Please type in your password again").withModel("passwordAgain").withRequiredness(true).addPasswordTextField();
 * formPanel.prepareSpecialFormBlock().withText("Register").addSubmitButton();
 * page.add(formPanel);
 * </pre>
 *
 * <p><b>Built-in features</b></p>
 *
 * A set of commonly needed form components can be built through the decorator builder. A new
 * decorator builder can be obtained as in the simple example above using {@link #prepareDecorator()}.
 * 
 * The decorator builder provides out-of-the-box support for:
 * <ul>
 * <li>component label, using a fixed string or a string-typed model. The label uses a LABEL tag
 * that points to the component</li>
 * <li>building the component's model automatically from a string-typed model specification. The interpretation
 * of that specification is left to the {@link ModelBuilder} set in this panel. The standard behavior is to use
 * a {@link PropertyModelBuilder} that interprets the model specification as a property path and builds a
 * {@link PropertyModel} from it.</li>
 * <li>setting a conversion type for the model value</li>
 * <li>setting the requiredness and adding validations or other behaviors</li>
 * <li>providing a help text, either as a fixed string or as a string-typed model</li>
 * </ul>
 *
 * <p><b>Statelessness</b></p>
 *
 * <p>
 * The form can be made stateless by calling {@link #setBuildStateless(boolean)}. This
 * changes the {@link Form} to a {@link StatelessForm} and causes the {@link DecoratorBuilder}
 * to build all components in a stateless manner. <b>Note:</b> If you intent to build custom
 * components of any kind, then <i>you</i> are responsible for making them respect the
 * {@link #isBuildStateless()} flag of this panel.
 * </p>
 * 
 * <p>
 * Changing statelessness must occur before the form has been created. The form gets created
 * when this panel is added to its parent, and also when adding any form blocks.
 * </p>
 * 
 * <p>
 * Also note that while {@link #isBuildStateless()} <i>demands</i> that components try to
 * be stateless, the opposite is not the case: If that method returns false, it only means
 * "statelessness is not required". Components are still allowed to be stateless, only they
 * don't <i>have</i> to be.
 * </p>
 *
 * <p><b>Terminology needed for advanced usage</b></p>
 *
 * <p>
 * A <b>form block</b> is a block-level component directly inside the form. Each form block
 * gets rendered to an empty DIV tag. The most common kind of form block is a decorator (see below).
 * In addition, by using custom form blocks, arbitrary content can be inserted into a form
 * if needed.
 * </p>
 *
 * <p>
 * A <b>form component</b> is a subclass of {@link FormComponent} -- such as a text field,
 * password field, or drop-down box. Obviously, these are the things that you'd want to build
 * a form with. However, you'll also want them to be decorated with a label, {@link FeedbackPanel}
 * (for validation errors), and so on.
 * </p>
 *
 * <p>
 * A <b>decorator</b> is a form block that provides these decorations. Decorators are provided by
 * this class out of the box. You'll only ever need to build decorators yourself if you are not
 * happy with the built-in ones. You do <i>not</i> have to build custom decorators just to include
 * new kinds of form components.
 * </p>
 *
 * <p>
 * The downside is that decorators cannot contain form components <i>directly</i>. Instead, the
 * <b>decorated body</b> of a decorator must be a {@link Panel} or panel-like component (like a
 * {@link Fragment}) that gets rendered to an empty DIV element. The decorated body contains
 * the actual form component(s). The primary task of the decorated body is to provide appropriate
 * markup for the form component -- the decorator itself cannot do that if we want to keep it
 * independent of the actual form components.
 * </p>
 *
 * <p><b>Creating decorators for custom form components</b></p>
 * <p>
 * {@link DecoratorBuilder} is used to build common form components, but it can also be used
 * with a custom panel or panel-like component as its decorated body using either
 * {@link DecoratorBuilder#addDecorator(Component, org.apache.wicket.markup.html.form.LabeledWebMarkupContainer, Component)}
 * or {@link DecoratorBuilder#addDecoratorForSingleFormComponent(Component, FormComponent)}.
 * Such a custom body will be decorated with a label, error {@link FeedbackPanel} etc. as usual.
 * The panel must contain the markup used for the custom form components. Likewise, the panel
 * could contain arbitrary content as well that then gets decorated.
 * </p>
 *
 * <p><b>Creating decorators with multiple components</b></p>
 * <p>
 * As a special case of the above, the decorated body can be a panel that contains more than
 * one form component. The panel would then contain the markup for all of them. In such a
 * case, you must choose one of them to be the source for validation errors, or provide a
 * custom {@link FeedbackPanel} yourself.
 * </p>
 *
 * <p><b>Creating custom form blocks</b></p>
 *
 * <p>
 * To include a custom form block, create an arbitrary panel or panel-like component and add it
 * using {@link #addFormBlock(Component)}. The component will be rendered to an empty DIV
 * element.
 * </p>
 *
 * @param <F> the type of form model object
 */
public class SimpleFormPanel<F> extends Panel {

	/**
	 * The wicket:id to use for the decorated body within the decorator fragment.
	 */
	public static final String DECORATED_BODY_ID = "decoratedBody";

	/**
	 * This flag indicates that the form and all its component should try to be
	 * stateless.
	 */
	public static final int FLAG_BUILD_STATELESS = FLAG_RESERVED1;

	private ModelBuilder<F> explicitModelBuilder;
	private transient ModelBuilder<F> cachedDefaultModelBuilder;

	/**
	 * Constructor.
	 * @param id the wicket id
	 */
	public SimpleFormPanel(final String id) {
		super(id);
	}

	/**
	 * Constructor.
	 * @param id the wicket id
	 * @param model the model
	 */
	public SimpleFormPanel(final String id, final IModel<F> model) {
		super(id, model);
	}

	/**
	 * Sets the model to use for this form.
	 * @param model the model
	 * @return this
	 */
	public SimpleFormPanel<F> setModel(final IModel<F> model) {
		super.setDefaultModel(model);
		return this;
	}

	/**
	 * Returns the model used for this form.
	 * @return the model
	 */
	public IModel<F> getModel() {
		@SuppressWarnings("unchecked")
		final IModel<F> model = (IModel<F>)getDefaultModel();
		return model;
	}

	/**
	 * Getter method for the buildStateless.
	 * @return the buildStateless
	 */
	public boolean isBuildStateless() {
		return getFlag(FLAG_BUILD_STATELESS);
	}

	/**
	 * Setter method for the buildStateless.
	 * @param buildStateless the buildStateless to set
	 * @return this
	 */
	public SimpleFormPanel<F> setBuildStateless(final boolean buildStateless) {
		if (get("form") != null) {
			throw new IllegalStateException("cannot change statelessness anymore -- form has already been created");
		}
		setFlag(FLAG_BUILD_STATELESS, buildStateless);
		return this;
	}

	/**
	 * Getter method for the modelBuilder.
	 * @return the modelBuilder
	 */
	public ModelBuilder<F> getModelBuilder() {
		if (explicitModelBuilder != null) {
			return explicitModelBuilder;
		}
		if (cachedDefaultModelBuilder == null) {
			cachedDefaultModelBuilder = new PropertyModelBuilder<>();
		}
		return cachedDefaultModelBuilder;
	}

	/**
	 * Setter method for the modelBuilder. Set to null to revert to the default builder.
	 * @param modelBuilder the modelBuilder to set
	 * @return this
	 */
	public SimpleFormPanel<F> setModelBuilder(final ModelBuilder<F> modelBuilder) {
		this.explicitModelBuilder = modelBuilder;
		return this;
	}

	/* (non-Javadoc)
	 * @see org.apache.wicket.Component#onInitialize()
	 */
	@Override
	protected void onInitialize() {
		super.onInitialize();
		setOutputMarkupId(true);
		needFormNow();
	}
	
	private void needFormNow() {
		if (get("form") == null) {
			final Form<F> form = newForm("form", getModel(), this::onSubmit);
			form.add(new RepeatingView("formBlocks"));
			add(form);
		}
	}

	/**
	 * Builds the form for this panel.
	 *
	 * @param id the wicket:id of the form to build
	 * @param model the model for the form
	 * @param onSubmitCallback a callback that should be invoked when the form gets submitted
	 * @return the form
	 */
	protected Form<F> newForm(final String id, final IModel<F> model, final Runnable onSubmitCallback) {
		if (isBuildStateless()) {
			return new StatelessForm<F>(id, model) {

				@Override
				protected void onValidate() {
					super.onValidate();
					AjaxRequestUtil.markForRender(SimpleFormPanel.this);
				}

				@Override
				protected void onSubmit() {
					super.onSubmit();
					onSubmitCallback.run();
				}

			};
		} else {
			return new Form<F>(id, model) {

				@Override
				protected void onValidate() {
					super.onValidate();
					AjaxRequestUtil.markForRender(SimpleFormPanel.this);
				}

				@Override
				protected void onSubmit() {
					super.onSubmit();
					onSubmitCallback.run();
				}

			};
		}
	}

	/**
	 * Getter method for the form.
	 * @return the form
	 */
	public final Form<F> getForm() {
		@SuppressWarnings("unchecked")
		final Form<F> form = (Form<F>)get("form");
		return form;
	}

	/**
	 * @return the {@link RepeatingView} for the form blocks
	 */
	public final RepeatingView getFormBlocksRepeatingView() {
		needFormNow();
		return (RepeatingView)get("form:formBlocks");
	}

	/**
	 * Generates a component ID for a new form block.
	 *
	 * @return the component ID
	 */
	public final String newFormBlockId() {
		return getFormBlocksRepeatingView().newChildId();
	}

	/**
	 * Adds the specified component as a form block, i.e. without
	 * an error text or label. This may be used to add custom form blocks.
	 *
	 * @param component the component to add
	 */
	public final void addFormBlock(final Component component) {
		getFormBlocksRepeatingView().add(component);
	}

	/**
	 * Creates an {@link DecoratorBuilder} for a new decorator.
	 *
	 * @return the builder
	 */
	public DecoratorBuilder prepareDecorator() {
		return new DecoratorBuilder(this);
	}

	/**
	 * Builds a model from a model specification. The model links to the form
	 * model of this panel and is built using this panel's model builder.
	 * 
	 * @param modelSpecification the model specification
	 * @return the model
	 */
	public <T> IModel<T> buildModel(final String modelSpecification) {
		return getModelBuilder().buildModel(getModel(), modelSpecification);
	}

	/**
	 * Creates a {@link SpecialFormBlockBuilder} for a new form block.
	 *
	 * @return the builder
	 */
	public SpecialFormBlockBuilder prepareSpecialFormBlock() {
		return new SpecialFormBlockBuilder(this);
	}

	/**
	 * Creates a {@link Fragment} for a built-in form block.
	 *
	 * @param markupId the wicket:id of the fragment's markup within this panel's markup
	 * @return the fragment
	 */
	final Fragment createBuiltInFormBlockFragment(final String markupId) {
		return createBuiltInFragment(newFormBlockId(), markupId);
	}

	/**
	 * Creates a {@link Fragment} for a built-in decorated body.
	 *
	 * @param markupId the wicket:id of the fragment's markup within this panel's markup
	 * @return the fragment
	 */
	final Fragment createBuiltInDecoratedBodyFragment(final String markupId) {
		return createBuiltInFragment("decoratedBody", markupId);
	}
	
	/**
	 * Creates a {@link Fragment} for which markup is provided by this form panel,
	 * and adds it to this form panel.
	 *
	 * @param componentId the wicket:id of the fragment component to create
	 * @param markupId the wicket:id of the fragment's markup within this panel's markup
	 * @return the fragment
	 */
	private final Fragment createBuiltInFragment(final String componentId, final String markupId) {
		final Fragment fragment = new Fragment(componentId, markupId, this);
		addFormBlock(fragment);
		return fragment;
	}

	/**
	 * This callback is called when the form was submitted.
	 */
	protected void onSubmit() {
	}

}
