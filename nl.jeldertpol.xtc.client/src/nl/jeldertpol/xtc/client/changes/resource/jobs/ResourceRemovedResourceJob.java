package nl.jeldertpol.xtc.client.changes.resource.jobs;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.common.changes.RemovedResourceChange;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
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

		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				removedResourceChange.getProjectName());
		IResource resource = project.findMember(removedResourceChange
				.getResourceName());

		boolean force = true;
		IContainer parent = resource.getParent();

		try {
			resource.delete(force, monitor);
			try {
				parent.refreshLocal(IResource.NONE, monitor);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			status = new Status(Status.OK, Activator.PLUGIN_ID,
					"Resource content set successfully.");
		} catch (CoreException e) {
			e.printStackTrace();

			status = new Status(Status.ERROR, Activator.PLUGIN_ID,
					"Resource content could not be set.");
		}

		return status;
	}
}
