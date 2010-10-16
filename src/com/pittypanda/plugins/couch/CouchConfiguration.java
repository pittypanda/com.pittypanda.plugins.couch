package com.pittypanda.plugins.couch;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

import com.pittypanda.plugins.couch.pages.Configuration;

public class CouchConfiguration extends FormEditor {
  Configuration overview = new Configuration(this);

  @Override
  protected void addPages() {
    try {
      addPage(overview);
    } catch (PartInitException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void doSave(IProgressMonitor monitor) {
    // TODO Auto-generated method stub
    overview.setModified(false);
    overview.doSave();
  }

  @Override
  public void doSaveAs() { }

  @Override
  public boolean isSaveAsAllowed() {
    return false;
  }
  
  public boolean isDirty() {
    return overview.isModified();
  }
}
