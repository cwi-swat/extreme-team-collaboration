package nl.jeldertpol.xtc.common.conversion;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

/**
 * Conversions.
 * 
 * @author Jeldert Pol
 */
public class Conversion {

	/**
	 * Converts an object to an array of bytes.
	 * 
	 * @param object
	 *            The object to convert.
	 * @return The converted object, or <code>null</code> in case of an error.
	 */
	public static byte[] objectToByte(final Object object) {
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(
					byteArrayOutputStream);
			objectOutputStream.writeObject(object);
			return byteArrayOutputStream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Converts an array of bytes to an object.
	 * 
	 * @param bytes
	 *            The array of bytes to convert.
	 * @return The converted array of bytes, or <code>null</code> in case of an
	 *         error.
	 */
	public static Object byteToObject(final byte[] bytes) {
		try {
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
					bytes);
			ObjectInputStream objectInputStream = new ObjectInputStream(
					byteArrayInputStream);
			return objectInputStream.readObject();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Reads a file, and converts it into an array of bytes.
	 * 
	 * @param file
	 *            The file to convert.
	 * @return The converted file, or <code>null</code> in case of an error.
	 * 
	 *         TODO move to client side, or stay in common?
	 */
	public static byte[] fileToByte(final File file) {
		byte[] bytes = null;

		try {
			FileInputStream fis = new FileInputStream(file);

			Vector<Integer> v = new Vector<Integer>();
			int read;
			while ((read = fis.read()) != -1) {
				v.add(read);
			}
			fis.close();

			bytes = objectToByte(v);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bytes;
	}

	/**
	 * Reads an array of bytes, and writes it to a file.
	 * 
	 * @param bytes
	 *            The content to be written.
	 * @param file
	 *            The file to be written to.
	 * 
	 *            TODO move to client side, or stay in common?
	 */
	public static void byteToFile(final byte[] bytes, final File file) {
		try {
			FileOutputStream fos = new FileOutputStream(file);

			Vector<Integer> v = (Vector<Integer>) byteToObject(bytes);
			for (int i = 0; i < v.size(); i++) {
				fos.write(v.get(i));
			}
			fos.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
