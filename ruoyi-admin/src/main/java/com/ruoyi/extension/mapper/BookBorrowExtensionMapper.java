package com.ruoyi.extension.mapper;

import java.util.List;
import com.ruoyi.extension.domain.BookBorrowExtension;

/**
 * 图书借阅延期Mapper接口
 * 
 * @author ruoyi
 * @date 2025-04-12
 */
public interface BookBorrowExtensionMapper 
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
     * 删除图书借阅延期
     * 
     * @param id 图书借阅延期主键
     * @return 结果
     */
    public int deleteBookBorrowExtensionById(String id);

    /**
     * 批量删除图书借阅延期
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBookBorrowExtensionByIds(String[] ids);
}
