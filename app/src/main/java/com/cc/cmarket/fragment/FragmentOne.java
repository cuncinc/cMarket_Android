package com.cc.cmarket.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cc.cmarket.R;
import com.cc.cmarket.source.Data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FragmentOne extends Fragment
{

    RecyclerView recyclerView;

    ListAdapter listAdapter;
    List<Data> dataList = new ArrayList<>();
    private static FragmentOne fragment;


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
        return inflater.inflate(R.layout.fragment_one, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler);
        listAdapter = new ListAdapter(dataList, requireActivity());
        recyclerView.setAdapter(listAdapter);
        for (int i = 0; i < 40; i++)
        {
            dataList.add(new Data(R.mipmap.ic_launcher, "测试+" + i * 30000000 + "sadkjaskd"));
        }
        listAdapter.notifyDataSetChanged();


        listAdapter.setOnItemClick(new OnItemClick()
        {//点击事件逻辑，具体代码看Adapter
            @Override
            public void onItemClick(int pos, Data data)
            {

            }
        });
    }

    class ListAdapter extends RecyclerView.Adapter<ListAdapter.Holder>
    {
        List<Data> datas;
        Context context;
        OnItemClick onItemClick;

        public ListAdapter(List<Data> datas, Context context)
        {
            this.datas = datas;
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
            final Data data = datas.get(position);
            holder.title.setText(data.title);
            holder.imageView.setImageResource(data.imgRes);
            holder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (onItemClick != null)
                    {
                        onItemClick.onItemClick(position, data);
                    }
                }
            });
        }

        @Override
        public int getItemCount()
        {
            return datas.size();
        }

        class Holder extends RecyclerView.ViewHolder
        {
            ImageView imageView;
            TextView title;

            public Holder(@NonNull View itemView)
            {
                super(itemView);
                imageView = itemView.findViewById(R.id.img);
                title = itemView.findViewById(R.id.disc);
            }
        }
    }

    //点击事件接口
    public interface OnItemClick
    {
        void onItemClick(int pos, Data data);
    }
}
