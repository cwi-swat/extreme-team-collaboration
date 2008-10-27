package nl.jeldertpol.xtc.server.session;

import java.util.ArrayList;
import java.util.List;

import nl.jeldertpol.xtc.common.changes.AbstractChange;
import nl.jeldertpol.xtc.common.chat.ChatMessage;
import nl.jeldertpol.xtc.common.session.SimpleSession;
import nl.jeldertpol.xtc.common.whosWhere.WhosWhere;

/**
 * Representation of a project. Also holds changes.
 * 
 * @author Jeldert Pol
 */
public class Session extends SimpleSession {

	private final List<AbstractChange> changes;

	private final List<WhosWhere> whosWheres;

	/**
	 * Holds the chat messages.
	 */
	private final List<ChatMessage> chatMessages = new ArrayList<ChatMessage>();

	private static final long serialVersionUID = 3L;

	/**
	 * Create a new project.
	 * 
	 * @param projectName
	 *            The name of the project.
	 * @param revision
	 *            The revision of the project.
	 * @param nickname
	 *            The client that started this session.
	 */
	public Session(final String projectName, final Long revision,
			final String nickname) {
		super(projectName, revision);
		addClient(nickname);

		changes = new ArrayList<AbstractChange>();
		whosWheres = new ArrayList<WhosWhere>();
	}

	/**
	 * Add a change to this session.
	 * 
	 * @param change
	 *            The change.
	 */
	public void addChange(final AbstractChange change) {
		changes.add(change);
	}

	/**
	 * Get all changes made in this session.
	 * 
	 * @return All changes, in order from first, to last.
	 */
	public List<AbstractChange> getChanges() {
		return changes;
	}

	public void addChat(ChatMessage chatMessage) {
		chatMessages.add(chatMessage);
	}

	public void addWhosWhere(final WhosWhere whosWhere) {
		whosWheres.add(whosWhere);
	}

	/**
	 * @return the whosWheres
	 */
	public List<WhosWhere> getWhosWheres() {
		return whosWheres;
	}

}
