package com.example.ble_test_v13_0;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ServicesExpandableListAdapter extends BaseExpandableListAdapter {
    private final Context context;
    private final ArrayList<ServiceModel> groupArrayList;
    private final ArrayList<ArrayList<CharacteristicsModel>> childArrayList;
    private final LVChildItemReadCharacteristicOnClickListener readCharacteristicOnClickListener;
    private String editableValue;

    // View lookup cache (view holders for parents and corresponding children for each parent)
    private static class ViewHolderParent {
        TextView ServiceNameTextListView;
        TextView ServiceUuidListTextView;
    }

    // expandable items (children)
    private static class ViewHolderChild {
        TextView CharUuidExpandedView;
        TextView CharNameExpandedView;
        Button CharReadExpandedView;
        EditText CharValueExpandedView;
        RadioGroup radioGroupReadWriteNotify;
        RadioButton radioButtonReadAccess;
        RadioButton radioButtonWriteAccess;
        RadioButton radioButtonNotifyAccess;
    }

    public ServicesExpandableListAdapter(Context context, ArrayList<ServiceModel> groupArrayList,
                                         ArrayList<ArrayList<CharacteristicsModel>> childArrayList,
                                         LVChildItemReadCharacteristicOnClickListener readCharacteristicOnClickListener) {
        this.context = context;
        this.groupArrayList = groupArrayList;
        this.childArrayList = childArrayList;
        this.readCharacteristicOnClickListener = readCharacteristicOnClickListener;
        editableValue = "";
    }
    @Override
    public int getGroupCount() {
        return this.groupArrayList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (this.childArrayList.get(groupPosition) != null){
            return (this.childArrayList.get(groupPosition).size());
        }
        else {
            return 0;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.groupArrayList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.childArrayList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        ServiceModel service = (ServiceModel) getGroup(groupPosition);
        String service_name = service.getServiceName();
        String service_uuid = service.getServiceUUID();

        View rowView;
        ViewHolderParent viewHolder; // view lookup cache stored in tag

        if (convertView == null) {
            viewHolder = new ViewHolderParent();

            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            rowView = layoutInflater.inflate(R.layout.list_service_item_view, null);

            viewHolder.ServiceNameTextListView = rowView.
                                                    findViewById(R.id.service_group_name);

            viewHolder.ServiceUuidListTextView = rowView.
                                                    findViewById(R.id.service_group_id);

            rowView.setTag(viewHolder); // Cache the viewHolder object inside the fresh view
        }
        else {
            rowView = convertView;
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolderParent) rowView.getTag();
        }

        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.ServiceNameTextListView.setText(service_name);
        viewHolder.ServiceUuidListTextView.setText(service_uuid);

        return rowView;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {

        CharacteristicsModel characteristic =
                (CharacteristicsModel)getChild(groupPosition, childPosition);

        View rowView;
        ViewHolderChild viewHolderChild;

        if (convertView == null) {
            viewHolderChild = new ViewHolderChild();

            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = layoutInflater.inflate(R.layout.list_characteristic_item_view, null);

            viewHolderChild.CharUuidExpandedView = rowView.
                    findViewById(R.id.expanded_characteristic_uuid);

            viewHolderChild.CharNameExpandedView = rowView.
                    findViewById(R.id.expanded_characteristic_name);

            viewHolderChild.radioGroupReadWriteNotify = rowView.
                    findViewById(R.id.radioGroupReadWriteNotify);

            viewHolderChild.radioButtonReadAccess = rowView.findViewById(R.id.radioButtonRead);
            viewHolderChild.radioButtonWriteAccess = rowView.findViewById(R.id.radioButtonWrite);
            viewHolderChild.radioButtonNotifyAccess = rowView.findViewById(R.id.radioButtonNotification);

            viewHolderChild.CharReadExpandedView = rowView.
                    findViewById(R.id.read_characteristics_value);

            viewHolderChild.CharValueExpandedView = rowView.
                    findViewById(R.id.characteristic_value);

            rowView.setTag(viewHolderChild); // Cache the viewHolder object inside the fresh view
        }
        else {
            rowView = convertView;
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolderChild = (ViewHolderChild) rowView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.

        if (!characteristic.getReadAccess()){
            viewHolderChild.radioButtonReadAccess.setVisibility(View.INVISIBLE);
        }
        else{ viewHolderChild.radioButtonReadAccess.setVisibility(View.VISIBLE);}

        if (!characteristic.getWriteAccess()){
            viewHolderChild.radioButtonWriteAccess.setVisibility(View.INVISIBLE);
        }
        else{ viewHolderChild.radioButtonWriteAccess.setVisibility(View.VISIBLE);}

        if (!characteristic.getNotificationAccess()){
            viewHolderChild.radioButtonNotifyAccess.setVisibility(View.INVISIBLE);
        }
        else{ viewHolderChild.radioButtonNotifyAccess.setVisibility(View.VISIBLE);}

        viewHolderChild.CharReadExpandedView.setOnClickListener(
                v -> readCharacteristicOnClickListener.onClick(groupPosition,
                        childPosition,
                        characteristic.getReadChecked(),
                        characteristic.getWriteChecked(),
                        editableValue
                ));

        viewHolderChild.radioButtonReadAccess.setOnClickListener(v -> {
            characteristic.setReadChecked(true);
            characteristic.setWriteChecked(false);
            characteristic.setNotificationChecked(false);
        });

        viewHolderChild.radioButtonWriteAccess.setOnClickListener(v -> {
            characteristic.setReadChecked(false);
            characteristic.setWriteChecked(true);
            characteristic.setNotificationChecked(false);
        });

        viewHolderChild.radioButtonNotifyAccess.setOnClickListener(v -> {
            characteristic.setReadChecked(false);
            characteristic.setWriteChecked(false);
            characteristic.setNotificationChecked(true);
        });

        viewHolderChild.CharValueExpandedView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                editableValue = String.valueOf(s);
            }
        });
/*
        viewHolderChild.CharValueExpandedView.setOnKeyListener((v, keyCode, event) -> {
            if ( !viewHolderChild.radioButtonWriteAccess.isChecked() ||
                    viewHolderChild.radioButtonWriteAccess.getVisibility()==View.INVISIBLE){
                return false;
            }

            if ((event.getAction() == KeyEvent.ACTION_DOWN)) {
            }
            return false;
        });
*/
        viewHolderChild.CharUuidExpandedView.setText(characteristic.getCharacteristicsUUID());

        viewHolderChild.CharNameExpandedView.setText(characteristic.getCharacteristicsName());

        viewHolderChild.CharValueExpandedView.setText(characteristic.getCharacteristicsValue());

        if (characteristic.getReadChecked() &&
            !viewHolderChild.radioButtonReadAccess.isChecked())
        {
            viewHolderChild.radioButtonReadAccess.setChecked(true);
        }

        if (characteristic.getWriteChecked() &&
            !viewHolderChild.radioButtonWriteAccess.isChecked())
        {
            viewHolderChild.radioButtonWriteAccess.setChecked(true);
        }

        if (characteristic.getNotificationChecked() &&
            !viewHolderChild.radioButtonNotifyAccess.isChecked())
        {
            viewHolderChild.radioButtonNotifyAccess.setChecked(true);
        }

        return rowView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
