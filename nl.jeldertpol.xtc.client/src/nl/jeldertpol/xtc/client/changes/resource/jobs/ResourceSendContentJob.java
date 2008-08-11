package nl.jeldertpol.xtc.client.changes.resource.jobs;

import java.io.File;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.common.conversion.Conversion;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Send the content of a file to the server.
 * 
 * @author Jeldert Pol
 */
public class ResourceSendContentJob extends HighPriorityJob {

	private IProject project;
	private IPath filePath;
	private File file;

	/**
	 * Send the content of a file to the server.
	 * 
	 * @param project
	 *            The project the content belongs to.
	 * @param filePath
	 *            The file, path must be relative to the project.
	 * @param file
	 *            A reference to the actual file.
	 */
	public ResourceSendContentJob(IProject project, IPath filePath, File file) {
		super(file.toString());

		this.project = project;
		this.filePath = filePath;
		this.file = file;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		System.out.println("Sending new content: " + filePath.toPortableString());
		IStatus status;

		// try {
		byte[] content = Conversion.fileToByte(file);

		Activator.SESSION.sendContent(project, filePath, content);

		status = new Status(Status.OK, Activator.PLUGIN_ID,
				"Resource content set successfully.");
		// } catch (CoreException e) {
		// e.printStackTrace();
		//
		// status = new Status(Status.ERROR, Activator.PLUGIN_ID,
		// "Resource content could not be set.");
		// // TODO revert, and re-apply all changes?
		// }

		return status;
	}
}
