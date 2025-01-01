package com.sspu.nssearch.repository.elasticsearch;

import com.sspu.nssearch.entity.AncientPoetry;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AncientPoetrySearchRepository extends ElasticsearchRepository<AncientPoetry, String> {
    List<AncientPoetry> findByTitleContaining(String keyword);
    List<AncientPoetry> findByWriterContaining(String keyword);
    List<AncientPoetry> findByContentContaining(String keyword);
}
