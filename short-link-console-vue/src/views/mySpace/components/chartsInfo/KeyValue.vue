<template>
  <div class="main">
    <div v-if="!dataLists || dataLists.length === 0" class="empty-hint">所选日期内没有访问数据</div>
    <div class="flex-box">
      <div class="flex-item" v-for="item in dataLists" :key="item.ip">
        <span class="key-text">{{ item.ip }}</span>
        <span>{{ item.cnt }} 次</span>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  dataLists: {
    type: Array,
    // eslint-disable-next-line vue/require-valid-default-prop
    default: []
  }
})

</script>

<style lang="scss" scoped>
.main {
  padding: 20px;
  .empty-hint {
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: 160px;
    color: #ababab;
    font-size: 13px;
  }
  .flex-box {
    display: flex;
    flex-direction: column;
    .flex-item {
      display: flex;
      margin-bottom: 5px;
      justify-content: space-between;
      .key-text {
        color: #3464e0;
      }
    }
  }
}
</style>
