package cn.com.demo.controller;

import junit.framework.Assert;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import cn.com.demo.pojo.User;
import cn.com.demo.serivce.UserService;

/**
 * The type TestBootController.
 * <p>
 *
 * @author Xiaolu.Liu
 * @date 2018/4/10
 */
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") Long userId) {
        User user = userService.selectByPrimaryKey(userId);
        return user;
    }

    @GetMapping
    public List<User> listUsers() {
        List<User> users = userService.listUsers();

        return users;
    }

    @DeleteMapping("/{id}")
    public boolean deleteUser(@PathVariable("id") Long userId) {
        boolean result = userService.deleteByPrimaryKey(userId);

        return result;
    }

    @PostMapping
    public boolean createUser(@RequestBody User user) {
        boolean result = userService.createUser(user);

        return result;
    }

    @PostMapping("/login")
    public boolean login(@RequestBody User user) {
        //1、获取SecurityManager工厂，此处使用Ini配置文件初始化SecurityManager
        Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
        //2、得到SecurityManager实例 并绑定给SecurityUtils
        SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);
        //3、得到Subject及创建用户名/密码身份验证Token（即用户身份/凭证）
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(user.getName(), user.getPassword());
        try {
            //4、登录，即身份验证
            subject.login(token);
        } catch (AuthenticationException e) {
            //5、身份验证失败
        }
        //断言用户已经登录
        Assert.assertEquals(true, subject.isAuthenticated());
        //6、退出
        subject.logout();

        return true;
    }
}
