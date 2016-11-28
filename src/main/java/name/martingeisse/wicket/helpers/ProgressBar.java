package name.martingeisse.wicket.helpers;

import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * This component should be attached to an element of display: block or display:inline-block. The width of that
 * element is the total width of the progress bar.
 * <p>
 * The progress bar has a "total amount" which is a number that corresponds to the total width. The progress is
 * taken from the component model and is expected to be between 0 and the total amount. The default total amount
 * is 100, making the progress from the model a percentage value.
 */
public class ProgressBar extends WebComponent {

	private static final IModel<Integer> defaultTotalAmountModel = new AbstractReadOnlyModel<Integer>() {
		@Override
		public Integer getObject() {
			return 100;
		}
	};

	private IModel<Integer> totalAmountModel = defaultTotalAmountModel;

	public ProgressBar(String id) {
		super(id);
	}

	public ProgressBar(String id, IModel<?> model) {
		super(id, model);
	}

	/**
	 * Getter method.
	 *
	 * @return the model
	 */
	public IModel<Integer> getModel() {
		return (IModel<Integer>) getDefaultModel();
	}

	/**
	 * Setter method.
	 *
	 * @param model the model
	 * @return this
	 */
	public ProgressBar setModel(IModel<Integer> model) {
		setDefaultModel(model);
		return this;
	}

	/**
	 * Getter method.
	 *
	 * @return the totalAmountModel
	 */
	public IModel<Integer> getTotalAmountModel() {
		return totalAmountModel;
	}

	/**
	 * Setter method.
	 *
	 * @param totalAmountModel the totalAmountModel
	 * @return this
	 */
	public ProgressBar setTotalAmountModel(IModel<Integer> totalAmountModel) {
		this.totalAmountModel = totalAmountModel;
		return this;
	}

	/**
	 * @return
	 */
	public Integer getTotalAmount() {
		return (totalAmountModel == null ? null : totalAmountModel.getObject());
	}

	/**
	 * @return
	 */
	public Integer getProgress() {
		IModel<Integer> model = getModel();
		return (model == null ? null : model.getObject());
	}

	/**
	 * @return
	 */
	public Integer getProgressPercentage() {
		Integer totalAmount = getTotalAmount();
		Integer progress = getProgress();
		return (totalAmount == null || progress == null) ? null : (progress * 100 / totalAmount);
	}

	@Override
	protected void onComponentTag(ComponentTag tag) {
		super.onComponentTag(tag);
		tag.append("class", "progress", " ");
	}

	@Override
	public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
		super.onComponentTagBody(markupStream, openTag);

		final ProgressBarClientProgressBehavior clientProgressBehavior;
		List<ProgressBarClientProgressBehavior> clientProgressBehaviors = getBehaviors(ProgressBarClientProgressBehavior.class);
		if (clientProgressBehaviors.isEmpty()) {
			clientProgressBehavior = null;
		} else if (clientProgressBehaviors.size() == 1) {
			clientProgressBehavior = clientProgressBehaviors.get(0);
		} else {
			throw new IllegalStateException("found multiple ProgressBarClientProgressBehaviors for the same progress bar");
		}

		Integer progressPercentage = getProgressPercentage();
		int normalizedPercentage = (progressPercentage == null ? 0 : progressPercentage);
		getResponse().write("<div class=\"progress-bar");
		if (clientProgressBehavior != null) {
			getResponse().write(" js-progress");
		}
		getResponse().write("\" style=\"width: ");
		getResponse().write(Integer.toString(normalizedPercentage));
		getResponse().write("%; ");
		if (clientProgressBehavior != null) {
			getResponse().write("animation-duration: ");
			// add 1 second to compensate for the duration of the AJAX call
			getResponse().write(Integer.toString(clientProgressBehavior.getRemainingSeconds() + 1));
			getResponse().write("s; ");
		}
		getResponse().write("\"></div>");
	}

}
