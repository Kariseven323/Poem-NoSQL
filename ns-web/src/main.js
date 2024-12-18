/**
 * @file main.js
 * @description 应用程序主入口文件
 * 功能：
 * 1. 创建 Vue 应用实例
 * 2. 注册全局组件和插件
 * 3. 配置 Element Plus UI 库
 * 4. 配置路由
 * 5. 挂载应用程序
 */

import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import router from './router'
import App from './App.vue'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const app = createApp(App)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(ElementPlus)
app.use(router)
app.mount('#app')
