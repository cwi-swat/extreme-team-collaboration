package nl.jeldertpol.xtc.client.actions;

import nl.jeldertpol.xtc.client.actions.sessions.ShowSessions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * Shows the sessions on the server.
 * 
 * @author Jeldert Pol
 */
public class ShowSessionsAction extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// Only show the sessions
		ShowSessions showSessions = new ShowSessions();
		showSessions.showSessions("Sessions currently on the server.");

		return null;
	}

}
