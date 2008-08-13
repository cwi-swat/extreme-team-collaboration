package nl.jeldertpol.xtc.client.views;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ViewPart;

/**
 * 
 * @author Jeldert Pol
 */
public class EditorViewPart extends ViewPart {
	private TextEditor editor;

	/**
	 * 
	 */
	public EditorViewPart() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		editor = new TextEditor();
		// IWorkbenchPage page = this.getSite().getPage();
		// IEditorDescriptor descriptor = PlatformUI.getWorkbench()
		// .getEditorRegistry().getDefaultEditor("foo.txt");
		// try {
		// this.ad
		// page.openEditor(new FileEditorInput(null), descriptor.getId());
		// } catch (PartInitException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	public void setInput(IFile file) {
		editor.setInput(new FileEditorInput(file));
	}
	
	public TextEditor getEditor() {
		return editor;
	}

}
