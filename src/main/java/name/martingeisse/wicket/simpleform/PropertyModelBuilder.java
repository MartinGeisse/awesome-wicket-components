/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.wicket.simpleform;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 * <p>
 * This is the default implementation of {@link ModelBuilder}. It builds
 * {@link PropertyModel}s using the specification as the path.
 * </p>
 * 
 * <p>
 * If possible, use the {@link #get()} method instead of creating new instances
 * of this class. That method uses an internally shared instance to save memory.
 * </p>
 *
 * @param <F> the form model type
 */
public final class PropertyModelBuilder<F> implements ModelBuilder<F> {

	private static final PropertyModelBuilder<Object> instance = new PropertyModelBuilder<>();
	
	/**
	 * Gets the shared instance of this class, using a user-defined type.
	 * 
	 * @return the shared instance
	 */
	public static <F> PropertyModelBuilder<F> get() {
		@SuppressWarnings("unchecked")
		PropertyModelBuilder<F> result = (PropertyModelBuilder<F>)instance;
		return result;
	}
	
	/* (non-Javadoc)
	 * @see name.martingeisse.wicket.simpleform.ModelBuilder#buildModel(org.apache.wicket.model.IModel, java.lang.String)
	 */
	@Override
	public <T> IModel<T> buildModel(final IModel<F> formModel, final String modelSpecification) {
		return new PropertyModel<>(formModel, modelSpecification);
	}

}
