package socialdear.views.component;

/**
 * @since 3.0
 */
public class DefaultCustomElementPanel extends CustomElementPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6314012526777421750L;

	public static interface Runner {

		public void run();
	}

	private Runner add;

	public DefaultCustomElementPanel(Runner add) {
		this.add = add;

	}

	@Override
	protected void addElements() {
		add.run();

	}

}
