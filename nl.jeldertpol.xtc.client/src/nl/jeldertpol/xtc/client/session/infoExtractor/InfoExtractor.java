package nl.jeldertpol.xtc.client.session.infoExtractor;

import java.util.ArrayList;
import java.util.List;

import nl.jeldertpol.xtc.client.exceptions.RevisionExtractorException;
import nl.jeldertpol.xtc.client.exceptions.UnversionedProjectException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Jeldert Pol
 * 
 */
public abstract class InfoExtractor {

	// TODO Javadoc

	/**
	 * Get the revision of a project.
	 * 
	 * @param project
	 *            The project to get the revision from.
	 * @return The revision of the project.
	 * 
	 * @throws RevisionExtractorException
	 *             The underlying version control system throws an error.
	 * @throws UnversionedProjectException
	 *             Thrown when the project is not under version control.
	 */
	public abstract Long getRevision(final IProject project)
			throws RevisionExtractorException, UnversionedProjectException;

	public final List<IResource> getResources(final IProject project) {
		return getResources((IResource) project);
	}

	private List<IResource> getResources(final IResource resource) {
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

	public abstract List<IResource> modifiedFiles(final IProject project);

	public abstract List<IResource> unmanagedFiles(final IProject project);

	public abstract void revert(final IProject project);
}
