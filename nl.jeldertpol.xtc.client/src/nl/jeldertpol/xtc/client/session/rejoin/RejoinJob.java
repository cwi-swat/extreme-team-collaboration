/**
 * 
 */
package nl.jeldertpol.xtc.client.session.rejoin;

import nl.jeldertpol.xtc.client.Activator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Jeldert Pol
 * 
 */
public class RejoinJob extends UIJob {

	/**
	 * @param name
	 */
	public RejoinJob() {
		super("Rejoin");

		setPriority(INTERACTIVE);

		schedule();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	public IStatus runInUIThread(IProgressMonitor monitor) {
		IStatus status;

		Activator.SESSION.rejoin();

		status = new Status(IStatus.OK, Activator.PLUGIN_ID, "Rejoin OK");

		return status;
	}

}
