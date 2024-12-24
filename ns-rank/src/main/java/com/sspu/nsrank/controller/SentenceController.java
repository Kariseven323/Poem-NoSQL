package com.sspu.nsrank.controller;

import com.sspu.nsrank.model.Sentence;
import com.sspu.nsrank.service.SentenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sentences")
public class SentenceController {
    @Autowired
    private SentenceService sentenceService;

    @GetMapping("/random")
    public List<Sentence> getRandomSentences() {
        return sentenceService.getRandomSentences();
    }
}
