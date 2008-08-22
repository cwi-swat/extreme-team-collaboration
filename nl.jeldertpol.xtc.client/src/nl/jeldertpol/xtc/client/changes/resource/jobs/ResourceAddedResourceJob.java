package nl.jeldertpol.xtc.client.changes.resource.jobs;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

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
					// File already exists, ignoring.
					// TODO Can be bin, or can be error...
					status = new Status(Status.OK, Activator.PLUGIN_ID,
							"Resource already exists. Ignoring add.");
				} else {
					// An empty InputStream. Needed in order to create the file
					// locally.
					InputStream source = new ByteArrayInputStream(new byte[0]);
					boolean force = false;

					synchronized (Activator.resourceChangeListener) {
						Activator.SESSION.removeResourceChangeListener();
						file.create(source, force, monitor);
						file.refreshLocal(IResource.NONE, monitor);
						Activator.SESSION.addResourceChangeListener();
					}

					status = new Status(Status.OK, Activator.PLUGIN_ID,
							"Resource added successfully.");
				}
			} else if (type == IResource.FOLDER) {
				IFolder folder = project.getFolder(resourceName);

				if (folder.exists()) {
					// Folder already exists, ignoring.
					// TODO Can be bin, or can be error...
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

					status = new Status(Status.OK, Activator.PLUGIN_ID,
							"Resource added successfully.");
				}
			} else {
				status = new Status(Status.ERROR, Activator.PLUGIN_ID,
						"Resource not a file or folder, but of type " + type
								+ ".");
			}
		} catch (CoreException e) {
			e.printStackTrace();

			status = new Status(Status.ERROR, Activator.PLUGIN_ID,
					"Resource content could not be set.");
			// TODO revert, and re-apply all changes?
		}

		return status;
	}
}
