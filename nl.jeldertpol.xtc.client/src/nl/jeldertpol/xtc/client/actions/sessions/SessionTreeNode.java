package nl.jeldertpol.xtc.client.actions.sessions;

import java.util.List;

import org.eclipse.jface.viewers.TreeNode;

import nl.jeldertpol.xtc.common.Session.SimpleSession;

/**
 * @author Jeldert Pol
 */
public class SessionTreeNode extends TreeNode {

	private final SimpleSession session;
	
	public SessionTreeNode(SimpleSession session) {
		super(session.getProjectName());
		
		List<String> clients = session.getClients();
		ClientTreeNode[] children = new ClientTreeNode[clients.size()];

		for (int i = 0; i < children.length; i++) {
			children[i] = new ClientTreeNode(clients.get(i));
		}

		super.setChildren(children);
		
		this.session = session;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String nameRevision = session.getProjectName() + " (revision " + session.getRevision().toString() + ")";
		return nameRevision;
	}

}
