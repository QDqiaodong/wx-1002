<template>
  <div class="umbrella-management">
    <el-card>
      <div class="card-header">
        <h2>遮阳伞管理</h2>
        <el-button type="primary" @click="openAddModal">新增遮阳伞</el-button>
      </div>
      
      <el-table :data="umbrellas" border stripe>
        <el-table-column type="selection" width="55" />
        <el-table-column prop="umbrellaCode" label="伞体编号" />
        <el-table-column prop="size" label="尺寸" />
        <el-table-column prop="material" label="材质类型" />
        <el-table-column prop="zoneName" label="所属片区">
          <template #default="scope">
            <span v-if="scope.row.zoneName">{{ scope.row.zoneName }}</span>
            <span v-else class="text-gray">未分配</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'normal' ? 'success' : 'warning'">
              {{ scope.row.status === 'normal' ? '正常' : '维护中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remarks" label="备注" />
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" @click="openEditModal(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteUmbrella(scope.row.id)">删除</el-button>
            <el-button size="small" type="primary" @click="openAssignModal(scope.row)">分配片区</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <el-dialog title="新增/编辑遮阳伞" :visible.sync="modalVisible" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="伞体编号" prop="umbrellaCode">
          <el-input v-model="form.umbrellaCode" required />
        </el-form-item>
        <el-form-item label="尺寸" prop="size">
          <el-input v-model="form.size" />
        </el-form-item>
        <el-form-item label="材质类型" prop="material">
          <el-input v-model="form.material" />
        </el-form-item>
        <el-form-item label="所属片区">
          <el-select v-model="form.zoneId" placeholder="请选择片区">
            <el-option label="未分配" :value="null" />
            <el-option v-for="zone in zones" :key="zone.id" :label="zone.zoneName" :value="zone.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status">
            <el-option label="正常" value="normal" />
            <el-option label="维护中" value="maintenance" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-textarea v-model="form.remarks" rows="3" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="modalVisible = false">取消</el-button>
        <el-button type="primary" @click="saveUmbrella">确定</el-button>
      </div>
    </el-dialog>
    
    <el-dialog title="分配片区" :visible.sync="assignModalVisible" width="400px">
      <el-form :model="assignForm" label-width="80px">
        <el-form-item label="目标片区">
          <el-select v-model="assignForm.zoneId" placeholder="请选择片区" required>
            <el-option v-for="zone in zones" :key="zone.id" :label="zone.zoneName" :value="zone.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作人">
          <el-input v-model="assignForm.operator" />
        </el-form-item>
        <el-form-item label="变更原因">
          <el-textarea v-model="assignForm.changeReason" rows="3" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="assignModalVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAssign">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { umbrellaAPI, zoneAPI } from '../api'

const umbrellas = ref([])
const zones = ref([])
const modalVisible = ref(false)
const assignModalVisible = ref(false)
const form = ref({
  id: null,
  umbrellaCode: '',
  size: '',
  material: '',
  zoneId: null,
  status: 'normal',
  remarks: ''
})
const assignForm = ref({
  umbrellaId: null,
  zoneId: null,
  operator: '',
  changeReason: ''
})

const loadUmbrellas = async () => {
  try {
    const res = await umbrellaAPI.getAll()
    umbrellas.value = res.data
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

const openAddModal = () => {
  form.value = {
    id: null,
    umbrellaCode: '',
    size: '',
    material: '',
    zoneId: null,
    status: 'normal',
    remarks: ''
  }
  modalVisible.value = true
}

const openEditModal = (row) => {
  form.value = { ...row }
  modalVisible.value = true
}

const saveUmbrella = async () => {
  try {
    if (form.value.id) {
      await umbrellaAPI.update(form.value.id, form.value)
    } else {
      await umbrellaAPI.create(form.value)
    }
    modalVisible.value = false
    loadUmbrellas()
    ElMessage.success('保存成功')
  } catch (e) {
    ElMessage.error(e.response?.data?.error || '保存失败')
  }
}

const deleteUmbrella = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该遮阳伞吗？', '提示', { type: 'warning' })
    await umbrellaAPI.delete(id)
    loadUmbrellas()
    ElMessage.success('删除成功')
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.response?.data?.error || '删除失败')
    }
  }
}

const openAssignModal = (row) => {
  assignForm.value = {
    umbrellaId: row.id,
    zoneId: row.zoneId,
    operator: '',
    changeReason: ''
  }
  assignModalVisible.value = true
}

const submitAssign = async () => {
  try {
    await umbrellaAPI.assignZone(assignForm.value.umbrellaId, {
      zoneId: assignForm.value.zoneId,
      operator: assignForm.value.operator,
      changeReason: assignForm.value.changeReason
    })
    assignModalVisible.value = false
    loadUmbrellas()
    ElMessage.success('分配成功')
  } catch (e) {
    ElMessage.error(e.response?.data?.error || '分配失败')
  }
}

onMounted(() => {
  loadUmbrellas()
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

.text-gray {
  color: #999;
}
</style>