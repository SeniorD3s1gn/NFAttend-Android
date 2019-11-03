package com.nfa.android.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

public abstract class StringValidator implements TextWatcher {
    private final TextView view;

    public StringValidator(TextView view) {
        this.view = view;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { }

    @Override
    public void afterTextChanged(Editable s) {
        String content = view.getText().toString();
        validate(view, content);
    }

    public abstract void validate(TextView view, String s);
}
