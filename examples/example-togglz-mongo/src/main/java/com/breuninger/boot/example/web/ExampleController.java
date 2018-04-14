package com.breuninger.boot.example.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.breuninger.boot.example.togglz.Features;

@RestController
public class ExampleController {

  @RequestMapping(value = "/", produces = "text/html", method = GET)
  public ModelAndView sayHelloAsHtml() {
    return new ModelAndView("example") {{
      if (Features.TEST_TOGGLE.isActive()) {
        addObject("hello", "Hello active toggle");
      } else {
        addObject("hello", "Hello inactive toggle");
      }
    }};
  }
}
