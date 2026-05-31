import { createRouter, createWebHistory } from 'vue-router'
import { isNotEmpty } from '@/utils/plugins'
import { getToken, getUsername, setToken, setUsername } from '@/core/auth' // 验权
import user from '@/api/modules/user'
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/home'
    },
    {
      path: '/login',
      name: 'LoginIndex',
      component: () => import('@/views/login/LoginIndex.vue')
    },
    {
      path: '/home',
      name: 'LayoutIndex',
      redirect: '/home/space',
      component: () => import('@/views/home/HomeIndex.vue'),
      children: [
        {
          // 前面不能加/
          path: 'space',
          name: 'MySpace',
          component: () => import('@/views/mySpace/MySpaceIndex.vue'),
          meta: { title: '我的空间' }
        },
        {
          path: 'recycleBin',
          name: 'RecycleBin',
          component: () => import('@/views/recycleBin/RecycleBinIndex.vue'),
          meta: { title: '账户设置' }
        },
        {
          path: 'account',
          name: 'Mine',
          component: () => import('@/views/mine/MineIndex.vue'),
          meta: { title: '个人中心' }
        }
      ]
    }
  ]
})

// eslint-disable-next-line no-unused-vars
router.beforeEach(async (to, from, next) => {
  const localToken = localStorage.getItem('token')
  const localUsername = localStorage.getItem('username')
  const cookieToken = getToken()
  const cookieUsername = getUsername()

  // 仅在 cookie 缺失且 localStorage 里有有效值时做一次回填，避免把现有 cookie 覆盖成 null/undefined
  if (!isNotEmpty(cookieToken) && isNotEmpty(localToken)) {
    setToken(localToken)
  }
  if (!isNotEmpty(cookieUsername) && isNotEmpty(localUsername)) {
    setUsername(localUsername)
  }

  const token = getToken()
  if (to.path === '/login') {
    next()
    return
  }
  if (isNotEmpty(token)) {
    next()
  } else {
    next('/login')
  }
})

export default router
