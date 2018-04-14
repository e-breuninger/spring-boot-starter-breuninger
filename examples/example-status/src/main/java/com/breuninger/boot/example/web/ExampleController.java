package com.breuninger.boot.example.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.codahale.metrics.annotation.Timed;

@RestController
public class ExampleController {

  @Timed
  @RequestMapping(value = "/", produces = "text/html", method = GET)
  public ModelAndView sayHelloAsHtml() {

    return new ModelAndView("example") {{
      addObject("hello", "Hello Microservice");
    }};
  }
}
