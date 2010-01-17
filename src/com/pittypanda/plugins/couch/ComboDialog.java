package com.pittypanda.plugins.couch;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class ComboDialog extends Dialog {
  private Combo combo;
  
  private String   title;
  private String   result;
  private String[] items;
  
  private int      selected = -1;

  public ComboDialog(Shell shell, String title, String[] input) {
    super(shell); this.title = title; this.items = input;
  }

  public ComboDialog(Shell shell, String title, String[] input, int selected) {
    super(shell); this.title = title; this.items = input;
    this.selected = selected;
  }
  
  public String getResult() {
    return this.result;
  }
  
  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    newShell.setText(title);
  }
  
  protected Control createDialogArea(Composite parent) {
    Composite composite = (Composite) super.createDialogArea(parent);
    
    combo = new Combo(composite, SWT.READ_ONLY);
    combo.setItems(items);
    
    if (selected != -1) { combo.select(selected); }
    
    return composite;
  }
  
  protected void okPressed() {
    result = combo.getItem(combo.getSelectionIndex()).toString();
    this.close();
  }
}
