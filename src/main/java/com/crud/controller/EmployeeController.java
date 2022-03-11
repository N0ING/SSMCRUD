package com.crud.controller;


import com.crud.bean.Employee;
import com.crud.bean.Msg;
import com.crud.service.EmployeeService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理员工请求
 * @author NO
 * @create 2022-02-14-10:19
 */
@Controller
public class EmployeeController {


    @Autowired
    EmployeeService employeeService;


    /**
     * 员工单个删除和批量删除二合一
     * 单个： 1
     * 批量： 1-2-3
     */
    @ResponseBody
    @RequestMapping(value = "/emp/{ids}",method = RequestMethod.DELETE)
    public Msg deleteEmpById(@PathVariable("ids") String ids){
        if(ids.contains("-")){
            //批量删除
            String[] str_ids = ids.split("-");
            //组装id的集合
            List<Integer> del_ids = new ArrayList<>();
            for (String id : str_ids){
                del_ids.add(Integer.parseInt(id));
            }
            employeeService.deleteBatch(del_ids);
        }else {
            //单个删除
            Integer id = Integer.parseInt(ids);
            employeeService.deleteEmpById(id);
        }

        return Msg.success();
    }


    /**
     * AJAX发送PUT请求引发的血案：
     *      PUT请求，请求体中的数据，request.getParameter(empName)拿不到
     *      Tomcat一看是PUT请求不会封装请求体中的数据为map，只有POST形式的请求才会封装请求体为map
     * 我们要能支持发送PUT之类请求还要封装请求体中的数据
     *1 配置HttpPutFormContentFilter
     * 它的作用：将请求体中的数据包装成一个map
     *          request被重新包装，request.getParameter被重写，就会从自己封装的map中提取员工更新方法
     *
     * 更新员工信息
     */
    @RequestMapping(value = "/emp/{empId}",method = RequestMethod.PUT)
    @ResponseBody
    public Msg updateEmp(Employee employee){
         employeeService.updateEmp(employee);
        return Msg.success();
    }


    /**
     * 查询员工
     */
    @RequestMapping(value = "/emp/{id}",method = RequestMethod.GET)
    @ResponseBody
    public Msg getEmp(@PathVariable("id") Integer id){

        Employee employee = employeeService.getEmp(id);

        return Msg.success().add("emp", employee);
    }



    /**
     * 校验用户名是否可用
     * @return 返回true可用，
     */
    @RequestMapping("/checkuser")
    @ResponseBody
    public Msg checkUser(@RequestParam("empName") String empName){
        //先判断用户名是否合法的表达式
        String regx = "(^[a-z0-9_-]{3,16}$)|(^[\\u2E80-\\u9FFF]{2,10})";
        if(!empName.matches(regx)){
            return Msg.fail().add("va_msg","用户名必须是2-10位中文或者是3-16位的英文数字组合");
        }

        //用户名数据库重复校验
        boolean b = employeeService.checkUser(empName);
        if( b ){
            return Msg.success();
        }else{
            return Msg.fail().add("va_msg", "用户名不可用");
        }
    }


    /**
     * 保存员工
     * 1 支持JSR303的校验
     * 2 hibernate-Validator
     */
    @RequestMapping(value = "/emp",method = RequestMethod.POST)
    @ResponseBody
    public Msg saveEmp(@Valid Employee employee, BindingResult result){

        if(result.hasErrors()){
            //校验失败
            Map<String,Object> map = new HashMap<>();
            List<FieldError> errors = result.getFieldErrors();
            for (FieldError fieldError :errors){
                System.out.println("错误的字段名"+fieldError.getField());
                System.out.println("错误信息"+fieldError.getDefaultMessage());
                map.put(fieldError.getField(),fieldError.getDefaultMessage());
            }
            return Msg.fail().add("errorFields", map);
        }else {
            employeeService.saveEmp(employee);
            return Msg.success();
        }

    }


    /**
     * 查询员工页面（分页查询）
     * @return
     */
    @RequestMapping("/emps")
    @ResponseBody
    public Msg getEmpsWithJson(@RequestParam(value = "pn" ,defaultValue = "1")Integer pn){
        //引入 pageHelper插件 来进行分页查询
        //在查询只要调用，传入页码以及每页的大小
        PageHelper.startPage(pn, 5);
        //startPage后面就是一个分页查询
        List<Employee> emps = employeeService.getAll();
        //使用pageInfo包装查询后的结果后，只需把pageInfo交给页面
        //封装类详细的分页信息，包括有我们查询的数据,传入连续显示的页数
        PageInfo page = new PageInfo(emps,5);
        return Msg.success().add("pageInfo", page);
    }


    /**
     * 查询员工页面（分页查询）
     * @return
     */
    //@RequestMapping("/emps")
//    public String getEmps(@RequestParam(value = "pn" ,defaultValue = "1")Integer pn ,Model model){
//
//        //引入 pageHelper插件 来进行分页查询
//        //在查询只要调用，传入页码以及每页的大小
//        PageHelper.startPage(pn, 5);
//        //startPage后面就是一个分页查询
//       List<Employee> emps = employeeService.getAll();
//        //使用pageInfo包装查询后的结果后，只需把pageInfo交给页面
//        //封装类详细的分页信息，包括有我们查询的数据,传入连续显示的页数
//        PageInfo page = new PageInfo(emps,5);
//        model.addAttribute("pageInfo", page);
//
//        return "list";
//    }

}
