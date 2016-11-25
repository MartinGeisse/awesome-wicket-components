package name.martingeisse.wicket.helpers;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.model.IModel;

/**
 *
 */
public class InlineProgressBar extends ProgressBar {

	public InlineProgressBar(String id) {
		super(id);
	}

	public InlineProgressBar(String id, IModel<?> model) {
		super(id, model);
	}

	@Override
	protected void onComponentTag(ComponentTag tag) {
		super.onComponentTag(tag);
		tag.append("style", "display: inline-block; margin-bottom: 0px; vertical-align: bottom; ", "; ");
	}
}
