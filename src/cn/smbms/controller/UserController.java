package cn.smbms.controller;

import cn.smbms.pojo.Role;
import cn.smbms.pojo.User;
import cn.smbms.service.role.RoleService;
import cn.smbms.service.user.UserService;
import cn.smbms.service.user.UserServiceImpl;
import cn.smbms.tools.Constants;
import cn.smbms.tools.PageSupport;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;
import com.sun.applet2.preloader.event.UserDeclinedEvent;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Controller
public class UserController extends BaseController {

    @Resource
    private UserService userService;

    @Resource
    private RoleService roleService;

    @RequestMapping("/login.html")
    public String login() {
        return "login";
    }

    @RequestMapping("/doLogin.html")
    public String doLogin(String userCode, String userPassword, HttpServletRequest request, HttpServletResponse response) {
        //调用service方法，进行用户匹配
        User user = userService.login(userCode, userPassword);
        if (null != user) {//登录成功
            //放入session
            request.getSession().setAttribute(Constants.USER_SESSION, user);
            //页面跳转（frame.jsp）
            return "redirect:main.html";
        } else {
            //页面跳转（login.jsp）带出提示信息--转发
            request.setAttribute("error", "请输入正确的账号密码！");
            return "login";
        }
    }

    @ExceptionHandler(value = {RuntimeException.class})
    public String handlerException(RuntimeException e, HttpServletRequest req) {
        req.setAttribute("e", e);
        return "error";
    }

    @RequestMapping("/main.html")
    public String main() {
        return "jsp/frame";
    }

    @RequestMapping("/logout.do")
    public String loginOut(HttpSession session) {
        //销毁会话
        session.removeAttribute(Constants.USER_SESSION);
        return "login";
    }

    @RequestMapping("error.html")
    public String Error() {
        return "error";
    }

    @RequestMapping("/user.html")
    public String getUserList(@RequestParam(value = "queryname", required = false) String queryUserName,
                              @RequestParam(value = "queryUserRole", required = false) String queryUserRole,
                              @RequestParam(value = "pageIndex", required = false) String pageIndex, Model model) {
        int _queryUserRole = 0;
        List<User> userList = null;
        //设置页码
        int pageSize = Constants.pageSize;
        //当前页码
        int currentPageNo = 1;
        if (queryUserName == null) {
            queryUserName = "";
        }
        if (queryUserRole != null && !queryUserRole.equals("")) {
            _queryUserRole = Integer.parseInt(queryUserRole);
        }
        if (pageIndex != null) {
            try {
                currentPageNo = Integer.valueOf(pageIndex);
            } catch (NumberFormatException e) {
                return "redirect:error.html";
            }
        }
        //总数量
        int totalCount = userService.getUserCount(queryUserName, _queryUserRole);
        //总页数
        PageSupport pages = new PageSupport();
        pages.setCurrentPageNo(currentPageNo);
        pages.setPageSize(pageSize);
        pages.setTotalCount(totalCount);
        int totalPageCount = pages.getTotalPageCount();
        //控制首页和尾页
        if (currentPageNo < 1) {
            currentPageNo = 1;
        } else if (currentPageNo > totalPageCount) {
            currentPageNo = totalPageCount;
        }
        userList = userService.getUserList(queryUserName, _queryUserRole, currentPageNo, pageSize);
        model.addAttribute("userList", userList);
        List<Role> roleList = null;
        roleList = roleService.getRoleList();
        model.addAttribute("roleList", roleList);
        model.addAttribute("queryUserName", queryUserName);
        model.addAttribute("queryUserRole", queryUserRole);
        model.addAttribute("totalPageCount", totalPageCount);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("currentPageNo", currentPageNo);
        return "jsp/userlist";
    }

    @RequestMapping("/pwdmodify.html")
    public String pwdmodify(String oldPassword, String newPassword, String reNewPassword, HttpServletRequest request) {
        Object o = request.getSession().getAttribute(Constants.USER_SESSION);
        boolean flag = false;
        if (o != null && oldPassword != null && newPassword != null && reNewPassword != null) {
            UserService userService = new UserServiceImpl();
            flag = userService.updatePwd(((User) o).getId(), oldPassword);
            if (flag) {
                request.getSession().removeAttribute(Constants.USER_SESSION);//session注销
            }
        }
        return "jsp/pwdmodify";
    }

    @RequestMapping("/useradd.html")
    public String addUser(@ModelAttribute("user") User user) {
        return "jsp/useradd";
    }

    @RequestMapping(value = "/addsave.html", method = RequestMethod.POST)
    public String addUserSave(User user, HttpSession session,
                              BindingResult bindingResult,
                              HttpServletRequest request,
                              @RequestParam(value = "a_idPicPath", required = false) MultipartFile[] multipartFile) {
        if (bindingResult.hasErrors()) {
            return "jsp/useradd";
        }
        user.setCreatedBy(((User) session.getAttribute(Constants.USER_SESSION)).getId());
        String idPicPath=null;
        String workPicPath=null;
        for(int i=0;i<multipartFile.length;i++){
           MultipartFile attach=multipartFile[i];
            //文件上传
            String oldName = attach.getOriginalFilename();//原文件名
            System.out.println("原文件名:" + oldName);
            String type = oldName.substring(oldName.lastIndexOf(".") + 1);
            //设置文件大小，检查文件后缀名
            //储存文件
            //获取要存储的路径   绝对路径
            String path = session.getServletContext().getRealPath("statics" + File.separator + "uploadfile");
            File filepath = new File(path);
            if (!filepath.exists()) {
                filepath.mkdirs();//自动创建路径
                System.out.println(filepath);
            }
            //生成一个文件名
            String name = UUID.randomUUID().toString() + "." + type;
            File targetFile = new File(path, name);//要存储的文件
            user.setIdPicPath(name);
            try {
                attach.transferTo(targetFile);//存储到targetfile
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(i==0){
                idPicPath=path+File.separator+filepath;
            }else if(i==1){
                workPicPath=path+File.separator+workPicPath;
            }
        }
        user.setCreationDate(new Date());
        user.setIdPicPath(idPicPath);
        user.setWorkPicPath(workPicPath);
        if (userService.add(user)) {
            return "redirect:/user.html";
        } else {
            return "jsp/useradd";
        }
    }

    @RequestMapping(value = "/testadduser.html", method = RequestMethod.GET)
    public String testAddUserpage(@ModelAttribute User user) {
        return "/jsp/jsp/Testadduser";
    }

    @RequestMapping(value = "/testadduser.html", method = RequestMethod.POST)
    public String testAddUser(@Valid User user, BindingResult bindingResult, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "/jsp/jsp/Testadduser";
        }
        int createBy = ((User) session.getAttribute(Constants.USER_SESSION)).getId();
        user.setCreatedBy(createBy);
        user.setCreationDate(new Date());
        if (userService.add(user)) {
            return "redirect:/user.html";//重定向到用户列表
        }
        return "/jsp/jsp/Testadduser";//转发回添加用户页面
    }

    @RequestMapping("/usermodify.html")
    public String getUserById(@RequestParam String uid, Model model) {
        Integer id = 0;
        try {
            id = Integer.parseInt(uid);
        } catch (NumberFormatException e) {
            throw new RuntimeException("数字格式有误！");
        }
        User user = userService.getUserById(uid);
        model.addAttribute(user);
        return "jsp/usermodify";
    }

    @RequestMapping("/usermodifysave.html")
    public String modifyUserSerSave(User user, HttpSession session) {
        user.setModifyBy(((User) session.getAttribute(Constants.USER_SESSION)).getId());
        user.setModifyDate(new Date());
        if (userService.modify(user)) {
            return "redirect:/user.html";
        }
        return "jsp/error";
    }

//    @RequestMapping("/view/{id}")
//    public String userview(@PathVariable String id,Model model){
//        User user=userService.getUserById(id);
//        model.addAttribute(user);
//        return "jsp/userview";
//    }

    @RequestMapping("/ucexist.xml")
    @ResponseBody
    public Object userCodeIsExit(@RequestParam String userCode) {
        HashMap<String, String> resultMap = new HashMap<>();
        if (StringUtils.isNullOrEmpty(userCode)) {
            resultMap.put("userCode", "exist");
        } else {
            User user = userService.selectUserCodeExist(userCode);
            if (null != user) {
                resultMap.put("userCode", "exist");
            } else {
                resultMap.put("userCode", "noexist");
            }
        }
        return JSONArray.toJSONString(resultMap);
    }

    @RequestMapping("/oldpassword.html")
    @ResponseBody
    public Object repassword(@RequestParam String oldpassword,HttpSession session){
        HashMap<String,String> password=new HashMap<>();
        User user=userService.getUserById(((User) session.getAttribute(Constants.USER_SESSION)).getId().toString());
        if(user==null){
            password.put("result","false");
        }
        if(StringUtils.isNullOrEmpty(oldpassword)){
            password.put("result","false");
        }else {
            password.put("result","true");
        }
        if(oldpassword.length()==0){
            password.put("result","error");
        }
        return JSONArray.toJSONString(oldpassword);
    }

    @RequestMapping(value = "/view", method = RequestMethod.GET)
    @ResponseBody
    public User view(@RequestParam String id) {
        User user = new User();
        try {
            user = userService.getUserById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

}
