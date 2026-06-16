package com.syong.shuati.controller;

import com.syong.shuati.common.BaseResponse;
import com.syong.shuati.common.ResultUtils;
import com.syong.shuati.service.QuestionService;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/question/tag")
public class QuestionTagController {

    @Resource
    private QuestionService questionService;

    @GetMapping("/list")
    public BaseResponse<List<String>> listQuestionTagList(String searchText) {
        return ResultUtils.success(questionService.listQuestionTagList(searchText));
    }
}
