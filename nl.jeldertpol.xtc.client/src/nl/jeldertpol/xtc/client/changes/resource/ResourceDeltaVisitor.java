package nl.jeldertpol.xtc.client.changes.resource;

import java.util.logging.Level;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.client.exceptions.LeaveSessionException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

/**
 * Visit a {@link IResourceDelta}, and look for anything interesting.
 * 
 * @author Jeldert Pol
 */
public class ResourceDeltaVisitor implements IResourceDeltaVisitor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core
	 * .resources.IResourceDelta)
	 */
	@Override
	public boolean visit(final IResourceDelta delta) throws CoreException {
		boolean ofInterest = false;
		boolean visitChildren = true;

		IResource resource = delta.getResource();
		int flags = delta.getFlags();

		String resourcePortableString = resource.getFullPath()
				.toPortableString();

		switch (delta.getKind()) {
		case IResourceDelta.NO_CHANGE:
			// Obviously, this is not of interest.
			ofInterest = false;
			break;
		case IResourceDelta.ADDED:
			// Resource is added, but could be the result of a move.
			if ((flags & IResourceDelta.MOVED_FROM) != 0) {
				// Add is the result of a move, ignoring add, but continue to
				// handle move.
				ofInterest = true;
			} else {
				// It is really a new resource.
				IProject project = resource.getProject();
				IPath resourcePath = resource.getProjectRelativePath();
				int type = resource.getType();

				Activator.SESSION
						.sendAddedResource(project, resourcePath, type);

				// If it is a file, send the content of the file
				if (resource.getType() == IResource.FILE) {
					Activator.SESSION.sendContent(project, resourcePath);
				}

				ofInterest = false;
			}

			break;
		case IResourceDelta.REMOVED:
			// Resource is removed, but could be the result of a move.
			if ((flags & IResourceDelta.MOVED_TO) != 0) {
				// Removal is the result of a move, ignoring removal, but
				// continue to handle move.
				ofInterest = true;
			} else {
				// It really is the removal of a resource.
				IProject project = resource.getProject();
				IPath resourcePath = resource.getProjectRelativePath();

				Activator.SESSION.sendRemovedResource(project, resourcePath);

				// Nothing left of interest in this delta.
				ofInterest = false;
			}

			break;
		case IResourceDelta.CHANGED:
			// Content of resource, or one of its children changed.
			ofInterest = true;
			break;
		case IResourceDelta.ADDED_PHANTOM:
			Activator.getLogger().log(Level.FINE,
					"Added Phantom: " + resourcePortableString + ".");
			// TODO of interest?
			ofInterest = true;
			break;
		case IResourceDelta.REMOVED_PHANTOM:
			Activator.getLogger().log(Level.FINE,
					"Removed Phantom: " + resourcePortableString + ".");
			// TODO of interest?
			ofInterest = true;
			break;
		default:
			Activator.getLogger().log(
					Level.WARNING,
					"Unhandled kind of delta: " + delta.getKind()
							+ ". For resource " + resourcePortableString + ".");
			// ofInterest = false;
			break;
		}

		if (ofInterest) {
			// The file system modification timestamp has changed since the
			// last notification. IResource.touch() will also trigger a
			// content change notification, even though the content may not
			// have changed in the file system.
			if ((flags & IResourceDelta.CONTENT) != 0) {
				IProject project = resource.getProject();
				IPath resourcePath = resource.getProjectRelativePath();

				// If it is a file, send the content of the file
				if (resource.getType() == IResource.FILE) {
					Activator.SESSION.sendContent(project, resourcePath);
				}

				visitChildren = false;
			}
			// The project description has changed.
			if ((flags & IResourceDelta.DESCRIPTION) != 0) {
				// Nothing to do
			}
			// The character encoding for a file, or for the files inside a
			// container, have changed. For listeners that care about the
			// character content of the file, as opposed to the raw bytes,
			// this should typically be treated the same as a content
			// change.
			if ((flags & IResourceDelta.ENCODING) != 0) {
				// Nothing to do
			}
			// The project has either been opened or closed. If the project
			// is now open, then it was previously closed, and vice-versa.
			if ((flags & IResourceDelta.OPEN) != 0) {
				Activator.getLogger().log(Level.INFO,
						"Project opened or closed. Leaving session.");

				try {
					Activator.SESSION.leaveSession();
				} catch (LeaveSessionException e) {
					Activator.getLogger().log(Level.SEVERE, e);
				}
			}
			// The resource was moved to another location. The location it
			// was moved to is indicated by IResourceDelta.getMovedToPath.
			if ((flags & IResourceDelta.MOVED_TO) != 0) {
				IPath movedFrom = delta.getResource().getProjectRelativePath();
				// Remove the project name, thus making path relative.
				IPath movedTo = delta.getMovedToPath().removeFirstSegments(1);
				IProject project = resource.getProject();

				Activator.SESSION.sendMove(project, movedFrom, movedTo);

				// Children will be moved by Eclipse, no need to visit them.
				visitChildren = false;
			}
			// The resource was moved from another location. You can find
			// out the path it came from by calling
			// IResourceDelta.getMovedFromPath.
			if ((flags & IResourceDelta.MOVED_FROM) != 0) {
				// Ignoring, since MOVED_TO covers this
			}
			// The resource has changed type. If the resource was previously
			// a file then it is now a folder, and vice-versa.
			if ((flags & IResourceDelta.TYPE) != 0) {
				// Will this ever happen?
				// Ignoring for now.
				Activator.getLogger().log(
						Level.WARNING,
						"Type changed to " + resource.getType()
								+ ", but not handled. Resource "
								+ resourcePortableString);
			}
			// The resource's synchronization information has changed. Sync
			// info is used to determine if a resource is in sync with some
			// remote server, and is not typically of interest to local
			// tools. See the API interface ISynchronizer for more details.
			//
			// Is true when importing files in Eclipse, and when changes are
			// detected after refreshing the workspace tree.
			if ((flags & IResourceDelta.SYNC) != 0) {
				// Changes are caught by appropriate if's.
			}
			// The resource's markers have changed. Markers are annotations
			// to resources such as breakpoints, bookmarks, to-do items,
			// etc. The method IResourceDelta.getMarkerDeltas() is used to
			// find out exactly which markers have changed.
			if ((flags & IResourceDelta.MARKERS) != 0) {
				// Markers are not part of the file itself, therefore not
				// synchronizing.
			}
			// The resource has been replaced by a different resource at the
			// same location (i.e., the resource has been deleted and then
			// re-added).
			if ((flags & IResourceDelta.REPLACED) != 0) {
				Activator.getLogger().log(
						Level.WARNING,
						"Resource replaced, but not handled. Resource "
								+ resourcePortableString + ".");
			}

		}

		return visitChildren;
	}

}
