<template>
  <div class="common-layout">
    <el-container>
      <el-header height="56px" style="padding: 0">
        <div class="header">
          <div @click="toMySpace" class="logo">
            <span class="logo-icon">S</span>
            SaaS短链接
          </div>
        </div>
      </el-header>
      <el-main style="padding: 0">
        <div class="content-box">
          <RouterView class="content-space" />
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, getCurrentInstance, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUsername } from '@/core/auth.js'
const { proxy } = getCurrentInstance()
const API = proxy.$API
const router = useRouter()
const toMySpace = () => {
  router.push('/home' + '/space')
}
const username = ref('')
onMounted(async () => {
  const actualUsername = getUsername()
  username.value = actualUsername
})
</script>

<style lang="scss" scoped>
.el-container {
  height: 100vh;

  .el-main {
    background-color: #f0f2f5;
  }
}

.header {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  padding: 0 32px;
  height: 100%;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
}

.logo {
  font-size: 18px;
  font-weight: 700;
  color: #ffffff;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 10px;
  letter-spacing: 0.5px;
  transition: opacity 0.2s;

  .logo-icon {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 32px;
    height: 32px;
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    border-radius: 8px;
    font-size: 16px;
    font-weight: 800;
    color: #fff;
  }
}

.logo:hover {
  opacity: 0.85;
}

.content-box {
  height: calc(100vh - 56px);
  background-color: #f0f2f5;
}
</style>
