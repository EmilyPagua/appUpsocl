package com.upsocl.upsoclapp.ui.fragments;

import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.upsocl.upsoclapp.R;
import com.upsocl.upsoclapp.domain.News;
import com.upsocl.upsoclapp.keys.JsonKeys;
import com.upsocl.upsoclapp.ui.adapters.BookmarksAdapter;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by emily.pagua on 13-07-16.
 */

public class BookmarksFragment extends Fragment  {

    private SharedPreferences preferences;
    private ArrayList<News> newses;


    private ListView newsListView;
    private ArrayAdapter<News> newsArrayAdapter;


    public static BookmarksFragment newInstance(SharedPreferences preferences){
        BookmarksFragment fragment =  new BookmarksFragment();
        fragment.setPreferences(preferences);
        return fragment;

    }

    @Override
    public void onCreate(Bundle saveIntanceState){
        super.onCreate(saveIntanceState);
        if (getArguments() !=null){
            newsListView =null;
            newsArrayAdapter=null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceStates){
        View root = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        newsListView =  (ListView) root.findViewById(R.id.news_list);
        ImageButton ima_bookmarks = (ImageButton) root.findViewById(R.id.ima_bookmarks);

        loadPosts();

        newsArrayAdapter =  new BookmarksAdapter(getActivity(), newses);
        newsListView.setAdapter(newsArrayAdapter);

        return root;
    }

    public void loadPosts(){

        Map<String, ?> map = preferences.getAll();
        map.size();
        newses= new ArrayList<>();

        for (Map.Entry<String, ?> e: map.entrySet()) {
            JsonObject root = new JsonParser().parse(e.getValue().toString()).getAsJsonObject();

            News news = new News();
            news.setId(getElement(root.get(JsonKeys.NEWS_ID)));
            news.setContent(getElement(root.get(JsonKeys.NEWS_CONTENT)));
            news.setTitle(getElement(root.get(JsonKeys.NEWS_TITLE)));
            news.setAuthor(getElement(root.get(JsonKeys.NEWS_AUTHOR)));
            news.setImage(getElement(root.get(JsonKeys.NEWS_IMAGES)));
            news.setDate(getElement(root.get(JsonKeys.NEWS_DATE)));
            news.setLink(getElement(root.get(JsonKeys.NEWS_LINK)));
            news.setCategories(getElement(root.get(JsonKeys.NEWS_CATEGORIES)));
            newses.add(news);
        }
    }
    private String getElement(JsonElement jsonElement) {
        if (jsonElement!=null)
            return jsonElement.getAsString();
        else
            return null;
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }

    public void setPreferences(SharedPreferences preferences) {
        this.preferences = preferences;
    }
}
