package com.abhi.asyncjobs.event;

@FunctionalInterface
public interface JobEventListener {
    void onEvent(JobEvent event);
}
