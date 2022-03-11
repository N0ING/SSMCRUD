package com.crud.service;


import com.crud.bean.Employee;
import com.crud.bean.EmployeeExample;
import com.crud.dao.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author NO
 */
@Service
public class EmployeeService {

    @Autowired
    EmployeeMapper employeeMapper;


    /**
     * 保存员工
     * @return
     */
    public void saveEmp(Employee employee) {
        employeeMapper.insertSelective(employee);
    }


    /**
     * 查询所有员工
     * @return
     */
    public List<Employee> getAll() {
        List<Employee> employees = employeeMapper.selectByExample(null);
        return employees;
    }

    /**
     * 校验用户名是否可用
     */
    public boolean checkUser(String empName) {

        EmployeeExample employeeExample = new EmployeeExample();
        EmployeeExample.Criteria criteria = employeeExample.createCriteria();
        criteria.andEmpNameEqualTo(empName);
        long count = employeeMapper.countByExample(employeeExample);


        return count == 0;
    }

    public Employee getEmp(Integer id) {

        Employee employee = employeeMapper.selectByPrimaryKey(id);
        return employee;
    }

    /**
     * 更新员工信息
     */
    public void updateEmp(Employee employee) {
        employeeMapper.updateByPrimaryKeySelective(employee);
    }

    /**
     * 删除单个员工
     * @param id
     */
    public void deleteEmpById(Integer id) {
          employeeMapper.deleteByPrimaryKey(id);
    }

    public void deleteBatch(List<Integer> ids) {

        EmployeeExample employeeExample = new EmployeeExample();
        EmployeeExample.Criteria criteria = employeeExample.createCriteria();
        criteria.andEmpIdIn(ids);
        employeeMapper.deleteByExample(employeeExample);
    }
}
