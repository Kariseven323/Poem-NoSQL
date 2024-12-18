/**
 * @file Register.vue
 * @description 用户注册页面组件
 * 功能：
 * 1. 提供用户注册表单界面
 * 2. 实现用户信息的验证（用户名、密码、手机号等）
 * 3. 处理注册请求并反馈结果
 * 4. 提供登录入口链接
 * 5. 包含表单验证和错误提示
 * 6. 包含美观的UI设计和动画效果
 */

<template>
  <div class="register-container">
    <div class="background-squares">
      <div class="square1"></div>
      <div class="square2"></div>
      <div class="square3"></div>
    </div>
    <el-card class="register-card animate__animated animate__fadeIn">
      <template #header>
        <div class="card-header">
          <h2>创建账号</h2>
          <p class="subtitle">填写信息开始您的旅程</p>
        </div>
      </template>
      <el-form :model="registerForm" :rules="rules" ref="registerFormRef">
        <el-form-item prop="username">
          <el-input 
            v-model="registerForm.username" 
            placeholder="用户名"
            :prefix-icon="User"
            class="custom-input"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input 
            v-model="registerForm.password" 
            type="password" 
            placeholder="密码"
            :prefix-icon="Lock"
            class="custom-input"
          />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input 
            v-model="registerForm.confirmPassword" 
            type="password" 
            placeholder="确认密码"
            :prefix-icon="Lock"
            class="custom-input"
          />
        </el-form-item>
        <el-form-item prop="phone">
          <el-input 
            v-model="registerForm.phone" 
            placeholder="手机号"
            :prefix-icon="Phone"
            class="custom-input"
          />
        </el-form-item>
        <el-form-item prop="gender">
          <el-select 
            v-model="registerForm.gender" 
            placeholder="性别" 
            class="custom-select"
          >
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button 
            type="primary" 
            @click="handleRegister" 
            class="submit-btn"
            :loading="loading"
          >
            注册
          </el-button>
        </el-form-item>
        <div class="login-link">
          已有账号？<router-link to="/login" class="link-text">立即登录</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { User, Lock, Phone } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import request from '../utils/request'
import 'animate.css'

const router = useRouter()
const registerFormRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  phone: '',
  gender: ''
})

const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else {
    if (registerForm.confirmPassword !== '') {
      registerFormRef.value.validateField('confirmPassword')
    }
    callback()
  }
}

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { validator: validatePass, trigger: 'blur' },
    { min: 6, message: '密码长度至少为 6 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validatePass2, trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const { confirmPassword, ...registerData } = registerForm
        const response = await request.post('/users/register', registerData)
        
        if (response) {
          ElMessage.success('注册成功')
          router.push('/login')
        }
      } catch (error) {
        console.error('注册错误:', error)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.register-container {
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

.register-card {
  width: 400px;
  border-radius: 15px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
  transition: transform 0.3s ease;
}

.register-card:hover {
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

.custom-input :deep(.el-input__wrapper),
.custom-select :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.custom-input :deep(.el-input__wrapper:hover),
.custom-select :deep(.el-input__wrapper:hover) {
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

.login-link {
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