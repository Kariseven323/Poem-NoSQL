package com.sspu.nsrank.service;

import com.sspu.nsrank.repository.AncientPoetryRepository;
import com.sspu.nsrank.entity.RankedPoetry;
import com.sspu.nsrank.repository.RankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.ZSetOperations;
import com.sspu.nsrank.entity.*;
import com.sspu.nslike.entity.PoemLike;
import com.sspu.nsrank.repository.PoemLikeRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RankService {

    @Autowired
    private RankRepository rankRepository;

    @Autowired
    private AncientPoetryRepository ancientPoetryRepository;

    // 更新诗词的排名分数
    public void updatePoemScore(String poemId, double score) {
        rankRepository.updateRank(poemId, score);
    }

    // 获取排名前100的诗词
    public List<RankedPoetry> getTop100RankedPoems() {
        Set<ZSetOperations.TypedTuple<String>> topRanked = rankRepository.getTopRanked(100);

        if (topRanked.isEmpty()) {
            System.out.println("Redis 返回的排名数据为空");
            return Collections.emptyList();
        }

        return topRanked.stream().map(tuple -> {
            String poemId = tuple.getValue();
            double score = tuple.getScore();

            // 从数据库查询诗词信息
            AncientPoetry poetry = ancientPoetryRepository.findById(poemId).orElse(null);
            if (poetry == null) {
                System.out.println("未找到诗词 ID: " + poemId);
                return null;
            }

            RankedPoetry rankedPoetry = new RankedPoetry();
            rankedPoetry.setId(poetry.getId());
            rankedPoetry.setTitle(poetry.getTitle());
            rankedPoetry.setWriter(poetry.getWriter());
            rankedPoetry.setScore((int) score);
            return rankedPoetry;
        }).filter(rankedPoetry -> rankedPoetry != null).collect(Collectors.toList());
    }

    @Autowired
    private PoemLikeRepository poemLikeRepository; // MongoDB 中的点赞数据
    public void refreshPoemRankings() {
        System.out.println("开始刷新诗词排名...");

        int pageSize = 1000; // 每次读取 1000 条数据
        int page = 0; // 分页计数
        List<PoemLike> poemLikes;

        do {
            // 从 MongoDB 分页查询
            poemLikes = poemLikeRepository.findWithPagination(page, pageSize);

            // 将查询结果转换为 Redis 批量更新的数据
            Map<String, Double> scores = new HashMap<>();
            poemLikes.forEach(poemLike ->
                    scores.put(poemLike.getPoemId(), (double) poemLike.getLikeCount())
            );

            // 批量更新 Redis
            rankRepository.updateRankBatch(scores);

            page++; // 下一页
        } while (!poemLikes.isEmpty());

        System.out.println("诗词排名刷新完成！");
    }

    @Async
    public void refreshPoemRankingsAsync() {
        refreshPoemRankings();
    }

    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void triggerRefreshPoemRankings() {
        refreshPoemRankingsAsync(); // 异步调用
    }

}
