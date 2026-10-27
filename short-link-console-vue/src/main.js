import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
// Element Plus 组件由 unplugin-vue-components 按需引入；ElMessage 是显式 import 的函数式 API，
// 按需插件不会替它补样式，这里只引入 message 自己的样式（内部已含 base 变量）
import 'element-plus/es/components/message/style/css'
import {
  ArrowLeft,
  CaretBottom,
  Close,
  Connection,
  CopyDocument,
  Edit,
  FolderOpened,
  Histogram,
  Iphone,
  Link,
  Message,
  Monitor,
  MoreFilled,
  Picture,
  Plus,
  Refresh,
  Remove,
  Share,
  Tickets,
  TrendCharts,
  User,
  UserFilled
} from '@element-plus/icons-vue'
import '../font_icon/iconfont.css'
import './style.scss'
import API from '@/api/index'
import store from './store'

const app = createApp(App)

app.config.globalProperties.$API = API
// 只过滤 ElOnlyChild 这一条已知噪音，其余警告保留，避免掩盖真实问题
const origWarn = console.warn
console.warn = (...args) => {
  if (args[0] && typeof args[0] === 'string' && args[0].includes('ElOnlyChild')) {
    return
  }
  origWarn.apply(console, args)
}
app.use(router)
app.use(store)

// 只注册模板里实际用到的 22 个图标。原来是把 @element-plus/icons-vue 的 293 个图标全量注册，
// 全部会被打进首屏包
const icons = {
  ArrowLeft,
  CaretBottom,
  Close,
  Connection,
  CopyDocument,
  Edit,
  FolderOpened,
  Histogram,
  Iphone,
  Link,
  Message,
  Monitor,
  MoreFilled,
  Picture,
  Plus,
  Refresh,
  Remove,
  Share,
  Tickets,
  TrendCharts,
  User,
  UserFilled
}
for (const [name, component] of Object.entries(icons)) {
  app.component(name, component)
}

app.mount('#app')
