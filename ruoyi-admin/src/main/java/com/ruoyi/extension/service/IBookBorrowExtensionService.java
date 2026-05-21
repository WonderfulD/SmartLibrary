package com.ruoyi.extension.service;

import java.util.List;
import com.ruoyi.extension.domain.BookBorrowExtension;

/**
 * 图书借阅延期Service接口
 * 
 * @author ruoyi
 * @date 2025-04-12
 */
public interface IBookBorrowExtensionService 
{
    /**
     * 查询图书借阅延期
     * 
     * @param id 图书借阅延期主键
     * @return 图书借阅延期
     */
    public BookBorrowExtension selectBookBorrowExtensionById(String id);

    /**
     * 查询图书借阅延期列表
     * 
     * @param bookBorrowExtension 图书借阅延期
     * @return 图书借阅延期集合
     */
    public List<BookBorrowExtension> selectBookBorrowExtensionList(BookBorrowExtension bookBorrowExtension);

    /**
     * 新增图书借阅延期
     * 
     * @param bookBorrowExtension 图书借阅延期
     * @return 结果
     */
    public int insertBookBorrowExtension(BookBorrowExtension bookBorrowExtension);

    /**
     * 修改图书借阅延期
     * 
     * @param bookBorrowExtension 图书借阅延期
     * @return 结果
     */
    public int updateBookBorrowExtension(BookBorrowExtension bookBorrowExtension);

    /**
     * 批量删除图书借阅延期
     * 
     * @param ids 需要删除的图书借阅延期主键集合
     * @return 结果
     */
    public int deleteBookBorrowExtensionByIds(String[] ids);

    /**
     * 删除图书借阅延期信息
     * 
     * @param id 图书借阅延期主键
     * @return 结果
     */
    public int deleteBookBorrowExtensionById(String id);
}
