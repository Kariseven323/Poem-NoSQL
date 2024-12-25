<template>
  <div class="home">
    <div class="poems-container">
      <div v-for="poem in poems" :key="poem.id" class="poem-card">
        <comment-item
            :comment="poem.comment"
            :userId="userId"
            :poemTitle="poem.title"
            @update-comment="updateComment"
        >
          <template #poem-content>
            <div class="poem-content">
              <h2 class="poem-title">{{ poem.title }}</h2>
              <p class="poem-author">{{ poem.author }}</p>
              <p class="poem-text">{{ poem.content }}</p>
              <div class="poem-meta">
                <span class="visit-count">访问量: {{ poem.visitCount }}</span>
                <span class="like-count">点赞数: {{ poem.likeCount }}</span>
              </div>
            </div>
          </template>
        </comment-item>
      </div>
    </div>

    <div class="pagination">
      <el-pagination
          v-model:current-page="currentPage"
          :page-size="poemsPerPage"
          :total="totalPoems"
          layout="prev, pager, next"
          @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script>
import axios from "axios";
import CommentItem from "../components/CommentItem.vue";

export default {
  name: "Home",
  components: {
    CommentItem,
  },
  data() {
    return {
      poems: [], // 存储诗词数据
      totalPoems: 0, // 总诗词数量
      poemsPerPage: 10, // 每页诗词数量
      currentPage: 1, // 当前页码
      userId: "user-123", // 模拟当前登录用户ID
    };
  },
  methods: {
    // 加载诗词数据（调用 ns-rank 模块）
    async loadPoems() {
      try {
        const response = await axios.get("http://localhost:8083/rank/top");
        const topPoems = response.data;

        // 确保 topPoems 是数组
        if (Array.isArray(topPoems)) {
          this.poems = topPoems.map((poem) => ({
            ...poem,
            likeCount: 0,
            visitCount: 0,
            comment: { replies: [] }, // 初始化评论数据
          }));
        } else {
          console.error("返回的数据不是数组:", topPoems);
          this.poems = [];
        }
      } catch (error) {
        console.error("加载诗词数据失败:", error);
      }
    },

    // 加载单首诗词的点赞和评论数据（调用 ns-like 模块）
    async loadPoemData(poem) {
      try {
        // 获取点赞数和访问量
        const likeResponse = await axios.get(
            `http://localhost:8080/api/likes/poems/${poem.id}/count`
        );
        poem.likeCount = likeResponse.data.likeCount || 0;
        poem.visitCount = likeResponse.data.visitCount || 0;

        // 获取评论数据
        const commentResponse = await axios.get(
            `http://localhost:8080/api/comments/poems/${poem.id}`
        );
        poem.comment = commentResponse.data || { replies: [] };
      } catch (error) {
        console.error(`加载诗词 ${poem.id} 的数据失败:`, error);
      }
    },

    // 处理分页切换
    async handlePageChange(page) {
      this.currentPage = page;
      await this.loadPoems();
    },

    // 更新评论数据
    updateComment(updatedComment) {
      const index = this.poems.findIndex(
          (poem) => poem.comment.id === updatedComment.id
      );
      if (index !== -1) {
        this.poems[index].comment = updatedComment;
      }
    },
  },
  async mounted() {
    await this.loadPoems();
  },
};
</script>

<style scoped>
.home {
  padding: 20px;
}

.poems-container {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.poem-card {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  width: 300px;
}

.poem-title {
  font-size: 18px;
  font-weight: bold;
}

.poem-meta {
  margin-top: 10px;
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #888;
}
</style>
