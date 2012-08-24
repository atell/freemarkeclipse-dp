package org.jboss.ide.eclipse.freemarker.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class AtellHyperlink implements IHyperlink {
   private IRegion  region;
   private String   path;
   private IProject iproject;

   public AtellHyperlink(IRegion region, String path, IProject iproject) {
      this.region = region;
      this.path = path;
      this.iproject = iproject;
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
         IFile file = iproject.getFile(path);
         IDE.openEditor(page, file);
      } catch (Exception e) {
         //MessageDialog.openError(null, "Sorry...", e.getMessage());
      }

   }
}
