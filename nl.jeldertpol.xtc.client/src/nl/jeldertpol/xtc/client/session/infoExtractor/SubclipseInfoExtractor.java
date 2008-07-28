package nl.jeldertpol.xtc.client.session.infoExtractor;

import java.util.ArrayList;
import java.util.List;

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
public class SubclipseInfoExtractor {

	/**
	 * Returns a list of modified {@link IResource} in a {@link IProject}, based
	 * on information from Subclipse.
	 * 
	 * @param project
	 *            the project in which to look for modified {@link IResource}.
	 * @return a list containing each modified {@link IResource}.
	 */
	public List<IResource> modifiedFiles(IProject project) {
		return modifiedFiles((IResource) project);
	}

	/**
	 * Returns a list of modified {@link IResource} in a {@link IResource}
	 * (inclusive), based on information from Subclipse.
	 * 
	 * @param resource
	 *            the resource in which to look for modified {@link IResource}.
	 * @return a list containing each modified {@link IResource}.
	 */
	private List<IResource> modifiedFiles(IResource resource) {
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

				if (resource instanceof IFolder) { // || resource instanceof
													// IProject) {
					IFolder folder = (IFolder) resource;
					try {
						for (IResource member : folder.members()) {
							modifiedFiles.addAll(modifiedFiles(member));
						}
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (resource instanceof IProject) {
					IProject project = (IProject) resource;
					try {
						for (IResource member : project.members()) {
							modifiedFiles.addAll(modifiedFiles(member));
						}
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} catch (SVNException e) {
			// // TODO Auto-generated catch block
			e.printStackTrace();
		}

		return modifiedFiles;
	}

	/**
	 * Get the revision of a project.
	 * 
	 * @param project
	 *            the project to get the revision from.
	 * @return the revision of the project.
	 */
	public Long getRevision(IProject project) {
		ISVNLocalResource svnResource = SVNWorkspaceRoot
				.getSVNResourceFor(project);
		Long number = null;

		try {
			SVNRevision revision = svnResource.getRevision();
			number = new Long(revision.toString());

			// LocalResourceStatus localResourceStatus =
			// svnResource.getStatus();
			// // Only happens when no synchronization info is available
			// assert (localResourceStatus != null);
			//			
			// Number revision = localResourceStatus.getRevision();
			// // Only happens when resource is not managed
			// assert (revision != null);
			//			
			// number = revision.getNumber();
		} catch (SVNException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return number;
	}

}
