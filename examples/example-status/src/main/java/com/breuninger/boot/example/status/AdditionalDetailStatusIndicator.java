package com.breuninger.boot.example.status;

import static com.breuninger.boot.status.domain.Status.OK;

import java.util.LinkedHashMap;

import org.springframework.stereotype.Component;

import com.breuninger.boot.status.domain.StatusDetail;
import com.breuninger.boot.status.indicator.StatusDetailIndicator;

@Component
public class AdditionalDetailStatusIndicator implements StatusDetailIndicator {

  @Override
  public StatusDetail statusDetail() {
    return StatusDetail.statusDetail("Status with additional details", OK, "Some message", new LinkedHashMap<>() {{
      put("first", "extra information");
      put("second", "more information");
      put("third", "even more information");
    }});
  }
}
