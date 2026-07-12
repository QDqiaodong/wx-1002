<template>
  <div class="batch-assign">
    <el-card>
      <div class="card-header">
        <h2>批量分配片区</h2>
      </div>
      
      <div class="assign-container">
        <div class="zone-panel">
          <h3>园区片区</h3>
          <el-tree
            :data="zoneTree"
            :props="treeProps"
            node-key="id"
            :expand-on-click-node="false"
            :highlight-current="true"
            @node-click="handleZoneClick"
            default-expand-all
          >
            <template #default="scope">
            <span class="tree-node">
              <span>{{ scope.node.label }}</span>
              <span v-if="scope.node.capacity" class="capacity-info">
                <span :class="['capacity-count', scope.node.remainingCapacity != null && scope.node.remainingCapacity <= 0 ? 'warning' : '']">
                  {{ scope.node.umbrellaCount || 0 }}/{{ scope.node.capacity }}
                </span>
              </span>
              <span v-else class="umbrella-count">{{ scope.node.umbrellaCount || 0 }}台</span>
            </span>
          </template>
          </el-tree>
        </div>
        
        <div class="umbrella-panel">
          <div class="panel-header">
            <h3>遮阳伞列表</h3>
            <span class="selected-count">已选择: {{ selectedIds.length }}台</span>
          </div>
          
          <el-table 
            :data="currentUmbrellas" 
            border 
            stripe
            :row-key="(row) => row.id"
            :selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="55" />
            <el-table-column prop="umbrellaCode" label="伞体编号" />
            <el-table-column prop="size" label="尺寸" />
            <el-table-column prop="material" label="材质类型" />
            <el-table-column prop="zoneName" label="当前片区">
              <template #default="scope">
                <span v-if="scope.row.zoneName">{{ scope.row.zoneName }}</span>
                <span v-else class="text-gray">未分配</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
      
      <div class="assign-footer">
        <div class="assign-form">
          <el-form :model="assignForm" inline>
            <el-form-item label="目标片区">
              <el-select v-model="assignForm.zoneId" placeholder="请选择目标片区" style="width: 250px;" @change="handleTargetZoneChange">
                <el-option v-for="zone in zones" :key="zone.id" :label="zone.capacity ? zone.zoneName + ' (' + (zone.umbrellaCount || 0) + '/' + zone.capacity + ')' : zone.zoneName" :value="zone.id" />
              </el-select>
            </el-form-item>
            <el-form-item v-if="targetZoneInfo" class="capacity-status">
              <div class="capacity-detail">
                <span v-if="targetZoneInfo.capacity">
                  已占用: {{ targetZoneInfo.umbrellaCount || 0 }} | 剩余: {{ targetZoneInfo.remainingCapacity }} | 上限: {{ targetZoneInfo.capacity }}
                </span>
                <span v-else>无容量限制</span>
              </div>
            </el-form-item>
            <el-form-item label="操作人">
              <el-input v-model="assignForm.operator" placeholder="请输入操作人" style="width: 150px;" />
            </el-form-item>
            <el-form-item label="变更原因">
              <el-input v-model="assignForm.changeReason" placeholder="请输入变更原因" style="width: 250px;" />
            </el-form-item>
            <el-form-item>
              <el-button 
                type="primary" 
                :disabled="selectedIds.length === 0 || !assignForm.zoneId"
                @click="submitBatchAssign"
              >
                批量分配
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { umbrellaAPI, zoneAPI } from '../api'

const zoneTree = ref([])
const zones = ref([])
const allUmbrellas = ref([])
const selectedIds = ref([])
const selectedZoneId = ref(null)
const targetZoneInfo = ref(null)

const assignForm = ref({
  zoneId: null,
  operator: '',
  changeReason: ''
})

const treeProps = {
  label: 'zoneName',
  children: 'children'
}

const currentUmbrellas = computed(() => {
  if (selectedZoneId.value) {
    return allUmbrellas.value.filter(u => u.zoneId === selectedZoneId.value)
  }
  return allUmbrellas.value
})

const loadZoneTree = async () => {
  try {
    const res = await zoneAPI.getTree()
    zoneTree.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const loadZones = async () => {
  try {
    const res = await zoneAPI.getAll()
    zones.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const loadUmbrellas = async () => {
  try {
    const res = await umbrellaAPI.getAll()
    allUmbrellas.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const handleZoneClick = (data) => {
  selectedZoneId.value = data.id
  selectedIds.value = []
}

const handleTargetZoneChange = (zoneId) => {
  const zone = zones.value.find(z => z.id === zoneId)
  if (zone) {
    targetZoneInfo.value = {
      id: zone.id,
      zoneName: zone.zoneName,
      capacity: zone.capacity,
      umbrellaCount: zone.umbrellaCount || 0,
      remainingCapacity: zone.capacity ? zone.capacity - (zone.umbrellaCount || 0) : null
    }
  } else {
    targetZoneInfo.value = null
  }
}

const handleSelectionChange = (val) => {
  selectedIds.value = val.map(item => item.id)
}

const submitBatchAssign = async () => {
  try {
    await umbrellaAPI.batchAssign({
      umbrellaIds: selectedIds.value,
      zoneId: assignForm.value.zoneId,
      operator: assignForm.value.operator,
      changeReason: assignForm.value.changeReason
    })
    loadUmbrellas()
    loadZoneTree()
    selectedIds.value = []
    assignForm.value = {
      zoneId: null,
      operator: '',
      changeReason: ''
    }
    ElMessage.success('批量分配成功')
  } catch (e) {
    ElMessage.error(e.response?.data?.error || '批量分配失败')
  }
}

onMounted(() => {
  loadZoneTree()
  loadZones()
  loadUmbrellas()
})
</script>

<style scoped>
.card-header {
  margin-bottom: 20px;
}

.card-header h2 {
  margin: 0;
  font-size: 18px;
}

.assign-container {
  display: flex;
  gap: 20px;
  height: 400px;
}

.zone-panel {
  width: 300px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  overflow: hidden;
}

.zone-panel h3 {
  margin: 0;
  padding: 10px 15px;
  background: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
  font-size: 14px;
}

.umbrella-panel {
  flex: 1;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  background: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
}

.panel-header h3 {
  margin: 0;
  font-size: 14px;
}

.selected-count {
  color: #667eea;
  font-weight: 600;
}

.umbrella-panel .el-table {
  flex: 1;
}

.tree-node {
  display: flex;
  justify-content: space-between;
  width: 100%;
}

.umbrella-count {
  color: #666;
  font-size: 12px;
  background: #f5f5f5;
  padding: 2px 8px;
  border-radius: 10px;
}

.capacity-info {
  display: flex;
  align-items: center;
  gap: 4px;
}

.capacity-count {
  color: #666;
  font-size: 12px;
  background: #f0f9ff;
  padding: 2px 8px;
  border-radius: 10px;
}

.capacity-count.warning {
  background: #fef2f2;
  color: #dc2626;
}

.capacity-status {
  margin-right: 10px;
}

.capacity-detail {
  font-size: 13px;
  color: #666;
  padding: 5px 10px;
  background: #fafafa;
  border-radius: 4px;
}

.assign-footer {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.text-gray {
  color: #999;
}
</style>