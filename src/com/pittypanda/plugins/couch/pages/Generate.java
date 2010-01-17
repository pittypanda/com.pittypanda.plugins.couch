package com.pittypanda.plugins.couch.pages;

import org.eclipse.jface.wizard.WizardPage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class Generate extends WizardPage {
  private Text      text;
  private Composite container;

  public Generate() {
    super("generate");
    setTitle("Create CouchApp");
    setDescription("Generate stubs for your CouchDB App.");
  }

  public void createControl(Composite parent) {
    GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    
    container = new Composite(parent, SWT.NULL);
    container.setLayout(layout);

    Label label = new Label(container, SWT.NULL);
    label.setText("CouchApp Name");
    
    text = new Text(container, SWT.BORDER | SWT.SINGLE);
    text.setText("");
    
    text.addKeyListener(new KeyListener() {
      public void keyPressed(KeyEvent e) { }
      
      public void keyReleased(KeyEvent e) {
        if (! "".equals(text.getText())) {
          setPageComplete(true);
        }
      }
    });
    
    setControl(container);
    setPageComplete(false);
  }

}
