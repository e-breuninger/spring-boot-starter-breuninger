package com.breuninger.boot.testsupport.applicationdriver;

import org.springframework.context.ApplicationContext;

import com.breuninger.boot.testsupport.TestServer;

public class SpringTestBase {

  static {
    TestServer.main();
  }

  public static ApplicationContext applicationContext() {
    return TestServer.applicationContext();
  }
}
