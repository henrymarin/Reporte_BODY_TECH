package com.bodytech.reporte.repositorios;

import org.springframework.data.repository.CrudRepository;

import com.bodytech.reporte.entidades.User;




public interface UserRepository extends CrudRepository<User, Long> {

}
