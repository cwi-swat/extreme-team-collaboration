/**
 * 
 */
package nl.jeldertpol.xtc.client.actions.sessions;

import org.eclipse.jface.viewers.TreeNode;

/**
 * @author jeldert
 *
 */
public class ClientTreeNode extends TreeNode {

	private final String client;
	
	public ClientTreeNode(String client) {
		super(client);
		this.client = client;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return client;
	}

}
