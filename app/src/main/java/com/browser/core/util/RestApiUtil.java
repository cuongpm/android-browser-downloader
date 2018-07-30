package com.browser.core.util;

import com.browser.core.R;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava.HttpException;
import timber.log.Timber;

public class RestApiUtil {


    public static <T extends ErrorMessage> String getError(Throwable throwable, Class<T> clazz) {
        if (throwable instanceof UnknownHostException
                || throwable instanceof ConnectException
                || throwable instanceof SocketTimeoutException) {
            // internet connection error
            return AppUtil.getString(R.string.error_internet_connection);
        } else if (throwable instanceof JsonSyntaxException) {
            // data format error
            return AppUtil.getString(R.string.error_data_format);
        } else if (throwable instanceof HttpException) {
            // HTTP exception (code != 200)
            try {
                T errorMessage = RestApiUtil.parseErrorBody(((HttpException) throwable).response().errorBody(), clazz);
                return errorMessage.getErrorMessage();
            } catch (JsonSyntaxException e) {
                Timber.e(e);
                // data format error
                return AppUtil.getString(R.string.error_data_format);
            } catch (IOException e) {
                Timber.e(e);
                // unknown error
                return AppUtil.getString(R.string.error_unknown);
            }
        } else {
            // unknown error
            return AppUtil.getString(R.string.error_unknown);
        }
    }

    public static <T extends ErrorMessage> T getErrorObject(Throwable throwable, Class<T> clazz) {
        if (throwable instanceof UnknownHostException) {
            // no internet connection
            return null;
        } else if (throwable instanceof HttpException) {
            // HTTP exception (code != 200)
            try {
                return RestApiUtil.parseErrorBody(((HttpException) throwable).response().errorBody(), clazz);
            } catch (Exception e) {
                Timber.e(e);
                return null;
            }
        } else {
            // unknown reason
            return null;
        }
    }

    public static <T extends Object> T parseErrorBody(ResponseBody errorBody, Class<T> clazz) throws JsonSyntaxException, IOException {
        return new Gson().fromJson(errorBody.string(), clazz);
    }

    public static abstract class ErrorMessage {
        protected abstract String getErrorMessage();
    }


}