package socialdear.views.component.implemented.filter;

public abstract class AlwaysEnabledFilter extends FilterableComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7426632186585962716L;

	@Override
	public boolean shouldShow(String filter) {
		return true;
	}
}
