package a_plugin_struts;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

public class FindRelativeAngularPAth {

	
	@Test
	public void relativetest() {
		File relative = new File("/service/konfiguration-branching-include.xml");
		assertEquals("\\service", relative.getParentFile().toString());
	}
	
	
	@Test
	public void url() {
		String t="../../.././service/konfiguration/konfiguration-branching.component";
		//String path=t.matches(regex)replaceFirst("\\w(.*)\\/", "$1");
		Pattern p = Pattern.compile("\\w(.*)\\/");
		Matcher matcher = p.matcher(t);
		matcher.find();
		String test=matcher.group();
		assertEquals("service/konfiguration/", test);
	}
	
	
	@Test
	public void path() {
			
			File componentModule = new File("D:\\webapps\\sources\\webapps\\Angular\\src\\app\\service\\konfiguration\\konfiguration-branching.module.ts");
			File angularModule = new File("D:\\webapps\\sources\\webapps\\Angular\\src\\app\\app.module.audi.ts");
		
			File sharedRootFolder=componentModule;
			while(!angularModule.getAbsolutePath().contains(sharedRootFolder.getAbsolutePath())){
				sharedRootFolder=sharedRootFolder.getParentFile();
			}
			String relativeLevel=angularModule.getAbsolutePath().replace(sharedRootFolder.getAbsolutePath(),"").replace("\\"+angularModule.getName(), "");
			int levelUp=relativeLevel.split("\\\\").length-1;
			String moduleIMport="";
			
			if(levelUp>0) {
				for(int i=0;i<levelUp;i++) {
					moduleIMport+="../";
				}
			}else {
				moduleIMport="./";
			}
			
			moduleIMport+=componentModule.getAbsolutePath().replace(sharedRootFolder.getAbsolutePath()+"\\","")
					.replaceAll("\\\\", "/") //
					.replace(".ts",""); //
				
			assertEquals(moduleIMport, "../test//service/konfiguration/konfiguration-branching.module");
	}
}
