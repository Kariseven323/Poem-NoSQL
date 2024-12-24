package com.sspu.nsrank.service;

import com.sspu.nsrank.model.Sentence;
import com.sspu.nsrank.repository.SentenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SentenceService {
    @Autowired
    private SentenceRepository sentenceRepository;

    public List<Sentence> getRandomSentences() {
        return sentenceRepository.findRandomSentences();
    }
}
