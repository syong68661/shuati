package com.syong.shuati.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.syong.shuati.model.entity.QuestionTest;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

public interface QuestionTestMapper extends BaseMapper<QuestionTest> {

    @Select("select id, questionId, title, description, status, userId, createTime, updateTime, isDelete " +
        "from question_test where questionId = #{questionId} limit 1")
    QuestionTest selectAnyByQuestionId(Long questionId);

    @Delete("delete from question_test where id = #{id}")
    int hardDeleteById(Long id);
}
