package com.syong.shuati.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syong.shuati.common.ErrorCode;
import com.syong.shuati.exception.BusinessException;
import com.syong.shuati.exception.ThrowUtils;
import com.syong.shuati.mapper.QuestionTestRecordMapper;
import com.syong.shuati.model.dto.questionTest.QuestionTestRecordQueryRequest;
import com.syong.shuati.model.dto.questionTest.QuestionTestSubmitRequest;
import com.syong.shuati.model.dto.questionTest.QuestionTestUserAnswerDTO;
import com.syong.shuati.model.entity.Question;
import com.syong.shuati.model.entity.QuestionTest;
import com.syong.shuati.model.entity.QuestionTestItem;
import com.syong.shuati.model.entity.QuestionTestRecord;
import com.syong.shuati.model.entity.QuestionTestRecordDetail;
import com.syong.shuati.model.entity.User;
import com.syong.shuati.model.vo.QuestionTestItemResultVO;
import com.syong.shuati.model.vo.QuestionTestRecordVO;
import com.syong.shuati.model.vo.QuestionTestSubmitVO;
import com.syong.shuati.service.QuestionService;
import com.syong.shuati.service.QuestionTestItemService;
import com.syong.shuati.service.QuestionTestRecordDetailService;
import com.syong.shuati.service.QuestionTestRecordService;
import com.syong.shuati.service.QuestionTestService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 题目测试记录服务实现
 *
 * @author  <a href="https://github.com/LightingForest">SYong</a>
 */
@Service
public class QuestionTestRecordServiceImpl extends ServiceImpl<QuestionTestRecordMapper, QuestionTestRecord>
    implements QuestionTestRecordService {

    @Resource
    private QuestionTestService questionTestService;

    @Resource
    private QuestionTestItemService questionTestItemService;

    @Resource
    private QuestionTestRecordDetailService questionTestRecordDetailService;

    @Resource
    private QuestionService questionService;

    /**
     * 提交题目测试答案并计算成绩
     * 该方法会验证参数、保存测试记录、逐题判分、保存答题明细，并最终更新总分
     *
     * @param questionTestSubmitRequest 题目测试提交请求对象，包含测试题ID和用户答案列表
     * @param loginUser 当前登录用户对象，用于关联测试记录
     * @return 题目测试提交结果对象，包含测试记录、各题判分结果及总成绩
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuestionTestSubmitVO submitQuestionTest(QuestionTestSubmitRequest questionTestSubmitRequest, User loginUser) {
        ThrowUtils.throwIf(questionTestSubmitRequest == null || questionTestSubmitRequest.getQuestionTestId() == null
            || questionTestSubmitRequest.getQuestionTestId() <= 0, ErrorCode.PARAMS_ERROR);
        QuestionTest questionTest = questionTestService.getById(questionTestSubmitRequest.getQuestionTestId());
        ThrowUtils.throwIf(questionTest == null, ErrorCode.NOT_FOUND_ERROR, "测试不存在");
        ThrowUtils.throwIf(questionTest.getStatus() == null || questionTest.getStatus() != 1,
            ErrorCode.NO_AUTH_ERROR, "测试未启用");

        List<QuestionTestItem> questionTestItemList = questionTestItemService.list(Wrappers.lambdaQuery(QuestionTestItem.class)
            .eq(QuestionTestItem::getQuestionTestId, questionTest.getId())
            .orderByAsc(QuestionTestItem::getSort, QuestionTestItem::getId));
        ThrowUtils.throwIf(questionTestItemList.isEmpty(), ErrorCode.NOT_FOUND_ERROR, "测试题不存在");

        List<QuestionTestUserAnswerDTO> answerList = questionTestSubmitRequest.getAnswerList();
        ThrowUtils.throwIf(answerList == null || answerList.size() != questionTestItemList.size(),
            ErrorCode.PARAMS_ERROR, "提交答案数量不正确");

        Map<Long, String> userAnswerMap = answerList.stream().collect(Collectors.toMap(
            QuestionTestUserAnswerDTO::getQuestionTestItemId,
            item -> StringUtils.upperCase(StringUtils.trimToEmpty(item.getUserAnswer())),
            (a, b) -> b));

        int rightCount = 0;
        List<QuestionTestItemResultVO> itemResultVOList = new ArrayList<>();
        QuestionTestRecord questionTestRecord = new QuestionTestRecord();
        questionTestRecord.setQuestionId(questionTest.getQuestionId());
        questionTestRecord.setQuestionTestId(questionTest.getId());
        questionTestRecord.setUserId(loginUser.getId());
        questionTestRecord.setQuestionCount(questionTestItemList.size());
        questionTestRecord.setScore(0);
        questionTestRecord.setRightCount(0);
        boolean result = this.save(questionTestRecord);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "保存答题记录失败");

        List<QuestionTestRecordDetail> detailList = new ArrayList<>();
        for (QuestionTestItem item : questionTestItemList) {
            String userAnswer = userAnswerMap.get(item.getId());
            ThrowUtils.throwIf(userAnswer == null, ErrorCode.PARAMS_ERROR, "存在未作答题目");

            boolean isCorrect = userAnswer.equalsIgnoreCase(item.getAnswer());
            if (isCorrect) {
                rightCount++;
            }

            Map<String, String> options = buildOptions(item);
            itemResultVOList.add(buildItemResult(item.getId(), item.getTitle(), options, userAnswer, item.getAnswer(),
                isCorrect, item.getAnalysis()));

            QuestionTestRecordDetail detail = new QuestionTestRecordDetail();
            detail.setRecordId(questionTestRecord.getId());
            detail.setQuestionTestItemId(item.getId());
            detail.setTitleSnapshot(item.getTitle());
            detail.setOptionsSnapshot(JSONUtil.toJsonStr(options));
            detail.setUserAnswer(userAnswer);
            detail.setRightAnswer(item.getAnswer());
            detail.setIsCorrect(isCorrect ? 1 : 0);
            detail.setAnalysisSnapshot(item.getAnalysis());
            detailList.add(detail);
        }

        result = questionTestRecordDetailService.saveBatch(detailList);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "保存答题明细失败");

        questionTestRecord.setRightCount(rightCount);
        questionTestRecord.setScore(rightCount * 100 / questionTestItemList.size());
        result = this.updateById(questionTestRecord);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "更新答题记录失败");

        return buildSubmitVO(questionTestRecord, null, itemResultVOList);
    }


    /**
     * 根据记录ID获取题目测试提交详情
     * 支持管理员查看所有记录，普通用户仅能查看自己的记录，并进行权限校验
     *
     * @param recordId 测试记录ID，必须为正数
     * @param loginUser 当前登录用户对象，用于权限校验
     * @param isAdmin 是否为管理员，管理员可查看所有用户的测试记录
     * @return 题目测试提交结果对象，包含测试记录、各题判分结果及总成绩
     */
    @Override
    public QuestionTestSubmitVO getQuestionTestSubmitVOById(Long recordId, User loginUser, boolean isAdmin) {
        ThrowUtils.throwIf(recordId == null || recordId <= 0, ErrorCode.PARAMS_ERROR);
        QuestionTestRecord questionTestRecord = this.getById(recordId);
        ThrowUtils.throwIf(questionTestRecord == null, ErrorCode.NOT_FOUND_ERROR, "答题记录不存在");

        if (!isAdmin && !questionTestRecord.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        List<QuestionTestRecordDetail> detailList = questionTestRecordDetailService.list(Wrappers.lambdaQuery(QuestionTestRecordDetail.class)
            .eq(QuestionTestRecordDetail::getRecordId, recordId)
            .orderByAsc(QuestionTestRecordDetail::getId));

        List<QuestionTestItemResultVO> itemResultVOList = detailList.stream()
                .map(detail -> {
                    // 1. 显式声明选项 Map 的类型，辅助 Java 8 编译器推断
                    Map<String, String> options = JSONUtil.toBean(detail.getOptionsSnapshot(), LinkedHashMap.class);

                    // 2. 调用方法返回 VO
                    return buildItemResult(
                            detail.getQuestionTestItemId(),
                            detail.getTitleSnapshot(),
                            options,
                            detail.getUserAnswer(),
                            detail.getRightAnswer(),
                            detail.getIsCorrect() != null && detail.getIsCorrect() == 1,
                            detail.getAnalysisSnapshot());
                })
                // 3. 核心：在 collect 时显式指定泛型参数
                .collect(Collectors.<QuestionTestItemResultVO>toList());

        return buildSubmitVO(questionTestRecord, null, itemResultVOList);
    }

    /**
     * 分页获取当前用户的题目测试记录列表
     * 支持按题目ID筛选，按创建时间降序排列
     *
     * @param queryRequest 查询请求对象，包含分页参数（current、pageSize）及可选的题目ID筛选条件
     * @param loginUser 当前登录用户对象，用于查询该用户的测试记录
     * @return 分页后的用户题目测试记录列表，包含记录ID、题目信息、成绩等概要信息
     */
    @Override
    public Page<QuestionTestRecordVO> listMyQuestionTestRecordVOByPage(QuestionTestRecordQueryRequest queryRequest,
                                                                       User loginUser) {
        ThrowUtils.throwIf(queryRequest == null, ErrorCode.PARAMS_ERROR);
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();

        Page<QuestionTestRecord> recordPage = this.page(new Page<>(current, size),
            Wrappers.lambdaQuery(QuestionTestRecord.class)
                .eq(QuestionTestRecord::getUserId, loginUser.getId())
                .eq(queryRequest.getQuestionId() != null && queryRequest.getQuestionId() > 0,
                    QuestionTestRecord::getQuestionId, queryRequest.getQuestionId())
                .orderByDesc(QuestionTestRecord::getCreateTime, QuestionTestRecord::getId));

        Page<QuestionTestRecordVO> voPage = new Page<>(current, size, recordPage.getTotal());
        List<QuestionTestRecord> recordList = recordPage.getRecords();
        if (recordList.isEmpty()) {
            voPage.setRecords(Collections.emptyList());
            return voPage;
        }

        Set<Long> questionIdSet = recordList.stream().map(QuestionTestRecord::getQuestionId).collect(Collectors.toSet());
        Map<Long, Question> questionMap = questionService.listByIds(questionIdSet).stream()
            .collect(Collectors.toMap(Question::getId, Function.identity(), (a, b) -> a));

        List<QuestionTestRecordVO> voList = recordList.stream().map(record -> {
            QuestionTestRecordVO vo = new QuestionTestRecordVO();
            vo.setRecordId(record.getId());
            vo.setQuestionId(record.getQuestionId());
            Question question = questionMap.get(record.getQuestionId());
            vo.setQuestionTitle(question == null ? "题目测试" : question.getTitle());
            vo.setScore(record.getScore());
            vo.setRightCount(record.getRightCount());
            vo.setQuestionCount(record.getQuestionCount());
            vo.setCreateTime(record.getCreateTime());
            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 将测试题目的选项组装成Map格式
     * 固定包含A-D四个选项，若存在E选项则动态添加
     *
     * @param item 测试题目实体对象，包含各个选项的内容
     * @return 选项Map，键为选项标识（A/B/C/D/E），值为选项内容
     */
    private Map<String, String> buildOptions(QuestionTestItem item) {
        Map<String, String> options = new LinkedHashMap<>();
        options.put("A", item.getOptionA());
        options.put("B", item.getOptionB());
        options.put("C", item.getOptionC());
        options.put("D", item.getOptionD());
        if (StringUtils.isNotBlank(item.getOptionE())) {
            options.put("E", item.getOptionE());
        }
        return options;
    }


    /**
     * 构建单道题目的判分结果对象
     *
     * @param itemId 测试题目项ID
     * @param title 题目标题
     * @param options 题目选项
     * @param userAnswer 用户答案
     * @param rightAnswer 正确答案
     * @param isCorrect 是否回答正确
     * @param analysis 题目解析
     * @return 单道题目判分结果对象，包含题目信息、用户答案、正确答案及解析
     */
    private QuestionTestItemResultVO buildItemResult(Long itemId, String title, Map<String, String> options,
                                                     String userAnswer, String rightAnswer, boolean isCorrect,
                                                     String analysis) {
        QuestionTestItemResultVO itemResultVO = new QuestionTestItemResultVO();
        itemResultVO.setQuestionTestItemId(itemId);
        itemResultVO.setTitle(title);
        itemResultVO.setOptions(options);
        itemResultVO.setUserAnswer(userAnswer);
        itemResultVO.setRightAnswer(rightAnswer);
        itemResultVO.setIsCorrect(isCorrect);
        itemResultVO.setAnalysis(analysis);
        return itemResultVO;
    }

    /**
     * 构建题目测试提交结果对象
     *
     * @param questionTestRecord 题目测试记录实体，包含测试成绩、答题数量等统计信息
     * @param questionTitle 题目标题（此处未使用，保留参数以维持接口一致性）
     * @param itemResultVOList 各题目判分结果列表
     * @return 题目测试提交结果对象，包含完整的测试记录和答题详情
     */
    private QuestionTestSubmitVO buildSubmitVO(QuestionTestRecord questionTestRecord, String questionTitle,
                                               List<QuestionTestItemResultVO> itemResultVOList) {
        QuestionTestSubmitVO submitVO = new QuestionTestSubmitVO();
        submitVO.setRecordId(questionTestRecord.getId());
        submitVO.setQuestionId(questionTestRecord.getQuestionId());
        submitVO.setQuestionTestId(questionTestRecord.getQuestionTestId());
        submitVO.setScore(questionTestRecord.getScore());
        submitVO.setRightCount(questionTestRecord.getRightCount());
        submitVO.setQuestionCount(questionTestRecord.getQuestionCount());
        submitVO.setItemResultList(itemResultVOList);
        return submitVO;
    }


}
