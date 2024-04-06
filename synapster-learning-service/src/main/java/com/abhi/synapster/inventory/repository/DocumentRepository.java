package com.abhi.synapster.inventory.repository;

import com.abhi.synapster.inventory.entities.Content;
import com.abhi.synapster.inventory.entities.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    public Document findByRefId(long refId);
    public Document findByName(String name);

    public Document findByUuid(String uuid);

    public List<Document> findByStorageType(String storageType);

    public List<Document> findByType(String type);

    public List<Document> findByContent(Content content);
}
