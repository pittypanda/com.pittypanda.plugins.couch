package com.pittypanda.plugins.couch;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;

import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

import com.pittypanda.plugins.couch.glue.*;

public class CouchWizard extends Wizard implements INewWizard {
  WizardNewProjectCreationPage page;
  
  public void init(IWorkbench workbench, IStructuredSelection selection) {
    setNeedsProgressMonitor(true);
  }
  
  @Override
  public void addPages() {
    super.addPages();

    page = new WizardNewProjectCreationPage("CouchDB Wizard");
    page.setDescription("Enter an application name");
    page.setTitle("Create a CouchDB Application");
    addPage(page);
  }

  @Override
  public boolean performFinish() {
    try {
      WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
        protected void execute(IProgressMonitor monitor) {
          createProject(monitor != null ? monitor : new NullProgressMonitor());
        }
      };
      getContainer().run(false, true, op);
    } catch (InvocationTargetException e) {
      e.printStackTrace();
      return false;
    } catch (InterruptedException e) {
      e.printStackTrace();
      return false;
    }
    
    return true;
  }

  protected void createProject(IProgressMonitor monitor) {
    monitor.beginTask("Creating CouchApp", 100);
    
    String dirName = page.getLocationPath() + "/" + page.getProjectName();
    String fileName = dirName + "/couchapprc";
    
    try {
      IWorkspaceRoot      root        = ResourcesPlugin.getWorkspace().getRoot();
      IProject            project     = root.getProject(page.getProjectName());
      IProjectDescription description =
        ResourcesPlugin.getWorkspace().newProjectDescription(project.getName());
      
      if ( ! root.getLocation().equals(page.getLocationPath()) ) {
        description.setLocation(page.getLocationPath().append(project.getName()));
      }
      
      String[] natures = description.getNatureIds();
      String[] newNatures = new String[natures.length + 1];
      System.arraycopy(natures, 0, newNatures, 0, natures.length);
      newNatures[natures.length] = "com.pittypanda.plugins.couch.nature";
      description.setNatureIds(newNatures);
      
      monitor.subTask("Create project directory");
      project.create(description, monitor);

      monitor.worked(25);
      monitor.subTask("loading couchapp runtime");
      
      CouchApplicationType couchapp = CouchApplication.getInstance();

      monitor.worked(50);
      monitor.subTask("generating couchapp");
      
      String sep = System.getProperty("file.separator");
      
      String location = project.getLocation().toOSString() + sep + "app";
      String[] params = { "generate", "app", location };
      System.out.println(couchapp.dispatch(params));
            
      monitor.worked(75);
      monitor.subTask("configuration");
      
      try {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
        
        // -- TODO: we should do this in a nicer way, shouldn't we?
        writer.write("{");
        writer.newLine();
        writer.write("  \"env\": {");
        writer.newLine();
        writer.write("    \"default\": {");
        writer.newLine();
        writer.write("      \"db\": \"http://user:password@127.0.0.1:5984/development\"");
        writer.newLine();
        writer.write("    },");
        writer.newLine();
        writer.write("    \"staging\": {");
        writer.newLine();
        writer.write("      \"db\": \"http://user:password@127.0.0.1:5984/staging\"");
        writer.newLine();
        writer.write("    },");
        writer.newLine();
        writer.write("    \"production\": {");
        writer.newLine();
        writer.write("      \"db\": \"http://user:password@127.0.0.1:5984/production\"");
        writer.newLine();
        writer.write("    }");
        writer.newLine();
        writer.write("  }");
        writer.newLine();
        writer.write("}");
        writer.newLine();
        writer.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
      
      project.open(monitor);
    } catch (CoreException e) {
      System.out.println("CoreException");
      e.printStackTrace();
    } finally {
      monitor.done();
    }
  }
}
