package com.ipoki.android;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.preference.Preference.BaseSavedState;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

public class LoginPreference extends Preference {
	public LoginPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setWidgetLayoutResource(R.layout.preference_login);
	}

	private String mData;

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        
        // Set our custom views inside the layout
        final EditText myEditText = (EditText) view.findViewById(R.id.username);
        if (myEditText != null) {
            myEditText.setText(mData);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        // This preference type's value type is Integer, so we read the default
        // value from the attributes as an Integer.
    	return a.getString(index);
    }
    
    @Override
    protected Parcelable onSaveInstanceState() {
        /*
         * Suppose a client uses this preference type without persisting. We
         * must save the instance state so it is able to, for example, survive
         * orientation changes.
         */
        
        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            // No need to save instance state since it's persistent
            return superState;
        }

        // Save the instance state
        final SavedState myState = new SavedState(superState);
        myState.data = mData;
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!state.getClass().equals(SavedState.class)) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state);
            return;
        }
     
        // Restore the instance state
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        mData = myState.data;
        notifyChanged();
    }
    
    /**
     * SavedState, a subclass of {@link BaseSavedState}, will store the state
     * of MyPreference, a subclass of Preference.
     * <p>
     * It is important to always call through to super methods.
     */
    private static class SavedState extends BaseSavedState {
        String data;
        
        public SavedState(Parcel source) {
            super(source);
            
            // Restore the click counter
            data = source.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            
            // Save the click counter
            dest.writeString(data);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
