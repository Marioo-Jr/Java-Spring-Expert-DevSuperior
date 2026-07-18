package com.devsuperior.demo.dto;

import com.devsuperior.demo.entities.Department;

public class DepartmentDTO {

    public Long id;
	public String name;

    public DepartmentDTO(){}

    public DepartmentDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public DepartmentDTO(Department entity) {
        id = entity.getId();
        name = entity.getName();
    }

}
