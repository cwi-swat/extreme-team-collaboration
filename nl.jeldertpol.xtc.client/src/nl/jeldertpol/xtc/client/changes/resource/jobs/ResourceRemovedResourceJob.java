package nl.jeldertpol.xtc.client.changes.resource.jobs;

import nl.jeldertpol.xtc.client.Activator;

import org.eclipse.core.resources.IContainer;
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

	final private IResource resource;

	/**
	 * Remove a resource. Schedules itself to be run.
	 * 
	 * @param resource
	 *            The resource to remove.
	 */
	public ResourceRemovedResourceJob(final IResource resource) {
		super(ResourceRemovedResourceJob.class.getName() + ": "
				+ resource.toString());

		this.resource = resource;

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

		try {
			boolean force = true;

			IContainer parent = resource.getParent();

			synchronized (Activator.resourceChangeListener) {
				Activator.SESSION.removeResourceChangeListener();

				resource.delete(force, monitor);
				try {
					parent.refreshLocal(IResource.NONE, monitor);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Activator.SESSION.addResourceChangeListener();
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
