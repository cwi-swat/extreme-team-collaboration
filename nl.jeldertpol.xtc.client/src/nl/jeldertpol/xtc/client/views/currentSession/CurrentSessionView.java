package nl.jeldertpol.xtc.client.views.currentSession;

import java.util.Set;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.client.session.whosWhere.WhosWhere;
import nl.jeldertpol.xtc.client.session.whosWhere.WhosWhereListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

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

}
