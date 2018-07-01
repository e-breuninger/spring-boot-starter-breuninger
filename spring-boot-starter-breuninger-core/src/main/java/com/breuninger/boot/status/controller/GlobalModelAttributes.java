package com.breuninger.boot.status.controller;

import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.breuninger.boot.configuration.BreuningerApplicationProperties;

@ControllerAdvice
public class GlobalModelAttributes {

  private final WebEndpointProperties webEndpointProperties;
  private final BreuningerApplicationProperties breuningerApplicationProperties;

  public GlobalModelAttributes(final WebEndpointProperties webEndpointProperties,
                               final BreuningerApplicationProperties breuningerApplicationProperties) {
    this.webEndpointProperties = webEndpointProperties;
    this.breuningerApplicationProperties = breuningerApplicationProperties;
  }

  @ModelAttribute
  public void addAttributes(final Model model) {
    model.addAttribute("webEndpointBasePath", webEndpointProperties.getBasePath());
    model.addAttribute("breuningerManagementBasePath", breuningerApplicationProperties.getManagement().getBasePath());
  }
}
