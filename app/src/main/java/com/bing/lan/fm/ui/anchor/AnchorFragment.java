package com.bing.lan.fm.ui.anchor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import com.bing.lan.comm.base.mvp.fragment.BaseFragment;
import com.bing.lan.comm.di.FragmentComponent;
import com.bing.lan.comm.utils.AppUtil;
import com.bing.lan.fm.R;
import com.bing.lan.fm.ui.anchor.bean.AnchorResult;
import com.bing.lan.fm.ui.anchor.bean.FamousBean;
import com.bing.lan.fm.ui.anchor.bean.NormalBean;
import com.bing.lan.fm.ui.anchor.delagate.MultipleItemDelagate;
import com.bing.lan.fm.ui.anchor.delagate.UpperRecomItemDelagate;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.wrapper.HeaderAndFooterWrapper;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;

/**
 * 主播界面
 */
public class AnchorFragment extends BaseFragment<IAnchorContract.IAnchorPresenter>
        implements IAnchorContract.IAnchorView, SwipeRefreshLayout.OnRefreshListener {

    /**1级数据*/
    private List<FamousBean> mRecyclerViewData;
    private List<NormalBean> mTowRecyclerViewData;

    @BindView(R.id.anchor_recyclerView)
    RecyclerView mAnchorRecyclerView;
    @BindView(R.id.anchor_refresh_container)
    SwipeRefreshLayout mAnchorRefreshContainer;
    /**代理的对象*/
    private MultiItemTypeAdapter<FamousBean> mMultiItemTypeAdapter;
    private MultiItemTypeAdapter<NormalBean> mTwoMultiItemTypeAdapter;
    private HeaderAndFooterWrapper mHeaderAndFooterWrapper;
    /**添加一个尾部*/
    private View mFoot;
    /**尾部的控件对象*/
    private RecyclerView mTworecyclerView;


    /**
     * 绑定的布局
     *
     * @return 返回当前的布局
     */
    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_anchor;
    }

    /**
     * @param fragmentComponent 注入界面
     */
    @Override
    protected void startInject(FragmentComponent fragmentComponent) {
        fragmentComponent.inject(this);
    }

    /**
     * 用于发起网络请求
     * 申请设置点击事件
     */
    @Override
    protected void readyStartPresenter() {
        //设置刷新监听事件
        mAnchorRefreshContainer.setOnRefreshListener(this);
        //发起一个网络请求
        mPresenter.onStart();
    }

    /**
     * @param intent    Intent 的实例
     * @param arguments Fragment 传递的参数
     */
    @Override
    protected void initViewAndData(Intent intent, Bundle arguments) {
        initFoot();
        //初始化数据,绑定第一个布局
        initRecyclerView();


    }

    /**
     * 添加一个尾部
     */
    private void initFoot() {
        //打气筒
        //找到布局
        mFoot = mLayoutInflater.inflate(R.layout.anchor_recycler_fo, null);
        //设置宽高
        ViewGroup.LayoutParams params = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mFoot.setLayoutParams(params);

        mTworecyclerView = (RecyclerView) mFoot.findViewById(R.id.anchor_foot_recyclerView);
        //设置模式
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };//绑定模式
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        mTworecyclerView.setLayoutManager(linearLayoutManager);

        mTowRecyclerViewData = new ArrayList<>();
        //设置代理
        mTwoMultiItemTypeAdapter = new MultiItemTypeAdapter<>(AppUtil.getAppContext(), mTowRecyclerViewData);
        mTwoMultiItemTypeAdapter.addItemViewDelegate(new MultipleItemDelagate());
        //关联代理,刷新数据
        mTworecyclerView.setAdapter(mTwoMultiItemTypeAdapter);
        mTwoMultiItemTypeAdapter.notifyDataSetChanged();

        /*if(mTwoMultiItemTypeAdapter != null) {
            mHeaderAndFooterWrapper.notifyDataSetChanged();
        }*/

    }

    //绑定数据
    private void initRecyclerView() {
        /*RecyclerView 的代理*/
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        mAnchorRecyclerView.setLayoutManager(linearLayoutManager);

        //空的集合,用来装载数据
        mRecyclerViewData = new ArrayList<>();

        //创建一个代理的对象
        mMultiItemTypeAdapter = new MultiItemTypeAdapter<>(AppUtil.getAppContext(), mRecyclerViewData);

        //添加adapter对象
        mMultiItemTypeAdapter.addItemViewDelegate(new UpperRecomItemDelagate());

        mHeaderAndFooterWrapper = new HeaderAndFooterWrapper(mMultiItemTypeAdapter);
        //TODO
        mHeaderAndFooterWrapper.addFootView(mFoot);
        //关联数据
        mAnchorRecyclerView.setAdapter(mHeaderAndFooterWrapper);
        //刷新
        mHeaderAndFooterWrapper.notifyDataSetChanged();
    }


    /**
     * 得到数据后,开始操作数据
     *
     * @param datas 所有的数据
     */
    @Override
    public void upDateRecyclerView(AnchorResult datas) {
        //一层数据
        famousData(datas);
        //二层数据
        normalBeenData(datas);
    }

    /**
     * 二层数据
     */
    private void normalBeenData(AnchorResult datas) {
        List<NormalBean> normalBeen = datas.getNormal();
        //清除所有数据
        mTowRecyclerViewData.clear();
        //添加数据
        mTowRecyclerViewData.addAll(normalBeen);

        //刷新数据
        mTwoMultiItemTypeAdapter.notifyDataSetChanged();
        mHeaderAndFooterWrapper.notifyDataSetChanged();

    }

    /**
     * 一层数据
     */
    private void famousData(AnchorResult datas) {
        //一层数据
        List<FamousBean> famous = datas.getFamous();
        //清除所有数据
        mRecyclerViewData.clear();
        //添加数据
        mRecyclerViewData.addAll(famous);
        //刷新数据
        mMultiItemTypeAdapter.notifyDataSetChanged();
        mHeaderAndFooterWrapper.notifyDataSetChanged();

    }



    /**
     * 刷新监听
     */
    @Override
    public void onRefresh() {
        mPresenter.reStartUpdate();
    }

    /**
     * 关闭刷新
     */
    public void closeRefreshing() {
        if (mAnchorRefreshContainer != null && mAnchorRefreshContainer.isRefreshing()) {
            mAnchorRefreshContainer.setRefreshing(false);
        }

    }

    /**
     * 回收资源
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
