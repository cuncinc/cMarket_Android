package com.cc.cmarket.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.cc.cmarket.R;
import com.cc.cmarket.activity.DetailActivity;
import com.cc.cmarket.source.Data;
import com.cc.cmarket.source.Goods;
import com.cc.cmarket.source.ResponseObject;
import com.cc.cmarket.utils.OkhttpUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class FragmentOne extends Fragment
{
    private Unbinder unbinder;
    private static FragmentOne fragment;
    private Handler handler = new Handler();

    ListAdapter adapter;
    List<Goods> list = new ArrayList<Goods>();

    @BindView(R.id.recycler)
    RecyclerView recyclerView;

    public static Fragment getInstance()
    {
        if (fragment == null)
        {
            synchronized (FragmentOne.class)
            {
                if (fragment == null)
                {
                    fragment = new FragmentOne();
                }
            }
        }
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_one, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        adapter = new ListAdapter(list, requireActivity());
        recyclerView.setAdapter(adapter);

        initData();

        //点击事件逻辑，具体代码看Adapter
        adapter.setOnItemClick((pos, good) -> {
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtra("goodsId", good.getGoodsId());
            startActivity(intent);
        });
    }

    private void initData()
    {
        OkhttpUtils.get("/goods/getAllDisplayGoods", null, new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e)
            {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
            {
                String json = response.body().string();
                Type type = new TypeToken<ResponseObject<List<Goods>>>()
                {
                }.getType();
                ResponseObject<List<Goods>> object = new Gson().fromJson(json, type);
                list.addAll(object.getData());
                handler.post(() -> adapter.notifyDataSetChanged());
            }
        });
    }

    class ListAdapter extends RecyclerView.Adapter<ListAdapter.Holder>
    {
        List<Goods> goods;
        Context context;
        OnItemClick onItemClick;
        // 图片圆形化
        DrawableCrossFadeFactory factory = new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();

        public ListAdapter(List<Goods> goods, Context context)
        {
            this.goods = goods;
            this.context = context;
        }

        public void setOnItemClick(OnItemClick onItemClick)
        {
            this.onItemClick = onItemClick;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            return new Holder(LayoutInflater.from(context).inflate(R.layout.item_view, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, final int position)
        {
            Goods good = goods.get(position);
            holder.descTv.setText(good.getGoodsDesc());
            holder.priceTv.setText(good.getPrice());
            holder.usernameTv.setText(good.getUserName());
            Glide.with(context).load(good.getPicUrl()).into(holder.goodsImg);
            Glide.with(context).load(good.getAvatarUrl()).transition(withCrossFade(factory)).skipMemoryCache(true)
                    .apply(bitmapTransform(new CropCircleTransformation())) //头像变圆
                    .into(holder.avatarImg);
            holder.itemView.setOnClickListener(view -> {
                if (onItemClick != null)
                {
                    onItemClick.onItemClick(position, good);
                }
            });
        }

        @Override
        public int getItemCount()
        {
            return goods.size();
        }


        class Holder extends RecyclerView.ViewHolder
        {
            @BindView(R.id.item_img)
            ImageView goodsImg;
            @BindView(R.id.item_desc)
            TextView descTv;
            @BindView(R.id.item_avatar)
            ImageView avatarImg;
            @BindView(R.id.item_username)
            TextView usernameTv;
            @BindView(R.id.item_price)
            TextView priceTv;

            public Holder(@NonNull View itemView)
            {
                super(itemView);
                ButterKnife.bind(this, itemView);//此处进行绑定
            }
        }
    }

    //点击事件接口
    public interface OnItemClick
    {
        void onItemClick(int pos, Goods data);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }
}
