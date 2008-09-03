package nl.jeldertpol.xtc.client.changes.resource.jobs;

import java.util.logging.Level;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.common.changes.MoveChange;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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

		IProject project = Activator.COMMON_ACTIONS.getProject(moveChange
				.getProjectName());

		IPath moveFrom = Path.fromPortableString(moveChange.getFrom());
		IResource resource = project.findMember(moveFrom);

		IPath moveTo = Path.fromPortableString(moveChange.getTo());
		// Making full path from root, so move can find destination.
		moveTo = project.getFullPath().append(moveTo);

		try {
			Activator.LOGGER.log(Level.INFO, "Moving resource " + moveFrom
					+ " --> " + moveTo);

			// TODO true or false?
			boolean force = true;

			resource.move(moveTo, force, monitor);

			status = new Status(IStatus.OK, Activator.PLUGIN_ID,
					"Move applied successfully.");
		} catch (CoreException e) {
			Activator.LOGGER.log(Level.SEVERE, "Resource could not be moved.",
					e);

			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Resource could not be moved.");
			// TODO revert, and re-apply all changes?
		}

		return status;
	}

}
