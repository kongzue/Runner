package com.kongzue.runner.interfaces;

import android.content.Context;
import android.widget.ListAdapter;

/**
 * @author: Kongzue
 * @github: https://github.com/kongzue/
 * @homepage: http://kongzue.com/
 * @mail: myzcxhh@live.cn
 * @createTime: 2022/10/21 18:03
 */
public interface AutoCreateListViewInterface {
    
    int itemLayoutRes();
    
    void setAdapter(ListAdapter adapter);
    
    ListAdapter getAdapter();
    
    Context getContext();
}
