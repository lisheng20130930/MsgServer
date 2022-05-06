package com.web;

import com.alibaba.fastjson.JSON;
import com.config.QProfile;
import org.apache.commons.io.FileUtils;
import org.event.HttpReq;
import org.utils.CmmnUtils;
import org.utils.GZIPUtils;
import org.utils.Logger;
import org.vo.R;
import org.vo.Rsp;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Gate {
    private static final String webRoot = QProfile.DATA_DIR+"/web";
    private static Gate s_gate = new Gate();
    private Controller controller;

    private Gate() {
        this.controller = new Controller();
    }

    public static Gate getInstance() {
        return s_gate;
    }

    private String getSid(Map<String, String> headers) {
        String cookie = headers.get("Cookie".toLowerCase());
        if (null != cookie) {
            int index = cookie.indexOf("JSESSIONID=");
            if (index >= 0) {
                String sid = cookie.substring(index + 11).trim();
                int tmp = sid.indexOf(";");
                if (tmp >= 0) {
                    return sid.substring(0, tmp);
                } else {
                    return sid;
                }
            }
        }
        return null;
    }

    public Rsp handle(HttpReq req) {
        Map<String, String> map = req.parameters();
        String page = req.page();
        if (!page.startsWith("/api")) {
            return deliver(page);
        }
        return api(page, map, true);
    }

    public static Map<String,String> apiHeader(boolean flag, String mimeType, String contentDisposition, int contentLength){
        Map<String, String> headers = new HashMap<>();
        /**
         * 禁止IE11的ajax缓存
         */
        headers.put("Cache-Control", "no-store, no-cache");
        headers.put("Pragma", "no-cache");
        headers.put("Expires", "0");
        headers.put("Content-Type", mimeType);
        headers.put("Content-Length", String.format("%d",contentLength));
        if(!CmmnUtils.isEmpty(contentDisposition)){
            headers.put("Content-disposition",contentDisposition);
        }
        return headers;
    }

    private Rsp api(String page, Map<String, String> map, boolean flag) {
        R r;
        try {
            r = controller.handle(page, map);
        }catch (Exception e){
            Logger.log(e.getMessage());
            r = R.error();
        }
        String mimeType = "application/json";
        byte[] d = null;
        if(page.equals("/api/captcha.jpg")) {
            if(r.get("code").equals(200)) {
                d = (byte[])r.get("captcha");
                mimeType = "image/jpeg";
            }
        }
        String contentDisposition = null;
        if(null==d){
            d = JSON.toJSONString(r).getBytes();
        }
        Logger.log("[Gate] "+page+", params="+map+", r="+r);
        return new Rsp(apiHeader(flag,mimeType,contentDisposition,d.length),d);
    }

    private Rsp deliver(String page) {
        String pathname = null;
        byte[] buffer = null;
        try {
            if (page.equals("/")) {
                page = "/index.html";
            }
            pathname = webRoot + page;
            buffer = FileUtils.readFileToByteArray(new File(pathname));
        } catch (Exception e) {
            Logger.log(e.getMessage());
        }
        if (null == buffer || buffer.length==0) {
            return null;
        }

        Map<String, String> headers = new HashMap<>();
        /**
         * GZIP压缩传输
         */
        boolean compress = !(page.contains(".xls")
                || page.contains(".zip")
                || page.contains(".doc")
                || page.contains(".PDF")
        );
        if(compress){
            buffer = GZIPUtils.compress(buffer);
            headers.put("Content-Encoding", "gzip");
        }
        headers.put("Content-Type", getMimeType(page));
        headers.put("Content-Length", String.format("%d", buffer.length));
        Logger.log("[Gate] web, pathname="+pathname);
        return new Rsp(headers, buffer);
    }

    private String getMimeType(String page) {
        if (page.contains(".htm")) {
            return "text/html";
        }
        if (page.contains(".js")) {
            return "text/javascript";
        }
        if (page.contains(".css")) {
            return "text/css";
        }
        if (page.contains(".mp4")) {
            return "video/mp4";
        }
        if (page.contains(".apk")) {
            return "application/vnd.android.package-archive";
        }
        if (page.contains(".ico")) {
            return "image/x-icon";
        }
        if (page.contains(".ipa")) {
            return "application/vnd.iphone";
        }
        return "application/octet-stream";
    }
}
