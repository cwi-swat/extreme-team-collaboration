package nl.jeldertpol.xtc.client.actions.sessions;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.client.actions.AbstractLabelProvider;
import nl.jeldertpol.xtc.common.session.SimpleSession;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TreeNode;
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
public class SessionLabelProvider extends AbstractLabelProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(final Object element) {
		Image image = null;

		if (element instanceof TreeNode) {
			TreeNode treeNode = (TreeNode) element;
			Object value = treeNode.getValue();

			Device device = Display.getCurrent();
			if (value instanceof SimpleSession) {
				ImageData session = Activator.getImageDescriptor(
						Activator.IMAGE_SESSION).getImageData();
				image = new Image(device, session);
			} else if (value instanceof String) {
				ImageData client = Activator.getImageDescriptor(
						Activator.IMAGE_CLIENT).getImageData();
				image = new Image(device, client);
			}
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
		if (element instanceof TreeNode) {
			TreeNode treeNode = (TreeNode) element;
			Object value = treeNode.getValue();

			if (value instanceof SimpleSession) {
				SimpleSession simpleSession = (SimpleSession) value;
				text = simpleSession.getProjectName() + " (revision "
						+ simpleSession.getRevision() + ")";
			} else if (value instanceof String) {
				text = (String) value;
			}
		}
		return text;
	}

}
