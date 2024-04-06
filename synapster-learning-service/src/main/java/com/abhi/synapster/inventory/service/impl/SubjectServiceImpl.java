package com.abhi.synapster.inventory.service.impl;

import com.abhi.synapster.inventory.constants.Status;
import com.abhi.synapster.inventory.dto.SubjectDTO;
import com.abhi.synapster.inventory.entities.Subject;
import com.abhi.synapster.inventory.exceptions.entities.ServerException;
import com.abhi.synapster.inventory.repository.SubjectRepository;
import com.abhi.synapster.inventory.service.SubjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SubjectServiceImpl implements SubjectService {
    private final SubjectRepository subjectRepository;

    @Override
    public SubjectDTO add(SubjectDTO subjectDTO) {
        Subject subject = ServiceUtil.SubjectUtil.buildEntity(subjectDTO);
        return ServiceUtil.SubjectUtil.buildDTO(subject);
    }

    @Override
    public List<SubjectDTO> addAll(List<SubjectDTO> subjectDTO) {
        List<Subject> subjects = subjectDTO.stream().map(ServiceUtil.SubjectUtil::buildEntity).toList();
        return subjects.stream().map(ServiceUtil.SubjectUtil::buildDTO).toList();
    }

    @Override
    public SubjectDTO update(SubjectDTO subjectDTO) throws ServerException.EntityObjectNotFound {
        Subject subject = subjectRepository.findByRefId(Long.parseLong(subjectDTO.getSubjectRefId()));
        if (subject == null) throw new ServerException().new EntityObjectNotFound("Entity not found");
        subject.setName(subjectDTO.getName());
        subject.setStatus(Status.ApplicationStatus.getStatus(subjectDTO.getStatus()));
        subject.setDescription(subject.getDescription());
        subject.setCategory(subject.getCategory());
        subject = subjectRepository.save(subject);
        return ServiceUtil.SubjectUtil.buildDTO(subject);
    }

    @Override
    public void delete(SubjectDTO subjectDTO) throws ServerException.EntityObjectNotFound {
        Subject subject = subjectRepository.findByRefId(Long.parseLong(subjectDTO.getSubjectRefId()));
        if (subject == null) throw new ServerException().new EntityObjectNotFound("Entity not found");
        subjectRepository.delete(subject);
    }

    @Override
    public void deleteAll(List<SubjectDTO> subjectDTOS) throws ServerException.EntityObjectNotFound {
        List<Subject> subjects = new LinkedList<>();
        for (SubjectDTO subjectDTO : subjectDTOS) {
            Subject subject = subjectRepository.findByRefId(Long.parseLong(subjectDTO.getSubjectRefId()));
            if (subject == null) throw new ServerException().new EntityObjectNotFound("Entity not found");
            subjects.add(subject);
        }
        subjectRepository.deleteAll(subjects);
    }

    @Override
    public SubjectDTO getByRefId(long refId) throws ServerException.EntityObjectNotFound {
        Subject subject = subjectRepository.findByRefId(refId);
        if (subject == null) throw new ServerException().new EntityObjectNotFound("Entity not found");
        return ServiceUtil.SubjectUtil.buildDTO(subject);
    }
}
