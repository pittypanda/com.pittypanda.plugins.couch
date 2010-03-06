package com.pittypanda.plugins.couch.glue;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import org.osgi.framework.Bundle;

import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

public class JythonObjectFactory {
  private static JythonObjectFactory instance = null;
  private static PyObject pyObject = null;

  protected JythonObjectFactory() { }

  public static JythonObjectFactory getInstance() {
    if (instance == null) { instance = new JythonObjectFactory(); }

    return instance;
  }

  public static Object createObject(Object interfaceType, String moduleName) {
    Object javaInt    = null;
    String pythonHome = "";
    
    Bundle bundle = Platform.getBundle("com.pittypanda.plugins.couch");
    URL url = FileLocator.find(bundle, new Path("/couchapp"), null);

    try { pythonHome = FileLocator.toFileURL(url).getPath(); } catch (IOException e) { }
    
    Properties props=new Properties();  
    props.setProperty("python.home", pythonHome);  
    PythonInterpreter.initialize(System.getProperties(), props, null);
    
    PythonInterpreter interpreter = new PythonInterpreter();
    interpreter.exec("from " + moduleName + " import " + moduleName);

    pyObject = interpreter.get(moduleName);

    try {
      PyObject newObj = pyObject.__call__();

      javaInt = newObj.__tojava__(Class.forName(interfaceType.toString()
          .substring(interfaceType.toString().indexOf(" ") + 1,
              interfaceType.toString().length())));
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(JythonObjectFactory.class.getName()).log(Level.SEVERE, null, ex);
    }

    return javaInt;
  }
}