package org.mongodb.meclipse.views.objects;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.mongodb.meclipse.views.MeclipseView;

/**
 * @author Flavio [FlaPer87] Percoco Premoli
 */
public class TreeObject implements IAdaptable {
	private String name;
	public MeclipseView view;
	private TreeParent parent;
	private IAction showPropertiesView;

	public TreeObject(String name) {
		this.name = name;
		makeActions();
	}
	
	private void makeActions() {
		showPropertiesView = new Action() {
			public void run() {
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.ui.views.PropertySheet");
				} catch (PartInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		showPropertiesView.setText("Properties");
	}

	public String getName() {
		return name;
	}

	public void setParent(TreeParent parent) {
		this.parent = parent;
	}

	public TreeParent getParent() {
		return parent;
	}

	public String toString() {
		return getName();
	}

	public Object getAdapter(Class key) {
		return null;
	}
	
	public void doubleClickAction() {
	}
	
	public void fillContextMenu(IMenuManager manager) {
		manager.add(showPropertiesView);
		manager.add(new Separator());
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	public void setViewer(MeclipseView view) {
		this.view = view;
	}
}