package de.exestrial.colorizer.mailcalendarcolor.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import de.exestrial.colorizer.mailcalendarcolor.Account;
import de.exestrial.colorizer.mailcalendarcolor.R;

/**
 * Created by Philipp on 17.04.2016.
 */
public class PickColorDialog extends CustomViewDialogFragment {

    public static final String COLOR_INDEX = "cIndex";
    public static final String RGB = "color";

    private int colorIndex = -1;

    private GridView gridView;
    private ColorAdapter adapter;
    private Button positiveButton;


    public static PickColorDialog newInstance(Context context){
        return PickColorDialog.newInstance(context, null, null);
    }
    public static PickColorDialog newInstance(Context context, Bundle extras, Account.Color preset){
        PickColorDialog f = new PickColorDialog();
        Bundle args = new Bundle();
        args.putString(TITLE, context.getString(R.string.pick_color));
        //args.putString(MESSAGE, "Please enter something:");
        args.putString(POSITIVE_BUTTON_TEXT, context.getString(R.string.apply));
        //args.putString(NEGATIVE_BUTTON_TEXT, context.getString(R.string.unmute));
        args.putString(NEUTRAL_BUTTON_TEXT, context.getString(R.string.cancel));
        args.putBoolean(CANCELABLE, true);
        args.putBundle(BUNDLE, extras);
        if (preset != null){
            for (int i = 0; i < Account.COLORS.length; i++) {
                if (Account.COLORS[i].rgb == preset.rgb) {
                    args.putInt(COLOR_INDEX, i);
                    break;
                }
            }
        }
        f.setArguments(args);
        return f;
    }


    @Override
    public View onCreateContentView(Bundle savedInstanceState, final AlertDialog dialog) {
        // inflate and set your custom view
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_pick_color, null);
        gridView = (GridView) view.findViewById(R.id.gridView);

        if (savedInstanceState != null){
            colorIndex = savedInstanceState.getInt(COLOR_INDEX, -1);
        } else {
            colorIndex = getArguments().getInt(COLOR_INDEX, colorIndex);
        }

        adapter = new ColorAdapter(dialog.getLayoutInflater());
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                colorIndex = position;
                adapter.notifyDataSetChanged();
                positiveButton.setEnabled(true);
                positiveButton.setTextColor(getResources().getColor(R.color.colorAccent));
            }
        });



        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

                if (colorIndex < 0) {
                    positiveButton.setEnabled(false);
                    positiveButton.setTextColor(getResources().getColor(android.R.color.darker_gray));
                }
            }
        });


        return view;
    }


    @Override
    public Bundle onResult(int which) {
        Bundle result = new Bundle();
        if (colorIndex >= 0) {
            result.putInt(RGB, Account.COLORS[colorIndex].rgb);
        }

        return result;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(COLOR_INDEX, colorIndex);
    }


    class ColorAdapter extends BaseAdapter{

        LayoutInflater inflater;

        ColorAdapter(LayoutInflater inflater){
            this.inflater = inflater;
        }

        @Override
        public int getCount() {
            return Account.COLORS.length;
        }

        @Override
        public Object getItem(int position) {
            return Account.COLORS[position].rgb;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = inflater.inflate(R.layout.grid_item, null);
            }

            ImageView view = (ImageView) convertView.findViewById(R.id.color);
            view.setBackgroundColor(0xFF000000 + (Integer) getItem(position));
            if (position == colorIndex){
                view.setImageResource(R.drawable.ic_check);
            } else {
                view.setImageBitmap(null);
            }


            return convertView;
        }
    }

}
