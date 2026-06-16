package com.syong.shuati.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syong.shuati.common.ErrorCode;
import com.syong.shuati.exception.BusinessException;
import com.syong.shuati.exception.ThrowUtils;
import com.syong.shuati.mapper.QuestionBankTestRecordMapper;
import com.syong.shuati.model.dto.questionBankTest.QuestionBankTestRecordQueryRequest;
import com.syong.shuati.model.dto.questionBankTest.QuestionBankTestSubmitRequest;
import com.syong.shuati.model.dto.questionBankTest.QuestionBankTestUserAnswerDTO;
import com.syong.shuati.model.entity.Question;
import com.syong.shuati.model.entity.QuestionBank;
import com.syong.shuati.model.entity.QuestionBankQuestion;
import com.syong.shuati.model.entity.QuestionBankTestRecord;
import com.syong.shuati.model.entity.QuestionBankTestRecordDetail;
import com.syong.shuati.model.entity.QuestionTest;
import com.syong.shuati.model.entity.QuestionTestItem;
import com.syong.shuati.model.entity.User;
import com.syong.shuati.model.vo.QuestionBankTestItemVO;
import com.syong.shuati.model.vo.QuestionBankTestRecordVO;
import com.syong.shuati.model.vo.QuestionBankTestSubmitVO;
import com.syong.shuati.model.vo.QuestionBankTestVO;
import com.syong.shuati.model.vo.QuestionTestItemResultVO;
import com.syong.shuati.service.QuestionBankQuestionService;
import com.syong.shuati.service.QuestionBankService;
import com.syong.shuati.service.QuestionBankTestRecordDetailService;
import com.syong.shuati.service.QuestionBankTestRecordService;
import com.syong.shuati.service.QuestionService;
import com.syong.shuati.service.QuestionTestItemService;
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
 * 题库测试记录服务实现类
 */
@Service
public class QuestionBankTestRecordServiceImpl extends ServiceImpl<QuestionBankTestRecordMapper, QuestionBankTestRecord>
    implements QuestionBankTestRecordService {

    private static final int DEFAULT_BANK_TEST_ITEM_LIMIT = 10;

    @Resource
    private QuestionBankService questionBankService;

    @Resource
    private QuestionBankQuestionService questionBankQuestionService;

    @Resource
    private QuestionTestService questionTestService;

    @Resource
    private QuestionTestItemService questionTestItemService;

    @Resource
    private QuestionBankTestRecordDetailService questionBankTestRecordDetailService;

    @Resource
    private QuestionService questionService;


    /**
     * 根据题库ID获取题库测试信息
     *
     * @param questionBankId 题库ID，必须为正数
     * @return 题库测试信息对象，包含题库标题、题目数量及题目列表；若题库下无题目则返回null
     */
    @Override
    public QuestionBankTestVO getQuestionBankTestVOByQuestionBankId(Long questionBankId) {
        ThrowUtils.throwIf(questionBankId == null || questionBankId <= 0, ErrorCode.PARAMS_ERROR);
        QuestionBank questionBank = questionBankService.getById(questionBankId);
        ThrowUtils.throwIf(questionBank == null, ErrorCode.NOT_FOUND_ERROR, "题库不存在");

        List<QuestionBankTestItemVO> itemVOList = listQuestionBankTestItems(questionBankId);
        if (itemVOList.isEmpty()) {
            return null;
        }

        QuestionBankTestVO vo = new QuestionBankTestVO();
        vo.setQuestionBankId(questionBankId);
        vo.setQuestionBankTitle(questionBank.getTitle());
        vo.setQuestionCount(itemVOList.size());
        vo.setItemList(itemVOList);
        return vo;
    }


    /**
     * 提交题库测试答案并计算成绩
     * 该方法会验证参数、保存测试记录、逐题判分、保存答题明细，并最终更新总分
     *
     * @param request 题库测试提交请求对象，包含题库ID和用户答案列表
     * @param loginUser 当前登录用户对象，用于关联测试记录
     * @return 题库测试提交结果对象，包含测试记录、各题判分结果及总成绩
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public QuestionBankTestSubmitVO submitQuestionBankTest(QuestionBankTestSubmitRequest request, User loginUser) {
        ThrowUtils.throwIf(request == null || request.getQuestionBankId() == null || request.getQuestionBankId() <= 0,
            ErrorCode.PARAMS_ERROR);
        QuestionBank questionBank = questionBankService.getById(request.getQuestionBankId());
        ThrowUtils.throwIf(questionBank == null, ErrorCode.NOT_FOUND_ERROR, "题库不存在");

        List<QuestionBankTestItemVO> itemVOList = listQuestionBankTestItems(request.getQuestionBankId());
        ThrowUtils.throwIf(itemVOList.isEmpty(), ErrorCode.NOT_FOUND_ERROR, "当前题库暂无可用测试");

        List<QuestionBankTestUserAnswerDTO> answerList = request.getAnswerList();
        ThrowUtils.throwIf(answerList == null || answerList.size() != itemVOList.size(), ErrorCode.PARAMS_ERROR,
            "提交答案数量不正确");

        Map<Long, String> userAnswerMap = answerList.stream().collect(Collectors.toMap(
            QuestionBankTestUserAnswerDTO::getQuestionTestItemId,
            item -> StringUtils.upperCase(StringUtils.trimToEmpty(item.getUserAnswer())),
            (a, b) -> b));

        QuestionBankTestRecord record = new QuestionBankTestRecord();
        record.setQuestionBankId(questionBank.getId());
        record.setUserId(loginUser.getId());
        record.setScore(0);
        record.setRightCount(0);
        record.setQuestionCount(itemVOList.size());
        boolean result = this.save(record);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "保存题库测试记录失败");

        int rightCount = 0;
        List<QuestionBankTestRecordDetail> detailList = new ArrayList<>();
        List<QuestionTestItemResultVO> itemResultList = new ArrayList<>();
        Map<Long, QuestionBankTestItemVO> itemVOMap = itemVOList.stream()
            .collect(Collectors.toMap(QuestionBankTestItemVO::getQuestionTestItemId, Function.identity()));
        Set<Long> itemIdSet = itemVOMap.keySet();
        Map<Long, QuestionTestItem> itemEntityMap = questionTestItemService.listByIds(itemIdSet).stream()
            .collect(Collectors.toMap(QuestionTestItem::getId, Function.identity(), (a, b) -> a));

        for (QuestionBankTestItemVO itemVO : itemVOList) {
            String userAnswer = userAnswerMap.get(itemVO.getQuestionTestItemId());
            ThrowUtils.throwIf(userAnswer == null, ErrorCode.PARAMS_ERROR, "存在未作答题目");

            QuestionTestItem itemEntity = itemEntityMap.get(itemVO.getQuestionTestItemId());
            ThrowUtils.throwIf(itemEntity == null, ErrorCode.NOT_FOUND_ERROR, "测试题不存在");

            boolean isCorrect = userAnswer.equalsIgnoreCase(itemEntity.getAnswer());
            if (isCorrect) {
                rightCount++;
            }
            itemResultList.add(buildItemResult(itemVO.getQuestionTestItemId(), itemVO.getTitle(), itemVO.getOptions(),
                userAnswer, itemEntity.getAnswer(), isCorrect, itemEntity.getAnalysis()));

            QuestionBankTestRecordDetail detail = new QuestionBankTestRecordDetail();
            detail.setRecordId(record.getId());
            detail.setQuestionId(itemVO.getQuestionId());
            detail.setQuestionTestId(itemVO.getQuestionTestId());
            detail.setQuestionTestItemId(itemVO.getQuestionTestItemId());
            detail.setTitleSnapshot(itemVO.getTitle());
            detail.setOptionsSnapshot(JSONUtil.toJsonStr(itemVO.getOptions()));
            detail.setUserAnswer(userAnswer);
            detail.setRightAnswer(itemEntity.getAnswer());
            detail.setIsCorrect(isCorrect ? 1 : 0);
            detail.setAnalysisSnapshot(itemEntity.getAnalysis());
            detailList.add(detail);
        }

        result = questionBankTestRecordDetailService.saveBatch(detailList);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "保存题库测试明细失败");

        record.setRightCount(rightCount);
        record.setScore(rightCount * 100 / itemVOList.size());
        result = this.updateById(record);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "更新题库测试记录失败");

        return buildSubmitVO(record, questionBank.getTitle(), itemResultList);
    }


    /**
     * 根据记录ID获取题库测试提交详情
     * 支持管理员查看所有记录，普通用户仅能查看自己的记录，并进行权限校验
     *
     * @param recordId 测试记录ID，必须为正数
     * @param loginUser 当前登录用户对象，用于权限校验
     * @param isAdmin 是否为管理员，管理员可查看所有用户的测试记录
     * @return 题库测试提交结果对象，包含测试记录、各题判分结果及总成绩
     */
    @Override
    public QuestionBankTestSubmitVO getQuestionBankTestSubmitVOById(Long recordId, User loginUser, boolean isAdmin) {
        ThrowUtils.throwIf(recordId == null || recordId <= 0, ErrorCode.PARAMS_ERROR);
        QuestionBankTestRecord record = this.getById(recordId);
        ThrowUtils.throwIf(record == null, ErrorCode.NOT_FOUND_ERROR, "答题记录不存在");

        if (!isAdmin && !record.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        QuestionBank questionBank = questionBankService.getById(record.getQuestionBankId());
        List<QuestionBankTestRecordDetail> detailList = questionBankTestRecordDetailService.list(
                Wrappers.lambdaQuery(QuestionBankTestRecordDetail.class)
                        .eq(QuestionBankTestRecordDetail::getRecordId, recordId)
                        .orderByAsc(QuestionBankTestRecordDetail::getId));

        List<QuestionTestItemResultVO> itemResultVOList = detailList.stream()
                .map(detail -> {
                    Map<String, String> options = JSONUtil.toBean(detail.getOptionsSnapshot(), LinkedHashMap.class);
                    return buildItemResult(
                            detail.getQuestionTestItemId(),
                            detail.getTitleSnapshot(),
                            options,
                            detail.getUserAnswer(),
                            detail.getRightAnswer(),
                            detail.getIsCorrect() != null && detail.getIsCorrect() == 1,
                            detail.getAnalysisSnapshot());
                })
                .collect(Collectors.<QuestionTestItemResultVO>toList());

        // 必须确保返回时使用的变量名是 itemResultVOList
        return buildSubmitVO(record, questionBank == null ? "题库测试" : questionBank.getTitle(), itemResultVOList);
    }


    /**
     * 分页获取当前用户的题库测试记录列表
     * 支持按题库ID筛选，按创建时间降序排列
     *
     * @param queryRequest 查询请求对象，包含分页参数（current、pageSize）及可选的题库ID筛选条件
     * @param loginUser 当前登录用户对象，用于查询该用户的测试记录
     * @return 分页后的用户题库测试记录列表，包含记录ID、题库信息、成绩等概要信息
     */
    @Override
    public Page<QuestionBankTestRecordVO> listMyQuestionBankTestRecordVOByPage(
        QuestionBankTestRecordQueryRequest queryRequest, User loginUser) {
        ThrowUtils.throwIf(queryRequest == null, ErrorCode.PARAMS_ERROR);
        long current = queryRequest.getCurrent();
        long size = queryRequest.getPageSize();

        Page<QuestionBankTestRecord> recordPage = this.page(new Page<>(current, size),
            Wrappers.lambdaQuery(QuestionBankTestRecord.class)
                .eq(QuestionBankTestRecord::getUserId, loginUser.getId())
                .eq(queryRequest.getQuestionBankId() != null && queryRequest.getQuestionBankId() > 0,
                    QuestionBankTestRecord::getQuestionBankId, queryRequest.getQuestionBankId())
                .orderByDesc(QuestionBankTestRecord::getCreateTime, QuestionBankTestRecord::getId));

        Page<QuestionBankTestRecordVO> voPage = new Page<>(current, size, recordPage.getTotal());
        List<QuestionBankTestRecord> recordList = recordPage.getRecords();
        if (recordList.isEmpty()) {
            voPage.setRecords(Collections.emptyList());
            return voPage;
        }

        Set<Long> bankIdSet = recordList.stream().map(QuestionBankTestRecord::getQuestionBankId).collect(Collectors.toSet());
        Map<Long, QuestionBank> questionBankMap = questionBankService.listByIds(bankIdSet).stream()
            .collect(Collectors.toMap(QuestionBank::getId, Function.identity(), (a, b) -> a));

        List<QuestionBankTestRecordVO> voList = recordList.stream().map(record -> {
            QuestionBankTestRecordVO vo = new QuestionBankTestRecordVO();
            vo.setRecordId(record.getId());
            vo.setQuestionBankId(record.getQuestionBankId());
            QuestionBank questionBank = questionBankMap.get(record.getQuestionBankId());
            vo.setQuestionBankTitle(questionBank == null ? "题库测试" : questionBank.getTitle());
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
     * 获取指定题库的测试题目列表
     * 根据题库ID查询关联的题目和小题，按顺序组装成测试项，并限制最大返回数量
     *
     * @param questionBankId 题库ID
     * @return 题库测试题目列表，包含题目、小题及选项信息，最多返回DEFAULT_BANK_TEST_ITEM_LIMIT条
     */
    private List<QuestionBankTestItemVO> listQuestionBankTestItems(Long questionBankId) {
        List<QuestionBankQuestion> relationList = questionBankQuestionService.list(Wrappers.lambdaQuery(QuestionBankQuestion.class)
            .eq(QuestionBankQuestion::getQuestionBankId, questionBankId)
            .orderByAsc(QuestionBankQuestion::getCreateTime, QuestionBankQuestion::getId));
        if (relationList.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> questionIdList = relationList.stream().map(QuestionBankQuestion::getQuestionId).collect(Collectors.toList());
        List<QuestionTest> questionTestList = questionTestService.list(Wrappers.lambdaQuery(QuestionTest.class)
            .in(QuestionTest::getQuestionId, questionIdList)
            .eq(QuestionTest::getStatus, 1));
        if (questionTestList.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, QuestionTest> questionIdTestMap = questionTestList.stream()
            .collect(Collectors.toMap(QuestionTest::getQuestionId, Function.identity(), (a, b) -> a));
        List<Long> questionTestIdList = questionTestList.stream().map(QuestionTest::getId).collect(Collectors.toList());
        Map<Long, List<QuestionTestItem>> testItemMap = questionTestItemService.list(Wrappers.lambdaQuery(QuestionTestItem.class)
                .in(QuestionTestItem::getQuestionTestId, questionTestIdList)
                .orderByAsc(QuestionTestItem::getSort, QuestionTestItem::getId))
            .stream().collect(Collectors.groupingBy(QuestionTestItem::getQuestionTestId));

        List<QuestionBankTestItemVO> result = new ArrayList<>();
        int sort = 1;
        for (Long questionId : questionIdList) {
            QuestionTest questionTest = questionIdTestMap.get(questionId);
            if (questionTest == null) {
                continue;
            }

            List<QuestionTestItem> itemList = testItemMap.get(questionTest.getId());
            if (itemList == null) {
                continue;
            }

            for (QuestionTestItem item : itemList) {
                QuestionBankTestItemVO vo = new QuestionBankTestItemVO();
                vo.setQuestionId(questionId);
                vo.setQuestionTestId(questionTest.getId());
                vo.setQuestionTestItemId(item.getId());
                vo.setTitle(item.getTitle());
                vo.setSort(sort++);
                vo.setOptions(buildOptions(item));
                result.add(vo);

                if (result.size() >= DEFAULT_BANK_TEST_ITEM_LIMIT) {
                    return result;
                }
            }
        }
        return result;
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
     * 构建题库测试提交结果对象
     *
     * @param record 题库测试记录实体，包含测试成绩、答题数量等统计信息
     * @param questionBankTitle 题库标题
     * @param itemResultList 各题目判分结果列表
     * @return 题库测试提交结果对象，包含完整的测试记录和答题详情
     */
    private QuestionBankTestSubmitVO buildSubmitVO(QuestionBankTestRecord record, String questionBankTitle,
                                                   List<QuestionTestItemResultVO> itemResultList) {
        QuestionBankTestSubmitVO vo = new QuestionBankTestSubmitVO();
        vo.setRecordId(record.getId());
        vo.setQuestionBankId(record.getQuestionBankId());
        vo.setQuestionBankTitle(questionBankTitle);
        vo.setScore(record.getScore());
        vo.setRightCount(record.getRightCount());
        vo.setQuestionCount(record.getQuestionCount());
        vo.setItemResultList(itemResultList);
        return vo;
    }

}
