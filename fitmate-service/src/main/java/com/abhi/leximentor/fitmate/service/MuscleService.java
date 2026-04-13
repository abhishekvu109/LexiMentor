package com.abhi.leximentor.fitmate.service;

import com.abhi.leximentor.fitmate.dto.MuscleDTO;
import com.abhi.leximentor.fitmate.dto.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MuscleService {

    List<MuscleDTO> addAll(List<MuscleDTO> request);

    List<MuscleDTO> findAll();

    PagedResponse<MuscleDTO> findAll(Pageable pageable);

    void deleteAll();

    MuscleDTO findByRefId(long refId);

    MuscleDTO findByName(String name);

    void deleteByRefId(long refId);
}
