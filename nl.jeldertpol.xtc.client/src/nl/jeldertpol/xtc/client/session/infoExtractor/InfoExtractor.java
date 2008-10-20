package nl.jeldertpol.xtc.client.session.infoExtractor;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import nl.jeldertpol.xtc.client.Activator;
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

	protected final List<IResource> getResources(final IProject project) {
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
				Activator.getLogger().log(Level.SEVERE, e);
			}
		} else if (resource instanceof IProject) {
			IProject project = (IProject) resource;
			try {
				for (IResource member : project.members()) {
					resources.addAll(getResources(member));
				}
			} catch (CoreException e) {
				Activator.getLogger().log(Level.SEVERE, e);
			}
		}

		return resources;
	}

	/**
	 * Returns a list of modified {@link IResource} (can be empty) in a
	 * {@link IProject} (inclusive), based on information from a version control
	 * system.
	 * 
	 * @param project
	 *            The project in which to look for modified {@link IResource}.
	 * @return A list containing each modified {@link IResource}.
	 */
	public abstract List<IResource> modifiedFiles(final IProject project);

	/**
	 * Returns a list of unmanaged {@link IResource} (can be empty) in a
	 * {@link IProject} (inclusive), based on information from a version control
	 * system.
	 * 
	 * @param project
	 *            The project in which to look for unmanaged {@link IResource}.
	 * @return A list containing each unmanaged {@link IResource}.
	 */
	public abstract List<IResource> unmanagedFiles(final IProject project);

	/**
	 * Revert a project. All modifications made to it will be undone to the
	 * state the project resides on the version control system.
	 * 
	 * @param project
	 *            The project to revert.
	 */
	public abstract void revert(final IProject project);
}
