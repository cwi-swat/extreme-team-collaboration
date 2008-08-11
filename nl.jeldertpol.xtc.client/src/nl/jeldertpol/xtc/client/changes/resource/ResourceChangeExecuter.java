package nl.jeldertpol.xtc.client.changes.resource;

import java.io.File;

import nl.jeldertpol.xtc.client.changes.resource.jobs.ResourceAddedResourceJob;
import nl.jeldertpol.xtc.client.changes.resource.jobs.ResourceMoveJob;
import nl.jeldertpol.xtc.client.changes.resource.jobs.ResourceReceiveContentJob;
import nl.jeldertpol.xtc.client.changes.resource.jobs.ResourceRemovedResourceJob;
import nl.jeldertpol.xtc.client.changes.resource.jobs.ResourceSendContentJob;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

/**
 * Execute a change to a resource.
 * 
 * @author Jeldert Pol
 */
public class ResourceChangeExecuter {

	public void move(IResource resource, IPath moveTo) {
		ResourceMoveJob job = new ResourceMoveJob(resource, moveTo);
		job.schedule();
	}

	public void sendContent(IProject project, IPath filePath, File file) {
		ResourceSendContentJob job = new ResourceSendContentJob(project,
				filePath, file);
		job.schedule();
	}

	public void receiveContent(IProject project, IPath filePath, File file,
			byte[] content) {
		ResourceReceiveContentJob job = new ResourceReceiveContentJob(project,
				filePath, file, content);
		job.schedule();
	}

	public void addedResource(IResource resource, int type) {
		ResourceAddedResourceJob job = new ResourceAddedResourceJob(resource,
				type);
		job.schedule();
	}

	public void removedResource(IResource resource) {
		ResourceRemovedResourceJob job = new ResourceRemovedResourceJob(
				resource);
		job.schedule();
	}

}
