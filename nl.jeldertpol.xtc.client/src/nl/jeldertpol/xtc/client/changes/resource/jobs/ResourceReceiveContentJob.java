package nl.jeldertpol.xtc.client.changes.resource.jobs;

import java.io.File;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.client.changes.editor.RevertToSavedJob;
import nl.jeldertpol.xtc.common.changes.ContentChange;
import nl.jeldertpol.xtc.common.conversion.Conversion;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
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

	final private ContentChange contentChange;

	/**
	 * Set the content of a file. Schedules itself to be run.
	 * 
	 * @param contentChange
	 *            Contains all information needed about the change.
	 */
	public ResourceReceiveContentJob(final ContentChange contentChange) {
		super(ResourceReceiveContentJob.class.getName() + ": "
				+ contentChange.getFilename());

		this.contentChange = contentChange;

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
		IStatus status;

		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				contentChange.getProjectName());
		IResource resource = project.findMember(contentChange.getFilename());
		IPath location = resource.getLocation();
		File file = location.toFile();

		byte[] content = contentChange.getContent();

		synchronized (Activator.resourceChangeListener) {
			Activator.SESSION.removeResourceChangeListener();

			Conversion.byteToFile(content, file);

			try {
				resource.refreshLocal(IResource.NONE, monitor);

				// Look if there is an open editor with this resource
				ITextEditor editor = Activator.documentReplacer
						.findEditor(resource);
				if (editor != null) {
					// Reload file from filesystem.
					new RevertToSavedJob(editor);
				}

				status = new Status(Status.OK, Activator.PLUGIN_ID,
						"Resource content set successfully.");
			} catch (CoreException e) {
				e.printStackTrace();

				status = new Status(Status.ERROR, Activator.PLUGIN_ID,
						"Resource content could not be set.");
				// TODO revert, and re-apply all changes?
			}
			Activator.SESSION.addResourceChangeListener();
		}

		return status;
	}
}
