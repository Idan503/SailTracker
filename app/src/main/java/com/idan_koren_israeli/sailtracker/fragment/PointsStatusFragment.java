package com.idan_koren_israeli.sailtracker.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.api.Distribution;
import com.idan_koren_israeli.sailtracker.R;
import com.idan_koren_israeli.sailtracker.club.ClubMember;
import com.idan_koren_israeli.sailtracker.common.CommonUtils;
import com.idan_koren_israeli.sailtracker.firebase.MemberDataManager;

import org.joda.time.LocalDate;

import java.util.Locale;


public class PointsStatusFragment extends Fragment {


    private MaterialButton addButton;
    private TextView pointsCountText;
    private int pointsToPurchase = 0;

    private static final int[] BUYING_OPTIONS = new int[]{5,10,20,50};

    public PointsStatusFragment() {
        // Required empty public constructor
    }

    public static PointsStatusFragment newInstance(String param1, String param2) {
        PointsStatusFragment fragment = new PointsStatusFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parent =  inflater.inflate(R.layout.fragment_points_status, container, false);
        findViews(parent);

        // by default view will start as the current member
        ClubMember current = MemberDataManager.getInstance().getCurrentUser();
        if(current!=null)
            setMember(current);
        return parent;
    }

    public void setMember(ClubMember member){
        setPurchaseListener(member);
        updateCount(member.getPointsCount());
    }

    public void updateCount(int count){
        this.pointsCountText.setText(String.valueOf(count));
    }

    private void findViews(View parent){
        pointsCountText = parent.findViewById(R.id.points_status_LBL_points_count);
        addButton = parent.findViewById(R.id.points_status_BTN_add);
    }

    private void setPurchaseListener(final ClubMember member){
        addButton.setOnClickListener(onAddPressed);
    }

    private View.OnClickListener onAddPressed = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RadioGroup radioGroup = generatePurchaseOptions(view.getContext());
            showPurchaseDialog(view.getContext(), radioGroup);

        }
    };

    private void showPurchaseDialog(Context context, final RadioGroup options){
        final ClubMember currentMember = MemberDataManager.getInstance().getCurrentUser();
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(context);
        dialog.setTitle("Purchase Points");
        dialog.setMessage("How many points would you like to purchase?");
        dialog.setView(options);
        dialog.setNegativeButton("Cancel", null);
        dialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(options.getCheckedRadioButtonId()!=-1){
                    MaterialRadioButton selectedButton = options.findViewById(options.getCheckedRadioButtonId());
                    purchasePoints(currentMember, Integer.parseInt(selectedButton.getText().toString()));
                }

            }
        });


        dialog.show();
    }

    // Runtime generation of radio buttons group based on the BUYING_OPTIONS final array
    private RadioGroup generatePurchaseOptions(Context context){
        final RadioGroup radioGroup = new RadioGroup(context);

        for(int amount : BUYING_OPTIONS){
            final MaterialRadioButton amountOption = new MaterialRadioButton(context);
            amountOption.setText(String.format(Locale.US,"%d",amount));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                    (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMarginStart(10);
            params.setMarginEnd(10);

            amountOption.setLayoutParams(params);

            radioGroup.addView(amountOption);
        }

        radioGroup.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        radioGroup.setGravity(Gravity.CENTER);
        radioGroup.setLayoutParams(lp);
        return radioGroup;
    }


    // Save in db and updates ui
    private void purchasePoints(ClubMember member, int count){
        member.addPoints(count);
        MemberDataManager.getInstance().storeMember(member);
        updateCount(member.getPointsCount());
        CommonUtils.getInstance().showToast(count + " Points were added!");
    }


}