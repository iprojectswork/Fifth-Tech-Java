package com.fifthtech.service.org;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fifthtech.dao.entity.org.Org;
import com.fifthtech.dao.entity.user.User;
import com.fifthtech.dto.org.OrgDTO;
import com.fifthtech.dto.org.OrgMembersDTO;
import com.fifthtech.dto.org.OrgMoveDTO;
import com.fifthtech.dto.org.OrgQueryDTO;
import com.fifthtech.vo.org.OrgMemberVO;
import com.fifthtech.vo.org.OrgTreeVO;
import com.fifthtech.vo.org.OrgVO;

import java.util.List;
import java.util.Map;

/**
 * @author RH
 * @ClassName OrgService
 * @description: 组织服务接口
 * @date 2026年08月09日
 * @version: 1.0
 */
public interface OrgService extends IService<Org> {

    /**
    * @description: 根据父id查询直接子组织，含是否有子节点与路径
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [query]
    * @return: {@link List}<{@link OrgVO}>
    **/
    List<OrgVO> listChildren(OrgQueryDTO query);

    /**
    * @description: 根据父id查询子组织列表
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [query]
    * @return: {@link List}<{@link OrgVO}>
    **/
    List<OrgVO> list(OrgQueryDTO query);

    /**
    * @description: 查询组织全树，含叶子与路径
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: []
    * @return: {@link List}<{@link OrgTreeVO}>
    **/
    List<OrgTreeVO> tree();

    /**
    * @description: 根据id查询组织详情，含路径与是否有子节点
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [id]
    * @return: {@link OrgVO}
    **/
    OrgVO info(Long id);

    /**
    * @description: 新增组织
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [dto]
    * @return: {@link OrgVO}
    **/
    OrgVO insert(OrgDTO dto);

    /**
    * @description: 根据id修改组织，不改父节点
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [dto]
    * @return: {@link OrgVO}
    **/
    OrgVO edit(OrgDTO dto);

    /**
    * @description: 改挂组织父节点，校验防环与深度
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [dto]
    * @return: void
    **/
    void move(OrgMoveDTO dto);

    /**
    * @description: 根据id逻辑删除组织，有子节点或成员则拒绝
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [id]
    * @return: void
    **/
    void delete(Long id);

    /**
    * @description: 查询启用组织扁平列表，供前端组装树形选择器
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: []
    * @return: {@link List}<{@link OrgVO}>
    **/
    List<OrgVO> options();

    /**
    * @description: 根据组织id和角色id查询成员用户
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [query]
    * @return: {@link List}<{@link User}>
    **/
    List<User> usersByRole(OrgQueryDTO query);

    /**
    * @description: 根据组织id查询成员
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [query]
    * @return: {@link List}<{@link OrgMemberVO}>
    **/
    List<OrgMemberVO> listMembers(OrgQueryDTO query);

    /**
    * @description: 根据组织id全量替换成员
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [dto]
    * @return: void
    **/
    void replaceMembers(OrgMembersDTO dto);

    /**
    * @description: 根据组织id查询自身及全部下级id
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [orgId]
    * @return: {@link List}<{@link Long}>
    **/
    List<Long> subtreeOrgIds(Long orgId);

    /**
    * @description: 根据组织id列表查询名称路径
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [orgIds]
    * @return: {@link Map}<{@link Long}, {@link String}>
    **/
    Map<Long, String> pathNames(List<Long> orgIds);
}
