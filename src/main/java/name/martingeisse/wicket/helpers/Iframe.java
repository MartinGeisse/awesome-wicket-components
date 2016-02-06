/**
 * Copyright (c) 2013 Shopgate GmbH
 */

package name.martingeisse.wicket.helpers;

import org.apache.wicket.Application;
import org.apache.wicket.IResourceListener;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.image.resource.LocalizedImageResource;
import org.apache.wicket.markup.parser.XmlTag.TagType;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.IResource.Attributes;
import org.apache.wicket.request.resource.ResourceReference;

/**
 * This component should be used with an iframe HTML element. The model set for this component
 * must return an {@link IResource} or a {@link ResourceReference}, which is then used for the
 * contents of the iframe.
 *
 * This implies that no localization is done in this class. If localization is needed, it must be
 * done in the model. Unfortunately, there is currently no other way since {@link ResourceReference}
 * doesn't support creating a clone with different localization attributes, so this class doesn't
 * even know how to create the localized reference. Note that {@link LocalizedImageResource}
 * has the same problem and contains a to-do marker for it.
 * 
 * You can set parameters for the resource as a {@link PageParameters} object. For an {@link IResource},
 * these parameters will be passed to the resource when the listener URL in the iframe's "src" attribute
 * gets requested. For a {@link ResourceReference}, they will be rendered into the iframe's "src"
 * attribute so the browser passes them to the resource.
 */
public class Iframe extends WebComponent implements IResourceListener {

	private PageParameters resourceParameters;

	/**
	 * Constructor.
	 * @param id the wicket id
	 */
	public Iframe(final String id) {
		super(id);
		setOutputMarkupId(true);
	}

	/**
	 * Constructor.
	 * @param id the wicket id
	 * @param model the model
	 */
	public Iframe(final String id, final IModel<?> model) {
		super(id, model);
		setOutputMarkupId(true);
	}

	/**
	 * Getter method for the resourceParameters.
	 * @return the resourceParameters
	 */
	public PageParameters getResourceParameters() {
		return resourceParameters;
	}

	/**
	 * Setter method for the resourceParameters.
	 * @param resourceParameters the resourceParameters to set
	 * @return this
	 */
	public Iframe setResourceParameters(final PageParameters resourceParameters) {
		this.resourceParameters = resourceParameters;
		return this;
	}

	// override
	@Override
	protected boolean getStatelessHint() {
		return (getDefaultModelObject() instanceof ResourceReference);
	}

	/* (non-Javadoc)
	 * @see org.apache.wicket.Component#onComponentTag(org.apache.wicket.markup.ComponentTag)
	 */
	@Override
	protected void onComponentTag(final ComponentTag tag) {
		super.onComponentTag(tag);
		if (tag.isOpenClose()) {
			tag.setType(TagType.OPEN);
		}
		Object modelObject = getDefaultModelObject();
		if (modelObject instanceof IResource) {
			tag.put("src", urlFor(IResourceListener.INTERFACE, resourceParameters));
		} else if (modelObject instanceof ResourceReference) {
			ResourceReference resourceReference = (ResourceReference)modelObject;
			if (resourceReference.canBeRegistered() && Application.exists()) {
				Application.get().getResourceReferenceRegistry().registerResourceReference(resourceReference);
			}
			tag.put("src", RequestCycle.get().urlFor(resourceReference, resourceParameters));
		} else {
			throw new RuntimeException("iframe model object is neither an IResource nor a ResourceReference");
		}
	}

	// override
	@Override
	public void onResourceRequested() {
		final RequestCycle requestCycle = RequestCycle.get();
		final Attributes attributes = new Attributes(requestCycle.getRequest(), requestCycle.getResponse(), resourceParameters);
		Object modelObject = getDefaultModelObject();
		if (modelObject instanceof IResource) {
			((IResource)modelObject).respond(attributes);
		} else if (modelObject instanceof ResourceReference) {
			((ResourceReference)modelObject).getResource().respond(attributes);
		} else {
			throw new RuntimeException("iframe model object is neither an IResource nor a ResourceReference");
		}
	}
	
	/**
	 * Renders a javascript snipped to the current {@link AjaxRequestTarget} that
	 * reloads the iframe.
	 */
	public final void renderReloadScript() {
		renderReloadScript(AjaxRequestUtil.getAjaxRequestTarget());
	}

	/**
	 * Renders a javascript snipped to the specified {@link AjaxRequestTarget} that
	 * reloads the iframe.
	 * @param target the target to render to
	 */
	public void renderReloadScript(final AjaxRequestTarget target) {
		target.appendJavaScript("document.getElementById('" + getMarkupId() + "').contentWindow.location.reload(true);");
	}

}
