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

    public String createJob() {
        return repository.createJob(JobDTO.builder()
                .refId(UUID.randomUUID().toString())
                .status(0)
                .crtnDate(LocalDateTime.now())
                .build());
    }

    public void updateStatus(long jobId, int status) {
        repository.updateJob(jobId, status);
    }

    public JobDTO getJob(long jobId) {
        return repository.getJob(jobId);
    }

    public JobDTO getJobByRefId(String refId) {
        return repository.getJobByRefId(refId);
    }

}
