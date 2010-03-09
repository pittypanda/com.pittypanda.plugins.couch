package com.pittypanda.plugins.couch.glue;

public class CouchApplication {
  private static CouchApplicationType instance = null;
  
  public static CouchApplicationType getInstance() {
    if (instance == null) {
      JythonObjectFactory factory = new JythonObjectFactory(
          CouchApplicationType.class, "CouchApplication", "CouchApplication"
      );

      instance = (CouchApplicationType) factory.createObject();
    }
    
    return instance;
  }
}
