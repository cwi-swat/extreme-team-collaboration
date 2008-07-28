package nl.jeldertpol.xtc.server;

import toolbus.adapter.java.AbstractJavaTool;
import aterm.ATerm;

/**
 * @author Jeldert Pol
 * 
 */
public class Server extends AbstractJavaTool {

	/**
	 * 
	 */
	public Server(String host, String port) {
		String toolname = Server.class.getName();
		String[] connectioninfo = { "-TYPE", "remote", "-TB_TOOL_NAME",
				toolname, "-TB_HOST", host, "-TB_PORT", port };
		try {
			connect(connectioninfo);
			System.out.println("XTC server is up and running...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Server(String[] args) {
		super();
		try {
			connect(args);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		System.out.println("Connected!");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		for (int i = 0; i < args.length; i++) {
//			System.out.println("Arg: " + args[i]);
//		}
//		if (args.length == 2) {
//			String host = args[0];
//			String port = args[1];
//
//			new Server(host, port);
		new Server(args);
//		} else {
//			System.out.println("Use: " + Server.class.getName() + " host port");
//			System.out.println("Example: java -jar server.jar localhost 8998");
//		}
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
