/**
 * @file guard.js
 * @description 路由守卫配置
 * 功能：
 * 1. 路由前置守卫
 * 2. 路由后置守卫
 * 3. 权限控制
 */

import router from './index'
import { ElMessage } from 'element-plus'

// 白名单路由
const whiteList = ['/login', '/register']

router.beforeEach((to, from, next) => {
  // 获取token
  const hasToken = localStorage.getItem('user')

  if (hasToken) {
    if (to.path === '/login') {
      // 如果已登录，重定向到首页
      next({ path: '/' })
    } else {
      next()
    }
  } else {
    if (whiteList.includes(to.path)) {
      // 在白名单中，直接进入
      next()
    } else {
      // 其他没有访问权限的页面将被重定向到登录页面
      ElMessage.warning('请先登录')
      next(`/login?redirect=${to.path}`)
    }
  }
})

router.afterEach(() => {
  // 路由切换后的操作
}) 