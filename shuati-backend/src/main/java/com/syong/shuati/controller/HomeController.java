package com.syong.shuati.controller;

import com.syong.shuati.common.BaseResponse;
import com.syong.shuati.common.ResultUtils;
import com.syong.shuati.model.vo.HomeStatisticsVO;
import com.syong.shuati.service.HomeStatisticsService;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 首页控制器
 */
@RestController
@RequestMapping("/home")
public class HomeController {

    @Resource
    private HomeStatisticsService homeStatisticsService;

    /**
     * @Description: 获取首页统计信息
     */
    @GetMapping("/statistics")
    public BaseResponse<HomeStatisticsVO> getHomeStatistics() {
        return ResultUtils.success(homeStatisticsService.getHomeStatistics());
    }
}
