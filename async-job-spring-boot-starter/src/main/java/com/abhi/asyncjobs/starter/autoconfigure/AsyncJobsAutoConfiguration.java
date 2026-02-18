package com.abhi.asyncjobs.starter.autoconfigure;

import com.abhi.asyncjobs.api.JobClient;
import com.abhi.asyncjobs.core.AsyncJobEngine;
import com.abhi.asyncjobs.core.PayloadCoercionStrategy;
import com.abhi.asyncjobs.core.StrictPayloadCoercionStrategy;
import com.abhi.asyncjobs.event.JobEventListener;
import com.abhi.asyncjobs.starter.api.AsyncJobController;
import com.abhi.asyncjobs.starter.jdbc.JdbcJobStore;
import com.abhi.asyncjobs.starter.registration.AsyncJobRegistration;
import com.abhi.asyncjobs.store.InMemoryJobStore;
import com.abhi.asyncjobs.store.JobStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.concurrent.Executors;

@AutoConfiguration
@EnableConfigurationProperties(AsyncJobsProperties.class)
public class AsyncJobsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public PayloadCoercionStrategy payloadCoercionStrategy(ObjectProvider<ObjectMapper> objectMapperProvider) {
        ObjectMapper objectMapper = objectMapperProvider.getIfAvailable();
        if (objectMapper != null) {
            return new JacksonPayloadCoercionStrategy(objectMapper);
        }
        return StrictPayloadCoercionStrategy.INSTANCE;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(JdbcTemplate.class)
    @ConditionalOnProperty(prefix = "async.jobs", name = "store-type", havingValue = "jdbc", matchIfMissing = true)
    public JobStore jdbcJobStore(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper, AsyncJobsProperties properties) {
        return new JdbcJobStore(jdbcTemplate, objectMapper, properties.getTableName());
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "async.jobs", name = "store-type", havingValue = "memory")
    public JobStore inMemoryJobStore() {
        return new InMemoryJobStore();
    }

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public AsyncJobEngine asyncJobEngine(
        JobStore jobStore,
        PayloadCoercionStrategy payloadCoercionStrategy,
        AsyncJobsProperties properties,
        ObjectProvider<JobEventListener> eventListeners
    ) {
        AsyncJobEngine.Builder builder = AsyncJobEngine.builder()
            .jobStore(jobStore)
            .payloadCoercionStrategy(payloadCoercionStrategy)
            .workerPool(Executors.newFixedThreadPool(properties.getWorkerThreads()))
            .scheduler(Executors.newScheduledThreadPool(properties.getSchedulerThreads()));

        List<JobEventListener> listeners = eventListeners.orderedStream().toList();
        for (JobEventListener listener : listeners) {
            builder.addListener(listener);
        }
        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean
    public JobClient asyncJobClient(AsyncJobEngine asyncJobEngine) {
        return asyncJobEngine;
    }

    @Bean
    @ConditionalOnClass(AsyncJobController.class)
    @ConditionalOnProperty(prefix = "async.jobs", name = "api-enabled", havingValue = "true", matchIfMissing = true)
    public AsyncJobController asyncJobController(JobClient jobClient) {
        return new AsyncJobController(jobClient);
    }

    @Bean
    public AsyncJobsRegistrar asyncJobsRegistrar(JobClient jobClient, ObjectProvider<AsyncJobRegistration> registrations) {
        return new AsyncJobsRegistrar(jobClient, registrations.orderedStream().toList());
    }
}
