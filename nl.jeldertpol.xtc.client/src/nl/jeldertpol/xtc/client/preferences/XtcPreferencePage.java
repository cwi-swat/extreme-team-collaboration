package nl.jeldertpol.xtc.client.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class XtcPreferencePage
	extends PreferencePage
	implements IWorkbenchPreferencePage {

	public XtcPreferencePage() {
		super();
		setDescription("Use these pages to set the preferences of XTC.");
	}

	@Override
	protected Control createContents(Composite parent) {
//		Label label = new Label(parent, SWT.HORIZONTAL);
//		label.setText("Use these pages to set the preferences of XTC.");
//		
//		return label;
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		noDefaultAndApplyButton();
	}

}
