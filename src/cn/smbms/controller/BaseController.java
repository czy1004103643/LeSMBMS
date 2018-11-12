package cn.smbms.controller;

import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import javax.xml.crypto.Data;
import java.text.SimpleDateFormat;

public class BaseController {
    /**
     * 使用@InitBinder解决SpringMVC日期类型无法绑定的问题
     * @param dataBinder
     */
    @InitBinder
    public void InitBinder(WebDataBinder dataBinder){
        System.out.println("initBinder=================");
        dataBinder.registerCustomEditor(Data.class,
                new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"),true));
    }
}
