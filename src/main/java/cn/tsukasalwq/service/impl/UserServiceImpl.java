package cn.tsukasalwq.service.impl;

import cn.tsukasalwq.common.Const;
import cn.tsukasalwq.common.ServerResponse;
import cn.tsukasalwq.common.TokenCache;
import cn.tsukasalwq.dao.UserMapper;
import cn.tsukasalwq.pojo.User;
import cn.tsukasalwq.service.IUserService;
import cn.tsukasalwq.utils.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0) {
            return ServerResponse.createByErrorByMessage("用户名不存在");
        }
        // 2017/8/12 密码登陆MD5
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, md5Password);
        if (user == null) {
            return ServerResponse.createByErrorByMessage("密码错误");
        }

        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登陆成功", user);
    }

    @Override
    public ServerResponse<String> register(User user) {

        ServerResponse validResponce = this.checkValid(user.getUsername(), Const.USERNAME);
        if (!validResponce.isSuccess()) {
            return validResponce;
        }

        validResponce = this.checkValid(user.getEmail(), Const.EMAIL);
        if (!validResponce.isSuccess()) {
            return validResponce;
        }

        int resultCount = userMapper.checkEmail(user.getEmail());
        if (resultCount > 0) {
            return ServerResponse.createByErrorByMessage("email不存在");
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);
        // MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        resultCount = userMapper.insert(user);
        if (resultCount == 0) {
            return ServerResponse.createByErrorByMessage("注册失败");
        }

        return ServerResponse.createBySuccessByMessage("注册成功");
    }

    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNotBlank(type)) {
            // 开始校验
            if (Const.USERNAME.equals(type)) {
                int resultCount = userMapper.checkUsername(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorByMessage("用户名已存在");
                }
            }
            if (Const.EMAIL.equals(type)) {
                int resultCount = userMapper.checkEmail(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorByMessage("email已存在");
                }
            }
        } else {
            return ServerResponse.createByErrorByMessage("参数错误");
        }

        return ServerResponse.createBySuccessByMessage("校验成功");
    }


    public ServerResponse selectQuestion(String username) {

        ServerResponse validResponce = this.checkValid(username, Const.USERNAME);
        if (validResponce.isSuccess()) {
            // 用户不存在
            return ServerResponse.createByErrorByMessage("用户不存在");
        }

        String question = userMapper.selectQuestionByUsername(username);
        if (StringUtils.isNotBlank(question)) {
            return ServerResponse.createBySuccess(question);
        }

        return ServerResponse.createByErrorByMessage("找回密码的问题是空的");
    }


    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if (resultCount > 0) {
            // 说明问题及问题答案是用户的，并且正确
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFFIX + username, forgetToken);
            return ServerResponse.createByErrorByMessage(forgetToken);
        }

        return ServerResponse.createByErrorByMessage("问题的答案错误");
    }

    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        if (StringUtils.isBlank(forgetToken)) {
            return ServerResponse.createByErrorByMessage("参数错误，token需要传递");
        }

        ServerResponse validResponce = this.checkValid(username, Const.USERNAME);
        if (validResponce.isSuccess()) {
            return ServerResponse.createByErrorByMessage("用户不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFFIX + username);

        if (StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorByMessage("token无效或者已经过期");
        }

        if (StringUtils.equals(token, forgetToken)) {
            // 更新密码
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username, md5Password);
            if (rowCount > 0) {
                return ServerResponse.createBySuccess("修改密码成功");
            }
        } else {
            return ServerResponse.createByErrorByMessage("token错误，请重新获取重置密码的token");
        }

        return ServerResponse.createByErrorByMessage("修改密码失败");
    }


    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {
        // 防止横向越权，要校验一下这个用户的旧密码，一定要指定这个用户，因为我们会查询一个count(1),如果不指定id必然为true即count>0
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld), user.getId());
        if (resultCount == 0) {
            return ServerResponse.createByErrorByMessage("旧密码错误");
        }

        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 0) {
            return ServerResponse.createBySuccess("密码更新成功");
        }
        return ServerResponse.createByErrorByMessage("密码更新失败");
    }

    public ServerResponse<User> updateInformation(User user) {
        // username 不能被更新
        // email 也要进行一个校验，检验新的email是不是已经存在，并且存在相同的email相同的话，不能是我们当前的用户的
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
        if (resultCount > 0) {
            return ServerResponse.createByErrorByMessage("email已存在");
        }

        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (updateCount > 0) {
            return ServerResponse.createBySuccess("更新个人信息成功", updateUser);
        }
        return ServerResponse.createByErrorByMessage("更新个人信息失败");
    }


    @Override
    public ServerResponse<User> getInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return ServerResponse.createByErrorByMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY); // 防止密码泄露
        return ServerResponse.createBySuccess(user);
    }

    // backend

    /**
     * 校验是否是管理员
     * @param user
     * @return
     */
    public ServerResponse checkAdminRole(User user) {
        if (user != null && user.getRole() == Const.Role.ROLE_ADMIN) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
