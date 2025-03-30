package edu.scut.tongzhou.mapper;

import edu.scut.tongzhou.model.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author DS
* @description 针对表【user(用户)】的数据库操作Mapper
* @createDate 2025-03-20 16:31:39
* @Entity generator.domain.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




