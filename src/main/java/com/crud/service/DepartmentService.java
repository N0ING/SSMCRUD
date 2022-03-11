package com.crud.service;

import com.crud.bean.Department;
import com.crud.dao.DepartmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author NO
 * @create 2022-02-23-11:41
 */
@Service
public class DepartmentService {

    @Autowired
    private DepartmentMapper departmentMapper;

    public List<Department> getDepartment (){

        List<Department> list = departmentMapper.selectByExample(null);

        return list;
    }
}
