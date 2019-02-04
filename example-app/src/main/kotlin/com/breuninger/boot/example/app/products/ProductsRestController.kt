package com.breuninger.boot.example.app.products

import com.breuninger.boot.example.app.Feature
import io.micrometer.core.annotation.Timed
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.validation.Valid

@RestController
@RequestMapping("/products")
class ProductsRestController {

  @Timed("rest.products.findAll")
  @GetMapping
  fun findAll() =
    if (Feature.REST_PRODUCTS_FINDALL.isActive())
      Flux.just(Product("Käse"), Product("Schinken"), Product("Brot"))
    else
      Flux.empty()

  @Timed("rest.products.save")
  @PostMapping
  fun save(@RequestBody @Valid product: Product) = Mono.just(product)
}
