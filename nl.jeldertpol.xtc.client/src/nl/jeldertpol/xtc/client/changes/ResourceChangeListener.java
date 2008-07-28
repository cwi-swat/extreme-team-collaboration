package nl.jeldertpol.xtc.client.changes;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;

public class ResourceChangeListener implements IResourceChangeListener {
	
	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		// TODO Auto-generated method stub
		System.out.println("resourceChanged");
		IResourceDelta delta = event.getDelta();
	}

}
