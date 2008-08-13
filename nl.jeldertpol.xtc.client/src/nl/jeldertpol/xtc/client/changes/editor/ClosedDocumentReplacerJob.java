package nl.jeldertpol.xtc.client.changes.editor;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.client.views.EditorViewPart;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Jeldert Pol
 * 
 */
public class ClosedDocumentReplacerJob extends UIJob {

	private final int length;

	private final int offset;

	private final String text;

	private IFile file;
	private String id;

	public ClosedDocumentReplacerJob(final int length, final int offset,
			final String text, IFile file, String id) {
		super(file.getName());

		this.length = length;
		this.offset = offset;
		this.text = text;

		this.file = file;
		this.id = id;

		setPriority(INTERACTIVE);

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
	public IStatus runInUIThread(IProgressMonitor monitor) {
		IStatus status;

		IWorkbench bench = PlatformUI.getWorkbench();
		IWorkbenchWindow[] windows = bench.getWorkbenchWindows();
		IWorkbenchPage[] pages = windows[0].getPages();
		IWorkbenchPage page = pages[0];

		String viewId = "nl.jeldertpol.xtc.client.views.editor";
		try {
			IViewPart part = page.showView(viewId);
			if (part instanceof EditorViewPart) {
				EditorViewPart editorViewPart = (EditorViewPart) part;
				editorViewPart.setInput(file);
				TextEditor editor = editorViewPart.getEditor();
				
				new OpenedDocumentReplacerJob(editor, length, offset, text, true);
		
				status = new Status(Status.OK, Activator.PLUGIN_ID,
						"Opened editor successfully.");
			} else {
				status = new Status(Status.ERROR, Activator.PLUGIN_ID,
						"Editor could not be opened.");
			}
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			status = new Status(Status.ERROR, Activator.PLUGIN_ID,
					"Editor could not be opened.");
		}

		return status;
	}

}
