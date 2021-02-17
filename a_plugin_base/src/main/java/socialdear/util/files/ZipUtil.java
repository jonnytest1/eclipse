package socialdear.util.files;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtil {

	private static final int BUFFER_SIZE = 4096;

	public static void extractResourceToFile(String resource, String target) throws IOException {

		InputStream fx = ZipUtil.class.getResourceAsStream(resource);
		ZipInputStream zipIn = new ZipInputStream(fx);

		FilePermission permission = new FilePermission(target, "write");
		ZipEntry entry = zipIn.getNextEntry();
		while (entry != null) {
			String filePath = target + File.separator + entry.getName();
			File file = new File(filePath);
			if (!entry.isDirectory()) {
				// if the entry is a file, extracts it
				extractFile(zipIn, file, permission);
			} else {
				Files.createDirectories(file.toPath());
			}
			zipIn.closeEntry();
			entry = zipIn.getNextEntry();
		}
		zipIn.close();

	}

	private static void extractFile(ZipInputStream zipIn, File file, FilePermission permission) throws IOException {
		if (!file.exists()) {
			if (!file.createNewFile()) {
				throw new IOException("couldnt create file");
			}
		}
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
		byte[] bytesIn = new byte[BUFFER_SIZE];
		int read = 0;
		while ((read = zipIn.read(bytesIn)) != -1) {
			bos.write(bytesIn, 0, read);
		}
		bos.close();
	}
}
