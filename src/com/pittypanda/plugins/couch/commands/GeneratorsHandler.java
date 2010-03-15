package com.pittypanda.plugins.couch.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import com.pittypanda.plugins.couch.glue.CouchApplication;
import com.pittypanda.plugins.couch.glue.CouchApplicationType;

public class GeneratorsHandler extends AbstractHandler {

  public Object execute(ExecutionEvent event) throws ExecutionException {
    Shell     shell    = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
    IResource resource = extractSelection(HandlerUtil.getCurrentSelection(event));
    IPath     location = resource.getProject().getLocation();

    String generator = event.getParameter("com.pittypanda.plugins.couch.commands.parameter");
    
    InputDialog dialog = new InputDialog(
        shell, "Generate " + generator, generator + " name", location.toOSString(), null
    );
    
    dialog.open();
    
    if ((dialog.getValue() != null) && (! dialog.getValue().equals(""))) {
      String target = dialog.getValue();
    
      String appfolder = resource.getProject().getLocation().append("app").toOSString();
    
      CouchApplicationType couchapp = CouchApplication.getInstance();
      
      String[] params = { "generate", generator, appfolder, target };
      System.out.println(couchapp.dispatch(params));
    }
    System.out.println(dialog.getValue());
    
    return null;
  }

  private IResource extractSelection(ISelection selection) {
    if (!(selection instanceof IStructuredSelection)) return null;
    
    IStructuredSelection structure = (IStructuredSelection) selection;
    Object element = structure.getFirstElement();
    
    if (element instanceof IResource) return (IResource) element;
    if (!(element instanceof IAdaptable)) return null;
    
    IAdaptable adaptable = (IAdaptable) element;
    Object adapter = adaptable.getAdapter(IResource.class);
    
    return (IResource) adapter;
  }
}
