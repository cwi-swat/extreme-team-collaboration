package nl.jeldertpol.xtc.client.actions;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.client.changes.editor.RevertToSavedJob;

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
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		IResource resource = Activator.documentListener.getResource();
		IPath resourcePath = resource.getProjectRelativePath();

		// Action is called from UIJob, so can directly call this method.
		RevertToSavedJob.revertToSaved(resource);

		Activator.SESSION.requestTextualChanges(resourcePath);

		return null;
	}

}
