package com.abhi.synapster.inventory.service.impl;

import com.abhi.synapster.inventory.constants.Status;
import com.abhi.synapster.inventory.dto.TopicDTO;
import com.abhi.synapster.inventory.entities.Subject;
import com.abhi.synapster.inventory.entities.Topic;
import com.abhi.synapster.inventory.exceptions.entities.ServerException;
import com.abhi.synapster.inventory.repository.SubjectRepository;
import com.abhi.synapster.inventory.repository.TopicRepository;
import com.abhi.synapster.inventory.service.TopicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.select.Top;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TopicServiceImpl implements TopicService {
    private final TopicRepository topicRepository;
    private final SubjectRepository subjectRepository;

    @Override
    public TopicDTO add(TopicDTO topicDTO, long subjectRefId) throws ServerException.EntityObjectNotFound {
        Subject subject = subjectRepository.findByRefId(subjectRefId);
        if (subject == null) throw new ServerException().new EntityObjectNotFound("Entity not found");
        Topic topic = ServiceUtil.TopicUtil.buildEntity(topicDTO, subject);
        topic = topicRepository.save(topic);
        return ServiceUtil.TopicUtil.buildDTO(topic);
    }

    @Override
    public List<TopicDTO> addAll(List<TopicDTO> topicDTOList, long subjectRefId) throws ServerException.EntityObjectNotFound {
        Subject subject = subjectRepository.findByRefId(subjectRefId);
        if (subject == null) throw new ServerException().new EntityObjectNotFound("Entity not found");
        List<Topic> topics = topicDTOList.stream().map(topicDTO -> ServiceUtil.TopicUtil.buildEntity(topicDTO, subject)).toList();
        return topics.stream().map(ServiceUtil.TopicUtil::buildDTO).toList();
    }

    @Override
    public TopicDTO update(TopicDTO topicDTO) throws ServerException.EntityObjectNotFound {
        Topic topic = topicRepository.findByRefId(Long.parseLong(topicDTO.getTopicRefId()));
        if (topic == null) throw new ServerException().new EntityObjectNotFound("Entity not found");
        topic.setName(topic.getName());
        topic.setStatus(Status.ApplicationStatus.getStatus(topicDTO.getStatus()));
        topic.setDescription(topic.getDescription());
        topic = topicRepository.save(topic);
        return ServiceUtil.TopicUtil.buildDTO(topic);
    }

    @Override
    public void delete(TopicDTO topicDTO) throws ServerException.EntityObjectNotFound {
        Topic topic = topicRepository.findByRefId(Long.parseLong(topicDTO.getTopicRefId()));
        if (topic == null) throw new ServerException().new EntityObjectNotFound("Entity not found");
        topicRepository.delete(topic);
    }

    @Override
    public void deleteAll(List<TopicDTO> topicDTOList) throws ServerException.EntityObjectNotFound {
        List<Topic> topics = new LinkedList<>();
        for (TopicDTO topicDTO : topicDTOList) {
            Topic topic = topicRepository.findByRefId(Long.parseLong(topicDTO.getTopicRefId()));
            if (topic == null) throw new ServerException().new EntityObjectNotFound("Entity not found");
            topics.add(topic);
        }
        topicRepository.deleteAll(topics);
    }

    @Override
    public TopicDTO getByRefId(long refId) throws ServerException.EntityObjectNotFound {
        Topic topic = topicRepository.findByRefId(refId);
        if (topic == null) throw new ServerException().new EntityObjectNotFound("Entity not found");
        return ServiceUtil.TopicUtil.buildDTO(topic);
    }

    @Override
    public List<TopicDTO> getBySubjects(long subjectRefId) throws ServerException.EntityObjectNotFound {
        Subject subject = subjectRepository.findByRefId(subjectRefId);
        if (subject == null) throw new ServerException().new EntityObjectNotFound("Entity not found");
        List<Topic> topics = topicRepository.findBySubject(subject);
        return topics.stream().map(ServiceUtil.TopicUtil::buildDTO).toList();
    }

    @Override
    public TopicDTO getByName(String name) throws ServerException.EntityObjectNotFound {
        Topic topic = topicRepository.findByName(name);
        if (topic == null) throw new ServerException().new EntityObjectNotFound("Entity not found");
        return ServiceUtil.TopicUtil.buildDTO(topic);
    }
}
