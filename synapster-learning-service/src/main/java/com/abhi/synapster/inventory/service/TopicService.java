package com.abhi.synapster.inventory.service;

import com.abhi.synapster.inventory.dto.TopicDTO;

import java.util.List;

public interface TopicService {
    public TopicDTO add(TopicDTO topicDTO, long subjectRefId);

    public List<TopicDTO> addAll(List<TopicDTO> topicDTOList, long subjectRefId);

    public TopicDTO update(TopicDTO topicDTO);

    public void delete(TopicDTO topicDTO);

    public void deleteAll(List<TopicDTO> topicDTOList);

    public TopicDTO getByRefId(long refId);

    public List<TopicDTO> getBySubjects(long subjectRefId);

    public TopicDTO getByName(String name);
}
