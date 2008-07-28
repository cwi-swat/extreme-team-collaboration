package nl.jeldertpol.xtc.client.session;

import nl.jeldertpol.xtc.client.exceptions.UnableToConnectException;
import toolbus.adapter.java.AbstractJavaTool;
import aterm.ATerm;

/**
 * @author Jeldert Pol
 * 
 */
public class Toolbus extends AbstractJavaTool {

	public Toolbus() {
		super();
	}
	
	public void connect(String toolname, String host, String port) throws UnableToConnectException {
		try {
			String[] connectioninfo = { "-TYPE", "remote", "-TB_TOOL_NAME",
					toolname, "-TB_HOST", host, "-TB_PORT", port };

			connect(connectioninfo);

			// kwekClientDisplayer = new KwekClientGUI(this, isAdmin);

			// join(nickname);

			// this.nickname = nickname;

		} catch (Exception ex) {
			throw new UnableToConnectException(ex);
		}
	}

	@Override
	public void receiveAckEvent(ATerm term) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveTerminate(ATerm term) {
		// TODO Auto-generated method stub

	}

}
