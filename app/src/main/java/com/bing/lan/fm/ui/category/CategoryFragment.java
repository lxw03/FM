package com.bing.lan.fm.ui.category;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.bing.lan.comm.base.mvp.fragment.BaseFragment;
import com.bing.lan.comm.di.FragmentComponent;
import com.bing.lan.comm.utils.AppUtil;
import com.bing.lan.fm.R;
import com.bing.lan.fm.cons.Constants;
import com.bing.lan.fm.ui.category.adapter.CategoryInfoDelagate;
import com.bing.lan.fm.ui.category.adapter.SecendCategoryInfoDelagate;
import com.bing.lan.fm.ui.category.bean.CategoryListBean;
import com.bing.lan.fm.ui.categorydetails.CategoryDetailActivity;
import com.facebook.drawee.view.SimpleDraweeView;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.wrapper.HeaderAndFooterWrapper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.bing.lan.comm.utils.AppUtil.dip2px;
import static com.bing.lan.fm.R.id.iv_category_head_image;

/**
 *
 */
public class CategoryFragment extends BaseFragment<ICategoryContract.ICategoryPresenter>
        implements ICategoryContract.ICategoryView {
    private HeaderAndFooterWrapper mHeaderAndFooterWrapper;
    private List<CategoryListBean.ListBean> mRecyclerViewData;
    @BindView(R.id.recyclerView_categolory)
    RecyclerView mRecyclerView;
    SimpleDraweeView mHeadImage;
    private MultiItemTypeAdapter<CategoryListBean.ListBean> mMultiItemTypeAdapter;

    public static CategoryFragment newInstance(String title) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putString(Constants.FRAGMENT_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    //这个地方是创建布局的地方.
    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_categolory;
    }

    @Override
    protected void startInject(FragmentComponent fragmentComponent) {
        fragmentComponent.inject(this);
    }


    @Override
    protected void initViewAndData(Intent intent, Bundle arguments) {
        initRecyclerView();
        //设置fragment的背景色

    }

    /*-----------设置RecyclerView---------*/
    private void initRecyclerView() {
        //设置recyclerview
        GridLayoutManager gridLayoutManager = new GridLayoutManager(AppUtil.getAppContext(),
                2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        //设置分割线
        int leftRight = dip2px(10);
        int topBottom = dip2px(10);
    //   mRecyclerView.addItemDecoration(new SpacesItemDecoration(leftRight, topBottom));
      // mRecyclerView.addItemDecoration(new SpacesItemDecoration(dip2px(2), dip2px(2), getResources().getColor(R.color.colorPrimary)));
        //mRecyclerView.addItemDecoration(new SpacesItemDecoration(2,2));

        mRecyclerViewData = new ArrayList<>();
        mMultiItemTypeAdapter = new MultiItemTypeAdapter<>(AppUtil.getAppContext(), mRecyclerViewData);
        CategoryInfoDelagate editorRecomItemDelagate = new CategoryInfoDelagate();
        SecendCategoryInfoDelagate seceditorRecomItemDelagate = new SecendCategoryInfoDelagate();

        //2.设置普通的数据
        mMultiItemTypeAdapter.addItemViewDelegate(seceditorRecomItemDelagate);
        mMultiItemTypeAdapter.addItemViewDelegate(editorRecomItemDelagate);
        mMultiItemTypeAdapter.notifyDataSetChanged();
        setCategoryHeader();
        mMultiItemTypeAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), CategoryDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(CategoryDetailActivity.CATEGORYDETAILS_ITEM, mRecyclerViewData.get(position));
                intent.putExtras(bundle);
                startActivity(intent);

            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });

    }

    void setCategoryHeader() {
        //1.1通过一个打气筒创建一个view
        View mHeadView = mLayoutInflater.inflate(R.layout.category_item_head, null);
        //1.2设置宽和高
        int viewpageHeight = (int) (AppUtil.getScreenW() * 0.5f);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, viewpageHeight);
        mHeadView.setLayoutParams(layoutParams);
        //1.3定义数据
        mHeadImage = (SimpleDraweeView) mHeadView.findViewById(iv_category_head_image);
        mHeaderAndFooterWrapper = new HeaderAndFooterWrapper(mMultiItemTypeAdapter);
        mHeaderAndFooterWrapper.addHeaderView(mHeadView);
        mHeaderAndFooterWrapper.notifyDataSetChanged();
        mRecyclerView.setAdapter(mHeaderAndFooterWrapper);
    }


    //抓的包的url需要的
    @Override
    protected void readyStartPresenter() {

        //进行网络请求
        mPresenter.onStart();
    }

    /*-----------初始化数据---------*/
    @Override
    public void setRecyclerViewDatas(List<CategoryListBean.ListBean> list) {
        mRecyclerViewData.clear();
        for (int i = 0; i < list.size(); i++) {
            if (i % 6 == 0) {
                CategoryListBean.ListBean listBean = new CategoryListBean.ListBean();
                listBean.setNormal(false);
            }
            if (i != 0) {
                //如果是第0个数据,那就不要加入到集合中
                mRecyclerViewData.add(list.get(i));
            }
        }
        mHeadImage.setImageURI(list.get(0).getCoverPath());
        mMultiItemTypeAdapter.notifyDataSetChanged();
        mHeaderAndFooterWrapper.notifyDataSetChanged();
    }


}
