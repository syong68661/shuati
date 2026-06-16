package com.syong.shuati.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.syong.shuati.model.entity.Question;
import com.syong.shuati.model.entity.QuestionBankTestRecord;
import com.syong.shuati.model.entity.QuestionTestRecord;
import com.syong.shuati.model.entity.User;
import com.syong.shuati.model.vo.HomeStatisticsVO;
import com.syong.shuati.model.vo.HotQuestionVO;
import com.syong.shuati.model.vo.HotTagVO;
import com.syong.shuati.model.vo.TopUserVO;
import com.syong.shuati.service.HomeStatisticsService;
import com.syong.shuati.service.QuestionBankTestRecordService;
import com.syong.shuati.service.QuestionService;
import com.syong.shuati.service.QuestionTestRecordService;
import com.syong.shuati.service.UserService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class HomeStatisticsServiceImpl implements HomeStatisticsService {

    private static final int TOP_LIMIT = 10;

    @Resource
    private QuestionTestRecordService questionTestRecordService;

    @Resource
    private QuestionBankTestRecordService questionBankTestRecordService;

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    /**
     * 获取首页统计信息
     *
     * @return 首页统计信息
     */
    @Override
    public HomeStatisticsVO getHomeStatistics() {
        HomeStatisticsVO vo = new HomeStatisticsVO();
        vo.setHotQuestionList(listHotQuestionVO());
        vo.setTopUserList(listTopUserVO());
        vo.setHotTagList(listHotTagVO());
        return vo;
    }

    /**
     * 获取热门题目列表
     * 根据答题记录统计出现次数最多的题目，返回TOP_LIMIT条热门题目信息
     *
     * @return 热门题目列表，按热度分数（答题次数）降序排列
     */
    private List<HotQuestionVO> listHotQuestionVO() {
        QueryWrapper<QuestionTestRecord> wrapper = new QueryWrapper<>();
        wrapper.select("questionId", "count(*) as hotScore")
                .groupBy("questionId")
                .orderByDesc("hotScore")
                .last("limit " + TOP_LIMIT);

        List<Map<String, Object>> mapList = questionTestRecordService.listMaps(wrapper);
        if (mapList.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> questionIdList = mapList.stream()
                .map(map -> Long.valueOf(String.valueOf(map.get("questionId"))))
                .collect(Collectors.toList());

        Map<Long, Question> questionMap = questionService.listByIds(questionIdList).stream()
                .collect(Collectors.toMap(Question::getId, Function.identity(), (a, b) -> a));
        return mapList.stream().map(map -> {
            Long questionId = Long.valueOf(String.valueOf(map.get("questionId")));
            HotQuestionVO hotQuestionVO = new HotQuestionVO();
            hotQuestionVO.setQuestionId(questionId);
            hotQuestionVO.setHotScore(Long.valueOf(String.valueOf(map.get("hotScore"))));
            Question question = questionMap.get(questionId);
            hotQuestionVO.setTitle(question == null ? "题目" : question.getTitle());
            return hotQuestionVO;
        }).collect(Collectors.toList());
    }


    /**
     * 获取热门用户排行榜
     * 统计用户在题目测试和题库测试中的总答题次数，按答题数量降序排列返回TOP_LIMIT条热门用户信息
     *
     * @return 热门用户列表，按解题数量降序排列
     */
    private List<TopUserVO> listTopUserVO() {
        QueryWrapper<QuestionTestRecord> questionWrapper = new QueryWrapper<>();
        questionWrapper.select("userId", "count(*) as solveCount").groupBy("userId");
        List<Map<String, Object>> questionMapList = questionTestRecordService.listMaps(questionWrapper);

        QueryWrapper<QuestionBankTestRecord> bankWrapper = new QueryWrapper<>();
        bankWrapper.select("userId", "count(*) as solveCount").groupBy("userId");
        List<Map<String, Object>> bankMapList = questionBankTestRecordService.listMaps(bankWrapper);

        Map<Long, Long> userSolveCountMap = questionMapList.stream().collect(Collectors.toMap(
            map -> Long.valueOf(String.valueOf(map.get("userId"))),
            map -> Long.valueOf(String.valueOf(map.get("solveCount"))),
            Long::sum));

        for (Map<String, Object> map : bankMapList) {
            Long userId = Long.valueOf(String.valueOf(map.get("userId")));
            Long solveCount = Long.valueOf(String.valueOf(map.get("solveCount")));
            userSolveCountMap.merge(userId, solveCount, Long::sum);
        }
        if (userSolveCountMap.isEmpty()) {
            return Collections.emptyList();
        }

        List<Map.Entry<Long, Long>> sortedEntryList = new ArrayList<>(userSolveCountMap.entrySet());
        sortedEntryList.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));
        if (sortedEntryList.size() > TOP_LIMIT) {
            sortedEntryList = sortedEntryList.subList(0, TOP_LIMIT);
        }

        Set<Long> userIdSet = sortedEntryList.stream().map(Map.Entry::getKey).collect(Collectors.toSet());
        Map<Long, User> userMap = userService.listByIds(userIdSet).stream()
            .collect(Collectors.toMap(User::getId, Function.identity(), (a, b) -> a));
        return sortedEntryList.stream().map(entry -> {
            TopUserVO vo = new TopUserVO();
            vo.setUserId(entry.getKey());
            vo.setSolveCount(entry.getValue());
            User user = userMap.get(entry.getKey());
            vo.setUserName(user == null ? "用户" : user.getUserName());
            vo.setUserAvatar(user == null ? null : user.getUserAvatar());
            return vo;
        }).collect(Collectors.toList());
    }


    /**
     * 获取热门标签排行榜
     * 统计所有题目中各个标签的使用次数，按使用频率降序排列返回TOP_LIMIT条热门标签信息
     *
     * @return 热门标签列表，按使用次数降序排列
     */
    private List<HotTagVO> listHotTagVO() {
        List<Question> questionList = questionService.list();
        if (questionList.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Long> tagCountMap = questionList.stream()
            .filter(question -> StringUtils.isNotBlank(question.getTags()))
            .flatMap(question -> JSONUtil.toList(question.getTags(), String.class).stream())
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        return tagCountMap.entrySet().stream()
            .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
            .limit(TOP_LIMIT)
            .map(entry -> {
                HotTagVO vo = new HotTagVO();
                vo.setTagName(entry.getKey());
                vo.setUseCount(entry.getValue());
                return vo;
            }).collect(Collectors.toList());
    }


}
