<template>
  <div class="zone-management">
    <el-card>
      <div class="card-header">
        <h2>片区管理</h2>
        <el-button type="primary" @click="openAddModal">新增片区</el-button>
      </div>
      
      <el-tree
        :data="zoneTree"
        :props="treeProps"
        node-key="id"
        :expand-on-click-node="false"
        @node-click="handleNodeClick"
        default-expand-all
      >
        <template #default="scope">
          <span class="tree-node">
            <span>{{ scope.node.label }}</span>
            <span class="umbrella-count">{{ scope.node.umbrellaCount || 0 }}台</span>
          </span>
        </template>
      </el-tree>
      
      <div v-if="selectedZone" class="zone-detail">
        <h3>片区详情</h3>
        <el-form :model="selectedZone" label-width="100px">
          <el-form-item label="片区名称">
            {{ selectedZone.zoneName }}
          </el-form-item>
          <el-form-item label="层级">
            {{ selectedZone.level }}级
          </el-form-item>
          <el-form-item label="排序">
            {{ selectedZone.sortOrder }}
          </el-form-item>
          <el-form-item label="状态">
            <el-tag :type="selectedZone.status === 'active' ? 'success' : 'warning'">
              {{ selectedZone.status === 'active' ? '启用' : '禁用' }}
            </el-tag>
          </el-form-item>
          <el-form-item label="备注">
            {{ selectedZone.remarks || '-' }}
          </el-form-item>
        </el-form>
        <div class="detail-actions">
          <el-button size="small" @click="openEditModal(selectedZone)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteZone(selectedZone.id)">删除</el-button>
        </div>
      </div>
    </el-card>
    
    <el-dialog title="新增/编辑片区" :visible.sync="modalVisible" width="450px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="片区名称" prop="zoneName">
          <el-input v-model="form.zoneName" required />
        </el-form-item>
        <el-form-item label="上级片区">
          <el-select v-model="form.parentId" placeholder="请选择上级片区">
            <el-option label="无" :value="null" />
            <el-option v-for="zone in zoneOptions" :key="zone.id" :label="zone.zoneName" :value="zone.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status">
            <el-option label="启用" value="active" />
            <el-option label="禁用" value="inactive" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-textarea v-model="form.remarks" rows="3" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="modalVisible = false">取消</el-button>
        <el-button type="primary" @click="saveZone">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { zoneAPI } from '../api'

const zoneTree = ref([])
const zones = ref([])
const modalVisible = ref(false)
const selectedZone = ref(null)
const form = ref({
  id: null,
  zoneName: '',
  parentId: null,
  level: 1,
  sortOrder: 0,
  status: 'active',
  remarks: ''
})

const treeProps = {
  label: 'zoneName',
  children: 'children'
}

const zoneOptions = computed(() => {
  return zones.value.filter(z => z.id !== form.value.id)
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

const handleNodeClick = (data) => {
  selectedZone.value = data
}

const openAddModal = () => {
  form.value = {
    id: null,
    zoneName: '',
    parentId: null,
    level: 1,
    sortOrder: 0,
    status: 'active',
    remarks: ''
  }
  modalVisible.value = true
}

const openEditModal = (row) => {
  form.value = { ...row }
  modalVisible.value = true
}

const saveZone = async () => {
  try {
    if (form.value.id) {
      await zoneAPI.update(form.value.id, form.value)
    } else {
      await zoneAPI.create(form.value)
    }
    modalVisible.value = false
    loadZoneTree()
    loadZones()
    ElMessage.success('保存成功')
  } catch (e) {
    ElMessage.error(e.response?.data?.error || '保存失败')
  }
}

const deleteZone = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该片区吗？', '提示', { type: 'warning' })
    await zoneAPI.delete(id)
    loadZoneTree()
    loadZones()
    selectedZone.value = null
    ElMessage.success('删除成功')
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.response?.data?.error || '删除失败')
    }
  }
}

onMounted(() => {
  loadZoneTree()
  loadZones()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.card-header h2 {
  margin: 0;
  font-size: 18px;
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

.zone-detail {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.zone-detail h3 {
  margin: 0 0 15px 0;
  font-size: 16px;
}

.detail-actions {
  margin-top: 15px;
}
</style>