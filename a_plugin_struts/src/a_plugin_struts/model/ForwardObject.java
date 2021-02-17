package a_plugin_struts.model;

import java.io.File;
import java.util.List;

import org.w3c.dom.Node;

import socialdear.views.component.implemented.Visualization.Visualisable;

public class ForwardObject extends StrutsObject implements Visualisable {

	String name;

	String path;

	private List<ActionBeanObject> action;

	private ActionBeanObject parent;

	public ForwardObject(Node node, File file) {
		super(node, file);

		name = node.getAttributes().getNamedItem("name").getNodeValue();
		Node namedItem = node.getAttributes().getNamedItem("path");
		if (namedItem != null) {
			path = namedItem.getNodeValue();
		}
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public List<ActionBeanObject> getActions() {
		return action;
	}

	public void setAction(List<ActionBeanObject> fActions) {
		this.action = fActions;
		fActions.forEach(action -> action.fromForward(this));
	}

	public void parentAction(ActionBeanObject parent) {
		this.parent = parent;
	}

	@Override
	public <T extends Visualisable> List<T> getConnections() {
		if (action == null) {
			return List.of();
		}
		return (List<T>) action;
	}

	@Override
	public String getNodeName() {
		if (parent == null) {
			return "forward\n" + file.getName() + " - " + getName();
		}
		return "forward\n" + parent.getPath() + " - " + getName();
	}

	@Override
	public <T extends Visualisable> List<T> getBackwardConnections() {
		if (parent == null) {
			return List.of();
		}
		return (List<T>) List.of(parent);
	}
}
