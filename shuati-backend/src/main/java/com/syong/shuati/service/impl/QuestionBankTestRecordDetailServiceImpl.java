package com.syong.shuati.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syong.shuati.mapper.QuestionBankTestRecordDetailMapper;
import com.syong.shuati.model.entity.QuestionBankTestRecordDetail;
import com.syong.shuati.service.QuestionBankTestRecordDetailService;
import org.springframework.stereotype.Service;

/**
 * 题库测试记录详情服务
 */
@Service
public class QuestionBankTestRecordDetailServiceImpl
    extends ServiceImpl<QuestionBankTestRecordDetailMapper, QuestionBankTestRecordDetail>
    implements QuestionBankTestRecordDetailService {
}
