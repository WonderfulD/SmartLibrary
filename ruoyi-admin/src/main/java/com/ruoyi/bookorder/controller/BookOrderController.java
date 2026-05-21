package com.ruoyi.bookorder.controller;

import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.bookorder.domain.BookOrder;
import com.ruoyi.bookorder.service.IBookOrderService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.storage.domain.BookStorage;
import com.ruoyi.storage.service.IBookStorageService;

import lombok.extern.slf4j.Slf4j;

/**
 * 图书订购Controller
 * @author ruoyi
 * @date 2024-12-11
 */
@RestController
@RequestMapping("/bookorder/BookOrder")
@Slf4j
public class BookOrderController extends BaseController {
    @Autowired
    private IBookOrderService bookOrderService;
    @Autowired
    private IBookStorageService bookStorageService;

    /**
     * 查询图书订购列表
     */
//    @PreAuthorize("@ss.hasPermi('bookorder:BookOrder:list')")
    @GetMapping("/list")
    public TableDataInfo list(BookOrder bookOrder) {
        startPage();
        List<BookOrder> list = bookOrderService.selectBookOrderList(bookOrder);
        return getDataTable(list);
    }

    /**
     * 导出图书订购列表
     */
//    @PreAuthorize("@ss.hasPermi('bookorder:BookOrder:export')")
    @Log(title = "图书订购", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BookOrder bookOrder) {
        List<BookOrder> list = bookOrderService.selectBookOrderList(bookOrder);
        ExcelUtil<BookOrder> util = new ExcelUtil<BookOrder>(BookOrder.class);
        util.exportExcel(response, list, "图书订购数据");
    }

    /**
     * 获取图书订购详细信息
     */
//    @PreAuthorize("@ss.hasPermi('bookorder:BookOrder:query')")
    @GetMapping(value = "/{orderId}")
    public AjaxResult getInfo(@PathVariable("orderId") Long orderId) {
        return success(bookOrderService.selectBookOrderByOrderId(orderId));
    }

    /**
     * 新增图书订购
     */
//    @PreAuthorize("@ss.hasPermi('bookorder:BookOrder:add')")
    @Log(title = "图书订购", businessType = BusinessType.INSERT)
    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult add(@RequestBody BookOrder bookOrder) {
        if (!checkValidForm(bookOrder)) {
            return AjaxResult.error("参数非法");
        }
        Long userId = SecurityUtils.getUserId();
        if (bookOrder.getLibraryId() == null) {
            bookOrder.setLibraryId(userId);
        }
        Long bookId = bookOrder.getBookId();
        LocalDate ld = checkIfFirstOrder(bookId);
        boolean first = false;
        if (ld == null) {
            // 此书第一次订购
            first = true;
            bookOrder.setOrderDate(LocalDate.now());
        } else {
            bookOrder.setOrderDate(ld);
        }
        try {
            bookOrderService.insertBookOrder(bookOrder);
            BookStorage bookStorage;
            Long amount = bookOrder.getAmount();
            if (first) {
                bookStorage = new BookStorage();
                bookStorage.setBookId(bookId);
                bookStorage.setLibraryId(userId);
                bookStorage.setPurchaseDate(LocalDate.now());
                bookStorage.setTotal(amount);
                bookStorage.setStock(amount);
                bookStorageService.insertBookStorage(bookStorage);
            } else {
                BookStorage example = new BookStorage();
                example.setBookId(bookId);
                example.setLibraryId(userId);
                bookStorage = bookStorageService.selectBookStorageList(example).get(0);
                bookStorage.setStock(bookStorage.getStock() + amount);
                bookStorage.setTotal(bookStorage.getTotal() + amount);
                bookStorageService.updateBookStorage(bookStorage);
            }
        } catch (Exception e) {
            log.error("订购图书时发生错误", e);
            throw new RuntimeException(e);
        }
        return AjaxResult.success();
    }

    private LocalDate checkIfFirstOrder(Long bookId) {
        BookOrder bookOrderExample = new BookOrder();
        bookOrderExample.setBookId(bookId);
        bookOrderExample.setLibraryId(SecurityUtils.getUserId());
        List<BookOrder> bookOrders = bookOrderService.selectBookOrderList(bookOrderExample);
        if (bookOrders.isEmpty()) {
            return null;
        } else {
            return bookOrders.get(0).getOrderDate();
        }
    }

    private boolean checkValidForm(BookOrder bookOrder) {
        return bookOrder.getBookId() != null && bookOrder.getAmount() != null && bookOrder.getAmount() > 0;
    }

    /**
     * 修改图书订购
     */
//    @PreAuthorize("@ss.hasPermi('bookorder:BookOrder:edit')")
    @Log(title = "图书订购", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BookOrder bookOrder) {
        return toAjax(bookOrderService.updateBookOrder(bookOrder));
    }

    /**
     * 删除图书订购
     */
//    @PreAuthorize("@ss.hasPermi('bookorder:BookOrder:remove')")
    @Log(title = "图书订购", businessType = BusinessType.DELETE)
    @DeleteMapping("/{orderIds}")
    public AjaxResult remove(@PathVariable Long[] orderIds) {
        return toAjax(bookOrderService.deleteBookOrderByOrderIds(orderIds));
    }
}
