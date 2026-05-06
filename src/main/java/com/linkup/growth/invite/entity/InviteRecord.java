package com.linkup.growth.invite.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

/**
 * @TableName invite_record
 */
@TableName(value ="invite_record")
public class InviteRecord implements Serializable {
    private Object id;

    private Object inviterId;

    private Object inviteeId;

    private Object status;

    private Object inviteeFirstOrderId;

    private Integer rewardAmount;

    private Date rewardedAt;

    private Date createdAt;

    private static final long serialVersionUID = 1L;

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public Object getInviterId() {
        return inviterId;
    }

    public void setInviterId(Object inviterId) {
        this.inviterId = inviterId;
    }

    public Object getInviteeId() {
        return inviteeId;
    }

    public void setInviteeId(Object inviteeId) {
        this.inviteeId = inviteeId;
    }

    public Object getStatus() {
        return status;
    }

    public void setStatus(Object status) {
        this.status = status;
    }

    public Object getInviteeFirstOrderId() {
        return inviteeFirstOrderId;
    }

    public void setInviteeFirstOrderId(Object inviteeFirstOrderId) {
        this.inviteeFirstOrderId = inviteeFirstOrderId;
    }

    public Integer getRewardAmount() {
        return rewardAmount;
    }

    public void setRewardAmount(Integer rewardAmount) {
        this.rewardAmount = rewardAmount;
    }

    public Date getRewardedAt() {
        return rewardedAt;
    }

    public void setRewardedAt(Date rewardedAt) {
        this.rewardedAt = rewardedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}



