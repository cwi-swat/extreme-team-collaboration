package nl.jeldertpol.xtc.client.session.infoExtractor;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.client.exceptions.RevisionExtractorException;
import nl.jeldertpol.xtc.client.exceptions.UnversionedProjectException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.tigris.subversion.subclipse.core.ISVNLocalResource;
import org.tigris.subversion.subclipse.core.SVNException;
import org.tigris.subversion.subclipse.core.resources.LocalResourceStatus;
import org.tigris.subversion.subclipse.core.resources.SVNWorkspaceRoot;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNStatusKind;

/**
 * Extract Subclipse information from local project.
 * 
 * @author Jeldert Pol
 */
public class SubclipseInfoExtractor extends InfoExtractor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.jeldertpol.xtc.client.session.infoExtractor.InfoExtractor#getRevision
	 * (org.eclipse.core.resources.IProject)
	 */
	@Override
	public Long getRevision(final IProject project)
			throws UnversionedProjectException, RevisionExtractorException {
		ISVNLocalResource svnResource = SVNWorkspaceRoot
				.getSVNResourceFor(project);
		Long number;

		try {
			SVNRevision revision = svnResource.getRevision();
			number = Long.valueOf(revision.toString());
		} catch (SVNException e) {
			throw new RevisionExtractorException(e);
		} catch (NullPointerException e) {
			// Not a Subversion project
			throw new UnversionedProjectException(project.getName());
		}

		return number;
	}

	/**
	 * Returns a list of modified {@link IResource} in a {@link IProject}
	 * (inclusive), based on information from Subclipse.
	 * 
	 * @param project
	 *            The project in which to look for modified {@link IResource}.
	 * @return A list containing each modified {@link IResource}.
	 */
	@Override
	public List<IResource> modifiedFiles(final IProject project) {
		List<IResource> resources = getResources(project);
		List<IResource> modifiedFiles = new ArrayList<IResource>();

		for (IResource resource : resources) {
			ISVNLocalResource svnResource = SVNWorkspaceRoot
					.getSVNResourceFor(resource);
			try {
				if (svnResource.isManaged()) {
					LocalResourceStatus localResourceStatus = svnResource
							.getStatus();
					SVNStatusKind statusKind = localResourceStatus
							.getStatusKind();
					if (!statusKind.equals(SVNStatusKind.NORMAL)) {
						modifiedFiles.add(resource);
					}
				}
			} catch (SVNException e) {
				Activator.LOGGER.log(Level.SEVERE, e);
			}
		}

		return modifiedFiles;
	}

	/**
	 * Returns a list of unmanaged {@link IResource} in a {@link IProject}
	 * (inclusive), based on information from Subclipse.
	 * 
	 * @param project
	 *            The project in which to look for unmanaged {@link IResource}.
	 * @return A list containing each unmanaged {@link IResource}.
	 */
	@Override
	public List<IResource> unmanagedFiles(IProject project) {
		List<IResource> resources = getResources(project);
		List<IResource> unmanagedFiles = new ArrayList<IResource>();

		for (IResource resource : resources) {
			ISVNLocalResource svnResource = SVNWorkspaceRoot
					.getSVNResourceFor(resource);
			try {
				if (!svnResource.isManaged()) {
					// Resource is not managed
					IPath resourcePath = resource.getProjectRelativePath();
					boolean isIgnored = Activator.SESSION
							.isIgnored(resourcePath);
					if (!isIgnored) {
						unmanagedFiles.add(resource);
					}
				}

			} catch (SVNException e) {
				Activator.LOGGER.log(Level.SEVERE, e);
			}
		}

		return unmanagedFiles;
	}

	@Override
	public void revert(IProject project) {
		ISVNLocalResource svnResource = SVNWorkspaceRoot
				.getSVNResourceFor(project);

		try {
			svnResource.revert();
		} catch (SVNException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
