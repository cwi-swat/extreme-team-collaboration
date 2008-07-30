package nl.jeldertpol.xtc.client.preferences.connection;

import nl.jeldertpol.xtc.client.Activator;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.P_HOST, "localhost");
		store.setDefault(PreferenceConstants.P_PORT, 8998);
		store.setDefault(PreferenceConstants.P_NICKNAME, System.getProperty("user.name"));
	}

}
