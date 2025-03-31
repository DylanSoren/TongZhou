package edu.scut.tongzhou.mapper;

import edu.scut.tongzhou.model.entity.Tag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author DS
* @description 针对表【tag(标签)】的数据库操作Mapper
* @createDate 2025-03-30 11:29:58
* @Entity edu.scut.tongzhou.model.entity.Tag
*/
@Mapper
public interface TagMapper extends BaseMapper<Tag> {

}




