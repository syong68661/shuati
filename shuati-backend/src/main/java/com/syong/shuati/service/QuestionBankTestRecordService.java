package com.syong.shuati.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.syong.shuati.model.dto.questionBankTest.QuestionBankTestRecordQueryRequest;
import com.syong.shuati.model.dto.questionBankTest.QuestionBankTestSubmitRequest;
import com.syong.shuati.model.entity.QuestionBankTestRecord;
import com.syong.shuati.model.entity.User;
import com.syong.shuati.model.vo.QuestionBankTestRecordVO;
import com.syong.shuati.model.vo.QuestionBankTestSubmitVO;
import com.syong.shuati.model.vo.QuestionBankTestVO;

/**
 * 题库测试记录服务
 *
 * @author  <a href="https://github.com/LightingForest">SYong</a>
 */
public interface QuestionBankTestRecordService extends IService<QuestionBankTestRecord> {

    /**
     * 根据题库ID获取题库测试信息
     *
     * @param questionBankId 题库ID，必须为正数
     * @return 题库测试信息对象，包含题库标题、题目数量及题目列表；若题库下无题目则返回null
     */
    QuestionBankTestVO getQuestionBankTestVOByQuestionBankId(Long questionBankId);


    /**
     * 提交题库测试
     *
     * @param questionBankTestSubmitRequest 提交题库测试请求
     * @param loginUser                     登录用户
     * @return 提交题库测试结果对象
     */
    QuestionBankTestSubmitVO submitQuestionBankTest(QuestionBankTestSubmitRequest questionBankTestSubmitRequest,
                                                    User loginUser);

    /**
     * 获取题库测试提交结果
     *
     * @param recordId   测试记录ID
     * @param loginUser  登录用户
     * @param isAdmin    是否为管理员
     * @return 提交题库测试结果对象
     */
    QuestionBankTestSubmitVO getQuestionBankTestSubmitVOById(Long recordId, User loginUser, boolean isAdmin);

    /**
     * 获取我的题库测试记录
     *
     * @param questionBankTestRecordQueryRequest 查询题库测试记录请求
     * @param loginUser                          登录用户
     * @return 分页题库测试记录对象
     */
    Page<QuestionBankTestRecordVO> listMyQuestionBankTestRecordVOByPage(
        QuestionBankTestRecordQueryRequest questionBankTestRecordQueryRequest, User loginUser);
}
