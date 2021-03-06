package nl.jeldertpol.xtc.client.preferences.connection;

import nl.jeldertpol.xtc.client.Activator;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class ConnectionPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	private final int portMin = 1;
	private final int portMax = 65535;

	public ConnectionPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Set the connection preferences of XTC.");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {
		// Host
		addField(new HostFieldEditor(PreferenceConstants.P_HOST,
				"XTC server address", getFieldEditorParent()));

		// Port
		IntegerFieldEditor portFieldEditor = new IntegerFieldEditor(
				PreferenceConstants.P_PORT, "XTC server port",
				getFieldEditorParent());
		portFieldEditor.setValidRange(portMin, portMax);
		addField(portFieldEditor);

		// Nickname
		StringFieldEditor nicknameFieldEditor = new StringFieldEditor(
				PreferenceConstants.P_NICKNAME, "Nickname",
				getFieldEditorParent());
		nicknameFieldEditor.setEmptyStringAllowed(false);
		addField(nicknameFieldEditor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(final IWorkbench workbench) {
		// Nothing to do
	}

}
