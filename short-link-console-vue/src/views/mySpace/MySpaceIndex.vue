<template>
  <div style="display: flex; height: 100%">
    <div class="options-box">
      <!-- 分组标题 -->
      <div class="option-title">
        <div class="title-left">
          <FolderOpened class="el-icon title-icon" />
          <span>短链分组</span>
          <span class="group-count">共{{ editableTabs?.length }}组</span>
        </div>
        <el-tooltip content="新增分组" placement="right">
          <div class="add-group-btn" @click="showAddGroup">
            <Plus class="el-icon" />
          </div>
        </el-tooltip>
      </div>
      <!-- 分组列表 -->
      <ul class="sortOptions">
        <li v-for="(item, index) in editableTabs" :key="item.gid">
          <div class="item-box" :class="{ selectedItem: selectedIndex === index }"
            @click="changeSelectIndex(index)">
            <div class="item-left">
              <img src="@/assets/svg/移动竖.svg" width="12" class="drag-icon" alt="" />
              <Link class="el-icon group-icon" />
              <span class="over-text">{{ item.name }}</span>
            </div>
            <div class="item-right">
              <el-tooltip show-after="500" content="查看图表" placement="left">
                <span>
                  <Histogram v-if="item.shortLinkCount !== 0 && item.shortLinkCount !== null"
                    class="el-icon action-icon"
                    @click.stop="chartsVisible({ describe: item.name, gid: item.gid, group: true })" />
                </span>
              </el-tooltip>
              <el-dropdown trigger="click" @command="(cmd) => handleGroupAction(cmd, item)">
                <MoreFilled class="el-icon action-icon more-btn" />
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="edit">
                      <Edit class="el-icon" />编辑
                    </el-dropdown-item>
                    <el-dropdown-item command="delete" divided>
                      <Remove class="el-icon" />删除分组
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
              <span class="item-count">{{ item.shortLinkCount ?? 0 }}</span>
            </div>
          </div>
        </li>
      </ul>
      <!-- 回收站 -->
      <div class="recycle-section">
        <div class="recycle-bin-item" :class="{ 'is-active': selectedIndex === -1 }" @click="recycleBin">
          <div class="recycle-left">
            <i class="iconfont icon-huishouzhan recycle-icon"></i>
            <span>回收站</span>
          </div>
          <span v-if="recycleBinNums > 0" class="recycle-badge">{{ recycleBinNums }}</span>
        </div>
      </div>
      <!-- 用户信息（页面左下角） -->
      <div class="user-footer">
        <div class="user-avatar">
          {{ userInitial }}
        </div>
        <div class="user-info-text">
          <span class="user-name">{{ userName }}</span>
          <span class="user-actions">
            <el-button text size="small" @click="showProfileDrawer">个人信息</el-button>
            <el-divider direction="vertical" />
            <el-button text size="small" @click="showLogoutDrawer">退出</el-button>
          </span>
        </div>
      </div>
    </div>
    <!-- 主要数据展示区域 -->
    <div class="content-panel">
      <div class="table-card">
        <!-- 工具栏 -->
        <div v-if="!isRecycleBin" class="toolbar">
          <div class="toolbar-left">
            <div class="quick-url-bar">
              <div class="quick-url-input-wrap">
                <el-input
                  v-model="quickUrl"
                  placeholder="请输入 http:// 或 https:// 开头的链接，按 Enter 快速创建"
                  size="default"
                  @keyup.enter="submitQuickUrl"
                ></el-input>
              </div>
              <el-button type="primary" :loading="quickUrlLoading" @click="submitQuickUrl">
                <Plus class="el-icon" />创建短链
              </el-button>
            </div>
            <el-button class="btn-secondary" @click="showAddLinkDrawerBatch">
              <Connection class="el-icon" />批量创建
            </el-button>
          </div>
        </div>
        <div v-else class="recycle-header">
          <i class="iconfont icon-huishouzhan recycle-icon"></i>
          <span class="recycle-title">回收站</span>
          <span class="recycle-count">共{{ recycleBinNums }}条短链接</span>
        </div>
        <!-- 表格 -->
        <el-table :data="tableData" height="calc(100vh - 280px)" style="width: 100%"
          :header-cell-style="{ background: '#fafbfc', color: '#606266', fontWeight: 600 }"
          row-key="id" stripe>
          <template #empty>
            <div class="empty-state">
              <Link class="el-icon empty-icon" />
              <span>暂无链接</span>
            </div>
          </template>
          <el-table-column type="selection" width="40" />
          <el-table-column label="短链接信息" min-width="280">
            <template #header>
              <el-dropdown v-if="!isRecycleBin">
                <span class="column-header">
                  短链接信息
                  <CaretBottom class="el-icon" />
                </span>
                <template #dropdown>
                  <el-dropdown-item @click="pageParams.orderTag = null, orderIndex = 0">创建时间</el-dropdown-item>
                </template>
              </el-dropdown>
              <span v-else class="column-header">短链接信息</span>
            </template>
            <template #default="scope">
              <div class="link-info-cell" :class="{ expired: scope?.row?.validDateType === 1 && !isExpire(scope?.row?.validDate) }">
                <img :src="getImgUrl(scope.row.favicon)" :key="scope?.row?.id" width="20" height="20" class="favicon" alt="" />
                <div class="link-meta">
                  <el-tooltip show-after="500" :content="scope.row.describe">
                    <span class="link-title">{{ scope.row.describe }}</span>
                  </el-tooltip>
                  <div class="link-time">
                    <span>{{ scope.row.createTime }}</span>
                    <el-tooltip show-after="500" v-if="scope?.row?.validDate" :content="'到期时间：' + scope?.row?.validDate">
                      <img v-if="isExpire(scope?.row?.validDate)" width="16" src="@/assets/png/沙漏倒计时.png" class="expire-icon" alt="" />
                      <span v-else class="expired-tag">已失效</span>
                    </el-tooltip>
                  </div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="短链接网址" min-width="320">
            <template #default="scope">
              <div class="url-cell">
                <div class="url-top">
                    <el-tooltip content="复制链接" placement="top">
                      <span>
                        <CopyDocument class="el-icon copy-btn" @click="copyUrl(normalizeUrl(scope.row.fullShortUrl))" />
                      </span>
                    </el-tooltip>
                  <el-link type="primary" :underline="false" target="_blank"
                    :disabled="scope?.row?.validDateType === 1 && !isExpire(scope?.row?.validDate)"
                    :href="normalizeUrl(scope.row.fullShortUrl)" class="short-url">
                    {{ scope.row.domain + '/' + scope.row.shortUri }}
                  </el-link>
                </div>
                <el-tooltip show-after="500" :content="scope.row.originUrl">
                  <span class="origin-url">{{ scope.row.originUrl }}</span>
                </el-tooltip>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180">
            <template #default="scope">
              <div class="action-cell">
                <el-tooltip content="查看二维码" placement="top">
                  <span><i class="iconfont icon-erweima action-btn qr-icon" @click="showQrCode(scope.row)"></i></span>
                </el-tooltip>
                <template v-if="selectedIndex !== -1">
                  <el-tooltip content="查看统计" placement="top">
                    <span><i class="iconfont icon-tongji action-btn chart-icon" @click="chartsVisible(scope.row)"></i></span>
                  </el-tooltip>
                  <el-tooltip content="编辑" placement="top">
                    <span><i class="iconfont icon-icon action-btn edit-icon" @click="editLink(scope.row)"></i></span>
                  </el-tooltip>
                  <el-tooltip content="移入回收站" placement="top">
                    <span><i class="iconfont icon-shanchu action-btn delete-icon" @click="showDeleteDrawer(scope.row, 'recycle')"></i></span>
                  </el-tooltip>
                </template>
                <template v-else>
                  <el-tooltip content="恢复" placement="top">
                    <span>
                      <i class="iconfont icon-huifu action-btn recover-icon" @click="recoverLink(scope.row)"></i>
                    </span>
                  </el-tooltip>
                  <el-tooltip content="彻底删除" placement="top">
                    <span><i class="iconfont icon-shanchu action-btn delete-icon" @click="showDeleteDrawer(scope.row, 'permanent')"></i></span>
                  </el-tooltip>
                </template>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="访问次数" width="130" align="center">
            <template #header>
              <el-dropdown v-if="!isRecycleBin">
                <span class="column-header">
                  访问次数
                  <CaretBottom class="el-icon" />
                </span>
                <template #dropdown>
                  <el-dropdown-item @click="pageParams.orderTag = 'todayPv', orderIndex = 1">今日访问次数</el-dropdown-item>
                  <el-dropdown-item @click="pageParams.orderTag = 'totalPv', orderIndex = 1">累计访问次数</el-dropdown-item>
                </template>
              </el-dropdown>
              <span v-else class="column-header">访问次数</span>
            </template>
            <template #default="scope">
              <div class="stat-cell">
                <div class="stat-row today">
                  <span class="stat-label">今日</span>
                  <span class="stat-value">{{ scope.row.todayPv }}</span>
                </div>
                <div class="stat-row total">
                  <span class="stat-label">累计</span>
                  <span class="stat-value">{{ scope.row.totalPv }}</span>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="访问人数" width="130" align="center">
            <template #header>
              <el-dropdown v-if="!isRecycleBin">
                <span class="column-header">
                  访问人数
                  <CaretBottom class="el-icon" />
                </span>
                <template #dropdown>
                  <el-dropdown-item @click="pageParams.orderTag = 'todayUv', orderIndex = 2">今日访问人数</el-dropdown-item>
                  <el-dropdown-item @click="pageParams.orderTag = 'totalUv', orderIndex = 2">累计访问人数</el-dropdown-item>
                </template>
              </el-dropdown>
              <span v-else class="column-header">访问人数</span>
            </template>
            <template #default="scope">
              <div class="stat-cell">
                <div class="stat-row today">
                  <span class="stat-label">今日</span>
                  <span class="stat-value">{{ scope.row.todayUv }}</span>
                </div>
                <div class="stat-row total">
                  <span class="stat-label">累计</span>
                  <span class="stat-value">{{ scope.row.totalUv }}</span>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="IP数" width="130" align="center">
            <template #header>
              <el-dropdown v-if="!isRecycleBin">
                <span class="column-header">
                  IP数
                  <CaretBottom class="el-icon" />
                </span>
                <template #dropdown>
                  <el-dropdown-item @click="pageParams.orderTag = 'todayUip', orderIndex = 3">今日IP数</el-dropdown-item>
                  <el-dropdown-item @click="pageParams.orderTag = 'totalUip', orderIndex = 3">累计IP数</el-dropdown-item>
                </template>
              </el-dropdown>
              <span v-else class="column-header">IP数</span>
            </template>
            <template #default="scope">
              <div class="stat-cell">
                <div class="stat-row today">
                  <span class="stat-label">今日</span>
                  <span class="stat-value">{{ scope.row.todayUip }}</span>
                </div>
                <div class="stat-row total">
                  <span class="stat-label">累计</span>
                  <span class="stat-value">{{ scope.row.totalUip }}</span>
                </div>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <!-- 分页 -->
        <div class="pagination-wrapper">
          <el-pagination
            :current-page="pageParams.current"
            :page-size="pageParams.size"
            :page-sizes="[10, 15, 20, 30]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="totalNums"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            background
          ></el-pagination>
        </div>
      </div>
    </div>
    <!-- 弹窗 -->
    <ChartsInfo ref="chartsInfoRef" :title="chartsInfoTitle" :info="chartsInfo"
      :tableInfo="tableInfo" :isGroup="isGroup" :nums="nums" :favicon="favicon1" :originUrl="originUrl1"
      :fullShortUrl="tableFullShortUrl"
      @changeTime="changeTime" @changePage="changePage" @loadAccessRecords="loadAccessRecords"></ChartsInfo>
    <!-- 右侧新建分组抽屉 -->
    <el-drawer
      v-model="isAddGroup"
      direction="rtl"
      size="420px"
      :show-close="false"
      @close="isAddGroup = false"
    >
      <template #header>
        <div class="drawer-header">
          <span class="drawer-title">新建短链接分组</span>
        </div>
      </template>
      <div class="drawer-step2">
        <el-form @submit.prevent="addGroup">
          <el-form-item label="分组名称">
            <el-input autocomplete="off" v-model="newGroupName" placeholder="请输入分组名称" @keyup.enter="addGroup" />
          </el-form-item>
        </el-form>
        <div style="display: flex; justify-content: flex-end; gap: 12px; margin-top: 24px;">
          <el-button @click="isAddGroup = false">取消</el-button>
          <el-button type="primary" @click="addGroup" :loading="addGroupLoading">确认</el-button>
        </div>
      </div>
    </el-drawer>
    <!-- 右侧编辑分组抽屉 -->
    <el-drawer
      v-model="isEditGroup"
      direction="rtl"
      size="420px"
      :show-close="false"
      @close="isEditGroup = false"
    >
      <template #header>
        <div class="drawer-header">
          <span class="drawer-title">编辑短链接分组</span>
        </div>
      </template>
      <div class="drawer-step2">
        <el-form @submit.prevent="editGroup">
          <el-form-item label="分组名称">
            <el-input autocomplete="off" v-model="editGroupName" @keyup.enter="editGroup" />
          </el-form-item>
        </el-form>
        <div style="display: flex; justify-content: flex-end; gap: 12px; margin-top: 24px;">
          <el-button @click="isEditGroup = false">取消</el-button>
          <el-button type="primary" @click="editGroup" :loading="editGroupLoading">确认</el-button>
        </div>
      </div>
    </el-drawer>
    <!-- 右侧创建短链抽屉 -->
    <el-drawer
      v-model="isAddLinkDrawer"
      title="创建短链接"
      direction="rtl"
      size="520px"
      :close-on-click-modal="true"
      :show-close="false"
      @close="onAddLinkDrawerClose"
    >
      <template #header>
        <div class="drawer-header">
          <span class="drawer-title">创建短链接</span>
        </div>
      </template>
      <div class="drawer-step2">
        <CreateLink ref="createLink1Ref" :groupInfo="editableTabs" @onSubmit="onCreateLinkSubmit" @cancel="onCreateLinkCancel"
          :defaultGid="pageParams.gid" :is-single="true" :domain="selectedDomain"></CreateLink>
      </div>
    </el-drawer>
    <!-- 右侧批量创建抽屉 -->
    <el-drawer
      v-model="isAddLinkDrawerBatch"
      title="批量创建短链接"
      direction="rtl"
      size="520px"
      :close-on-click-modal="true"
      :show-close="false"
      @close="onBatchLinkCancel"
    >
      <template #header>
        <div class="drawer-header">
          <span class="drawer-title">批量创建短链接</span>
        </div>
      </template>
      <div class="drawer-step2">
        <CreateLinks ref="createLink2Ref" :groupInfo="editableTabs" @onSubmit="onBatchLinkSubmit" @cancel="onBatchLinkCancel"
          :defaultGid="pageParams.gid" :domain="selectedDomain"></CreateLinks>
      </div>
    </el-drawer>
    <!-- 右侧编辑短链抽屉 -->
    <el-drawer
      v-model="isEditLink"
      title="编辑短链"
      direction="rtl"
      size="520px"
      :show-close="false"
      @close="isEditLink = false"
    >
      <template #header>
        <div class="drawer-header">
          <span class="drawer-title">编辑短链</span>
        </div>
      </template>
      <div class="drawer-step2">
        <EditLink ref="editLinkRef" :groupInfo="editableTabs" :editData="editData" @onSubmit="coverEditLink"
          @cancel="coverEditLink" @updatePage="updatePage"></EditLink>
      </div>
    </el-drawer>
    <!-- 右侧二维码抽屉 -->
    <el-drawer
      v-model="qrCodeDrawer"
      direction="rtl"
      size="420px"
      :show-close="false"
      @close="qrCodeDrawer = false; qrCodeOssUrl = ''"
    >
      <template #header>
        <div class="drawer-header">
          <span class="drawer-title">二维码</span>
        </div>
      </template>
      <div class="drawer-step2" style="display: flex; flex-direction: column; align-items: center; gap: 24px; padding-top: 40px;">
        <div v-if="qrCodeLoading" style="padding: 60px 0; color: #86909c;">正在生成二维码...</div>
        <img v-else-if="qrCodeOssUrl" :src="qrCodeOssUrl" class="qr-code-img" alt="二维码" />
        <div v-else style="padding: 60px 0; color: #86909c;">点击二维码图标生成</div>
        <el-button type="primary" @click="downloadQrCode" :disabled="!qrCodeOssUrl">下载二维码</el-button>
      </div>
    </el-drawer>
    <!-- 右侧删除确认抽屉 -->
    <el-drawer
      v-model="deleteDrawer"
      direction="rtl"
      size="420px"
      :show-close="false"
      @close="deleteDrawer = false"
    >
      <template #header>
        <div class="drawer-header">
          <span class="drawer-title">{{ deleteDrawerMode === 'recycle' ? '移入回收站' : '彻底删除' }}</span>
        </div>
      </template>
      <div class="drawer-step2">
        <div style="padding: 16px 0;">
          <p style="font-size: 15px; color: #1d2129; margin-bottom: 8px;">
            {{ deleteDrawerMode === 'recycle' ? '确定移入回收站？' : '确定彻底删除？' }}
          </p>
          <p style="font-size: 13px; color: #86909c; line-height: 1.6;">
            <template v-if="deleteDrawerMode === 'recycle'">
              移入回收站后，短链接将停止访问，但仍可在回收站中恢复。
            </template>
            <template v-else>
              删除后短链跳转会失效，同时停止数据统计，此操作不可撤销。
            </template>
          </p>
        </div>
        <div style="display: flex; justify-content: flex-end; gap: 12px; margin-top: 24px;">
          <el-button @click="deleteDrawer = false">取消</el-button>
          <el-button type="primary" @click="confirmDelete" :loading="deleteLoading">
            {{ deleteDrawerMode === 'recycle' ? '移入回收站' : '确认删除' }}
          </el-button>
        </div>
      </div>
    </el-drawer>
    <!-- 右侧个人信息抽屉（查看/编辑双模式） -->
    <el-drawer
      v-model="profileDrawer"
      direction="rtl"
      size="420px"
      :show-close="false"
      @close="profileDrawer = false"
    >
      <template #header>
        <div class="drawer-header">
          <el-button v-if="profileMode === 'edit'" text class="drawer-back" @click="profileMode = 'view'">
            <ArrowLeft class="el-icon" />
          </el-button>
          <span class="drawer-title">{{ profileMode === 'view' ? '个人信息' : '编辑个人信息' }}</span>
        </div>
      </template>

      <!-- 查看模式 -->
      <template v-if="profileMode === 'view'">
        <div class="drawer-step2">
          <div v-if="profileInfo" class="profile-card">
            <div class="profile-avatar">
              {{ userInitial }}
            </div>
            <div class="profile-name">{{ profileInfo.username }}</div>
            <div class="profile-details">
              <div class="profile-row">
                <span class="profile-label">用户名称</span>
                <span class="profile-value">{{ profileInfo.username }}</span>
              </div>
              <div class="profile-row">
                <span class="profile-label">手机号</span>
                <span class="profile-value">{{ profileInfo.phone || '-' }}</span>
              </div>
              <div class="profile-row">
                <span class="profile-label">姓名</span>
                <span class="profile-value">{{ profileInfo.realName || '-' }}</span>
              </div>
              <div class="profile-row">
                <span class="profile-label">邮箱</span>
                <span class="profile-value">{{ profileInfo.mail || '-' }}</span>
              </div>
            </div>
            <el-button type="primary" style="width:100%;margin-top:20px;border-radius:10px;height:42px;font-size:14px;" @click="switchToEdit">
              <Edit class="el-icon" /> 编辑个人信息
            </el-button>
          </div>
          <div v-else style="text-align:center;padding:40px;color:#86909c">加载中...</div>
        </div>
      </template>

      <!-- 编辑模式 -->
      <template v-if="profileMode === 'edit'">
        <div class="drawer-step2">
          <div class="edit-profile-card">
            <el-form ref="editProfileFormRef" :model="editProfileForm" :rules="editProfileRules" label-width="0" status-icon>
              <div class="edit-field">
                <span class="edit-field-label">用户名称</span>
                <el-input v-model="editProfileForm.username" disabled />
              </div>
              <div class="edit-field">
                <span class="edit-field-label">手机号</span>
                <el-input v-model="editProfileForm.phone" placeholder="请输入手机号" maxlength="11" />
              </div>
              <div class="edit-field">
                <span class="edit-field-label">姓名</span>
                <el-input v-model="editProfileForm.realName" placeholder="请输入姓名" />
              </div>
              <div class="edit-field">
                <span class="edit-field-label">邮箱</span>
                <el-input v-model="editProfileForm.mail" placeholder="请输入邮箱" />
              </div>
              <div class="edit-field">
                <span class="edit-field-label">密码</span>
                <el-input v-model="editProfileForm.password" type="password" placeholder="如需修改密码请填写" show-password />
              </div>
            </el-form>
            <div style="display: flex; gap: 12px; margin-top: 28px;">
              <el-button style="flex:1;border-radius:10px;height:42px;font-size:14px;" @click="profileMode = 'view'">取消</el-button>
              <el-button type="primary" style="flex:1;border-radius:10px;height:42px;font-size:14px;" @click="submitEditProfile" :loading="editProfileLoading">保存</el-button>
            </div>
          </div>
        </div>
      </template>
    </el-drawer>
    <!-- 右侧退出确认抽屉 -->
    <el-drawer
      v-model="logoutDrawer"
      direction="rtl"
      size="420px"
      :show-close="false"
      @close="logoutDrawer = false"
    >
      <template #header>
        <div class="drawer-header">
          <span class="drawer-title">退出登录</span>
        </div>
      </template>
      <div class="drawer-step2" style="display: flex; flex-direction: column; align-items: center; padding-top: 32px;">
        <div class="logout-icon">
          <UserFilled class="el-icon" />
        </div>
        <p style="font-size: 17px; font-weight: 600; color: #1d2129; margin: 16px 0 8px;">确认退出登录？</p>
        <p style="font-size: 14px; color: #86909c; text-align: center; line-height: 1.6; max-width: 280px;">
          退出后需要重新登录<br />你的数据安全由我们保障，期待你再次回来
        </p>
        <div style="display: flex; flex-direction: column; gap: 12px; width: 100%; margin-top: 32px;">
          <el-button type="primary" style="border-radius: 10px; height: 44px; font-size: 15px;" @click="confirmLogout" :loading="logoutLoading">
            确认退出
          </el-button>
          <el-button style="border-radius: 10px; height: 44px; font-size: 15px;" @click="logoutDrawer = false">
            再想想
          </el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import Sortable from 'sortablejs'
import { cloneDeep } from 'lodash'
import { removeKey, removeUsername, getToken, getUsername } from '@/core/auth.js'
import ChartsInfo from './components/chartsInfo/ChartsInfo.vue'
import CreateLink from './components/createLink/CreateLink.vue'
import CreateLinks from './components/createLink/CreateLinks.vue'
import { getTodayFormatDate, getLastWeekFormatDate } from '@/utils/plugins.js'
import EditLink from './components/editLink/EditLink.vue'
import { ElMessage } from 'element-plus'
import defaultImg from '@/assets/png/短链默认图标.png'

const router = useRouter()
const nums = ref(0)
const favicon1 = ref()
const originUrl1 = ref()
const orderIndex = ref(0)
const { proxy } = getCurrentInstance()
const API = proxy.$API
const chartsInfoRef = ref()
const chartsInfoTitle = ref()
const chartsInfo = ref()
const tableInfo = ref()
const createLink1Ref = ref()
const createLink2Ref = ref()
let selectedIndex = ref(0)
const editableTabs = ref([])
const userName = ref('')
const userInitial = ref('')

// 个人信息抽屉（查看/编辑双模式）
const profileDrawer = ref(false)
const profileMode = ref('view')
const profileInfo = ref(null)
const showProfileDrawer = async () => {
  profileMode.value = 'view'
  profileDrawer.value = true
  try {
    const username = getUsername()
    const res = await API.user.queryUserByUsername(username)
    if (res?.data?.data) {
      profileInfo.value = res.data.data
    } else {
      profileInfo.value = { username: getUsername() || '', phone: '', realName: '', mail: '' }
    }
  } catch {
    profileInfo.value = { username: getUsername() || '', phone: '', realName: '', mail: '' }
  }
}
const switchToEdit = async () => {
  const username = getUsername()
  try {
    const res = await API.user.queryUserInfo(username)
    if (res?.data?.data) {
      editProfileForm.value = cloneDeep(res.data.data)
    } else {
      editProfileForm.value = cloneDeep(profileInfo.value || {})
    }
  } catch {
    editProfileForm.value = cloneDeep(profileInfo.value || {})
  }
  editProfileForm.value.password = ''
  profileMode.value = 'edit'
}
const editProfileForm = ref({})
const editProfileFormRef = ref()
const editProfileLoading = ref(false)
const editProfileRules = reactive({
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3|5|7|8|9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' },
    { min: 11, max: 11, message: '手机号必须是11位', trigger: 'blur' }
  ],
  mail: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { pattern: /^([a-zA-Z]|[0-9])(\w|\-)+@[a-zA-Z0-9]+\.([a-zA-Z]{2,4})$/, message: '请输入正确的邮箱', trigger: 'blur' }
  ],
  password: [
    { min: 8, max: 15, message: '密码长度请在8-15位之间', trigger: 'blur' }
  ]
})
const submitEditProfile = async () => {
  const formEl = editProfileFormRef.value
  if (!formEl) return
  formEl.validate(async (valid) => {
    if (!valid) return
    editProfileLoading.value = true
    try {
      const res = await API.user.editUser(editProfileForm.value)
      if (res?.data?.code !== '0') {
        ElMessage.error(res.data.message)
      } else {
        ElMessage.success('修改成功')
        const username = getUsername()
        const infoRes = await API.user.queryUserByUsername(username)
        profileInfo.value = infoRes?.data?.data || { username: getUsername() || '', phone: '', realName: '', mail: '' }
        profileMode.value = 'view'
      }
    } catch {
      ElMessage.error('修改失败，请重试')
    } finally {
      editProfileLoading.value = false
    }
  })
}

// 退出抽屉
const logoutDrawer = ref(false)
const logoutLoading = ref(false)
const showLogoutDrawer = () => {
  logoutDrawer.value = true
}
const confirmLogout = async () => {
  logoutLoading.value = true
  try {
    await API.user.logout()
    removeUsername()
    removeKey()
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    logoutDrawer.value = false
    ElMessage.success('已安全退出')
    router.push('/login')
  } catch {
    ElMessage.error('退出失败，请重试')
  } finally {
    logoutLoading.value = false
  }
}

const afterAddLink = () => {
  setTimeout(() => {
    getGroupInfo(queryPage)
  }, 0)
  if (editLinkRef.value) {
    editLinkRef.value.initFormData()
  }
}
const statsFormData = reactive({
  endDate: getTodayFormatDate(),
  startDate: getLastWeekFormatDate(),
  size: 10,
  current: 1
})
const initStatsFormData = () => {
  statsFormData.endDate = getTodayFormatDate()
  statsFormData.startDate = getLastWeekFormatDate()
  statsFormData.current = 1
  statsFormData.size = 10
}
const visitLink = {
  fullShortUrl: '',
  gid: '',
  enableStatus: null
}
const isGroup = ref(false)
const tableFullShortUrl = ref()
const tableGid = ref()
const chartsVisible = async (rowInfo, dateList) => {
  chartsInfoTitle.value = rowInfo?.describe
  const { fullShortUrl, gid, group, originUrl, favicon, enableStatus } = rowInfo
  originUrl1.value = originUrl
  favicon1.value = favicon
  isGroup.value = group
  tableFullShortUrl.value = fullShortUrl
  tableGid.value = gid
  visitLink.fullShortUrl = fullShortUrl
  visitLink.gid = gid
  visitLink.enableStatus = enableStatus
  chartsInfoRef?.value.isVisible()
  if (!dateList) {
    initStatsFormData()
  } else {
    statsFormData.startDate = dateList?.[0] + ' 00:00:00'
    statsFormData.endDate = dateList?.[1] + ' 23:59:59'
  }
  let res = null
  if (group) {
    res = await API.group.queryGroupStats({ startDate: statsFormData.startDate, endDate: statsFormData.endDate, gid })
  } else {
    res = await API.smallLinkPage.queryLinkStats({ startDate: statsFormData.startDate, endDate: statsFormData.endDate, fullShortUrl, gid, enableStatus })
  }
  chartsInfo.value = res?.data?.data
}
const changeTimeData = async (rowInfo, dateList) => {
  const { fullShortUrl, gid, enableStatus } = rowInfo
  if (!dateList) {
    initStatsFormData()
  } else {
    statsFormData.startDate = dateList?.[0] + ' 00:00:00'
    statsFormData.endDate = dateList?.[1] + ' 23:59:59'
  }
  let res = null
  if (isGroup.value) {
    res = await API.group.queryGroupStats({ startDate: statsFormData.startDate, endDate: statsFormData.endDate, gid })
  } else {
    res = await API.smallLinkPage.queryLinkStats({ startDate: statsFormData.startDate, endDate: statsFormData.endDate, fullShortUrl, gid, enableStatus })
  }
  chartsInfo.value = res?.data?.data
}
const changeTime = (dateList) => {
  changeTimeData(visitLink, dateList)
}
const changePage = async (page) => {
  const { current, size } = page
  statsFormData.current = current ?? 1
  statsFormData.size = size ?? 10
  let tableRes = null
  if (isGroup.value) {
    tableRes = await API.group.queryGroupTable({ gid: tableGid.value, ...statsFormData })
  } else {
    tableRes = await API.smallLinkPage.queryLinkTable({
      gid: tableGid.value,
      fullShortUrl: tableFullShortUrl.value,
      ...statsFormData
    })
  }
  tableInfo.value = tableRes
}
const loadAccessRecords = async () => {
  if (isGroup.value) {
    const res = await API.group.queryGroupTable({ gid: tableGid.value, ...statsFormData })
    tableInfo.value = res
  } else {
    const res = await API.smallLinkPage.queryLinkTable({
      gid: tableGid.value,
      fullShortUrl: tableFullShortUrl.value,
      ...statsFormData
    })
    tableInfo.value = res
  }
}
const handleGroupAction = (cmd, item) => {
  if (cmd === 'edit') {
    showEditGroup(item.gid, item.name)
  } else if (cmd === 'delete') {
    deleteGroup(item.gid)
  }
}
const transformGroupData = (oldIndex, newIndex) => {
  const formData = editableTabs.value
  const tempData = formData.splice(oldIndex, 1)
  formData.splice(newIndex, 0, tempData[0])
  formData.forEach((item, index) => {
    item.sortOrder = index
  })
  return formData
}
const initSortable = (className) => {
  const table = document.querySelector('.' + className)
  Sortable.create(table, {
    animation: 150,
    onStart: () => {},
    onEnd: async ({ to, from, oldIndex, newIndex, clone, pullMode }) => {
      if (newIndex !== oldIndex) {
        if (selectedIndex.value === oldIndex) {
          selectedIndex.value = newIndex
        } else if (oldIndex < newIndex && selectedIndex.value > oldIndex && selectedIndex.value <= newIndex) {
          selectedIndex.value--
        } else if (oldIndex > newIndex && selectedIndex.value < oldIndex && selectedIndex.value >= newIndex) {
          selectedIndex.value++
        }
        const formData = transformGroupData(oldIndex, newIndex)
        const res = await API.group.sortGroup(formData)
        if (!res?.data?.success) {
          ElMessage.error(res?.data?.message)
        }
      }
    }
  })
}
onMounted(async () => {
  const actualUsername = getUsername()
  userName.value = actualUsername
  userInitial.value = actualUsername?.charAt(0)?.toUpperCase() || 'U'
  nextTick(() => {
    initSortable('sortOptions')
  })
})

const tableData = ref([])
const pageParams = reactive({
  gid: null,
  current: 1,
  size: 10,
  orderTag: null
})
watch(
  () => pageParams.orderTag,
  () => {
    if (isRecycleBin.value) return
    queryPage()
  }
)
const totalNums = ref(0)
const queryPage = async () => {
  pageParams.gid = editableTabs.value?.[selectedIndex.value]?.gid
  nums.value = editableTabs.value?.[selectedIndex.value]?.shortLinkCount || 0
  const res = await API.smallLinkPage.queryPage(pageParams)
  if (res?.data.success) {
    tableData.value = res.data?.data?.records
    totalNums.value = +res.data?.data?.total
  } else {
    ElMessage.error(res?.data.message)
  }
}
const handleSizeChange = (val) => {
  pageParams.size = val
  !isRecycleBin.value ? queryPage() : queryRecycleBinPage()
}
const handleCurrentChange = (val) => {
  pageParams.current = val
  !isRecycleBin.value ? queryPage() : queryRecycleBinPage()
}
const getGroupInfo = async (fn) => {
  const res = await API.group.queryGroup()
  const groupList = res.data?.data?.reverse() || []
  editableTabs.value = groupList
  const gids = groupList.map(item => item.gid)
  if (gids.length > 0) {
    const countRes = await API.group.queryLinkCount({ requestParam: gids })
    if (countRes?.data?.success) {
      const countMap = {}
      ;(countRes.data.data || []).forEach(item => {
        countMap[item.gid] = item.shortLinkCount
      })
      editableTabs.value = groupList.map(item => ({
        ...item,
        shortLinkCount: countMap[item.gid] ?? item.shortLinkCount ?? 0
      }))
    }
  }
  fn && fn()
}
getGroupInfo(queryPage)
const updatePage = () => {
  getGroupInfo(queryPage)
}
const isRecycleBin = ref(false)
const recycleBinNums = ref(0)
const queryRecycleBinPage = () => {
  API.smallLinkPage
    .queryRecycleBin({ current: pageParams.current, size: pageParams.size })
    .then((res) => {
      tableData.value = res.data?.data?.records
      totalNums.value = +res.data?.data?.total
      recycleBinNums.value = totalNums.value
    })
}
const recycleBin = () => {
  isRecycleBin.value = true
  selectedIndex.value = -1
  pageParams.current = 1
  pageParams.size = 15
  pageParams.orderTag = null
  queryRecycleBinPage()
}
const changeSelectIndex = (index) => {
  selectedIndex.value = index
  isRecycleBin.value = false
  queryPage()
}
const isAddGroup = ref(false)
const newGroupName = ref()
const addGroupLoading = ref(false)
const showAddGroup = () => {
  newGroupName.value = ''
  isAddGroup.value = true
}
const addGroup = async () => {
  const name = newGroupName.value?.trim()
  if (!name) {
    ElMessage.warning('分组名称不能为空')
    return
  }
  addGroupLoading.value = true
  const res1 = await API.group.addGroup({ name })
  if (res1?.data.success) {
    ElMessage.success('添加成功')
    getGroupInfo(queryPage)
  } else {
    ElMessage.error(res1?.data.message)
  }
  isAddGroup.value = false
  addGroupLoading.value = false
}
const deleteGroup = async (gid) => {
  const res = await API.group.deleteGroup({ gid })
  selectedIndex.value = 0
  if (res.data.success) {
    ElMessage.success('删除成功')
    getGroupInfo(queryPage)
  } else {
    ElMessage.error(res.data.message)
  }
}
const isEditGroup = ref(false)
const editGroupName = ref()
const editGid = ref('')
const editGroupLoading = ref(false)
const showEditGroup = (gid, name) => {
  editGid.value = gid
  editGroupName.value = name
  isEditGroup.value = true
}
const editGroup = async () => {
  const name = editGroupName.value?.trim()
  if (!name) {
    ElMessage.warning('分组名称不能为空')
    return
  }
  editGroupLoading.value = true
  const res = await API.group.editGroup({ gid: editGid.value, name })
  if (res.data.success) {
    ElMessage.success('编辑成功')
    getGroupInfo(queryPage)
  } else {
    ElMessage.error('编辑失败')
  }
  isEditGroup.value = false
  editGroupLoading.value = false
}
const isAddLinkDrawer = ref(false)
const isAddLinkDrawerBatch = ref(false)
const selectedDomain = ref('lnko.cn')
const domainOptions = ['lnko.cn']
const quickUrl = ref('')
const quickUrlLoading = ref(false)
const quickUrlReg = /^https?:\/\/.+/
const submitQuickUrl = async () => {
  const url = quickUrl.value?.trim()
  if (!url) {
    ElMessage.warning('请输入以 http:// 或 https:// 开头的有效URL')
    return
  }
  if (!/^https?:\/\//.test(url)) {
    ElMessage.warning('请输入以 http:// 或 https:// 开头的有效URL')
    return
  }
  if (!quickUrlReg.test(url)) {
    ElMessage.warning('输入的链接格式无效，请检查后重试')
    return
  }
  quickUrlLoading.value = true
  try {
    const res = await API.smallLinkPage.queryTitle({ url: url })
    const title = res?.data?.data || ''
    quickUrlLoading.value = false
    quickUrl.value = ''
    isAddLinkDrawer.value = true
    nextTick(() => {
      createLink1Ref.value?.initWithUrl(url, title)
    })
  } catch {
    quickUrlLoading.value = false
    quickUrl.value = ''
    isAddLinkDrawer.value = true
    nextTick(() => {
      createLink1Ref.value?.initWithUrl(url, '')
    })
  }
}
const onAddLinkDrawerClose = () => {
  quickUrl.value = ''
  createLink1Ref.value?.initFormData()
}
const onCreateLinkSubmit = () => {
  isAddLinkDrawer.value = false
  createLink1Ref.value?.initFormData()
  getGroupInfo(queryPage)
}
const onCreateLinkCancel = () => {
  isAddLinkDrawer.value = false
  createLink1Ref.value?.initFormData()
}
const showAddLinkDrawerBatch = () => {
  isAddLinkDrawerBatch.value = true
}
const onBatchLinkSubmit = () => {
  isAddLinkDrawerBatch.value = false
  createLink2Ref.value?.initFormData()
  getGroupInfo(queryPage)
}
const onBatchLinkCancel = () => {
  isAddLinkDrawerBatch.value = false
  createLink2Ref.value?.initFormData()
}
const getImgUrl = (url) => {
  return url ?? defaultImg
}
const isExpire = (time) => {
  return new Date().getTime() < new Date(time).getTime()
}
let timer = ''
const copyUrl = (url) => {
  clearTimeout(timer)
  let eInput = document.createElement('input')
  eInput.value = url
  document.body.appendChild(eInput)
  eInput.select()
  let copyText = document.execCommand('Copy')
  eInput.style.display = 'none'
  if (copyText) {
    ElMessage.success('链接复制成功!')
  }
}

const normalizeUrl = (url) => {
  return url?.startsWith('http://') || url?.startsWith('https://') ? url : `https://${url}`
}
const isEditLink = ref(false)
const editLinkRef = ref()
const editData = ref()
const editLink = (data) => {
  editData.value = data
  isEditLink.value = true
}
const coverEditLink = () => {
  isEditLink.value = false
}

// 二维码抽屉
const qrCodeDrawer = ref(false)
const qrCodeOssUrl = ref('')
const qrCodeLoading = ref(false)
const qrCodeRow = ref(null)
const showQrCode = async (row) => {
  qrCodeRow.value = row
  qrCodeLoading.value = true
  qrCodeOssUrl.value = ''
  qrCodeDrawer.value = true
  try {
    const url = normalizeUrl(row.fullShortUrl)
    const res = await API.smallLinkPage.generateQrCode({ url, width: 300, height: 300 })
    if (res?.data?.success) {
      qrCodeOssUrl.value = res.data.data?.url
    } else {
      ElMessage.error(res?.data?.message || '生成二维码失败')
    }
  } catch {
    ElMessage.error('生成二维码失败')
  } finally {
    qrCodeLoading.value = false
  }
}
const downloadQrCode = () => {
  if (!qrCodeOssUrl.value) return
  const a = document.createElement('a')
  a.href = qrCodeOssUrl.value
  a.download = '二维码.png'
  a.target = '_blank'
  a.click()
  ElMessage.success('二维码已下载')
}

// 删除确认抽屉
const deleteDrawer = ref(false)
const deleteDrawerMode = ref('recycle')
const deleteTarget = ref(null)
const deleteLoading = ref(false)
const showDeleteDrawer = (row, mode) => {
  deleteTarget.value = row
  deleteDrawerMode.value = mode
  deleteDrawer.value = true
}
const confirmDelete = async () => {
  if (!deleteTarget.value) return
  deleteLoading.value = true
  const { gid, fullShortUrl } = deleteTarget.value
  if (deleteDrawerMode.value === 'recycle') {
    const res = await API.smallLinkPage.toRecycleBin({ gid, fullShortUrl })
    if (res?.data?.code !== '0') {
      ElMessage.error(res.data.message)
    } else {
      ElMessage.success('已移入回收站')
      getGroupInfo(queryPage)
    }
  } else {
    const res = await API.smallLinkPage.removeLink({ gid, fullShortUrl })
    if (res?.data?.code !== '0') {
      ElMessage.error(res.data.message)
    } else {
      ElMessage.success('已删除')
      queryRecycleBinPage()
    }
  }
  deleteLoading.value = false
  deleteDrawer.value = false
  deleteTarget.value = null
}

const toRecycleBin = (data) => {
  const { gid, fullShortUrl } = data
  API.smallLinkPage
    .toRecycleBin({ gid, fullShortUrl })
    .then((res) => {
      if (res?.data?.code !== '0') {
        ElMessage.error(res.data.message)
      } else {
        ElMessage.success('删除成功')
        getGroupInfo(queryPage)
      }
    })
    .catch(() => {
      ElMessage.error('删除失败')
    })
}
const recoverLink = (data) => {
  const { gid, fullShortUrl } = data
  API.smallLinkPage
    .recoverLink({ gid, fullShortUrl })
    .then((res) => {
      ElMessage.success('恢复成功')
      queryRecycleBinPage()
      getGroupInfo()
    })
    .catch(() => {
      ElMessage.error('恢复失败')
    })
}
const removeLink = (data) => {
  const { gid, fullShortUrl } = data
  API.smallLinkPage
    .removeLink({ gid, fullShortUrl })
    .then((res) => {
      if (res?.data?.code !== '0') {
        ElMessage.error(res.data.message)
      } else {
        ElMessage.success('删除成功')
        queryRecycleBinPage()
      }
    })
    .catch(() => {
      ElMessage.error('删除失败')
    })
}
</script>

<style lang="scss" scoped>
// 主色调
$primary: #3464e0;
$primary-light: #4f7df3;
$primary-bg: #eef1fb;
$sidebar-bg: #ffffff;
$content-bg: #f2f4f8;
$card-bg: #ffffff;
$text-primary: #1d2129;
$text-secondary: #4e5969;
$text-muted: #86909c;
$border-color: #e5e6eb;
$shadow-sm: 0 1px 3px rgba(0, 0, 0, 0.06);
$shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
$shadow-hover: 0 6px 20px rgba(0, 0, 0, 0.1);

// 左侧栏 - 卡片式
.options-box {
  display: flex;
  flex-direction: column;
  width: 240px;
  height: 100%;
  background: $sidebar-bg;
  border-right: 1px solid $border-color;
  position: relative;
  overflow: hidden;
}

.option-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
  padding: 0 18px;
  border-bottom: 1px solid $border-color;
  flex-shrink: 0;

  .title-left {
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .title-icon {
    font-size: 20px;
    color: $primary;
  }

  span {
    font-size: 16px;
    font-weight: 600;
    color: $text-primary;
  }

  .group-count {
    font-size: 12px;
    font-weight: 400;
    color: $text-muted;
    margin-left: 6px;
    background: #f2f4f8;
    padding: 2px 8px;
    border-radius: 10px;
  }

  .add-group-btn {
    width: 30px;
    height: 30px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 8px;
    cursor: pointer;
    color: $text-muted;
    transition: all 0.2s;

    &:hover {
      background: $primary-bg;
      color: $primary;
    }
  }
}

.sortOptions {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 10px 10px;
  margin: 0;
  list-style: none;

  li {
    padding: 0;
    margin-bottom: 2px;
  }
}

.item-box {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 44px;
  padding: 0 12px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;

  &:hover {
    background: #f5f7fa;

    .action-icon {
      opacity: 1;
    }

    .item-count {
      opacity: 0;
    }
  }

  &.selectedItem {
    background: $primary-bg;
    color: $primary;
    box-shadow: inset 3px 0 0 $primary;

    .item-left span {
      color: $primary;
      font-weight: 600;
    }

    .group-icon {
      color: $primary;
    }

    .item-count {
      color: $primary;
      font-weight: 600;
    }
  }
}

.item-left {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
  flex: 1;

  .drag-icon {
    flex-shrink: 0;
    opacity: 0.3;
  }

  .group-icon {
    font-size: 17px;
    color: $text-muted;
    flex-shrink: 0;
  }

  .over-text {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    font-size: 14px;
    color: $text-primary;
  }
}

.item-right {
  display: flex;
  align-items: center;
  gap: 4px;
  flex-shrink: 0;
}

.action-icon {
  font-size: 16px;
  color: $text-muted;
  opacity: 0;
  transition: all 0.2s;
  cursor: pointer;
  padding: 3px;
  border-radius: 6px;

  &:hover {
    color: $primary;
    background: rgba(52, 100, 224, 0.1);
  }
}

.more-btn {
  opacity: 0;
}

.item-count {
  font-size: 12px;
  color: $text-muted;
  min-width: 22px;
  text-align: right;
  font-weight: 500;
  transition: opacity 0.2s;
}

// 回收站 - 卡片式
.recycle-section {
  flex-shrink: 0;
  padding: 8px 10px 6px;
  border-top: 1px solid $border-color;
}

.recycle-bin-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 42px;
  padding: 0 12px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
  color: $text-secondary;
  font-size: 14px;

  &:hover {
    background: #f5f7fa;
    color: $primary;
  }

  &.is-active {
    background: $primary-bg;
    color: $primary;
    font-weight: 600;
    box-shadow: inset 3px 0 0 $primary;

    .recycle-icon {
      color: $primary;
    }
  }

  .recycle-left {
    display: flex;
    align-items: center;
    gap: 10px;

    .recycle-icon {
      font-size: 17px;
      color: $text-muted;
    }
  }

  .recycle-badge {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    min-width: 22px;
    height: 22px;
    padding: 0 7px;
    background: #f0f2f5;
    border-radius: 11px;
    font-size: 11px;
    font-weight: 600;
    color: $text-muted;
  }

  &.is-active .recycle-badge {
    background: #dce3f8;
    color: $primary;
  }
}

// 用户信息页脚
.user-footer {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 16px;
  border-top: 1px solid $border-color;
  background: #fafbfc;

  .user-avatar {
    width: 38px;
    height: 38px;
    border-radius: 50%;
    background: linear-gradient(135deg, $primary 0%, $primary-light 100%);
    color: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 15px;
    font-weight: 700;
    flex-shrink: 0;
    box-shadow: 0 2px 8px rgba(52, 100, 224, 0.3);
  }

  .user-info-text {
    display: flex;
    flex-direction: column;
    min-width: 0;
    flex: 1;

    .user-name {
      font-size: 14px;
      font-weight: 600;
      color: $text-primary;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .user-actions {
      display: flex;
      align-items: center;
      margin-top: 2px;

      :deep(.el-button) {
        font-size: 12px;
        color: $text-muted;

        &:hover {
          color: $primary;
        }
      }
    }
  }
}

// 内容面板
.content-panel {
  flex: 1;
  height: 100%;
  background: $content-bg;
  padding: 20px;
  overflow-y: auto;
}

.table-card {
  height: 100%;
  background: $card-bg;
  display: flex;
  flex-direction: column;
  position: relative;
  border-radius: 14px;
  box-shadow: $shadow;
  overflow: hidden;
}

.toolbar {
  padding: 20px 24px;
  border-bottom: 1px solid $border-color;
  background: $card-bg;

  .toolbar-left {
    display: flex;
    gap: 12px;
    align-items: center;
    flex-wrap: wrap;
  }

  .quick-url-bar {
     display: flex;
     gap: 10px;
     flex: 1;
     min-width: 420px;
 
     .quick-url-input-wrap {
       flex: 1;
       position: relative;
 
       .el-input {
          width: 100%;

          :deep(.el-input__wrapper) {
            border-radius: 10px;
            box-shadow: 0 0 0 1px $border-color inset;
            transition: box-shadow 0.2s;

            &:hover {
              box-shadow: 0 0 0 1px $primary inset;
            }
          }

          :deep(.el-input__inner) {
            &::placeholder {
              color: $text-muted;
            }
          }
        }
     }
 
     .el-button {
       flex-shrink: 0;
       border-radius: 10px;
       height: 40px;
       padding: 0 22px;
       font-weight: 500;

       &:active {
         transform: scale(0.97);
       }
     }
   }
}

.btn-primary {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  height: 36px;
  padding: 0 20px;
  border-radius: 10px;
  font-weight: 500;
}

.btn-secondary {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  height: 36px;
  padding: 0 20px;
  border-radius: 10px;
  font-weight: 500;
  border: 1px solid $border-color;

  &:hover {
    border-color: $primary;
    color: $primary;
  }
}

// 回收站头部
.recycle-header {
  display: flex;
  align-items: center;
  gap: 10px;
  height: 60px;
  padding: 0 24px;
  border-bottom: 1px solid $border-color;

  .recycle-icon {
    font-size: 22px;
    color: $text-muted;
  }

  .recycle-title {
    font-size: 17px;
    font-weight: 600;
    color: $text-primary;
  }

  .recycle-count {
    font-size: 13px;
    color: $text-muted;
    margin-left: 4px;
  }
}

// 列头
.column-header {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  font-size: 13px;
  color: $text-secondary;
  user-select: none;
}

// 表格单元格
.link-info-cell {
  display: flex;
  align-items: center;
  gap: 12px;

  &.expired {
    opacity: 0.6;
  }

  .favicon {
    border-radius: 6px;
    flex-shrink: 0;
    width: 24px;
    height: 24px;
  }

  .link-meta {
    display: flex;
    flex-direction: column;
    min-width: 0;

    .link-title {
      font-size: 14px;
      font-weight: 500;
      color: $text-primary;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .link-time {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-top: 3px;

      span {
        font-size: 12px;
        color: $text-muted;
      }

      .expire-icon {
        flex-shrink: 0;
      }

      .expired-tag {
        font-size: 11px;
        color: #f56c6c;
        background: #fef0f0;
        padding: 1px 8px;
        border-radius: 4px;
        line-height: 18px;
      }
    }
  }
}

.url-cell {
  display: flex;
  flex-direction: column;
  gap: 6px;

  .url-top {
    display: flex;
    align-items: center;
    gap: 8px;

    .copy-btn {
      flex-shrink: 0;
      font-size: 18px;
      color: $text-muted;
      cursor: pointer;
      transition: all 0.2s;
      padding: 5px;
      border-radius: 6px;

      &:hover {
        color: #fff;
        background: $primary;
        box-shadow: 0 3px 10px rgba(52, 100, 224, 0.4);
      }
    }
  }

  .short-url {
    font-size: 13px;
    font-weight: 500;
  }

  .origin-url {
    font-size: 12px;
    color: $text-muted;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    max-width: 280px;
    padding-left: 24px;
  }
}

.action-cell {
  display: flex;
  align-items: center;
  gap: 6px;
}

// 个人信息卡片
.profile-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px 0;
}

.profile-avatar {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: linear-gradient(135deg, #3464e0, #4f7df3);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  font-weight: 700;
  box-shadow: 0 4px 14px rgba(52, 100, 224, 0.35);
  margin-bottom: 12px;
}

.profile-name {
  font-size: 18px;
  font-weight: 600;
  color: #1d2129;
  margin-bottom: 24px;
}

.profile-details {
  width: 100%;
  background: #f8f9fc;
  border-radius: 12px;
  padding: 8px 16px;
}

.profile-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #eef0f2;

  &:last-child {
    border-bottom: none;
  }

  .profile-label {
    font-size: 14px;
    color: #86909c;
  }

  .profile-value {
    font-size: 14px;
    font-weight: 500;
    color: #1d2129;
  }
}

// 退出抽屉
.logout-icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: #eef2ff;
  display: flex;
  align-items: center;
  justify-content: center;

  .el-icon {
    font-size: 32px;
    color: #3464e0;
  }
}

.action-btn {
  font-size: 20px;
  cursor: pointer;
  transition: all 0.2s;
  padding: 6px;
  border-radius: 8px;

  &:hover {
    background: #f0f2f5;
    transform: translateY(-1px);
  }
}

.action-btn.chart-icon {
  color: #6366f1;

  &:hover {
    color: #4f46e5;
    background: #eef2ff;
  }
}

.action-btn.edit-icon {
  color: #0ea5e9;

  &:hover {
    color: #0284c7;
    background: #ecfeff;
  }
}

.action-btn.delete-icon {
  color: #94a3b8;

  &:hover {
    color: #ef4444;
    background: #fef2f2;
  }
}

.action-btn.recover-icon {
  color: #10b981;

  &:hover {
    color: #059669;
    background: #ecfdf5;
  }
}

.action-btn.qr-icon {
  color: #10b981;

  &:hover {
    color: #059669;
    background: #ecfdf5;
  }
}

.stat-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;

  .stat-row {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;

    .stat-label {
      font-size: 12px;
      color: $text-muted;
    }

    .stat-value {
      font-size: 14px;
      font-weight: 600;
      color: $text-primary;
      font-variant-numeric: tabular-nums;
    }
  }
}

.pagination-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-top: 1px solid $border-color;
  flex-shrink: 0;
  background: $card-bg;

  :deep(.el-pagination) {
    font-weight: 400;
  }

  :deep(.el-pagination button) {
    border-radius: 6px;
  }

  :deep(.el-pager li) {
    border-radius: 6px;
    margin: 0 2px;
    font-weight: 500;
    min-width: 32px;
    height: 32px;
    line-height: 32px;

    &.is-active {
      background: $primary;
      color: #fff;
    }

    &:hover:not(.is-active) {
      color: $primary;
    }
  }

  :deep(.el-pagination .el-input__wrapper) {
    border-radius: 6px;
  }
}

// 空状态
.empty-state {
  height: 40vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  color: $text-muted;

  .empty-icon {
    font-size: 56px;
    opacity: 0.25;
  }

  span {
    font-size: 15px;
    color: $text-muted;
  }
}

// 右侧抽屉
.drawer-header {
  display: flex;
  align-items: center;
  width: 100%;
  gap: 8px;

  .drawer-back {
    font-size: 20px;
    color: $text-muted;
    padding: 4px;
    border-radius: 6px;

    &:hover {
      color: $primary;
      background: $primary-bg;
    }
  }

  .drawer-title {
    font-size: 18px;
    font-weight: 700;
    color: $text-primary;
  }
}

// 编辑个人信息表单
.edit-profile-card {
  padding: 8px 0;
}

.edit-field {
  margin-bottom: 18px;
}

.edit-field-label {
  display: block;
  font-size: 13px;
  font-weight: 500;
  color: #86909c;
  margin-bottom: 6px;
}

:deep(.edit-field .el-input__wrapper) {
  border-radius: 10px;
  box-shadow: 0 0 0 1px #e5e6eb inset;
  transition: box-shadow 0.2s;

  &:hover {
    box-shadow: 0 0 0 1px #3464e0 inset;
  }
}

:deep(.edit-field .el-input.is-disabled .el-input__wrapper) {
  background: #f7f8fa;
}

.drawer-step2 {
  padding: 8px 4px;
}

// 选中标识样式
.selectedItem {
  background: $primary-bg !important;
  color: $primary;
}

// 标签页
.tab-label {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

// 排序索引高亮
.orderIndex {
  color: $primary;
}

// 删除确认弹窗样式
:deep(.el-popconfirm) {
  .el-popconfirm__main {
    font-size: 14px;
    color: $text-primary;
    line-height: 1.5;
    padding: 4px 0;
  }

  .el-popconfirm__action {
    margin-top: 12px;
    display: flex;
    gap: 8px;
    justify-content: flex-end;

    .el-button {
      height: 32px;
      padding: 0 16px;
      border-radius: 6px;
      font-size: 13px;
      font-weight: 500;
    }

    .el-button--primary {
      background: $primary;
      border-color: $primary;

      &:hover {
        background: $primary-light;
        border-color: $primary-light;
      }
    }
  }
}

:deep(.el-popper.is-light) {
  border-radius: 10px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
}

// 表格内部样式优化
:deep(.el-table) {
  --el-table-border-color: transparent;
  --el-table-header-bg-color: #f8f9fc;

  th.el-table__cell {
    background: #f8f9fc !important;
    border-bottom: 1px solid $border-color;
  }

  td.el-table__cell {
    border-bottom: 1px solid #f0f1f3;
  }

  .el-table__body tr:hover > td.el-table__cell {
    background: #f8f9fc;
  }

  .el-table__body tr.el-table__row--striped td.el-table__cell {
    background: #fafbfc;
  }

  .el-table__body tr.el-table__row--striped:hover td.el-table__cell {
    background: #f5f7fa;
  }
}

// 滚动条
.sortOptions::-webkit-scrollbar {
  width: 4px;
}

.sortOptions::-webkit-scrollbar-thumb {
  background: #d9d9d9;
  border-radius: 4px;
}

.sortOptions::-webkit-scrollbar-track {
  background: transparent;
}

:deep(.el-table__body-wrapper::-webkit-scrollbar) {
  width: 6px;
  height: 6px;
}

:deep(.el-table__body-wrapper::-webkit-scrollbar-thumb) {
  background: #d9d9d9;
  border-radius: 4px;
}

:deep(.el-table__body-wrapper::-webkit-scrollbar-track) {
  background: transparent;
}

:deep(.el-table__body-wrapper::-webkit-scrollbar-thumb:hover) {
  background: #bfbfbf;
}

// 选择框样式
:deep(.el-checkbox__inner) {
  border-radius: 4px;
}

// 表格内二维码组件
:deep(.qr-code-wrapper) {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: #f0f2f5;
  }
}

.qr-code-img {
  width: 240px;
  height: 240px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  object-fit: contain;
}
</style>
