package com.pittypanda.plugins.couch.pages;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Configuration extends FormPage {
  private FormToolkit  toolkit;
  private ScrolledForm form;
  
  private String development, staging, production;
  private String couchapprcpath;
  
  private JSONObject object, couch;
  
  private boolean modified = false;
  
  public Configuration(FormEditor editor) {
    super(editor, "overview", "Overview");
  }
  
  public boolean isModified() {
    return modified;
  }

//-- TODO: fixup save logic.
  public void setModified(boolean modified) {
    this.modified = modified;
    getEditor().editorDirtyStateChanged();
  }
  
  public boolean doSave() {
    try {
      ((JSONObject) couch.get("default")).put("db", development);
      ((JSONObject) couch.get("staging")).put("db", staging);
      ((JSONObject) couch.get("production")).put("db", production);

      FileWriter writer = new FileWriter(couchapprcpath);
      object.writeJSONString(writer);
      writer.flush();
      writer.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return true;
  }

  protected void createFormContent(IManagedForm managedForm) {
    toolkit = managedForm.getToolkit();
    form    = managedForm.getForm();
    
    form.setText("Overview");
  
    ColumnLayout layout = new ColumnLayout();
    layout.maxNumColumns = layout.minNumColumns = 2;
    
    form.getBody().setLayout(layout);
    
    Composite general = createSection(managedForm,
        "General Information", "CouchApp global configuration details.\n", 2
    );
    
    Composite server = createSection(managedForm,
        "CouchDB Servers", "CouchDB deployment servers.\n", 2
    );

    GridData gd  = new GridData(GridData.FILL_HORIZONTAL);

    ModifyListener listener = new ModifyListener() { 
      public void modifyText(ModifyEvent event) {
        if ( ((Text)event.widget).getToolTipText().equals("development") ) {
          development = ((Text)event.widget).getText();
        } else if ( ((Text)event.widget).getToolTipText().equals("staging") ) {
          staging = ((Text)event.widget).getText();
        } else if ( ((Text)event.widget).getToolTipText().equals("production") ) {
          production = ((Text)event.widget).getText();
        }
        
        if (! modified) { modified = true; getEditor().editorDirtyStateChanged(); }
      }
    };

    // -- TODO: check couchapp.rc file path detection logic.
    IResource resource = extractSelection(
      getEditor().getSite().getWorkbenchWindow().getSelectionService().getSelection()
    );

    IPath location;
    if (resource != null) {
      location = resource.getProject().getLocation();
      couchapprcpath = location.append("couchapp.rc").toOSString();
      getEditor().setPartProperty("couchapp.rc", couchapprcpath);
    } else {
      couchapprcpath = getEditor().getPartProperty("couchapp.rc");
    }

    System.out.println(couchapprcpath);

    JSONParser parser  = new JSONParser();
    
    try {
      FileReader reader = new FileReader(couchapprcpath);

      object = (JSONObject) parser.parse(reader);
      couch  = (JSONObject) object.get("env");

      toolkit.createLabel(general, "Name: ");
      Text text = toolkit.createText(general, "");
      text.addModifyListener(listener);
      text.setLayoutData(gd);

      toolkit.createLabel(general, "Description: ");
      text = toolkit.createText(general, "");
      text.addModifyListener(listener);
      text.setLayoutData(gd);

      toolkit.createLabel(server, "Development: ");
      development = (String)((JSONObject) couch.get("default")).get("db");
      text = toolkit.createText(server, development);
      text.setToolTipText("development");
      text.addModifyListener(listener);
      text.setLayoutData(gd);

      toolkit.createLabel(server, "Staging: ");
      staging = (String)((JSONObject) couch.get("staging")).get("db");
      text = toolkit.createText(server, staging);
      text.setToolTipText("staging");
      text.addModifyListener(listener);
      text.setLayoutData(gd);

      toolkit.createLabel(server, "Production: ");
      production = (String)((JSONObject) couch.get("production")).get("db");
      text = toolkit.createText(server, production);
      text.setToolTipText("production");
      text.addModifyListener(listener);
      text.setLayoutData(gd);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  private Composite createSection(IManagedForm mform, String title, String desc, int col) {
    toolkit = mform.getToolkit();
    form    = mform.getForm();
    
    Section section = toolkit.createSection(form.getBody(),
        Section.DESCRIPTION|Section.TITLE_BAR
    );
    
    section.setText(title);
    section.setDescription(desc);
    
    Composite client = toolkit.createComposite(section);
    
    GridLayout layout = new GridLayout();
    layout.marginWidth = layout.marginHeight = 0;
    layout.numColumns  = col;

    client.setLayout(layout);
    section.setClient(client);
    
    return client;
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
