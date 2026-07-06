<template>
  <div class="record-management">
    <el-card>
      <div class="card-header">
        <h2>片区变更记录</h2>
      </div>
      
      <el-table :data="records" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="umbrellaCode" label="伞体编号" />
        <el-table-column prop="oldZoneName" label="原片区">
          <template #default="scope">
            <span v-if="scope.row.oldZoneName">{{ scope.row.oldZoneName }}</span>
            <span v-else class="text-gray">无</span>
          </template>
        </el-table-column>
        <el-table-column prop="newZoneName" label="新片区" />
        <el-table-column prop="changeType" label="变更类型">
          <template #default="scope">
            <el-tag :type="scope.row.changeType === 'batch' ? 'primary' : 'success'">
              {{ scope.row.changeType === 'batch' ? '批量变更' : '单台变更' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operator" label="操作人" />
        <el-table-column prop="changeReason" label="变更原因" />
        <el-table-column prop="createdAt" label="变更时间" />
      </el-table>
      
      <el-pagination
        v-if="total > 0"
        :total="total"
        :page-size="pageSize"
        :current-page="page"
        @current-change="handlePageChange"
        layout="total, prev, pager, next, jumper"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { recordAPI } from '../api'

const records = ref([])
const page = ref(1)
const pageSize = ref(20)
const total = ref(0)

const loadRecords = async () => {
  try {
    const res = await recordAPI.getAll()
    records.value = res.data
    total.value = res.data.length
  } catch (e) {
    console.error(e)
  }
}

const handlePageChange = (val) => {
  page.value = val
}

onMounted(() => {
  loadRecords()
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

.text-gray {
  color: #999;
}

.el-pagination {
  margin-top: 20px;
  text-align: right;
}
</style>