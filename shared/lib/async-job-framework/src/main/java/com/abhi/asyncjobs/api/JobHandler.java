package com.abhi.asyncjobs.api;

@FunctionalInterface
public interface JobHandler<P, R> {
    R handle(JobExecutionContext<P> context) throws Exception;
}
