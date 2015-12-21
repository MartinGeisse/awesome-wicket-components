/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.wicket.simpleform;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 * Used by the {@link DecoratorBuilder} to build the models for form components.
 * 
 * @param <F> the form model type
 */
public interface ModelBuilder<F> {

	/**
	 * Builds a model for a form component.
	 * 
	 * @param formModel the model used by the form
	 * @param modelSpecification the specification string that describes the model, e.g.
	 * a path for a {@link PropertyModel}.
	 * @return the form component model
	 */
	public <T> IModel<T> buildModel(IModel<F> formModel, String modelSpecification);
	
}
