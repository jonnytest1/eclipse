package cfg_tp;

import java.awt.GridBagConstraints;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonReader;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbenchPreferencePage;

import cfg_tp.SystemSettings.TP_SETTINGS;
import config.CredentialsException;
import config.CredentialsFieldEditor;
import http.SapServerRepository;
import http.TargetprocessRepository;
import model.Team;
import service.GitListener;
import socialdear.http.CustomHttp;
import socialdear.http.CustomHttp.Auth;
import socialdear.http.CustomResponse;
import socialdear.logging.SystemProperties;
import socialdear.views.ImplementedPreferences;
import socialdear.views.SettingsEnum;
import socialdear.views.component.implemented.customizable.CustomizableViewPanel;
import views.current.widgets.ProjectViewWidget;
import views.current.widgets.bugview.BugViewWidget;

/**
 * @since 2.1
 */
public class SystemSettings extends ImplementedPreferences<TP_SETTINGS> implements IWorkbenchPreferencePage {

	public enum TP_SETTINGS implements SettingsEnum {
		TARGET_PROCESS_DOMAIN,

		TARGET_PROCESS_TOKEN,

		TARGET_PROCESS_VIEW_SETTINGS {
			@Override
			public Object getDefault() {
				return "{}";
			}
		},

		TARGET_PROCESS_TEAM,

		DBPICKER_LISTENER {

			@Override
			public Object getDefault() {
				return true;
			}
		},
		SAP_LISTENER {

			@Override
			public Object getDefault() {
				return true;
			}
		},

		SAP_REQUEST_TOKEN, PORTAINER_API, PORTAINER_USERNAME, PORTAINER_PASSWORD, SAR_SERVER_ENDPOINT
	}

	public SystemSettings() {
		// reflect constructor
	}

	static Long lastDisplayForce = 0L;

	private StringFieldEditor tokeneditor;

	private StringFieldEditor sapStringTokenEditor;

	@Override
	protected void createFieldEditors() {

		addTField(FIELD_TYPE.STRING, TP_SETTINGS.TARGET_PROCESS_DOMAIN, "Target Process Domain");

		Optional<FieldEditor> editor = addTField(FIELD_TYPE.CREDENTIAL, TP_SETTINGS.TARGET_PROCESS_TOKEN, null);
		if (editor.isPresent()) {
			CredentialsFieldEditor cEditor = (CredentialsFieldEditor) editor.get();
			cEditor.setConverter(this::convert);
		}

		tokeneditor = (StringFieldEditor) addTField(FIELD_TYPE.STRING, TP_SETTINGS.TARGET_PROCESS_TOKEN,
				"Target Process Token").orElseGet(() -> null);

		List<Team> teams = new TargetprocessRepository().getTeams();
		teams.add(0, new Team(-1, ""));
		String[][] teamNames = teams.stream() //
				.sorted(Comparator.comparing(team -> team.getName().toLowerCase())) //
				.map(team -> new String[] { team.getName(), team.getId() + "" }) //
				.toArray(String[][]::new);

		addTField(FIELD_TYPE.COMBO, TP_SETTINGS.TARGET_PROCESS_TEAM, "Team", (Object) teamNames);

		Optional<FieldEditor> dbEditor = addTField(FIELD_TYPE.CHECKBOX, TP_SETTINGS.DBPICKER_LISTENER,
				"enable dbpicker listener");

		if (dbEditor.isPresent()) {
			BooleanFieldEditor db = (BooleanFieldEditor) dbEditor.get();
			db.setPropertyChangeListener(event -> new GitListener());
		}

		addTField(FIELD_TYPE.CHECKBOX, TP_SETTINGS.SAP_LISTENER, "enable sap listener");
		addTField(FIELD_TYPE.STRING, TP_SETTINGS.SAR_SERVER_ENDPOINT, "sap endpoint");
		Optional<FieldEditor> sapTokenEditor = addTField(FIELD_TYPE.CREDENTIAL, TP_SETTINGS.SAP_REQUEST_TOKEN, null);
		if (sapTokenEditor.isPresent()) {
			CredentialsFieldEditor cEditor = (CredentialsFieldEditor) sapTokenEditor.get();
			cEditor.setConverter(this::convertSapCredentials);
		}
		sapStringTokenEditor = (StringFieldEditor) addTField(FIELD_TYPE.STRING, TP_SETTINGS.SAP_REQUEST_TOKEN,
				"Sap Server Token").orElseGet(() -> null);

		addTField(FIELD_TYPE.STRING, TP_SETTINGS.PORTAINER_API, "portainer api");
		addTField(FIELD_TYPE.STRING, TP_SETTINGS.PORTAINER_PASSWORD, "portainer username");
		addTField(FIELD_TYPE.STRING, TP_SETTINGS.PORTAINER_USERNAME, "portainer password");

	}

	private String convertSapCredentials(String username, String password) throws CredentialsException {
		if (username == null || password == null) {
			MessageBox messageBox = new MessageBox(this.getShell());
			messageBox.setMessage("needs both username and password");
			messageBox.open();
			return null;
		}

		String token = new SapServerRepository().requestToken(username, password);

		if (token == null) {
			throw new CredentialsException("couldnt get request token");
		}
		sapStringTokenEditor.setStringValue(token);
		return token;

	}

	private String convert(String username, String password) throws CredentialsException {

		String domain = getValue(TP_SETTINGS.TARGET_PROCESS_DOMAIN);
		if (domain == null || domain.isBlank()) {

			MessageBox messageBox = new MessageBox(this.getShell());
			messageBox.setMessage("you need to set the domain before requesting a token");
			messageBox.open();
			throw new CredentialsException("you need to set the domain before requesting a token");

		}
		if (username == null || password == null) {
			MessageBox messageBox = new MessageBox(this.getShell());
			messageBox.setMessage("needs both username and password");
			messageBox.open();
			return null;
		}

		try {

			String base64 = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

			CustomResponse response = new CustomHttp().header("Authorization", "Basic " + base64)
					.header("Accept", "application/json").target("https://" + domain + "/api/v1/Authentication")
					.request().get();
			if (response.getResponseCode() != 200) {
				MessageBox messageBox = new MessageBox(this.getShell());
				messageBox.setMessage("failed getting token code:" + response.getResponseCode());
				messageBox.open();
				return null;
			}

			String token = response.json().asJsonObject().getString("Token");
			tokeneditor.setStringValue(token);
			return token;
		} catch (IOException e) {
			SystemProperties.print(e.getMessage());
			MessageBox messageBox = new MessageBox(this.getShell());
			messageBox.setMessage("failed getting token");
			messageBox.open();
			return null;
		}
	}

	public static String getTeamId() {
		return getValue(TP_SETTINGS.TARGET_PROCESS_TEAM);
	}

	public static Auth getAuth() {
		return http -> http.addQuery("token", getValue(TP_SETTINGS.TARGET_PROCESS_TOKEN));
	}

	public static Auth getSapServerAuth() {
		return http -> http.header("authorizationtoken", getValue(TP_SETTINGS.SAP_REQUEST_TOKEN));
	}

	public static void setCustomizableView(List<CustomizableViewPanel> cfg) {
		JsonArrayBuilder arrayB = Json.createArrayBuilder();

		cfg.stream().forEach(cVP -> arrayB.add(cVP.toJson()));
		setValue(TP_SETTINGS.TARGET_PROCESS_VIEW_SETTINGS, arrayB.build().toString());
	}

	public static List<CustomizableViewPanel> getCustomizableView() {
		List<CustomizableViewPanel> els = new ArrayList<>();

		String jsonList = getValue(TP_SETTINGS.TARGET_PROCESS_VIEW_SETTINGS);
		if (jsonList.isEmpty()) {
			GridBagConstraints c2 = new GridBagConstraints();
			c2.gridx = 1;
			c2.gridy = 0;
			ProjectViewWidget w2 = new ProjectViewWidget();
			w2.setConstraints(c2);

			els.add(w2);

			GridBagConstraints c3 = new GridBagConstraints();
			c3.gridx = 0;
			c3.gridy = 1;
			BugViewWidget w3 = new BugViewWidget();
			w3.setConstraints(c3);
			els.add(w3);
			return els;
		}
		try (JsonReader createReader = Json.createReader(new StringReader(jsonList))) {
			JsonArray array = createReader.readArray();

			array.forEach(jsonS -> {

				CustomizableViewPanel instance = CustomizableViewPanel.fromJson(jsonS,
						SystemSettings.class.getClassLoader());
				if (instance != null) {
					els.add(instance);
				}

			});

			return els;
		}
	}

	@Override
	public TP_SETTINGS[] getRequiredValues() {
		return new TP_SETTINGS[] { TP_SETTINGS.TARGET_PROCESS_DOMAIN, TP_SETTINGS.TARGET_PROCESS_TOKEN,
				TP_SETTINGS.TARGET_PROCESS_TEAM };
	}

}
