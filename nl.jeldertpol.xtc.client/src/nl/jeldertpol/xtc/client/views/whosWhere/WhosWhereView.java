package nl.jeldertpol.xtc.client.views.whosWhere;

import java.util.Set;
import java.util.logging.Level;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.client.session.whosWhere.WhosWhere;
import nl.jeldertpol.xtc.client.session.whosWhere.WhosWhereListener;
import nl.jeldertpol.xtc.client.views.chat.ChatView;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Display nicknames of clients and the file they are editing.
 * 
 * @author Jeldert Pol
 */
public class WhosWhereView extends ViewPart implements WhosWhereListener {

	/**
	 * Table containing data displayed.
	 */
	private Table table;

	/**
	 * Nickname column of table.
	 */
	public static final int COLUMN_NICKNAME = 0;

	/**
	 * Resource column of table.
	 */
	public static final int COLUMN_RESOURCE = 1;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(final Composite parent) {
		table = new Table(parent, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		table.setLayoutData(data);

		String[] titles = { "Nickname", "Resource" };

		for (String title : titles) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(title);
		}

		// Fill table with current information
		WhosWhere whosWhere = Activator.SESSION.getWhosWhere();
		Set<String> nicknames = whosWhere.getNicknames();
		for (String nickname : nicknames) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(COLUMN_NICKNAME, nickname);
			item.setText(COLUMN_RESOURCE, whosWhere.getFilePath(nickname));
		}

		for (TableColumn column : table.getColumns()) {
			column.pack();
		}

		final TableCursor cursor = new TableCursor(table, SWT.NULL);
		cursor.addMouseListener(new MouseAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.MouseAdapter#mouseDoubleClick(org.eclipse
			 * .swt.events.MouseEvent)
			 */
			@Override
			public void mouseDoubleClick(final MouseEvent e) {
				int column = cursor.getColumn();
				String text = cursor.getRow().getText(column);

				handleEvent(column, text);
			}
		});

		cursor.addKeyListener(new KeyAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.KeyAdapter#keyReleased(org.eclipse.swt
			 * .events.KeyEvent)
			 */
			@Override
			public void keyReleased(final KeyEvent e) {
				// Enter
				if (e.character == SWT.CR) {
					int column = cursor.getColumn();
					String text = cursor.getRow().getText(column);

					handleEvent(column, text);
				}
			}
		});

		whosWhere.addListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// Nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.jeldertpol.xtc.client.session.whosWhere.WhosWhereListener#updateWhosWhere
	 * (java.lang.String, java.lang.String)
	 */
	@Override
	public void updateWhosWhere(final String nickname, final String filePath) {
		// Job will update UI.

		if (Display.getCurrent() == null) {
			// Not running in a UIThread, so create a UIJob.
			new WhosWhereUpdateJob(table, nickname, filePath);
		} else {
			// Already running in a UIThread, so apply change directly.
			WhosWhereUpdateJob.update(table, nickname, filePath);
		}
	}

	private void handleEvent(final int column, final String text) {
		if (column == COLUMN_NICKNAME) {
			// Open / focus view
			try {
				IViewPart part = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage().showView(
								Activator.CHAT_VIEW_ID);
				if (part instanceof ChatView) {
					// Append nickname to message
					ChatView chatView = (ChatView) part;
					chatView.appendMessage(text + ": ");
				}
			} catch (PartInitException e) {
				Activator.LOGGER.log(Level.SEVERE,
						"Chat view could not be shown.", e);
			}

		} else if (column == COLUMN_RESOURCE) {
			// Try to open the resource.
			IProject project = Activator.COMMON_ACTIONS
					.getProject(Activator.SESSION.getCurrentProject());

			// Look if resource is already opened
			IResource resource = project.findMember(text);
			ITextEditor editor = Activator.COMMON_ACTIONS.findEditor(resource);

			if (editor != null) {
				// Focus editor
				editor.getEditorSite().getPage().activate(editor);
			} else {
				// Find default editor for resource
				IEditorDescriptor desc = PlatformUI.getWorkbench()
						.getEditorRegistry().getDefaultEditor(
								resource.getName());

				// Default text editor
				String editorID;

				if (desc != null) {
					// Default editor found, use that one
					editorID = desc.getId();
				} else {
					// No default editor, use default text editor
					editorID = EditorsUI.DEFAULT_TEXT_EDITOR_ID;
				}

				// Required variables
				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				IFile file = (IFile) resource;

				try {
					// Open new editor
					page.openEditor(new FileEditorInput(file), editorID);
				} catch (PartInitException e) {
					Activator.LOGGER.log(Level.SEVERE,
							"Editor could not be opened.", e);
				}
			}
		}
	}

}
