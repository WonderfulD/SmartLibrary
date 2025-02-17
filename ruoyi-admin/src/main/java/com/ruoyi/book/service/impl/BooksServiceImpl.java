package com.ruoyi.book.service.impl;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.ruoyi.book.domain.Books;
import com.ruoyi.book.domain.BooksBO;
import com.ruoyi.book.mapper.BooksMapper;
import com.ruoyi.book.service.IBooksService;
import com.ruoyi.bookorder.domain.BookOrder;
import com.ruoyi.bookorder.service.IBookOrderService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.storage.domain.BookStorage;
import com.ruoyi.storage.service.IBookStorageService;

import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 图书副本信息Service业务层处理
 *
 * @author ruoyi
 * @date 2024-03-12
 */
@Service
@Slf4j
public class BooksServiceImpl implements IBooksService {
    @Autowired
    private BooksMapper booksMapper;

    @Autowired
    private IBookStorageService bookStorageService;

    @Autowired
    private IBookOrderService bookOrderService;

    /**
     * 查询图书副本信息
     *
     * @param bookId 图书副本信息主键
     * @return 图书副本信息
     */
    @Override
    public Books selectBooksByBookId(Long bookId) {
        return booksMapper.selectBooksByBookId(bookId);
    }

    /**
     * 查询图书副本信息列表
     *
     * @param books 图书副本信息
     * @return 图书副本信息
     */
    @Override
    public List<Books> selectBooksList(Books books) {
        return booksMapper.selectBooksList(books);
    }

    /**
     * 新增图书副本信息
     *
     * @param books 图书副本信息
     * @return 结果
     */
    @Override
    public int insertBooks(Books books) {
        return booksMapper.insertBooks(books);
    }

    /**
     * 修改图书副本信息
     *
     * @param books 图书副本信息
     * @return 结果
     */
    @Override
    public int updateBooks(Books books) {
        return booksMapper.updateBooks(books);
    }

    /**
     * 批量删除图书副本信息
     *
     * @param bookIds 需要删除的图书副本信息主键
     * @return 结果
     */
    @Override
    public int deleteBooksByBookIds(Long[] bookIds) {
        return booksMapper.deleteBooksByBookIds(bookIds);
    }

    /**
     * 删除图书副本信息信息
     *
     * @param bookId 图书副本信息主键
     * @return 结果
     */
    @Override
    public int deleteBooksByBookId(Long bookId) {
        return booksMapper.deleteBooksByBookId(bookId);
    }

    /**
     * 根据图书ID列表查询图书列表
     * @param bookIds 图书ID列表
     * @return 图书列表
     */
    @Override
    public List<Books> selectBooksListByIds(List<Long> bookIds) {
        if (bookIds == null || bookIds.isEmpty()) {
            // 若没有满足条件的图书ID，则返回空列表
            return Collections.emptyList();
        }
        // 通过查询得到的book_id列表，从图书表中获取对应的图书信息
        List<Books> availableBooks = booksMapper.selectBooksByIds(bookIds);
        // 返回获取到的图书列表
        return availableBooks;
    }


    /**
     * 新增图书，插入Books表和book_storage表
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public AjaxResult addBooks(BooksBO booksBO) {
        if (booksBO.getAmount() <= 0) {
            return AjaxResult.error("表单不合法");
        }
        //判断图书表中是否有该书
        String isbn = booksBO.getIsbn();
        Books books = new Books();
        books.setIsbn(isbn);
        List<Books> existBooksList = selectBooksList(books);
        Long libraryId = SecurityUtils.getDeptId();
        Books books1 = BeanUtil.copyProperties(booksBO, Books.class);
        if (existBooksList.isEmpty()) {
            //图书表中没有这本书,插入该书至图书表
            insertBooks(books1);
        }
        //图书表中有该书
        Long bookId = books1.getBookId();
        Long amount = booksBO.getAmount();
        //插入订购记录至图书订购表
        BookOrder bookOrder = new BookOrder();
        bookOrder.setBookId(bookId);
        bookOrder.setLibraryId(libraryId);
        bookOrder.setAmount(amount);
        bookOrder.setOrderDate(LocalDate.now());
        bookOrderService.insertBookOrder(bookOrder);

        //判断该图书馆之前是否有这本书
        BookStorage bookStorage = new BookStorage();
        bookStorage.setBookId(bookId);
        bookStorage.setLibraryId(libraryId);
        List<BookStorage> existBookStorages = bookStorageService.selectBookStorageList(bookStorage);

        if (existBookStorages.isEmpty()) {
            //该图书馆之前没有这本书
            BookStorage insertStorage = new BookStorage();
            insertStorage.setStock(amount);
            insertStorage.setTotal(amount);
            insertStorage.setPurchaseDate(LocalDate.now());
            insertStorage.setBookId(bookId);
            insertStorage.setLibraryId(libraryId);
            bookStorageService.insertBookStorage(insertStorage);
            return AjaxResult.success("新增图书成功");
        }
        BookStorage bookStorage1 = existBookStorages.get(0);
        bookStorage1.setStock(bookStorage1.getStock() + amount);
        bookStorage1.setTotal(bookStorage1.getTotal() + amount);
        bookStorageService.updateBookStorage(bookStorage1);
        return AjaxResult.success("增加库存成功");
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult removeBooks(Long[] bookIds) {
        try {
            //移除图书表中记录
            deleteBooksByBookIds(bookIds);

            Long libraryId = SecurityUtils.getDeptId();
            //移除库存表中图书馆id+图书id匹配的记录
            bookStorageService.deleteBookStorageByLibraryIdAndBookIds(libraryId, bookIds);

            //移除订购表中记录
            bookOrderService.deleteBookOrdersByLibraryIdAndBookIds(libraryId, bookIds);
        } catch (Exception e) {
            // 记录异常日志
            log.info("删除图书失败，回滚\n错误信息{}", e.getMessage());
            // 手动设置事务回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return AjaxResult.error("删除图书失败");
        }
        return AjaxResult.success("删除图书成功");
    }

    /**
     * 获取所有图书ID，返回列表
     * @return
     */
    @Override
    public List<Long> getBookIds() {
        return booksMapper.getBookIds();
    }
}
