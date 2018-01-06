package cn.tsukasalwq.controller.backend;

import cn.tsukasalwq.common.Const;
import cn.tsukasalwq.common.ResponseCode;
import cn.tsukasalwq.common.ServerResponse;
import cn.tsukasalwq.pojo.User;
import cn.tsukasalwq.service.ICategoryService;
import cn.tsukasalwq.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/category/")
public class CartegoryManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping(value = "add_category.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse addCategory(HttpSession session, String categoryName, @RequestParam(value = "parentId", defaultValue = "0") Integer parentId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        // 检验用户是否是管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            // 增加我们处理分类的逻辑
            return iCategoryService.addCategory(categoryName, parentId);
        } else {
            return ServerResponse.createByErrorByMessage("无权限操作，需要管理员权限");
        }
    }

    @RequestMapping(value = "set_category_name.do", method = RequestMethod.PATCH)
    @ResponseBody
    public ServerResponse setCategoryName(HttpSession session, Integer categoryId, String categoryName) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }

        // 检验用户是否是管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            // 增加我们修改分类的逻辑
            return iCategoryService.updateCategory(categoryId, categoryName);
        } else {
            return ServerResponse.createByErrorByMessage("无权限操作，需要管理员权限");
        }
    }


    @RequestMapping(value = "get_category.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getChildrenParalleCategory(HttpSession session,@RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }

        // 检验用户是否是管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            // 查询当前子节点的同级分类
            return iCategoryService.getChildrenParallelCategory(categoryId);
        } else {
            return ServerResponse.createByErrorByMessage("无权限操作，需要管理员权限");
        }
    }

    @RequestMapping(value = "get_deep_category.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpSession session, Integer categoryId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }

        // 检验用户是否是管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            // 递归查询当前节点的所有子节点Category
            return iCategoryService.getCategoryAndChildrenById(categoryId);
        } else {
            return ServerResponse.createByErrorByMessage("无权限操作，需要管理员权限");
        }
    }
}
