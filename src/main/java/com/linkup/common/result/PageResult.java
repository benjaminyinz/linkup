package com.linkup.common.result;

import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * 分页响应对象。
 *
 * <p>列表接口通常不只返回 records，还要告诉前端总数、当前页、每页数量。
 * 这个对象统一承载分页信息，外层仍然使用 Result 包起来。</p>
 *
 * <pre>
 * {
 *   "code": 0,
 *   "message": "ok",
 *   "data": {
 *     "total": 100,
 *     "page": 1,
 *     "pageSize": 20,
 *     "records": []
 *   }
 * }
 * </pre>
 */
@Data
@Builder
public class PageResult<T> {

    /**
     * 总记录数。
     */
    private Long total;

    /**
     * 当前页码，从 1 开始。
     */
    private Long page;

    /**
     * 每页数量。
     */
    private Long pageSize;

    /**
     * 当前页数据。
     */
    private List<T> records;
}
