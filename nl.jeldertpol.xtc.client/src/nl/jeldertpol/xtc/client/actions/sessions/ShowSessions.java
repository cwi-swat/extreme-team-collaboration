package nl.jeldertpol.xtc.client.actions.sessions;

import java.util.ArrayList;
import java.util.List;

import nl.jeldertpol.xtc.common.Session.SimpleSession;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Jeldert Pol
 */
public class ShowSessions extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// try {
		// List<SimpleSession> sessions = Activator.session.getSessions();
		List<SimpleSession> sessions = new ArrayList<SimpleSession>();
		// Tijdelijk vullen
		SimpleSession session = new SimpleSession("testProject", 6L, "Rick");
		sessions.add(session);

		SimpleSession session2 = new SimpleSession("XTCproject", 534L,
				"Jeldert");
		session2.addClient("Sjon");
		sessions.add(session2);

		IWorkbenchWindow window = HandlerUtil
				.getActiveWorkbenchWindowChecked(event);
		ElementTreeSelectionDialog sessionsDialog = new ElementTreeSelectionDialog(
				window.getShell(), new SessionLabelProvider(),
				new TreeNodeContentProvider());

		TreeNode[] treeNodes = createTreeNodes(sessions);
		sessionsDialog.setInput(treeNodes);

		sessionsDialog.setTitle("Sessions");
		sessionsDialog.setBlockOnOpen(true);
		sessionsDialog.setHelpAvailable(false);
		sessionsDialog.setEmptyListMessage("No sessions on the server.");

		sessionsDialog.open();
		// } catch (UnableToConnectException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		return null;
	}

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
			}

			sessionNode.setChildren(clients);
			sessions[i] = sessionNode;
		}

		return sessions;
	}

}
