package com.pittypanda.plugins.couch;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class Nature implements IProjectNature {
  private IProject project;

  public void configure() throws CoreException {
    // TODO Auto-generated method stub
  }

  public void deconfigure() throws CoreException {
    // TODO Auto-generated method stub
  }

  public IProject getProject() {
    // TODO Auto-generated method stub
    return project;
  }

  public void setProject(IProject value) {
    project = value;
  }

}
