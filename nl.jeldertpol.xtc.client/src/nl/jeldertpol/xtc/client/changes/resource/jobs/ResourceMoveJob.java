package nl.jeldertpol.xtc.client.changes.resource.jobs;

import nl.jeldertpol.xtc.client.Activator;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Move a resource.
 * 
 * @author Jeldert Pol
 */
public class ResourceMoveJob extends HighPriorityJob {

	private IResource resource;
	private IPath moveTo;

	/**
	 * Move a resource to a new location.
	 * 
	 * @param resource
	 *            The resource to move.
	 * @param moveTo
	 *            The new location of the resource.
	 */
	public ResourceMoveJob(IResource resource, IPath moveTo) {
		super(resource.toString());

		this.resource = resource;
		this.moveTo = moveTo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		IStatus status;

		try {
			resource.move(moveTo, true, null);
			status = new Status(Status.OK, Activator.PLUGIN_ID,
					"Move applied successfully.");
		} catch (CoreException e) {
			e.printStackTrace();

			status = new Status(Status.ERROR, Activator.PLUGIN_ID,
					"Change could not be applied.");
			// TODO revert, and re-apply all changes?
		}

		return status;
	}

}
