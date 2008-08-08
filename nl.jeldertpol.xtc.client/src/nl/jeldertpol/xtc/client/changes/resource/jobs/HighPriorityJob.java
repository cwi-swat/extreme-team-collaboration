package nl.jeldertpol.xtc.client.changes.resource.jobs;

import org.eclipse.core.runtime.jobs.Job;

/**
 * A {@link Job} with a high priority.
 * 
 * @author Jeldert Pol
 */
public abstract class HighPriorityJob extends Job {

	/**
	 * A {@link Job} with a high priority.
	 * 
	 * @param name
	 *            The name of the job.
	 * 
	 * @see Job#INTERACTIVE
	 */
	public HighPriorityJob(String name) {
		super(name);

		setPriority(INTERACTIVE);
	}

}
