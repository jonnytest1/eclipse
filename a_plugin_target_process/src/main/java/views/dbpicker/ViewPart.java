package views.dbpicker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cfg_tp.SystemSettings;
import http.PortainerRepository;
import socialdear.views.BaseViewPart;
import socialdear.views.ImplementedFieldEditorPreferencePage;
import socialdear.views.component.CustomElementPanel;
import socialdear.views.component.filter.implemented.FilterScrollComponent;
//import socialdear.views.component.filter.implemented.FilterScrollComponent;

/**
 * @since 2.1
 */
public class ViewPart extends BaseViewPart {

	private PortainerRepository portainerRepo;

	private List<String> names = new ArrayList<>();

	public ViewPart() {
		super();
		portainerRepo = new PortainerRepository();
	}

	@Override
	protected CustomElementPanel createElement() {
		fetchNames();
		return new FilterScrollComponent<String, DBComponent>(names, DBComponent.class);
	}

	@Override
	protected void recreate() {
		fetchNames();
		super.recreate();

	}

	private void fetchNames() {
		try {
			names.clear();
			names.addAll(portainerRepo.getContainerNames().stream()//
					.map(str -> {
						String[] split = str.split("-", 2);
						if (split.length == 1) {
							return str;
						}
						return split[1];
					}) //
					.distinct()//
					.collect(Collectors.toList()));
		} catch (IOException e) {
			names.clear();
			names.addAll(Arrays.asList("failed getting dbs", "test"));
		}
	}

	@Override
	public Class<? extends ImplementedFieldEditorPreferencePage> getSystem() {
		return SystemSettings.class;
	}

	@Override
	protected boolean shouldAddRefreshButton() {
		return true;
	}

}
