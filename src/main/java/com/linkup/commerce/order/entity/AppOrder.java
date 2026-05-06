package com.linkup.commerce.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

/**
 * @TableName app_order
 */
@TableName(value ="app_order")
public class AppOrder implements Serializable {
    private Object id;

    private Object userId;

    private Object dealId;

    private String orderNo;

    private String paymentNo;

    private Integer amountPaid;

    private String paymentReference;

    private Object paymentStatus;

    private String redeemCode;

    private Object redeemStatus;

    private Date redeemedAt;

    private Object refundStatus;

    private String refundReason;

    private String afterSaleNote;

    private String usageWindow;

    private String usageRule;

    private Object packageSnapshot;

    private Date expiresAt;

    private Date createdAt;

    private static final long serialVersionUID = 1L;

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public Object getUserId() {
        return userId;
    }

    public void setUserId(Object userId) {
        this.userId = userId;
    }

    public Object getDealId() {
        return dealId;
    }

    public void setDealId(Object dealId) {
        this.dealId = dealId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getPaymentNo() {
        return paymentNo;
    }

    public void setPaymentNo(String paymentNo) {
        this.paymentNo = paymentNo;
    }

    public Integer getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(Integer amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public Object getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(Object paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getRedeemCode() {
        return redeemCode;
    }

    public void setRedeemCode(String redeemCode) {
        this.redeemCode = redeemCode;
    }

    public Object getRedeemStatus() {
        return redeemStatus;
    }

    public void setRedeemStatus(Object redeemStatus) {
        this.redeemStatus = redeemStatus;
    }

    public Date getRedeemedAt() {
        return redeemedAt;
    }

    public void setRedeemedAt(Date redeemedAt) {
        this.redeemedAt = redeemedAt;
    }

    public Object getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(Object refundStatus) {
        this.refundStatus = refundStatus;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public String getAfterSaleNote() {
        return afterSaleNote;
    }

    public void setAfterSaleNote(String afterSaleNote) {
        this.afterSaleNote = afterSaleNote;
    }

    public String getUsageWindow() {
        return usageWindow;
    }

    public void setUsageWindow(String usageWindow) {
        this.usageWindow = usageWindow;
    }

    public String getUsageRule() {
        return usageRule;
    }

    public void setUsageRule(String usageRule) {
        this.usageRule = usageRule;
    }

    public Object getPackageSnapshot() {
        return packageSnapshot;
    }

    public void setPackageSnapshot(Object packageSnapshot) {
        this.packageSnapshot = packageSnapshot;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}



