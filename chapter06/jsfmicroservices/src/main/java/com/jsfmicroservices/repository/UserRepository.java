/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
 * and open the template in the editor.
*/
package com.jsfmicroservices.repository;

import com.avbravo.jmoordb.mongodb.repository.Repository;

import com.jsfmicroservices.entity.User;
import javax.ejb.Stateless;

/**
 *
 * @author avbravo
 */
@Stateless
public class UserRepository extends Repository<User> {
       public UserRepository(){
        super(User.class,"jsfdata","user");
    }   
}
