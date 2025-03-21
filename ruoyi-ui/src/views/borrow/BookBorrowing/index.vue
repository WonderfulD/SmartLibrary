<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="借阅号" prop="borrowId">
        <el-input
          v-model="queryParams.borrowId"
          placeholder="请输入借阅号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="书籍编号" prop="bookId">
        <el-input
          v-model="queryParams.bookId"
          placeholder="请输入书籍编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="读者号" prop="readerId">
        <el-input
          v-model="queryParams.readerId"
          placeholder="请输入读者号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="借出日期" prop="borrowDate">
        <el-date-picker clearable
          v-model="queryParams.borrowDate"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择借出日期">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="应还日期" prop="dueDate">
        <el-date-picker clearable
          v-model="queryParams.dueDate"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择应还日期">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['borrow:BookBorrowing:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['borrow:BookBorrowing:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['borrow:BookBorrowing:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['borrow:BookBorrowing:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="BookBorrowingList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="借阅号" align="center" prop="borrowId" />
      <el-table-column label="书籍编号" align="center" prop="bookId" />
      <el-table-column  label="书籍封面" align="center">
        <template slot-scope="scope">
          <el-popover placement="top-start" title="" trigger="hover">
            <img :src="scope.row.coverUrl" alt="" style="width: 150px;height: 150px">
            <img slot="reference" :src="scope.row.coverUrl" style="width: 50px;height: 50px">
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column label="书名" align="center" prop="title" />
      <el-table-column label="读者号" align="center" prop="readerId" />
      <el-table-column label="借出日期" align="center" prop="borrowDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.borrowDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="应还日期" align="center" prop="dueDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.dueDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="实际还书日期" align="center" prop="returnDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.returnDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="逾期罚款" align="center" prop="fine" />
      <el-table-column label="借阅状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.latest_borrow_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['borrow:BookBorrowing:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['borrow:BookBorrowing:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改图书借阅信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="书籍ID" prop="bookId">
          <el-input v-model="form.bookId" placeholder="请输入书籍ID" />
        </el-form-item>
        <el-form-item label="读者ID" prop="readerId">
          <el-input v-model="form.readerId" placeholder="请输入读者ID" />
        </el-form-item>
        <el-form-item label="借出日期" prop="borrowDate">
          <el-date-picker clearable
            v-model="form.borrowDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择借出日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="应还日期" prop="dueDate">
          <el-date-picker clearable
            v-model="form.dueDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择应还日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="借阅备注" prop="comments">
          <el-input v-model="form.comments" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listBookBorrowing,
  getBookBorrowing,
  delBookBorrowing,
  addBookBorrowing,
  updateBookBorrowing,
  listBookBorrowingByDept
} from "@/api/borrow/BookBorrowing";
import {transactionList} from "@/api/remote-search";

export default {
  name: "BookBorrowing",
  dicts: ['latest_borrow_status'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 图书借阅信息表格数据
      BookBorrowingList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        borrowId: null,
        bookId: null,
        readerId: null,
        borrowDate: null,
        dueDate: null,
        status: null,
        pendingStatus: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        bookId: [
          { required: true, message: "书籍ID不能为空", trigger: "blur" }
        ],
        readerId: [
          { required: true, message: "读者ID不能为空", trigger: "blur" }
        ],
        libraryId: [
          { required: true, message: "图书馆ID不能为空", trigger: "blur" }
        ],
        borrowDate: [
          { required: true, message: "借出日期不能为空", trigger: "blur" }
        ],
        dueDate: [
          { required: true, message: "应还日期不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 根据当前登录管理员所在图书馆（部门）id查询图书借阅信息列表 */
    getList() {
      this.loading = true;
      transactionList(this.queryParams).then(response => {
        this.BookBorrowingList = response.rows;
        this.total = response.total;
        this.loading = false;
      })
    },

    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        borrowId: null,
        bookId: null,
        readerId: null,
        libraryId: null,
        borrowDate: null,
        dueDate: null,
        returnDate: null,
        fine: null,
        comments: null,
        status: null,
        pendingStatus: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 多选框选中数据 */
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.borrowId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加图书借阅信息";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const borrowId = row.borrowId || this.ids
      getBookBorrowing(borrowId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改图书借阅信息";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.borrowId != null) {
            updateBookBorrowing(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addBookBorrowing(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const borrowIds = row.borrowId || this.ids;
      this.$modal.confirm('是否确认删除图书借阅信息编号为"' + borrowIds + '"的数据项？').then(function() {
        return delBookBorrowing(borrowIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('borrow/BookBorrowing/export', {
        ...this.queryParams
      }, `BookBorrowing_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
