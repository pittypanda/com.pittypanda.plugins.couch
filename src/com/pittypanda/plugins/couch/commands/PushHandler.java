package com.pittypanda.plugins.couch.commands;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.pittypanda.plugins.couch.ComboDialog;
import com.pittypanda.plugins.couch.utils.Executor;

public class PushHandler extends AbstractHandler {

  public Object execute(ExecutionEvent event) throws ExecutionException {
    Shell     shell    = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
    IResource resource = extractSelection(HandlerUtil.getCurrentSelection(event));
    IPath     location = resource.getProject().getLocation();

    ArrayList<String> choices = new ArrayList<String>();
    JSONParser        parser  = new JSONParser();
        
    int selection = -1;
    
    try {
      FileReader reader = new FileReader(location.append("couchapprc").toOSString());
      
      JSONObject object = (JSONObject) parser.parse(reader);
      JSONObject couch  = (JSONObject) object.get("env");
      
      int index = 0;
      for (Object key : couch.keySet()) {
        String choice = key + "\t" + ((JSONObject) couch.get(key)).get("db");
        choices.add(choice);
        if ("default".equals(key.toString())) { selection = index; }
        index++;
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    ComboDialog dialog = new ComboDialog(shell, "CouchDB Application Push",
                                         choices.toArray(new String[0]), selection);
    
    dialog.open();

    if ((dialog.getResult() != null) && (! dialog.equals(""))) {
      String target = (dialog.getResult().split("\t"))[1];
    
      String appfolder = resource.getProject().getLocation().append("app").toOSString();
      String command   = "couchapp push " + appfolder + " " + target;
      System.out.println(command);
    
      Executor.run(command);
    }

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
