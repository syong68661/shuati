package com.syong.shuati.config;

import com.volcengine.ark.runtime.service.ArkService;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

//@Configuration
//@ConfigurationProperties(prefix = "ai")
public class AiConfig {

//    // 必须要有 getter / setter
//    private String apiKey;
//
//    public String getApiKey() {
//        return apiKey;
//    }
//
//    public void setApiKey(String apiKey) {
//        this.apiKey = apiKey;
//    }
//
//    /**
//     * ai请求客户端
//     * @return
//     */
//    @Bean
//    public ArkService arkService() {
//
//        // 防止配置没加载（非常重要）
//        if (apiKey == null || apiKey.isEmpty()) {
//            throw new RuntimeException("AI apiKey 未配置！");
//        }
//
//        ConnectionPool connectionPool = new ConnectionPool(5, 1, TimeUnit.SECONDS);
//        Dispatcher dispatcher = new Dispatcher();
//
//        return ArkService.builder()
//                .dispatcher(dispatcher)
//                .connectionPool(connectionPool)
//                .baseUrl("https://ark.cn-beijing.volces.com/api/v3")
//                .apiKey(apiKey)
//                .build();
//    }
}