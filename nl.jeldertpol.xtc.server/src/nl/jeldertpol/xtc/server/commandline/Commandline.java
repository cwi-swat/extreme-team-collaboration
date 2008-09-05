package nl.jeldertpol.xtc.server.commandline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import nl.jeldertpol.xtc.common.conversion.Conversion;
import nl.jeldertpol.xtc.common.session.SimpleSession;
import nl.jeldertpol.xtc.server.Server;
import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermBlob;

/**
 * @author Jeldert Pol
 * 
 */
public class Commandline {
	final Server server;

	public static void main(final String[] args) throws Exception {
		new Commandline(args);
	}

	/**
	 * @throws Exception
	 * 
	 */
	public Commandline(final String[] args) throws Exception {
		super();

		server = new Server();
		server.connect(args);

		printHelp();
		loopInput();
	}

	private void loopInput() {

		while (true) {
			String input = readInput();

			if (input.equals("H")) {
				printHelp();
			} else if (input.equals("S")) {
				showSessions();
			} else if (input.equals("K")) {
				// kickClient();
			}
		}
	}

	private void showSessions() {
		List<SimpleSession> sessions = server.getSimpleSessions();

		for (SimpleSession simpleSession : sessions) {
			System.out.println(simpleSession.getProjectName() + " (" + simpleSession.getRevision() + ")");
			for (String client : simpleSession.getClients()) {
				System.out.println("- " + client);
			}
			System.out.println();
		}
		
	}

	private void printHelp() {
		System.out.println("XTC administrative console.");
		System.out.println("Usage:");
		System.out.println("S: Show sessions and clients");
		System.out.println("K: Kick a client");
	}

	private String readInput() {
		// open up standard input
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		String input = "";

		// Read the input from the command-line.
		try {
			input = br.readLine();
		} catch (IOException ioe) {
			System.out.println("IO error trying to read input!");
		}

		return input;
	}

}
