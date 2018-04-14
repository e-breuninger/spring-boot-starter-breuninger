package com.breuninger.boot.jobs.eventbus;

import org.springframework.context.event.EventListener;

import com.breuninger.boot.jobs.eventbus.events.StateChangeEvent;

public interface JobStateChangeListener {

  @EventListener
  void consumeStateChange(StateChangeEvent stateChangeEvent);
}
