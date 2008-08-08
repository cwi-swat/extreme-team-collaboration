package nl.jeldertpol.xtc.common.conversion;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

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
	 * Converts an {@link InputStream} to an array of bytes.
	 * 
	 * Note that {@link #objectToByte(Object)} does not work for an
	 * {@link InputStream}, because it is not serializable.
	 * 
	 * @param inputStream
	 *            The {@link InputStream} to convert.
	 * @return The converted {@link InputStream}, or <code>null</code> in case
	 *         of an error.
	 * 
	 * @see #byteToInputStream(byte[])
	 * @see #objectToByte(Object)
	 */
	public static byte[] inputStreamToByte(final InputStream inputStream) {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));

		List<Integer> result = new ArrayList<Integer>();
		int read;

		try {
			while ((read = bufferedReader.read()) != -1) {
				result.add(read);
			}
			return objectToByte(result);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static InputStream byteToInputStream(final byte[] listOfIntegers) {
		List<Integer> integers = (List<Integer>) byteToObject(listOfIntegers);

		byte[] bytes = new byte[integers.size()];

		for (int i = 0; i < integers.size(); i++) {
			byte b = Byte.parseByte("" + integers.get(i));
			bytes[i] = b;
		}

		InputStream inputStream = new ByteArrayInputStream(bytes);
		return inputStream;
	}
}
