package com.syong.shuati.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.syong.shuati.model.dto.questionTest.QuestionTestAddRequest;
import com.syong.shuati.model.dto.questionTest.QuestionTestUpdateRequest;
import com.syong.shuati.model.entity.QuestionTest;
import com.syong.shuati.model.entity.User;
import com.syong.shuati.model.vo.QuestionTestAdminVO;
import com.syong.shuati.model.vo.QuestionTestVO;

/**
 * 题目测试服务
 *
 * @author  <a href="https://github.com/LightingForest">SYong</a>
 */
public interface QuestionTestService extends IService<QuestionTest> {

    /**
     * 验证题目测试信息
     * 对新增和编辑操作进行不同的参数校验，确保数据合法性
     *
     * @param questionTest 题目测试实体对象，包含待验证的测试信息
     * @param add 是否为新增操作，新增时需要校验必填字段
     */
    void validQuestionTest(QuestionTest questionTest, boolean add);


    /**
     * 添加题目测试
     * 验证题目是否存在、检查是否已配置测试，保存测试基本信息及小题列表
     *
     * @param questionTestAddRequest 题目测试添加请求对象，包含测试基本信息和小题列表
     * @param loginUser 当前登录用户对象，用于设置测试创建者
     * @return 新创建的题目测试ID
     */
    Long addQuestionTest(QuestionTestAddRequest questionTestAddRequest, User loginUser);

    /**
     * 更新题目测试信息
     * 验证测试和题目是否存在，更新测试基本信息并替换原有小题列表
     *
     * @param questionTestUpdateRequest 题目测试更新请求对象，包含测试ID、更新后的测试信息和小题列表
     * @return 是否更新成功
     */
    boolean updateQuestionTest(QuestionTestUpdateRequest questionTestUpdateRequest);

    /**
     * 删除题目测试及其关联的小题
     * 先删除该测试下的所有小题，再删除测试记录本身，确保数据一致性
     *
     * @param id 题目测试ID，必须为正数
     * @return 是否删除成功
     */
    boolean deleteQuestionTest(Long id);

    /**
     * 根据题目ID获取题目测试管理详情
     * 查询该题目关联的测试信息及所有小题，用于后台管理编辑
     *
     * @param questionId 题目ID，必须为正数
     * @return 题目测试管理对象，包含测试基本信息和小题列表；若未配置测试则返回null
     */
    QuestionTestAdminVO getQuestionTestAdminVOByQuestionId(Long questionId);

    /**
     * 根据题目ID获取题目测试详情（用户端）
     * 仅返回状态为启用（status=1）的测试，包含测试基本信息和不含答案的小题列表
     *
     * @param questionId 题目ID，必须为正数
     * @return 题目测试对象，包含测试基本信息和小题列表（不含正确答案）；若测试不存在或未启用则返回null
     */
    QuestionTestVO getQuestionTestVOByQuestionId(Long questionId);
}
