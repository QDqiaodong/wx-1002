<template>
  <div class="app-container">
    <el-container>
      <el-header class="header">
        <h1>园区公共遮阳伞片区分组批量分配系统</h1>
      </el-header>
      <el-container>
        <el-aside width="200px" class="aside">
          <el-menu :default-active="activeMenu" mode="vertical" @select="handleMenuSelect">
            <el-menu-item index="umbrella">
              <el-icon><Umbrella /></el-icon>
              <span>遮阳伞管理</span>
            </el-menu-item>
            <el-menu-item index="zone">
              <el-icon><MapLocation /></el-icon>
              <span>片区管理</span>
            </el-menu-item>
            <el-menu-item index="batch">
              <el-icon><Grid /></el-icon>
              <span>批量分配</span>
            </el-menu-item>
            <el-menu-item index="records">
              <el-icon><Clock /></el-icon>
              <span>变更记录</span>
            </el-menu-item>
          </el-menu>
        </el-aside>
        <el-main class="main">
          <UmbrellaManagement v-if="activeMenu === 'umbrella'" />
          <ZoneManagement v-else-if="activeMenu === 'zone'" />
          <BatchAssign v-else-if="activeMenu === 'batch'" />
          <RecordManagement v-else-if="activeMenu === 'records'" />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Umbrella, MapLocation, Grid, Clock } from '@element-plus/icons-vue'
import UmbrellaManagement from './components/UmbrellaManagement.vue'
import ZoneManagement from './components/ZoneManagement.vue'
import BatchAssign from './components/BatchAssign.vue'
import RecordManagement from './components/RecordManagement.vue'

const activeMenu = ref('umbrella')

const handleMenuSelect = (key) => {
  activeMenu.value = key
}
</script>

<style scoped>
.app-container {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-align: center;
  line-height: 60px;
  padding: 0;
}

.header h1 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.aside {
  background: #2f4050;
  min-height: calc(100vh - 60px);
}

.el-menu {
  border-right: none;
}

.main {
  padding: 20px;
}
</style>