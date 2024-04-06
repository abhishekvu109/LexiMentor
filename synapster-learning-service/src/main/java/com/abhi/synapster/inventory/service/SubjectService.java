package com.abhi.synapster.inventory.service;

import com.abhi.synapster.inventory.dto.SubjectDTO;

import java.util.List;

public interface SubjectService {
    public SubjectDTO add(SubjectDTO subjectDTO);

    public List<SubjectDTO> addAll(List<SubjectDTO> subjectDTO);

    public SubjectDTO update(SubjectDTO subjectDTO);

    public void delete(SubjectDTO subjectDTO);

    public void deleteAll(List<SubjectDTO> subjectDTOS);

    public SubjectDTO getByRefId(long refId);
}
