package nl.jeldertpol.xtc.client.changes.resource;

import nl.jeldertpol.xtc.client.Activator;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

/**
 * Listener for changes to resources. Events are fired when saving a file.
 * 
 * @author Jeldert Pol
 */
public class ResourceChangeListener implements IResourceChangeListener {

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		// TODO ignore bin? (build map)

		// TODO remove fakeStart before production
		if (Activator.SESSION.inSession() || Activator.fakeStart) {
			assert (event.getType() == IResourceChangeEvent.POST_CHANGE);
			
			// Only listen to project in current session.
			IResourceDelta delta = event.getDelta();
			IResourceDelta projectDelta = delta.findMember(new Path(
					Activator.SESSION.getCurrentProject()));

			System.out.println("resourceChanged");

			try {
				projectDelta.accept(new ResourceDeltaVisitor());
			} catch (NullPointerException e) {
				// Resource changed for a project other that the project in the session. Ignoring.
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
