package com.abhi.synapster.inventory.service;

import com.abhi.synapster.inventory.dto.ContentDTO;

import java.util.List;

public interface ContentService {
    public ContentDTO add(ContentDTO contentDTO,long topicRefId);
    public List<ContentDTO> addAll(List<ContentDTO> contentDTOList,long topicRefId);

    public ContentDTO update(ContentDTO contentDTO);

    public void delete(ContentDTO contentDTO);

    public void deleteAll(List<ContentDTO> contentDTOList);

    public ContentDTO getByRefId(long refId);

    public List<ContentDTO> getByTopic(long topicRefId);

}
