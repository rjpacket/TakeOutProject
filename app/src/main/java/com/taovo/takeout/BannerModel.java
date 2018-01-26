package com.taovo.takeout;

import java.io.Serializable;

/**
 * @author Gimpo create on 2018/1/26 16:07
 * @email : jimbo922@163.com
 */

public class BannerModel implements Serializable{
    private int id;
    private String name;
    private String pic;
    private String func;
    private int sortNo;

    private Object appVersion;
    private Object clientUpdateInfo;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public int getSortNo() {
        return sortNo;
    }

    public void setSortNo(int sortNo) {
        this.sortNo = sortNo;
    }

    public Object getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(Object appVersion) {
        this.appVersion = appVersion;
    }

    public Object getClientUpdateInfo() {
        return clientUpdateInfo;
    }

    public void setClientUpdateInfo(Object clientUpdateInfo) {
        this.clientUpdateInfo = clientUpdateInfo;
    }
}
