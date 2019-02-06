package com.breuninger.boot.example.app.products

import com.breuninger.boot.example.app.Feature
import com.breuninger.boot.validation.web.BindExceptionValidator
import io.micrometer.core.annotation.Timed
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/products")
class ProductsRestController(private val validator: BindExceptionValidator) {

  @Timed("rest.products.findAll")
  @GetMapping
  fun findAll() =
    if (Feature.REST_PRODUCTS_FINDALL.isActive())
      Flux.just(Product("Käse"), Product("Schinken"), Product("Brot"))
    else
      Flux.empty()

  @Timed("rest.products.save")
  @PostMapping
  fun save(@RequestBody body: Product): Mono<Product> {
    val product = body.slugifyAndSanatize()
    validator.validateAndThrowException(product)
    return Mono.just(product)
  }
}
