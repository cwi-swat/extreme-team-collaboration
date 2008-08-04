package nl.jeldertpol.xtc.client.session.infoExtractor;

import java.util.ArrayList;
import java.util.List;

import nl.jeldertpol.xtc.client.exceptions.UnrevisionedProjectException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Jeldert Pol
 * 
 */
public abstract class InfoExtractor {

	public final List<IResource> getResources(IProject project) {
		return getResources((IResource) project);
	}

	private List<IResource> getResources(IResource resource) {
		List<IResource> resources = new ArrayList<IResource>();
		
		resources.add(resource);
		
		if (resource instanceof IFolder) {
			IFolder folder = (IFolder) resource;
			try {
				for (IResource member : folder.members()) {
					resources.addAll(getResources(member));
				}
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (resource instanceof IProject) {
			IProject project = (IProject) resource;
			try {
				for (IResource member : project.members()) {
					resources.addAll(getResources(member));
				}
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return resources;
	}

	public abstract List<IResource> modifiedFiles(IProject project);

	public abstract Long getRevision(IProject project) throws UnrevisionedProjectException;
}
