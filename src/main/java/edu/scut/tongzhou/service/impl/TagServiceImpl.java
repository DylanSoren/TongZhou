package edu.scut.tongzhou.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.scut.tongzhou.model.entity.Tag;
import edu.scut.tongzhou.service.TagService;
import edu.scut.tongzhou.mapper.TagMapper;
import org.springframework.stereotype.Service;

/**
* @author DS
* @description 针对表【tag(标签)】的数据库操作Service实现
* @createDate 2025-03-30 11:29:58
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

}




