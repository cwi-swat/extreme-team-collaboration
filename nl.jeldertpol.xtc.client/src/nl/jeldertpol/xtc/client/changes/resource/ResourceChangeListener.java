package nl.jeldertpol.xtc.client.changes.resource;

import java.util.logging.Level;

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
	public void resourceChanged(final IResourceChangeEvent event) {
		if (Activator.SESSION.inSession()) {
			assert (event.getType() == IResourceChangeEvent.POST_CHANGE);

			// Only listen to project in current session.
			IResourceDelta delta = event.getDelta();
			IResourceDelta projectDelta = delta.findMember(new Path(
					Activator.SESSION.getCurrentProject()));

			try {
				projectDelta.accept(new ResourceDeltaVisitor());
			} catch (NullPointerException e) {
				// Resource changed for a project other that the project in the
				// session. Ignoring.
				Activator
						.getLogger()
						.log(Level.FINE,
								"Resource does not belong to session project, ignoring.");
			} catch (CoreException e) {
				Activator.getLogger().log(Level.SEVERE, e);
			}
		}
	}

}
