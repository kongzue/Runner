package com.kongzue.messagebusdemo.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.kongzue.messagebusdemo.R;
import com.kongzue.runner.interfaces.AutoCreateListViewInterface;

/**
 * @author: Kongzue
 * @github: https://github.com/kongzue/
 * @homepage: http://kongzue.com/
 * @mail: myzcxhh@live.cn
 * @createTime: 2022/10/21 18:03
 */
public class AutoCreateListView extends ListView implements AutoCreateListViewInterface {
    
    public AutoCreateListView(Context context) {
        super(context);
    }
    
    public AutoCreateListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public AutoCreateListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    
    @Override
    public int itemLayoutRes() {
        return R.layout.item_list_test;
    }
}
