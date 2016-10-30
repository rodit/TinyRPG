package net.site40.rodit.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.content.Context;

public class ZipUtil {

	private ZipFile zipFile;

	public ZipUtil(Context context, String fileName)throws IOException{
		this.zipFile = new ZipFile(new File(context.getFilesDir(), fileName));
	}
	
	public ZipFile getZipFile(){
		return zipFile;
	}

	public InputStream openFile(String path)throws IOException{
		ZipEntry entry = zipFile.getEntry(path);
		if(entry != null)
			return zipFile.getInputStream(entry);
		return null;
	}

	public byte[] readFile(String file)throws IOException{
		InputStream in = openFile(file);
		if(in == null)
			return null;
		byte[] readBuffer = new byte[4096];
		int read = 0;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while((read = in.read(readBuffer)) > 0)
			baos.write(readBuffer, 0, read);
		in.close();
		byte[] full = baos.toByteArray();
		baos.close();
		return full;
	}

	public void close()throws IOException{
		zipFile.close();
	}
}
