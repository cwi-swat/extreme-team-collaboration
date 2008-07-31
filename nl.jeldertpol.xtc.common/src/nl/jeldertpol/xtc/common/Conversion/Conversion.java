package nl.jeldertpol.xtc.common.Conversion;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
	 * @return The converted object, or null in case of an error.
	 */
	public static byte[] ObjectToByte(Object object) {
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
	 * @return The converted array of bytes, or null in case of an error.
	 */
	public static Object ByteToObject(byte[] bytes) {
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
}
