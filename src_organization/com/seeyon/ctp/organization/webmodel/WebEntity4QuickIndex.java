package com.seeyon.ctp.organization.webmodel;

/**
 * 为快速检索录入组装数据的实体<br>
 * @author lilong
 *
 */
public class WebEntity4QuickIndex {

    private String k; //key 用于检索检索查询
    private String v; //value 检索用的key对应的值
    private String d; //desc 描述信息
    /**
     * 0 || null 无状态数据正确
     * 1 代表 没有找到匹配的数据
     * 2 代表 重复
     */
    private String s = "0"; //state 数据的状态

    public WebEntity4QuickIndex() {
    }

    public WebEntity4QuickIndex(String k, String v, String d) {
        super();
        this.k = k;
        this.v = v;
        this.d = d;
    }
    
    public WebEntity4QuickIndex(String k, String v, String d, String s) {
        super();
        this.k = k;
        this.v = v;
        this.d = d;
        this.s = s;
    }
    
    public WebEntity4QuickIndex(String k, String v) {
        super();
        this.k = k;
        this.v = v;
    }

    public String getK() {
        return k;
    }

    public void setK(String k) {
        this.k = k;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }
    
    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

}
