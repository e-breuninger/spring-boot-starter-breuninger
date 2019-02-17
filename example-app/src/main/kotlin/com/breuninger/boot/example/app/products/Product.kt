package com.breuninger.boot.example.app.products

import com.breuninger.boot.core.util.SanitizingUtil
import com.breuninger.boot.core.util.SlugificationUtil
import org.hibernate.validator.constraints.Length
import javax.validation.constraints.NotBlank

data class Product(

  @field:NotBlank
  @field:Length(min = 5, max = 10)
  val name: String
) {

  fun slugifyAndSanatize() = Product(SlugificationUtil.slugify(SanitizingUtil.sanitize(name)))
}
