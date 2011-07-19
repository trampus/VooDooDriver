package voodoodriver;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * Loads an external java class file into a java Class.
 * 
 * @author trichmond
 *
 */
public class VDDClassLoader extends ClassLoader {

	public VDDClassLoader(ClassLoader parent) {
		super(parent);
	}

	/**
	 * Loads a class by name from a java class file.
	 * 
	 * @param className The name of the class to load.
	 * @param classFile The java .class file to load into memory.
	 * @return Class
	 * @throws ClassNotFoundException
	 */
	public Class loadClass(String className, String classFile) throws ClassNotFoundException {
		Class result = null;
		byte[] classData;
		
		try {
			File classfd = new File(classFile);
			FileInputStream fin = new FileInputStream(classfd);
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int data = fin.read();

		    while(data != -1){
		        buffer.write(data);
		        data = fin.read();
		    }
		    fin.close();
		    
		    classData = buffer.toByteArray();
		    result = defineClass(className, classData, 0, classData.length);
			
		} catch (Exception exp) {
			exp.printStackTrace();
			result = null;
		}
		
		return result;
	}
}
