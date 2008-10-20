package nl.jeldertpol.xtc.client.views.whosWhere;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.client.session.whosWhere.WhosWhereTracker;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.progress.UIJob;

/**
 * An {@link UIJob} that can safely update {@link WhosWhereTracker} information
 * in the view. Needs to be safe to prevent invalid thread access exception from
 * SWT.
 * 
 * @author Jeldert Pol
 */
public class WhosWhereUpdateJob extends UIJob {

	/**
	 * Table containing data displayed.
	 */
	private final Table table;

	/**
	 * Nickname affected by change.
	 */
	private final String nickname;

	/**
	 * FilePath affected by change.
	 */
	private final String filePath;

	/**
	 * Create a new job. Schedules itself. Will call
	 * {@link #update(Table, String, String)} when it runs.
	 * 
	 * @param table
	 *            Table containing data displayed.
	 * @param nickname
	 *            Nickname affected by change.
	 * @param filePath
	 *            FilePath affected by change.
	 */
	public WhosWhereUpdateJob(final Table table, final String nickname,
			final String filePath) {
		super(WhosWhereUpdateJob.class.getName() + ": " + nickname);

		this.table = table;
		this.nickname = nickname;
		this.filePath = filePath;

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
		return WhosWhereUpdateJob.update(table, nickname, filePath);
	}

	/**
	 * Call this when already running in a UIThread. Otherwise, use
	 * {@link WhosWhereUpdateJob}.
	 * 
	 * @param table
	 *            Table containing data displayed.
	 * @param nickname
	 *            Nickname affected by change.
	 * @param filePath
	 *            FilePath affected by change.
	 * @return Status of replace.
	 */
	public static IStatus update(final Table table, final String nickname,
			final String filePath) {
		IStatus status;
		boolean found = false;

		for (TableItem tableItem : table.getItems()) {
			if (tableItem.getText(0).equals(nickname)) {
				tableItem.setText(1, filePath);
				found = true;
			}
		}

		if (!found) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(WhosWhereView.COLUMN_NICKNAME, nickname);
			item.setText(WhosWhereView.COLUMN_RESOURCE, filePath);
		}

		status = new Status(IStatus.OK, Activator.PLUGIN_ID,
				"CurrentSessionUpdateJob finished successfully.");

		return status;
	}

}
