package nl.jeldertpol.xtc.client.actions.projects;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.client.actions.AbstractLabelProvider;
import nl.jeldertpol.xtc.client.exceptions.RevisionExtractorException;
import nl.jeldertpol.xtc.client.exceptions.UnversionedProjectException;
import nl.jeldertpol.xtc.client.session.infoExtractor.InfoExtractor;
import nl.jeldertpol.xtc.client.session.infoExtractor.SubclipseInfoExtractor;
import nl.jeldertpol.xtc.common.session.SimpleSession;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

/**
 * {@link ILabelProvider} for {@link SimpleSession}. Shows an icon and a
 * description of the session and the clients.
 * 
 * @author Jeldert Pol
 */
public class ProjectLabelProvider extends AbstractLabelProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(final Object element) {
		Image image = null;

		if (element instanceof IProject) {
			Device device = Display.getCurrent();
			ImageData session = Activator.getImageDescriptor(
					Activator.IMAGE_PROJECT).getImageData();
			image = new Image(device, session);
		}

		return image;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(final Object element) {
		String text = null;

		if (element instanceof IProject) {
			IProject project = (IProject) element;
			text = project.getName();

			InfoExtractor infoExtractor = new SubclipseInfoExtractor();

			try {
				Long revision = infoExtractor.getRevision(project);
				text += " (revision " + revision + ")";
			} catch (UnversionedProjectException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RevisionExtractorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return text;
	}

}
