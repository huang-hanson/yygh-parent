package com.hanson.yygh.hosp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hanson.yygh.common.exception.YyghException;
import com.hanson.yygh.common.result.Result;
import com.hanson.yygh.common.utils.MD5;
import com.hanson.yygh.hosp.service.HospitalSetService;
import com.hanson.yygh.model.hosp.HospitalSet;
import com.hanson.yygh.vo.hosp.HospitalSetQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

/**
 * @author hanson
 * @date 2024/5/22 13:05
 */
@Api(description = "医院设置接口")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
//@CrossOrigin
public class HospitalSetController {

    // 注入service
    @Autowired
    private HospitalSetService hospitalSetService;

    // http://localhost:8201/admin/hosp/hospitalSet/findAll

    // 1.查询医院设置表所有信息
    @ApiOperation(value = "获取所有医院设置")
    @GetMapping("findAll")
    public Result findAllHospitalSet() {
        // 调用service的方法
        List<HospitalSet> list = hospitalSetService.list();
        return Result.ok(list);
    }

    // 2.删除医院设置
    @ApiOperation(value = "逻辑删除医院设置")
    @DeleteMapping("{id}")
    public Result removeHospSet(@PathVariable Long id) {
        boolean flag = hospitalSetService.removeById(id);
        if (flag) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    // 3.条件查询带分页
    @PostMapping("findPageHospSet/{current}/{limit}")
    public Result findPageHospSet(@PathVariable Long current,
                                  @PathVariable Long limit,
                                  @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo) {
        // 创建page对象，传递当前页，每页记录数
        Page<HospitalSet> page = new Page<>(current, limit);
        // 构建条件
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        String hosname = hospitalSetQueryVo.getHosname();
        String hoscode = hospitalSetQueryVo.getHoscode();
        if (!StringUtils.isEmpty(hosname)) {
            wrapper.like("hosname", hospitalSetQueryVo.getHosname());
        }
        if (!StringUtils.isEmpty(hoscode)) {
            wrapper.eq("hoscode", hospitalSetQueryVo.getHoscode());
        }

        // 调用方法实现分页查询
        Page<HospitalSet> pageHospitalSet = hospitalSetService.page(page, wrapper);

        // 返回结果
        return Result.ok(pageHospitalSet);
    }


    // 4.添加医院设置
    @PostMapping("saveHospitalSet")
    public Result saveHospitalSet(@RequestBody HospitalSet hospitalSet) {
        // 设置状态 1 使用 0 不能使用
        hospitalSet.setStatus(1);
        // 签名密钥
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis() + "" + random.nextInt(1000)));
        // 调用service
        boolean save = hospitalSetService.save(hospitalSet);
        if (save) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    // 5.根据id获取医院设置
    @GetMapping("getHospSet/{id}")
    public Result getHospitalSet(@PathVariable Long id) {
        // 模拟一个异常
//        try{
//            int a = 1/0;
//        }catch (Exception e){
//            throw new YyghException("失败",201);
//        }
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        return Result.ok(hospitalSet);

    }

    // 6.修改医院设置
    @PostMapping("updateHospitalSet")
    public Result updateHospitalSet(@RequestBody HospitalSet hospitalSet) {
        boolean flag = hospitalSetService.updateById(hospitalSet);
        if (flag) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    // 7.批量删除医院设置
    @DeleteMapping("batchRemove")
    public Result batchRemoveHospitalSet(@RequestBody List<Long> idList) {
        hospitalSetService.removeByIds(idList);
        return Result.ok();
    }

    // 8.医院设置锁定和解锁
    @PutMapping("lockHospitalSet/{id}/{status}")
    public Result lockHospitalSet(@PathVariable Long id,
                                  @PathVariable Integer status) {
        // 根据id查询医院设置信息
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        // 设置状态
        hospitalSet.setStatus(status);
        // 调用方法
        hospitalSetService.updateById(hospitalSet);
        return Result.ok();
    }


    // 9.发送签名密钥
    @PutMapping("sendKey/{id}")
    public Result lockHospitalSet(@PathVariable Long id) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        String signKey = hospitalSet.getSignKey();
        String hoscode = hospitalSet.getHoscode();
        // TODO 发送短信
        return Result.ok();
    }

}
