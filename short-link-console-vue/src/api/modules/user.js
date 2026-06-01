import http from '../axios'
export default {
  // 注册
  addUser(data) {
    return http({
      url: '/user/register',
      method: 'post',
      data
    })
  },
  // 编辑信息
  editUser(data) {
    return http({
      url: '/user',
      method: 'put',
      data
    })
  },
  // 登录
  login(data, config = {}) {
    return http({
      url: '/user/login',
      method: 'post',
      data,
      timeout: 10000,
      ...config
    })
  },
  // 退出登录
  logout() {
    return http({
      url: '/user/logout',
      method: 'delete'
    })
  },
  // 检查用户名是否可用
  hasUsername(data) {
    return http({
      url: '/user/has-username',
      method: 'get',
      params: data
    })
  },
  // 根据用户名查找用户信息
  queryUserInfo(data) {
    return http({
      url: '/actual/user/' + data,
      method: 'get'
    })
  },
  // 检查登录状态
  checkLogin(config = {}) {
    return http({
      url: '/user/check-login',
      method: 'get',
      timeout: 2000,
      ...config
    })
  },
  // 根据用户名查询用户基本信息
  queryUserByUsername(data) {
    return http({
      url: '/user/' + data,
      method: 'get'
    })
  }
}
