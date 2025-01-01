package com.sspu.nssearch.repository.jpa;

import com.sspu.nssearch.entity.AncientPoetry;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PoetryJpaRepository extends JpaRepository<AncientPoetry, String> {
    List<AncientPoetry> findByTitleContaining(String keyword);
    // 按作者查询，支持分页
    List<AncientPoetry> findByWriterContaining(String keyword, Pageable pageable);

    // 按内容查询，支持分页
    List<AncientPoetry> findByContentContaining(String keyword, Pageable pageable);
}
