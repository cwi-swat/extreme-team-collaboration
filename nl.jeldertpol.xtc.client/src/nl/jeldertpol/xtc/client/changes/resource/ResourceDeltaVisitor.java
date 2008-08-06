package nl.jeldertpol.xtc.client.changes.resource;

import nl.jeldertpol.xtc.client.Activator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

/**
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
	public boolean visit(IResourceDelta delta) throws CoreException {
		boolean ofInterest = false;
		boolean visitChildren = true;
		
		switch (delta.getKind()) {
		case IResourceDelta.NO_CHANGE:
			ofInterest = false;
			break;
		case IResourceDelta.ADDED:
			ofInterest = true;
			break;
		case IResourceDelta.REMOVED:
			ofInterest = true;
			break;
		case IResourceDelta.CHANGED:
			ofInterest = true;
			break;
		case IResourceDelta.ADDED_PHANTOM:
			// TODO of interest?
			ofInterest = true;
			break;
		case IResourceDelta.REMOVED_PHANTOM:
			// TODO of interest?
			ofInterest = true;
			break;
		default:
			ofInterest = true;
			break;
		}

		if (ofInterest) {
			IResource resource = delta.getResource();
			int flags = delta.getFlags();

			System.out.println(resource.getProjectRelativePath().toString());

			// IResourceDelta#CONTENT
			// IResourceDelta#DESCRIPTION
			// IResourceDelta#ENCODING
			// IResourceDelta#OPEN
			// IResourceDelta#MOVED_TO
			// IResourceDelta#MOVED_FROM
			// IResourceDelta#TYPE
			// IResourceDelta#SYNC
			// IResourceDelta#MARKERS
			// IResourceDelta#REPLACED

			// The filesystem modification timestamp has changed since the
			// last notification. IResource.touch() will also trigger a
			// content change notification, even though the content may not
			// have changed in the file system.
			if ((flags & IResourceDelta.CONTENT) != 0) {
				System.out.println("CONTENT");
				// ((IFile)resource).setContents(source, force, keepHistory,
				// monitor);
			}
			// The project description has changed.
			if ((flags & IResourceDelta.DESCRIPTION) != 0) {
				System.out.println("DESCRIPTION");
			}
			// The character encoding for a file, or for the files inside a
			// container, have changed. For listeners that care about the
			// character content of the file, as opposed to the raw bytes,
			// this should typically be treated the same as a content
			// change.
			if ((flags & IResourceDelta.ENCODING) != 0) {
				System.out.println("ENCODING");
			}
			// The project has either been opened or closed. If the project
			// is now open, then it was previously closed, and vice-versa.
			if ((flags & IResourceDelta.OPEN) != 0) {
				System.out.println("OPEN");
			}
			// The resource was moved to another location. The location it
			// was moved to is indicated by IResourceDelta.getMovedToPath.
			if ((flags & IResourceDelta.MOVED_TO) != 0) {
				IPath moveFrom = delta.getResource().getFullPath();
				IPath moveTo = delta.getMovedToPath();
				IProject project = delta.getResource().getProject();
				
				System.out.println("MOVED_TO: " + moveFrom.toString() + " --> " + moveTo.toString());
				
				Activator.session.sendMove(project, moveFrom, moveTo);
				
				// Children will be moved by Eclipse, no need to visit them.
				visitChildren = false;
			}
			// The resource was moved from another location. You can find
			// out the path it came from by calling
			// IResourceDelta.getMovedFromPath.
			if ((flags & IResourceDelta.MOVED_FROM) != 0) {
				System.out.println("MOVED_FROM");
				// Ignoring, since MOVED_TO covers this
			}
			// The resource has changed type. If the resource was previously
			// a file then it is now a folder, and vice-versa.
			if ((flags & IResourceDelta.TYPE) != 0) {
				System.out.println("TYPE");
			}
			// The resource's synchronization information has changed. Sync
			// info is used to determine if a resource is in sync with some
			// remote server, and is not typically of interest to local
			// tools. See the API interface ISynchronizer for more details.
			if ((flags & IResourceDelta.SYNC) != 0) {
				System.out.println("SYNC");
			}
			// The resource's markers have changed. Markers are annotations
			// to resources such as breakpoints, bookmarks, to-do items,
			// etc. The method IResourceDelta.getMarkerDeltas() is used to
			// find out exactly which markers have changed.
			if ((flags & IResourceDelta.MARKERS) != 0) {
				System.out.println("MARKERS");
			}
			// The resource has been replaced by a different resource at the
			// same location (i.e., the resource has been deleted and then
			// re-added).
			if ((flags & IResourceDelta.REPLACED) != 0) {
				System.out.println("REPLACED");
			}

		}

		// // only interested in content changes
		// if ((delta.getFlags() & IResourceDelta.CONTENT) == 0)
		// return true;
		// IResource resource = delta.getResource();

		// TODO Auto-generated method stub
		return visitChildren;
	}
}
