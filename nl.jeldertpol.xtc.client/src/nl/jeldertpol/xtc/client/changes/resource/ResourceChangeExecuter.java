package nl.jeldertpol.xtc.client.changes.resource;

import java.io.InputStream;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.client.changes.resource.jobs.ResourceAddedResourceJob;
import nl.jeldertpol.xtc.client.changes.resource.jobs.ResourceMoveJob;
import nl.jeldertpol.xtc.client.changes.resource.jobs.ResourceRemovedResourceJob;
import nl.jeldertpol.xtc.client.changes.resource.jobs.ResourceSetContentJob;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;

/**
 * Execute a change to a resource.
 * 
 * @author Jeldert Pol
 */
public class ResourceChangeExecuter implements IJobChangeListener {

	public void move(IResource resource, IPath moveTo) {
		ResourceMoveJob job = new ResourceMoveJob(resource, moveTo);
		job.addJobChangeListener(this);
		job.schedule();
	}

	public void setContent(IFile file, byte[] content) {
		ResourceSetContentJob job = new ResourceSetContentJob(file, content);
		job.addJobChangeListener(this);
		job.schedule();
	}

	public void addedResource(IResource resource, int type) {
		ResourceAddedResourceJob job = new ResourceAddedResourceJob(resource,
				type);
		job.addJobChangeListener(this);
		job.schedule();
	}

	public void removedResource(IResource resource) {
		ResourceRemovedResourceJob job = new ResourceRemovedResourceJob(
				resource);
		job.addJobChangeListener(this);
		job.schedule();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.jobs.IJobChangeListener#aboutToRun(org.eclipse
	 * .core.runtime.jobs.IJobChangeEvent)
	 */
	@Override
	public void aboutToRun(IJobChangeEvent event) {
		// Temporarily remove listener, prevents round-tripping
		Activator.SESSION.removeResourceChangeListener();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.jobs.IJobChangeListener#awake(org.eclipse.core
	 * .runtime.jobs.IJobChangeEvent)
	 */
	@Override
	public void awake(IJobChangeEvent event) {
		// Nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.jobs.IJobChangeListener#done(org.eclipse.core
	 * .runtime.jobs.IJobChangeEvent)
	 */
	@Override
	public void done(IJobChangeEvent event) {
		// Add listener again
		Activator.SESSION.addResourceChangeListener();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.jobs.IJobChangeListener#running(org.eclipse.
	 * core.runtime.jobs.IJobChangeEvent)
	 */
	@Override
	public void running(IJobChangeEvent event) {
		// Nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.jobs.IJobChangeListener#scheduled(org.eclipse
	 * .core.runtime.jobs.IJobChangeEvent)
	 */
	@Override
	public void scheduled(IJobChangeEvent event) {
		// Nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.jobs.IJobChangeListener#sleeping(org.eclipse
	 * .core.runtime.jobs.IJobChangeEvent)
	 */
	@Override
	public void sleeping(IJobChangeEvent event) {
		// Nothing to do
	}

}
