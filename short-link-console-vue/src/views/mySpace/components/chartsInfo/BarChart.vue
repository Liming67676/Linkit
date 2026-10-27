<template>
  <!-- 柱状图 -->
  <div ref="barChartRef">
  </div>
</template>

<script setup>
import * as echarts from 'echarts'
import { onMounted, onBeforeUnmount, nextTick, ref, watch } from 'vue'
const props = defineProps({
  chartData: {
    type: Object,
    // eslint-disable-next-line vue/require-valid-default-prop
    default: {
      xAxis: [1, 2, 3, 4, 5, 6, 7],
      value: [12, 12, 32, 32, 23, 23, 16]
    }
  }
})
watch(
  () => props.chartData,
  () => {
    initChart()
  }
)
const barChartRef = ref()
const initChart = () => {
  const barDom = barChartRef.value
  // 切换日期区间会反复触发重绘，复用已有实例避免重复 init 造成实例泄漏
  const barChart = echarts.getInstanceByDom(barDom) || echarts.init(barDom)
  const values = props.chartData?.value || []
  // 全为 0 也视为无数据，避免出现只有坐标轴的空图
  const isEmpty = !values.length || values.every((item) => !Number(item))
  const option = {
    graphic: isEmpty
      ? [
          {
            type: 'text',
            left: 'center',
            top: '50%',
            silent: true,
            style: { text: '所选日期内没有访问数据', fill: '#ababab', fontSize: 13 }
          }
        ]
      : [],
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      show: true,
      top: 0,
      right: 8,
      itemWidth: 10,
      itemHeight: 10,
      icon: 'roundRect',
      textStyle: {
        color: '#ababab',
        fontSize: 12
      },
      data: ['访问次数']
    },
    grid: {
      left: '5%',
      right: '5%',
      bottom: '5%',
      top: '20%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      axisLine: {
        show: false
      },
      axisTick: {
        show: false
      },
      axisLabel: {
        color: '#ababab'
      },
      data: props.chartData.xAxis
    },
    yAxis: {
      axisLabel: {
        color: '#ababab'
      },
      axisLine: {
        show: false
      },
      axisTick: {
        show: false
      },
      type: 'value'
    },
    series: [
      {
        name: '访问次数',
        data: props.chartData.value,
        type: 'bar',
        barWidth: '10px',
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#83bff6' },
            { offset: 0.5, color: '#188df0' },
            { offset: 1, color: '#188df0' }
          ])
        },
        // 浅灰色是柱子的底槽（背景轨道），只做视觉参考，不是数据系列，所以不进图例
        showBackground: true,
        backgroundStyle: {
          color: 'rgba(180, 180, 180, 0.2)'
        }
      }
    ]
  };
  barChart.setOption(option, true)
}
// 抽屉打开前容器宽度接近 0，图表按 0 尺寸 init 后会一直空白，
// 用 ResizeObserver 在容器真正有尺寸时补一次 resize
let resizeObserver = null
onMounted(() => {
  nextTick(() => {
    initChart()
    if (typeof ResizeObserver !== 'undefined' && barChartRef.value) {
      resizeObserver = new ResizeObserver(() => {
        const chart = echarts.getInstanceByDom(barChartRef.value)
        if (chart && !chart.isDisposed()) chart.resize()
      })
      resizeObserver.observe(barChartRef.value)
    }
  })
})
onBeforeUnmount(() => {
  resizeObserver?.disconnect()
  resizeObserver = null
})
</script>

<style lang="scss" scoped>
.barChart {
  width: 100%;
  height: 100%;
}
</style>
