package com.syong.shuati.service;

import com.syong.shuati.model.vo.HomeStatisticsVO;

/**
 * 首页统计服务
 *
 * @author  <a href="https://github.com/LightingForest">SYong</a>
 */
public interface HomeStatisticsService {

    /**
     * 获取首页统计信息
     * @return 首页统计信息
     */
    HomeStatisticsVO getHomeStatistics();
}
