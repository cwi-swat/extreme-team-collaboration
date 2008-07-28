package nl.jeldertpol.xtc.client;

import nl.jeldertpol.xtc.client.changes.ResourceChangeListener;
import nl.jeldertpol.xtc.client.changes.WindowListener;
import nl.jeldertpol.xtc.client.session.Session;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "nl.jeldertpol.xtc.client";

	// The shared instance
	private static Activator plugin;

	private ResourceChangeListener resourceChangeListener;

	private WindowListener windowListener;
	
	public static final Session session = new Session();

	/**
	 * The constructor.
	 */
	public Activator() {
		resourceChangeListener = new ResourceChangeListener();
		windowListener = new WindowListener();
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
		
		// Registers the resourceChangeListener to the workspace.
		ResourcesPlugin.getWorkspace()
				.addResourceChangeListener(resourceChangeListener);

		// Registers the windowListener to the workbench.
		IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.addWindowListener(windowListener);
		windowListener.updatePartListener();
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
