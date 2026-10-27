import { fileURLToPath, URL } from 'node:url'
import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')

  return {
    plugins: [
      vue(),
      // Element Plus 按需引入：模板里用到哪个 el-* 组件、v-loading 指令就引哪个，
      // 连同对应样式一起注入，不再全量引入组件库和 338KB 的 index.css
      Components({
        resolvers: [ElementPlusResolver()],
        dts: false
      })
    ],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url))
      }
    },
    css: {
      preprocessorOptions: {
        scss: {
          // Vite 4 仍通过 sass 的 legacy JS API 编译，sass 1.80+ 会打 deprecation 警告，
          // 这里显式静音；升级到 Vite 5（原生 modern API）后可移除
          silenceDeprecations: ['legacy-js-api']
        }
      }
    },
    server: {
      host: '0.0.0.0',
      proxy: {
        '/api': {
          target: env.VITE_PROXY_TARGET || 'http://127.0.0.1:8000',
          changeOrigin: true,
          rewrite: (path) => path
        }
      }
    },
    build: {
      // 地图数据本身较大，单块阈值放宽到 800KB
      chunkSizeWarningLimit: 800,
      rollupOptions: {
        output: {
          // 手动分包：框架、UI 库、图表库各自成块，改业务代码时用户不必重新下载整包
          manualChunks(id) {
            if (!id.includes('node_modules')) {
              return
            }
            if (id.includes('echarts') || id.includes('zrender')) {
              return 'echarts'
            }
            if (id.includes('html2canvas')) {
              return 'html2canvas'
            }
            if (id.includes('element-plus') || id.includes('@element-plus')) {
              return 'element-plus'
            }
            if (
              id.includes('vue-router') ||
              id.includes('vuex') ||
              id.includes('@vue/') ||
              /[\\/]node_modules[\\/]vue[\\/]/.test(id)
            ) {
              return 'vue'
            }
            return 'vendor'
          }
        }
      }
    }
  }
})
