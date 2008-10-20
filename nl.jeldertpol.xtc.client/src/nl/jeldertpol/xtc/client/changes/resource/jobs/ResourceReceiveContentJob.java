package nl.jeldertpol.xtc.client.changes.resource.jobs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.client.changes.editor.RevertToSavedJob;
import nl.jeldertpol.xtc.client.session.rejoin.RejoinJob;
import nl.jeldertpol.xtc.common.changes.ContentChange;
import nl.jeldertpol.xtc.common.conversion.Conversion;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Set content of a file.
 * 
 * @author Jeldert Pol
 */
public class ResourceReceiveContentJob extends HighPriorityJob {

	final private ContentChange contentChange;

	/**
	 * Set the content of a file. Schedules itself to be run.
	 * 
	 * @param contentChange
	 *            Contains all information needed about the change.
	 */
	public ResourceReceiveContentJob(final ContentChange contentChange) {
		super(ResourceReceiveContentJob.class.getName() + ": "
				+ contentChange.getFilename());

		this.contentChange = contentChange;

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

		IProject project = Activator.COMMON_ACTIONS.getProject(contentChange
				.getProjectName());
		IResource resource = project.findMember(contentChange.getFilename());
		IPath location = resource.getLocation();
		File file = location.toFile();

		Activator.getLogger().log(Level.INFO,
				"Setting content of file " + file.toString() + ".");

		byte[] content = contentChange.getContent();
		try {
			Conversion.byteToFile(content, file);
		} catch (FileNotFoundException e) {
			Activator.getLogger().log(Level.WARNING, e);

			// Rejoin
			new RejoinJob();
		} catch (IOException e) {
			Activator.getLogger().log(Level.WARNING, e);

			// Rejoin
			new RejoinJob();
		}

		try {
			resource.refreshLocal(IResource.NONE, monitor);

			status = new Status(IStatus.OK, Activator.PLUGIN_ID,
					"Resource content set successfully.");
		} catch (CoreException e) {
			Activator.getLogger().log(Level.SEVERE, e);

			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Error refreshing resource.");
		}

		// Revert any editor to display new content.
		new RevertToSavedJob(resource);

		return status;
	}
}
