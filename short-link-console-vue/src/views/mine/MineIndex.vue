<template>
  <div class="mine-layout">
    <div class="side-nav">
      <div class="nav-item active">
        <el-icon><User /></el-icon>
        <span>账号设置</span>
      </div>
    </div>
    <div class="main-content">
      <div class="info-card">
        <div class="card-header">
          <el-icon class="header-icon"><UserFilled /></el-icon>
          <span>个人信息</span>
        </div>
        <el-descriptions :column="1" border>
          <el-descriptions-item>
            <template #label>
              <div class="label-cell">
                <el-icon><User /></el-icon>
                用户名
              </div>
            </template>
            <span v-if="!dialogVisible" class="info-value">{{ userInfo?.data?.data?.username }}</span>
          </el-descriptions-item>
          <el-descriptions-item>
            <template #label>
              <div class="label-cell">
                <el-icon><Iphone /></el-icon>
                手机号
              </div>
            </template>
            <span class="info-value">{{ userInfo?.data?.data?.phone }}</span>
          </el-descriptions-item>
          <el-descriptions-item>
            <template #label>
              <div class="label-cell">
                <el-icon><Tickets /></el-icon>
                姓名
              </div>
            </template>
            <span class="info-value">{{ userInfo?.data?.data?.realName }}</span>
          </el-descriptions-item>
          <el-descriptions-item>
            <template #label>
              <div class="label-cell">
                <el-icon><Message /></el-icon>
                邮箱
              </div>
            </template>
            <span class="info-value">{{ userInfo?.data?.data?.mail }}</span>
          </el-descriptions-item>
        </el-descriptions>
        <div class="card-actions">
          <el-button type="primary" @click="dialogVisible = !dialogVisible">
            <el-icon><Edit /></el-icon>修改个人信息
          </el-button>
        </div>
      </div>
    </div>
    <el-dialog v-model="dialogVisible" title="修改个人信息" width="500px">
      <el-form ref="loginFormRef" :model="userInfoForm" label-width="70px" :rules="formRule">
        <el-form-item prop="username">
          <el-input v-model="userInfoForm.username" placeholder="请输入用户名" maxlength="11" show-word-limit disabled>
            <template #prepend>用户名</template>
          </el-input>
        </el-form-item>
        <el-form-item prop="mail">
          <el-input v-model="userInfoForm.mail" placeholder="请输入邮箱" show-word-limit clearable>
            <template #prepend>邮箱</template>
          </el-input>
        </el-form-item>
        <el-form-item prop="phone">
          <el-input v-model="userInfoForm.phone" placeholder="请输入手机号" show-word-limit clearable>
            <template #prepend>手机号</template>
          </el-input>
        </el-form-item>
        <el-form-item prop="realName">
          <el-input v-model="userInfoForm.realName" placeholder="请输入姓名" show-word-limit clearable>
            <template #prepend>姓名</template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="userInfoForm.password"
            placeholder="如需修改密码，请输入至少8位且包含大小写字母、数字和特殊符号的新密码"
            show-word-limit
            clearable
          >
            <template #prepend>密码</template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <div class="dialog-actions">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="changeUserInfo(loginFormRef)">提交</el-button>
          </div>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script setup>
import { getCurrentInstance, ref, reactive } from 'vue'
import { getUsername } from '@/core/auth'
import { cloneDeep } from 'lodash'
import { ElMessage } from 'element-plus'

const passwordPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z\d]).{8,}$/

const loginFormRef = ref()
const { proxy } = getCurrentInstance()
const API = proxy.$API
const userInfo = ref()
const userInfoForm = ref()

const getUserInfo = async () => {
  const username = getUsername()
  userInfo.value = await API.user.queryUserInfo(username)
  userInfoForm.value = cloneDeep(userInfo.value.data?.data)
}
getUserInfo()

const dialogVisible = ref(false)
const formRule = reactive({
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    {
      pattern: /^1[3456789]\d{9}$/,
      message: '请输入正确的手机号',
      trigger: 'blur'
    },
    { min: 11, max: 11, message: '手机号必须是11位', trigger: 'blur' }
  ],
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  mail: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    {
      pattern: /^([a-zA-Z]|[0-9])(\w|\-)+@[a-zA-Z0-9]+\.([a-zA-Z]{2,4})$/,
      message: '请输入正确的邮箱',
      trigger: 'blur'
    }
  ],
  password: [
    {
      validator: (rule, value, callback) => {
        if (!value) {
          callback()
          return
        }
        if (!passwordPattern.test(value)) {
          callback(new Error('密码需至少8位，且包含大写字母、小写字母、数字和特殊符号'))
          return
        }
        callback()
      },
      trigger: 'blur'
    }
  ],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }]
})

const changeUserInfo = (formEl) => {
  if (!formEl) return
  formEl.validate(async (valid) => {
    if (valid) {
      await API.user.editUser(userInfoForm.value).then((res) => {
        if (res?.data?.code !== '0') {
          ElMessage.error(res.data.message)
        } else {
          getUserInfo()
          dialogVisible.value = false
          ElMessage.success('修改成功!')
        }
      })
    }
  })
}
</script>

<style lang="scss" scoped>
$primary: #3464e0;
$primary-bg: #eef1fb;
$text-primary: #1d2129;
$text-muted: #86909c;
$border-color: #e5e6eb;

.mine-layout {
  display: flex;
  height: 100%;
  background: #f0f2f5;
}

.side-nav {
  width: 190px;
  background: #fff;
  border-right: 1px solid $border-color;
  padding: 16px 12px;

  .nav-item {
    display: flex;
    align-items: center;
    gap: 8px;
    height: 42px;
    padding: 0 12px;
    border-radius: 8px;
    font-size: 14px;
    font-weight: 500;
    color: $text-primary;
    cursor: pointer;
    transition: all 0.2s;

    &.active {
      background: $primary-bg;
      color: $primary;
      font-weight: 600;
    }

    &:hover:not(.active) {
      background: #f5f7fa;
    }
  }
}

.main-content {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}

.info-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  overflow: hidden;
  max-width: 600px;

  .card-header {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 16px 20px;
    border-bottom: 1px solid $border-color;
    font-size: 16px;
    font-weight: 600;
    color: $text-primary;

    .header-icon {
      color: $primary;
      font-size: 20px;
    }
  }

  :deep(.el-descriptions) {
    padding: 20px;

    .el-descriptions__label {
      width: 120px !important;
    }
  }

  .card-actions {
    padding: 0 20px 20px;
    display: flex;
    justify-content: flex-end;
  }
}

.label-cell {
  display: flex;
  align-items: center;
  gap: 6px;
}

.info-value {
  color: $text-primary;
}

.dialog-actions {
  width: 100%;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
