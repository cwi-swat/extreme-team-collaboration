package nl.jeldertpol.xtc.client.actions;

import nl.jeldertpol.xtc.client.preferences.XtcPreferencePage;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.ui.dialogs.PreferencesUtil;

/**
 * 
 */

/**
 * @author Jeldert Pol
 * 
 */
public class PreferencesAction extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		PreferenceDialog preferenceDialog = PreferencesUtil
				.createPreferenceDialogOn(null, XtcPreferencePage.class
						.getName(), null, null);
		preferenceDialog.open();

		return null;
	}

}
