package nl.jeldertpol.xtc.client.preferences.logging;

import nl.jeldertpol.xtc.client.Activator;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Class used to initialize default preference values.
 * 
 * @author Jeldert Pol
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.P_LOGGING_ENABLED, true);
		store.setDefault(PreferenceConstants.P_LOGGING_FORMAT, "xml");
		store.setDefault(PreferenceConstants.P_LOGGING_LOCATION, System
				.getProperty("user.home"));
	}

}
