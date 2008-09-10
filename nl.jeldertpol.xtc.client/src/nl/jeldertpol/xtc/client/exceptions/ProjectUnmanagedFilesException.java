package nl.jeldertpol.xtc.client.exceptions;

import java.util.List;

import org.eclipse.core.runtime.IPath;

/**
 * @author Jeldert Pol
 */
public class ProjectUnmanagedFilesException extends XtcException {

	private static final long serialVersionUID = 1L;

	private static final String MESSAGE1 = "The project (";
	private static final String MESSAGE2 = ") has unmanaged files (";
	private static final String MESSAGE3 = ").";

	private final List<IPath> modifiedFiles;

	public ProjectUnmanagedFilesException(final String project,
			final List<IPath> modifiedFiles) {
		super(MESSAGE1 + project + MESSAGE2 + modifiedFiles.toString()
				+ MESSAGE3);

		this.modifiedFiles = modifiedFiles;
	}

	public List<IPath> getModifiedFiles() {
		return modifiedFiles;
	}
}
