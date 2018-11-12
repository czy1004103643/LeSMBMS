package cn.smbms.controller;

import cn.smbms.pojo.Bill;
import cn.smbms.service.bill.BillService;
import cn.smbms.tools.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class BillController {

    @Resource
    private BillService billService;

    @RequestMapping("bill.html")
    public String getBillList(@RequestParam(value = "queryProductName",required = false)String queryProductName,
                              @RequestParam(value = "queryProviderId",required = false)String queryProviderId,
                              @RequestParam(value = "queryIsPayment",required = false)String queryIsPayment,
                              @RequestParam(value = "pageIndex",required = false)String pageIndex, Model model){
        int query=0;
        List<Bill> billList=null;
        //设置页码
        int pageSize= Constants.pageSize;
        //当前页码
        int currentPageNo=1;
        if(queryProductName==null){
            queryProductName="";
        }
        if(queryProviderId!=null&&!queryProviderId.equals("")){
            query=Integer.parseInt(queryProviderId);
        }
        if(pageIndex!=null){
            try {
                currentPageNo=Integer.valueOf(pageIndex);
            } catch (NumberFormatException e) {
                return "redirect:error.html";
            }
        }
        return "jsp/billlist";
    }
}
