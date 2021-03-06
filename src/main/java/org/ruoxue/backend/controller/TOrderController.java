package org.ruoxue.backend.controller;


import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import org.ruoxue.backend.service.ITOrderService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author fengjb
 * @since 2018-08-30
 */
@Controller
@RequestMapping("/api")
public class TOrderController {

    @Resource
    private ITOrderService orderService;

    @ApiOperation("查看客户的对公充值记录(分类)")
    @RequestMapping(value = "/customer/{uid}/orders/{status}", method = RequestMethod.GET)
    public @ResponseBody
    Object ordersStatus(@PathVariable Integer uid, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size, @PathVariable String status, @RequestParam(required = false) Integer count) {
        return orderService.ordersStatus(uid, page, size, status, count);
    }

    @ApiOperation("获取产品订单信息")
    @RequestMapping(value = "/order/{status}", method = RequestMethod.GET)
    public @ResponseBody Object listOrder(@RequestParam(required = false) Integer cid, @RequestParam(required = false) Integer type, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size, @PathVariable String status, @RequestParam(required = false) Date start, @RequestParam(required = false) Date end, @RequestParam(required = false) Integer count) {
        return orderService.listOrder(cid, type, page, size, status, start, end, count);
    }

    @ApiOperation("手动为客户增加订单")
    @RequestMapping(value = "/customer/_/company/{cid}/order/new", method = RequestMethod.POST)
    public @ResponseBody Object addCustomerOrder(@PathVariable Integer cid, @RequestBody JSONObject jsonObject) {
        return orderService.addCustomerOrder(cid, jsonObject);
    }


	
}
