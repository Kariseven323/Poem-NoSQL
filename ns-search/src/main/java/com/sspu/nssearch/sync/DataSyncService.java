package com.sspu.nssearch.sync;

import com.sspu.nssearch.entity.AncientPoetry;
import com.sspu.nssearch.repository.elasticsearch.AncientPoetrySearchRepository;
import com.sspu.nssearch.repository.jpa.PoetryJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Component
public class DataSyncService {

    @Autowired
    private PoetryJpaRepository poetryJpaRepository;

    @Autowired
    private AncientPoetrySearchRepository poetrySearchRepository;

    @PostConstruct
    public void syncDataToElasticsearch() {
        // 从 MySQL 加载数据
        List<AncientPoetry> poems = poetryJpaRepository.findAll();

        // 保存到 Elasticsearch
        poetrySearchRepository.saveAll(poems);

        System.out.println("数据已同步到 Elasticsearch 索引！");
    }
}
