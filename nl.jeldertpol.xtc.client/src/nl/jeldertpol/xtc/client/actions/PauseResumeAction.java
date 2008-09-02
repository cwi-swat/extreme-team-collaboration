package nl.jeldertpol.xtc.client.actions;

import nl.jeldertpol.xtc.client.Activator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * Leave the currently joined session.
 * 
 * @author Jeldert Pol
 */
public class PauseResumeAction extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		leaveSession();

		return null;
	}

	/**
	 * When client is in a session it will ask user to leave it. Shows a message
	 * when not in a session.
	 */
	private void leaveSession() {
		Shell parent = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();
		if (Activator.SESSION.isPaused()) {
			MessageDialog.openInformation(null, "XTC Start/Pause",
					"Pending changes will now be applied.");
			Activator.SESSION.resume();
		} else {
			String title = "Pause?";
			String message = "If you pause, all incoming changes will be hold back untill you resume. Also, when making changes yourself, XTC will resume again. Pause?";

			boolean pause = MessageDialog.openQuestion(parent, title, message);

			if (pause) {
					Activator.SESSION.pause();
			}
		}
	}

}
