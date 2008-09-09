package nl.jeldertpol.xtc.server.commandline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import nl.jeldertpol.xtc.common.session.SimpleSession;
import nl.jeldertpol.xtc.server.Server;
import toolbus.adapter.java.AbstractJavaTool;

/**
 * Command Line Interface for the server. Can print current sessions and
 * clients. Can also kick a client, in case the client crashed, but is still
 * present on the server.
 * 
 * @author Jeldert Pol
 */
public class Commandline {
	final Server server;

	/**
	 * Starting point for XTC Server. Can be called directly from the Toolbus
	 * script. This supplies the correct args (as required by
	 * {@link AbstractJavaTool#connect(String[])}).
	 * 
	 * @param args
	 *            Arguments to connect to the Toolbus.
	 * @throws Exception
	 *             Thrown when something goes wrong during the parsing of the
	 *             arguments or the establishing of the connection.
	 */
	public static void main(final String[] args) throws Exception {
		new Commandline(args);
	}

	/**
	 * Constructor. Connects to the server, prints help, and starts taking user
	 * input.
	 * 
	 * @param args
	 *            Arguments to connect to the Toolbus.
	 * @throws Exception
	 *             Thrown when something goes wrong during the parsing of the
	 *             arguments or the establishing of the connection.
	 */
	public Commandline(final String[] args) throws Exception {
		super();

		server = new Server();
		server.connect(args);

		printHelp();
		loopInput();
	}

	/**
	 * Read the input on the command line, and process it.
	 */
	private void loopInput() {

		while (true) {
			String input = readInput();

			if (input.equals("H")) {
				printHelp();
			} else if (input.equals("S")) {
				showSessions();
			} else if (input.equals("K")) {
				kickClient();
			}
		}
	}

	/**
	 * Read the input on the command line
	 * 
	 * @return The input, or an empty String ("") in case of an error.
	 */
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

	/**
	 * Prints a help message.
	 */
	private void printHelp() {
		System.out.println("XTC administrative console.");
		System.out.println("Usage:");
		System.out.println("S: Show sessions and clients.");
		System.out
				.println("K: Kick a client. (Should only be used if client crashed!)");
	}

	/**
	 * Shows the sessions and clients on the server.
	 */
	private void showSessions() {
		List<SimpleSession> sessions = server.getSimpleSessions();

		for (SimpleSession simpleSession : sessions) {
			System.out.println(simpleSession.getProjectName() + " ("
					+ simpleSession.getRevision() + ")");
			for (String client : simpleSession.getClients()) {
				System.out.println("- " + client);
			}
			System.out.println();
		}

	}

	/**
	 * Kicks a client from the server. Asks for a session and client. Incorrect
	 * input will be ignored.
	 */
	private void kickClient() {
		System.out.println("Enter name of session:");
		String inputProject = readInput();

		List<SimpleSession> sessions = server.getSimpleSessions();

		for (SimpleSession simpleSession : sessions) {
			System.out.println(simpleSession.getProjectName());
		}

		boolean foundSession = false;
		boolean foundClient = false;

		for (SimpleSession simpleSession : sessions) {
			if (simpleSession.getProjectName().equals(inputProject)) {
				foundSession = true;

				for (String client : simpleSession.getClients()) {
					System.out.println("- " + client);
				}

				System.out.println("Enter name of client to kick:");
				String inputClient = readInput();
				for (String client : simpleSession.getClients()) {
					if (client.equals(inputClient)) {
						foundClient = true;

						server.leaveSession(inputProject, inputClient);
						System.out.println("Client kicked!");

						break;
					}
				}
				break;
			}
		}

		if (!foundSession) {
			System.out.println("Session not found.");
		} else if (!foundClient) {
			System.out.println("Client not found.");
		}

		printHelp();
	}

}
