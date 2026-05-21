package com.ruoyi.extension.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.extension.mapper.BookBorrowExtensionMapper;
import com.ruoyi.extension.domain.BookBorrowExtension;
import com.ruoyi.extension.service.IBookBorrowExtensionService;

/**
 * 图书借阅延期Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-04-12
 */
@Service
public class BookBorrowExtensionServiceImpl implements IBookBorrowExtensionService 
{
    @Autowired
    private BookBorrowExtensionMapper bookBorrowExtensionMapper;

    /**
     * 查询图书借阅延期
     * 
     * @param id 图书借阅延期主键
     * @return 图书借阅延期
     */
    @Override
    public BookBorrowExtension selectBookBorrowExtensionById(String id)
    {
        return bookBorrowExtensionMapper.selectBookBorrowExtensionById(id);
    }

    /**
     * 查询图书借阅延期列表
     * 
     * @param bookBorrowExtension 图书借阅延期
     * @return 图书借阅延期
     */
    @Override
    public List<BookBorrowExtension> selectBookBorrowExtensionList(BookBorrowExtension bookBorrowExtension)
    {
        return bookBorrowExtensionMapper.selectBookBorrowExtensionList(bookBorrowExtension);
    }

    /**
     * 新增图书借阅延期
     * 
     * @param bookBorrowExtension 图书借阅延期
     * @return 结果
     */
    @Override
    public int insertBookBorrowExtension(BookBorrowExtension bookBorrowExtension)
    {
        return bookBorrowExtensionMapper.insertBookBorrowExtension(bookBorrowExtension);
    }

    /**
     * 修改图书借阅延期
     * 
     * @param bookBorrowExtension 图书借阅延期
     * @return 结果
     */
    @Override
    public int updateBookBorrowExtension(BookBorrowExtension bookBorrowExtension)
    {
        return bookBorrowExtensionMapper.updateBookBorrowExtension(bookBorrowExtension);
    }

    /**
     * 批量删除图书借阅延期
     * 
     * @param ids 需要删除的图书借阅延期主键
     * @return 结果
     */
    @Override
    public int deleteBookBorrowExtensionByIds(String[] ids)
    {
        return bookBorrowExtensionMapper.deleteBookBorrowExtensionByIds(ids);
    }

    /**
     * 删除图书借阅延期信息
     * 
     * @param id 图书借阅延期主键
     * @return 结果
     */
    @Override
    public int deleteBookBorrowExtensionById(String id)
    {
        return bookBorrowExtensionMapper.deleteBookBorrowExtensionById(id);
    }
}
