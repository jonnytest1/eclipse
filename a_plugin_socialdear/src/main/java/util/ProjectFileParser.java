package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;

import socialdear.http.logging.Logging;
import socialdear.http.logging.Logging.LogLevel;
import socialdear.logging.SystemProperties;
import sql.SQLCommand;

public class ProjectFileParser {

	private ProjectFileParser() {
		// private
	}

	public static List<File> getMigrationFiles(IProject project) {
		try {
			IFolder folder = project.getFolder("src/main/resources/db/migration");
			File[] files = new File(folder.getLocationURI()).listFiles();
			if (files == null) {
				return new ArrayList<>();
			}
			return Arrays.asList(files);
		} catch (Exception e) {
			SystemProperties.printInfo(e);
			return new ArrayList<>();
		}
	}

	static File getModelFolderRek(File parent) {
		File[] files = parent.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				if (file.getName().equals("Branch")) {
					return file;
				} else {
					File target = getModelFolderRek(file);
					if (target != null) {
						return target;
					}
				}

			}
		}
		return null;
	}

	private static URLClassLoader getClassLoader(IJavaProject javaProject) {

		try {
			String[] classPathEntries = JavaRuntime.computeDefaultRuntimeClassPath(javaProject);

			List<URL> urlList = new ArrayList<>();
			for (int i = 0; i < classPathEntries.length; i++) {
				String entry = classPathEntries[i];
				IPath path = new Path(entry);
				URL url = path.toFile().toURI().toURL();
				urlList.add(url);
			}
			ClassLoader parentClassLoader = javaProject.getClass().getClassLoader();
			URL[] urls = urlList.toArray(new URL[urlList.size()]);
			return new URLClassLoader(urls, parentClassLoader);
		} catch (CoreException | MalformedURLException e) {
			SystemProperties.printInfo(e);
		}
		return null;

	}

	public static List<Class<?>> getModelsAsClass(IProject project) {

		try {
			List<Class<?>> classesList = new ArrayList<>();
			project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());
			project.open(new NullProgressMonitor() /* IProgressMonitor */);
			IJavaProject javaProject = JavaCore.create(project);

			URLClassLoader classLoader = getClassLoader(javaProject);
			List<String> models = ProjectFileParser.getModels(javaProject);

			Map<String, String> contents = new HashMap<>();
			for (String classPath : models) {
				try {
					Class<?> loadClass = classLoader.loadClass(classPath);
					for (Annotation annotation : loadClass.getDeclaredAnnotations()) {
						if (annotation.annotationType().toString().equals(javax.persistence.Entity.class.toString())) {
							try {
								String content = new BufferedReader(new InputStreamReader(
										project.getFile("src/main/java/" + classPath.replace(".", "/") + ".java")
												.getContents())).lines().collect(Collectors.joining("\n"));
								contents.put(loadClass.getName(), content);
							} catch (Exception e) {
								e.printStackTrace();
								// nohing
							}

							classesList.add(loadClass);
						}
					}
				} catch (ClassNotFoundException e) {
					SystemProperties.printInfo(e);
				}
			}
			contents.put("message", "javamodels");
			Logging.logRequest(contents, LogLevel.INFO, null, null);
			return classesList;
		} catch (CoreException e) {
			SystemProperties.printInfo(e);
		}
		return new ArrayList<>();
	}

	public static List<String> getModels(IJavaProject javaProject) {
		List<String> classList = new ArrayList<>();
		try {
			IPackageFragmentRoot[] roots = javaProject.getPackageFragmentRoots();
			for (IPackageFragmentRoot rootPackage : roots) {
				if (rootPackage.getPath().toString().contains("src/main/java")) {
					IJavaElement[] packages = rootPackage.getChildren();
					for (IJavaElement packageObject : packages) {
						if (packageObject.getElementName().contains("Branch")) {
							String packagePath = packageObject.getElementName() + ".";
							IPackageFragment modelPackage = rootPackage
									.getPackageFragment(packageObject.getElementName());
							modelPackage.open(SubMonitor.convert(new NullProgressMonitor()));
							IJavaElement[] classes = modelPackage.getChildren();
							for (IJavaElement classElement : classes) {
								classList.add(packagePath + classElement.getElementName().replace(".java", ""));
							}
						}
					}
					break;
				}
			}
			return classList;
		} catch (JavaModelException e) {
			return new ArrayList<>();
		}
	}

	static File getModelFolder(IProject project) {
		File mainFolder = new File(project.getFolder("src/main/java").getLocationURI());
		return getModelFolderRek(mainFolder);

	}

	public static List<SQLCommand> getSQLCommands(IProject project) {
		List<SQLCommand> commands = new ArrayList<>();
		List<File> files = getMigrationFiles(project);
		for (File file : files) {
			InputStream fileResource;
			try {
				fileResource = new FileInputStream(file);

				StringWriter writer = new StringWriter();

				IOUtils.copy(fileResource, writer, StandardCharsets.UTF_8);

				String sql = writer.toString();
				List<String> sqlCommands = Arrays.asList(sql.split(";"));
				for (String sqlCommand : sqlCommands) {
					if (!sqlCommand.trim().equals("")) {

						commands.add(SQLCommand.parse(sqlCommand.toUpperCase().trim(), file.getName()));
					}
				}
			} catch (IOException e) {
				SystemProperties.printInfo(e);
			}
		}
		return commands;
	}
}
