/**
 * @file App.vue
 * @description 应用程序根组件
 * 功能：
 * 1. 作为应用程序的入口组件
 * 2. 提供路由出口
 * 3. 配置全局样式
 * 4. 设置基础布局
 */

<script setup>
import HelloWorld from './components/HelloWorld.vue'
</script>

<template>
  <router-view />
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}
</style>
