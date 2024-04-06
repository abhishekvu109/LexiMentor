package com.abhi.synapster.inventory.service.impl;

import com.abhi.synapster.inventory.constants.Status;
import com.abhi.synapster.inventory.dto.DocumentDTO;
import com.abhi.synapster.inventory.entities.Content;
import com.abhi.synapster.inventory.entities.Document;
import com.abhi.synapster.inventory.exceptions.entities.ServerException;
import com.abhi.synapster.inventory.repository.ContentRepository;
import com.abhi.synapster.inventory.repository.DocumentRepository;
import com.abhi.synapster.inventory.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.print.Doc;
import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final ContentRepository contentRepository;

    @Override
    public DocumentDTO add(DocumentDTO documentDTO, long contentRefId) {
        Content content = contentRepository.findByRefId(contentRefId);
        Document document = ServiceUtil.DocumentUtil.buildEntity(documentDTO, content);
        document = documentRepository.save(document);
        return ServiceUtil.DocumentUtil.buildDTO(document);
    }

    @Override
    public List<DocumentDTO> addAll(List<DocumentDTO> documentDTOList, long contentRefId) {
        Content content = contentRepository.findByRefId(contentRefId);
        List<Document> documents = documentDTOList.stream().map(documentDTO -> ServiceUtil.DocumentUtil.buildEntity(documentDTO, content)).toList();
        return documentRepository.saveAll(documents).stream().map(ServiceUtil.DocumentUtil::buildDTO).toList();
    }

    @Override
    public DocumentDTO getByRefId(long refId) throws ServerException.EntityObjectNotFound {
        Document document = documentRepository.findByRefId(refId);
        if (document == null) throw new ServerException().new EntityObjectNotFound("Entity not found");
        return ServiceUtil.DocumentUtil.buildDTO(document);
    }

    @Override
    public DocumentDTO getByName(String name) throws ServerException.EntityObjectNotFound {
        Document document = documentRepository.findByName(name);
        if (document == null) throw new ServerException().new EntityObjectNotFound("Entity not found");
        return ServiceUtil.DocumentUtil.buildDTO(document);
    }

    @Override
    public DocumentDTO update(DocumentDTO documentDTO) throws ServerException.EntityObjectNotFound {
        Document document = documentRepository.findByRefId(Long.parseLong(documentDTO.getDocumentRefId()));
        if (document == null) throw new ServerException().new EntityObjectNotFound("Entity not found");
        document.setName(document.getName());
        document.setStatus(Status.ApplicationStatus.getStatus(documentDTO.getStatus()));
        document.setDocumentLocation(documentDTO.getDocumentLocation());
        document.setStorageType(documentDTO.getStorageType());
        document.setType(documentDTO.getType());
        document = documentRepository.save(document);
        return ServiceUtil.DocumentUtil.buildDTO(document);
    }

    @Override
    public void delete(DocumentDTO documentDTO) {
        Document document = documentRepository.findByRefId(Long.parseLong(documentDTO.getDocumentRefId()));
        if (document == null) throw new ServerException().new EntityObjectNotFound("Entity not found");
        documentRepository.delete(document);
    }

    @Override
    @Transactional
    public void deleteAll(List<DocumentDTO> documentDTOList) throws ServerException.EntityObjectNotFound {
        List<Document> documents = new LinkedList<>();
        for (DocumentDTO documentDTO : documentDTOList) {
            Document document = documentRepository.findByRefId(Long.parseLong(documentDTO.getDocumentRefId()));
            if (document == null) throw new ServerException().new EntityObjectNotFound("Entity not found");
            documents.add(document);
        }
        documentRepository.deleteAll(documents);
    }
}
