package a_plugin_struts.service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import a_plugin_struts.StrutsProperties;
import a_plugin_struts.StrutsProperties.STRUTS_SETTINGS;
import a_plugin_struts.model.ActionBeanObject;
import socialdear.logging.SystemProperties;
import socialdear.util.files.FileParser;

public class StrutsRegistration {
		private static final String IMPORT = "import { ";
		private String name;
	private ActionBeanObject actionobj;
	private String componentBase;
	private ConverterAttributes converterAttributes;


		public StrutsRegistration(ActionBeanObject actionobj, ConverterAttributes converterAttributes) {
			this.name = converterAttributes.getBaseName();
			this.actionobj = actionobj;
			this.converterAttributes = converterAttributes;
			
			componentBase = Arrays.asList(this.name.split("-"))//
					.parallelStream() //
					.map(word -> word.substring(0, 1).toUpperCase() + word.substring(1)).collect(Collectors.joining(""));
			
			
		}
		
		
		public void registrer() {
			registrerRestRegistry();
			registerAngularModule();
			registerAngularRoutes();
		}


		private String getRelativeImport(File componentModule,File angularModule) {
		
			File sharedRootFolder=componentModule;
			while(!angularModule.getAbsolutePath().contains(sharedRootFolder.getAbsolutePath())){
				sharedRootFolder=sharedRootFolder.getParentFile();
			}
			String relativeLevel=angularModule.getAbsolutePath().replace(sharedRootFolder.getAbsolutePath(),"").replace("\\"+angularModule.getName(), "");
			int levelUp=relativeLevel.split("\\\\").length-1;
			StringBuilder moduleIMport=new StringBuilder();
			
			if(levelUp>0) {
				for(int i=0;i<levelUp;i++) {
					moduleIMport.append("../");
				}
			}else {
				moduleIMport.append("./");
			}
			
			moduleIMport.append(componentModule.getAbsolutePath().replace(sharedRootFolder.getAbsolutePath()+"\\","")
					.replaceAll("\\\\", "/") //
					.replace(".ts","")); //
			return moduleIMport.toString();
		}
		
		private void registerAngularRoutes(){
			File angularRoutes = new File(StrutsProperties.getValue(STRUTS_SETTINGS.ANGULAR_ROUTE));
			try {
				String restRegistryContent=FileParser.readFile(angularRoutes);
				String relativeImport = getRelativeImport(converterAttributes.getComponentFile(), angularRoutes);
				String addingImportLine=IMPORT+converterAttributes.getAngularComponentClassName()+" } from '"+relativeImport+"';";
				if(restRegistryContent.contains(addingImportLine)) {
					SystemProperties.print("skipping registering angular route");
					return;
				}
				
				Pattern p = Pattern.compile("\\w(.*)\\/");
				Matcher matcher = p.matcher(relativeImport);
				matcher.find();
				String path=matcher.group();
				
				
				String[] lines=restRegistryContent.split("\n");
				int lastImportLine=-1;
				boolean withinImport=false;
				int bracketCount=-1;
				for(int i=0;i<lines.length;i++) {
						if(lines[i].startsWith(IMPORT)) {
							lastImportLine=i;
						}
						if(lines[i].contains("const routes: Routes = [") && !withinImport) {
							withinImport=true;
							bracketCount=1;
						}
						if(withinImport) {
							bracketCount+=lines[i].split("\\[").length-1;
							bracketCount-=lines[i].split("\\]").length-1;
							if(bracketCount==0) {
								lines[i-1]+=",\n	{\n		path: '"+path+"',\n		component: "+converterAttributes.getAngularComponentClassName()+",\r\n" + 
										"		canActivate: [\r\n" + 
										"			LoggedInGuard\r\n" + 
										"		]\n	}";
								withinImport=false;
							}
							
						}
				}
				if(bracketCount==-1) {
					SystemProperties.print("didnt find imports start");
					throw new RuntimeException("failed registering component module in angular module 'import: [' not found");
				}
				if(lastImportLine==-1) {
					SystemProperties.print("didnt find import line");
					throw new RuntimeException("failed registering import in angular module");
				}
				lines[lastImportLine]+="\n"+addingImportLine;
				
				String addedFileCOntent=String.join("\n", lines);
				FileParser.writeFile(angularRoutes, addedFileCOntent+"\n");
			} catch (IOException e) {
				SystemProperties.print(e);
			}
			
		}
		
		private void registerAngularModule() {
			File angularModule = new File(StrutsProperties.getValue(STRUTS_SETTINGS.ANGULAR_MODULE));
			try {
				File componentModule=	converterAttributes.getModuleFile();			
				
				String restRegistryContent=FileParser.readFile(angularModule);
				String addingImportLine=IMPORT+converterAttributes.getModuleClassName()+" } from '"+getRelativeImport(componentModule, angularModule)+"';";
				if(restRegistryContent.contains(addingImportLine)) {
					return;
				}
				String[] lines=restRegistryContent.split("\n");
				int lastImportLine=-1;
				boolean withinImport=false;
				int squareBracketCount=-1;
				for(int i=0;i<lines.length;i++) {
						if(lines[i].startsWith(IMPORT)) {
							lastImportLine=i;
						}
						if(lines[i].contains("imports: [") && !withinImport) {
							withinImport=true;
							squareBracketCount=1;
						}
						if(withinImport) {
							squareBracketCount+=lines[i].split("\\[").length-1;
							squareBracketCount-=lines[i].split("\\]").length-1;
							if(squareBracketCount==0) {
								lines[i-1]+=",\n		"+converterAttributes.getModuleClassName()+",";
								withinImport=false;
							}
							
						}
				}
				if(squareBracketCount==-1) {
					SystemProperties.print("didnt find imports start");
					throw new RuntimeException("failed registering component module in angular module 'import: [' not found");
				}
				if(lastImportLine==-1) {
					SystemProperties.print("didnt find import line");
					throw new RuntimeException("failed registering import in angular module");
				}
				lines[lastImportLine]+="\n"+addingImportLine;
				
				String addedFileCOntent=String.join("\n", lines);
				FileParser.writeFile(angularModule, addedFileCOntent);
			} catch (IOException e) {
				SystemProperties.print(e);
			}
			
		}


		private void registrerRestRegistry() {
			File restREgistry = new File(StrutsProperties.getValue(STRUTS_SETTINGS.REGISTRY));
			try {
				String restRegistryContent=FileParser.readFile(restREgistry);
				String addingImportLine="import de.brandad.service."+componentBase+"Webservice;";
				String addingRegisterLine="		classes.add("+componentBase+"Webservice.class);";
				if(restRegistryContent.contains(addingImportLine)) {
					return;
				}
				String[] lines=restRegistryContent.split("\n");
				int lastImportLine=-1;
				int lastAddClasLine=-1;
				boolean foundSwaggerStart=false;
				for(int i=0;i<lines.length;i++) {
						if(lines[i].startsWith("import ")) {
							lastImportLine=i;
						}
						if(lines[i].contains("classes.add") && !foundSwaggerStart) {
							lastAddClasLine=i;
						}
						if(lines[i].contains("activateSwaggerOnTestServer")) {
							foundSwaggerStart=true;
						}
				}
				
				lines[lastImportLine]=lines[lastImportLine]+"\n"+addingImportLine;
				lines[lastAddClasLine]=lines[lastAddClasLine]+"\n"+addingRegisterLine;
				
				String addedFileCOntent=String.join("\n", lines);
				FileParser.writeFile(restREgistry, addedFileCOntent);
			} catch (IOException e) {
				SystemProperties.print(e);
			}
		}
}
