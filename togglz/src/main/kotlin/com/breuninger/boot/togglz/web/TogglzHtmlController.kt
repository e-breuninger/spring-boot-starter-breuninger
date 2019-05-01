package com.breuninger.boot.togglz.web

import com.breuninger.boot.togglz.domain.TogglzFeature
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/togglz")
// TODO(BS): we need to use sth different, which is not directly connected to mongodb...
class TogglzHtmlController(private val mongoTemplate: MongoTemplate) {

  @GetMapping
  fun getTogglz(model: Model): String {
    model.addAttribute("togglz",  mongoTemplate.findAll(TogglzFeature::class.java))
    return "togglz"
  }
}
