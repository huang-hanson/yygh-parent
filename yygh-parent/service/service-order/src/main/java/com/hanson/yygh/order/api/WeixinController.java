package com.hanson.yygh.order.api;

import com.alibaba.fastjson.JSONObject;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import com.hanson.yygh.common.result.Result;
import com.hanson.yygh.order.service.PaymentService;
import com.hanson.yygh.order.service.WeixinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author hanson
 * @date 2024/5/29 22:40
 */
@RestController
@RequestMapping("/api/order/weixin")
public class WeixinController {

    @Autowired
    private WeixinService weixinService;

    @Autowired
    private PaymentService paymentService;

    //生成微信支付二维码
    @GetMapping("createNative/{orderId}")
    public Result createNative(@PathVariable Long orderId) {
        Map map = weixinService.createNative(orderId);
        return Result.ok(map);
    }

    //查询支付状态
    @GetMapping("queryPayStatus/{orderId}")
    public Result queryPayStatus(@PathVariable Long orderId) {
        //调用微信接口实现支付状态查询
        Map<String, String> resultMap = weixinService.queryPayStatus(orderId);
        //判断
        if (resultMap == null) {
            return Result.fail().message("支付出错");
        }
        if ("SUCCESS".equals(resultMap.get("trade_state"))) { //支付成功
            //更新订单状态
            String out_trade_no = resultMap.get("out_trade_no");//订单编码
            paymentService.paySuccess(out_trade_no, resultMap);
            return Result.ok().message("支付成功");
        }
        return Result.ok().message("支付中");
    }
}
