package com.linkup;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@MapperScan({
        "com.linkup.activity.mapper",
        "com.linkup.account.auth.mapper",
        "com.linkup.finance.bank.mapper",
        "com.linkup.commerce.deal.mapper",
        "com.linkup.growth.invite.mapper",
        "com.linkup.commerce.merchant.mapper",
        "com.linkup.commerce.order.mapper",
        "com.linkup.account.user.mapper",
        "com.linkup.growth.voucher.mapper",
        "com.linkup.finance.wallet.mapper"
})
public class LinkUpApplication {

    public static void main(String[] args) {
        SpringApplication.run(LinkUpApplication.class, args);
    }
}
