package com.syong.shuati.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.syong.shuati.common.BaseResponse;
import com.syong.shuati.common.DeleteRequest;
import com.syong.shuati.common.ErrorCode;
import com.syong.shuati.common.ResultUtils;
import com.syong.shuati.constant.UserConstant;
import com.syong.shuati.exception.ThrowUtils;
import com.syong.shuati.model.dto.questionTest.QuestionTestAddRequest;
import com.syong.shuati.model.dto.questionTest.QuestionTestRecordQueryRequest;
import com.syong.shuati.model.dto.questionTest.QuestionTestSubmitRequest;
import com.syong.shuati.model.dto.questionTest.QuestionTestUpdateRequest;
import com.syong.shuati.model.entity.User;
import com.syong.shuati.model.vo.QuestionTestAdminVO;
import com.syong.shuati.model.vo.QuestionTestRecordVO;
import com.syong.shuati.model.vo.QuestionTestSubmitVO;
import com.syong.shuati.model.vo.QuestionTestVO;
import com.syong.shuati.service.QuestionTestRecordService;
import com.syong.shuati.service.QuestionTestService;
import com.syong.shuati.service.UserService;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试题目接口
 */
@RestController
@RequestMapping("/questionTest")
public class QuestionTestController {

    @Resource
    private QuestionTestService questionTestService;

    @Resource
    private QuestionTestRecordService questionTestRecordService;

    @Resource
    private UserService userService;

    /**
     * 添加测试题目
     * @param questionTestAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addQuestionTest(@RequestBody QuestionTestAddRequest questionTestAddRequest,
                                              HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(questionTestService.addQuestionTest(questionTestAddRequest, loginUser));
    }

    /**
     * 更新测试题目
     * @param questionTestUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestionTest(@RequestBody QuestionTestUpdateRequest questionTestUpdateRequest) {
        return ResultUtils.success(questionTestService.updateQuestionTest(questionTestUpdateRequest));
    }

    /**
     *  删除测试题目
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteQuestionTest(@RequestBody DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() == null || deleteRequest.getId() <= 0,
            ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(questionTestService.deleteQuestionTest(deleteRequest.getId()));
    }

    /**
     * 根据题目id获取测试题目
     * @param questionId
     * @return
     */
    @GetMapping("/get/byQuestionId")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<QuestionTestAdminVO> getQuestionTestByQuestionId(Long questionId) {
        return ResultUtils.success(questionTestService.getQuestionTestAdminVOByQuestionId(questionId));
    }

    /**
     * 根据题目id获取测试题目
     * @param questionId
     * @return
     */
    @GetMapping("/get/vo/byQuestionId")
    public BaseResponse<QuestionTestVO> getQuestionTestVOByQuestionId(Long questionId) {
        return ResultUtils.success(questionTestService.getQuestionTestVOByQuestionId(questionId));
    }

    /**
     * 提交测试题目
     * @param questionTestSubmitRequest
     * @param request
     * @return
     */
    @PostMapping("/submit")
    public BaseResponse<QuestionTestSubmitVO> submitQuestionTest(@RequestBody QuestionTestSubmitRequest questionTestSubmitRequest,
                                                                 HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(questionTestRecordService.submitQuestionTest(questionTestSubmitRequest, loginUser));
    }

    /**
     * 获取测试题目提交结果
     * @param recordId
     * @param request
     * @return
     */
    @GetMapping("/record/get/vo")
    public BaseResponse<QuestionTestSubmitVO> getQuestionTestRecordVO(Long recordId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(questionTestRecordService.getQuestionTestSubmitVOById(recordId, loginUser,
            userService.isAdmin(loginUser)));
    }

    /**
     * 获取测试题目提交结果列表
     * @param questionTestRecordQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/record/my/list/page/vo")
    public BaseResponse<Page<QuestionTestRecordVO>> listMyQuestionTestRecordVOByPage(
        @RequestBody QuestionTestRecordQueryRequest questionTestRecordQueryRequest,
        HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(questionTestRecordService.listMyQuestionTestRecordVOByPage(
            questionTestRecordQueryRequest, loginUser));
    }
}
