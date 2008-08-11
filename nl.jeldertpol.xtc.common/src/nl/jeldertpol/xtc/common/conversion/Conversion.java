package nl.jeldertpol.xtc.common.conversion;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
//	public static byte[] inputStreamToByte(final InputStream inputStream) {
//		DataInputStream dis = new DataInputStream(inputStream);
//		List<Byte> result = new ArrayList<Byte>();
//		byte[] bytes = null;
//		
//		try {
//			// An error will be thrown when EOF is reached, so loop will end.
//			while (true) {
//				result.add(dis.readByte());
//			}
//		} catch (EOFException e) {
//			// Done reading
//			
//			bytes = objectToByte(result);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		return bytes;
//		// BufferedReader bufferedReader = new BufferedReader(
//		// new InputStreamReader(inputStream));
//		//
//		// List<Integer> result = new ArrayList<Integer>();
//		// int read;
//		//
//		// try {
//		// while ((read = bufferedReader.read()) != -1) {
//		// result.add(read);
//		// }
//		// return objectToByte(result);
//		// } catch (IOException e) {
//		// e.printStackTrace();
//		// return null;
//		// }
//	}
	
	public static byte[] FileToByte(File file) {
		byte[] bytes = null;
		
		try {
			FileInputStream fis = new FileInputStream(file);
			
			Vector<Integer> v = new Vector<Integer>();
			int read;
			while((read = fis.read()) != -1) {
				v.add(read);
			}

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
	
	public static void byteToFile(byte[] bytes, File file) {
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

//	public static InputStream byteToInputStream(final byte[] listOfBytes) {
//		List<Byte> bytes = (List<Byte>) byteToObject(listOfBytes);
//		
//		DataOutputStream dos = new DataOutputStream(outputStream);
//		
//		for (Byte byte1 : bytes) {
//			try {
//				dos.writeByte(byte1);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		
//		InputStream is = new DataInputStream()
//		dos.
//		DataInputStream dis = new DataInputStream()
//		OutputStream os = new DataOutputStream()
//		DataOutputStream dos = new DataOutputStream();
//		
//		
//		DataInputStream dis = new DataInputStream(inputStream);
//		List<Byte> result = new ArrayList<Byte>();
//		byte[] bytes = null;
//		
//		try {
//			// An error will be thrown when EOF is reached, so loop will end.
//			while (true) {
//				result.add(dis.readByte());
//			}
//		} catch (EOFException e) {
//			// Done reading
//			
//			bytes = objectToByte(result);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		return bytes;
//		return null;
		
//		List<Integer> integers = (List<Integer>) byteToObject(listOfIntegers);
//
//		byte[] bytes = new byte[integers.size()];
//
//		for (int i = 0; i < integers.size(); i++) {
//			Byte test = Byte.valueOf("" + integers.get(i));
//			byte b = Byte.parseByte("" + integers.get(i));
//			bytes[i] = b;
//		}
//
//		InputStream inputStream = new ByteArrayInputStream(bytes);
//		return inputStream;
//	}
}
