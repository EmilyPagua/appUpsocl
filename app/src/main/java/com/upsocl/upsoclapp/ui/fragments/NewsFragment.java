package com.upsocl.upsoclapp.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.upsocl.upsoclapp.R;
import com.upsocl.upsoclapp.domain.News;
import com.upsocl.upsoclapp.io.ApiConstants;
import com.upsocl.upsoclapp.io.WordpressApiAdapter;
import com.upsocl.upsoclapp.ui.adapters.NewsAdapter;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by leninluque on 09-11-15.
 */
public class NewsFragment extends Fragment implements Callback<ArrayList<News>> {
    private RecyclerView newsList;
    private NewsAdapter adapter;
    private Integer page;
    private LinearLayoutManager llm;
    private ProgressBar spinner;
    private String word;
    private TextView header_news;

    private SwipeRefreshLayout swipeContainer;

    public void setWord(String word) {
        this.word = word;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_news, container, false);
        uploadView(root);

        swipeContainer = (SwipeRefreshLayout) root.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                uploadView(root);
                swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.color_accent_home,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        return root;
    }

    private void uploadView(View root) {
        page = 1;
        loadPosts(page);

        header_news = (TextView) root.findViewById(R.id.header_news_search);
        newsList = (RecyclerView) root.findViewById(R.id.news_list_search);
        newsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new NewsAdapter(getActivity(), false);
        newsList.setAdapter(adapter);
        spinner = (ProgressBar) getActivity().findViewById(R.id.spinner);
        spinner.setVisibility(View.VISIBLE);

        newsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            llm = (LinearLayoutManager) recyclerView.getLayoutManager();
            int size = llm.getItemCount();
            if (size == llm.findLastCompletelyVisibleItemPosition() + 1) {
                page = page + 1;
                spinner.setVisibility(View.VISIBLE);
                loadPosts(page);
            }
            }
        });
    }

    @Override
    public void success(ArrayList<News> newses, Response response) {
        if (newses.size()==0) {
            header_news.setText("No se encontraron resultados para la busqueda: " + word);
            header_news.setVisibility(View.VISIBLE);
        }
        else header_news.setVisibility(View.GONE);

        adapter.addAll(newses);
        spinner.setVisibility(View.GONE);
    }

    @Override
    public void failure(RetrofitError error) {
        System.out.println("NewsFragment "+error );
        adapter.addAll(new ArrayList<News>());
    }

    public void loadPosts(Integer page){

        WordpressApiAdapter.getApiService(ApiConstants.BASE_URL).getListWord(word,page, this);

    }


}
