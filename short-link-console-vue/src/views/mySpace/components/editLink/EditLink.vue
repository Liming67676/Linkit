<template>
  <div>
    <el-form ref="ruleFormRef" :model="formData" :rules="formRule" label-width="85px">
      <el-form-item label="跳转链接" prop="originUrl">
        <el-input v-model="formData.originUrl" placeholder="请输入http://或https://开头的链接或应用跳转链接"></el-input>
      </el-form-item>
      <el-form-item label="描述信息" prop="describe">
        <el-input :rows="3" v-model="formData.describe" type="textarea"
          placeholder="请输入描述信息" />
        <span>{{ describeChars + '/' + maxDescribeChars }}</span>
      </el-form-item>

      <el-form-item label="短链分组" prop="gid">
        <el-select v-model="formData.gid" placeholder="请选择">
          <el-option v-for="item in groupInfo" :key="item.gid" :label="item.name" :value="item.gid" />
        </el-select>
      </el-form-item>
      <el-form-item label="短链域名">
        <el-input v-model="formData.domain" disabled placeholder="域名不可修改" />
      </el-form-item>
      <el-form-item label="有效期">
        <el-radio-group v-model="formData.validDateType">
          <el-radio :label="0">永久</el-radio>
          <el-radio :label="1">自定义</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item v-if="formData.validDateType === 1" label="选择时间">
        <el-date-picker :disabled-date="disabledDate" v-model="formData.validDate" value-format="YYYY-MM-DD HH:mm:ss"
          type="datetime" placeholder="选择日期" :shortcuts="shortcuts" />
        <span class="alert">链接失效后将自动跳转到404页面 !</span>
      </el-form-item>
      <el-form-item>
        <div style="width: 100%; display: flex; justify-content: flex-end; gap: 12px;">
          <el-button class="buttons" type="primary" @click="onSubmit(ruleFormRef)">确认</el-button>
          <el-button class="buttons" @click="cancel">取消</el-button>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, nextTick, watch, onBeforeUnmount, getCurrentInstance } from 'vue'
import { useStore } from 'vuex'
import {ElMessage} from "element-plus";

const store = useStore()
const defaultDomain = store.state.domain?? ' '
const props = defineProps({
  groupInfo: Array,
  editData: Object
})
const { proxy } = getCurrentInstance()
const API = proxy.$API
const editData = props.editData
const reg = /^https?:\/\/.+/
const shortcuts = [
  {
    text: '一天',
    value: () => {
      const date = new Date()
      date.setTime(date.getTime() + 3600 * 1000 * 24)
      return date
    },
  },
  {
    text: '七天',
    value: () => {
      const date = new Date()
      date.setTime(date.getTime() + 3600 * 1000 * 24 * 7)
      return date
    },
  },
  {
    text: '三十天',
    value: () => {
      const date = new Date()
      date.setTime(date.getTime() + 3600 * 1000 * 24 * 30)
      return date
    },
  },
]
const groupInfo = ref()
const formData = reactive({
  domain: editData.domain || defaultDomain,
  originUrl: editData.originUrl,
  gid: editData.gid,
  originGid: editData.gid,
  createdType: editData.createdType,
  validDate: editData.validDate,
  describe: editData.describe,
  validDateType: editData.validDateType,
  fullShortUrl: editData.fullShortUrl
})
const initFormData = () => {
  formData.domain = defaultDomain
  formData.originUrl = null
  formData.createdType = 1
  formData.validDate = null
  formData.describe = null
  formData.validDateType = 0
  formData.fullShortUrl = null
}
const maxOriginUrlRows = ref(100)
const originUrlRows = ref(0)
const fd = (fn, delay) => {
  let timer = null
  return function (url) {
    if (timer) {
      clearTimeout(timer)
      timer = null
    }
    timer = setTimeout(() => {
      fn(url)
    }, delay)
  }
}
const queryTitle = (url) => {
  if (reg.test(url)) {
    API.smallLinkPage.queryTitle({ url: url }).then(res => {
      formData.describe = res?.data?.data
    })
  }
}
const getTitle = fd(queryTitle, 1000)
watch(
  () => formData.originUrl,
  nV => {
    originUrlRows.value = (nV || '').split(/\r\n|\r|\n/).filter(Boolean)?.length ?? 0
    if (!formData.describe) {
      getTitle(nV)
    }
  }
)
const maxDescribeChars = ref(200)
const describeChars = ref(0)
watch(
  () => formData.describe,
  nV => {
    describeChars.value = (nV || '').length
  }
)

watch(
  () => props.groupInfo,
  nV => {
    groupInfo.value = nV
    formData.gid = nV[0].gid
  },
  {
    immediate: true
  }
)
watch(
  () => props.editData,
  (nV) => {
    formData.domain = nV.domain || defaultDomain
    formData.originUrl = nV.originUrl
    formData.gid = nV.gid
    formData.originGid = nV.gid
    formData.createdType = nV.createdType
    formData.validDate = nV.validDate
    formData.describe = nV.describe
    formData.validDateType = nV.validDateType
    formData.fullShortUrl = nV.fullShortUrl
  },
  {
    immediate: true
  }
)
const formRule = reactive({
  originUrl: [
    { required: true, message: '请输入链接', trigger: 'blur' },
    {
      validator: function (rule, value, callback) {
        if (value) {
          const lines = value.split(/\r\n|\r|\n/).filter(Boolean)
          for (const item of lines) {
            if (!reg.test(item.trim())) {
              callback(new Error('请输入 http:// 或 https:// 开头的链接或应用跳转链接'))
              return
            }
          }
        }
        if (originUrlRows.value > maxOriginUrlRows.value) {
          callback(new Error('超过输入' + maxOriginUrlRows.value + '行'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    },
  ],
  gid: [{ required: true, message: '请选择分组', trigger: 'blur' }],
  describe: [
    { required: true, message: '请输入描述信息', trigger: 'blur' },
    {
      validator: function (rule, value, callback) {
        if (describeChars.value > maxDescribeChars.value) {
          callback(new Error('超过输入' + maxDescribeChars.value + '字符'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    },
  ],
  validDate: [
    { required: false, message: '请输日期', trigger: 'blur' },
  ]
})
const disabledDate = (time) => {
  return new Date(time).getTime() < new Date().getTime()
}

const emits = defineEmits(['onSubmit', 'cancel', 'updatePage'])
const ruleFormRef = ref()
const onSubmit = async (formEl) => {
  if (!formEl) {
    return
  }
  await formEl.validate(async (valid) => {
    if (valid) {
      emits('onSubmit', false)
      const res = await API.smallLinkPage.editSmallLink(formData)
      if (res?.data?.code !== '0') {
        ElMessage.error(res.data.message)
      } else {
        ElMessage.success('修改成功')
        emits('updatePage')
      }
    }
  })
}
const cancel = () => {
  emits('cancel', false)
  initFormData()
}
onBeforeUnmount(() => {
  initFormData()
})
defineExpose({
  initFormData
})
</script>

<style lang="less" scoped>
.alert {
  color: rgb(231, 166, 67);
  font-size: 12px;
  width: 90%;
}
</style>
