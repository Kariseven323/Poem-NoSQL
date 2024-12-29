package com.sspu.nslike.repository;

import com.sspu.nslike.entity.AncientPoetry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AncientPoetryRepository extends JpaRepository<AncientPoetry, String> {
    List<AncientPoetry> findByType(String type);
}
