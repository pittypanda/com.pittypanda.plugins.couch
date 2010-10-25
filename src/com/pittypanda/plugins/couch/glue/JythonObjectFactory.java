package com.pittypanda.plugins.couch.glue;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import org.osgi.framework.Bundle;

import org.python.core.Py;
import org.python.core.PyObject;
import org.python.core.PySystemState;

public class JythonObjectFactory {

  private final Class    interfaceType;
  private final PyObject klass;

  public JythonObjectFactory(Class interfaceType, String moduleName, String className) {
    String pythonHome = "";
    
    Bundle bundle = Platform.getBundle("com.pittypanda.plugins.couch");
    URL url = FileLocator.find(bundle, new Path("/couchapp"), null);

    try { pythonHome = FileLocator.toFileURL(url).getPath(); } catch (IOException e) { }
    
    Properties props=new Properties();  
    props.setProperty("python.home", pythonHome);
    props.setProperty("python.console.encoding", "UTF8");
    props.setProperty("file.encoding", "UTF8");
    PySystemState.initialize(System.getProperties(), props, null);
    
    PySystemState state = new PySystemState();
    
    // -- init jython.
    this.interfaceType = interfaceType;
    PyObject importer  = state.getBuiltins().__getitem__(Py.newString("__import__"));
    PyObject module    = importer.__call__(Py.newString(moduleName));
    
    klass = module.__getattr__(className);
    System.err.println("module=" + module + ",class=" + klass);
  }

  public Object createObject() {
    return klass.__call__().__tojava__(interfaceType);
  }

  public Object createObject(Object arg1) {
    return klass.__call__(Py.java2py(arg1)).__tojava__(interfaceType);
  }

  public Object createObject(Object arg1, Object arg2) {
    return klass.__call__(Py.java2py(arg1), Py.java2py(arg2)).__tojava__(
        interfaceType);
  }

  public Object createObject(Object arg1, Object arg2, Object arg3) {
    return klass.__call__(Py.java2py(arg1), Py.java2py(arg2), Py.java2py(arg3))
        .__tojava__(interfaceType);
  }

  public Object createObject(Object args[], String keywords[]) {
    PyObject convertedArgs[] = new PyObject[args.length];
    for (int i = 0; i < args.length; i++) {
      convertedArgs[i] = Py.java2py(args[i]);
    }

    return klass.__call__(convertedArgs, keywords).__tojava__(interfaceType);
  }

  public Object createObject(Object... args) {
    return createObject(args, Py.NoKeywords);
  }
}