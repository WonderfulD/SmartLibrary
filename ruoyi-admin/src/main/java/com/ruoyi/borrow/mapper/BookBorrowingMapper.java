package com.ruoyi.borrow.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.borrow.domain.BookBorrowing;

/**
 * 图书借阅信息Mapper接口
 *
 * @author ruoyi
 * @date 2024-03-12
 */
public interface BookBorrowingMapper {
    /**
     * 查询图书借阅信息
     *
     * @param borrowId 图书借阅信息主键
     * @return 图书借阅信息
     */
    BookBorrowing selectBookBorrowingByBorrowId(Long borrowId);

    /**
     * 查询图书借阅信息列表
     *
     * @param bookBorrowing 图书借阅信息
     * @return 图书借阅信息集合
     */
    List<BookBorrowing> selectBookBorrowingList(BookBorrowing bookBorrowing);

    /**
     * 新增图书借阅信息
     *
     * @param bookBorrowing 图书借阅信息
     * @return 结果
     */
    int insertBookBorrowing(BookBorrowing bookBorrowing);

    /**
     * 修改图书借阅信息
     *
     * @param bookBorrowing 图书借阅信息
     * @return 结果
     */
    int updateBookBorrowing(BookBorrowing bookBorrowing);

    /**
     * 删除图书借阅信息
     *
     * @param borrowId 图书借阅信息主键
     * @return 结果
     */
    int deleteBookBorrowingByBorrowId(Long borrowId);

    /**
     * 批量删除图书借阅信息
     *
     * @param borrowIds 需要删除的数据主键集合
     * @return 结果
     */
    int deleteBookBorrowingByBorrowIds(Long[] borrowIds);


    /**
     * 根据部门查询图书借阅信息
     *
     * @param bookBorrowing 包含查询条件的实体，包括图书馆（部门）ID
     * @return 符合条件的图书借阅信息列表
     */
    List<BookBorrowing> selectBookBorrowingListByDept(BookBorrowing bookBorrowing);


    /**
     *根据借阅人ID查询图书借阅信息
     *
     * @param bookBorrowing 包含查询条件的实体，包括借阅人ID
     * @return 符合条件的图书借阅信息列表
     */
    List<BookBorrowing> selectBookBorrowingListByReaderId(BookBorrowing bookBorrowing);


    /**
     * 根据借阅日期查询截止借阅日期的累计会员数
     *
     * @param date 借阅日期
     * @return 符合条件的会员数
     */
    Integer countDistinctReaderIdsByDate(@Param("borrowDate") Date date, @Param("libraryId") Long libraryId);

    /**
     * 查询已通过审核未还的书籍借阅列表
     *
     * @param bookBorrowing 包含查询条件的实体，包括节约审核情况
     * @return 符合条件的图书借阅信息列表
     */
    List<BookBorrowing> selectBookBorrowingByPendingStatusWithNullReturnDate(BookBorrowing bookBorrowing);


    /**
     * 根据当前登录管理员所在图书馆ID，查询每天借阅图书所属种类列表
     *
     * @param bookBorrowing 包含查询条件的实体，包括图书馆Id
     * @return 符合条件的图书借阅信息列表，包括图书种类
     */
    List<BookBorrowing> selectBookBorrowingListByLibraryIdWithCategory(BookBorrowing bookBorrowing);

    /**
     * 根据当前登录管理员所在图书馆ID，查询读者归还待确认的借阅列表
     * @param bookBorrowing
     * @return
     */
    List<BookBorrowing> selectBookBorrowingListWithReturnPending(BookBorrowing bookBorrowing);

    Long selectNotReturnedBooksCountByReaderIdAndBookId(@Param("readerId") Long readerId, @Param("bookId") Long bookId);
}
