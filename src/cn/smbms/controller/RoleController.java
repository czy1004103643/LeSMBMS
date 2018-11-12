package cn.smbms.controller;

import cn.smbms.pojo.Role;
import cn.smbms.service.role.RoleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/role")
public class RoleController {

    @Resource
    private RoleService roleService;

    @RequestMapping(value = "/list",produces = {"application/json;charset=utf-8"})
    @ResponseBody//直接返回内容，而不是跳转到某个页面
    public String list() {
        List<Role> roleList = roleService.getRoleList();
        StringBuffer json = new StringBuffer();
        //拼接json
        json.append("[");
        for (int i=0;i<roleList.size();i++){
            json.append("{");
            //拼接属性值
            json.append("\"id\":\""+roleList.get(i).getId()+"\",");
            json.append("\"roleName\":\""+roleList.get(i).getRoleName()+"\"");
            json.append("}");
            if(i==roleList.size()-1){
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }
}
