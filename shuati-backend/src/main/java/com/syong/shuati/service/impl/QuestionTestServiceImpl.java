package com.syong.shuati.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syong.shuati.common.ErrorCode;
import com.syong.shuati.exception.ThrowUtils;
import com.syong.shuati.mapper.QuestionTestItemMapper;
import com.syong.shuati.mapper.QuestionTestMapper;
import com.syong.shuati.model.dto.questionTest.QuestionTestAddRequest;
import com.syong.shuati.model.dto.questionTest.QuestionTestItemDTO;
import com.syong.shuati.model.dto.questionTest.QuestionTestUpdateRequest;
import com.syong.shuati.model.entity.Question;
import com.syong.shuati.model.entity.QuestionTest;
import com.syong.shuati.model.entity.QuestionTestItem;
import com.syong.shuati.model.entity.User;
import com.syong.shuati.model.vo.QuestionTestAdminVO;
import com.syong.shuati.model.vo.QuestionTestItemAdminVO;
import com.syong.shuati.model.vo.QuestionTestItemVO;
import com.syong.shuati.model.vo.QuestionTestVO;
import com.syong.shuati.service.QuestionService;
import com.syong.shuati.service.QuestionTestItemService;
import com.syong.shuati.service.QuestionTestService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuestionTestServiceImpl extends ServiceImpl<QuestionTestMapper, QuestionTest>
    implements QuestionTestService {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionTestItemService questionTestItemService;

    @Resource
    private QuestionTestItemMapper questionTestItemMapper;

    @Override
    public void validQuestionTest(QuestionTest questionTest, boolean add) {
        ThrowUtils.throwIf(questionTest == null, ErrorCode.PARAMS_ERROR);
        if (add) {
            ThrowUtils.throwIf(questionTest.getQuestionId() == null || questionTest.getQuestionId() <= 0,
                ErrorCode.PARAMS_ERROR, "题目不存在");
            ThrowUtils.throwIf(StringUtils.isBlank(questionTest.getTitle()),
                ErrorCode.PARAMS_ERROR, "测试标题不能为空");
        }
        if (StringUtils.isNotBlank(questionTest.getTitle())) {
            ThrowUtils.throwIf(questionTest.getTitle().length() > 256,
                ErrorCode.PARAMS_ERROR, "测试标题过长");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long addQuestionTest(QuestionTestAddRequest questionTestAddRequest, User loginUser) {
        ThrowUtils.throwIf(questionTestAddRequest == null, ErrorCode.PARAMS_ERROR);
        Long questionId = questionTestAddRequest.getQuestionId();
        Question question = questionService.getById(questionId);
        ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR, "题目不存在");

        QuestionTest existQuestionTest = this.baseMapper.selectAnyByQuestionId(questionId);
        if (existQuestionTest != null) {
            if (existQuestionTest.getIsDelete() != null && existQuestionTest.getIsDelete() == 1) {
                questionTestItemMapper.hardDeleteByQuestionTestId(existQuestionTest.getId());
                this.baseMapper.hardDeleteById(existQuestionTest.getId());
            } else {
                ThrowUtils.throwIf(true, ErrorCode.OPERATION_ERROR, "该题目已配置测试");
            }
        }

        QuestionTest questionTest = new QuestionTest();
        BeanUtils.copyProperties(questionTestAddRequest, questionTest);
        questionTest.setStatus(questionTestAddRequest.getStatus() == null ? 1 : questionTestAddRequest.getStatus());
        questionTest.setUserId(loginUser.getId());
        validQuestionTest(questionTest, true);
        validQuestionTestItemList(questionTestAddRequest.getItemList());

        boolean result = this.save(questionTest);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        saveQuestionTestItems(questionTest.getId(), questionTestAddRequest.getItemList());
        return questionTest.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateQuestionTest(QuestionTestUpdateRequest questionTestUpdateRequest) {
        ThrowUtils.throwIf(questionTestUpdateRequest == null || questionTestUpdateRequest.getId() == null
            || questionTestUpdateRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        QuestionTest oldQuestionTest = this.getById(questionTestUpdateRequest.getId());
        ThrowUtils.throwIf(oldQuestionTest == null, ErrorCode.NOT_FOUND_ERROR, "测试不存在");

        Question question = questionService.getById(questionTestUpdateRequest.getQuestionId());
        ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR, "题目不存在");

        QuestionTest questionTest = new QuestionTest();
        BeanUtils.copyProperties(questionTestUpdateRequest, questionTest);
        questionTest.setUserId(oldQuestionTest.getUserId());
        validQuestionTest(questionTest, false);
        validQuestionTestItemList(questionTestUpdateRequest.getItemList());

        boolean result = this.updateById(questionTest);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        questionTestItemMapper.hardDeleteByQuestionTestId(questionTest.getId());
        saveQuestionTestItems(questionTest.getId(), questionTestUpdateRequest.getItemList());
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteQuestionTest(Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        QuestionTest questionTest = this.getById(id);
        ThrowUtils.throwIf(questionTest == null, ErrorCode.NOT_FOUND_ERROR, "测试不存在");

        questionTestItemMapper.hardDeleteByQuestionTestId(id);
        return this.baseMapper.hardDeleteById(id) > 0;
    }

    @Override
    public QuestionTestAdminVO getQuestionTestAdminVOByQuestionId(Long questionId) {
        ThrowUtils.throwIf(questionId == null || questionId <= 0, ErrorCode.PARAMS_ERROR);
        QuestionTest questionTest = this.getOne(Wrappers.lambdaQuery(QuestionTest.class)
            .eq(QuestionTest::getQuestionId, questionId), false);
        if (questionTest == null) {
            return null;
        }

        List<QuestionTestItem> questionTestItemList = listQuestionTestItems(questionTest.getId());
        QuestionTestAdminVO questionTestAdminVO = new QuestionTestAdminVO();
        BeanUtils.copyProperties(questionTest, questionTestAdminVO);
        List<QuestionTestItemAdminVO> itemAdminVOList = questionTestItemList.stream().map(item -> {
            QuestionTestItemAdminVO itemAdminVO = new QuestionTestItemAdminVO();
            BeanUtils.copyProperties(item, itemAdminVO);
            return itemAdminVO;
        }).collect(Collectors.toList());
        questionTestAdminVO.setItemList(itemAdminVOList);
        return questionTestAdminVO;
    }

    @Override
    public QuestionTestVO getQuestionTestVOByQuestionId(Long questionId) {
        ThrowUtils.throwIf(questionId == null || questionId <= 0, ErrorCode.PARAMS_ERROR);
        QuestionTest questionTest = this.getOne(Wrappers.lambdaQuery(QuestionTest.class)
            .eq(QuestionTest::getQuestionId, questionId)
            .eq(QuestionTest::getStatus, 1), false);
        if (questionTest == null) {
            return null;
        }

        List<QuestionTestItem> questionTestItemList = listQuestionTestItems(questionTest.getId());
        QuestionTestVO questionTestVO = new QuestionTestVO();
        BeanUtils.copyProperties(questionTest, questionTestVO);
        List<QuestionTestItemVO> itemVOList = questionTestItemList.stream().map(item -> {
            QuestionTestItemVO itemVO = new QuestionTestItemVO();
            itemVO.setId(item.getId());
            itemVO.setTitle(item.getTitle());
            itemVO.setSort(item.getSort());
            Map<String, String> options = new LinkedHashMap<>();
            options.put("A", item.getOptionA());
            options.put("B", item.getOptionB());
            options.put("C", item.getOptionC());
            options.put("D", item.getOptionD());
            if (StringUtils.isNotBlank(item.getOptionE())) {
                options.put("E", item.getOptionE());
            }
            itemVO.setOptions(options);
            return itemVO;
        }).collect(Collectors.toList());
        questionTestVO.setItemList(itemVOList);
        return questionTestVO;
    }

    private void validQuestionTestItemList(List<QuestionTestItemDTO> itemList) {
        ThrowUtils.throwIf(itemList == null || itemList.isEmpty(), ErrorCode.PARAMS_ERROR, "测试题不能为空");
        ThrowUtils.throwIf(itemList.size() < 4 || itemList.size() > 5,
            ErrorCode.PARAMS_ERROR, "测试题数量必须为 4 到 5 题");
        List<String> validAnswers = Arrays.asList("A", "B", "C", "D", "E");
        for (QuestionTestItemDTO item : itemList) {
            ThrowUtils.throwIf(item == null, ErrorCode.PARAMS_ERROR, "测试题目不能为空");
            ThrowUtils.throwIf(StringUtils.isBlank(item.getTitle()), ErrorCode.PARAMS_ERROR, "题干不能为空");
            ThrowUtils.throwIf(
                StringUtils.isAnyBlank(item.getOptionA(), item.getOptionB(), item.getOptionC(), item.getOptionD()),
                ErrorCode.PARAMS_ERROR, "A-D 选项不能为空");
            String rawAnswer = item.getAnswer();
            ThrowUtils.throwIf(StringUtils.isBlank(rawAnswer), ErrorCode.PARAMS_ERROR, "正确答案不能为空");
            String answer = rawAnswer.trim().toUpperCase();
            ThrowUtils.throwIf(!validAnswers.contains(answer), ErrorCode.PARAMS_ERROR, "正确答案非法");
            if ("E".equals(answer)) {
                ThrowUtils.throwIf(StringUtils.isBlank(item.getOptionE()),
                    ErrorCode.PARAMS_ERROR, "选择 E 答案时，E 选项内容不能为空");
            }
        }
    }

    private void saveQuestionTestItems(Long questionTestId, List<QuestionTestItemDTO> itemList) {
        List<QuestionTestItem> questionTestItemList = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++) {
            QuestionTestItemDTO itemDTO = itemList.get(i);
            QuestionTestItem item = new QuestionTestItem();
            BeanUtils.copyProperties(itemDTO, item);
            item.setId(null);
            item.setQuestionTestId(questionTestId);
            item.setAnswer(itemDTO.getAnswer().trim().toUpperCase());
            item.setSort(itemDTO.getSort() == null ? i + 1 : itemDTO.getSort());
            questionTestItemList.add(item);
        }
        boolean result = questionTestItemService.saveBatch(questionTestItemList);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "保存测试题失败");
    }

    private List<QuestionTestItem> listQuestionTestItems(Long questionTestId) {
        return questionTestItemService.list(new LambdaQueryWrapper<QuestionTestItem>()
            .eq(QuestionTestItem::getQuestionTestId, questionTestId)
            .orderByAsc(QuestionTestItem::getSort, QuestionTestItem::getId));
    }
}
