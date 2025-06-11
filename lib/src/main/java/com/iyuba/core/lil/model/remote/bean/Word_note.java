package com.iyuba.core.lil.remote.bean;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * @title: 生词本模型
 * @date: 2023/12/18 11:13
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Root(name = "response",strict = false)
public class Word_note {

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
    @ElementList(required = false,inline = true)
    public List<TempWord> list;

    @Root(name = "row",strict = false)
    public static class TempWord{
        @Element(required = false)
        public String Word;
        @Element(required = false)
        public String createDate;
        @Element(required = false)
        public String Audio;
        @Element(required = false)
        public String Pron;
        @Element(required = false)
        public String Def;
    }
}
