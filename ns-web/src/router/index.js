/**
 * @file router/index.js
 * @description 路由配置文件
 * 功能：
 * 1. 配置前端路由规则
 * 2. 定义页面跳转路径
 * 3. 配置路由懒加载
 * 4. 设置默认重定向
 */

import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router 