package builder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.osgi.service.resolver.BundleSpecification;
import org.eclipse.pde.internal.core.FeatureModelManager;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.bundle.BundlePlugin;
import org.eclipse.pde.internal.core.exports.SiteBuildOperation;
import org.eclipse.pde.internal.core.feature.FeaturePlugin;
import org.eclipse.pde.internal.core.ibundle.IBundlePluginModelBase;
import org.eclipse.pde.internal.core.ifeature.IFeatureModel;
import org.eclipse.pde.internal.core.ifeature.IFeaturePlugin;
import org.eclipse.pde.internal.core.isite.ISiteFeature;
import org.eclipse.pde.internal.core.site.WorkspaceSiteModel;
import org.osgi.framework.Version;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import cfg.BuildSettings;
import socialdear.logging.SystemProperties;
import socialdear.util.files.FileParser;
import socialdear.views.ImplementedPreferences;
import views.CustomBuildView;

@SuppressWarnings("restriction")
public class Builder {

	List<String> incrementedPlugins = new ArrayList<>();

	private class PluginContainer {

		public PluginContainer(BundleDescription description, IProject iProject) {
			this.description = description;
			this.iProject = iProject;
		}

		BundleDescription description;
		IProject iProject;

	}

	private IProject project;
	private CustomBuildView customBuildView;

	public Builder(IProject project, CustomBuildView customBuildView) {
		this.project = project;
		this.customBuildView = customBuildView;
	}

	public void build(Boolean incrementVErsions) throws CoreException, IOException, JSchException, SftpException {

		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = new Date();
		customBuildView.addMessage(formatter.format(date));
		IFile siteXmlIFile = project.getFile("site.xml");
		File siteXmlFile = new File(siteXmlIFile.getLocationURI());
		String siteXml = FileParser.readFile(siteXmlFile);
		WorkspaceSiteModel wsm = new WorkspaceSiteModel(siteXmlIFile);
		wsm.load();

		ISiteFeature[] features = wsm.getSite().getFeatures();
		IFeatureModel[] featureModels = getFeatureModels(features);

		List<IProject> projects = FileParser.getProjects();
		for (IProject pr : projects) {
			if (pr.getName().equals("a_plugin_socialdear")) {
				pr.getFile("bin").delete(true, null);
				break;
			}
		}

		Map<String, PluginContainer> ids = new HashMap<>();

		loadPlugins(projects, ids);

		if (incrementVErsions) {
			customBuildView.addMessage("incrementing versions ");
			incrementVersion(featureModels, ids);
		}

		wsm.load();

		ISiteFeature[] features2 = wsm.getSite().getFeatures();
		IFeatureModel[] featureModels2 = getFeatureModels(features2);

		SiteBuildOperation sbo = new SiteBuildOperation(featureModels2, wsm, "Build Update Site");
		customBuildView.addMessage("building");
		sbo.schedule();

		try {
			upload();
		} catch (Exception e) {
			customBuildView.addMessage("exception in upload " + e.getMessage());
			SystemProperties.print(e);
			// no errors between overwriting site.xml and resetting it
		}

		FileParser.writeFile(siteXmlFile, siteXml);
		siteXmlIFile.refreshLocal(IResource.DEPTH_ZERO, null);

		customBuildView.addMessage("done");

	}

	private void loadPlugins(List<IProject> projects, Map<String, PluginContainer> ids) throws CoreException {
		for (IProject iProject : projects) {
			IFile file = iProject.getFile(JarFile.MANIFEST_NAME);
			if (file.exists()) {
				BundleDescription description = PDECore.getDefault().getModelManager().getState()
						.addBundle(new File(iProject.getLocationURI()), -1);

				String pluginId = description.getName();

				ids.put(pluginId, new PluginContainer(description, iProject));
			}

		}
	}

	private void incrementVersion(IFeatureModel[] featureModels, Map<String, PluginContainer> ids)
			throws IOException, CoreException {
		for (IFeatureModel feature : featureModels) {
			customBuildView.addMessage("checking increment for feature " + feature.getInstallLocation());

			for (IFeaturePlugin plugin : feature.getFeature().getPlugins()) {
				FeaturePlugin featurePlugin = (FeaturePlugin) plugin;
				BundlePlugin pluginBase = (BundlePlugin) featurePlugin.getPluginBase();
				if (pluginBase == null) {
					SystemProperties.print("pluginBase is null");
					continue;
				}
				IBundlePluginModelBase iBundlePluginModelBase = (IBundlePluginModelBase) pluginBase.getModel();
				if (iBundlePluginModelBase == null) {
					SystemProperties.print("iBundlePluginModelBase is null " + plugin.toString());
					continue;
				}
				BundleDescription description = iBundlePluginModelBase.getBundleDescription();

				BundleSpecification[] requiredBundles = description.getRequiredBundles();

				for (BundleSpecification bundle : requiredBundles) {
					String dependency = bundle.getName();
					PluginContainer dependencyPlugin = ids.get(dependency);
					if (dependencyPlugin != null
							&& !incrementedPlugins.contains(dependencyPlugin.description.getName())) {
						incrementedPlugins.add(dependencyPlugin.description.getName());
						Version version = dependencyPlugin.description.getVersion();
						Version newVersion = new Version(version.getMajor(), version.getMinor(),
								version.getMicro() + 1);

						customBuildView.addMessage("incrementing for plugin "
								+ dependencyPlugin.description.getLocation() + " to " + newVersion.toString());
						FileParser.replaceInFile(
								dependencyPlugin.description.getLocation() + "/" + JarFile.MANIFEST_NAME,
								"Bundle-Version: (.*)", "Bundle-Version: " + newVersion.toString());
						dependencyPlugin.iProject.getFile(JarFile.MANIFEST_NAME).refreshLocal(IResource.DEPTH_ZERO,
								null);
						dependencyPlugin.toString();
					}
				}
			}
		}
	}

	private IFeatureModel[] getFeatureModels(ISiteFeature[] sFeatures) {
		ArrayList<IFeatureModel> list = new ArrayList<>();
		FeatureModelManager featureModelManager = new FeatureModelManager();
		featureModelManager.getWorkspaceModels();
		for (ISiteFeature siteFeature : sFeatures) {
			IFeatureModel model = featureModelManager.findFeatureModelRelaxed(siteFeature.getId(),
					siteFeature.getVersion());
			if (model != null)
				list.add(model);
		}
		return list.toArray(new IFeatureModel[list.size()]);
	}

	private String getPath() {
		return BuildSettings.getValue(BuildSettings.PROPERTIES.TARGET_RELATIVE_PATH);
	}

	public List<String> upload() throws JSchException, SftpException, FileNotFoundException {
		customBuildView.addMessage("uploading");
		List<String> changes = new ArrayList<>();
		ChannelSftp channel = getSFTP();
		File buildDirectoy = new File(project.getLocationURI());
		customBuildView.addMessage(getPath());
		channel.cd(getPath());
		List<LsEntry> items = toList(channel.ls("."));
		for (File file : buildDirectoy.listFiles()) {
			channel.cd(getPath());
			if (file.isFile() && !file.getName().contentEquals(".project")) {
				changes.add(file.getName());
				customBuildView.addMessage("adding " + file.getName());
				channel.put(new FileInputStream(file), file.getName());

			} else if (file.isDirectory()) {
				uploadDirectories(changes, channel, items, file);
			}
		}

		return changes;
	}

	private void uploadDirectories(List<String> changes, ChannelSftp channel, List<LsEntry> items, File file)
			throws SftpException, FileNotFoundException {
		if (items.stream().noneMatch(item -> item.getAttrs().isDir() && item.getFilename().equals(file.getName()))) {
			channel.mkdir(file.getName());
		}
		channel.cd(getPath() + "/" + file.getName());

		List<LsEntry> exiistingFiles = toList(channel.ls("."));
		for (File pluginOrFeature : file.listFiles()) {
			if (exiistingFiles.stream().anyMatch(entry -> entry.getFilename().equals(pluginOrFeature.getName()))) {
				continue;
			}
			changes.add(pluginOrFeature.getName());
			customBuildView.addMessage("adding " + pluginOrFeature.getName());
			channel.put(new FileInputStream(pluginOrFeature), pluginOrFeature.getName());
		}
	}

	private List<LsEntry> toList(List<?> vec) {
		return vec.stream()//
				.map(obj -> (LsEntry) obj)//
				.collect(Collectors.toList());
	}

	private ChannelSftp getSFTP() throws JSchException {
		Channel channel = getConnection().openChannel("sftp");
		channel.connect();
		return (ChannelSftp) channel;

	}

	private Session getConnection() throws JSchException {
		JSch jsch = new JSch();
		String privateKey = ImplementedPreferences.getValue(BuildSettings.PROPERTIES.FTP_SSH_KEY);
		jsch.addIdentity(privateKey);
		String user = BuildSettings.getValue(BuildSettings.PROPERTIES.FTP_USER);
		String host = BuildSettings.getValue(BuildSettings.PROPERTIES.FTP_HOST);
		Session session = jsch.getSession(user, host, 22);
		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
		session.connect();
		return session;

	}
}
