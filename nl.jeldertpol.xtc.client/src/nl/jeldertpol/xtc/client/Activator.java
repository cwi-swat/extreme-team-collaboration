package nl.jeldertpol.xtc.client;

import nl.jeldertpol.xtc.client.changes.editor.DocumentListener;
import nl.jeldertpol.xtc.client.changes.resource.ResourceChangeListener;
import nl.jeldertpol.xtc.client.session.Session;
import nl.jeldertpol.xtc.client.session.infoExtractor.InfoExtractor;
import nl.jeldertpol.xtc.client.session.infoExtractor.SubclipseInfoExtractor;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "nl.jeldertpol.xtc.client";

	public static final Session session = new Session();

	public static final DocumentListener documentListener = new DocumentListener();

	public static final ResourceChangeListener resourceChangeListener = new ResourceChangeListener();

	public static final InfoExtractor infoExtractor = new SubclipseInfoExtractor();

	/**
	 * Image for an session.
	 */
	public static final String IMAGE_SESSION = "resources/icons/group.png";

	/**
	 * Image for a client.
	 */
	public static final String IMAGE_CLIENT = "resources/icons/user.png";

	/**
	 * Image for a project.
	 */
	public static final String IMAGE_PROJECT = "resources/icons/folder.png";

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor.
	 */
	public Activator() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(
				resourceChangeListener);

		session.disconnect();

		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return the shared instance.
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path.
	 * 
	 * @param path
	 *            the path.
	 * @return the image descriptor.
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
