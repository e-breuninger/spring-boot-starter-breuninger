package com.breuninger.boot.jobs.eventbus;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.event.EventListener;

import com.breuninger.boot.jobs.eventbus.events.StateChangeEvent;

public class InMemoryEventRubbishBin {

  private final List<String> stateChangedEvents = new ArrayList<>();

  @EventListener
  public void consumeStateChangedEvent(final StateChangeEvent stateChangeEvent) {
    stateChangedEvents.add(stateChangeEvent.getJobId());
  }

  public List<String> getStateChangedEvents() {
    return stateChangedEvents;
  }

  public void clear() {
    stateChangedEvents.clear();
  }
}
