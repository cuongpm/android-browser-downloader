package com.browser.core.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.browser.core.R;

public class DialogAction {

    private MaterialDialog dialog;

    protected DialogAction(MaterialDialog dialog) {
        this.dialog = dialog;
    }

    public void show() {
        dismiss();
        dialog.show();
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public static class Builder {

        private final View view;

        private final MaterialDialog dialog;

        public Builder(Context context) {
            view = LayoutInflater.from(context).inflate(R.layout.dialog_action, null);
            dialog = new MaterialDialog.Builder(context).customView(view, true).build();
            dialog.setCancelable(false);
            view.findViewById(R.id.btn_negative).setOnClickListener(v -> {
                if (dialog.isShowing())
                    dialog.dismiss();
            });

        }

        public Builder cancelable(boolean cancelable) {
            dialog.setCancelable(cancelable);
            return this;
        }

        public Builder title(String title) {
            ((TextView) view.findViewById(R.id.tv_title)).setText(title);
            view.findViewById(R.id.tv_title).setVisibility(View.VISIBLE);
            return this;
        }

        public Builder message(String message) {
            ((TextView) view.findViewById(R.id.tv_message)).setText(message);
            return this;
        }

        /**
         * Show positive button with input label
         *
         * @param text
         * @param onClickListener
         * @return
         */
        public Builder positive(String text, View.OnClickListener onClickListener) {
            TextView button = (TextView) view.findViewById(R.id.btn_positive);
            button.setVisibility(View.VISIBLE);
            button.setText(text);
            button.setOnClickListener(v -> {
                dialog.dismiss();
                if (onClickListener != null) onClickListener.onClick(v);
            });

            if (onClickListener != null) {
                view.findViewById(R.id.btn_negative).setVisibility(View.VISIBLE);
            }

            return this;
        }

        /**
         * Show postivie button with default label ("OK")
         *
         * @param onClickListener
         * @return
         */
        public Builder positive(View.OnClickListener onClickListener) {
            return positive(view.getContext().getString(R.string.dialog_positive), onClickListener);
        }

        /**
         * Show positive with default label without callback
         *
         * @return
         */
        public Builder positive() {
            return positive(view.getContext().getString(R.string.dialog_positive), null);
        }

        /**
         * Show positive with retry label
         *
         * @param onClickListener
         * @return
         */
        public Builder retry(View.OnClickListener onClickListener) {
            return positive(view.getContext().getString(R.string.dialog_retry), onClickListener);
        }

        /**
         * Show negative button with input label
         *
         * @param text
         * @param onClickListener
         * @return
         */
        public Builder negative(String text, View.OnClickListener onClickListener) {
            TextView button = (TextView) view.findViewById(R.id.btn_negative);
            button.setVisibility(View.VISIBLE);
            button.setText(text);
            button.setOnClickListener(v -> {
                dialog.dismiss();
                if (onClickListener != null) {
                    onClickListener.onClick(v);
                }
            });
            return this;
        }

        /**
         * Show negative button with default label ("Cancel")
         *
         * @param onClickListener
         * @return
         */
        public Builder negative(View.OnClickListener onClickListener) {
            return negative(view.getContext().getString(R.string.dialog_negative), onClickListener);
        }

        /**
         * Show negative button with default label ("Cancel") without callback
         *
         * @return
         */
        public Builder negative() {
            return negative(view.getContext().getString(R.string.dialog_negative), null);
        }

        public DialogAction build() {
            return new DialogAction(dialog);
        }
    }
}
