package com.breuninger.boot.example.app.products

import org.hibernate.validator.constraints.Length
import org.hibernate.validator.constraints.URL
import javax.validation.constraints.NotBlank

data class Product(

  @field:NotBlank
  @field:Length(min = 5, max = 10)
  @field:URL
  val name: String
)
