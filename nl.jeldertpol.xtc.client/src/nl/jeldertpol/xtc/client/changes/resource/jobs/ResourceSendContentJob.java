package nl.jeldertpol.xtc.client.changes.resource.jobs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.common.conversion.Conversion;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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

	final private IProject project;
	final private IPath filePath;

	/**
	 * Send the content of a file to the server. Schedules itself to be run.
	 * 
	 * @param project
	 *            The project the content belongs to.
	 * @param filePath
	 *            The file, path must be relative to the project.
	 * @param file
	 *            A reference to the actual file.
	 */
	public ResourceSendContentJob(final IProject project, final IPath filePath) {
		super(ResourceSendContentJob.class.getName() + ": "
				+ filePath.toPortableString());

		this.project = project;
		this.filePath = filePath;

		schedule();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		IStatus status;

		// Absolute path in the local file system.
		IResource resource = project.findMember(filePath);
		IPath location = resource.getLocation();
		File file = location.toFile();

		String filename = filePath.toPortableString();

		try {
			byte[] content = Conversion.fileToByte(file);

			Activator.SESSION.sendContent(project, filename, content);

			status = new Status(IStatus.OK, Activator.PLUGIN_ID,
					"Resource content set successfully.");
		} catch (FileNotFoundException e) {
			Activator.getLogger().log(Level.WARNING, e);

			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Resource not found.");
		} catch (IOException e) {
			Activator.getLogger().log(Level.SEVERE, e);

			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Resource could not be read.");
		}

		return status;
	}
}
