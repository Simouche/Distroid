package com.safesoft.safemobile.ui.generics.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.safesoft.safemobile.R;

import java.util.ArrayList;
import java.util.Objects;




/**
 * Created by Md Farhan Raja on 2/23/2017.
 * <p>
 * modified by Chaguetmi Wassim on 12/15/2019
 */

public class SpinnerDialog<O> {
    private ArrayList<O> items;
    private Activity context;
    private String dTitle, closeTitle = "Close";
    private OnSpinnerItemClick<O> onSpinnerItemClick;
    private AlertDialog alertDialog;
    private int style;
    private boolean cancellable = false;
    private boolean showKeyboard = false;

    public SpinnerDialog(Activity activity, ArrayList<O> items, String dialogTitle) {
        this.items = items;
        this.context = activity;
        this.dTitle = dialogTitle;
    }

    public void bindOnSpinnerListener(OnSpinnerItemClick<O> onSpinnerItemClick1) {
        this.onSpinnerItemClick = onSpinnerItemClick1;
    }

    public void showSpinnerDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        View v = context.getLayoutInflater().inflate(R.layout.dialog_layout, null);
        TextView rippleViewClose = v.findViewById(R.id.close);
        TextView title = v.findViewById(R.id.spinnerTitle);
        rippleViewClose.setText(closeTitle);
        title.setText(dTitle);
        final ListView listView = v.findViewById(R.id.purchases_list);
        final EditText searchBox = v.findViewById(R.id.searchBox);
        if (isShowKeyboard()) {
            showKeyboard(searchBox);
        }
        final ArrayAdapter<O> adapter = new ArrayAdapter<>(context, R.layout.items_view, items);
        listView.setAdapter(adapter);
        adb.setView(v);
        alertDialog = adb.create();
        Objects.requireNonNull(alertDialog.getWindow()).getAttributes().windowAnimations = style; //R.style.DialogAnimations_SmileWindow;

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            onSpinnerItemClick.onClick(items.get(i));
            closeSpinnerDialog();
        });

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                adapter.getFilter().filter(searchBox.getText().toString());
            }
        });

        rippleViewClose.setOnClickListener(v1 -> closeSpinnerDialog());
        alertDialog.setCancelable(isCancellable());
        alertDialog.setCanceledOnTouchOutside(isCancellable());
        alertDialog.show();
    }

    private void closeSpinnerDialog() {
        hideKeyboard();
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    private void hideKeyboard() {
        try {
            InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            Objects.requireNonNull(inputManager).hideSoftInputFromWindow(Objects.requireNonNull(context.getCurrentFocus()).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showKeyboard(final EditText ettext) {
        ettext.requestFocus();
        ettext.postDelayed(() -> {
                    InputMethodManager keyboard = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    Objects.requireNonNull(keyboard).showSoftInput(ettext, 0);
                }
                , 200);
    }

    private boolean isCancellable() {
        return cancellable;
    }

    public void setCancellable(boolean cancellable) {
        this.cancellable = cancellable;
    }

    private boolean isShowKeyboard() {
        return showKeyboard;
    }

    public void setShowKeyboard(boolean showKeyboard) {
        this.showKeyboard = showKeyboard;
    }

    public void setTitle(String dTitle) {
        this.dTitle = dTitle;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public void setdTitle(String dTitle) {
        this.dTitle = dTitle;
    }

    public void setCloseTitle(String closeTitle) {
        this.closeTitle = closeTitle;
    }
}
