package com.breuninger.boot.example.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import static com.breuninger.boot.example.togglz.Features.HELLO_TOGGLE;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class ExampleController {

  @RequestMapping(value = "/", produces = "text/html", method = GET)
  public ModelAndView sayHelloAsHtml() {
    return new ModelAndView("example") {{
      if (HELLO_TOGGLE.isActive()) {
        addObject("hello", "Hello active toggle");
      } else {
        addObject("hello", "Hello inactive toggle");
      }
    }};
  }
}
