package model.launcher;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.json.JsonObject;
import javax.json.JsonValue;

import org.eclipse.swt.graphics.RGB;

import views.logging.SingleLoggingElementPanel;

public class ELKLoggingModel {

	static Map<String, RGB> colorMAp = new LinkedHashMap<>();

	private String severity;
	private String timestamp;
	private String facility;
	private String message;

	private SingleLoggingElementPanel referencingJComponent;

	private List<Entry<String, JsonValue>> mdcValues = new ArrayList<>();
	private RGB color;
	private String application;

	public ELKLoggingModel(String message) {
		this.message = message;
		severity = "INFO";
		timestamp = "";
		facility = "Frontend Test";
		application = "";
	}

	public ELKLoggingModel(JsonObject details) {
		JsonObject source = details.getJsonObject("_source");
		JsonObject fields = details.getJsonObject("fields");

		application = fields.getJsonArray("applicationmapped").getString(0);
		color = colorMAp.get(application);
		if (color == null) {
			color = getNewColor();
			colorMAp.put(application, color);
		}

		for (Entry<String, JsonValue> entry : source.entrySet()) {
			if (entry.getKey().startsWith("mdc.")) {
				mdcValues.add(entry);
			}
		}
		severity = source.getString("Severity");
		timestamp = source.getString("@timestamp");
		facility = source.getString("facility");
		message = source.getString("message");

	}

	RGB getNewColor() {

		// to get rainbow, pastel colors
		Random random = new Random();
		final float hue = random.nextFloat();
		final float saturation = 1f;// 1.0 for brilliant, 0.0 for dull
		final float luminance = 0.7f; // 1.0 for brighter, 0.0 for black
		Color jColor = Color.getHSBColor(hue, saturation, luminance);

		return new RGB(jColor.getRed(), jColor.getGreen(), jColor.getBlue());
	}

	public String toShortString(int length) {
		length = length / 5;
		String shortMessage = message.length() > length ? message.substring(0, length) + " ..." : message;

		String string = timestamp + " " + severity + " " + application + " " + facility + "\n\t" + shortMessage;

		String sevirityHTML = string;
		if ("ERROR".equals(severity) || "SEVERE".equals(severity)) {
			sevirityHTML = "<span style=\"color:red\" >" + string + "</span>";
		} else if ("WARNING".equals(sevirityHTML)) {
			sevirityHTML = "<span style=\"color:orange\" >" + string + "</span>";
		}
		return sevirityHTML;
	}

	public String toExpandedString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Timestamp : " + timestamp + "\n" + "Severity : " + severity + "\nApplication : " + application
				+ "\nFacility : " + facility + "\n");

		mdcValues.forEach(entry -> builder.append(entry.getKey() + " : " + entry.getValue() + "\n"));
		builder.append("Message : <span style=\"color:gray\" >" + message + "</span>");

		return builder.toString();
	}

	int getCommpactWidth() {
		return message.split("\n").length;
	}

	public int getExpandedHeight() {
		int messageLines = getCommpactWidth();
		return (4 + mdcValues.size() + messageLines) * 9;
	}

	public int getMEssageStart() {
		StringBuilder builder = new StringBuilder();
		builder.append("Timestamp : " + timestamp + "\n" + "Severity : " + severity + "\nApplication : " + application
				+ "\nFacility : " + facility + "\n");

		mdcValues.forEach(entry -> builder.append(entry.getKey() + " : " + entry.getValue() + "\n"));
		builder.append("Message : ");

		return builder.toString().length();
	}

	public String getMessage() {
		return message;
	}

	public boolean isReferencedComponentClicked() {
		return referencingJComponent != null && referencingJComponent.isClicked();

	}

	public SingleLoggingElementPanel getReferencingJComponent() {
		return referencingJComponent;
	}

	public void setReferencingJComponent(SingleLoggingElementPanel referencingJComponent) {
		this.referencingJComponent = referencingJComponent;
	}
}
