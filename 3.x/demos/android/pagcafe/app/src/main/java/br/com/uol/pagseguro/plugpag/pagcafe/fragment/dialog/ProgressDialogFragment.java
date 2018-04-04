package br.com.uol.pagseguro.plugpag.pagcafe.fragment.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import br.com.uol.pagseguro.plugpag.pagcafe.OnAbortListener;
import br.com.uol.pagseguro.plugpag.pagcafe.fragmentinteraction.FragmentInteraction;
import br.com.uol.pagseguro.plugpag.pagcafe.fragmentinteraction.OnFragmentInteractionListener;
import br.com.uol.pagseguro.plugpag.pagcafe.R;
import br.com.uol.pagseguro.plugpag.pagcafe.exception.InvalidContextTypeException;
import br.com.uol.pagseguro.plugpag.pagcafe.plugpag.PlugPagManager;


public class ProgressDialogFragment
        extends DialogFragment
        implements View.OnClickListener {

    // ---------------------------------------------------------------------------------------------
    // Instance attributes
    // ---------------------------------------------------------------------------------------------

    private Toolbar mToolbar = null;
    private ProgressBar mPgbAnimation = null;
    private TextView mTxtMessage = null;
    private Button mBtnAbort = null;

    private OnFragmentInteractionListener mListener = null;
    private OnAbortListener mAbortListener = null;

    // ---------------------------------------------------------------------------------------------
    // Lifecycle
    // ---------------------------------------------------------------------------------------------

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;

        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_progress_dialog, container, false);

        this.getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.setupViews(view);
        this.parseArgs(this.getArguments());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (!(context instanceof OnFragmentInteractionListener)) {
            throw new InvalidContextTypeException("Context isn't of type OnFragmentInteractionListener");
        }

        this.mListener = (OnFragmentInteractionListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mListener = null;
    }

    // ---------------------------------------------------------------------------------------------
    // Setup Views
    // ---------------------------------------------------------------------------------------------

    /**
     * Setups Views.
     *
     * @param view Root View.
     */
    private void setupViews(@NonNull View view) {
        this.mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        this.mPgbAnimation = (ProgressBar) view.findViewById(R.id.pgb_animation);
        this.mTxtMessage = (TextView) view.findViewById(R.id.txt_message);
        this.mBtnAbort = (Button) view.findViewById(R.id.btn_abort);

        this.mBtnAbort.setOnClickListener(this);
    }

    // ---------------------------------------------------------------------------------------------
    // Arguments parse
    // ---------------------------------------------------------------------------------------------

    /**
     * Parses the arguments passed to the DialogFragment.
     *
     * @param args Arguments to be parsed.
     */
    private void parseArgs(@Nullable Bundle args) {
        if (args != null) {
            // Title
            if (TextUtils.isEmpty(args.getString(FragmentInteraction.KEY_DIALOG_TITLE))) {
                this.mToolbar.setVisibility(View.GONE);
            } else {
                this.mToolbar.setVisibility(View.VISIBLE);
                this.mToolbar.setTitle(args.getString(FragmentInteraction.KEY_DIALOG_TITLE));
            }

            // Dismissable
            this.setCancelable(args.getBoolean(FragmentInteraction.KEY_DIALOG_DISMISSABLE, true));

            // Message
            if (args.containsKey(FragmentInteraction.KEY_DIALOG_MESSAGE) &&
                    !TextUtils.isEmpty(args.getString(FragmentInteraction.KEY_DIALOG_MESSAGE))) {
                this.mTxtMessage.setVisibility(View.VISIBLE);
                this.mTxtMessage.setText(args.getString(FragmentInteraction.KEY_DIALOG_MESSAGE));
            }

            if (args.containsKey(FragmentInteraction.KEY_DIALOG_ABORT_LISTENER) &&
                    args.getSerializable(FragmentInteraction.KEY_DIALOG_ABORT_LISTENER) != null) {
                this.mBtnAbort.setVisibility(View.VISIBLE);
                this.mAbortListener =
                        (OnAbortListener) args.getSerializable(
                                FragmentInteraction.KEY_DIALOG_ABORT_LISTENER);
            } else {
                this.mBtnAbort.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Updates the DialogFragment's arguments.
     * Just the DialogFragment's content is changed. The original arguments are still set in memory.
     *
     * @param args New arguments used to refresh the Views.
     */
    public void updateArguments(@NonNull Bundle args) {
        this.parseArgs(args);
    }

    // ---------------------------------------------------------------------------------------------
    // Title change
    // ---------------------------------------------------------------------------------------------

    /**
     * Changes the title being displayed.
     * If the given title is null or empty, hides the Toolbar.
     *
     * @param title Title to be displayed.
     */
    public void setTitle(@Nullable String title) {
        if (!TextUtils.isEmpty(title)) {
            this.mToolbar.setTitle(title);
        } else {
            this.mToolbar.setVisibility(View.GONE);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Message change
    // ---------------------------------------------------------------------------------------------

    /**
     * Changes the text being displayed.
     *
     * @param message Message to be displayed.
     */
    public void updateMessage(@Nullable String message) {
        if (!TextUtils.isEmpty(message)) {
            this.mTxtMessage.setText(message);
        } else {
            this.mTxtMessage.setText("");
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Abort button click handling
    // ---------------------------------------------------------------------------------------------

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_abort && this.mAbortListener != null) {
            this.mBtnAbort.setEnabled(false);
            this.mBtnAbort.setText(R.string.button_aborting);
            this.mAbortListener.onAbort(PlugPagManager.getInstance().getPlugPag());
        }
    }

}
