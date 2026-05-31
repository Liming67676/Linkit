import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import '../font_icon/iconfont.css'
import './style.scss'
import API from '@/api/index'
import store from './store'
// import ViewUIPlus from 'view-ui-plus'
// import 'view-ui-plus/dist/styles/viewuiplus.css'


const app = createApp(App)

app.config.globalProperties.$API = API
// 屏蔽 ElOnlyChild 警告，防止干扰页面功能
const origWarn = console.warn
console.warn = (...args) => {
  if (args[0] && typeof args[0] === 'string' && args[0].includes('ElOnlyChild')) {
    return
  }
  origWarn.apply(console, args)
}
app.config.warnHandler = () => null;
app.use(router)
app.use(ElementPlus)
app.use(store)
// app.use(ViewUIPlus)
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.mount('#app')
