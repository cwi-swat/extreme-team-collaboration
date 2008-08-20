package nl.jeldertpol.xtc.client.views.currentSession;

import java.util.Set;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.client.session.whosWhere.WhosWhere;
import nl.jeldertpol.xtc.client.session.whosWhere.WhosWhereListener;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorDescriptor;
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
public class CurrentSessionView extends ViewPart implements WhosWhereListener {

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
		for (int i = 0; i < titles.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(titles[i]);
		}

		// Fill table with current information
		WhosWhere whosWhere = Activator.SESSION.getWhosWhere();
		Set<String> nicknames = whosWhere.getNicknames();
		for (String nickname : nicknames) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(COLUMN_NICKNAME, nickname);
			item.setText(COLUMN_RESOURCE, whosWhere.getFilePath(nickname));
		}

		for (int i = 0; i < titles.length; i++) {
			table.getColumn(i).pack();
		}

		whosWhere.addListener(this);

		final TableCursor cursor = new TableCursor(table, SWT.NULL);
		cursor.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(final MouseEvent e) {
				// Nothing to do
			}

			@Override
			public void mouseDown(final MouseEvent e) {
				// Nothing to do
			}

			@Override
			public void mouseDoubleClick(final MouseEvent e) {
				int column = cursor.getColumn();
				String text = cursor.getRow().getText(column);

				handleEvent(column, text);
			}
		});
		cursor.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(final KeyEvent e) {
				// Enter
				if (e.character == SWT.CR) {
					int column = cursor.getColumn();
					String text = cursor.getRow().getText(column);

					handleEvent(column, text);
				}
			}

			@Override
			public void keyPressed(final KeyEvent e) {
				// Nothing to do
			}
		});
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
		new CurrentSessionUpdateJob(table, nickname, filePath);
	}

	private void handleEvent(final int column, final String text) {
		if (column == COLUMN_NICKNAME) {
			// TODO initiate chat
			System.out.println("Chat: " + text);
		} else if (column == COLUMN_RESOURCE) {
			// Open resource
			System.out.println("Open: " + text);

			IProject project = ResourcesPlugin.getWorkspace().getRoot()
					.getProject(Activator.SESSION.getCurrentProject());

			IResource resource = project.findMember(text);
			System.out.println("" + resource);
			ITextEditor editor = Activator.documentReplacer
					.findEditor(resource);

			if (editor != null) {
				editor.getEditorSite().getPage().activate(editor);
			} else {
				IEditorDescriptor desc = PlatformUI.getWorkbench()
						.getEditorRegistry().getDefaultEditor(
								resource.getName());

				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();

				IFile file = (IFile) resource;

				try {
					String editorID = EditorsUI.DEFAULT_TEXT_EDITOR_ID;
					if (desc != null) {
						editorID = desc.getId();
					}

					page.openEditor(new FileEditorInput(file), editorID);

				} catch (PartInitException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

}
