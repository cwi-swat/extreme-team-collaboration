package nl.jeldertpol.xtc.client.changes.resource.jobs;

import java.util.logging.Level;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.common.changes.RemovedResourceChange;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Remove a resource.
 * 
 * @author Jeldert Pol
 */
public class ResourceRemovedResourceJob extends HighPriorityJob {

	final private RemovedResourceChange removedResourceChange;

	/**
	 * Remove a resource. Schedules itself to be run.
	 * 
	 * @param removedResourceChange
	 *            Contains all information needed about the change.
	 */
	public ResourceRemovedResourceJob(
			final RemovedResourceChange removedResourceChange) {
		super(ResourceRemovedResourceJob.class.getName() + ": "
				+ removedResourceChange.getResourceName());

		this.removedResourceChange = removedResourceChange;

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

		IProject project = Activator.COMMON_ACTIONS
				.getProject(removedResourceChange.getProjectName());
		IResource resource = project.findMember(removedResourceChange
				.getResourceName());

		boolean force = true;
		IContainer parent = resource.getParent();

		try {
			Activator.getLogger().log(Level.INFO,
					"Deleting resource " + resource.toString() + ".");

			resource.delete(force, monitor);
			try {
				parent.refreshLocal(IResource.NONE, monitor);
			} catch (CoreException e) {
				Activator.getLogger().log(Level.SEVERE, e);

				status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						"Error refreshing resource.");
			}
			status = new Status(IStatus.OK, Activator.PLUGIN_ID,
					"Resource content set successfully.");
		} catch (CoreException e) {
			Activator.getLogger().log(Level.SEVERE,
					"Resource could not be deleted.", e);

			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Resource could not be deleted.");
		}

		return status;
	}
}
