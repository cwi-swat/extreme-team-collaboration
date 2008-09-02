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

	/**
	 * Returns a list of modified {@link IResource} in a {@link IProject}, based
	 * on information from Subclipse.
	 * 
	 * @param project
	 *            the project in which to look for modified {@link IResource}.
	 * @return a list containing each modified {@link IResource}.
	 */
	public List<IResource> modifiedFiles(final IProject project) {
		return modifiedFiles((IResource) project);
	}

	/**
	 * Returns a list of modified {@link IResource} in a {@link IResource}
	 * (inclusive), based on information from Subclipse.
	 * 
	 * TODO unmanaged files are ignored, this is OK for bin files etc, but not
	 * for new files.
	 * 
	 * @param resource
	 *            the resource in which to look for modified {@link IResource}.
	 * @return a list containing each modified {@link IResource}.
	 */
	private List<IResource> modifiedFiles(final IResource resource) {
		List<IResource> modifiedFiles = new ArrayList<IResource>();

		ISVNLocalResource svnResource = SVNWorkspaceRoot
				.getSVNResourceFor(resource);
		try {
			if (svnResource.isManaged()) {
				LocalResourceStatus localResourceStatus = svnResource
						.getStatus();
				SVNStatusKind statusKind = localResourceStatus.getStatusKind();
				if (!statusKind.equals(SVNStatusKind.NORMAL)) {
					modifiedFiles.add(resource);
				}

				if (resource instanceof IFolder) {
					IFolder folder = (IFolder) resource;
					try {
						for (IResource member : folder.members()) {
							modifiedFiles.addAll(modifiedFiles(member));
						}
					} catch (CoreException e) {
						Activator.LOGGER.log(Level.SEVERE, e);
					}
				} else if (resource instanceof IProject) {
					IProject project = (IProject) resource;
					try {
						for (IResource member : project.members()) {
							modifiedFiles.addAll(modifiedFiles(member));
						}
					} catch (CoreException e) {
						Activator.LOGGER.log(Level.SEVERE, e);
					}
				}
			}
		} catch (SVNException e) {
			Activator.LOGGER.log(Level.SEVERE, e);
		}

		return modifiedFiles;
	}

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

}
