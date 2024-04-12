package com.abhi.leximentor.fitmate.service;

import com.abhi.leximentor.fitmate.dto.BodyPartsDTO;

import java.util.List;

public interface BodyPartService {
    List<BodyPartsDTO> addAll(List<BodyPartsDTO> bodyPartsDTOS);

    BodyPartsDTO getByRefId(long refId);

    List<BodyPartsDTO> getAllByRefId(List<Long> refIds);

    BodyPartsDTO getByName(String name);

    BodyPartsDTO update(BodyPartsDTO bodyPartsDTO);

    void delete(BodyPartsDTO bodyPartsDTO);

    void deleteAll(List<BodyPartsDTO> bodyPartsDTOS);
}
