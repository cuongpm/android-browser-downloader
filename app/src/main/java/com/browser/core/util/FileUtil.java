package com.browser.core.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.browser.core.R;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class FileUtil {

    public final static String FOLDER_NAME = "VideoDownloader";

    private static final DecimalFormat format = new DecimalFormat("#.##");
    private static final long MiB = 1024 * 1024;
    private static final long KiB = 1024;

    public static File getFolderDir() {
        return new File(Environment.getExternalStorageDirectory(), FOLDER_NAME);
    }

    public static ArrayList<File> getListFiles() {
        File[] files = getFolderDir().listFiles();
        return files == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(files));
    }

    public static String getFileSize(File file) {
        try {
            double length = file.length();
            return getFileSize(length);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getFileSize(double length) {
        if (length > MiB) {
            return format.format(length / MiB) + " MB";
        }
        if (length > KiB) {
            return format.format(length / KiB) + " KB";
        }
        return format.format(length) + " B";
    }

    public static String getFileExtension(String path) {
        try {
            Uri uri = Uri.parse(path);
            return MimeTypeMap.getFileExtensionFromUrl(uri.toString().replace(" ", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void scanMedia(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        context.sendBroadcast(intent);
    }

    private static void deleteMedia(Context context, File file) {
        context.getContentResolver().delete(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                MediaStore.Video.Media.DATA + "=?", new String[]{file.getAbsolutePath()});
    }

    public static void renameFile(Context context, File file, OnFileChangedCallback onFileChangedCallback) {
        try {
            final InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

            String extension = "." + getFileExtension(file.getPath());
            String currentName = file.getName().substring(0, file.getName().length() - extension.length());

            LinearLayout layout = new LinearLayout(context);
            layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(80, 40, 80, 20);
            EditText etName = new EditText(context);
            etName.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            etName.setText(currentName);
            etName.setSelection(etName.getText().length());
            etName.setTextColor(Color.BLACK);
            etName.setImeOptions(EditorInfo.IME_ACTION_DONE);
            etName.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            etName.setSingleLine();
            layout.addView(etName);

            new AlertDialog.Builder(context).setTitle(context.getString(R.string.rename_title)).setView(layout)
                    .setNegativeButton(context.getString(android.R.string.cancel),
                            (dialog, whichButton) -> imm.hideSoftInputFromWindow(etName.getWindowToken(), 0))
                    .setPositiveButton(context.getString(android.R.string.ok), (dialog, whichButton) -> {
                        String fileName = etName.getText().toString().trim();
                        if (!TextUtils.isEmpty(fileName)) {
                            File newFile = new File(getFolderDir(), fileName + extension);
                            if (newFile.exists()) {
                                Toast.makeText(context, context.getString(R.string.rename_exist), Toast.LENGTH_SHORT).show();
                            } else if (file.renameTo(newFile)) {
                                deleteMedia(context, file);
                                scanMedia(context, newFile);
                                onFileChangedCallback.renameFileCompleted(newFile.getName());
                            }
                        } else {
                            Toast.makeText(context, context.getString(R.string.rename_invalid), Toast.LENGTH_SHORT).show();
                        }
                        imm.hideSoftInputFromWindow(etName.getWindowToken(), 0);
                    }).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void renameFile(Context context, String name, OnFileChangedCallback onFileChangedCallback) {
        try {
            final InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

            String extension = "." + getFileExtension(name);
            String currentName = name.substring(0, name.length() - extension.length());

            LinearLayout layout = new LinearLayout(context);
            layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(80, 40, 80, 20);
            EditText etName = new EditText(context);
            etName.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            etName.setText(currentName);
            etName.setSelection(etName.getText().length());
            etName.setTextColor(Color.BLACK);
            etName.setImeOptions(EditorInfo.IME_ACTION_DONE);
            etName.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            etName.setSingleLine();
            layout.addView(etName);

            new AlertDialog.Builder(context).setTitle(context.getString(R.string.rename_title)).setView(layout)
                    .setNegativeButton(context.getString(android.R.string.cancel),
                            (dialog, whichButton) -> imm.hideSoftInputFromWindow(etName.getWindowToken(), 0))
                    .setPositiveButton(context.getString(android.R.string.ok), (dialog, whichButton) -> {
                        String fileName = etName.getText().toString().trim();
                        if (!TextUtils.isEmpty(fileName)) {
                            onFileChangedCallback.renameFileCompleted(fileName + extension);
                        } else {
                            Toast.makeText(context, context.getString(R.string.rename_invalid), Toast.LENGTH_SHORT).show();
                        }
                        imm.hideSoftInputFromWindow(etName.getWindowToken(), 0);
                    }).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnFileChangedCallback {
        void renameFileCompleted(String fileName);
    }

}
