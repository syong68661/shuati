package com.syong.shuati.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.syong.shuati.model.dto.questionTest.QuestionTestRecordQueryRequest;
import com.syong.shuati.model.dto.questionTest.QuestionTestSubmitRequest;
import com.syong.shuati.model.entity.QuestionTestRecord;
import com.syong.shuati.model.entity.User;
import com.syong.shuati.model.vo.QuestionTestRecordVO;
import com.syong.shuati.model.vo.QuestionTestSubmitVO;

/**
 * 题目测试记录服务
 *
 * @author  <a href="https://github.com/LightingForest">SYong</a>
 */
public interface QuestionTestRecordService extends IService<QuestionTestRecord> {

    /**
     * 提交题目测试
     *
     * @param questionTestSubmitRequest
     * @param loginUser
     * @return 题目测试提交结果对象，包含测试记录、各题判分结果及总成绩
     */
    QuestionTestSubmitVO submitQuestionTest(QuestionTestSubmitRequest questionTestSubmitRequest, User loginUser);

    /**
     * 获取题目测试提交信息
     *
     * @param recordId
     * @param loginUser
     * @param isAdmin
     * @return 题目测试提交结果对象，包含测试记录、各题判分结果及总成绩
     */
    QuestionTestSubmitVO getQuestionTestSubmitVOById(Long recordId, User loginUser, boolean isAdmin);

    /**
     * 获取题目测试记录列表（仅管理员）
     *
     * @param questionTestRecordQueryRequest
     * @return 分页后的用户题目测试记录列表，包含记录ID、题目信息、成绩等概要信息
     */
    Page<QuestionTestRecordVO> listMyQuestionTestRecordVOByPage(QuestionTestRecordQueryRequest questionTestRecordQueryRequest,
                                                                User loginUser);
}
