package cn.smbms.controller;

import cn.smbms.dao.provider.ProviderDao;
import cn.smbms.pojo.Provider;
import cn.smbms.pojo.User;
import cn.smbms.service.provider.ProviderService;
import cn.smbms.service.provider.ProviderServiceImpl;
import cn.smbms.tools.Constants;
import cn.smbms.tools.PageSupport;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

@Controller
public class ProviderController {

    @Resource
    private ProviderService providerService;

    @RequestMapping("/provider.html")
    public String getProviderList(@RequestParam(value = "queryProCode",required = false)String queryProCode,
                                  @RequestParam(value = "queryProName",required = false)String queryProName,
                                  @RequestParam(value = "pageIndex",required = false)String pageIndex,Model model){
        int _queryProvider=0;
        List<Provider> providerList=null;
        //设置页码
        int pageSize= Constants.pageSize;
        //当前页码
        int currentPageNo=1;
        if(queryProCode==null){
            queryProCode="";
        }
        if(queryProName==null){
            queryProName="";
        }
        if(queryProCode!=null&&!queryProCode.equals("")){
            _queryProvider=Integer.parseInt(queryProCode);
        }
        if(pageIndex!=null){
            try {
                currentPageNo=Integer.valueOf(pageIndex);
            } catch (NumberFormatException e) {
                return "redirect:error.html";
            }
        }
        //总数量
        int totalCount=providerService.getProviderCount(queryProName,queryProCode);
        //总页数
        PageSupport pages=new PageSupport();
        pages.setCurrentPageNo(currentPageNo);
        pages.setPageSize(pageSize);
        pages.setTotalCount(totalCount);
        int totalPageCount=pages.getTotalPageCount();
        //控制首页和尾页
        if(currentPageNo<1){
            currentPageNo=1;
        }else if (currentPageNo>totalPageCount){
            currentPageNo=totalPageCount;
        }
        providerList=providerService.getProviderList(queryProName,queryProCode,currentPageNo,pageSize);
        model.addAttribute("providerList",providerList);
        model.addAttribute("queryProCode", queryProCode);
        model.addAttribute("queryProName", queryProName);
        model.addAttribute("totalPageCount", totalPageCount);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("currentPageNo", currentPageNo);
        return "jsp/providerlist";
    }

    @RequestMapping("/addprovider.html")
    public String addProvider(){
        return "jsp/provideradd";
    }

    @RequestMapping("/saveProvider.html")
    public String saveProvider(Provider provider, HttpSession session){
        provider.setCreatedBy(((User)session.getAttribute(Constants.USER_SESSION)).getId());
        boolean flag = false;
        flag = providerService.add(provider);
        if(flag){
            return "redirect:/provider.html";
        }
        return "redirect:/addprovider.html";
    }

    @RequestMapping("/proview/{id}")
    public String proview(@PathVariable String id,Model model){
        Provider provider=providerService.getProviderById(id);
        model.addAttribute(provider);
        return "jsp/providerview";
    }

    @RequestMapping("/providermodify.html")
    public String getProById(@RequestParam String pid,Model model){
        Provider provider=providerService.getProviderById(pid);
        model.addAttribute(provider);
        return "jsp/providermodify";
    }

    @RequestMapping("/providermodifysave.html")
    public String modifyProvider(Provider provider,HttpSession session){
        provider.setModifyBy(((User) session.getAttribute(Constants.USER_SESSION)).getId());
        provider.setCreationDate(new Date());
        if(providerService.modify(provider)){
            return "redirect:/provider.html";
        }
        return "jsp/error";
    }
}
