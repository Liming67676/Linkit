<template>
  <el-drawer
    v-model="drawerVisible"
    direction="rtl"
    size="880px"
    :show-close="false"
    @opened="handleOpened"
    @close="handleClose"
  >
    <template #header>
      <div class="stats-drawer-header">
        <div class="stats-drawer-title">
          <img v-if="!isGroup" :src="getImgUrl(props.favicon)" width="28" height="28" class="stats-favicon" alt=""
            referrerpolicy="no-referrer" @error="onFaviconError" />
          <div class="stats-title-text">
            <span class="stats-title">统计报表 - {{ props.title }}</span>
            <span v-if="!isGroup" class="stats-links">
              <a class="stats-short-url" :href="normalizeUrl(props.fullShortUrl)" target="_blank" :title="props.fullShortUrl">{{ props.fullShortUrl }}</a>
              <span class="stats-link-sep">|</span>
              <span class="stats-origin-url" :title="props.originUrl">{{ props.originUrl }}</span>
            </span>
          </div>
        </div>
        <span v-if="isGroup" class="stats-group-nums">共{{ props.nums }}条短链接</span>
        <el-button text class="stats-close-btn" @click="handleClose">
          <Close class="el-icon" />
        </el-button>
      </div>
    </template>
    <div class="stats-body">
      <!-- 分享数据行 -->
      <div class="stats-share-bar">
        <div class="share-actions">
          <el-dropdown trigger="click">
            <el-button text class="share-btn">
              <Share class="el-icon" />分享数据
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="exportImage">
                  <Picture class="el-icon" /> 导出图片
                </el-dropdown-item>
                <el-dropdown-item @click="copyShareLink">
                  <Link class="el-icon" /> 链接分享
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-tooltip content="刷新数据" placement="top">
            <el-button text class="share-btn" @click="refreshData">
              <Refresh class="el-icon" />刷新数据
            </el-button>
          </el-tooltip>
        </div>
      </div>
      <!-- Tabs + 日期选择器在同一行 -->
      <div class="stats-tabs-row">
        <el-tabs v-model="showPane" class="stats-tabs">
          <el-tab-pane name="访问数据" label="访问数据" />
          <el-tab-pane name="历史记录" label="历史记录" />
        </el-tabs>
        <el-date-picker v-model="dateValue" type="daterange" range-separator="至" start-placeholder="开始时间"
          end-placeholder="结束时间" value-format="YYYY-MM-DD" :shortcuts="shortcuts" size="small" />
      </div>
      <!-- 内容区 -->
      <div class="stats-content-area">
        <div v-show="showPane === '访问数据'" class="stats-scroll-area">
          <!-- 概览卡片 -->
          <div class="stats-overview">
            <div class="overview-card">
              <div class="overview-icon" style="background: #eef2ff; color: #6366f1">
                <TrendCharts class="el-icon" />
              </div>
              <div class="overview-info">
                <span class="overview-label">访问次数</span>
                <span class="overview-value">{{ totalPv }}</span>
              </div>
            </div>
            <div class="overview-card">
              <div class="overview-icon" style="background: #ecfeff; color: #0ea5e9">
                <User class="el-icon" />
              </div>
              <div class="overview-info">
                <span class="overview-label">访问人数</span>
                <span class="overview-value">{{ totalUv }}</span>
              </div>
            </div>
            <div class="overview-card">
              <div class="overview-icon" style="background: #fef2f2; color: #ef4444">
                <Monitor class="el-icon" />
              </div>
              <div class="overview-info">
                <span class="overview-label">访问IP数</span>
                <span class="overview-value">{{ totalUip }}</span>
              </div>
            </div>
          </div>

          <!-- 行1: 访问趋势 -->
          <div class="stats-card">
            <div class="stats-card-header">
              <span class="stats-card-title">访问趋势</span>
            </div>
            <div class="stats-card-body" style="height: 360px">
              <div class="lineChart" style="width: 100%; height: 100%"></div>
            </div>
          </div>

          <!-- 行2: 地区分布 -->
          <div class="stats-card map-card">
            <div class="stats-card-header">
              <span class="stats-card-title">地区分布 TOP 10 省份</span>
            </div>
            <div class="stats-card-body">
              <div v-show="isChina" class="top10-list">
                <div v-if="!chinaMapData || chinaMapData.length === 0" class="top10-empty">所选日期内没有访问数据</div>
                <div class="top10-item" v-for="(item, index) in chinaMapData" :key="item.name">
                  <div v-if="index <= 9" class="key-value">
                    <span class="kv-rank">{{ index + 1 }}</span>
                    <span class="kv-name">{{ item.name }}</span>
                    <span class="kv-bar"><span class="kv-bar-fill" :style="{ width: clampPct(item.ratio) + '%' }"></span></span>
                    <span class="kv-ratio">{{ clampPct(item.ratio).toFixed(2) }}%</span>
                    <span class="kv-count">{{ item.value }}次</span>
                  </div>
                </div>
              </div>
              <div v-show="isChina" class="chinaMap"></div>
            </div>
          </div>

          <!-- 行3: 24小时分布 -->
          <div class="stats-card">
            <div class="stats-card-header">
              <span class="stats-card-title">24小时分布</span>
            </div>
            <div class="stats-card-body">
              <BarChart style="height: 240px" :chartData="{
                xAxis: [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23],
                value: props.info?.hourStats || new Array(24).fill(0)
              }"></BarChart>
            </div>
          </div>

          <!-- 行4: 高频IP + 一周分布 -->
          <div class="stats-row-pair">
            <div class="stats-card">
              <div class="stats-card-header">
                <span class="stats-card-title">高频访问IP</span>
              </div>
              <div class="stats-card-body">
                <KeyValue :dataLists="props.info?.topIpStats" style="min-height: 200px"></KeyValue>
              </div>
            </div>
            <div class="stats-card">
              <div class="stats-card-header">
                <span class="stats-card-title">一周分布</span>
              </div>
              <div class="stats-card-body">
                <BarChart style="height: 200px" :chartData="{
                  xAxis: ['周一','周二','周三','周四','周五','周六','周日'],
                  value: props.info?.weekdayStats || new Array(7).fill(0)
                }"></BarChart>
              </div>
            </div>
          </div>

          <!-- 行5: 操作系统 + 访问浏览器 -->
          <div class="stats-row-pair">
            <div class="stats-card">
              <div class="stats-card-header">
                <span class="stats-card-title">操作系统</span>
              </div>
              <div class="stats-card-body">
                <ProgressLine style="min-height: 200px" :dataLists="props.info?.osStats"></ProgressLine>
              </div>
            </div>
            <div class="stats-card">
              <div class="stats-card-header">
                <span class="stats-card-title">访问浏览器</span>
              </div>
              <div class="stats-card-body">
                <ProgressLine style="min-height: 200px" :dataLists="props.info?.browserStats"></ProgressLine>
              </div>
            </div>
          </div>

          <!-- 行6: 访客类型 + 访问网络 + 访问设备（环形饼图） -->
          <div class="stats-row-triple">
            <div class="stats-card" v-if="!isGroup">
              <div class="stats-card-header">
                <span class="stats-card-title">访客类型</span>
              </div>
              <div class="stats-card-body stats-pie-body">
                <div class="pie-chart" ref="pieUserTypeRef"></div>
                <div class="pie-legend">
                  <div class="legend-item" v-for="item in userTypeLegend" :key="item.name">
                    <span class="legend-dot" :style="{ background: item.color }"></span>
                    <span class="legend-label">{{ item.name }}</span>
                    <span class="legend-value">{{ item.value }}</span>
                    <span class="legend-pct">{{ item.pct }}%</span>
                  </div>
                </div>
              </div>
            </div>
            <div class="stats-card">
              <div class="stats-card-header">
                <span class="stats-card-title">访问网络</span>
              </div>
              <div class="stats-card-body stats-pie-body">
                <div class="pie-chart" ref="pieNetworkRef"></div>
                <div class="pie-legend">
                  <div class="legend-item" v-for="item in networkLegend" :key="item.name">
                    <span class="legend-dot" :style="{ background: item.color }"></span>
                    <span class="legend-label">{{ item.name }}</span>
                    <span class="legend-value">{{ item.value }}</span>
                    <span class="legend-pct">{{ item.pct }}%</span>
                  </div>
                </div>
              </div>
            </div>
            <div class="stats-card">
              <div class="stats-card-header">
                <span class="stats-card-title">访问设备</span>
              </div>
              <div class="stats-card-body stats-pie-body">
                <div class="pie-chart" ref="pieDeviceRef"></div>
                <div class="pie-legend">
                  <div class="legend-item" v-for="item in deviceLegend" :key="item.name">
                    <span class="legend-dot" :style="{ background: item.color }"></span>
                    <span class="legend-label">{{ item.name }}</span>
                    <span class="legend-value">{{ item.value }}</span>
                    <span class="legend-pct">{{ item.pct }}%</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div v-show="showPane === '历史记录'" class="stats-scroll-area">
          <div class="stats-card">
            <div class="stats-card-header">
              <span class="stats-card-title">访问记录</span>
            </div>
            <div class="stats-card-body" style="padding: 0">
              <el-table ref="accessTableRef" :data="tableInfo?.data?.data?.records" style="width: 100%" :header-cell-style="{ background: '#f8f9fc', color: '#606266' }">
                <el-table-column prop="createTime" label="访问时间" width="160" />
                <el-table-column prop="ip" label="访问IP" width="140" />
                <el-table-column prop="locale" label="访客地区" />
                <el-table-column prop="device" label="设备信息">
                  <template #default="scope">
                    <div style="display: flex; gap: 4px">
                      <img :src="getUrl1(scope?.row?.browser)" width="20" alt="" />
                      <img :src="getUrl2(scope?.row?.os)" width="20" alt="" />
                      <img :src="getUrl3(scope?.row?.device)" width="20" alt="" />
                      <img :src="getUrl4(scope?.row?.network)" width="20" alt="" />
                    </div>
                  </template>
                </el-table-column>
                <el-table-column v-if="!isGroup" prop="uvType" label="访客类型" width="100" />
              </el-table>
            </div>
            <div class="stats-card-footer">
              <el-pagination
                small
                :current-page="pageParams.current"
                :page-size="pageParams.size"
                :page-sizes="[10, 15, 20, 30]"
                layout="total, sizes, prev, pager, next"
                :total="totalNums"
                @size-change="onSizeChange"
                @current-change="onCurrentChange"
              ></el-pagination>
            </div>
          </div>
        </div>
      </div>
    </div>
  </el-drawer>
</template>

<script setup>
import { ref, watch, reactive, nextTick, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import html2canvas from 'html2canvas'
import TitleContent from './TitleContent.vue'
import * as echarts from 'echarts'
import 'echarts/map/js/china.js'
import 'echarts/map/js/world.js'
import BarChart from './BarChart.vue'
import KeyValue from './KeyValue.vue'
import ProgressLine from './ProgressLine.vue'
import edge from '@/assets/png/edge.png'
import Andriod from '@/assets/png/Andriod.png'
import Chorme from '@/assets/png/Chorme.png'
import firefox from '@/assets/png/firefox.png'
import iOS from '@/assets/png/iOS.png'
import macOS from '@/assets/png/macOS.png'
import other from '@/assets/png/other.png'
import Safair from '@/assets/png/Safair.png'
import WeChat from '@/assets/png/WeChat.png'
import Windows from '@/assets/png/Windows.png'
import linux from '@/assets/png/linux.png'
import wifi from '@/assets/png/wifi.png'
import PC from '@/assets/png/电脑.png'
import Mobile from '@/assets/png/移动设备.png'
import MobileDevices from '@/assets/png/移动设备.png'
import defaultImg from '@/assets/png/短链默认图标.png'
import opera from '@/assets/png/opera.png'
import IE from '@/assets/png/IE.png'
import { getTodayFormatDate, getLastWeekFormatDate } from '@/utils/plugins.js'

// 选择时间
const shortcuts = [
  {
    text: '今天',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 0)
      return [start, end]
    }
  },
  {
    text: '昨天',
    value: () => {
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 1)
      return [start, start]
    }
  },
  {
    text: '近七天',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
      return [start, end]
    }
  },
  {
    text: '近三十天',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
      return [start, end]
    }
  }
]
const getImgUrl = (url) => {
  return url ?? defaultImg
}
// 第三方 favicon 可能 403（防盗链），加载失败回落到默认图标
const onFaviconError = (e) => {
  const img = e.target
  if (!img || img.dataset.fallback) return
  img.dataset.fallback = '1'
  img.src = defaultImg
}
const dailyXAxis = ref()
// const dailyXAxis = ref([])
const showPane = ref('访问数据')
const accessTableRef = ref()
// 浏览器
const getUrl1 = (img) => {
  if (img) {
    img = img.toLowerCase()
  }
  if (img?.includes('edge')) {
    return edge
  } else if (img?.includes('chrome')) {
    return Chorme
  } else if (img?.includes('fire')) {
    return firefox
  } else if (img?.includes('safari')) {
    return Safair
  } else if (img?.includes('wechat') || img?.includes('微信')) {
    return WeChat
  } else if (img?.includes('opera')) {
    return opera
  }
  else if (img?.includes('internet')) {
    return IE
  }
  else {
    return other
  }
}
// 操作系统
const getUrl2 = (img) => {
  if (img) {
    img = img.toLowerCase()
  }
  if (img?.includes('andriod')) {
    return Andriod
  } else if (img?.includes('ios')) {
    return iOS
  } else if (img?.includes('mac')) {
    return macOS
  } else if (img?.includes('windows')) {
    return Windows
  } else if (img?.includes('linux')) {
    return linux
  } else {
    return other
  }
}
// 访问设备（pc或者移动设备）
const getUrl3 = (img) => {
  if (img) {
    img = img.toLowerCase()
  }
  if (img?.includes('pc')) {
    return PC
  } else {
    return Mobile
  }
}
// 访问网络（wifi和移动网络）
const getUrl4 = (img) => {
  if (img) {
    img = img.toLowerCase()
  }
  if (img?.includes('Mobile')) {
    return MobileDevices
  } else {
    return wifi
  }
}
const dateValue = ref([getLastWeekFormatDate(), getTodayFormatDate()])
const emit = defineEmits(['changeTime', 'changePage', 'loadAccessRecords'])
watch(
  () => dateValue.value,
  (newValue) => {
    if (!drawerVisible.value) {
      return
    }
    emit('changeTime', newValue)
    if (showPane.value === '历史记录') {
      emit('loadAccessRecords')
    }
  }
)
const props = defineProps({
  title: {
    type: String,
    default: '默认标题'
  },
  info: Object,
  tableInfo: Object,
  isGroup: Boolean,
  nums: Number,
  favicon: String,
  originUrl: String,
  fullShortUrl: String
})
const pageParams = reactive({
  current: 1,
  size: 10
})
const totalNums = ref(0)
const onSizeChange = (val) => {
  pageParams.size = val
  pageParams.current = 1
}
const onCurrentChange = (val) => {
  pageParams.current = val
}
watch(
  () => props.tableInfo,
  () => {
    totalNums.value = props?.tableInfo?.data?.data?.total
  }
)
watch(
  () => pageParams,
  (newValue) => {
    // 解决首次关闭数据统计页面需要点两次关闭键的bug
    if (!newValue && !drawerVisible.value) {
      return
    }
    emit('changePage', newValue)
  },
  {
    deep: true
  }
)
watch(
  () => showPane.value,
  (newValue) => {
    if (newValue === '历史记录') {
      emit('loadAccessRecords')
      // 面板从 v-show 隐藏切到显示时表格宽度还是 0，需要重新布局，否则整张表不可见
      nextTick(() => accessTableRef.value?.doLayout?.())
    }
  }
)
// const title = ref(props.title)
// const info = ref(props.info)

// watch(
//   () => {
//     props.info
//   },
//   () => {
//     console.log(props.info, props.title)
//     info.value = props.info
//     title.value = props.title
//   },
//   {
//     deep: true,
//   }
// )
const drawerVisible = ref(false)
const handleClose = () => {
  dateValue.value = null
  drawerVisible.value = false
  showPane.value = '访问数据'
  dateValue.value = [getLastWeekFormatDate(), getTodayFormatDate()]
}
const isVisible = () => {
  drawerVisible.value = true
}
const unVisible = () => {
  drawerVisible.value = false
}
defineExpose({
  unVisible,
  isVisible
})

// 中国地图中展示的数据
const chinaMapData = ref([
  // {
  //   name: '北京',
  //   value: 0
  // }
])
// 后端返回的 ratio 已经是 0-100 的百分比，前端不再二次乘 100，仅做边界兜底
const clampPct = (v) => Math.min(Math.max(Number(v) || 0, 0), 100)
// 归一化省份名，对齐 ECharts china 地图命名（去掉省/市/自治区/特别行政区等后缀）
const normalizeProvince = (name) =>
  String(name || '').replace(/(维吾尔自治区|壮族自治区|回族自治区|特别行政区|自治区|省|市)$/, '')
// 中国地图中的总次数
const chinaTotalNum = ref(0)
// 将请求到的数据转化为中国地图中需要的数据结构
watch(
  () => props.info?.localeCnStats,
  () => {
    chinaTotalNum.value = 0
    chinaMapData.value = (props.info?.localeCnStats || []).map((item) => {
      const { cnt, locale, ratio } = item
      chinaTotalNum.value += cnt
      return { name: normalizeProvince(locale), value: cnt, ratio }
    })
    initChinaMap()
  },
  {
    deep: true
  }
)
// 世界地图中展示的数据
const worldMapData = ref([
  {
    name: '中国',
    value: 28397.812
  }
])
const isChina = ref(true)
const initChinaMap = () => {
  // 中国地图
  const chinaMapDom = document.querySelector('.chinaMap')
  // 抽屉首次打开时内容区可能还没渲染，取不到容器就直接返回，避免 init 抛错中断后续图表
  if (!chinaMapDom) return
  const chinaMap = keepChart(echarts.getInstanceByDom(chinaMapDom) || echarts.init(chinaMapDom))
  const option = {
    tooltip: {
      formatter: function (params) {
        if (params.value) {
          return params.name + ' : ' + params.value
        } else {
          return params.name + ' : ' + '0'
        }
      }
    },
    visualMap: {
      min: 0,
      max: chinaTotalNum.value,
      left: 'left',
      top: 'bottom',
      text: ['高', '低'],
      calculable: false,
      orient: 'horizontal',
      inRange: {
        // 最小值到最大值的颜色
        color: ['#e0ffff', '#006edd'],
        symbolSize: [30, 100]
      }
    },
    series: {
      type: 'map',
      map: 'china',
      itemStyle: {
        // 鼠标移入后的样式
        emphasis: {
          // 鼠标移入后的颜色
          areaColor: '#78dffc'
        }
      },
      data: chinaMapData.value
    }
  }
  chinaMap.setOption(option)
}
// 世界地图默认展示英文国家名，在echarts的options中配置nameMap进行转化
let nameMap = {
  Afghanistan: '阿富汗',
  Singapore: '新加坡',
  Angola: '安哥拉',
  Albania: '阿尔巴尼亚',
  'United Arab Emirates': '阿联酋',
  Argentina: '阿根廷',
  Armenia: '亚美尼亚',
  'French Southern and Antarctic Lands': '法属南半球和南极领地',
  Australia: '澳大利亚',
  Austria: '奥地利',
  Azerbaijan: '阿塞拜疆',
  Burundi: '布隆迪',
  Belgium: '比利时',
  Benin: '贝宁',
  'Burkina Faso': '布基纳法索',
  Bangladesh: '孟加拉国',
  Bulgaria: '保加利亚',
  'The Bahamas': '巴哈马',
  'Bosnia and Herzegovina': '波斯尼亚和黑塞哥维那',
  Belarus: '白俄罗斯',
  Belize: '伯利兹',
  Bermuda: '百慕大',
  Bolivia: '玻利维亚',
  Brazil: '巴西',
  Brunei: '文莱',
  Bhutan: '不丹',
  Botswana: '博茨瓦纳',
  'Central African Republic': '中非共和国',
  Canada: '加拿大',
  Switzerland: '瑞士',
  Chile: '智利',
  China: '中国',
  'Ivory Coast': '象牙海岸',
  Cameroon: '喀麦隆',
  'Democratic Republic of the Congo': '刚果民主共和国',
  'Republic of the Congo': '刚果共和国',
  Colombia: '哥伦比亚',
  'Costa Rica': '哥斯达黎加',
  Cuba: '古巴',
  'Northern Cyprus': '北塞浦路斯',
  Cyprus: '塞浦路斯',
  'Czech Republic': '捷克共和国',
  Germany: '德国',
  Djibouti: '吉布提',
  Denmark: '丹麦',
  'Dominican Republic': '多明尼加共和国',
  Algeria: '阿尔及利亚',
  Ecuador: '厄瓜多尔',
  Egypt: '埃及',
  Eritrea: '厄立特里亚',
  Spain: '西班牙',
  Estonia: '爱沙尼亚',
  Ethiopia: '埃塞俄比亚',
  Finland: '芬兰',
  Fiji: '斐',
  'Falkland Islands': '福克兰群岛',
  France: '法国',
  Gabon: '加蓬',
  'United Kingdom': '英国',
  Georgia: '格鲁吉亚',
  Ghana: '加纳',
  Guinea: '几内亚',
  Gambia: '冈比亚',
  'Guinea Bissau': '几内亚比绍',
  Greece: '希腊',
  Greenland: '格陵兰',
  Guatemala: '危地马拉',
  'French Guiana': '法属圭亚那',
  Guyana: '圭亚那',
  Honduras: '洪都拉斯',
  Croatia: '克罗地亚',
  Haiti: '海地',
  Hungary: '匈牙利',
  Indonesia: '印度尼西亚',
  India: '印度',
  Ireland: '爱尔兰',
  Iran: '伊朗',
  Iraq: '伊拉克',
  Iceland: '冰岛',
  Israel: '以色列',
  Italy: '意大利',
  Jamaica: '牙买加',
  Jordan: '约旦',
  Japan: '日本',
  Kazakhstan: '哈萨克斯坦',
  Kenya: '肯尼亚',
  Kyrgyzstan: '吉尔吉斯斯坦',
  Cambodia: '柬埔寨',
  Kosovo: '科索沃',
  Kuwait: '科威特',
  Laos: '老挝',
  Lebanon: '黎巴嫩',
  Liberia: '利比里亚',
  Libya: '利比亚',
  'Sri Lanka': '斯里兰卡',
  Lesotho: '莱索托',
  Lithuania: '立陶宛',
  Luxembourg: '卢森堡',
  Latvia: '拉脱维亚',
  Morocco: '摩洛哥',
  Moldova: '摩尔多瓦',
  Madagascar: '马达加斯加',
  Mexico: '墨西哥',
  Macedonia: '马其顿',
  Mali: '马里',
  Myanmar: '缅甸',
  Montenegro: '黑山',
  Mongolia: '蒙古',
  Mozambique: '莫桑比克',
  Mauritania: '毛里塔尼亚',
  Malawi: '马拉维',
  Malaysia: '马来西亚',
  Namibia: '纳米比亚',
  'New Caledonia': '新喀里多尼亚',
  Niger: '尼日尔',
  Nigeria: '尼日利亚',
  Nicaragua: '尼加拉瓜',
  Netherlands: '荷兰',
  Norway: '挪威',
  Nepal: '尼泊尔',
  'New Zealand': '新西兰',
  Oman: '阿曼',
  Pakistan: '巴基斯坦',
  Panama: '巴拿马',
  Peru: '秘鲁',
  Philippines: '菲律宾',
  'Papua New Guinea': '巴布亚新几内亚',
  Poland: '波兰',
  'Puerto Rico': '波多黎各',
  'North Korea': '北朝鲜',
  Portugal: '葡萄牙',
  Paraguay: '巴拉圭',
  Qatar: '卡塔尔',
  Romania: '罗马尼亚',
  Russia: '俄罗斯',
  Rwanda: '卢旺达',
  'Western Sahara': '西撒哈拉',
  'Saudi Arabia': '沙特阿拉伯',
  Sudan: '苏丹',
  'South Sudan': '南苏丹',
  Senegal: '塞内加尔',
  'Solomon Islands': '所罗门群岛',
  'Sierra Leone': '塞拉利昂',
  'El Salvador': '萨尔瓦多',
  Somaliland: '索马里兰',
  Somalia: '索马里',
  'Republic of Serbia': '塞尔维亚',
  Suriname: '苏里南',
  Slovakia: '斯洛伐克',
  Slovenia: '斯洛文尼亚',
  Sweden: '瑞典',
  Swaziland: '斯威士兰',
  Syria: '叙利亚',
  Chad: '乍得',
  Togo: '多哥',
  Thailand: '泰国',
  Tajikistan: '塔吉克斯坦',
  Turkmenistan: '土库曼斯坦',
  'East Timor': '东帝汶',
  'Trinidad and Tobago': '特里尼达和多巴哥',
  Tunisia: '突尼斯',
  Turkey: '土耳其',
  'United Republic of Tanzania': '坦桑尼亚',
  Uganda: '乌干达',
  Ukraine: '乌克兰',
  Uruguay: '乌拉圭',
  'United States': '美国',
  Uzbekistan: '乌兹别克斯坦',
  Venezuela: '委内瑞拉',
  Vietnam: '越南',
  Vanuatu: '瓦努阿图',
  'West Bank': '西岸',
  Yemen: '也门',
  'South Africa': '南非',
  Zambia: '赞比亚',
  Korea: '韩国',
  Tanzania: '坦桑尼亚',
  Zimbabwe: '津巴布韦',
  Congo: '刚果',
  'Central African Rep.': '中非',
  Serbia: '塞尔维亚',
  'Bosnia and Herz.': '波黑',
  'Czech Rep.': '捷克',
  'W. Sahara': '西撒哈拉',
  'Lao PDR': '老挝',
  'Dem.Rep.Korea': '朝鲜',
  'Falkland Is.': '福克兰群岛',
  'Timor-Leste': '东帝汶',
  'Solomon Is.': '所罗门群岛',
  Palestine: '巴勒斯坦',
  'N. Cyprus': '北塞浦路斯',
  Aland: '奥兰群岛',
  'Fr. S. Antarctic Lands': '法属南半球和南极陆地',
  Mauritius: '毛里求斯',
  Comoros: '科摩罗',
  'Eq. Guinea': '赤道几内亚',
  'Guinea-Bissau': '几内亚比绍',
  'Dominican Rep.': '多米尼加',
  'Saint Lucia': '圣卢西亚',
  Dominica: '多米尼克',
  'Antigua and Barb.': '安提瓜和巴布达',
  'U.S. Virgin Is.': '美国原始岛屿',
  Montserrat: '蒙塞拉特',
  Grenada: '格林纳达',
  Barbados: '巴巴多斯',
  Samoa: '萨摩亚',
  Bahamas: '巴哈马',
  'Cayman Is.': '开曼群岛',
  'Faeroe Is.': '法罗群岛',
  'IsIe of Man': '马恩岛',
  Malta: '马耳他共和国',
  Jersey: '泽西',
  'Cape Verde': '佛得角共和国',
  'Turks and Caicos Is.': '特克斯和凯科斯群岛',
  'St. Vin. and Gren.': '圣文森特和格林纳丁斯'
}
const initWorldMap = () => {
  // 世界地图
  const worldMapDom = document.querySelector('.worldMap')
  if (!worldMapDom) return
  const worldMap = keepChart(echarts.getInstanceByDom(worldMapDom) || echarts.init(worldMapDom))
  const option = {
    tooltip: {
      formatter: '{b0}: {c0}'
    },
    nameMap,
    visualMap: {
      min: 0,
      max: 1000,
      left: 'left',
      top: 'bottom',
      text: ['高', '低'],
      calculable: false,
      orient: 'horizontal',
      inRange: {
        // 最小值到最大值的颜色
        color: ['#e0ffff', '#006edd'],
        symbolSize: [30, 100]
      }
    },
    series: {
      name: '数量',
      type: 'map',
      map: 'world',
      itemStyle: {
        // 鼠标移入后的样式
        emphasis: {
          // 鼠标移入后的颜色
          areaColor: '#78dffc'
        }
      },
      data: worldMapData.value
    }
  }
  worldMap.setOption(option)
}
const initMap = () => {
  initChinaMap()
  initWorldMap()
}
// 访问次数相关
const isLine = ref(true)
// 访问曲线数据请求
// 图表无数据时的统一占位提示
const EMPTY_TEXT = '所选日期内没有访问数据'
const emptyGraphic = (show) =>
  show
    ? [
        {
          type: 'text',
          left: 'center',
          top: 'middle',
          silent: true,
          style: { text: EMPTY_TEXT, fill: '#ababab', fontSize: 13 }
        }
      ]
    : []
// 统一登记 ECharts 实例：复用已有实例，卸载时统一释放，避免反复 init 造成内存泄漏
const chartInstances = new Set()
const keepChart = (chart) => {
  if (chart) chartInstances.add(chart)
  return chart
}
// 窗口尺寸变化时统一重绘（原实现没有任何 resize 处理，窗口缩放后图表会错位）
let resizeTimer = null
const handleResize = () => {
  clearTimeout(resizeTimer)
  resizeTimer = setTimeout(() => {
    chartInstances.forEach((chart) => {
      if (chart && !chart.isDisposed()) chart.resize()
    })
  }, 150)
}
onMounted(() => window.addEventListener('resize', handleResize))
onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  clearTimeout(resizeTimer)
  chartInstances.forEach((chart) => {
    if (chart && !chart.isDisposed()) chart.dispose()
  })
  chartInstances.clear()
})
const initLineChart = () => {
  const lineChartDom = document.querySelector('.lineChart')
  if (!lineChartDom) return
  // 切换日期区间会反复触发重绘，复用已有实例避免重复 init
  const lineChart = keepChart(echarts.getInstanceByDom(lineChartDom) || echarts.init(lineChartDom))
  const isEmpty = !pvList.value.length || totalPv.value === 0
  let option = {
    title: {
      show: false,
      text: 'Stacked Line'
    },
    graphic: emptyGraphic(isEmpty),
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['访问次数', '访问人数', '访问IP数']
    },
    grid: {
      left: '3%',
      right: '9%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dailyXAxis.value
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '访问次数',
        type: 'line',
        data: pvList.value
      },
      {
        name: '访问人数',
        type: 'line',
        data: uvList.value
      },
      {
        name: '访问IP数',
        type: 'line',
        data: uipList.value
      }
    ]
  }
  // 必须用 notMerge：setOption 默认是合并，上一次画上去的"无数据"文案不会被清掉
  lineChart.setOption(option, true)
}
// 总访问次数
const totalPv = ref(0)
// 访问次数数据集数组
const pvList = ref([])
// 总访问人数
const totalUv = ref(0)
// 访问人数数据集数组
const uvList = ref([])
// 总访问IP数
const totalUip = ref(0)
// 访问IP数数据集数组
const uipList = ref([])
// 更新数据
watch(
  () => props?.info?.daily,
  () => {
    // 归零
    totalPv.value = 0
    totalUv.value = 0
    totalUip.value = 0
    pvList.value = []
    uvList.value = []
    uipList.value = []
    dailyXAxis.value = []
    visitsData.value = props?.info?.daily
    // 获取总数量和数据集数组
    visitsData?.value?.forEach((item) => {
      const { pv, uv, uip, date } = item
      const formDate = date.split('-')[1] + '月' + date.split('-')[2] + '日'
      totalPv.value += pv
      totalUv.value += uv
      totalUip.value += uip
      pvList.value.push(pv)
      uvList.value.push(uv)
      uipList.value.push(uip)
      dailyXAxis.value.push(formDate)
    })
    initLineChart()
  }
)
// 访问表格数据
const visitsData = ref()
// 访问设备和访问网络
const userTypeList = ref([0, 0])
const deviceList = ref([0, 0])
const deviceLabels = ref(['电脑', '移动设备'])
const netWorkList = ref([0, 0])
const networkLabels = ref(['WIFI', '移动数据'])
// 后端返回的是多分类明细（PC/手机/平板、WIFI/4G/5G），饼图按界面口径归并；
// 归并不进去的值单独成一片，保证各片之和与总访问次数一致
const groupCounts = (list, key, groups) => {
  const data = groups.map(() => 0)
  let other = 0
  ;(list || []).forEach((item) => {
    const name = String(item?.[key] ?? '')
    const cnt = Number(item?.cnt) || 0
    const idx = groups.findIndex((names) => names.includes(name))
    if (idx >= 0) {
      data[idx] += cnt
    } else {
      other += cnt
    }
  })
  return { data, other }
}
watch(
  () => props.info?.uvTypeStats,
  () => {
    // 初始化
    userTypeList.value = [0, 0]
    // 对访问用户类型的数据进行转化
    props.info?.uvTypeStats?.forEach((item) => {
      if (item.uvType === 'newUser') {
        userTypeList.value[0] = item.cnt
      } else if (item.uvType === 'oldUser') {
        userTypeList.value[1] = item.cnt
      }
    })
  },
  {
    immediate: true
  }
)
watch(
  () => props.info?.deviceStats,
  () => {
    const { data, other } = groupCounts(props.info?.deviceStats, 'device', [
      ['PC', '电脑'],
      ['手机', '平板', 'Mobile', 'Tablet']
    ])
    const labels = ['电脑', '移动设备']
    if (other > 0) {
      data.push(other)
      labels.push('未知设备')
    }
    deviceList.value = data
    deviceLabels.value = labels
  },
  {
    immediate: true
  }
)
watch(
  () => props.info?.networkStats,
  () => {
    const { data, other } = groupCounts(props.info?.networkStats, 'network', [
      ['WIFI'],
      ['4G', '5G', '3G', '2G', 'Mobile']
    ])
    const labels = ['WIFI', '移动数据']
    if (other > 0) {
      data.push(other)
      labels.push('未知网络')
    }
    netWorkList.value = data
    networkLabels.value = labels
  },
  {
    immediate: true
  }
)
// 环形饼图
const pieUserTypeRef = ref()
const pieNetworkRef = ref()
const pieDeviceRef = ref()
const PIE_COLORS = ['#6366f1', '#0ea5e9', '#10b981', '#f59e0b', '#ef4444']
const userTypeLegend = ref([])
const networkLegend = ref([])
const deviceLegend = ref([])

const initPieChart = (dom, data, labels) => {
  if (!dom) return
  const sum = data.reduce((a, b) => a + (Number(b) || 0), 0)
  // 复用实例，切换日期区间时不重复 init
  const chart = keepChart(echarts.getInstanceByDom(dom) || echarts.init(dom))
  chart.setOption(
    {
      graphic: emptyGraphic(sum === 0),
      tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
      color: PIE_COLORS,
      series: [
        {
          type: 'pie',
          radius: ['50%', '75%'],
          avoidLabelOverlap: false,
          label: { show: false },
          emphasis: {
            label: { show: true, fontSize: 14, fontWeight: 'bold' },
            itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,0.5)' }
          },
          data: labels.map((name, i) => ({ name, value: data[i] || 0 }))
        }
      ]
    },
    true
  )
  return chart
}

// 抽屉展开动画结束后统一补渲染一次：内容区在动画期间宽度接近 0，
// 此时初始化的图表会得到 0×0 画布（有数据也看不见），这里统一重绘 + resize
const handleOpened = () => {
  nextTick(() => {
    initLineChart()
    if (isChina.value) {
      initChinaMap()
    } else {
      initWorldMap()
    }
    initPieChart(pieUserTypeRef.value, userTypeList.value, ['新访客', '旧访客'])
    initPieChart(pieNetworkRef.value, netWorkList.value, networkLabels.value)
    initPieChart(pieDeviceRef.value, deviceList.value, deviceLabels.value)
    chartInstances.forEach((chart) => {
      if (chart && !chart.isDisposed()) chart.resize()
    })
  })
}

watch(
  () => [userTypeList.value, drawerVisible.value],
  () => {
    if (!drawerVisible.value || !pieUserTypeRef.value) return
    const labels = ['新访客', '旧访客']
    const data = userTypeList.value
    const total = data.reduce((a, b) => a + b, 0) || 1
    userTypeLegend.value = labels.map((name, i) => ({
      name, value: data[i], pct: ((data[i] / total) * 100).toFixed(1), color: PIE_COLORS[i]
    }))
    initPieChart(pieUserTypeRef.value, data, labels)
  },
  { immediate: true }
)
watch(
  () => [netWorkList.value, networkLabels.value, drawerVisible.value],
  () => {
    if (!drawerVisible.value || !pieNetworkRef.value) return
    const labels = networkLabels.value
    const data = netWorkList.value
    const total = data.reduce((a, b) => a + b, 0) || 1
    networkLegend.value = labels.map((name, i) => ({
      name, value: data[i], pct: ((data[i] / total) * 100).toFixed(1), color: PIE_COLORS[i]
    }))
    initPieChart(pieNetworkRef.value, data, labels)
  },
  { immediate: true }
)
watch(
  () => [deviceList.value, deviceLabels.value, drawerVisible.value],
  () => {
    if (!drawerVisible.value || !pieDeviceRef.value) return
    const labels = deviceLabels.value
    const data = deviceList.value
    const total = data.reduce((a, b) => a + b, 0) || 1
    deviceLegend.value = labels.map((name, i) => ({
      name, value: data[i], pct: ((data[i] / total) * 100).toFixed(1), color: PIE_COLORS[i]
    }))
    initPieChart(pieDeviceRef.value, data, labels)
  },
  { immediate: true }
)

// 分享功能
const normalizeUrl = (url) => {
  if (!url) return ''
  return url.startsWith('http://') || url.startsWith('https://') ? url : 'https://' + url
}

const exportImage = async () => {
  const scrollEl = document.querySelector('.stats-scroll-area:not([style*="display: none"])')
  if (!scrollEl) {
    ElMessage.warning('未找到可导出的内容')
    return
  }
  ElMessage.info('正在生成报表图片...')
  const originalOverflow = scrollEl.style.overflow
  const originalHeight = scrollEl.style.height
  scrollEl.style.overflow = 'visible'
  scrollEl.style.height = 'auto'
  await nextTick()
  let wrapper = null
  try {
    wrapper = document.createElement('div')
    wrapper.style.cssText = 'padding:24px;background:#f8f9fc;font-family:system-ui,sans-serif;'
    const headerEl = document.createElement('div')
    headerEl.style.cssText = 'margin-bottom:20px;padding:0 4px;'
    const titleEl = document.createElement('div')
    titleEl.style.cssText = 'font-size:18px;font-weight:700;color:#1d2129;margin-bottom:8px;'
    titleEl.textContent = `统计报表 - ${props.title || ''}`
    headerEl.appendChild(titleEl)
    if (!props.isGroup && props.fullShortUrl) {
      const shortEl = document.createElement('div')
      shortEl.style.cssText = 'font-size:13px;color:#3464e0;font-weight:500;margin-bottom:4px;'
      shortEl.textContent = props.fullShortUrl
      headerEl.appendChild(shortEl)
    }
    if (!props.isGroup && props.originUrl) {
      const originEl = document.createElement('div')
      originEl.style.cssText = 'font-size:12px;color:#86909c;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;max-width:800px;'
      originEl.textContent = props.originUrl
      headerEl.appendChild(originEl)
    }
    wrapper.appendChild(headerEl)
    const clone = scrollEl.cloneNode(true)
    clone.style.cssText = 'overflow:visible;height:auto;'
    wrapper.appendChild(clone)
    document.body.appendChild(wrapper)
    const canvas = await html2canvas(wrapper, {
      backgroundColor: '#f8f9fc',
      scale: 2,
      useCORS: true,
      logging: false
    })
    if (document.body.contains(wrapper)) document.body.removeChild(wrapper)
    scrollEl.style.overflow = originalOverflow
    scrollEl.style.height = originalHeight
    const link = document.createElement('a')
    link.download = `统计报表_${props.title || '数据'}_${Date.now()}.png`
    link.href = canvas.toDataURL('image/png')
    link.click()
    ElMessage.success('报表图片已导出')
  } catch (e) {
    if (document.body.contains(wrapper)) document.body.removeChild(wrapper)
    scrollEl.style.overflow = originalOverflow
    scrollEl.style.height = originalHeight
    ElMessage.error('导出失败，请重试')
    console.error(e)
  }
}

const copyShareLink = () => {
  const shortUrl = props.fullShortUrl || ''
  navigator.clipboard.writeText(shortUrl).then(() => {
    ElMessage.success('短链接已复制')
  }).catch(() => {
    ElMessage.warning('复制失败，请手动复制')
  })
}

const refreshData = () => {
  const val = dateValue.value
  if (val && val[0] && val[1]) {
    const cleanDates = [
      val[0].split(' ')[0],
      val[1].split(' ')[0]
    ]
    emit('changeTime', cleanDates)
    ElMessage.success('数据已刷新')
  } else {
    ElMessage.warning('请先选择日期范围')
  }
}
</script>

<style lang="less" scoped>
.stats-body {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.stats-drawer-header {
  display: flex;
  align-items: center;
  width: 100%;
  gap: 12px;
}

.stats-drawer-title {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  min-width: 0;
}

.stats-favicon {
  border-radius: 6px;
  flex-shrink: 0;
}

.stats-title-text {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.stats-title {
  font-size: 18px;
  font-weight: 700;
  color: #1d2129;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.stats-origin-url {
  font-size: 12px;
  color: #86909c;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 260px;
}

.stats-links {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 2px;
  margin-top: 2px;
  font-size: 12px;
  line-height: 1.5;
}

.stats-link-sep {
  display: none;
}

.stats-short-url {
  color: #3464e0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 200px;
  font-weight: 500;
  text-decoration: none;
  cursor: pointer;

  &:hover {
    text-decoration: underline;
    color: #4f7df3;
  }
}

.stats-group-nums {
  color: #86909c;
  font-size: 13px;
  flex-shrink: 0;
}

.stats-close-btn {
  font-size: 20px;
  color: #86909c;
  flex-shrink: 0;
}

// Tabs 行（tabs + 日期在一行）
.stats-tabs-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 0;
  border-bottom: 1px solid #e5e6eb;

  .stats-tabs {
    flex: 1;
    overflow: hidden;

    :deep(.el-tabs__header) {
      margin: 0;
    }

    :deep(.el-tabs__nav-wrap::after) {
      display: none;
    }
  }

  :deep(.el-date-editor--daterange.el-input__inner) {
    width: 260px;
  }
}

// 内容区（带底部间距）
.stats-content-area {
  flex: 1;
  overflow: hidden;
  position: relative;
  padding-bottom: 24px;

  .stats-scroll-area {
    height: 100%;
    overflow-y: auto;
    padding: 16px 4px 32px 0;

    > .stats-card,
    > .stats-row-pair,
    > .stats-row-triple {
      margin-bottom: 20px;
    }

    > .stats-overview {
      margin-bottom: 24px;
    }

    > .stats-row-pair,
    > .stats-row-triple {
      margin-top: 0;
    }
  }
}
.stats-share-bar {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  margin-bottom: 0;
  padding: 6px 0;
}

.share-actions {
  display: flex;
  align-items: center;
  gap: 4px;
}

.share-btn {
  font-size: 13px;
  color: #4e5969;
  padding: 4px 10px;
  border-radius: 6px;

  &:hover {
    color: #3464e0;
    background: #eef1fb;
  }

  .el-icon {
    font-size: 16px;
    margin-right: 2px;
  }
}

// 布局行
.stats-row-pair {
  display: flex;
  gap: 16px;

  > .stats-card {
    flex: 1;
    min-width: 0;
  }
}

.stats-row-triple {
  display: flex;
  gap: 16px;

  > .stats-card {
    flex: 1;
    min-width: 0;
  }
}

.stats-overview {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.overview-card {
  flex: 1;
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  border: 1px solid #e5e6eb;
  transition: box-shadow 0.2s, transform 0.2s;

  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    transform: translateY(-2px);
  }
}

.overview-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;

  .el-icon {
    font-size: 24px;
  }
}

.overview-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.overview-label {
  font-size: 13px;
  color: #86909c;
}

.overview-value {
  font-size: 28px;
  font-weight: 700;
  color: #1d2129;
  font-variant-numeric: tabular-nums;
  line-height: 1.2;
}

// 卡片
.stats-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  border: 1px solid #e5e6eb;
  overflow: hidden;
  transition: box-shadow 0.2s;

  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  }
}

.stats-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 20px;
  border-bottom: 1px solid #f0f1f3;
}

.stats-card-title {
  font-size: 14px;
  font-weight: 600;
  color: #1d2129;
}

.stats-card-body {
  padding: 16px 20px;
}

.stats-card-footer {
  padding: 12px 20px;
  border-top: 1px solid #f0f1f3;
  display: flex;
  justify-content: flex-end;
}

// 访问趋势
.lineChart {
  width: 100%;
  height: 320px;
}

// 双栏网格
.stats-grid {
  display: flex;
  gap: 16px;
  margin-top: 16px;
}

.stats-grid-col {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

// 四列网格
.stats-grid-4 {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-top: 16px;
}

// 地图卡片
.map-card {
  .stats-card-body {
    display: flex;
    gap: 16px;
  }
}

.chinaMap {
  width: 280px;
  height: 200px;
  flex-shrink: 0;
}

// TOP10 列表
.top10-list {
  flex: 1;
  min-width: 0;
}

.top10-title {
  font-size: 13px;
  font-weight: 500;
  color: #4e5969;
  margin-bottom: 8px;
  display: block;
}

.top10-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 200px;
  font-size: 13px;
  color: #ababab;
}

.top10-item {
  .key-value {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 5px 0;
    font-size: 13px;
  }
}

.kv-rank {
  width: 18px;
  height: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  background: #f0f2f5;
  font-size: 11px;
  font-weight: 600;
  color: #4e5969;
  flex-shrink: 0;
}

.kv-name {
  width: 60px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #4e5969;
  flex-shrink: 0;
}

.kv-bar {
  flex: 1;
  height: 6px;
  background: #f0f2f5;
  border-radius: 3px;
  overflow: hidden;
}

.kv-bar-fill {
  display: block;
  height: 100%;
  background: linear-gradient(90deg, #3464e0, #78dffc);
  border-radius: 3px;
  transition: width 0.6s ease;
}

.kv-ratio {
  width: 55px;
  text-align: right;
  color: #3464e0;
  font-weight: 500;
  flex-shrink: 0;
}

.kv-count {
  width: 55px;
  text-align: right;
  color: #86909c;
  flex-shrink: 0;
}

// 历史记录表格
:deep(.el-table) {
  --el-table-border-color: transparent;

  th.el-table__cell {
    border-bottom: 1px solid #e5e6eb;
  }

  td.el-table__cell {
    border-bottom: 1px solid #f0f1f3;
  }

  .el-table__body tr:hover > td.el-table__cell {
    background: #f8f9fc;
  }
}

// 环形饼图
.stats-pie-body {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pie-chart {
  width: 110px;
  height: 110px;
  flex-shrink: 0;
}

.pie-legend {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
}

.legend-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.legend-label {
  color: #4e5969;
  min-width: 50px;
}

.legend-value {
  color: #1d2129;
  font-weight: 600;
  font-variant-numeric: tabular-nums;
}

.legend-pct {
  color: #86909c;
  margin-left: auto;
}
</style>
