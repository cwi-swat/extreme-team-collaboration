package nl.jeldertpol.xtc.client.actions.sessions;

import java.util.List;
import java.util.logging.Level;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.client.exceptions.XtcException;
import nl.jeldertpol.xtc.common.session.SimpleSession;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

/**
 * Shows the sessions on the server.
 * 
 * @author Jeldert Pol
 */
public class ShowSessions {

	/**
	 * Shows a list of current sessions on the server, with the connected
	 * clients. A user is allowed to select one of these sessions. The name of
	 * the selected project is returned. When a client is selected, the name of
	 * the related project is returned.
	 * 
	 * @param message
	 *            The message to display in the dialog.
	 * 
	 * @return The name of the selected session, or <code>null</code> when no
	 *         session was selected, or cancel was pressed.
	 */
	public String showSessions(final String message) {
		String projectName = null;

		try {
			List<SimpleSession> sessions = Activator.SESSION.getSessions();

			Shell parent = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getShell();

			ElementTreeSelectionDialog sessionsDialog = new ElementTreeSelectionDialog(
					parent, new SessionLabelProvider(),
					new TreeNodeContentProvider());

			TreeNode[] treeNodes = createTreeNodes(sessions);
			sessionsDialog.setInput(treeNodes);

			sessionsDialog.setTitle("Sessions");
			sessionsDialog.setMessage(message);
			sessionsDialog.setEmptyListMessage("No sessions on the server.");
			sessionsDialog.setAllowMultiple(false);
			sessionsDialog.setBlockOnOpen(true);
			sessionsDialog.setHelpAvailable(false);

			int returnCode = sessionsDialog.open();

			if (returnCode == ElementTreeSelectionDialog.OK) {
				Object selection = sessionsDialog.getFirstResult();
				projectName = getProjectNameFromSelection(selection);
			}
		} catch (XtcException e) {
			Activator.LOGGER.log(Level.WARNING, e);
			MessageDialog.openError(null, "XTC Start/Join", e.getMessage());
		}

		return projectName;
	}

	/**
	 * Convert a {@link TreeNode} array from a {@link List} of
	 * {@link SimpleSession}. A {@link TreeNode} is used to display the
	 * information in a {@link SimpleSession}.
	 * 
	 * The {@link TreeNode} contains the {@link SimpleSession} itself. The
	 * children of the {@link TreeNode} consist of a {@link TreeNode} containing
	 * a {@link String} holding the nickname of a client.
	 * 
	 * @param simpleSessions
	 *            The sessions to convert.
	 * @return The converted sessions.
	 */
	private TreeNode[] createTreeNodes(final List<SimpleSession> simpleSessions) {
		TreeNode[] sessions = new TreeNode[simpleSessions.size()];

		for (int i = 0; i < sessions.length; i++) {
			SimpleSession simpleSession = simpleSessions.get(i);
			TreeNode sessionNode = new TreeNode(simpleSession);

			List<String> sessionClients = simpleSession.getClients();
			TreeNode[] clients = new TreeNode[sessionClients.size()];
			for (int j = 0; j < clients.length; j++) {
				TreeNode clientNode = new TreeNode(sessionClients.get(j));
				clients[j] = clientNode;
				clientNode.setParent(sessionNode);
			}

			sessionNode.setChildren(clients);
			sessions[i] = sessionNode;
		}

		return sessions;
	}

	/**
	 * Get the name of the project from the selection. When a client is
	 * selected, the name of the related project is returned.
	 * 
	 * @param selection
	 *            The selected {@link TreeNode}.
	 * @return The name of the selected project, or <code>null</code>.
	 */
	private String getProjectNameFromSelection(final Object selection) {
		String projectName = null;

		if (selection instanceof TreeNode) {
			TreeNode treeNode = (TreeNode) selection;
			Object value = treeNode.getValue();

			if (value instanceof SimpleSession) {
				SimpleSession session = (SimpleSession) value;
				projectName = session.getProjectName();
			} else if (value instanceof String) {
				TreeNode parent = treeNode.getParent();
				SimpleSession session = (SimpleSession) parent.getValue();
				projectName = session.getProjectName();
			}
		}

		return projectName;
	}

}
