package nl.jeldertpol.xtc.client.actions.sessions;

import java.util.List;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.client.exceptions.XTCException;
import nl.jeldertpol.xtc.common.Session.SimpleSession;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
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
public class ShowSessions extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// Only show the sessions
		showSessions();

		return null;
	}

	/**
	 * Shows a list of current sessions on the server, with the connected
	 * clients. A user is allowed to select one of these sessions. When a client
	 * is selected, the name of the related project is returned.
	 * 
	 * @return The name of the selected session, or <code>null</code> when no
	 *         session was selected, or cancel was pressed.
	 */
	public String showSessions() {
		String projectName = null;

		try {
			List<SimpleSession> sessions = Activator.session.getSessions();

			// Tijdelijk vullen
			SimpleSession session1 = new SimpleSession("testProject", 6L,
					"Rick");
			sessions.add(session1);

			SimpleSession session2 = new SimpleSession("XTCproject", 534L,
					"Jeldert");
			session2.addClient("Sjon");
			sessions.add(session2);
			// Eind tijdelijk vullen

			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getShell();

			ElementTreeSelectionDialog sessionsDialog = new ElementTreeSelectionDialog(
					shell, new SessionLabelProvider(),
					new TreeNodeContentProvider());

			TreeNode[] treeNodes = createTreeNodes(sessions);
			sessionsDialog.setInput(treeNodes);

			sessionsDialog.setTitle("Sessions");
			sessionsDialog.setEmptyListMessage("No sessions on the server.");
			sessionsDialog.setAllowMultiple(false);
			sessionsDialog.setBlockOnOpen(true);
			sessionsDialog.setHelpAvailable(false);

			int returnCode = sessionsDialog.open();

			if (returnCode == ElementTreeSelectionDialog.OK) {
				Object selection = sessionsDialog.getFirstResult();
				projectName = getProjectNameFromSelection(selection);
			}
		} catch (XTCException e) {
			e.printStackTrace();
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
	private TreeNode[] createTreeNodes(List<SimpleSession> simpleSessions) {
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
	private String getProjectNameFromSelection(Object selection) {
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
