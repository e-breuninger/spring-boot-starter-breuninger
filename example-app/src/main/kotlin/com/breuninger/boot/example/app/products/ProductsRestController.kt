package com.breuninger.boot.example.app.products

import com.breuninger.boot.example.app.Feature
import io.micrometer.core.annotation.Timed
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/products")
class ProductsRestController {

  @Timed("rest.products.findAll")
  @GetMapping
  fun findAll() =
    if (Feature.REST_PRODUCTS_FINDALL.isActive())
      Flux.just("Käse", "Schinken", "Brot")
    else
      Flux.empty()
}
