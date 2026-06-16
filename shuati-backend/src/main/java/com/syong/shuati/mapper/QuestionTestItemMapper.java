package com.syong.shuati.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.syong.shuati.model.entity.QuestionTestItem;
import org.apache.ibatis.annotations.Delete;

public interface QuestionTestItemMapper extends BaseMapper<QuestionTestItem> {

    @Delete("delete from question_test_item where questionTestId = #{questionTestId}")
    int hardDeleteByQuestionTestId(Long questionTestId);
}
