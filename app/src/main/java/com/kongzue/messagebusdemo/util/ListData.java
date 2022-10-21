package com.kongzue.messagebusdemo.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Kongzue
 * @github: https://github.com/kongzue/
 * @homepage: http://kongzue.com/
 * @mail: myzcxhh@live.cn
 * @createTime: 2022/10/21 18:24
 */
public class ListData {
    
    List<Data> list;
    
    public ListData() {
        list = new ArrayList<>();
        list.add(new Data("zhangsan","2022"));
        list.add(new Data("lisi","2021"));
        list.add(new Data("wangwu","2020"));
        list.add(new Data("zhaoliu","2019"));
        list.add(new Data("sunqi","2018"));
        list.add(new Data("sunqi","2017"));
        list.add(new Data("zhouba","2016"));
        list.add(new Data("wujiu","2015"));
        list.add(new Data("zhenshi","2014"));
        list.add(new Data("kongzue","create"));
        list.add(new Data("version","1.0.0"));
        list.add(new Data("github","kongzue/Runner"));
        list.add(new Data("license","Apache License 2.0"));
        list.add(new Data("again","———————————————————————————————"));
        list.add(new Data("zhangsan","2022"));
        list.add(new Data("lisi","2021"));
        list.add(new Data("wangwu","2020"));
        list.add(new Data("zhaoliu","2019"));
        list.add(new Data("sunqi","2018"));
        list.add(new Data("sunqi","2017"));
        list.add(new Data("zhouba","2016"));
        list.add(new Data("wujiu","2015"));
        list.add(new Data("zhenshi","2014"));
        list.add(new Data("kongzue","create"));
        list.add(new Data("version","1.0.0"));
        list.add(new Data("github","kongzue/Runner"));
        list.add(new Data("license","Apache License 2.0"));
    }
    
    class Data{
        public Data(String title, String tip) {
            this.title = title;
            this.tip = tip;
        }
        String title;
        String tip;
    }
}
