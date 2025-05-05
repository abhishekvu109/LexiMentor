package com.abhi.leximentor.inventory.repository;

import com.abhi.leximentor.inventory.constants.QueryConstants;
import com.abhi.leximentor.inventory.entities.NamedObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NamedObjectRepository extends JpaRepository<NamedObject, Long> {
    NamedObject findByRefId(long refId);

    NamedObject findByName(String name);

    List<NamedObject> findByGenre(String genre);

    List<NamedObject> findByAlias(String alias);

    List<NamedObject> findByStatus(int status);

    @Query(value = QueryConstants.NamedObject.GET_ACTIVE_NAMED_OBJECT, nativeQuery = true)
    NamedObject get();
}
