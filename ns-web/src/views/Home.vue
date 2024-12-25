<template>
  <div class="home">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      正在加载诗词数据...
    </div>

    <!-- 错误提示 -->
    <div v-if="error" class="error-message">
      {{ error }}
    </div>

    <!-- 诗词列表 -->
    <div v-if="!loading && !error" class="poems-container">
      <div v-for="poem in currentPagePoems" :key="poem.id" class="poem-card">
        <div class="poem-content">
          <h2 class="poem-title">{{ poem.title }}</h2>
          <p class="poem-author">{{ poem.author }}</p>
          <p class="poem-text">{{ poem.content }}</p>
          <div class="poem-actions">
            <span @click="toggleLike(poem)" class="action-btn" :class="{ 'liked': isLiked(poem) }">
              <i class="el-icon-star-off"></i>
              点赞 {{ poem.likeCount || 0 }}
            </span>
            <span @click="openCommentDialog(poem)" class="action-btn">
              <i class="el-icon-chat-dot-round"></i>
              评论
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- ��论弹窗 -->
    <el-dialog
      v-model="commentDialogVisible"
      :title="currentPoem ? currentPoem.title + ' - 评论' : '评论'"
      width="50%"
      :before-close="handleCloseDialog"
    >
      <div class="comment-dialog-content">
        <!-- 评论输入框 -->
        <div class="comment-input">
          <el-input
            v-model="newComment"
            type="textarea"
            :rows="3"
            placeholder="写下你的评论..."
          />
          <el-button type="primary" @click="submitComment" :disabled="!newComment.trim()">
            发表评论
          </el-button>
        </div>

        <!-- 评论列表 -->
        <div class="comments-list">
          <div v-if="comments.length === 0" class="no-comments">
            暂无评论，快来发表第一条评论吧！
          </div>
          <div v-else v-for="comment in comments" :key="comment.id" class="comment-item">
            <div class="comment-header">
              <span class="comment-user">{{ comment.userId }}</span>
              <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
            </div>
            <div class="comment-content">{{ comment.content }}</div>
            <div class="comment-footer">
              <span @click="toggleCommentLike(comment)" 
                    class="comment-like" 
                    :class="{ 'liked': comment.likedUserIds?.includes(currentUserId) }">
                <i class="el-icon-star-off"></i>
                {{ comment.likeCount || 0 }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 分页器 -->
    <div v-if="!loading && !error && poems.length > 0" class="pagination">
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

export default {
  name: 'Home',
  components: {
    CommentItem
  },
  data() {
    return {
      poems: [],
      userId: 'user1',
      currentPage: 1,
      poemsPerPage: 10,
      totalPoems: 0,
      loading: false,
      error: null,
      currentUserId: 'user123',
      commentDialogVisible: false,
      currentPoem: null,
      newComment: '',
      comments: []
    };
  },
  async created() {
    await this.fetchPoems();
  },
  computed: {
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
        // 先刷新Redis缓存
        const refreshResponse = await fetch('http://localhost:8083/rank/refresh', {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
          },
          credentials: 'include'
        });
        
        if (!refreshResponse.ok) {
          console.warn('刷新缓存失败，尝试直接获取数据');
        }
        
        // 获取诗词数据
        const response = await fetch('http://localhost:8083/rank/top', {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
          },
          credentials: 'include'
        });

        if (!response.ok) {
          throw new Error('获取诗词数据失败: ' + response.statusText);
        }
        
        const data = await response.json();
        console.log('获取到的原始数据:', data);
        
        if (!data || Object.keys(data).length === 0) {
          throw new Error('没有获取到诗词数据');
        }
        
        // 转换数据格式
        this.poems = Object.values(data).map(poem => ({
          id: poem.id,
          title: poem.title,
          author: poem.writer || poem.author,
          content: poem.content,
          visitCount: poem.visitCount || 0,
          likeCount: poem.likeCount || 0,
          comment: {
            id: poem.id,
            content: poem.shangxi || '暂无赏析',
            likeCount: 0,
            likedUserIds: [],
            replies: []
          }
        }));
        
        this.totalPoems = this.poems.length;
        console.log('处理后的诗词数据:', this.poems);
      } catch (error) {
        console.error('获取诗词数据失败:', error);
        this.error = error.message || '获取数据失败，请稍后重试';
      } finally {
        this.loading = false;
      }
    },
    handlePageChange(newPage) {
      this.currentPage = newPage;
      window.scrollTo(0, 0);
    },
    async toggleLike(poem) {
      try {
        const response = await api.post(`/api/likes/poems/${poem.id}/toggle-like`, null, {
          params: { userId: this.currentUserId }
        });
        
        if (response.data) {
          poem.likeCount = response.data.likeCount;
          poem.likedUserIds = response.data.likedUserIds;
          ElMessage.success(poem.likedUserIds.includes(this.currentUserId) ? '点赞成功' : '取消点赞');
        }
      } catch (error) {
        console.error('点赞失败:', error);
        let errorMessage = '点赞操作失败，请稍后重试';
        if (error.response?.data?.message) {
          errorMessage = error.response.data.message;
        }
        ElMessage({
          message: errorMessage,
          type: 'error',
          duration: 3000
        });
      }
    },
    async addComment(poem, content) {
      if (!content.trim()) {
        this.$message.warning('评论内容不能为空');
        return;
      }

      try {
        const comment = {
          poemId: poem.id,
          userId: this.currentUserId,
          content: content.trim(),
          parentId: null // 顶级评论
        };

        const response = await api.post('/api/likes/comments', comment);
        const savedComment = response.data;
        
        // 更新评论列表
        if (!poem.comment.replies) {
          poem.comment.replies = [];
        }
        poem.comment.replies.unshift(savedComment);
        
        this.$message.success('评论发表成功');
        return savedComment;
      } catch (error) {
        console.error('发表评论失败:', error);
        this.$message.error('发表评论失败，请稍后重试');
        throw error;
      }
    },
    async loadComments(poem) {
      try {
        const response = await api.get(`/api/likes/poems/${poem.id}/comments`);
        if (response.data) {
          this.comments = response.data;
        } else {
          this.comments = [];
        }
      } catch (error) {
        console.error('加载评论失败:', error);
        let errorMessage = '加载评论失败，请稍后重试';
        if (error.response?.data?.message) {
          errorMessage = error.response.data.message;
        }
        ElMessage({
          message: errorMessage,
          type: 'error'
        });
        this.comments = [];
      }
    },
    async toggleCommentLike(comment) {
      try {
        const response = await api.post(`/api/likes/comments/${comment.id}/toggle-like`, null, {
          params: { userId: this.currentUserId }
        });
        
        if (response.data) {
          comment.likeCount = response.data.likeCount;
          comment.likedUserIds = response.data.likedUserIds;
          ElMessage.success(comment.likedUserIds.includes(this.currentUserId) ? '点赞成功' : '取消点赞');
        }
      } catch (error) {
        console.error('评论点赞失败:', error);
        let errorMessage = '点赞操作失败，请稍后重试';
        if (error.response?.data?.message) {
          errorMessage = error.response.data.message;
        }
        ElMessage({
          message: errorMessage,
          type: 'error',
          duration: 3000
        });
      }
    },
    isLiked(poem) {
      return poem.likedUserIds?.includes(this.currentUserId);
    },
    showComments(poem) {
      poem.showComments = !poem.showComments;
      if (poem.showComments && (!poem.comment.replies || poem.comment.replies.length === 0)) {
        this.loadComments(poem);
      }
    },
    updateComment(updatedComment) {
      const poem = this.poems.find(p => p.comment.id === updatedComment.id);
      if (poem) {
        poem.comment = updatedComment;
      }
    },
    openCommentDialog(poem) {
      this.currentPoem = poem;
      this.commentDialogVisible = true;
      this.newComment = '';
      this.loadComments(poem);
    },
    handleCloseDialog() {
      this.commentDialogVisible = false;
      this.currentPoem = null;
      this.comments = [];
      this.newComment = '';
    },
    async submitComment() {
      if (!this.newComment.trim()) {
        ElMessage.warning('评论内容不能为空');
        return;
      }

      try {
        const comment = {
          poemId: this.currentPoem.id,
          userId: this.currentUserId,
          content: this.newComment.trim(),
          parentId: null
        };

        const response = await api.post('/api/likes/comments', comment);
        if (response.data) {
          this.comments.unshift(response.data);
          this.newComment = '';
          ElMessage.success('评论发表成功');
        }
      } catch (error) {
        console.error('发表评论失败:', error);
        let errorMessage = '发表评论失败，请稍后重试';
        if (error.response?.data?.message) {
          errorMessage = error.response.data.message;
        }
        ElMessage({
          message: errorMessage,
          type: 'error'
        });
      }
    },
    formatTime(time) {
      if (!time) return '';
      return new Date(time).toLocaleString();
    }
  }
};
</script>

<style scoped>
.home {
  min-height: 100vh;
  width: 100%;
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
  background-color: rgba(255, 255, 255, 0.7);
  z-index: 1;
}

.poems-container {
  position: relative;
  z-index: 2;
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 40px;
}

.poem-card {
  padding: 40px 20px;
  border: none;
  position: relative;
  background: none;
  margin-bottom: 20px;
}

.poem-content {
  text-align: center;
  background: none;
}

.poem-title {
  font-size: 28px;
  color: #000;
  margin-bottom: 18px;
  font-weight: 500;
  letter-spacing: 4px;
  text-shadow: 1px 1px 2px rgba(255, 255, 255, 1.0);
}

.poem-author {
  color: #222;
  font-size: 20px;
  margin-bottom: 25px;
  letter-spacing: 2px;
  text-shadow: 1px 1px 2px rgba(255, 255, 255, 0.8);
}

.poem-text {
  font-size: 20px;
  line-height: 2;
  color: #000;
  margin-bottom: 30px;
  white-space: pre-wrap;
  letter-spacing: 3px;
  text-shadow: 1px 1px 2px rgba(255, 255, 255, 0.8);
}

.poem-meta {
  display: flex;
  justify-content: center;
  color: #444;
  font-size: 16px;
  gap: 30px;
  margin-bottom: 0;
  text-shadow: 1px 1px 2px rgba(255, 255, 255, 0.8);
}

.loading-state, .error-message {
  text-align: center;
  padding: 20px;
  color: #666;
  position: relative;
  z-index: 2;
  background: none;
}

.pagination {
  position: relative;
  z-index: 2;
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding: 20px 0;
  background: none;
}

:deep(.el-pagination .btn-prev),
:deep(.el-pagination .btn-next),
:deep(.el-pagination .el-pager li) {
  background: none;
  border: none;
  color: #333;
}

.like-btn, .comment-btn {
  cursor: pointer;
  transition: color 0.3s;
}

.like-btn:hover, .comment-btn:hover {
  color: #1890ff;
}

.like-btn.liked {
  color: #1890ff;
}

.poem-meta span {
  cursor: pointer;
  transition: color 0.3s;
}

.poem-meta span:hover {
  color: #1890ff;
}

.poem-actions {
  display: flex;
  justify-content: center;
  gap: 30px;
  margin-top: 20px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  color: #666;
  transition: all 0.3s;
}

.action-btn:hover {
  color: #1890ff;
}

.action-btn.liked {
  color: #1890ff;
}

.comment-dialog-content {
  padding: 20px 0;
}

.comment-input {
  margin-bottom: 20px;
}

.comment-input .el-button {
  margin-top: 10px;
  float: right;
}

.comments-list {
  margin-top: 30px;
}

.comment-item {
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.comment-user {
  font-weight: 500;
  color: #333;
}

.comment-time {
  color: #999;
  font-size: 12px;
}

.comment-content {
  color: #666;
  line-height: 1.6;
}

.comment-footer {
  margin-top: 8px;
  display: flex;
  justify-content: flex-end;
}

.comment-like {
  cursor: pointer;
  color: #666;
  transition: all 0.3s;
}

.comment-like:hover,
.comment-like.liked {
  color: #1890ff;
}

.no-comments {
  text-align: center;
  color: #999;
  padding: 20px 0;
}
</style>
