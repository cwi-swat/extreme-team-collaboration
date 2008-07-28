package nl.jeldertpol.xtc.client.session;

import java.util.List;

import nl.jeldertpol.xtc.client.session.infoExtractor.SubclipseInfoExtractor;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.MessageDialog;

/**
 * @author Jeldert Pol
 */
public class StartJoin {
	SubclipseInfoExtractor infoExtractor;

	public StartJoin() {
		super();
		infoExtractor = new SubclipseInfoExtractor();
	}

	private boolean validProject(IProject project) {
		boolean valid = false;

		if (infoExtractor.getRevision(project) != null) {
			List<IResource> modifiedFiles = infoExtractor
					.modifiedFiles(project);
			if (modifiedFiles.isEmpty()) {
				valid = true;
			}
		}

		return valid;
	}

	public void startJoin(IProject project) {
		boolean valid = validProject(project);

		if (valid) {
			// Contact server

			// Look if project is already started
			// Match revision
			// Join
			// Error

			// Start new session

		} else {
			invalidProject();
		}
	}

	private void invalidProject() {
		MessageDialog.openError(null, "Start / Join",
				"The selected project is not a valid project.");
	}

	private void wrongRevisionProject() {
		MessageDialog.openError(null, "Start / Join",
				"The selected project needs to have revision X.");
	}
}
