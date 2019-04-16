package com.breuninger.boot.togglz.web

import com.breuninger.boot.togglz.domain.TogglzFeature
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/togglz")
class TogglzHtmlController(private val mongoTemplate: MongoTemplate) { //umbauen hier auf StateRepository funktioniert nicht das ist keine eigene Klasse und die Klasse bietet keine Schnittstelle um alle Features abzufragen

  @GetMapping
  fun getTogglz(model: Model): String {
    model.addAttribute("togglzList",  mongoTemplate.findAll(TogglzFeature::class.java))
    return "togglzPage"
  }
}
