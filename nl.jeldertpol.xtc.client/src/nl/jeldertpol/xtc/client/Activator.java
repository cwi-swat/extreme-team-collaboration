package nl.jeldertpol.xtc.client;

import java.util.logging.Level;

import nl.jeldertpol.xtc.client.changes.editor.DocumentListener;
import nl.jeldertpol.xtc.client.changes.editor.PartListener;
import nl.jeldertpol.xtc.client.changes.resource.ResourceChangeListener;
import nl.jeldertpol.xtc.client.preferences.logging.PreferenceConstants;
import nl.jeldertpol.xtc.client.session.Session;
import nl.jeldertpol.xtc.client.workspace.CommonActions;
import nl.jeldertpol.xtc.common.logging.FileLogger;
import nl.jeldertpol.xtc.common.logging.Logger;
import nl.jeldertpol.xtc.common.logging.NullLogger;
import nl.jeldertpol.xtc.common.logging.FileLogger.LogType;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "nl.jeldertpol.xtc.client";

	public static final String CHAT_VIEW_ID = "nl.jeldertpol.xtc.client.view.chat";

	private static Logger LOGGER = new NullLogger();

	public static final Session SESSION = new Session();

	public static final CommonActions COMMON_ACTIONS = new CommonActions();

	public static final PartListener partListener = new PartListener();

	public static final DocumentListener documentListener = new DocumentListener();

	public static final ResourceChangeListener resourceChangeListener = new ResourceChangeListener();

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		LOGGER = createLogger();

		getLogger().log(Level.FINEST, "XTC plug-in started.");
	}

	/**
	 * Create a logger, based on preferences.
	 * 
	 * @return A logger.
	 */
	private Logger createLogger() {
		Preferences preferences = Activator.getDefault().getPluginPreferences();
		boolean enabled = preferences
				.getBoolean(PreferenceConstants.P_LOGGING_ENABLED);
		String format = preferences
				.getString(PreferenceConstants.P_LOGGING_FORMAT);
		String location = preferences
				.getString(PreferenceConstants.P_LOGGING_LOCATION);

		Logger logger = new NullLogger();

		if (enabled) {
			if ("XML".equals(format)) {
				logger = new FileLogger(location, LogType.XML);
			} else if ("PLAIN".equals(format)) {
				logger = new FileLogger(location, LogType.PLAIN);
			}
		}

		return logger;
	}

	public static Logger getLogger() {
		return LOGGER;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(
				resourceChangeListener);

		SESSION.disconnect();

		getLogger().log(Level.FINEST, "XTC plug-in stopped.");

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
	public static ImageDescriptor getImageDescriptor(final String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

}
