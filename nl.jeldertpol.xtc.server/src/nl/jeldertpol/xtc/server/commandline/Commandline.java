package nl.jeldertpol.xtc.server.commandline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Level;

import nl.jeldertpol.xtc.common.session.SimpleSession;
import nl.jeldertpol.xtc.server.Server;

/**
 * Command Line Interface for the server. Can print current sessions and
 * clients. Can also kick a client, in case the client crashed, but is still
 * present on the server.
 * 
 * @author Jeldert Pol
 */
public class Commandline extends Server {

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.jeldertpol.xtc.server.Server#Server(String[])
	 */
	public static void main(final String[] args) throws InterruptedException,
			Exception {
		new Commandline(args);
	}

	/**
	 * Start a XTC server. Can connect to an external ToolBus, or start an
	 * internal one.
	 * 
	 * The user can control the server via command line input.
	 * 
	 * @param args
	 *            The arguments needed for setting up the connection.
	 * @throws InterruptedException
	 *             When starting an internal ToolBus, a pause in performed, to
	 *             let the ToolBus start.
	 * @throws Exception
	 *             Thrown when something goes wrong during the parsing of the
	 *             arguments or the establishing of the connection.
	 * 
	 * @see #printHelp()
	 * @see #printHelpInput()
	 */
	public Commandline(final String[] args) throws InterruptedException,
			Exception {
		super(args);

		printHelpInput();

		loopInput();
	}

	/**
	 * Read the input on the command line, and process it.
	 */
	private void loopInput() {

		while (true) {
			String input = readInput();

			if ("H".equals(input)) {
				printHelpInput();
			} else if ("S".equals(input)) {
				showSessions();
			} else if ("L".equals(input)) {
				loadSession();
			} else if ("K".equals(input)) {
				kickClient();
			} else if ("Q".equals(input)) {
				quit();
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
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(System.in));

		String input = "";

		// Read the input from the command-line.
		try {
			input = bufferedReader.readLine();
		} catch (IOException ioe) {
			LOGGER.log(Level.SEVERE, ioe);
		}

		return input;
	}

	/**
	 * Prints a help message about how to interact with the server.
	 */
	private void printHelpInput() {
		System.out.println("XTC administrative console.");
		System.out.println("Usage:");
		System.out.println("H: Print this help message.");
		System.out.println("S: Show sessions and clients.");
		System.out.println("L: Load a session.");
		System.out
				.println("K: Kick a client. (Should only be used if client crashed!)");
		System.out
				.println("Q: Quit. Kicks all clients, and saves all sessions.");
	}

	/**
	 * Shows the sessions and clients on the server.
	 */
	private void showSessions() {
		List<SimpleSession> sessions = getSimpleSessions();

		for (SimpleSession simpleSession : sessions) {
			System.out.println(simpleSession.getProjectName() + " ("
					+ simpleSession.getRevision() + ")");
			for (String client : simpleSession.getClients()) {
				System.out.println("- " + client);
			}
			System.out.println();
		}

	}

	private void loadSession() {
		System.out.println("Enter file name of session:");
		String inputSession = readInput();

		loadSession(inputSession);
	}

	/**
	 * Kicks a client from the server. Asks for a session and client. Incorrect
	 * input will be ignored.
	 */
	private void kickClient() {
		System.out.println("Enter name of session:");
		String inputProject = readInput();

		List<SimpleSession> sessions = getSimpleSessions();

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

						leaveSession(inputProject, inputClient);
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

		printHelpInput();
	}

	private void quit() {
		System.out.println("Are you sure? (Y/N)");
		String sure = readInput();

		if ("Y".equals(sure)) {
			// Kick everyone
			List<SimpleSession> sessions = getSimpleSessions();

			for (SimpleSession simpleSession : sessions) {
				for (String client : simpleSession.getClients()) {
					leaveSession(simpleSession.getProjectName(), client);
				}
			}
		}
	}

}
