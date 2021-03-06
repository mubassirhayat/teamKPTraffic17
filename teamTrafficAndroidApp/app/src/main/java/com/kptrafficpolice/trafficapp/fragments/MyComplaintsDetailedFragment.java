package com.kptrafficpolice.trafficapp.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.kptrafficpolice.trafficapp.R;
import com.kptrafficpolice.trafficapp.utilities.Configuration;

import cn.pedant.SweetAlert.SweetAlertDialog;
//raabta
//rabta
/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyComplaintsDetailedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyComplaintsDetailedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyComplaintsDetailedFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ImageView ivComplaintImage, ivMapPointer;
    VideoView vvMyComplaints;
    TextView tvComplaintType, tvComplaintDescription, tvComplaintDate, tvComplaintStatus;
    SweetAlertDialog pDialog;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private FirebaseAnalytics mFirebaseAnalytics;

    public MyComplaintsDetailedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyComplaintsDetailedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyComplaintsDetailedFragment newInstance(String param1, String param2) {
        MyComplaintsDetailedFragment fragment = new MyComplaintsDetailedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_complaints_detailed, container, false);
        final Bundle args = getArguments();
        Log.d("zma args detailed", String.valueOf(args.get("description")));
        tvComplaintType = (TextView) view.findViewById(R.id.tv_complaint_type);
        tvComplaintDescription = (TextView) view.findViewById(R.id.tv_complaint_description);
        tvComplaintDate = (TextView) view.findViewById(R.id.tv_complaint_date);
        tvComplaintStatus = (TextView) view.findViewById(R.id.tv_complaint_status);
        ivComplaintImage = (ImageView) view.findViewById(R.id.iv_detail_title_image);
        ivMapPointer = (ImageView) view.findViewById(R.id.iv_detail_marker);
        vvMyComplaints = (VideoView) view.findViewById(R.id.vv_my_complaints);

        customActionBar();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "2");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "My Complaint List");
        mFirebaseAnalytics.logEvent("My_Complaint_screen", bundle);

        pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#179e99"));
        pDialog.setTitleText("Loading Video");
        WindowManager.LayoutParams dialogPosition = pDialog.getWindow().getAttributes();
        dialogPosition.gravity = Gravity.TOP | Gravity.CENTER;
        dialogPosition.y = 120;   //y position
        tvComplaintType.setText(args.getString("complaint_type"));
        tvComplaintDescription.setText(args.getString("description"));
        tvComplaintStatus.setText(args.getString("status"));
        tvComplaintDate.setText(args.getString("date"));
        //ivComplaintImage.setImageURI(Uri.parse(args.getString("image")));
        vvMyComplaints.setVisibility(View.GONE);
        ivComplaintImage.setVisibility(View.GONE);
        if (!args.getString("video").equals("") || !args.getString("image").equals("")) {
            if (!args.getString("video").equals("")) {
                pDialog.show();
                vvMyComplaints.setVisibility(View.VISIBLE);
                try {
                    // Start the MediaController
                    MediaController mediacontroller = new MediaController(
                            getActivity());
                    mediacontroller.setAnchorView(vvMyComplaints);
                    // Get the URL from String VideoURL
                    Uri video = Uri.parse("http://103.240.220.76/kptraffic/uploads/videos/" + args.getString("video"));
                    Log.d("zma video uri",String.valueOf(video));
                    vvMyComplaints.setMediaController(mediacontroller);
                    vvMyComplaints.setVideoURI(video);

                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }

                vvMyComplaints.requestFocus();
                vvMyComplaints.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    // Close the progress bar and play the video
                    public void onPrepared(MediaPlayer mp) {
                         pDialog.dismiss();
                    }
                });

            } else if (!args.getString("image").equals("")) {
                ivComplaintImage.setVisibility(View.VISIBLE);
                Glide.with(getActivity()).load(Configuration.END_POINT_LIVE+"uploads/images/" + args.getString("image")).into(ivComplaintImage);
            }
        }

//        String full_url = "http://103.240.220.76/kptraffic/uploads/images/" + args.getString("image");
//        Log.d("zma image adapter",full_url);

        ivMapPointer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String link = "http://maps.google.com/?q=" +
                        String.valueOf(args.getString("lat")) + "," + String.valueOf(args.getString("lon"));
                Log.d("zma url complaint", link);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                getActivity().startActivity(intent);
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void customActionBar() {
        android.support.v7.app.ActionBar mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View mCustomView = mInflater.inflate(R.layout.custom_action_bar, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);
        mTitleTextView.setText("Complaint Detail");
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
