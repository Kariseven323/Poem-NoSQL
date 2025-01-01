package com.sspu.nsrank.repository;

import com.sspu.nsrank.entity.AncientPoetry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AncientPoetryRepository extends JpaRepository<AncientPoetry, String> {

    // 使用模糊匹配类型
    @Query("SELECT p FROM AncientPoetry p WHERE p.type LIKE %:type%")
    List<AncientPoetry> findByType(@Param("type") String type);
}
