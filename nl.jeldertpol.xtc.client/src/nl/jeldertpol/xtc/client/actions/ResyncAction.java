package nl.jeldertpol.xtc.client.actions;

import nl.jeldertpol.xtc.client.Activator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

/**
 * @author Jeldert Pol
 * 
 */
public class ResyncAction extends AbstractHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IResource resource = Activator.documentListener.getResource();
		IPath resourcePath = resource.getProjectRelativePath();

		// Action is called from UIJob, so can directly call this method.
		Activator.COMMON_ACTIONS.revertToSaved(resource);

		Activator.SESSION.requestTextualChanges(resourcePath);

		return null;
	}

}
