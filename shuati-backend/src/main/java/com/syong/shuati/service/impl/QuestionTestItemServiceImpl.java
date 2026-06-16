package com.syong.shuati.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syong.shuati.mapper.QuestionTestItemMapper;
import com.syong.shuati.model.entity.QuestionTestItem;
import com.syong.shuati.service.QuestionTestItemService;
import org.springframework.stereotype.Service;

/**
 * 题目测试项服务实现
 */
@Service
public class QuestionTestItemServiceImpl extends ServiceImpl<QuestionTestItemMapper, QuestionTestItem>
    implements QuestionTestItemService {
}
