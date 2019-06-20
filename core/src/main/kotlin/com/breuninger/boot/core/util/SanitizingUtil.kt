package com.breuninger.boot.core.util

import org.owasp.html.HtmlPolicyBuilder
import org.springframework.util.StringUtils
import org.unbescape.html.HtmlEscape

object SanitizingUtil {

  private val DISALLOW_ANYTHING_POLICY = HtmlPolicyBuilder().toFactory()

  fun sanitize(text: String) =
    StringUtils.trimWhitespace(HtmlEscape.unescapeHtml(DISALLOW_ANYTHING_POLICY.sanitize(HtmlEscape.unescapeHtml(text))))
}
