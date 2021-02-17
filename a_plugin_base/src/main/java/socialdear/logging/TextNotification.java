package socialdear.logging;

import org.eclipse.mylyn.commons.ui.dialogs.AbstractNotificationPopup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

public class TextNotification extends AbstractNotificationPopup {

	private String title;
	private String content;

	public TextNotification(Display display, String title, String content) {
		super(display);
		this.title = title;
		this.content = content;
	}

	@Override
	protected void createContentArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		GridData data = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		container.setLayoutData(data);

		container.setLayout(new GridLayout(1, false));

		Label successMsg = new Label(container, SWT.NULL);
		successMsg.setText(content);

		/*
		 * new Label(container, SWT.NONE); Label testLabel1 = new Label(container,
		 * SWT.NONE); testLabel1.setText("This is a Test Label"); FontData fontData =
		 * testLabel1.getFont().getFontData()[0]; Font font = new
		 * Font(Display.getCurrent(), new FontData(fontData.getName(),
		 * fontData.getHeight(), SWT.BOLD)); testLabel1.setFont(font);
		 */

		// new Label(container, SWT.NONE);
	}

	protected void addLink(Composite container, String url) {
		Link restEP = new Link(container, SWT.WRAP | SWT.LEFT);
		restEP.setText(createUrl(url));
		GridData linkData = new GridData();
		linkData.widthHint = 400;
		restEP.setLayoutData(linkData);
		restEP.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Program.launch(url);
			}

		});
	}

	private String createUrl(String endpoint) {
		return "<a href=\"" + endpoint + "\">" + endpoint + "</a>";
	}

	@Override
	protected String getPopupShellTitle() {
		return title;
	}

}
