package com.abhi.synapster.inventory.service.impl;

import com.abhi.synapster.inventory.constants.Status;
import com.abhi.synapster.inventory.dto.ContentDTO;
import com.abhi.synapster.inventory.dto.DocumentDTO;
import com.abhi.synapster.inventory.dto.SubjectDTO;
import com.abhi.synapster.inventory.dto.TopicDTO;
import com.abhi.synapster.inventory.entities.Content;
import com.abhi.synapster.inventory.entities.Document;
import com.abhi.synapster.inventory.entities.Subject;
import com.abhi.synapster.inventory.entities.Topic;
import com.abhi.synapster.inventory.util.KeyGeneratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ServiceUtil {

    public static class ContentUtil {
        public static Content buildEntity(ContentDTO contentDTO, Topic topic) {
            Content content = Content.builder().uuid(KeyGeneratorUtil.uuid()).refId(KeyGeneratorUtil.refId()).mainHeading(contentDTO.getMainHeading()).status(Status.ApplicationStatus.ACTIVE).topic(topic).content(contentDTO.getContent()).build();
            content.setDocumentList(CollectionUtils.isNotEmpty(contentDTO.getDocumentDTOList()) ? contentDTO.getDocumentDTOList().stream().map(doc -> DocumentUtil.buildEntity(doc, content)).toList() : null);
            return content;
        }

        public static ContentDTO buildDTO(Content content) {
            return ContentDTO.builder().contentRefId(String.valueOf(content.getRefId())).mainHeading(content.getMainHeading()).crtnDate(content.getCrtnDate()).lastUpdDate(content.getLastUpdDate()).status(Status.ApplicationStatus.toString(content.getStatus())).topicDTO(TopicUtil.buildDTO(content.getTopic())).content(content.getContent()).documentDTOList(CollectionUtils.isNotEmpty(content.getDocumentList()) ? content.getDocumentList().stream().map(DocumentUtil::buildDTO).toList() : null).build();
        }
    }

    public static class SubjectUtil {
        public static Subject buildEntity(SubjectDTO subjectDTO) {
            return Subject.builder().refId(KeyGeneratorUtil.refId()).uuid(KeyGeneratorUtil.uuid()).name(subjectDTO.getName()).status(Status.ApplicationStatus.ACTIVE).description(subjectDTO.getDescription()).category(subjectDTO.getCategory()).build();
        }

        public static SubjectDTO buildDTO(Subject subject) {
            return SubjectDTO.builder().subjectRefId(String.valueOf(subject.getRefId())).crtnDate(subject.getCrtnDate()).lastUpdDate(subject.getLastUpdDate()).name(subject.getName()).status(Status.ApplicationStatus.toString(subject.getStatus())).description(subject.getDescription()).category(subject.getCategory()).build();
        }
    }

    public static class DocumentUtil {
        public static Document buildEntity(DocumentDTO documentDTO, Content content) {
            return Document.builder().refId(KeyGeneratorUtil.refId()).uuid(KeyGeneratorUtil.uuid()).name(documentDTO.getName()).status(Status.ApplicationStatus.ACTIVE).documentLocation(documentDTO.getDocumentLocation()).storageType(documentDTO.getStorageType()).type(documentDTO.getType()).content(content).build();
        }

        public static DocumentDTO buildDTO(Document document) {
            return DocumentDTO.builder().documentRefId(String.valueOf(document.getRefId())).name(document.getName()).crtnDate(document.getCrtnDate()).lastUpdDate(document.getLastUpdDate()).status(Status.ApplicationStatus.toString(document.getStatus())).documentLocation(document.getDocumentLocation()).storageType(document.getStorageType()).type(document.getType()).contentDTO(ContentUtil.buildDTO(document.getContent())).build();
        }
    }

    public static class TopicUtil {
        public static Topic buildEntity(TopicDTO topicDTO, Subject subject) {

            return Topic.builder().refId(KeyGeneratorUtil.refId()).uuid(KeyGeneratorUtil.uuid()).name(topicDTO.getName()).status(Status.ApplicationStatus.ACTIVE).description(topicDTO.getDescription()).subject(subject).build();
        }

        public static TopicDTO buildDTO(Topic topic) {
            return TopicDTO.builder().topicRefId(String.valueOf(topic.getRefId())).crtnDate(topic.getCrtnDate()).lastUpdDate(topic.getLastUpdDate()).name(topic.getName()).status(Status.ApplicationStatus.toString(topic.getStatus())).description(topic.getDescription()).subjectDTO(SubjectUtil.buildDTO(topic.getSubject())).parentTopic(String.valueOf(topic.getParentTopic().getRefId())).build();
        }

    }
}

