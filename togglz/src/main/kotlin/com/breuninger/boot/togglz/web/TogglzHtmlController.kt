package com.breuninger.boot.togglz.web

import com.breuninger.boot.togglz.repository.TogglzStateRepository
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/togglz")
// TODO(BS): we need to use sth different, which is not directly connected to mongodb...
class TogglzHtmlController(private val togglzStateRepository: TogglzStateRepository) {

  @GetMapping
  fun getTogglz(model: Model): String {
    model.addAttribute("togglz",  togglzStateRepository.findAll())
    return "togglz"
  }
}
