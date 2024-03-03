package com.abhi.leximentor.inventory.log;

import com.abhi.leximentor.inventory.log.mapping.JobDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JobService {

    private final JobRepository repository;

    public long createJob() {
        return repository.createJob(JobDTO.builder()
                .refId(UUID.randomUUID().toString())
                .status("IN_PROGRESS")
                .crtnDate(LocalDateTime.now())
                .build());
    }

    public JobDTO getJob(long jobId) {
        return repository.getJob(jobId);
    }
}
