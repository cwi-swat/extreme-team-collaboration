package nl.jeldertpol.xtc.client.exceptions;

public class ProjectNotPresentException extends Exception {

	private static final long serialVersionUID = 1L;

	private final static String message1 = "The project (";
	private final static String message2 = ") is not present on the server";
	
	public ProjectNotPresentException(String project) {
		super(message1 + project + message2);
	}
}
