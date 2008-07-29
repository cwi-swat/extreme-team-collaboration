package nl.jeldertpol.xtc.client.preferences.connection;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * A {@link StringFieldEditor} that checks if the input is a valid host.
 * 
 * @author Jeldert Pol
 */
public class HostFieldEditor extends StringFieldEditor {

	private final String errorMessage = "Invalid host";

	/**
	 * @see StringFieldEditor#StringFieldEditor(String, String, Composite)
	 */
	public HostFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
		setEmptyStringAllowed(false);
	}

	/**
	 * @see StringFieldEditor#StringFieldEditor(String, String, int, Composite)
	 */
	public HostFieldEditor(String name, String labelText, int width,
			Composite parent) {
		super(name, labelText, width, parent);
		setEmptyStringAllowed(false);
	}

	/**
	 * @see StringFieldEditor#StringFieldEditor(String, String, int, int,
	 *      Composite)
	 */
	public HostFieldEditor(String name, String labelText, int width,
			int strategy, Composite parent) {
		super(name, labelText, width, strategy, parent);
		setEmptyStringAllowed(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.StringFieldEditor#checkState()
	 */
	protected boolean checkState() {
		if (super.checkState()) {
			boolean validHost;
			try {
				InetAddress.getByName(getStringValue());
				validHost = true;
			} catch (UnknownHostException e) {
				validHost = false;

				// Set a custom error message, display it, and restore original
				// one.
				String oldErrorMessage = getErrorMessage();
				setErrorMessage(errorMessage);
				showErrorMessage();
				setErrorMessage(oldErrorMessage);
			}
			return validHost;
		} else {
			return false;
		}
	}

}
