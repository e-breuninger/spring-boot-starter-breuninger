package com.breuninger.boot.example.app.products

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable
import reactor.core.publisher.Flux

@Controller
@RequestMapping("/products")
class ProductsHtmlController {

  @GetMapping
  fun findAll(model: Model): String {
    model.addAttribute("products", ReactiveDataDriverContextVariable(
      Flux.just(Product("Käse"), Product("Schinken"), Product("Brot")))
    )
    return "productOverviewPage"
  }
}
