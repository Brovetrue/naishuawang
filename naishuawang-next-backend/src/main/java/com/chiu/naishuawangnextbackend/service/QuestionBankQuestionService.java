package com.chiu.naishuawangnextbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chiu.naishuawangnextbackend.model.dto.questionBankQuestion.QuestionBankQuestionQueryRequest;
import com.chiu.naishuawangnextbackend.model.entity.QuestionBankQuestion;
import com.chiu.naishuawangnextbackend.model.entity.User;
import com.chiu.naishuawangnextbackend.model.vo.QuestionBankQuestionVO;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 题库题目关联表服务
 *
 */
public interface QuestionBankQuestionService extends IService<QuestionBankQuestion> {

    /**
     * 校验数据
     *
     * @param questionBankQuestion
     * @param add 对创建的数据进行校验
     */
    void validQuestionBankQuestion(QuestionBankQuestion questionBankQuestion, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionBankQuestionQueryRequest
     * @return
     */
    QueryWrapper<QuestionBankQuestion> getQueryWrapper(QuestionBankQuestionQueryRequest questionBankQuestionQueryRequest);
    
    /**
     * 获取题库题目关联表封装
     *
     * @param questionBankQuestion
     * @param request
     * @return
     */
    QuestionBankQuestionVO getQuestionBankQuestionVO(QuestionBankQuestion questionBankQuestion, HttpServletRequest request);

    /**
     * 分页获取题库题目关联表封装
     *
     * @param questionBankQuestionPage
     * @param request
     * @return
     */
    Page<QuestionBankQuestionVO> getQuestionBankQuestionVOPage(Page<QuestionBankQuestion> questionBankQuestionPage, HttpServletRequest request);

    /**
     * 批量添加题目到题库
     * @param questionIdList 题目id列表
     * @param questionBankId 题库id
     * @param loginUser 登录用户
     */
    void batchAddQuestionsToBank(List<Long> questionIdList, Long questionBankId, User loginUser);

    /**
     * 批量从题库中移除题目
     * @param questionIdList 题目id列表
     * @param questionBankId 题库id
     */
    void batchRemoveQuestionsFromBank(List<Long> questionIdList, Long questionBankId);


}
