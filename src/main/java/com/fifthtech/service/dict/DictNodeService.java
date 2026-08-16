package com.fifthtech.service.dict;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fifthtech.dao.entity.dict.DictNode;
import com.fifthtech.dto.dict.DictNodeDTO;
import com.fifthtech.dto.dict.DictNodeMoveDTO;
import com.fifthtech.dto.dict.DictNodeQueryDTO;
import com.fifthtech.vo.dict.DictNodeTreeVO;
import com.fifthtech.vo.dict.DictNodeVO;

import java.util.List;

/**
 * @author RH
 * @ClassName DictNodeService
 * @description: 数据字典节点服务接口
 * @date 2026年08月02日
 * @version: 1.0
 */
public interface DictNodeService extends IService<DictNode> {

    /**
    * @description: 根据父id查询直接子节点，含是否有子节点与路径
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [query]
    * @return: {@link List}<{@link DictNodeVO}>
    **/
    List<DictNodeVO> listChildren(DictNodeQueryDTO query);

    /**
    * @description: 根据父id查询子节点列表
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [query]
    * @return: {@link List}<{@link DictNodeVO}>
    **/
    List<DictNodeVO> list(DictNodeQueryDTO query);

    /**
    * @description: 查询字典节点全树，含叶子与路径
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: []
    * @return: {@link List}<{@link DictNodeTreeVO}>
    **/
    List<DictNodeTreeVO> tree();

    /**
    * @description: 根据id查询字典节点详情，含路径与是否有子节点
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [id]
    * @return: {@link DictNodeVO}
    **/
    DictNodeVO info(Long id);

    /**
    * @description: 新增字典节点
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [dto]
    * @return: {@link DictNodeVO}
    **/
    DictNodeVO insert(DictNodeDTO dto);

    /**
    * @description: 根据id修改字典节点，不改父节点
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [dto]
    * @return: {@link DictNodeVO}
    **/
    DictNodeVO edit(DictNodeDTO dto);

    /**
    * @description: 改挂字典节点父节点，仅无子节点可拖，服务端权威校验防环与深度
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [dto]
    * @return: void
    **/
    void move(DictNodeMoveDTO dto);

    /**
    * @description: 根据id逻辑删除字典节点，有未删子节点则拒绝
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [id]
    * @return: void
    **/
    void delete(Long id);

    /**
    * @description: 根据pathCode查询启用的直接子节点
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [query]
    * @return: {@link List}<{@link DictNodeVO}>
    **/
    List<DictNodeVO> listDataByPathCode(DictNodeQueryDTO query);
}
