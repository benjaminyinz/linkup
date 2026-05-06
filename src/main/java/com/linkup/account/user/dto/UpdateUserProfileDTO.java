package com.linkup.account.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新个人资料请求参数。
 *
 * <p>这个 DTO 对应 material/api_profile(1).md 里的 PUT /api/user/me。
 * 所有字段都是可选的，前端只需要传本次要修改的字段。</p>
 */
@Data
@Schema(description = "更新个人资料请求参数")
public class UpdateUserProfileDTO {

    @Size(max = 50, message = "昵称不能超过 50 个字符")
    @Schema(description = "昵称", example = "Ben")
    private String nickname;

    @Pattern(regexp = "male|female", message = "性别只能是 male 或 female")
    @Schema(description = "性别：male / female", example = "male")
    private String gender;

    @Size(max = 100, message = "个性签名不能超过 100 个字符")
    @Schema(description = "个性签名", example = "Auckland 里，总有人和你同行")
    private String signature;
}
