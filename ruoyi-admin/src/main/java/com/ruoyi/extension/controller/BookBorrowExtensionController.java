package com.ruoyi.extension.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.extension.domain.BookBorrowExtension;
import com.ruoyi.extension.service.IBookBorrowExtensionService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 图书借阅延期Controller
 * 
 * @author ruoyi
 * @date 2025-04-12
 */
@RestController
@RequestMapping("/extension/BorrowExtension")
public class BookBorrowExtensionController extends BaseController
{
    @Autowired
    private IBookBorrowExtensionService bookBorrowExtensionService;

    /**
     * 查询图书借阅延期列表
     */
    @PreAuthorize("@ss.hasPermi('extension:BorrowExtension:list')")
    @GetMapping("/list")
    public TableDataInfo list(BookBorrowExtension bookBorrowExtension)
    {
        startPage();
        List<BookBorrowExtension> list = bookBorrowExtensionService.selectBookBorrowExtensionList(bookBorrowExtension);
        return getDataTable(list);
    }

    /**
     * 导出图书借阅延期列表
     */
    @PreAuthorize("@ss.hasPermi('extension:BorrowExtension:export')")
    @Log(title = "图书借阅延期", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BookBorrowExtension bookBorrowExtension)
    {
        List<BookBorrowExtension> list = bookBorrowExtensionService.selectBookBorrowExtensionList(bookBorrowExtension);
        ExcelUtil<BookBorrowExtension> util = new ExcelUtil<BookBorrowExtension>(BookBorrowExtension.class);
        util.exportExcel(response, list, "图书借阅延期数据");
    }

    /**
     * 获取图书借阅延期详细信息
     */
    @PreAuthorize("@ss.hasPermi('extension:BorrowExtension:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(bookBorrowExtensionService.selectBookBorrowExtensionById(id));
    }

    /**
     * 新增图书借阅延期
     */
    @PreAuthorize("@ss.hasPermi('extension:BorrowExtension:add')")
    @Log(title = "图书借阅延期", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BookBorrowExtension bookBorrowExtension)
    {
        return toAjax(bookBorrowExtensionService.insertBookBorrowExtension(bookBorrowExtension));
    }

    /**
     * 修改图书借阅延期
     */
    @PreAuthorize("@ss.hasPermi('extension:BorrowExtension:edit')")
    @Log(title = "图书借阅延期", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BookBorrowExtension bookBorrowExtension)
    {
        return toAjax(bookBorrowExtensionService.updateBookBorrowExtension(bookBorrowExtension));
    }

    /**
     * 删除图书借阅延期
     */
    @PreAuthorize("@ss.hasPermi('extension:BorrowExtension:remove')")
    @Log(title = "图书借阅延期", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(bookBorrowExtensionService.deleteBookBorrowExtensionByIds(ids));
    }
}
