package com.ruoyi.borrow.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 图书借阅信息对象 BookBorrowing
 *
 * @author ruoyi
 * @date 2024-03-12
 */
public class BookBorrowing extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 借阅号
     */
    @Excel(name = "借阅号")
    private Long borrowId;

    /**
     * 书籍ID
     */
    @Excel(name = "书籍ID")
    private Long bookId;

    /**
     * 书名
     */
    @Excel(name = "书名")
    private String title;

    /**
     * 书的类别
     */
    @Excel(name = "类别")
    private String category;
    /**
     * 读者ID
     */
    @Excel(name = "读者ID")
    private Long readerId;
    /**
     * 图书馆ID
     */
    @Excel(name = "图书馆ID")
    private Long libraryId;
    /**
     * 图书馆名称
     */
    private String deptName;
    /**
     * 借出日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "借出日期", width = 30, dateFormat = "yyyy-MM-dd")
    private LocalDate borrowDate;
    /**
     * 应还日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "应还日期", width = 30, dateFormat = "yyyy-MM-dd")
    private LocalDate dueDate;
    /**
     * 实际还书日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "实际还书日期", width = 30, dateFormat = "yyyy-MM-dd")
    private LocalDate returnDate;
    /**
     * 逾期罚款
     */
    @Excel(name = "逾期罚款")
    private BigDecimal fine;
    /**
     * 借阅备注
     */
    @Excel(name = "借阅备注")
    private String comments;
    /**
     * 借阅状态
     */
    @Excel(name = "借阅状态")
    private Long status;
    /**
     * 审核状态
     */
    @Excel(name = "审核状态")
    private Long pendingStatus;
    /**
     * 运单号
     */
    @Excel(name = "运单号")
    private String trackingNumber;
    /**
     * 归还方式
     */
    @Excel(name = "归还方式")
    private Long returnMethod;
    /**
     * 取阅方式
     */
    @Excel(name = "取阅方式")
    private Long borrowMethod;
    private String coverUrl;

    public Long getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(Long borrowId) {
        this.borrowId = borrowId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getReaderId() {
        return readerId;
    }

    public void setReaderId(Long readerId) {
        this.readerId = readerId;
    }

    public Long getLibraryId() {
        return libraryId;
    }

    public void setLibraryId(Long libraryId) {
        this.libraryId = libraryId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public BigDecimal getFine() {
        return fine;
    }

    public void setFine(BigDecimal fine) {
        this.fine = fine;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getPendingStatus() {
        return pendingStatus;
    }

    public void setPendingStatus(Long pendingStatus) {
        this.pendingStatus = pendingStatus;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public Long getReturnMethod() {
        return returnMethod;
    }

    public void setReturnMethod(Long returnMethod) {
        this.returnMethod = returnMethod;
    }

    public Long getBorrowMethod() {
        return borrowMethod;
    }

    public void setBorrowMethod(Long borrowMethod) {
        this.borrowMethod = borrowMethod;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    @Override
    public String toString() {
        return "BookBorrowing{" +
                "borrowId=" + borrowId +
                ", bookId=" + bookId +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", readerId=" + readerId +
                ", libraryId=" + libraryId +
                ", deptName='" + deptName + '\'' +
                ", borrowDate=" + borrowDate +
                ", dueDate=" + dueDate +
                ", returnDate=" + returnDate +
                ", fine=" + fine +
                ", comments='" + comments + '\'' +
                ", status=" + status +
                ", pendingStatus=" + pendingStatus +
                ", trackingNumber='" + trackingNumber + '\'' +
                ", returnMethod=" + returnMethod +
                ", borrowMethod=" + borrowMethod +
                ", coverUrl='" + coverUrl + '\'' +
                '}';
    }
}
