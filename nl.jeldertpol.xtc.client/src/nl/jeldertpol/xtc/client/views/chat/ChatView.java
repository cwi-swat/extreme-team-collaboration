package nl.jeldertpol.xtc.client.views.chat;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.client.session.chat.ChatListener;
import nl.jeldertpol.xtc.client.session.chat.ChatTracker;
import nl.jeldertpol.xtc.common.chat.ChatMessage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Jeldert Pol
 * 
 */
public class ChatView extends ViewPart implements ChatListener {

	private StyledText chatText;

	private StyledText inputText;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(final Composite parent) {
		GridLayout layout = new GridLayout(2, false);
		parent.setLayout(layout);

		// Empty placeholder
		Label emptyLabel = new Label(parent, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		emptyLabel.setLayoutData(data);

		// Contains all received messages.
		chatText = new StyledText(parent, SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		chatText.setEditable(false);
		data = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		chatText.setLayoutData(data);

		// Send a message button
		Button sendButton = new Button(parent, SWT.PUSH);
		sendButton.setText("Send");
		data = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		sendButton.setLayoutData(data);

		sendButton.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse
			 * .swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(final SelectionEvent e) {
				sendMessage();
			}
		});

		// Text to be send
		inputText = new StyledText(parent, SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		data = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		// Height is 3 lines of text
		int height = inputText.getLineHeight() * 3;
		data.heightHint = height;
		inputText.setLayoutData(data);

		inputText.addKeyListener(new KeyAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.KeyAdapter#keyReleased(org.eclipse.swt
			 * .events.KeyEvent)
			 */
			@Override
			public void keyReleased(final KeyEvent e) {
				// Control Enter
				if (e.character == SWT.CR) {
					sendMessage();
				}
			}
		});

		// Fill chatText with current messages
		ChatTracker chatTracker = Activator.SESSION.getChat();
		for (ChatMessage chatMessage : chatTracker.getChatMessages()) {
			updateChat(chatMessage);
		}

		// Listen to new messages
		chatTracker.addListener(this);
	}

	/**
	 * Send a new message
	 */
	private void sendMessage() {
		String message = inputText.getText();
		inputText.setText("");

		// Also needed to remove empty line inserted by Control Enter.
		message = message.trim();

		if (!message.isEmpty()) {
			Activator.SESSION.sendChat(message);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// Set focus on input
		inputText.setFocus();
	}

	/**
	 * Append some text to the message. Places the caret at the end of the text.
	 * 
	 * @param text
	 *            The text to append.
	 */
	public void appendMessage(final String text) {
		inputText.append(text);
		inputText.setCaretOffset(inputText.getText().length());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.jeldertpol.xtc.client.session.chat.ChatListener#updateChat(nl.jeldertpol
	 * .xtc.common.chat.ChatMessage)
	 */
	@Override
	public void updateChat(final ChatMessage chatMessage) {
		// Job will display new message
		new ChatUpdateJob(chatText, chatMessage);
	}

}
