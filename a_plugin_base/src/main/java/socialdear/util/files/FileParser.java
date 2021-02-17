package socialdear.util.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.stream.Collectors;

import javax.persistence.Entity;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
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

/**
 * 1.2
 */
public class FileParser {

	private FileParser() {
		// private
	}

	public static List<IProject> getProjects() {
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		List<IProject> projects = Arrays.asList(workspaceRoot.getProjects());
		return projects;
	}

	/*
	 * public static List<IProject> getProjects() { IWorkspaceRoot workspaceRoot =
	 * ResourcesPlugin.getWorkspace().getRoot(); List<IProject> projects =
	 * Arrays.asList(workspaceRoot.getProjects()); List<IProject> filteredProjects =
	 * new ArrayList<>(); projects.forEach(project -> { if (project.isOpen() &&
	 * getMigrationFiles(project) != null) { filteredProjects.add(project); } });
	 * return projects; }
	 */
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

	public static String readFile(File f) throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(f))) {
			StringBuilder str = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				str.append(line + "\n");
			}
			return str.toString();
		}
	}

	public static boolean writeFile(File f, String content) {
		System.out.println("writing to " + f.getAbsolutePath());
		f.getParentFile().mkdirs();
		try (FileWriter fw = new FileWriter(f)) {
			fw.write(content);
			return true;
		} catch (IOException e) {
			return false;
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

	public static URLClassLoader getClassLoader(IJavaProject javaProject) {

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
			List<String> models = FileParser.getModels(javaProject);

			Map<String, String> contents = new HashMap<>();
			for (String classPath : models) {
				try {
					Class<?> loadClass = classLoader.loadClass(classPath);
					for (Annotation annotation : loadClass.getDeclaredAnnotations()) {
						if (annotation.annotationType().toString().equals(Entity.class.toString())) {
							try {
								String content = new BufferedReader(new InputStreamReader(project.getFile("src/main/java/" + classPath.replace(".", "/") + ".java").getContents())).lines().collect(Collectors.joining("\n"));
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
							IPackageFragment modelPackage = rootPackage.getPackageFragment(packageObject.getElementName());
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

	public static String getFile(String path, Object o) throws IOException {
		InputStream stream = o.getClass().getResourceAsStream("/src/main/resources/" + path);
		try (Scanner s = new Scanner(stream)) {
			s.useDelimiter("\\A");
			return s.next();
		} catch (NoSuchElementException e) {
			throw new IOException(e);
		}

	}

	public static void replaceInFile(String file, String pattern, String replacement) throws IOException {
		File f = new File(file);
		String content = readFile(f);
		String replaced = content.replaceAll(pattern, replacement);
		writeFile(f, replaced);
	}

	public static List<File> getAllFiles(IProject project) {
		return getAllFiles(new File(project.getLocationURI()), new ArrayList<String>());

	}

	public static List<File> getAllFiles(IProject project, List<String> list) {
		return getAllFiles(new File(project.getLocationURI()), list);

	}

	public static List<File> getAllFiles(File topLevelDirectory, List<String> list) {
		ArrayList<File> files = new ArrayList<>();
		for (File file : topLevelDirectory.listFiles()) {
			if (file.isDirectory()) {
				if (file.getName().startsWith(".") || file.getName().equals("bin") || file.getName().startsWith("generated") || file.getName().equals("build") || file.getName().equals("test") || list.contains(file.getName())) {
					continue;
				}
				files.addAll(getAllFiles(file, list));
			} else {
				files.add(file);
			}
		}
		return files;

	}

	public static IFile createProjectFile(File file) {
		for (IProject project : FileParser.getProjects()) {
			IFile projectFile = project.getFile(project.getLocationURI().relativize(file.toURI()).toString());
			if (!projectFile.exists()) {
				continue;
			}
			return projectFile;
		}
		return null;

	}

}
