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
		// TODO ignore bin (build map)
		
		// Only listen to project in current session.
		// TODO inschakelen
		// if (Activator.session.inSession()) {
		IResourceDelta delta = event.getDelta();
		IResourceDelta projectDelta = delta.findMember(new Path(
				Activator.session.getCurrentProject()));
//		if (projectDelta != null) {
//			System.out.println(projectDelta.getAffectedChildren());
//			for (int i = 0; i < projectDelta.getAffectedChildren().length; i++) {
//				IResourceDelta child = projectDelta.getAffectedChildren()[i];
//				String resource = child.getResource().getProjectRelativePath().toString();
//				System.out.println("Bla: " + resource);
//			}
//		}
		try {
			projectDelta.accept(new ResourceDeltaVisitor());
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		projectDelta.accept(new IResourceDeltaVisitor() {
//		
//			@Override
//			public boolean visit(IResourceDelta delta) throws CoreException {
//				// TODO Auto-generated method stub
//				return false;
//			}
//		})
		IResourceDelta[] resourceDeltas = delta
				.getAffectedChildren();
		// }

		// TODO Auto-generated method stub
		System.out.println("resourceChanged");
		// event.
	}

}
