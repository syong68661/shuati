package com.syong.shuati.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.syong.shuati.common.BaseResponse;
import com.syong.shuati.common.ResultUtils;
import com.syong.shuati.model.dto.questionBankTest.QuestionBankTestRecordQueryRequest;
import com.syong.shuati.model.dto.questionBankTest.QuestionBankTestSubmitRequest;
import com.syong.shuati.model.entity.User;
import com.syong.shuati.model.vo.QuestionBankTestRecordVO;
import com.syong.shuati.model.vo.QuestionBankTestSubmitVO;
import com.syong.shuati.model.vo.QuestionBankTestVO;
import com.syong.shuati.service.QuestionBankTestRecordService;
import com.syong.shuati.service.UserService;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 题库测试记录
 */
@RestController
@RequestMapping("/questionBankTest")
public class QuestionBankTestController {

    @Resource
    private QuestionBankTestRecordService questionBankTestRecordService;

    @Resource
    private UserService userService;

    /**
     * 获取题库测试
     * @param questionBankId
     * @return
     */
    @GetMapping("/get/vo/byQuestionBankId")
    public BaseResponse<QuestionBankTestVO> getQuestionBankTestVOByQuestionBankId(Long questionBankId) {
        return ResultUtils.success(questionBankTestRecordService.getQuestionBankTestVOByQuestionBankId(questionBankId));
    }

    /**
     * 提交题库测试答案
     * @param questionBankTestSubmitRequest 题库测试提交请求对象，包含用户答题信息
     * @param request HTTP请求对象，用于获取当前登录用户信息
     * @return 题库测试提交结果的响应对象，包含提交结果（如答题记录ID、正确率等）
     */
    @PostMapping("/submit")
    public BaseResponse<QuestionBankTestSubmitVO> submitQuestionBankTest(
        @RequestBody QuestionBankTestSubmitRequest questionBankTestSubmitRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(questionBankTestRecordService.submitQuestionBankTest(questionBankTestSubmitRequest,
            loginUser));
    }


    /**
     * 根据记录ID获取题库测试记录详情
     *
     * @param recordId 测试记录ID
     * @param request HTTP请求对象，用于获取当前登录用户信息
     * @return 题库测试记录详情的响应对象，包含答题记录、答案及成绩等信息
     */
    @GetMapping("/record/get/vo")
    public BaseResponse<QuestionBankTestSubmitVO> getQuestionBankTestRecordVO(Long recordId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(questionBankTestRecordService.getQuestionBankTestSubmitVOById(recordId, loginUser,
            userService.isAdmin(loginUser)));
    }


    /**
     * 分页获取当前用户的题库测试记录列表
     *
     * @param questionBankTestRecordQueryRequest 查询请求对象，包含分页参数及筛选条件
     * @param request HTTP请求对象，用于获取当前登录用户信息
     * @return 分页后的用户题库测试记录列表响应对象
     */
    @PostMapping("/record/my/list/page/vo")
    public BaseResponse<Page<QuestionBankTestRecordVO>> listMyQuestionBankTestRecordVOByPage(
        @RequestBody QuestionBankTestRecordQueryRequest questionBankTestRecordQueryRequest,
        HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(questionBankTestRecordService.listMyQuestionBankTestRecordVOByPage(
            questionBankTestRecordQueryRequest, loginUser));
    }

}
