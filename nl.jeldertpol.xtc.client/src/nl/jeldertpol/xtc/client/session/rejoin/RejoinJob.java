package nl.jeldertpol.xtc.client.session.rejoin;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.client.session.Session;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.progress.UIJob;

/**
 * An {@link UIJob} that can safely rejoin a session. Is a {@link UIJob}, so it
 * will block the user-interface while rejoining.
 * 
 * @author Jeldert Pol
 */
public class RejoinJob extends UIJob {

	/**
	 * Constructor, calls {@link Session#rejoin()} when it runs.
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
