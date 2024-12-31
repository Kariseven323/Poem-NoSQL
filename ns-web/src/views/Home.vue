<template>
  <div class="home">
    <div class="poem-container">
      <div v-if="loading" class="loading">
        加载中...
      </div>
      <div v-else-if="error" class="error">
        {{ error }}
      </div>
      <div v-else class="poems-grid">
        <div v-for="poem in currentPagePoems" :key="poem.id" class="poem-card">
          <div class="poem-header">
            <h2 class="title">{{ poem.title }}</h2>
            <p class="dynasty">[{{ poem.dynasty }}]</p>
            <p class="author">{{ poem.writer }}</p>
          </div>
          <div class="content-vertical">
            <p v-for="(line, index) in getPoemLines(poem.content)" :key="index" class="line">{{ line }}</p>
          </div>
          <div class="poem-footer">
            <div class="like-button" 
                 :class="{ 'liked': isLiked(poem.id) }"
                 @click="handleLike(poem)">
              <i class="el-icon-star-on"></i>
              点赞 {{ poem.likeCount || 0 }}
            </div>
            <div class="comment-button" @click="openCommentDialog(poem)">
              <i class="el-icon-chat-dot-round"></i>
              评论
            </div>
          </div>
        </div>
        
        <div class="pagination">
          <button @click="previousPage" :disabled="currentPage <= 1" class="nav-button">
            上一页
          </button>
          <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
          <button @click="nextPage" :disabled="currentPage >= totalPages" class="nav-button">
            下一页
          </button>
        </div>
      </div>
    </div>

    <!-- 评论对话框 -->
    <el-dialog
      v-model="showCommentDialog"
      title="评论"
      width="50%"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <div class="comment-dialog-content">
        <!-- 评论列表 -->
        <div class="comments-list" v-loading="commentLoading">
          <div v-if="comments.length === 0" class="no-comments">
            暂无评论
          </div>
          <div v-else class="comment-item" v-for="comment in comments" :key="comment.id">
            <div class="comment-header">
              <span class="comment-user">用户{{ comment.userId }}</span>
              <span class="comment-time">{{ new Date(comment.createTime).toLocaleString() }}</span>
            </div>
            <div class="comment-content">{{ comment.content }}</div>
          </div>
        </div>

        <!-- 评论输入框 -->
        <div class="comment-input">
          <el-input
            v-model="commentContent"
            type="textarea"
            :rows="3"
            placeholder="请输入您的评论..."
            maxlength="200"
            show-word-limit
          />
          <el-button type="primary" @click="submitComment" class="submit-btn">
            发表评论
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { ElMessage } from 'element-plus';
import CommentItem from '../components/CommentItem.vue';
import axios from 'axios';

// 创建axios实例并设置基础配置
const api = axios.create({
  baseURL: 'http://localhost:8081',
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  },
  withCredentials: true,
  timeout: 10000
});

// 添加请求拦截器
api.interceptors.request.use(
  config => {
    return config;
  },
  error => {
    console.error('请求错误:', error);
    return Promise.reject(error);
  }
);

// 添加响应拦截器
api.interceptors.response.use(
  response => {
    return response;
  },
  error => {
    if (error.response) {
      console.error('错误状态码:', error.response.status);
      console.error('错误数据:', error.response.data);
    } else if (error.request) {
      console.error('请求未收到响应:', error.request);
    } else {
      console.error('请求配置错误:', error.message);
    }
    return Promise.reject(error);
  }
);

// 创建axios实例并设置基础配置
const likeApi = axios.create({
  baseURL: 'http://localhost:8081/api/likes',
  timeout: 5000,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  },
  withCredentials: true
});

// 创建评论API实例
const commentApi = axios.create({
  baseURL: 'http://localhost:8084/api/comments',
  timeout: 5000,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  },
  withCredentials: true
});

export default {
  name: 'Home',
  components: {
    CommentItem
  },
  data() {
    return {
      poems: [],
      loading: false,
      error: null,
      currentPage: 1,
      poemsPerPage: 10,
      likedPoems: new Set(),
      userId: localStorage.getItem('userId') || '1',
      recommendApi: axios.create({
        baseURL: 'http://localhost:8085/api/recommend',
        timeout: 5000
      }),
      // 添加评论相关的数据
      showCommentDialog: false,
      currentPoemId: null,
      commentContent: '',
      comments: [],
      commentLoading: false
    };
  },
  watch: {
    poems: {
      immediate: true,
      handler(newPoems) {
        if (newPoems && newPoems.length > 0) {
          this.currentPoem = newPoems[this.currentIndex];
        }
      }
    },
    currentIndex(newIndex) {
      if (this.poems && this.poems.length > 0) {
        this.currentPoem = this.poems[newIndex];
      }
    }
  },
  computed: {
    totalPages() {
      return Math.ceil(this.poems.length / this.poemsPerPage);
    },
    currentPagePoems() {
      const startIndex = (this.currentPage - 1) * this.poemsPerPage;
      return this.poems.slice(startIndex, startIndex + this.poemsPerPage);
    }
  },
  methods: {
    async fetchPoems() {
      this.loading = true;
      this.error = null;
      try {
        // 尝试不同的推荐接口
        const apis = [
          { method: 'hot', name: '热门诗词' },
          { method: 'daily', name: '每日推荐' }
        ];

        for (const api of apis) {
          try {
            const response = await this.recommendApi.get(`/${api.method}`);
            if (response.data && response.data.length > 0) {
              this.poems = response.data;
              this.currentIndex = 0;
              this.currentPoem = this.poems[0];
              console.log(`成功获取${api.name}数据`);
              break;
            }
          } catch (err) {
            console.warn(`获取${api.name}失败:`, err);
          }
        }

        if (this.poems.length === 0) {
          this.error = '暂无诗词数据';
        }
      } catch (error) {
        console.error('获取诗词数据失败:', error);
        this.error = '获取诗词数据失败，请稍后重试';
      } finally {
        this.loading = false;
      }
    },
    previousPoem() {
      if (this.currentIndex > 0) {
        this.currentIndex--;
      }
    },
    nextPoem() {
      if (this.currentIndex < this.poems.length - 1) {
        this.currentIndex++;
      }
    },
    async handleLike(poem) {
      if (!this.userId) {
        ElMessage.error('请先登录后再点赞');
        return;
      }
      
      try {
        const response = await likeApi.post(`/${poem.id}/toggle?userId=${this.userId}`);
        
        if (response.status === 200) {
          // 更新点赞状态和数量
          if (this.likedPoems.has(poem.id)) {
            this.likedPoems.delete(poem.id);
            poem.likeCount = (poem.likeCount || 1) - 1;
          } else {
            this.likedPoems.add(poem.id);
            poem.likeCount = (poem.likeCount || 0) + 1;
          }
          ElMessage.success(this.likedPoems.has(poem.id) ? '点赞成功' : '取消点赞成功');
        }
      } catch (error) {
        console.error('点赞操作失败:', error);
        if (error.response?.status === 401) {
          ElMessage.error('请先登录后再点赞');
        } else if (error.response?.status === 400) {
          ElMessage.error(error.response.data || '请求参数错误');
        } else {
          ElMessage.error('点赞失败，请稍后重试');
        }
      }
    },
    getPoemLines(content) {
      if (!content) return [];
      
      // 1. 先移除括号内的内容
      const contentWithoutBrackets = content.replace(/[（(].*?[）)]/g, '');
      
      // 2. 按句号分割，保留句号
      return contentWithoutBrackets
        .split(/([。])/g)
        .reduce((acc, cur, i, arr) => {
          if (i % 2 === 0) {
            const line = cur + (arr[i + 1] || '');
            if (line.trim()) {
              acc.push(line);
            }
          }
          return acc;
        }, [])
        .filter(line => line.trim());
    },
    previousPage() {
      if (this.currentPage > 1) {
        this.currentPage--;
        window.scrollTo(0, 0);
      }
    },
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++;
        window.scrollTo(0, 0);
      }
    },
    // 检查诗词是否已点赞
    isLiked(poemId) {
      return this.likedPoems.has(poemId);
    },
    // 打开评论对话框
    async openCommentDialog(poem) {
      this.currentPoemId = poem.id;
      this.showCommentDialog = true;
      this.commentContent = '';
      await this.loadComments(poem.id);
    },
    // 加载评论列表
    async loadComments(poemId) {
      this.commentLoading = true;
      try {
        const response = await commentApi.get(`/${poemId}/sorted-comments`);
        if (response.data && Array.isArray(response.data)) {
          this.comments = response.data;
          console.log('获取到的评论数据:', response.data);
        } else {
          this.comments = [];
          console.log('评论数据格式不正确:', response.data);
        }
      } catch (error) {
        console.error('获取评论失败:', error);
        ElMessage.error('获取评论失败，请稍后重试');
        this.comments = [];
      } finally {
        this.commentLoading = false;
      }
    },
    // 提交评论
    async submitComment() {
      if (!this.commentContent.trim()) {
        ElMessage.warning('请输入评论内容');
        return;
      }

      try {
        await commentApi.post(`/${this.currentPoemId}/add`, null, {
          params: {
            userId: this.userId,
            content: this.commentContent.trim()
          }
        });

        ElMessage.success('评论成功');
        this.commentContent = '';
        await this.loadComments(this.currentPoemId);
      } catch (error) {
        console.error('提交评论失败:', error);
        ElMessage.error('提交评论失败，请稍后重试');
      }
    }
  },
  created() {
    this.fetchPoems();
  }
};
</script>

<style scoped>
.home {
  min-height: 100vh;
  width: 100%;
  padding: 20px;
  position: relative;
  background: none;
}

.home::after {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: url('/background_no_watermark.jpg') no-repeat center center;
  background-size: cover;
  filter: none;
  -webkit-filter: none;
  z-index: 0;
}

.home::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.6);
  z-index: 1;
}

.poem-container {
  position: relative;
  z-index: 2;
  max-width: 800px;
  margin: 0 auto;
}

.loading, .error {
  text-align: center;
  padding: 20px;
  font-size: 18px;
}

.error {
  color: #ff4444;
}

.poems-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 30px;
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.poem-card {
  background: none;
  padding: 30px;
  display: flex;
  flex-direction: column;
  align-items: center;
  border: none;
  border-radius: 0;
  backdrop-filter: none;
}

.poem-header {
  text-align: center;
  margin-bottom: 15px;
  width: 100%;
}

.title {
  font-size: 22px;
  color: #333;
  margin-bottom: 10px;
  font-weight: normal;
  text-shadow: 1px 1px 2px rgba(255, 255, 255, 0.8);
}

.dynasty {
  font-size: 14px;
  color: #666;
  margin-bottom: 6px;
  text-shadow: 1px 1px 2px rgba(255, 255, 255, 0.8);
}

.author {
  font-size: 16px;
  color: #444;
  margin-bottom: 20px;
  text-shadow: 1px 1px 2px rgba(255, 255, 255, 0.8);
}

.content-vertical {
  width: 100%;
  padding: 0 10px;
}

.line {
  font-size: 16px;
  line-height: 1.8;
  color: #333;
  margin: 4px 0;
  text-align: center;
  letter-spacing: 2px;
  text-shadow: 1px 1px 2px rgba(255, 255, 255, 0.8);
}

.pagination {
  grid-column: 1 / -1;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 20px;
}

.like-button {
  padding: 6px 16px;
  background-color: transparent;
  border: 1px solid #666;
  border-radius: 16px;
  color: #666;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 4px;
}

.like-button:hover {
  background-color: rgba(0, 0, 0, 0.05);
  color: #ff4444;
}

.like-button.liked {
  color: #ff4444;
  border-color: #ff4444;
}

.like-button i {
  font-size: 16px;
}

.nav-button {
  background: transparent;
  border: 1px solid #666;
  cursor: pointer;
  color: #666;
  padding: 8px 16px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.nav-button:hover {
  background: rgba(0, 0, 0, 0.05);
}

.page-info {
  color: #666;
  font-size: 14px;
  text-shadow: 1px 1px 2px rgba(255, 255, 255, 0.8);
}

.poem-footer {
  display: flex;
  gap: 20px;
  justify-content: center;
  margin-top: 20px;
}

.comment-button {
  padding: 6px 16px;
  background-color: transparent;
  border: 1px solid #666;
  border-radius: 16px;
  color: #666;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 4px;
}

.comment-button:hover {
  background-color: rgba(0, 0, 0, 0.05);
  color: #409EFF;
  border-color: #409EFF;
}

.comment-button i {
  font-size: 16px;
}

.comment-dialog-content {
  padding: 20px;
}

.comments-list {
  max-height: 400px;
  overflow-y: auto;
  margin-bottom: 20px;
}

.no-comments {
  text-align: center;
  color: #999;
  padding: 20px;
}

.comment-item {
  padding: 15px;
  border-bottom: 1px solid #eee;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.comment-user {
  font-weight: bold;
  color: #409EFF;
}

.comment-time {
  color: #999;
  font-size: 12px;
}

.comment-content {
  color: #333;
  line-height: 1.5;
}

.comment-input {
  margin-top: 20px;
}

.submit-btn {
  margin-top: 10px;
  float: right;
}
</style>
