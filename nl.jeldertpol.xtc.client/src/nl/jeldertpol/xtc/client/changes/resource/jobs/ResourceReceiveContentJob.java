package nl.jeldertpol.xtc.client.changes.resource.jobs;

import java.io.File;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.client.changes.editor.RevertToSavedJob;
import nl.jeldertpol.xtc.common.conversion.Conversion;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Set content of a file.
 * 
 * @author Jeldert Pol
 */
public class ResourceReceiveContentJob extends HighPriorityJob {

	final private IProject project;
	final private IPath filePath;
	final private File file;
	final private byte[] content;

	/**
	 * Set the content of a file. Schedules itself to be run.
	 * 
	 * @param file
	 *            The file the content should be set of.
	 * @param content
	 *            The actual content for the file.
	 */
	public ResourceReceiveContentJob(final IProject project,
			final IPath filePath, final File file, final byte[] content) {
		super(ResourceReceiveContentJob.class.getName() + ": "
				+ file.toString());

		this.project = project;
		this.filePath = filePath;
		this.file = file;
		this.content = content;

		schedule();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		System.out.println("Receiving new content: "
				+ filePath.toPortableString());
		IStatus status;

		// try {
		synchronized (Activator.resourceChangeListener) {
			Activator.SESSION.removeResourceChangeListener();
			Conversion.byteToFile(content, file);

			IFile ifile = project.getFile(filePath);
			try {
				ifile.refreshLocal(IResource.NONE, monitor);

				// Look if there is an open editor with this resource
				ITextEditor editor = Activator.documentReplacer
						.findEditor(ifile);
				if (editor != null) {
					// Reload file from filesystem.
					System.out.println("Reverting to saved file.");
					new RevertToSavedJob(editor);
				}

			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Activator.SESSION.addResourceChangeListener();
		}

		status = new Status(Status.OK, Activator.PLUGIN_ID,
				"Resource content set successfully.");
		// } catch (CoreException e) {
		// e.printStackTrace();
		//
		// status = new Status(Status.ERROR, Activator.PLUGIN_ID,
		// "Resource content could not be set.");
		// // TODO revert, and re-apply all changes?
		// }

		return status;
	}
}
