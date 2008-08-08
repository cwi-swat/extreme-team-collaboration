package nl.jeldertpol.xtc.client.actions;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.client.exceptions.XtcException;

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
public class LeaveSessionAction extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
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
		if (Activator.SESSION.inSession()) {
			String title = "Leave session?";
			String message = "Leave currently joined session?";

			boolean leave = MessageDialog.openQuestion(parent, title, message);

			if (leave) {
				try {
					Activator.SESSION.leaveSession();
				} catch (XtcException e) {
					e.printStackTrace();
					MessageDialog.openError(null, "XTC Leave", e.getMessage());
				}
			}
		} else {
			MessageDialog.openInformation(null, "XTC Leave",
					"You are not in a session.");
		}
	}

}
