package com.iyuba.core.lil.model.remote.bean;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.List;

/**
 * @title: 文章收藏列表
 * @date: 2023/12/20 11:39
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Root(name = "response",strict = false)
public class Chapter_collect_show implements Serializable {

    @Element(required = false)
    public int counts;
    @Element(required = false)
    public int pageNumber;
    @Element(required = false)
    public int totalPage;
    @Element(required = false)
    public int firstPage;
    @Element(required = false)
    public int prevPage;
    @Element(required = false)
    public int nextPage;
    @Element(required = false)
    public int lastPage;
    @Element(required = false)
    public int result;
    @Element(required = false)
    public String msg;
    @ElementList(required = false,inline = true)
    public List<CollectBean> list;

    @Root(name = "row",strict = false)
    public static class CollectBean{

        @Element(required = false)
        public int voaid;
        @Element(required = false)
        public String sentenceid;
        @Element(required = false)
        public String createDate;
    }
}
