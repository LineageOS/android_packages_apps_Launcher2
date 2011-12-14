/*
 * Copyright (C) 2011 The Cyanogenmod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher2.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.util.Log;

import com.android.launcher.R;

/*
 * @author Danesh
 * @author nebkat
 */

public class NumberPickerPreference extends DialogPreference {
    private CharSequence mDialogTitle;
    private NumberPicker picker;
    private int mMin, mMax, mDefault;

    private String mMaxExternalKey, mMinExternalKey;
    private Preference mMaxExternalPreference, mMinExternalPreference;

    private NumberPicker mNumberPicker;

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray dialogType = context.obtainStyledAttributes(attrs,
                com.android.internal.R.styleable.DialogPreference, 0, 0);
        TypedArray numberPickerType = context.obtainStyledAttributes(attrs,
                R.styleable.NumberPickerPreference, 0, 0);

        String maxExternalKey = numberPickerType.getString(R.styleable.NumberPickerPreference_maxExternal);
        String minExternalKey = numberPickerType.getString(R.styleable.NumberPickerPreference_minExternal);
        if (maxExternalKey != null) {
            mMaxExternalKey = maxExternalKey;
        }
        if (minExternalKey != null) {
            mMinExternalKey = minExternalKey;
        }

        mMax = numberPickerType.getInt(R.styleable.NumberPickerPreference_max, 5);
        mMin = numberPickerType.getInt(R.styleable.NumberPickerPreference_min, 0);

        mDefault = dialogType.getInt(com.android.internal.R.styleable.Preference_defaultValue, mMin);
        dialogType.recycle();
    }


    protected void onAttachedToActivity() {
        if (mMaxExternalKey != null) {
            Preference maxPreference = findPreferenceInHierarchy(mMaxExternalKey);
            if (maxPreference != null) {
                if (maxPreference instanceof NumberPickerPreference) {
                    mMaxExternalPreference = maxPreference;
                }
            }
        }

        if (mMinExternalKey != null) {
            Preference minPreference = findPreferenceInHierarchy(mMinExternalKey);
            if (minPreference != null) {
                if (minPreference instanceof NumberPickerPreference) {
                    mMinExternalPreference = minPreference;
                }
            }
        }
    }

    @Override
    protected View onCreateDialogView() {
        int max = mMax;
        int min = mMin;
        if (mMaxExternalKey != null && mMaxExternalPreference != null) {
            max = mMaxExternalPreference.getSharedPreferences().getInt(mMaxExternalKey, mMax);
        }
        if (mMinExternalKey != null && mMinExternalPreference != null) {
            min = mMinExternalPreference.getSharedPreferences().getInt(mMinExternalKey, mMin);
        }

        LayoutInflater inflater =
                (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.number_picker_dialog, null);

        mNumberPicker = (NumberPicker) view.findViewById(R.id.number_picker);

        // initialize state
        mNumberPicker.setMaxValue(max);
        mNumberPicker.setMinValue(min);
        mNumberPicker.setValue(getPersistedInt(mDefault));
        mNumberPicker.setWrapSelectorWheel(false);

        EditText textInput = (EditText) mNumberPicker.findViewById(com.android.internal.R.id.numberpicker_input);
        textInput.setCursorVisible(false);
        textInput.setFocusable(false);
        textInput.setFocusableInTouchMode(false);


        return view;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            persistInt(mNumberPicker.getValue());
        }
    }

}
