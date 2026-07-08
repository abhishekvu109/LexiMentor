package com.abhi.leximentor.fitmate.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface ResourceService {
    String save(MultipartFile file) throws IOException;
    String save(MultipartFile file,String fileName) throws IOException;

    Optional<GridFsResource> find(String id);

    GridFSFile findGridFile(String id);

    void remove(String id);
}
