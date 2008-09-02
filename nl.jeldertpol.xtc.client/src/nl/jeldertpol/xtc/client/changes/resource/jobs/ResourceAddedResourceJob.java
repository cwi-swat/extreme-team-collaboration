package nl.jeldertpol.xtc.client.changes.resource.jobs;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.logging.Level;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.common.changes.AddedResourceChange;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Add a resource.
 * 
 * @author Jeldert Pol
 */
public class ResourceAddedResourceJob extends HighPriorityJob {

	final private AddedResourceChange addedResourceChange;

	/**
	 * Add a resource. Schedules itself to be run.
	 * 
	 * @param addedResourceChange
	 *            Contains all information needed about the change.
	 */
	public ResourceAddedResourceJob(
			final AddedResourceChange addedResourceChange) {
		super(ResourceAddedResourceJob.class.getName() + ": "
				+ addedResourceChange.getResourceName());

		this.addedResourceChange = addedResourceChange;

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

		// Read information
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				addedResourceChange.getProjectName());
		String resourceName = addedResourceChange.getResourceName();
		int type = addedResourceChange.getType();

		try {
			if (type == IResource.FILE) {
				IFile file = project.getFile(resourceName);

				if (file.exists()) {
					// TODO File already exists, ignoring?
					Activator.LOGGER.log(Level.WARNING, "File "
							+ file.toString() + " already exists.");

					status = new Status(Status.OK, Activator.PLUGIN_ID,
							"Resource already exists. Ignoring add.");
				} else {
					// An empty InputStream. Needed in order to create the file
					// locally.
					InputStream source = new ByteArrayInputStream(new byte[0]);
					boolean force = false;

					file.create(source, force, monitor);
					file.refreshLocal(IResource.NONE, monitor);

					Activator.LOGGER.log(Level.INFO, "File " + file.toString()
							+ " created.");

					status = new Status(Status.OK, Activator.PLUGIN_ID,
							"Resource added successfully.");
				}
			} else if (type == IResource.FOLDER) {
				IFolder folder = project.getFolder(resourceName);

				if (folder.exists()) {
					// TODO Folder already exists, ignoring?
					Activator.LOGGER.log(Level.WARNING, "Folder "
							+ folder.toString() + " already exists.");

					status = new Status(Status.OK, Activator.PLUGIN_ID,
							"Resource already exists. Ignoring add.");
				} else {

					boolean force = false;
					boolean local = true;

					synchronized (Activator.resourceChangeListener) {
						Activator.SESSION.removeResourceChangeListener();
						folder.create(force, local, monitor);
						folder.refreshLocal(IResource.NONE, monitor);
						Activator.SESSION.addResourceChangeListener();
					}

					Activator.LOGGER.log(Level.INFO, "Folder "
							+ folder.toString() + " created.");

					status = new Status(Status.OK, Activator.PLUGIN_ID,
							"Resource added successfully.");
				}
			} else {
				Activator.LOGGER.log(Level.SEVERE,
						"Resource not a file or folder, but of type " + type
								+ ".");

				status = new Status(Status.ERROR, Activator.PLUGIN_ID,
						"Resource not a file or folder, but of type " + type
								+ ".");
			}
		} catch (CoreException e) {
			Activator.LOGGER.log(Level.SEVERE,"Resource content could not be set.", e);

			status = new Status(Status.ERROR, Activator.PLUGIN_ID,
					"Resource content could not be set.");
			// TODO revert, and re-apply all changes?
		}

		return status;
	}
}
