package nl.jeldertpol.xtc.client.changes.resource.jobs;

import java.util.logging.Level;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.client.session.rejoin.RejoinJob;
import nl.jeldertpol.xtc.common.changes.MovedChange;

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
public class ResourceMovedJob extends HighPriorityJob {

	final private MovedChange movedChange;

	/**
	 * Move a resource to a new location. Schedules itself to be run.
	 * 
	 * @param movedChange
	 *            Contains all information needed about the change.
	 */
	public ResourceMovedJob(final MovedChange movedChange) {
		super(ResourceMovedJob.class.getName() + ": " + movedChange.getFrom()
				+ " --> " + movedChange.getTo());

		this.movedChange = movedChange;

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

		IProject project = Activator.COMMON_ACTIONS.getProject(movedChange
				.getProjectName());

		IPath moveFrom = Path.fromPortableString(movedChange.getFrom());
		IResource resource = project.findMember(moveFrom);

		IPath moveTo = Path.fromPortableString(movedChange.getTo());
		// Making full path from root, so move can find destination.
		moveTo = project.getFullPath().append(moveTo);

		try {
			Activator.getLogger().log(Level.INFO,
					"Moving resource " + moveFrom + " --> " + moveTo);

			// TODO true or false?
			boolean force = true;

			resource.move(moveTo, force, monitor);

			status = new Status(IStatus.OK, Activator.PLUGIN_ID,
					"Move applied successfully.");
		} catch (CoreException e) {
			Activator.getLogger().log(Level.SEVERE,
					"Resource could not be moved.", e);

			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Resource could not be moved.");

			// Rejoin
			new RejoinJob();
		}

		return status;
	}

}
