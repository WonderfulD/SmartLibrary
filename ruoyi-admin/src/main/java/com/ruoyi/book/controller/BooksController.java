package com.ruoyi.book.controller;

import static com.ruoyi.prediction.Prediction.predictNextWeek;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

import com.ruoyi.Utils.BorrowUtil;
import com.ruoyi.book.domain.Books;
import com.ruoyi.book.domain.BooksBO;
import com.ruoyi.book.service.IBooksService;
import com.ruoyi.bookorder.service.IBookOrderService;
import com.ruoyi.borrow.controller.BookBorrowingController;
import com.ruoyi.borrow.domain.BookBorrowing;
import com.ruoyi.borrow.service.IBookBorrowingService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.rate.domain.BookRatings;
import com.ruoyi.rate.service.IBookRatingsService;
import com.ruoyi.storage.service.IBookStorageService;

import lombok.extern.slf4j.Slf4j;

/**
 * 图书副本信息Controller
 *
 * @author ruoyi
 * @date 2024-03-12
 */

@Slf4j
@RestController
@RequestMapping("/book/BookInfo")
public class BooksController extends BaseController {
    @Autowired
    private IBooksService booksService;

    @Autowired
    private IBookBorrowingService bookBorrowingService;

    @Autowired
    private IBookRatingsService bookRatingsService;

    @Autowired
    private IBookStorageService bookStorageService;

    @Autowired
    private IBookOrderService bookOrderService;

    /**
     * 查询可被借阅的图书副本信息列表
     */
//    @PreAuthorize("@ss.hasPermi('book:BookInfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(Books books) {
        List<Long> availableBookIDsList = bookStorageService.selectAvailableBookIDsList();
        startPage();
        List<Books> availableBooksList = booksService.selectBooksListByIds(availableBookIDsList);
        return getDataTable(availableBooksList);
    }

    /**
     * 根据图书馆ID查询图书副本信息列表
     */
//    @PreAuthorize("@ss.hasPermi('book:BookInfo:list')")
    @GetMapping("/listByLibrary")
    public TableDataInfo listByLibrary(Books books) {
        List<Long> bookIdsByLibraryId = bookStorageService.selectBookIdsByLibraryId(SecurityUtils.getDeptId());
        startPage();
        List<Books> booksList = booksService.selectBooksListByIds(bookIdsByLibraryId);
        return getDataTable(booksList);
    }

    /**
     * 根据图书馆ID查询读者借阅图书类别分布
     */
    @GetMapping("/categoryDistribution")
    public AjaxResult getCategoryDistribution() {
        try {
            // 获取当前图书馆的所有图书信息
            Long libraryId = SecurityUtils.getDeptId();
            List<Long> bookIds = bookStorageService.selectBookIdsByLibraryId(libraryId);
            List<Books> bookList = booksService.selectBooksListByIds(bookIds);
            // 统计每个类别的图书数量
            Map<String, Integer> categoryCount = new HashMap<>();
            for (Books book : bookList) {
                // 获取每本图书的详细信息
                Books bookInfo = booksService.selectBooksByBookId(book.getBookId());
                String category = bookInfo.getCategory();
                if (category == null) {
                    continue;
                }
                // 更新统计信息
                categoryCount.put(category, categoryCount.getOrDefault(category, 0) + 1);
            }
            // 返回统计结果
            return AjaxResult.success("获取成功", categoryCount);
        } catch (Exception e) {
            log.info("根据图书馆ID查询读者借阅图书类别分布 报错:{}", e.getMessage());
            return AjaxResult.error("获取图书类别统计信息失败");
        }
    }

    /**
     * 根据图书馆ID查询总藏书量
     */
    @GetMapping("/totalBooksCount")
    public AjaxResult getTotalBooksCountByLibraryId() {
        Integer count = bookOrderService.selectTotalAmountByLibraryIdAndDate(SecurityUtils.getDeptId(), LocalDate.now());
        return AjaxResult.success(count == null ? 0 : count);
    }

    /**
     * 根据读者ID查询读者借阅图书类别分布
     */
    @GetMapping("/borrowedBooksCategoryDistribution")
    public AjaxResult getCategoryDistributionByReaderId() {
        try {
            // 获取当前读者的所有借阅信息
            BookBorrowing bookBorrowing = new BookBorrowing();
            bookBorrowing.setReaderId(SecurityUtils.getUserId());
            List<BookBorrowing> bookBorrowingList = bookBorrowingService.selectBookBorrowingListByReaderId(bookBorrowing);

            // 统计每个类别的图书数量
            Map<String, Integer> categoryCount = new HashMap<>();
            for (BookBorrowing borrowing : bookBorrowingList) {
                // 获取每本图书的详细信息
                Books bookInfo = booksService.selectBooksByBookId(borrowing.getBookId());
                String category = bookInfo.getCategory();

                // 更新统计信息
                categoryCount.put(category, categoryCount.getOrDefault(category, 0) + 1);
            }
            // 返回统计结果
            return AjaxResult.success("获取成功", categoryCount);
        } catch (Exception e) {
            log.info("根据读者ID查询读者借阅图书类别分布 报错:{}", e.getMessage());
            return AjaxResult.error("获取图书类别统计信息失败");
        }
    }

    /**
     * 根据图书馆ID查询最近藏书量列表和预计藏书量列表
     */
    @GetMapping("/listRecentBooks")
    public AjaxResult listRecentBooks() throws Exception {
        Long libraryId = SecurityUtils.getDeptId();
        LocalDate today = LocalDate.now();
        List<Integer> recentBooksCounts = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate specificDay = today.minusDays(i);
            Integer totalAmountUntilSpecificDay = bookOrderService.selectTotalAmountByLibraryIdAndDate(libraryId, specificDay);
            recentBooksCounts.add(totalAmountUntilSpecificDay == null ? 0 : totalAmountUntilSpecificDay);
        }
        List<Integer> estimatedBooksCount = predictNextWeek(recentBooksCounts);
        // 封装结果返回
        Map<String, Object> result = new HashMap<>();
        result.put("recentBooksCounts", recentBooksCounts);
        result.put("estimatedBooksCount", estimatedBooksCount);
        return AjaxResult.success(result);
    }


    /**
     * 根据图书馆ID查询最近借阅量列表和预计借阅量列表
     */
    @GetMapping("/listRecentBorrows")
    public AjaxResult listRecentBorrows() throws Exception {
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(7);

        // 获取当前图书馆的所有借阅信息
        BookBorrowing bookBorrowing = new BookBorrowing();
        bookBorrowing.setLibraryId(SecurityUtils.getDeptId());
        List<BookBorrowing> bookBorrowingList = bookBorrowingService.selectBookBorrowingListByDept(bookBorrowing);

        // 初始化最近七天的借阅列表
        List<Integer> recentBorrowsCounts = new ArrayList<>(Collections.nCopies(7, 0));

        // 遍历最近七天
        for (int i = 1; i < 8; i++) {
            LocalDate specificDay = sevenDaysAgo.plusDays(i);
            // 计算截至到specificDay的借阅量
            long count = bookBorrowingList.stream()
                    .filter(borrowing -> {
                        LocalDate borrowDate = borrowing.getBorrowDate();
                        return !borrowDate.isAfter(specificDay);
                    })
                    .count();
            recentBorrowsCounts.set(i - 1, (int) count);
        }

        // 初始化最近14天至最近7天的借阅列表
        List<Integer> lastBorrowsCounts = new ArrayList<>(Collections.nCopies(7, 0));

        LocalDate fourteenDaysAgo = today.minusDays(14);

        // 遍历最近14天至最近7天
        for (int i = 1; i < 8; i++) {
            LocalDate specificDay = fourteenDaysAgo.plusDays(i);
            // 计算截至到specificDay的借阅量
            long count = bookBorrowingList.stream()
                    .filter(borrowing -> {
                        LocalDate borrowDate = borrowing.getBorrowDate();
                        return !borrowDate.isAfter(specificDay);
                    })
                    .count();
            lastBorrowsCounts.set(i - 1, (int) count);
        }

        // 预计借阅量列表
        List<Integer> estimatedBorrowsCount = predictNextWeek(lastBorrowsCounts);

        // 封装结果返回
        Map<String, Object> result = new HashMap<>();
        result.put("recentBorrowsCounts", recentBorrowsCounts);
        result.put("estimatedBorrowsCount", estimatedBorrowsCount);

        return AjaxResult.success(result);
    }

    /**
     * 根据图书馆ID查询图书借阅量列表
     */
    @GetMapping("/listBorrowsListByLibraryId")
    public AjaxResult listBorrowsListByLibraryId() {
        BookBorrowing bookBorrowing = new BookBorrowing();
        bookBorrowing.setLibraryId(SecurityUtils.getDeptId());
        List<BookBorrowing> bookBorrowingList = bookBorrowingService.selectBookBorrowingListByDept(bookBorrowing);

        // 使用Map存储借阅次数
        Map<Long, Integer> borrowCounts = new HashMap<>();
        for (BookBorrowing borrowing : bookBorrowingList) {
            borrowCounts.put(borrowing.getBookId(), borrowCounts.getOrDefault(borrowing.getBookId(), 0) + 1);
        }

        // 使用List存储最终结果，每个元素包含图书详细信息和借阅次数
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : borrowCounts.entrySet()) {
            Long bookId = entry.getKey();
            Books bookInfo = booksService.selectBooksByBookId(bookId);

            // 创建一个包含图书详细信息和借阅次数的Map
            Map<String, Object> bookDetails = new HashMap<>();
            bookDetails.put("bookId", bookId);
            bookDetails.put("title", bookInfo.getTitle());
            bookDetails.put("coverUrl", bookInfo.getCoverUrl());
            bookDetails.put("borrowCount", entry.getValue());

            resultList.add(bookDetails);
        }

        // 对resultList按照borrowCount倒序排序
        resultList.sort((map1, map2) -> Integer.compare((int) map2.get("borrowCount"), (int) map1.get("borrowCount")));

        return AjaxResult.success(resultList);
    }

    /**
     * 查询所有图书馆图书借阅量列表
     */
    @GetMapping("/listBorrowsList")
    public AjaxResult listBorrowsList() {
        BookBorrowing bookBorrowing = new BookBorrowing();
        List<BookBorrowing> bookBorrowingList = bookBorrowingService.selectBookBorrowingListByDept(bookBorrowing);

        // 使用Map存储借阅次数
        Map<Long, Integer> borrowCounts = new HashMap<>();
        for (BookBorrowing borrowing : bookBorrowingList) {
            borrowCounts.put(borrowing.getBookId(), borrowCounts.getOrDefault(borrowing.getBookId(), 0) + 1);
        }

        // 使用List存储最终结果，每个元素包含图书详细信息和借阅次数
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : borrowCounts.entrySet()) {
            Long bookId = entry.getKey();
            Books bookInfo = booksService.selectBooksByBookId(bookId);

            // 创建一个包含图书详细信息和借阅次数的Map
            Map<String, Object> bookDetails = new HashMap<>();
            bookDetails.put("bookId", bookId);
            bookDetails.put("title", bookInfo.getTitle());
            bookDetails.put("coverUrl", bookInfo.getCoverUrl());
            bookDetails.put("borrowCount", entry.getValue());

            resultList.add(bookDetails);
        }

        // 对resultList按照borrowCount倒序排序
        resultList.sort((map1, map2) -> Integer.compare((int) map2.get("borrowCount"), (int) map1.get("borrowCount")));
        return AjaxResult.success(resultList);
    }

    /**
     * 根据读者id获取推荐图书列表
     */
    @GetMapping("/recommendations")
    public AjaxResult getBookRecommendations() {
        try {
            Long readerId = SecurityUtils.getUserId();
            BookBorrowing bookBorrowing = new BookBorrowing();
            bookBorrowing.setReaderId(readerId);
            // 获取此人已经借阅的书籍ID列表
            List<Long> borrowedBookIds = bookBorrowingService.selectBookBorrowingListByReaderId(bookBorrowing)
                    .stream()
                    .map(BookBorrowing::getBookId)
                    .collect(Collectors.toList());

            // 获取所有书籍列表
            List<Books> allBooks = booksService.selectBooksList(new Books());

            // 过滤出此人没有借阅过的书籍
            List<Books> unborrowedBooks = allBooks.stream()
                    .filter(book -> !borrowedBookIds.contains(book.getBookId()))
                    .collect(Collectors.toList());

            // 获取所有书籍的评分信息
            List<BookRatings> bookRatingsList = bookRatingsService.selectBookRatingsList(new BookRatings());

            // 创建一个Map来存储书籍ID和对应的评分
            Map<Long, Double> bookRatingsMap = new HashMap<>();
            for (BookRatings rating : bookRatingsList) {
                bookRatingsMap.put(rating.getBookId(), Double.valueOf(rating.getRating()));
            }

            // 对书籍列表按照评分进行排序
            unborrowedBooks.sort((book1, book2) -> {
                Double rating1 = bookRatingsMap.getOrDefault(book1.getBookId(), 0.0);
                Double rating2 = bookRatingsMap.getOrDefault(book2.getBookId(), 0.0);
                return Double.compare(rating2, rating1);
            });

            return AjaxResult.success("获取成功", unborrowedBooks);
        } catch (Exception e) {
            log.info("根据读者id获取推荐图书列表 报错:{}", e.getMessage());
            return AjaxResult.error("获取未借阅书籍并排序失败");
        }
    }


    /**
     * 根据借阅人ID查询图书副本信息列表
     */
//    @PreAuthorize("@ss.hasPermi('book:BookInfo:list')")
    @GetMapping("/listByReader")
    public TableDataInfo listByReader(BookBorrowing bookBorrowing) {
        startPage();
        bookBorrowing.setReaderId(SecurityUtils.getUserId()); // 设置当前用户ID
        List<BookBorrowing> list = bookBorrowingService.selectBookBorrowingListByReaderId(bookBorrowing);
        return getDataTable(list);
    }


    /**
     * 根据借阅人ID查询应还图书列表，并添加借阅状态
     */
//    @PreAuthorize("@ss.hasPermi('book:BookInfo:list')")
    @GetMapping("/returnListWithStatusByReader")
    public TableDataInfo returnListWithStatusByReader(BookBorrowing bookBorrowing) {
        BookBorrowingController.startPageByBorrowDateDesc();
        bookBorrowing.setReaderId(SecurityUtils.getUserId()); // 设置当前用户ID
        bookBorrowing.setPendingStatus(1L);
        List<BookBorrowing> list = bookBorrowingService.selectBookBorrowingByPendingStatusWithNullReturnDate(bookBorrowing);
        for (BookBorrowing borrowing : list) {
            borrowing.setStatus((long) BorrowUtil.getBorrowingStatus(borrowing));
        }
        return getDataTable(list);
    }


    /**
     * 导出图书副本信息列表
     */
//    @PreAuthorize("@ss.hasPermi('book:BookInfo:export')")
    @Log(title = "图书副本信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Books books) {
        List<Long> availableBookIDsList = bookStorageService.selectAvailableBookIDsList();
        List<Books> availableBooksList = booksService.selectBooksListByIds(availableBookIDsList);
        ExcelUtil<Books> util = new ExcelUtil<Books>(Books.class);
        util.exportExcel(response, availableBooksList, "图书信息数据");
    }

    /**
     * 获取图书副本信息详细信息
     */
//    @PreAuthorize("@ss.hasPermi('book:BookInfo:query')")
    @GetMapping(value = "/{bookId}")
    public AjaxResult getInfo(@PathVariable("bookId") Long bookId) {
        return success(booksService.selectBooksByBookId(bookId));
    }

    /**
     * 新增图书副本信息
     */
//    @PreAuthorize("@ss.hasPermi('book:BookInfo:add')")
    @Log(title = "图书副本信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BooksBO booksBO) {
        return booksService.addBooks(booksBO);
    }

    /**
     * 修改图书副本信息
     */
//    @PreAuthorize("@ss.hasPermi('book:BookInfo:edit')")
    @Log(title = "图书副本信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Books books) {
        return toAjax(booksService.updateBooks(books));
    }

    /**
     * 删除图书副本信息
     */
//    @PreAuthorize("@ss.hasPermi('book:BookInfo:remove')")
    @Log(title = "图书副本信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{bookIds}")
    public AjaxResult remove(@PathVariable Long[] bookIds) {
        return booksService.removeBooks(bookIds);
    }

    /**
     * 处理图书借阅
     * @return AjaxResult 响应结果
     */
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/borrow")
    public AjaxResult handleBorrow(@RequestBody BookBorrowing request) {
        return bookBorrowingService.borrowBook(request);
    }

    /**
     * 处理管理员确认图书归还
     * @return AjaxResult 响应结果
     */
    @Transactional
    @PostMapping("/return")
    public AjaxResult handleReturn(@RequestBody BookBorrowing request) {
        return bookBorrowingService.confirmReturnBook(request);
    }


    /**
     * 处理读者图书归还
     * @return AjaxResult 响应结果
     */
    @PostMapping("/readerReturn")
    public AjaxResult handleReaderReturn(@RequestBody BookBorrowing request) {
        return bookBorrowingService.returnBook(request);
    }



    /**
     * 处理借阅延期
     * @return AjaxResult 响应结果
     */
    @PostMapping("/extension")
    public AjaxResult handleExtension(@RequestBody BookBorrowing request) {
        return bookBorrowingService.handleExtension(request);
    }
}
