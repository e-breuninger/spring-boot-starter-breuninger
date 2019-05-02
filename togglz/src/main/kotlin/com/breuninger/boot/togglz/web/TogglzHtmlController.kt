package com.breuninger.boot.togglz.web

import com.breuninger.boot.togglz.domain.TogglzFeature
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.togglz.core.manager.FeatureManager

@Controller
@RequestMapping("/togglz")
class TogglzHtmlController(private val featureManager: FeatureManager) {

  @GetMapping
  fun findAll(model: Model): String {
    model.addAttribute("togglz", featureManager.features.map { TogglzFeature(it, featureManager.getFeatureState(it)) })
    return "togglz"
  }
}
