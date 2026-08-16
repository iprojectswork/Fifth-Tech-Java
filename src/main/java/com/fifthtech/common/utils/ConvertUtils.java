package com.fifthtech.common.utils;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RH
 * @ClassName ConvertUtils
 * @description: 对象转换工具
 * @date 2026年01月25日
 * @version: 1.0
 */
public class ConvertUtils {

    /**
    * @description: 单对象转 Entity
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [source, targetClass]
    * @return: {@link T}
    **/
    public static <S, T> T toEntity(S source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("转换失败", e);
        }
    }

    /**
    * @description: 单对象转 DTO
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [source, targetClass]
    * @return: {@link T}
    **/
    public static <S, T> T toDTO(S source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("转换失败", e);
        }
    }

    /**
    * @description: 单对象转 VO
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [source, targetClass]
    * @return: {@link T}
    **/
    public static <S, T> T toVO(S source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("转换失败", e);
        }
    }

    /**
    * @description: 集合转 Entity 列表
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [sourceList, targetClass]
    * @return: {@link List}<{@link T}>
    **/
    public static <S, T> List<T> toEntityList(List<S> sourceList, Class<T> targetClass) {
        if (sourceList == null || sourceList.isEmpty()) {
            return new ArrayList<>();
        }
        List<T> targetList = new ArrayList<>(sourceList.size());
        for (S source : sourceList) {
            targetList.add(toEntity(source, targetClass));
        }
        return targetList;
    }

    /**
    * @description: 集合转 DTO 列表
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [sourceList, targetClass]
    * @return: {@link List}<{@link T}>
    **/
    public static <S, T> List<T> toDTOList(List<S> sourceList, Class<T> targetClass) {
        if (sourceList == null || sourceList.isEmpty()) {
            return new ArrayList<>();
        }
        List<T> targetList = new ArrayList<>(sourceList.size());
        for (S source : sourceList) {
            targetList.add(toDTO(source, targetClass));
        }
        return targetList;
    }

    /**
    * @description: 集合转 VO 列表
    * @author: RH
    * @date: 2026/8/16 13:21
    * @param: [sourceList, targetClass]
    * @return: {@link List}<{@link T}>
    **/
    public static <S, T> List<T> toVOList(List<S> sourceList, Class<T> targetClass) {
        if (sourceList == null || sourceList.isEmpty()) {
            return new ArrayList<>();
        }
        List<T> targetList = new ArrayList<>(sourceList.size());
        for (S source : sourceList) {
            targetList.add(toVO(source, targetClass));
        }
        return targetList;
    }
}