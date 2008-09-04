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
import java.util.ArrayList;

/**
 * Conversions.
 * 
 * @author Jeldert Pol
 */
public final class Conversion {

	/**
	 * Private constructor, so this class cannot be initiated.
	 */
	private Conversion() {
		// Nothing to do
	}

	/**
	 * Converts an object to an array of bytes.
	 * 
	 * @param object
	 *            The object to convert.
	 * @return The converted object, or <code>null</code> in case of an error.
	 */
	public static byte[] objectToByte(final Object object) {
		byte[] byteArray = null;

		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(
					byteArrayOutputStream);
			objectOutputStream.writeObject(object);
			byteArray = byteArrayOutputStream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return byteArray;
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
		Object object = null;

		try {
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
					bytes);
			ObjectInputStream objectInputStream = new ObjectInputStream(
					byteArrayInputStream);
			object = objectInputStream.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return object;
	}

	/**
	 * Reads a file to an {@link ArrayList} of {@link Integer}, and converts
	 * that it into an array of bytes.
	 * 
	 * TODO move to client side, or stay in common?
	 * 
	 * @param file
	 *            The file to convert.
	 * @return The converted file, or <code>null</code> in case of an error.
	 * 
	 * @throws FileNotFoundException
	 *             The file can not be found.
	 * @throws IOException
	 *             Error while reading file.
	 */
	public static byte[] fileToByte(final File file)
			throws FileNotFoundException, IOException {
		byte[] bytes = null;

		FileInputStream fis = new FileInputStream(file);

		// ArrayList saves about 25% over a Vector for text, and 7% for
		// binary files.
		ArrayList<Integer> al = new ArrayList<Integer>();

		int read;
		while ((read = fis.read()) != -1) {
			al.add(read);
		}
		fis.close();

		bytes = objectToByte(al);

		return bytes;
	}

	/**
	 * Reads an array of bytes, and writes it to a file.
	 * 
	 * TODO move to client side, or stay in common?
	 * 
	 * @param bytes
	 *            The content to be written.
	 * @param file
	 *            The file to be written to.
	 * 
	 * @throws FileNotFoundException
	 *             The file can not be found.
	 * @throws IOException
	 *             Error while writing file.
	 */
	public static void byteToFile(final byte[] bytes, final File file)
			throws FileNotFoundException, IOException {
		FileOutputStream fos = new FileOutputStream(file);

		ArrayList<Integer> al = (ArrayList<Integer>) byteToObject(bytes);
		for (Integer integer : al) {
			fos.write(integer);
		}
		fos.flush();
	}

}
