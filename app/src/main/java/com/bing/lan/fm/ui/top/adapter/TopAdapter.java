package com.bing.lan.fm.ui.top.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bing.lan.comm.utils.AppUtil;
import com.bing.lan.comm.utils.load.ImageLoader;
import com.bing.lan.fm.R;
import com.bing.lan.fm.ui.top.bean.DatasBean;
import com.bing.lan.fm.ui.top.bean.FirstKResultsBean;
import com.bing.lan.fm.ui.top.bean.ListBeanX;
import com.facebook.drawee.view.SimpleDraweeView;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ItemViewDelegate;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

import static com.bing.lan.fm.R.id.rv_anchor_item_child;

/**
 * @author jk
 * @time 2017/3/7  16:21
 * @desc ${TODD}
 */
public class TopAdapter implements ItemViewDelegate<DatasBean> {
    @Override
    public int getItemViewLayoutId() {
        return R.layout.top_item_star;
    }

    @Override
    public boolean isForViewType(DatasBean item, int position) {
        return true;
    }

    @Override
    public void convert(ViewHolder holder, DatasBean datasBean, int position) {
        /*List<ListBeanX> list = datasBean.getList();
        SimpleDraweeView view = holder.getView(R.id.iv_cover_image);
        //设置图片
        view.setImageURI(list.get(0).getCoverPath());
        //设置标题
        holder.setText(R.id.tv_big_title,list.get(0).getTitle());
        String title = list.get(0).getFirstKResults().get(0).getTitle();
        holder.setText(R.id.tv_mid_title,"1 "+title);
        String title2 = list.get(1).getFirstKResults().get(0).getTitle();
        holder.setText(R.id.tv_samlle_title,"2 "+title2);*/
        holder.setText(R.id.tv_anchor_item_title, datasBean.getTitle());

        List<ListBeanX> list = datasBean.getList();
        initTowRecyclerView(holder, list);

    }

    private void initTowRecyclerView(ViewHolder holder, List<ListBeanX> list) {
        //获取布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(AppUtil.getAppContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        //获取到布局,绑定管理器rv_anchor_item_child
        RecyclerView recycler = holder.getView(rv_anchor_item_child);
        recycler.setLayoutManager(layoutManager);

        //关联数据
        TopAdapter.ChildLayoutRecyclerViewAdapter adapter = new TopAdapter.ChildLayoutRecyclerViewAdapter(
                AppUtil.getAppContext(), R.layout.item_top, list);
        recycler.setAdapter(adapter);
    }

    private class ChildLayoutRecyclerViewAdapter extends CommonAdapter<ListBeanX> {
        public ChildLayoutRecyclerViewAdapter(Context appContext, int layout, List<ListBeanX> list) {
            super(appContext, layout, list);
        }

        @Override
        public int getItemCount() {
            return mDatas.size() - 1;
        }

        @Override
        protected void convert(ViewHolder holder, ListBeanX listBeanX, int position) {
            SimpleDraweeView view = holder.getView(R.id.iv_cover_image);
            //设置图片
            ImageLoader.getInstance().loadImage(view, listBeanX.getCoverPath());

            //设置标题
            holder.setText(R.id.tv_big_title, listBeanX.getTitle());
            //  return            true;
            List<FirstKResultsBean> firstKResults = listBeanX.getFirstKResults();

            if (firstKResults.size() > 0) {
                String title = firstKResults.get(0).getTitle();
                holder.setText(R.id.tv_mid_title, "1 " + title);
            } else if (firstKResults.size() > 1) {
                String title2 = firstKResults.get(1).getTitle();
                holder.setText(R.id.tv_samlle_title, "2 " + title2);
            }

        }
    }
}
