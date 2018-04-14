package com.breuninger.boot.example.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.breuninger.boot.example.service.HelloService;

import io.micrometer.core.annotation.Timed;

@RestController
public class ExampleController {

  private final HelloService service;

  @Autowired
  public ExampleController(final HelloService service) {
    this.service = service;
  }

  @Timed("sayHelloAsHtml")
  @RequestMapping(value = "/", produces = "text/html", method = GET)
  public ModelAndView sayHelloAsHtml() throws InterruptedException {
    final var name = service.getName();
    return new ModelAndView("example") {{
      addObject("hello", "world");
    }};
  }

  @Timed("sayHelloAsHtmlWithRandomDelay")
  @RequestMapping(value = "/timer", produces = "text/html", method = GET)
  public ModelAndView sayHelloAsHtmlWithRandomDelay() throws InterruptedException {
    final var waitInterval = new Random().nextInt(250);
    Thread.sleep(waitInterval);
    return new ModelAndView("example") {{
      addObject("hello", "waited for " + waitInterval + " ms");
    }};
  }
}
