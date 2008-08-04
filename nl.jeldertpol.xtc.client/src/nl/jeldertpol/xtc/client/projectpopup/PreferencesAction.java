package nl.jeldertpol.xtc.client.projectpopup;

import nl.jeldertpol.xtc.client.preferences.XtcPreferencePage;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class PreferencesAction implements IObjectActionDelegate {

	public PreferencesAction() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run(IAction action) {
		PreferenceDialog preferenceDialog = PreferencesUtil
				.createPreferenceDialogOn(null, XtcPreferencePage.class
						.getName(), null, null);
		preferenceDialog.open();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

}
