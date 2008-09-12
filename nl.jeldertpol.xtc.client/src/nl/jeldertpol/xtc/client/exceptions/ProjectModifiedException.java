package nl.jeldertpol.xtc.client.exceptions;

import java.util.List;

import nl.jeldertpol.xtc.common.exceptions.XtcException;

import org.eclipse.core.resources.IResource;

/**
 * @author Jeldert Pol
 */
public class ProjectModifiedException extends XtcException {

	private static final long serialVersionUID = 1L;

	private static final String MESSAGE1 = "The project (";
	private static final String MESSAGE2 = ") has local modifications (";
	private static final String MESSAGE3 = ").";

	private final List<IResource> modifiedFiles;

	public ProjectModifiedException(final String project,
			final List<IResource> modifiedFiles) {
		super(MESSAGE1 + project + MESSAGE2 + modifiedFiles.toString()
				+ MESSAGE3);

		this.modifiedFiles = modifiedFiles;
	}

	/**
	 * @return the modifiedFiles
	 */
	public List<IResource> getModifiedFiles() {
		return modifiedFiles;
	}

}
