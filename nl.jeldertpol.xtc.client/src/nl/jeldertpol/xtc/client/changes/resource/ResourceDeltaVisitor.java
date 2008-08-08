package nl.jeldertpol.xtc.client.changes.resource;

import java.io.InputStream;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.client.exceptions.LeaveSessionException;

import org.eclipse.core.resources.IFile;
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
	public boolean visit(IResourceDelta delta) throws CoreException {
		boolean ofInterest = false;
		boolean visitChildren = true;

		IResource resource = delta.getResource();
		int flags = delta.getFlags();

		System.out.println(resource.getFullPath().toString());

		switch (delta.getKind()) {
		case IResourceDelta.NO_CHANGE:
			// Obvious, this is not of interest.
			ofInterest = false;
			break;
		case IResourceDelta.ADDED:
			// Resource is added, but could be the result of a move.
			System.out.println("Add resource???");

			if ((flags & IResourceDelta.MOVED_FROM) != 0) {
				// See, it is the result of a move.
				System.out.println("No, added resource is a move...");

				// Of interest, because it is a move.
				ofInterest = true;
			} else {
				// It is really a new resource.
				System.out
						.println("Yes, added resource is not moved, so new file...");

				IProject project = resource.getProject();
				IPath resourcePath = resource.getProjectRelativePath();

				Activator.session.sendAddedResource(project, resourcePath,
						resource.getType());

				// Nothing left of interest in this delta.
				ofInterest = false;
			}

			break;
		case IResourceDelta.REMOVED:
			// Resource is removed, but could be the result of a move.
			System.out.println("Remove resource???");

			if ((flags & IResourceDelta.MOVED_TO) != 0) {
				// See, it is the result of a remove.
				System.out.println("No, removed resource is a move...");

				// Of interest, because it is a move.
				ofInterest = true;
			} else {
				// It really is the removal of a resource.
				System.out
						.println("Yes, removed resource is not moved, so remove file...");

				IProject project = resource.getProject();
				IPath resourcePath = resource.getProjectRelativePath();

				Activator.session.sendRemovedResource(project, resourcePath);

				// Nothing left of interest in this delta.
				ofInterest = false;
			}

			ofInterest = true;
			break;
		case IResourceDelta.CHANGED:
			// Content of resource, or one of its children changed.
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
			// TODO default needed? All cases are covered above...
			ofInterest = true;
			break;
		}

		if (ofInterest) {
			// The filesystem modification timestamp has changed since the
			// last notification. IResource.touch() will also trigger a
			// content change notification, even though the content may not
			// have changed in the file system.
			if ((flags & IResourceDelta.CONTENT) != 0) {
				System.out.println("CONTENT");

				IProject project = resource.getProject();

				IFile file = (IFile) resource;
				InputStream content = file.getContents();

				IPath filePath = file.getProjectRelativePath();

				Activator.session.sendContent(project, filePath, content);

				visitChildren = false;
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
				
				try {
					Activator.session.leaveSession();
				} catch (LeaveSessionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// The resource was moved to another location. The location it
			// was moved to is indicated by IResourceDelta.getMovedToPath.
			if ((flags & IResourceDelta.MOVED_TO) != 0) {
				IPath moveFrom = delta.getResource().getFullPath();
				IPath moveTo = delta.getMovedToPath();
				IProject project = resource.getProject();

				System.out.println("MOVED_TO: " + moveFrom.toString() + " --> "
						+ moveTo.toString());

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
				// Will this ever happen?
				// Ignoring for now.
				System.out.println("TYPE changed!!! > "
						+ resource.getFullPath().toPortableString()
						+ ": is now of type " + resource.getType());
			}
			// The resource's synchronization information has changed. Sync
			// info is used to determine if a resource is in sync with some
			// remote server, and is not typically of interest to local
			// tools. See the API interface ISynchronizer for more details.
			if ((flags & IResourceDelta.SYNC) != 0) {
				System.out.println("SYNC");
				// A sync should detect a content change. Therefore ignoring
				// this one.
			}
			// The resource's markers have changed. Markers are annotations
			// to resources such as breakpoints, bookmarks, to-do items,
			// etc. The method IResourceDelta.getMarkerDeltas() is used to
			// find out exactly which markers have changed.
			if ((flags & IResourceDelta.MARKERS) != 0) {
				System.out.println("MARKERS");
				// Markers are not part of the file itself, therefore not
				// synchronizing.
			}
			// The resource has been replaced by a different resource at the
			// same location (i.e., the resource has been deleted and then
			// re-added).
			if ((flags & IResourceDelta.REPLACED) != 0) {
				System.out.println("REPLACED");
				// When does this happen?
			}

		}

		return visitChildren;
	}
}
