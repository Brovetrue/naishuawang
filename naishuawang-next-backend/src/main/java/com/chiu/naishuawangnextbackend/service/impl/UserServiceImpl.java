package com.chiu.naishuawangnextbackend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.server.HttpServerRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.chiu.naishuawangnextbackend.constant.RedisConstant;
import com.chiu.naishuawangnextbackend.constant.UserConstant;
import com.chiu.naishuawangnextbackend.exception.BusinessException;
import com.chiu.naishuawangnextbackend.exception.ErrorCode;
import com.chiu.naishuawangnextbackend.model.dto.user.UserQueryRequest;
import com.chiu.naishuawangnextbackend.model.entity.User;
import com.chiu.naishuawangnextbackend.model.enums.UserRoleEnum;
import com.chiu.naishuawangnextbackend.model.vo.LoginUserVO;
import com.chiu.naishuawangnextbackend.model.vo.UserVO;
import com.chiu.naishuawangnextbackend.satoken.DeviceUtils;
import com.chiu.naishuawangnextbackend.service.UserService;
import com.chiu.naishuawangnextbackend.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBitSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

import static com.chiu.naishuawangnextbackend.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author ASUS
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2025-01-24 14:54:36
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{
    @Resource
    private RedissonClient redissonClient;
    /**
     * 盐值。混淆密码
     */
    public static final String SALT = "chiu";
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        //1、校验参数
        //传统校验写法
        if(StrUtil.hasBlank(userAccount,userPassword,checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数不能为空");
        }
        if(userAccount.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度不能小于4");
        }
        if(userPassword.length()<8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码长度不能小于8");
        }
        if(!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次密码不一致");
        }
        //2、检查用户账号是否和数据库中已有重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        long count = this.baseMapper.selectCount(queryWrapper);
        if(count>0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号已存在");
        }
        //3、加密密码
        String encryptPassword = getEncryptPassword(userPassword);
        //4、插入数据库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName("默认名称");
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean saveResult = this.save(user);
        if(!saveResult){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"系统错误，注册失败，数据库错误");
        }
        return user.getId();//为什么能getId?这是mybatis-plus帮你实现的,save帮你写好了。===主键回填
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1、检验
        if(StrUtil.hasBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数不能为空");
        }
        if(userAccount.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度不能小于4");
        }
        if(userPassword.length()<8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码长度不能小于8");
        }
        //2、对用户传递的密码进行加密
        String encryptPassword = getEncryptPassword(userPassword);
        //3、查询数据库中的账号是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        //不存在抛异常
        if(user==null){
            log.info("userLogin failed, userAccount can not match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户不存在或者密码错误");
        }
        //4、保存用户的登录态
//        request.getSession().setAttribute(USER_LOGIN_STATE, user);

        // 使用 Sa-Token 登录，并指定设备，同端登录互斥
        StpUtil.login(user.getId(), DeviceUtils.getRequestDevice(request));
        StpUtil.getSession().set(USER_LOGIN_STATE, user);
        return this.getLoginUserVO(user);
    }

    /**
     * 获取加密后的密码
     * @param userPassword 用户密码
     * @return
     */
    //密码加密方法，公共的方法加上Override放回UserService接口中
    @Override
    public String getEncryptPassword(String userPassword){
        // 加盐、混洗密码
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object loginId = StpUtil.getLoginIdDefaultNull();
        if (Objects.isNull(loginId)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return (User) StpUtil.getSessionByLoginId(loginId).get(USER_LOGIN_STATE);
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }


    @Override
    public boolean userLogout(HttpServletRequest request) {
        //判断是否已经登录
//        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
//        if(userObj == null){
//            throw new BusinessException(ErrorCode.OPERATION_ERROR,"未登录");
//        }
        StpUtil.checkLogin();
        // 移除登录态
//        request.getSession().removeAttribute(USER_LOGIN_STATE);
        StpUtil.logout();
        return true;


    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if(userQueryRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String unionId = userQueryRequest.getUnionId();
        String mpOpenId = userQueryRequest.getMpOpenId();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(unionId), "unionId", unionId);
        queryWrapper.eq(StringUtils.isNotBlank(mpOpenId), "mpOpenId", mpOpenId);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StringUtils.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField),sortOrder.equals("ascend"),sortField);
        return queryWrapper;
    }

    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return isAdmin(user);
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    /**
     * 添加用户签到记录
     * @param userId 用户id
     * @return 当前用户是否签到成功
     */
    @Override
    public boolean addUserSignIn(long userId) {
        LocalDate date = LocalDate.now();
        String key = RedisConstant.getUserSignInRedisKey(date.getYear(),userId);
        // 获取Redis 的 BitMap
        RBitSet signInBitSet = redissonClient.getBitSet(key);
        // 获取当前日期是一年中的第几天，作为偏移量（从1开始计数）
        int offset = date.getDayOfYear();
        // 查询当天有没有签到
        if(!signInBitSet.get(offset)){
            //如果当天没有签到 则设置
            signInBitSet.set(offset,true);
        }
        //当天已签到
        return true;
    }
    /**
     * 获取用户某个年份的签到记录
     *
     * @param userId 用户id
     * @param year 年份
     * @return 用户签到映射
     */
    @Override
    public List<Integer> getUserSignInRecord(long userId, Integer year) {
        if (year == null) {
            LocalDate date = LocalDate.now();
            year = date.getYear();
        }
        String key = RedisConstant.getUserSignInRedisKey(year, userId);
        RBitSet signInBitSet = redissonClient.getBitSet(key);
        // 加载 BitSet 到内存中，避免后续读取时发送多次请求
        BitSet bitSet = signInBitSet.asBitSet();
        // 统计签到的日期
        List<Integer> dayList = new ArrayList<>();
        // 从索引 0 开始查找下一个被设置为 1 的位
        int index = bitSet.nextSetBit(0);
        while (index >= 0) {
            dayList.add(index);
            // 查找下一个被设置为 1 的位
            index = bitSet.nextSetBit(index + 1);
        }
        return dayList;
    }

}




