package com.abhi.leximentor.fitmate.service;

import com.abhi.leximentor.fitmate.dto.MuscleDTO;

import java.util.List;

public interface MuscleService {

    List<MuscleDTO> addAll(List<MuscleDTO> request);

    List<MuscleDTO> findAll();

    void deleteAll();

    MuscleDTO findByRefId(long refId);

    MuscleDTO findByName(String name);

    void deleteByRefId(long refId);
}
