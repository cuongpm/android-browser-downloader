package com.browser.downloader.data.remote;

import com.browser.downloader.data.model.ConfigData;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import rx.Observable;
import com.browser.downloader.data.local.Constant;

public interface DataService {

    @GET("config.json")
    Observable<ConfigData> getconfigData();

    class Factory {

        private static DataService dataService;

        public static DataService getInstance() {
            if (dataService == null) {
                dataService = create();
            }
            return dataService;
        }

        public static DataService create() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constant.REMOTE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            return retrofit.create(DataService.class);
        }
    }

}
