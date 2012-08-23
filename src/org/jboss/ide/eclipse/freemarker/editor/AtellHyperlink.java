package org.jboss.ide.eclipse.freemarker.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class AtellHyperlink implements IHyperlink {
	private IRegion region;
	private String path;

	public AtellHyperlink(IRegion region, String path, ITextViewer viewer) {
		this.region = region;
		this.path = path;
	}

	public IRegion getHyperlinkRegion() {
		return region;
	}

	public String getHyperlinkText() {
		return path;
	}

	public String getTypeLabel() {
		return path;
	}

	public void open() {
		try {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("search-deploy");
			IFile file = project.getFile(new Path(path));
			IDE.openEditor(page, file);
		} catch (Exception e) {
			//MessageDialog.openError(null, "Sorry...", e.getMessage());
		}  

	}
}
