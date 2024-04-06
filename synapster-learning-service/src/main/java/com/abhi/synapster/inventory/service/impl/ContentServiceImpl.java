package com.abhi.synapster.inventory.service.impl;

import com.abhi.synapster.inventory.constants.Status;
import com.abhi.synapster.inventory.dto.ContentDTO;
import com.abhi.synapster.inventory.entities.Content;
import com.abhi.synapster.inventory.entities.Topic;
import com.abhi.synapster.inventory.exceptions.entities.ServerException;
import com.abhi.synapster.inventory.repository.ContentRepository;
import com.abhi.synapster.inventory.repository.TopicRepository;
import com.abhi.synapster.inventory.service.ContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ContentServiceImpl implements ContentService {
    private final ContentRepository contentRepository;
    private final TopicRepository topicRepository;

    @Override
    public ContentDTO add(ContentDTO contentDTO, long topicRefId) throws ServerException.EntityObjectNotFound {
        Topic topic = topicRepository.findByRefId(topicRefId);
        if (topic == null) throw new ServerException().new EntityObjectNotFound("Entity not found");
        Content content = ServiceUtil.ContentUtil.buildEntity(contentDTO, topic);
        content = contentRepository.save(content);
        return ServiceUtil.ContentUtil.buildDTO(content);
    }

    @Override
    public List<ContentDTO> addAll(List<ContentDTO> contentDTOList, long topicRefId) throws ServerException.EntityObjectNotFound {
        Topic topic = topicRepository.findByRefId(topicRefId);
        if (topic == null) throw new ServerException().new EntityObjectNotFound("Entity not found");
        List<Content> contentList = contentDTOList.stream().map(contentDTO -> ServiceUtil.ContentUtil.buildEntity(contentDTO, topic)).toList();
        return contentRepository.saveAll(contentList).stream().map(ServiceUtil.ContentUtil::buildDTO).toList();
    }

    @Override
    public ContentDTO update(ContentDTO contentDTO) throws ServerException.EntityObjectNotFound {
        Content content = contentRepository.findByRefId(Long.parseLong(contentDTO.getContentRefId()));
        if (content == null) throw new ServerException().new EntityObjectNotFound("Entity not found");
        content.setMainHeading(contentDTO.getMainHeading());
        content.setStatus(Status.ApplicationStatus.getStatus(contentDTO.getStatus()));
        content.setContent(content.getContent());
        content = contentRepository.save(content);
        return ServiceUtil.ContentUtil.buildDTO(content);
    }

    @Override
    public void delete(ContentDTO contentDTO) throws ServerException.EntityObjectNotFound {
        Content content = contentRepository.findByRefId(Long.parseLong(contentDTO.getContentRefId()));
        if (content == null) throw new ServerException().new EntityObjectNotFound("Entity not found");
        contentRepository.delete(content);
    }

    @Override
    public void deleteAll(List<ContentDTO> contentDTOList) throws ServerException.EntityObjectNotFound {
        List<Content> contentList = new LinkedList<>();
        for (ContentDTO contentDTO : contentDTOList) {
            Content content = contentRepository.findByRefId(Long.parseLong(contentDTO.getContentRefId()));
            if (content == null) throw new ServerException().new EntityObjectNotFound("Entity not found");
            contentList.add(content);
        }
        contentRepository.deleteAll(contentList);
    }

    @Override
    public ContentDTO getByRefId(long refId) throws ServerException.EntityObjectNotFound {
        Content content = contentRepository.findByRefId(refId);
        if (content == null) throw new ServerException().new EntityObjectNotFound("Entity not found");
        return ServiceUtil.ContentUtil.buildDTO(content);
    }

    @Override
    public List<ContentDTO> getByTopic(long topicRefId) throws ServerException.EntityObjectNotFound {
        Topic topic = topicRepository.findByRefId(topicRefId);
        if (topic == null) throw new ServerException().new EntityObjectNotFound("Entity not found");
        List<Content> contents = contentRepository.findByTopic(topic);
        return contents.stream().map(ServiceUtil.ContentUtil::buildDTO).toList();
    }
}
