package ca.uoit.crobot.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import ca.uoit.crobot.R;
import io.github.controlwear.virtual.joystick.android.JoystickView;


public class RCFragment extends Fragment {
    private static final String TAG = RCFragment.class.getSimpleName();

    private OnRCFragmentInteractionListener mListener;
    private MotionHelper motionHelper = new MotionHelper();

    public RCFragment() {

        // Background thread for managing movements
        new Thread(() -> {
            while(true) {
                if(mListener != null) {
                    switch (motionHelper.direction) {
                        case 1:
                            Log.i(TAG, "Moving left");
                            mListener.onLeft();
                            break;
                        case 2:
                            Log.i(TAG, "Moving right");
                            mListener.onRight();
                            break;
                        case 3:
                            Log.i(TAG, "Moving forward");
                            mListener.onForward();
                            break;
                        case 4:
                            Log.i(TAG, "Moving backward");
                            mListener.onBackward();
                            break;
                    }
                }

                try {
                    //TODO: Increase sleep time if too aggressive
                    Thread.sleep(32);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_rc, container, false);

        view.findViewById(R.id.leftButton).setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.i(TAG, "Left down");
                motionHelper.direction = 1;
                return true;
            }

            if(event.getAction() == MotionEvent.ACTION_UP) {
                Log.i(TAG, "Left up");
                motionHelper.direction = 0;
                return true;
            }

            return false;
        });

        view.findViewById(R.id.rightButton).setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.i(TAG, "Right down");
                motionHelper.direction = 2;
                return true;
            }

            if(event.getAction() == MotionEvent.ACTION_UP) {
                Log.i(TAG, "Right up");
                motionHelper.direction = 0;
                return true;
            }

            return false;
        });

        view.findViewById(R.id.upButton).setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.i(TAG, "Forward down");
                motionHelper.direction = 3;
                return true;
            }

            if(event.getAction() == MotionEvent.ACTION_UP) {
                Log.i(TAG, "Forward up");
                motionHelper.direction = 0;
                return true;
            }

            return false;
        });

        view.findViewById(R.id.downButton).setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.i(TAG, "Backward down");
                motionHelper.direction = 4;
                return true;
            }

            if(event.getAction() == MotionEvent.ACTION_UP) {
                Log.i(TAG, "Backward up");
                motionHelper.direction = 0;
                return true;
            }

            return false;
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRCFragmentInteractionListener) {
            mListener = (OnRCFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnRCFragmentInteractionListener {

        void onLeft();

        void onRight();

        void onForward();

        void onBackward();
    }

    class MotionHelper {
        // 0 = not moving, 1 = left, 2 = right, 3 = forward, 4 = backward
        int direction;
    }
}
