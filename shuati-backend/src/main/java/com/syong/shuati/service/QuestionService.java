package com.syong.shuati.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.syong.shuati.model.dto.question.QuestionQueryRequest;
import com.syong.shuati.model.entity.Question;
import com.syong.shuati.model.vo.QuestionVO;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

public interface QuestionService extends IService<Question> {

    void validQuestion(Question question, boolean add);

    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);

    QuestionVO getQuestionVO(Question question, HttpServletRequest request);

    Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request);

    Page<Question> listQuestionByPage(QuestionQueryRequest questionQueryRequest);

    Page<Question> searchFromEs(QuestionQueryRequest questionQueryRequest);

    @Transactional(rollbackFor = Exception.class)
    void batchDeleteQuestions(List<Long> questionIdList);

    List<String> listQuestionTagList(String searchText);
}
