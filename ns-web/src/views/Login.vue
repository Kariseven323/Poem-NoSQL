/**
 * @file Login.vue
 * @description 用户登录页面组件
 * 功能：
 * 1. 提供用户登录表单界面
 * 2. 实现用户名密码的验证和登录请求
 * 3. 登录成功后存储用户信息并跳转
 * 4. 提供注册入口链接
 * 5. 包含美观的UI设计和动画效果
 */

<template>
  <div class="login-container">
    <div class="background-squares">
      <div class="square1"></div>
      <div class="square2"></div>
      <div class="square3"></div>
    </div>
    <el-card class="login-card animate__animated animate__fadeIn">
      <template #header>
        <div class="card-header">
          <h2>欢迎回来</h2>
          <p class="subtitle">请登录您的账号</p>
        </div>
      </template>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef">
        <el-form-item prop="username">
          <el-input 
            v-model="loginForm.username" 
            placeholder="用户名"
            :prefix-icon="User"
            class="custom-input"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input 
            v-model="loginForm.password" 
            type="password" 
            placeholder="密码"
            :prefix-icon="Lock"
            class="custom-input"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button 
            type="primary" 
            @click="handleLogin" 
            class="submit-btn"
            :loading="loading"
          >
            登录
          </el-button>
        </el-form-item>
        <div class="register-link">
          还没有账号？<router-link to="/register" class="link-text">立即注册</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import request from '../utils/request'
import 'animate.css'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const response = await request.post('/users/login', null, {
          params: {
            username: loginForm.username,
            password: loginForm.password
          }
        })

        if (response) {
          ElMessage.success('登录成功')
          localStorage.setItem('user', JSON.stringify(response))
          router.push('/home')  // 登录成功后跳转到主页
        } else {
          ElMessage.error('用户名或密码错误')
        }
      } catch (error) {
        console.error('登录错误:', error)
      } finally {
        loading.value = false
      }
    }
  })
}

</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #1a2a6c, #b21f1f, #fdbb2d);
  position: relative;
  overflow: hidden;
}

.background-squares div {
  position: absolute;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  animation: float 10s infinite;
}

.square1 {
  width: 100px;
  height: 100px;
  top: 20%;
  left: 20%;
  animation-delay: 0s !important;
}

.square2 {
  width: 150px;
  height: 150px;
  top: 40%;
  right: 20%;
  animation-delay: 2s !important;
}

.square3 {
  width: 80px;
  height: 80px;
  bottom: 20%;
  left: 30%;
  animation-delay: 4s !important;
}

@keyframes float {
  0% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(180deg); }
  100% { transform: translateY(0) rotate(360deg); }
}

.login-card {
  width: 400px;
  border-radius: 15px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
  transition: transform 0.3s ease;
}

.login-card:hover {
  transform: translateY(-5px);
}

.card-header {
  text-align: center;
  padding: 20px 0;
}

.card-header h2 {
  margin: 0;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.subtitle {
  color: #909399;
  font-size: 14px;
  margin-top: 8px;
}

.custom-input :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.custom-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.submit-btn {
  width: 100%;
  height: 44px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 1px;
  background: linear-gradient(135deg, #1a2a6c, #b21f1f);
  border: none;
  transition: all 0.3s ease;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.register-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #606266;
}

.link-text {
  color: #1a2a6c;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s ease;
}

.link-text:hover {
  color: #b21f1f;
}

:deep(.el-form-item) {
  margin-bottom: 25px;
}

:deep(.el-card__header) {
  border-bottom: none;
  padding-bottom: 0;
}
</style> 