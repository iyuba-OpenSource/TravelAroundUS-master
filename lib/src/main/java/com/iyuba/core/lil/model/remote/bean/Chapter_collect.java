package com.iyuba.core.lil.model.remote.bean;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * @title: 文章收藏操作
 * @date: 2023/12/20 13:46
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Root(name = "response",strict = false)
public class Chapter_collect implements Serializable {

    @Element(required = false)
    public String msg;
    @Element(required = false)
    public int result;
    @Element(required = false)
    public String type;
}
