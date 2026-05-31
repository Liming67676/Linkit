<template>
  <div class="login-page">
    <div class="login-card">
      <div class="form-panel">
        <div class="form-header">
          <h2 class="form-title">{{ isLogin ? '欢迎回来' : '创建账号' }}</h2>
          <p class="form-subtitle">
            {{ isLogin ? '登录您的短链接管理平台' : '注册一个短链接管理平台账号' }}
          </p>
        </div>
        <div class="form-switcher">
          <Transition name="form-fade" mode="out-in">
            <el-form
              v-if="isLogin"
              ref="loginFormRef1"
              :model="loginForm"
              :rules="loginFormRule"
              class="login-form"
              key="login"
              @keyup.enter="login(loginFormRef1)"
            >
              <el-form-item prop="username">
                <el-input v-model="loginForm.username" placeholder="用户名" size="large" clearable />
              </el-form-item>
              <el-form-item prop="password">
                <el-input
                  v-model="loginForm.password"
                  type="password"
                  placeholder="密码"
                  size="large"
                  show-password
                  clearable
                />
              </el-form-item>
              <div class="form-extra">
                <el-checkbox v-model="checked" class="remember-checkbox">记住密码</el-checkbox>
              </div>
              <el-button type="primary" size="large" class="submit-btn" :loading="loading" @click="login(loginFormRef1)">
                登录
              </el-button>
            </el-form>

            <el-form
              v-else
              ref="loginFormRef2"
              :model="addForm"
              :rules="addFormRule"
              class="login-form"
              key="register"
              @keyup.enter="addUser(loginFormRef2)"
            >
              <el-form-item prop="username">
                <el-input v-model="addForm.username" placeholder="用户名" size="large" clearable />
              </el-form-item>
              <el-form-item prop="phone">
                <el-input v-model="addForm.phone" placeholder="手机号" size="large" clearable />
              </el-form-item>
              <el-form-item prop="mail">
                <el-input v-model="addForm.mail" placeholder="邮箱" size="large" clearable />
              </el-form-item>
              <el-form-item prop="realName">
                <el-input v-model="addForm.realName" placeholder="姓名" size="large" clearable />
              </el-form-item>
              <el-form-item prop="password">
                <el-input
                  v-model="addForm.password"
                  type="password"
                  placeholder="密码需至少8位，包含大小写字母、数字和特殊符号"
                  size="large"
                  show-password
                  clearable
                />
              </el-form-item>
              <el-button type="primary" size="large" class="submit-btn" :loading="loading" @click="addUser(loginFormRef2)">
                注册
              </el-button>
            </el-form>
          </Transition>
        </div>
      </div>

      <div class="brand-panel">
        <div class="brand-content">
          <div class="brand-icon">
            <Link class="el-icon" />
          </div>
          <h3 class="brand-title">SaaS 短链接平台</h3>
          <p class="brand-desc">
            {{ isLogin ? '还没有账号？注册即可开始创建您的短链接' : '已有账号？返回登录' }}
          </p>
          <el-button class="switch-btn" @click="changeLogin">
            {{ isLogin ? '去注册' : '去登录' }}
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { setToken, setUsername, getUsername, getToken } from '@/core/auth.js'
import { ref, reactive, getCurrentInstance, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const { proxy } = getCurrentInstance()
const API = proxy.$API
const router = useRouter()

const passwordPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z\d]).{8,}$/

const validateStrongPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入密码'))
    return
  }
  if (!passwordPattern.test(value)) {
    callback(new Error('密码需至少8位，且包含大写字母、小写字母、数字和特殊符号'))
    return
  }
  callback()
}

const loginFormRef1 = ref()
const loginFormRef2 = ref()
const loading = ref(false)
const checked = ref(true)
const isLogin = ref(true)

const loginForm = reactive({
  username: '',
  password: ''
})

const addForm = reactive({
  username: '',
  password: '',
  realName: '',
  phone: '',
  mail: ''
})

const addFormRule = reactive({
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3456789]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' },
    { min: 11, max: 11, message: '手机号必须是11位', trigger: 'blur' }
  ],
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ validator: validateStrongPassword, trigger: 'blur' }],
  mail: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { pattern: /^([a-zA-Z]|[0-9])(\w|\-)+@[a-zA-Z0-9]+\.([a-zA-Z]{2,4})$/, message: '请输入正确的邮箱', trigger: 'blur' }
  ],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }]
})

const loginFormRule = reactive({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
})

const LOGIN_TIP_DELAY = 1500
let loginTipTimer = null

const clearLoginTipTimer = () => {
  if (loginTipTimer) {
    clearTimeout(loginTipTimer)
    loginTipTimer = null
  }
}

const startLoginSlowTip = () => {
  clearLoginTipTimer()
  loginTipTimer = setTimeout(() => {
    ElMessage({
      type: 'info',
      duration: 6000,
      message: '登录中，请耐心等待，不要重复点击登录。'
    })
  }, LOGIN_TIP_DELAY)
}

const handleLoginSuccess = (token, username) => {
  if (token) {
    setToken(token)
    setUsername(username)
    localStorage.setItem('token', token)
    localStorage.setItem('username', username)
  }
  ElMessage.success('登录成功！')
  router.push('/home')
}

const redirectIfLoggedIn = async () => {
  const token = getToken()
  const username = getUsername()
  if (!token || !username) {
    return
  }
  try {
    const res = await API.user.checkLogin()
    if (res?.data?.code === '0' || res?.data?.success) {
      router.replace('/home')
    }
  } catch {
    // token 失效时停留在登录页
  }
}

const addUser = (formEl) => {
  if (!formEl) return
  formEl.validate(async (valid) => {
    if (!valid) return
    try {
      const res1 = await API.user.hasUsername({ username: addForm.username })
      if (res1?.data?.success === false) {
        ElMessage.warning('用户名已存在！')
        return
      }

      const res2 = await API.user.addUser(addForm)
      if (res2?.data?.success === false) {
        ElMessage.warning(res2.data.message)
        return
      }

      const res3 = await API.user.login({ username: addForm.username, password: addForm.password })
      const token = res3?.data?.data?.token
      if (token) {
        handleLoginSuccess(token, addForm.username)
        return
      }

      ElMessage.success('注册成功，请登录')
      isLogin.value = true
      loginForm.username = addForm.username
      loginForm.password = ''
    } catch {
      ElMessage.error('注册失败，请检查网络连接')
    }
  })
}

const login = (formEl) => {
  if (!formEl) return
  formEl.validate(async (valid) => {
    if (!valid) return
    try {
      loading.value = true
      startLoginSlowTip()
      const res1 = await API.user.login(loginForm)
      if (res1?.data?.code === '0') {
        const token = res1?.data?.data?.token
        if (token) {
          handleLoginSuccess(token, loginForm.username)
          return
        }
        ElMessage.success('登录成功！')
        router.push('/home')
      } else if (res1?.data?.message === '用户已登录') {
        const cookiesUsername = getUsername()
        if (cookiesUsername === loginForm.username) {
          ElMessage.success('登录成功！')
          router.push('/home')
        } else {
          ElMessage.warning('用户已在别处登录，请勿重复登录！')
        }
      } else if (res1?.data?.message === '用户不存在') {
        ElMessage.error('请输入正确的账号密码！')
      } else {
        ElMessage.error(res1?.data?.message || '登录失败')
      }
    } catch {
      ElMessage.error('登录失败，接口响应较慢或网络异常，请稍后重试')
    } finally {
      clearLoginTipTimer()
      loading.value = false
    }
  })
}

const changeLogin = () => {
  const domain = window.location.host
  if (domain === 'shortlink.magestack.cn' || domain === 'shortlink.nageoffer.com') {
    ElMessage.warning('演示环境暂不支持注册')
    return
  }
  isLogin.value = !isLogin.value
}

onMounted(() => {
  redirectIfLoggedIn()
})
</script>

<style lang="less" scoped>
.login-page {
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1a2a4a 0%, #2d4a7a 40%, #3a5f8a 70%, #4a6a8a 100%);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background:
      radial-gradient(circle at 30% 50%, rgba(255, 255, 255, 0.03) 0%, transparent 50%),
      radial-gradient(circle at 70% 50%, rgba(255, 255, 255, 0.02) 0%, transparent 50%);
    pointer-events: none;
  }
}

.login-card {
  display: flex;
  width: 780px;
  height: 560px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  overflow: hidden;
  position: relative;
  z-index: 1;
  animation: cardIn 0.5s ease-out;
}

@keyframes cardIn {
  0% {
    opacity: 0;
    transform: translateY(20px) scale(0.97);
  }

  100% {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.form-panel {
  flex: 1;
  padding: 40px 44px;
  display: flex;
  flex-direction: column;
}

.form-header {
  margin-bottom: 20px;
  flex-shrink: 0;
}

.form-title {
  font-size: 26px;
  font-weight: 700;
  color: #1d2129;
  margin: 0 0 8px 0;
}

.form-subtitle {
  font-size: 14px;
  color: #86909c;
  margin: 0;
}

.form-switcher {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.login-form {
  width: 100%;

  :deep(.el-input__wrapper) {
    border-radius: 10px;
    box-shadow: 0 0 0 1px #e5e6eb inset;
    transition: box-shadow 0.2s;
    padding: 4px 16px;

    &:hover {
      box-shadow: 0 0 0 1px #3464e0 inset;
    }

    &.is-focus {
      box-shadow: 0 0 0 1px #3464e0 inset;
    }
  }

  :deep(.el-input__inner) {
    height: 44px;
    font-size: 14px;
  }

  :deep(.el-form-item) {
    margin-bottom: 20px;
  }
}

.form-fade-enter-active {
  animation: formIn 0.3s ease-out;
}

.form-fade-leave-active {
  animation: formOut 0.2s ease-in;
}

@keyframes formIn {
  0% {
    opacity: 0;
    transform: translateY(8px);
  }

  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes formOut {
  0% {
    opacity: 1;
    transform: translateY(0);
  }

  100% {
    opacity: 0;
    transform: translateY(-4px);
  }
}

.form-extra {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;

  .remember-checkbox {
    font-size: 13px;
    color: #86909c;
  }
}

.submit-btn {
  width: 100%;
  height: 46px;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 2px;
}

.brand-panel {
  width: 320px;
  background: linear-gradient(135deg, #1a2a4a 0%, #2d4a7a 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  flex-shrink: 0;
}

.brand-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  color: #fff;
}

.brand-icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.12);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;

  .el-icon {
    font-size: 32px;
    color: #fff;
  }
}

.brand-title {
  font-size: 20px;
  font-weight: 700;
  margin: 0 0 12px 0;
}

.brand-desc {
  font-size: 14px;
  line-height: 1.6;
  color: rgba(255, 255, 255, 0.7);
  margin: 0 0 28px 0;
}

.switch-btn {
  width: 140px;
  height: 42px;
  border-radius: 21px;
  font-size: 14px;
  font-weight: 500;
  color: #2d4a7a;
  background: #fff;
  border: none;

  &:hover {
    background: rgba(255, 255, 255, 0.9);
    color: #2d4a7a;
  }
}
</style>
