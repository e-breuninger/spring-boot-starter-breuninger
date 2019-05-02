package com.breuninger.boot.togglz.repository

import com.breuninger.boot.togglz.domain.TogglzFeature
import org.togglz.core.repository.StateRepository

interface TogglzStateRepository: StateRepository {

  fun findAll():List<TogglzFeature>
}
