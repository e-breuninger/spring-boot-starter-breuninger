package com.breuninger.boot.status.domain;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

import java.util.function.Supplier;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import com.breuninger.boot.annotations.Beta;

@Beta
@JsonInclude(NON_EMPTY)
public class ClusterInfo {

  private final Supplier<String> color;
  private final Supplier<String> colorState;

  public ClusterInfo(final String color, final String colorState) {
    this.color = () -> color;
    this.colorState = () -> colorState;
  }

  public ClusterInfo(final Supplier<String> colorSupplier, final Supplier<String> colorStateSupplier) {
    color = colorSupplier;
    colorState = colorStateSupplier;
  }

  public static ClusterInfo clusterInfo(final String color, final String colorState) {
    return new ClusterInfo(() -> color, () -> colorState);
  }

  public static ClusterInfo clusterInfo(final Supplier<String> colorSupplier, final Supplier<String> colorStateSupplier) {
    return new ClusterInfo(colorSupplier, colorStateSupplier);
  }

  public String getColor() {
    return color.get();
  }

  public String getColorState() {
    return colorState.get();
  }

  @JsonIgnore
  public boolean isEnabled() {
    return !getColor().isEmpty() || !getColorState().isEmpty();
  }
}
