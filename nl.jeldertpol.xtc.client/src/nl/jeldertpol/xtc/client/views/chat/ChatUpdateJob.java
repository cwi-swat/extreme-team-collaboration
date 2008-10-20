package nl.jeldertpol.xtc.client.views.chat;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.client.session.whosWhere.WhosWhereTracker;
import nl.jeldertpol.xtc.common.chat.ChatMessage;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.progress.UIJob;

/**
 * An {@link UIJob} that can safely update {@link WhosWhereTracker} information in the
 * view. Needs to be safe to prevent invalid thread access exception from SWT.
 * 
 * @author Jeldert Pol
 */
public class ChatUpdateJob extends UIJob {

	/**
	 * StyledText containing already received messages.
	 */
	private final StyledText chatText;

	/**
	 * New chat message.
	 */
	private final ChatMessage chatMessage;

	/**
	 * Create a new job. Schedules itself.
	 * 
	 * @param chatText
	 *            StyledText containing already received messages.
	 * @param chatMessage
	 *            New chat message.
	 */
	public ChatUpdateJob(final StyledText chatText,
			final ChatMessage chatMessage) {
		super(ChatUpdateJob.class.getName() + ": " + "Chat "
				+ chatMessage.getNickname());

		this.chatText = chatText;
		this.chatMessage = chatMessage;

		setPriority(SHORT);

		schedule();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	public IStatus runInUIThread(final IProgressMonitor monitor) {
		IStatus status;

		try {
			// Get time and format it
			Calendar calendar = new GregorianCalendar();
			calendar.setTimeInMillis(chatMessage.getTimestamp());
			
			NumberFormat formatter = new DecimalFormat("00");
			String hour = formatter.format(calendar.get(Calendar.HOUR_OF_DAY));
			String minute = formatter.format(calendar.get(Calendar.MINUTE));
			String second = formatter.format(calendar.get(Calendar.SECOND));

			// Compose different text parts
			String timeText = "[" + hour + ":" + minute + ":" + second + "] ";
			String nicknameText = "<" + chatMessage.getNickname() + "> ";
			String messageText = chatMessage.getMessage();

			// Append time
			chatText.append("\n");
			chatText.append(timeText);

			// Style time
			StyleRange timeStyle = new StyleRange();
			Color timeColor = chatText.getDisplay().getSystemColor(
					SWT.COLOR_GRAY);
			timeStyle.foreground = timeColor;
			timeStyle.start = chatText.getCharCount() - timeText.length();
			timeStyle.length = timeText.length();
			chatText.setStyleRange(timeStyle);

			// Append nickname
			chatText.append(nicknameText);

			// Style nickname
			StyleRange nicknameStyle = new StyleRange();
			Color nicknameColor = chatText.getDisplay().getSystemColor(
					SWT.COLOR_DARK_GRAY);
			nicknameStyle.foreground = nicknameColor;
			nicknameStyle.start = chatText.getCharCount()
					- nicknameText.length();
			nicknameStyle.length = nicknameText.length();
			chatText.setStyleRange(nicknameStyle);

			// Append message
			chatText.append(messageText);

			// Scroll to end of text
			chatText.invokeAction(ST.TEXT_END);

			status = new Status(IStatus.OK, Activator.PLUGIN_ID,
					"ChatUpdateJob finished successfully.");
		} catch (SWTException e) {
			// Sometimes this is thrown, even though nothing seems to be wrong?
			if (e.code == SWT.ERROR_WIDGET_DISPOSED) {
				// ignore
				status = new Status(IStatus.OK, Activator.PLUGIN_ID,
						"ChatUpdateJob finished successfully.");
			} else {
				Activator.getLogger().log(Level.SEVERE, e);

				status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						"ChatUpdateJob error.");
				throw e;
			}
		}
		return status;
	}
}
