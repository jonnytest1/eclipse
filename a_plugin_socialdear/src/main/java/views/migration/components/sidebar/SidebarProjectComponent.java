package views.migration.components.sidebar;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JLabel;
import javax.swing.JTextField;

import org.eclipse.core.resources.IProject;

import socialdear.http.logging.Logging;
import socialdear.http.logging.Logging.LogLevel;
import socialdear.listeners.ImplementedMouseListener;
import socialdear.views.component.CustomElementPanel;
import sql.SQLCommand;
import util.ProjectFileParser;

public class SidebarProjectComponent extends CustomElementPanel implements ImplementedMouseListener, KeyListener {

	/**
	 * 
	 */

	boolean isEnabled;
	private static final long serialVersionUID = 5191447886703229024L;
	private IProject project;

	private SideBarComponent sideBarComponent;

	public SidebarProjectComponent(IProject project, SideBarComponent sideBarComponent, boolean b) {
		this.project = project;
		this.sideBarComponent = sideBarComponent;

		isEnabled = b;
		setLayout(new BorderLayout());
		setOpaque(false);
		recreate();
	}

	@Override
	protected void addElements() {

		JLabel projectLabel = new JLabel("<html><p style=\"border:2px solid black;width:155px;height:30px\">"
				+ project.getName().trim() + "</p></html>");
		projectLabel.addMouseListener(this);
		add(projectLabel, BorderLayout.PAGE_START);
		setMaximumSize(new Dimension(200, 40));
		if (isEnabled) {
			List<SQLCommand> sqlCommands = ProjectFileParser.getSQLCommands(project);

			Logging.logRequest(sqlCommands.stream().map(cmd -> cmd.toString()).collect(Collectors.joining("\n")),
					LogLevel.INFO, null, null);

			sideBarComponent.setCommands(isEnabled ? project : null, sqlCommands);

			SQLCommandsComponent comp = new SQLCommandsComponent(sqlCommands, sideBarComponent);

			comp.setMaximumSize(new Dimension(200, 999999));
			add(getScrollPane(comp), BorderLayout.CENTER);

			String prefix = sqlCommands.stream().map((cmd) -> cmd.getFileName().split("__")[0]).reduce("", (a, b) -> {
				if ("".equals(a) || parseHibernateString(a) < parseHibernateString(b)) {
					return b;
				}
				return a;
			});

			JTextField saveToFile = new JTextField(getNextHigher(prefix) + "__");
			add(saveToFile, BorderLayout.PAGE_END);
			saveToFile.addKeyListener(this);

		}
	}

	private String getNextHigher(String s) {
		List<String> letters = Arrays.asList(s.split(""));
		Collections.reverse(letters);
		for (int i = 0; i < letters.size(); i++) {
			try {
				Integer n = Integer.parseInt(letters.get(i));
				if (i < 9) {
					letters.set(i, n + 1 + "");
					break;
				} else {
					letters.set(i, "0");
				}
			} catch (NumberFormatException e) {
			}
		}
		return new StringBuilder(letters.stream().collect(Collectors.joining(""))).reverse().toString();
	}

	private int parseHibernateString(String h) {
		return Integer.parseInt(h.replaceAll("\\.", "").replace("V", ""));
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		isEnabled = !isEnabled;
		recreate();

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		JTextField textField = (JTextField) e.getSource();
		if (e.getKeyCode() == 10) { // Enter
			sideBarComponent.createSQL(textField.getText());
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

}
