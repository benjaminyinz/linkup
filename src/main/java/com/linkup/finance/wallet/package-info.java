/**
 * 钱包流水子域：reference_type 多态指向 order / invite / payout 等多种来源。
 *
 * <p>余额扣减必须配合乐观锁（@Version）+ 分布式锁（@link com.linkup.infrastructure.lock.DistributedLock）
 * 防止并发场景下的丢失更新。</p>
 */
package com.linkup.finance.wallet;
