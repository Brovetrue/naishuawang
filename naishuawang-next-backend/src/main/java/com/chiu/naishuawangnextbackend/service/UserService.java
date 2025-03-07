package com.chiu.naishuawangnextbackend.service;

import cn.hutool.http.server.HttpServerRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chiu.naishuawangnextbackend.model.dto.user.UserQueryRequest;
import com.chiu.naishuawangnextbackend.model.entity.User;
import com.chiu.naishuawangnextbackend.model.vo.LoginUserVO;
import com.chiu.naishuawangnextbackend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
* @author ASUS
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-01-24 14:54:36
*/
public interface UserService extends IService<User> {
    /**
     *
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 确认密码
     * @return 新用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取加密后的密码
     * @param password
     * @return
     */
    String getEncryptPassword(String password);


    /**
     * 获得当前登录用户
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获得请求脱敏后的用户信息
     * @param user
     * @return
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param userList
     * @return
     */
    List<UserVO> getUserVOList(List<User> userList);


    /**
     * 用户注销
     * @param request
     * @return
     */
    //注销了只是一个动作不返回也没啥事情
    boolean userLogout(HttpServletRequest request);


    /**
     * 获取查询条件
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    boolean isAdmin(User user);

    /**
     * 添加用户签到记录
     * @param userId 用户id
     * @return 当前用户是否签到成功
     */
    boolean addUserSignIn(long userId);

    /**
     * 获取用户某个年份的签到记录
     *
     * @param userId 用户id
     * @param year 年份
     * @return 用户签到映射
     */
    List<Integer> getUserSignInRecord(long userId, Integer year);
}
