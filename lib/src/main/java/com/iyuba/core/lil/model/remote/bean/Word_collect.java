package com.iyuba.core.lil.model.remote.bean;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * @title: 单词收藏
 * @date: 2023/12/19 14:28
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Root(name = "response",strict = false)
public class Word_collect implements Serializable {

    @Element(required = false)
    public int result;
    @Element(required = false)
    public String word;
}
