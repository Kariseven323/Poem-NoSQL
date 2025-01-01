package com.sspu.nssearch.controller;

import com.sspu.nslike.entity.AncientPoetry;
import com.sspu.nssearch.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    // 搜索接口
    @GetMapping("/find")
    public List<AncientPoetry> search(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return searchService.search(keyword, page, size);
    }
}
