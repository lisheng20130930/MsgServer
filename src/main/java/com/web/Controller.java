package com.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.utils.CmmnUtils;
import org.vo.R;

import java.util.HashMap;
import java.util.Map;

public class Controller {
    private R onPublish(Map<String, String> params) {
        String destId = params.get("destId");
        String content = params.get("content");
        if(CmmnUtils.isEmpty(destId)
                ||CmmnUtils.isEmpty(content)){
            return R.error("Bad Params");
        }
        boolean r = Service.getInstance().publish(destId,content);
        if(!r){
            return R.error();
        }
        return R.ok();
    }

    public R handle(String page, Map<String, String> params) {
        if(page.equals("/api/publish")){
            return onPublish(params);
        }
        return R.error("Not Supported");
    }

    public static void main(String[] args){
        Map<String,String> m = new HashMap<>();
        m.put("destId", "100000000");
        m.put("content", "this is a test message!");
        R r = new Controller().onPublish(m);
        System.out.println(JSON.toJSONString(r, SerializerFeature.PrettyFormat));
    }
}
