package nl.jeldertpol.xtc.client.preferences.logging;

import nl.jeldertpol.xtc.client.Activator;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Jeldert Pol
 * 
 */
public class LoggingPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public LoggingPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Set the logging preferences of XTC.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors
	 * ()
	 */
	@Override
	protected void createFieldEditors() {
		// Logging enabled
		addField(new BooleanFieldEditor(PreferenceConstants.P_LOGGING_ENABLED,
				"Enable logging", getFieldEditorParent()));

		// Logging format
		String[][] format = { { "XML", "XML" }, { "Plain", "PLAIN" } };

		addField(new RadioGroupFieldEditor(
				PreferenceConstants.P_LOGGING_FORMAT, "Format of log", 2,
				format, getFieldEditorParent(), true));

		// Log file location
		DirectoryFieldEditor directoryFieldEditor = new DirectoryFieldEditor(
				PreferenceConstants.P_LOGGING_LOCATION, "Log file location",
				getFieldEditorParent());

		addField(directoryFieldEditor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(final IWorkbench workbench) {
		// Nothing to do
	}

}
