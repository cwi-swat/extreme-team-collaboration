package nl.jeldertpol.xtc.client.changes.resource.jobs;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.common.changes.MoveChange;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

/**
 * Move a resource to a new location.
 * 
 * @author Jeldert Pol
 */
public class ResourceMoveJob extends HighPriorityJob {

	final private MoveChange moveChange;

	/**
	 * Move a resource to a new location. Schedules itself to be run.
	 * 
	 * @param moveChange
	 *            Contains all information needed about the change.
	 */
	public ResourceMoveJob(final MoveChange moveChange) {
		super(ResourceMoveJob.class.getName() + ": " + moveChange.getFrom()
				+ " --> " + moveChange.getTo());

		this.moveChange = moveChange;

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
				moveChange.getProjectName());
		IPath moveTo = Path.fromPortableString(moveChange.getTo());
		IResource resource = project.findMember(moveChange.getFrom());

		try {
			// TODO true or false?
			boolean force = true;

			resource.move(moveTo, force, monitor);

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
