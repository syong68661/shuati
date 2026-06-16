package com.syong.shuati.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.syong.shuati.model.dto.user.UserQueryRequest;
import com.syong.shuati.model.entity.User;
import com.syong.shuati.model.vo.LoginUserVO;
import com.syong.shuati.model.vo.UserVO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;

/**
 * 用户服务
 *
 * @author <a href="https://github.com/LightingForest">SYong</a>
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户登录（微信开放平台）
     *
     * @param wxOAuth2UserInfo 从微信获取的用户信息
     * @param request
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLoginByMpOpen(WxOAuth2UserInfo wxOAuth2UserInfo, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request
     * @return
     */
    User getLoginUserPermitNull(HttpServletRequest request);

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
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取脱敏的已登录用户信息
     *
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
    List<UserVO> getUserVO(List<User> userList);

    /**
     * 获取查询条件
     *
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 添加用户签到记录
     *
     * @param userId 用户 id
     * @return 当前是否已签到成功
     */
    boolean addUserSignIn(long userId);

    //    public Map<LocalDate, Boolean> getUserSignInRecord(long userId, Integer year) {
//        if (year == null) {
//            LocalDate date = LocalDate.now();
//            year = date.getYear();
//        }
//        String key = RedisConstant.getUserSignInRedisKey(year, userId);
//        RBitSet signInBitSet = redissonClient.getBitSet(key);
//        // LinkedHashMap 保证有序
//        Map<LocalDate, Boolean> result = new LinkedHashMap<>();
//        // 获取当前年份的总天数
//        int totalDays = Year.of(year).length();
//        // 依次获取每一天的签到状态
//        // 加载 BitSet 到内存中，避免后续读取时发送多次请求
//        BitSet bitSet = signInBitSet.asBitSet();
//        for (int dayOfYear = 1; dayOfYear <= totalDays; dayOfYear++) {
//            // 获取 key：当前日期
//            LocalDate currentDate = LocalDate.ofYearDay(year, dayOfYear);
//            // 获取 value：当天是否有刷题
//            boolean hasRecord = signInBitSet.get(dayOfYear);
//            // 将结果放入 map
//            result.put(currentDate, hasRecord);
//        }//这里for循环可以优化  循环内部需要判断当天是否有刷题，实际上每次判断都会去与Redis 交互，一个循环需要交互 365 次 Redis，效率极低！
//        return result;
//    }

    /**
     * 获取用户某个年份的签到记录
     *
     * @param userId 用户 id
     * @param year   年份（为空表示当前年份）
     * @return 签到记录映射
     */
//    Map<LocalDate, Boolean> getUserSignInRecord(long userId, Integer year);
    List<Integer> getUserSignInRecord(long userId, Integer year);

}
