package com.abhi.leximentor.fitmate.service;

import com.abhi.leximentor.fitmate.dto.BodyPartsDTO;
import com.abhi.leximentor.fitmate.dto.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BodyPartService {
    List<BodyPartsDTO> addAll(List<BodyPartsDTO> bodyPartsDTOS);

    BodyPartsDTO getByRefId(long refId);

    List<BodyPartsDTO> getAllByRefId(List<Long> refIds);

    List<BodyPartsDTO> getAll();

    PagedResponse<BodyPartsDTO> getAll(Pageable pageable);

    BodyPartsDTO getByName(String name);

    BodyPartsDTO update(BodyPartsDTO bodyPartsDTO);

    void delete(BodyPartsDTO bodyPartsDTO);

    void deleteAll(List<BodyPartsDTO> bodyPartsDTOS);
}
