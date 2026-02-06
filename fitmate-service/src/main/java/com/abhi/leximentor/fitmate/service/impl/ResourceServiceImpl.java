package com.abhi.leximentor.fitmate.service.impl;

import com.abhi.leximentor.fitmate.service.ResourceService;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ResourceServiceImpl implements ResourceService {

    @Value("${resource.path}")
    private String BASE_DIR;

    private final GridFsTemplate gridFsTemplate;

    private final GridFsOperations operations;


    @Override
    @Transactional
    public String save(MultipartFile file) throws IOException {
        return store(file, file.getOriginalFilename());
    }

    @Override
    public String save(MultipartFile file, String fileName) throws IOException {
        return store(file, fileName);
    }

    private String store(MultipartFile file, String fileName) throws IOException {
        ObjectId fileId = gridFsTemplate.store(
                file.getInputStream(),
                fileName,
                file.getContentType()
        );
        return fileId.toHexString();
    }

    @Override
    public Optional<GridFsResource> find(String id) {
        GridFSFile file = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(id)));
        return file != null ? Optional.of(operations.getResource(file)) : Optional.empty();
    }

    @Override
    @Transactional
    public void remove(String id) {
        gridFsTemplate.delete(Query.query(Criteria.where("_id").is(id)));
    }

    @Override
    public GridFSFile findGridFile(String id) {
        return gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(id)));
    }
}
