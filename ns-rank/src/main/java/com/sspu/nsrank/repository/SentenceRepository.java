package com.sspu.nsrank.repository;

import com.sspu.nsrank.model.Sentence;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SentenceRepository extends CrudRepository<Sentence, Integer> {
    @Query(value = "SELECT * FROM sentences ORDER BY RAND() LIMIT 50", nativeQuery = true)
    List<Sentence> findRandomSentences();
}
