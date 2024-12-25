/**
 * @file request.js
 * @description Axios 请求工具封装
 * 功能：
 * 1. 创建统一的 axios 实例
 * 2. 配置请求和响应拦截器
 * 3. 统一的错误处理机制
 * 4. 支持跨域请求配置
 * 5. 请求超时设置
 */

import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建 axios 实例
const service = axios.create({
  baseURL: 'http://localhost:8083', // api的base_url
  timeout: 15000, // 请求超时时间
  withCredentials: true // 允许携带cookie
})

// request拦截器
service.interceptors.request.use(
  config => {
    // 在发送请求之前做些什么
    return config
  },
  error => {
    // 对请求错误做些什么
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// response拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    return res
  },
  error => {
    console.error('响应错误:', error)
    if (error.response) {
      ElMessage.error(error.response.data?.message || '服务器错误')
    } else {
      ElMessage.error('网络错误，请检查您的网络连接')
    }
    return Promise.reject(error)
  }
)

export default service 