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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.jeldertpol.xtc.client.session.infoExtractor.InfoExtractor#modifiedFiles
	 * (org.eclipse.core.resources.IProject)
	 */
	@Override
	public List<IResource> modifiedFiles(final IProject project) {
		List<IResource> resources = getResources(project);
		List<IResource> modifiedFiles = new ArrayList<IResource>();

		for (IResource resource : resources) {
			ISVNLocalResource svnResource = SVNWorkspaceRoot
					.getSVNResourceFor(resource);
			try {
				// When file is managed by SVN, or not ignored, so a new resource.
				if (svnResource.isManaged() || !svnResource.isIgnored()) {
					LocalResourceStatus localResourceStatus = svnResource
							.getStatus();
					SVNStatusKind statusKind = localResourceStatus
							.getStatusKind();
					if (!statusKind.equals(SVNStatusKind.NORMAL)) {
						modifiedFiles.add(resource);
					}
//				} else if (!svnResource.isManaged()) {
//					// Not managed, so must be modified.
//					System.out.println("niet managed: " + resource);
				}

			} catch (SVNException e) {
				Activator.LOGGER.log(Level.SEVERE, e);
			}
		}

		return modifiedFiles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.jeldertpol.xtc.client.session.infoExtractor.InfoExtractor#unmanagedFiles
	 * (org.eclipse.core.resources.IProject)
	 */
	@Override
	public List<IResource> unmanagedFiles(final IProject project) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.jeldertpol.xtc.client.session.infoExtractor.InfoExtractor#revert(org
	 * .eclipse.core.resources.IProject)
	 */
	@Override
	public void revert(final IProject project) {
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
