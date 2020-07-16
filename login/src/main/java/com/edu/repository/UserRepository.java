package com.edu.repository;

import com.edu.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author WangSong
 *	这个类使用spring data jpa实现链接数据库，简单的增删改查方法
 */

public interface UserRepository extends JpaRepository<User, Long> {

	//通过登陆用户名，得到该用户信息
	User findByUsername(String username);

	List<User> findByUsernameOrEmail(String name, String email);

	//通过邮箱查询用户
	User findByEmail(String email);

	/**
	 *注意：使用下面的方式修改数据时，需要在调用的地方加上事务，不然不能正常使用或者不能启动
     */
	//更新用户数据(未修改密码)
	@Modifying
	@Query(value="update User o set o.email=:email,o.lastModifyTime=:lastModifyTime where o.username =:username")
	int updateWithoutPassword(@Param("email") String email, @Param("username") String username, @Param("lastModifyTime") String lastModifyTime);

	//更新用户数据(用户已修改密码)
	@Modifying
	@Query(value="update User o set o.email=:email,o.password=:password,o.lastModifyTime=:lastModifyTime where o.username =:username")
	int updateWithPassword(@Param("email") String email, @Param("username") String username, @Param("password") String password, @Param("lastModifyTime") String lastModifyTime);

	//批量删除用户
	@Modifying
	@Query("delete from User s where s.userId in (?1)")
	void deleteByIds(List<Long> ids);

}
