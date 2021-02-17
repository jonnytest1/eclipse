package test;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;

public class TestFXInstall {

	@Test
	public void testGetEclipseInI() {

		File file = new File("C:\\Users\\Jonathan\\eclipse\\jee-2019-03\\eclipse\\eclipse.ini");
		assertTrue(file.exists());
	}
}
