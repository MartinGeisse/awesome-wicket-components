package name.martingeisse.wicket.helpers;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;

/**
 * Subclasses must implement a method to calculate the expected time to finish the progress bar to 100%.
 * A corresponding Javascript snippet will be added to advance the progress bar towards 100% within that time.
 * This should be combined with a mechanism that updates both the progress and this behavior with the actual
 * progress and new expected remaining time at some point, for example using an {@link AjaxSelfUpdatingTimerBehavior}.
 * <p>
 * This behavior must be used with a {@link ProgressBar}.
 */
public abstract class ProgressBarClientProgressBehavior extends Behavior {

	/**
	 * @return the remaining time in seconds to reach 100% progress
	 */
	protected abstract int getRemainingSeconds();

	@Override
	public void renderHead(Component component, IHeaderResponse response) {
		response.render(CssHeaderItem.forCSS("@keyframes progressBarFrames {100% {width: 100%;}} .js-progress {animation: progressBarFrames 5s 0s 1 linear forwards;}", ProgressBarClientProgressBehavior.class.getName()));
	}

}
