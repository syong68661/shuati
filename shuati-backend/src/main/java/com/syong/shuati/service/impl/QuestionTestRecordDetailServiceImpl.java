package com.syong.shuati.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syong.shuati.mapper.QuestionTestRecordDetailMapper;
import com.syong.shuati.model.entity.QuestionTestRecordDetail;
import com.syong.shuati.service.QuestionTestRecordDetailService;
import org.springframework.stereotype.Service;

/**
 * 题目测试记录明细服务实现
 */
@Service
public class QuestionTestRecordDetailServiceImpl
    extends ServiceImpl<QuestionTestRecordDetailMapper, QuestionTestRecordDetail>
    implements QuestionTestRecordDetailService {
}
