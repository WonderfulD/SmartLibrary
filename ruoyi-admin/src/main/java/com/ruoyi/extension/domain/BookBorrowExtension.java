package com.ruoyi.extension.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 图书借阅延期对象 book_borrow_extension
 * @author ruoyi
 * @date 2025-04-12
 */
public class BookBorrowExtension extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 延期记录ID */
    private Long id;

    /** 图书ID */
    @Excel(name = "图书ID")
    private Long bookId;

    /** 借阅记录 ID */
    @Excel(name = "借阅记录 ID")
    private Long borrowId;

    /** 延期发起时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "延期发起时间", width = 30, dateFormat = "yyyy-MM-dd")
    private LocalDateTime requestTime;

    /** 延期审核通过时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "延期审核通过时间", width = 30, dateFormat = "yyyy-MM-dd")
    private LocalDateTime approvalTime;

    /** 申请延期到的时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "申请延期到的时间", width = 30, dateFormat = "yyyy-MM-dd")
    private LocalDate requestedDueTime;

    /** 审核通过后实际延期到的时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "审核通过后实际延期到的时间", width = 30, dateFormat = "yyyy-MM-dd")
    private LocalDate approvedDueTime;

    /** 延期是否通过 */
    @Excel(name = "延期是否通过")
    private Integer status;

    /** 审核附言 */
    @Excel(name = "审核附言")
    private String approvalComment;

    /** 记录创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "记录创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private LocalDateTime gmtCreated;

    /** 记录最后修改时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "记录最后修改时间", width = 30, dateFormat = "yyyy-MM-dd")
    private LocalDateTime gmtModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Long getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(Long borrowId) {
        this.borrowId = borrowId;
    }

    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }

    public LocalDateTime getApprovalTime() {
        return approvalTime;
    }

    public void setApprovalTime(LocalDateTime approvalTime) {
        this.approvalTime = approvalTime;
    }

    public LocalDate getRequestedDueTime() {
        return requestedDueTime;
    }

    public void setRequestedDueTime(LocalDate requestedDueTime) {
        this.requestedDueTime = requestedDueTime;
    }

    public LocalDate getApprovedDueTime() {
        return approvedDueTime;
    }

    public void setApprovedDueTime(LocalDate approvedDueTime) {
        this.approvedDueTime = approvedDueTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getApprovalComment() {
        return approvalComment;
    }

    public void setApprovalComment(String approvalComment) {
        this.approvalComment = approvalComment;
    }

    public LocalDateTime getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(LocalDateTime gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    @Override
    public String toString() {
        return "BookBorrowExtension{" +
                "id=" + id +
                ", bookId=" + bookId +
                ", borrowId=" + borrowId +
                ", requestTime=" + requestTime +
                ", approvalTime=" + approvalTime +
                ", requestedDueTime=" + requestedDueTime +
                ", approvedDueTime=" + approvedDueTime +
                ", status=" + status +
                ", approvalComment='" + approvalComment + '\'' +
                ", gmtCreated=" + gmtCreated +
                ", gmtModified=" + gmtModified +
                '}';
    }
}
