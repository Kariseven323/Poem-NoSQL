package com.sspu.nslike.service;

import com.sspu.nslike.entity.PoemLike;
import com.sspu.nslike.repository.AncientPoetryRepository;
import com.sspu.nslike.repository.PoemLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PoemService {
    @Autowired
    private AncientPoetryRepository ancientPoetryRepository;

    @Autowired
    private PoemLikeRepository poemLikeRepository;

    // 初始化时仅将 MySQL 中的 id 同步到 MongoDB 的 poem_likes 集合
    @Async
    public void initializeLikesAsync() {
        ancientPoetryRepository.findAll().forEach(poetry -> {
            if (!poemLikeRepository.existsById(poetry.getId())) {
                PoemLike newLike = new PoemLike();
                newLike.setId(poetry.getId());
                newLike.setPoemId(poetry.getId());
                poemLikeRepository.save(newLike);
            }
        });
        System.out.println("PoemLike 数据初始化完成！");
    }

    // 点赞/取消点赞
    public boolean toggleLike(String poemId, String userId) {
        Optional<PoemLike> optionalPoemLike = poemLikeRepository.findByPoemId(poemId);

        if (optionalPoemLike.isPresent()) {
            PoemLike poemLike = optionalPoemLike.get();
            if (poemLike.getUserIds().contains(userId)) {
                // 用户已点赞，取消点赞
                poemLike.getUserIds().remove(userId);
                poemLike.setLikeCount(poemLike.getLikeCount() - 1);
            } else {
                // 用户未点赞，添加点赞
                poemLike.getUserIds().add(userId);
                poemLike.setLikeCount(poemLike.getLikeCount() + 1);
            }
            poemLikeRepository.save(poemLike);
            return true;
        }

        return false; // 诗词不存在
    }
}
