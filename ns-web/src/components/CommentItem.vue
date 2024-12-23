<template>
  <div class="comment-item" :id="'poem-' + comment.id" ref="commentItem">
    <!-- 空白区域用于显示诗词 -->
    <div class="poem-preview">
      <slot name="poem-content"></slot>
    </div>

    <div class="action-buttons">
      <!-- 点赞文字 -->
      <span class="like-text" @click="toggleLike" :class="{ liked: isLiked }">
        点赞 {{ comment.likeCount }}
      </span>

      <!-- 评论按钮 -->
      <span class="comment-text" @click="showComment" ref="commentBtn">
        评论
      </span>

      <!-- 评论内容弹窗 -->
      <Transition name="fade">
        <div v-if="showCommentWindow" class="custom-dialog">
          <div class="dialog-header">
            <span class="dialog-title">《{{ poemTitle }}》的评论</span>
            <span class="close-btn" @click="closeComment">×</span>
          </div>
          <div class="dialog-content">
            <!-- 主评论内容 -->
            <div class="main-comment">
              <div class="comment-text">{{ comment.content }}</div>
              <div class="comment-info">
                <span class="like-count">点赞 {{ comment.likeCount }}</span>
              </div>
            </div>

            <!-- 评论输入框 -->
            <div class="comment-input-box" v-if="!replyTo">
              <textarea
                v-model="newComment"
                placeholder="写下你的评论..."
                class="comment-textarea"
              ></textarea>
              <button class="submit-btn" @click="submitComment">发表评论</button>
            </div>

            <!-- 评论列表 -->
            <div class="comments-list">
              <div v-for="reply in comment.replies" :key="reply.id" class="comment-thread">
                <!-- 一级回复 -->
                <div class="comment-main">
                  <div class="comment-user">
                    <span class="username">{{ reply.username }}</span>
                    <span class="comment-time">{{ reply.time }}</span>
                  </div>
                  <div class="comment-content">{{ reply.content }}</div>
                  <div class="comment-actions">
                    <span class="action-btn" @click="likeComment(reply)">
                      {{ reply.isLiked ? '已赞' : '点赞' }} {{ reply.likes }}
                    </span>
                    <span class="action-btn" @click="showReplyInput(reply)">回复</span>
                  </div>

                  <!-- 回复输入框 -->
                  <div class="reply-input-box" v-if="replyTo === reply.id">
                    <textarea
                      v-model="replyContent"
                      :placeholder="'回复 @' + reply.username"
                      class="reply-textarea"
                    ></textarea>
                    <div class="reply-actions">
                      <button class="cancel-btn" @click="cancelReply">取消</button>
                      <button class="submit-btn" @click="submitReply(reply)">回复</button>
                    </div>
                  </div>

                  <!-- 二级回复 -->
                  <div class="nested-replies" v-if="reply.replies && reply.replies.length > 0">
                    <div v-for="nestedReply in reply.replies" :key="nestedReply.id" class="reply-item">
                      <div class="comment-user">
                        <span class="username">{{ nestedReply.username }}</span>
                        <span class="reply-to" v-if="nestedReply.replyTo">
                          回复 @{{ nestedReply.replyTo }}
                        </span>
                        <span class="comment-time">{{ nestedReply.time }}</span>
                      </div>
                      <div class="comment-content">{{ nestedReply.content }}</div>
                      <div class="comment-actions">
                        <span class="action-btn" @click="likeComment(nestedReply)">
                          {{ nestedReply.isLiked ? '已赞' : '点赞' }} {{ nestedReply.likes }}
                        </span>
                        <span class="action-btn" @click="showReplyInput(reply, nestedReply)">回复</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </Transition>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'CommentItem',
  props: {
    comment: {
      type: Object,
      required: true
    },
    userId: {
      type: String,
      required: true
    },
    poemTitle: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      showCommentWindow: false,
      newComment: '',
      replyTo: null,
      replyContent: ''
    };
  },
  computed: {
    isLiked() {
      return this.comment.likedUserIds?.includes(this.userId);
    }
  },
  methods: {
    toggleLike() {
      axios.post(`/likes/comments/${this.comment.id}/toggle-like`, { userId: this.userId })
        .then(response => {
          this.$emit('update-comment', response.data);
        })
        .catch(error => {
          console.error('Error toggling like:', error);
        });
    },
    showComment() {
      // 关闭其他可能打开的评论窗口
      document.querySelectorAll('.custom-dialog').forEach(dialog => {
        if (dialog.parentElement !== this.$refs.commentItem) {
          dialog.style.display = 'none';
        }
      });
      
      this.showCommentWindow = true;
    },
    closeComment() {
      this.showCommentWindow = false;
      this.replyTo = null;
      this.replyContent = '';
    },
    likeComment(comment) {
      comment.isLiked = !comment.isLiked;
      comment.likes += comment.isLiked ? 1 : -1;
    },
    showReplyInput(reply, replyToComment = null) {
      this.replyTo = reply.id;
      this.replyContent = replyToComment ? `@${replyToComment.username} ` : '';
    },
    cancelReply() {
      this.replyTo = null;
      this.replyContent = '';
    },
    submitComment() {
      if (!this.newComment.trim()) return;
      
      const newReply = {
        id: Date.now(),
        username: '当前用户',
        content: this.newComment,
        time: new Date().toLocaleString(),
        likes: 0,
        isLiked: false,
        replies: []
      };
      
      this.comment.replies.unshift(newReply);
      this.newComment = '';
    },
    submitReply(parentReply) {
      if (!this.replyContent.trim()) return;
      
      const newReply = {
        id: Date.now(),
        username: '当前用户',
        content: this.replyContent,
        time: new Date().toLocaleString(),
        likes: 0,
        isLiked: false,
        replyTo: parentReply.username
      };
      
      if (!parentReply.replies) {
        parentReply.replies = [];
      }
      parentReply.replies.push(newReply);
      this.cancelReply();
    }
  }
};
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.comment-item {
  background-color: #fff;
  position: relative;
}

.action-buttons {
  display: flex;
  gap: 30px;
  justify-content: center;
  padding: 10px 0;
  border-top: 1px dashed #eee;
  position: relative;
}

.like-text, .comment-text {
  color: #666;
  cursor: pointer;
  font-size: 14px;
  user-select: none;
}

.like-text.liked {
  color: #1890ff;
}

/* 自定义弹窗样式 */
.custom-dialog {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: white;
  width: 600px;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  z-index: 1000;
}

/* 移除箭头样式 */
.custom-dialog::after {
  display: none;
}

.dialog-header {
  padding: 20px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #f8f9fa;
  border-radius: 8px 8px 0 0;
}

.dialog-title {
  font-size: 18px;
  color: #333;
  font-weight: 500;
}

.close-btn {
  cursor: pointer;
  font-size: 24px;
  color: #666;
  transition: color 0.3s;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
}

.close-btn:hover {
  color: #333;
  background-color: #e9ecef;
}

.dialog-content {
  padding: 24px;
  line-height: 1.8;
  color: #333;
  font-size: 16px;
  max-height: 60vh;
  overflow-y: auto;
}

/* 评论区样式 */
.comment-input-box {
  margin-bottom: 20px;
}

.comment-textarea,
.reply-textarea {
  width: 100%;
  min-height: 80px;
  padding: 12px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  resize: vertical;
  margin-bottom: 10px;
  font-size: 14px;
}

.submit-btn,
.cancel-btn {
  padding: 6px 16px;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.submit-btn {
  background-color: #1890ff;
  color: white;
}

.submit-btn:hover {
  background-color: #40a9ff;
}

.cancel-btn {
  background-color: #f0f0f0;
  color: #666;
  margin-right: 10px;
}

.cancel-btn:hover {
  background-color: #e0e0e0;
}

.comments-list {
  margin-top: 20px;
}

.comment-thread {
  margin-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 20px;
}

.comment-main {
  margin-bottom: 10px;
}

.comment-user {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  font-size: 14px;
}

.username {
  font-weight: 500;
  color: #333;
  margin-right: 8px;
}

.reply-to {
  color: #1890ff;
  margin-right: 8px;
}

.comment-time {
  color: #999;
  font-size: 12px;
}

.comment-content {
  margin: 8px 0;
  color: #333;
  line-height: 1.6;
  font-size: 14px;
}

.comment-actions {
  display: flex;
  gap: 16px;
  margin-top: 8px;
}

.action-btn {
  color: #666;
  cursor: pointer;
  font-size: 13px;
}

.action-btn:hover {
  color: #1890ff;
}

.nested-replies {
  margin-left: 32px;
  padding-left: 16px;
  border-left: 2px solid #f0f0f0;
}

.reply-item {
  margin-top: 16px;
  padding-bottom: 16px;
}

.reply-item:last-child {
  padding-bottom: 0;
}

.reply-input-box {
  margin: 10px 0;
  padding: 10px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.reply-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}

/* 移除头像相关样式 */
.user-avatar {
  display: none;
}

.main-comment {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.main-comment .comment-text {
  font-size: 16px;
  line-height: 1.6;
  color: #333;
  margin-bottom: 10px;
}

.main-comment .comment-info {
  color: #666;
  font-size: 14px;
}

.like-count {
  cursor: pointer;
  transition: color 0.3s;
}

.like-count:hover {
  color: #1890ff;
}
</style>
