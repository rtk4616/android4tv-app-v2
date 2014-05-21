package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVAlertDialog;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVEditText;
import com.iwedia.gui.components.A4TVSpinner;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;

import java.util.ArrayList;

// import com.iwedia.gui.components.dialogs.CiChooseDialog.TypeOfContents;
/**
 * HBB settings dialog
 * 
 * @author bane
 */
public class CISettingsDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    public static final int TUNER_TYPE = 33330, CI_INFO = 13333,
            OPERATOR_INSTALL = 23333, OPERATOR_REMOVE = 33333,
            SET_CICAM_ID = 66545;
    private A4TVButton ciInfoButton, operatorInstallButton,
            operatorRemoveButton, setCiCamId;
    private A4TVSpinner tunerTypeSpinner;
    public static int TUNER_DVBS = -1, TUNER_DVBT = -1, TUNER_DVBC = -1,
            TUNER_IP = -1;
    public A4TVAlertDialog alertDialog = null;
    private EditText editText;
    private Context ctx;

    public CISettingsDialog(Context context) {
        super(context, checkTheme(context), 0);
        ctx = context;
        // fill lists
        returnArrayListsWithDialogContents(contentList, contentListIDs,
                titleIDs);
        // set content to dialog
        fillDialog();
        // set attributes
        setDialogAttributes();
        init();
    }

    private void init() {
        ciInfoButton = (A4TVButton) findViewById(CI_INFO);
        operatorInstallButton = (A4TVButton) findViewById(OPERATOR_INSTALL);
        operatorRemoveButton = (A4TVButton) findViewById(OPERATOR_REMOVE);
        // tunerTypeSpinner = (A4TVSpinner) findViewById(TUNER_TYPE);
        ciInfoButton.setText(R.string.button_text_view);
        operatorInstallButton.setText(R.string.button_text_begin);
        operatorRemoveButton.setText(R.string.button_text_remove);
        // ZORANA - this should be removed or redifined with Drazic
        /*
         * setCiCamId = (A4TVButton) findViewById(SET_CICAM_ID);
         * setCiCamId.setText(R.string.button_text_set); String tunerType = "";
         * tunerTypeSpinner.setSelectionByString(tunerType); //
         * //////////////////////////// // Check available tuners //
         * //////////////////////////// String[] spinnerOptions =
         * tunerTypeSpinner.getContents(); for (int i = 0; i <
         * spinnerOptions.length; i++) { Log.d("LOADED FROM SPINNER LIST",
         * spinnerOptions[i]); if
         * (spinnerOptions[i].equals(getContext().getResources().getString(
         * R.string.main_menu_content_list_dvb_s))) { TUNER_DVBS = i; } if
         * (spinnerOptions[i].equals(getContext().getResources().getString(
         * R.string.main_menu_content_list_dvb_t))) { TUNER_DVBT = i; } if
         * (spinnerOptions[i].equals(getContext().getResources().getString(
         * R.string.main_menu_content_list_dvb_c))) { TUNER_DVBC = i; } if
         * (spinnerOptions[i].equals(getContext().getResources().getString(
         * R.string.main_menu_content_list_ip))) { TUNER_IP = i; } }
         */
        // init password dialog
        alertDialog = new A4TVAlertDialog(ctx);
        alertDialog.setCancelable(true);
        alertDialog.setNegativeButton(R.string.button_text_cancel,
                new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                    }
                });
        alertDialog.setPositiveButton(R.string.parental_control_ok,
                new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // if (!editText.getText().equals("")) {
                        // ZORANA - temporary under comment
                        // try {
                        // MainActivity.service
                        // .getConditionalAccessControl().setPin(
                        // Integer.parseInt(editText
                        // .getText().toString()));
                        // alertDialog.cancel();
                        // } catch (NumberFormatException e) {
                        // e.printStackTrace();
                        // } catch (RemoteException e) {
                        // e.printStackTrace();
                        // }
                        // }
                    }
                });
        LinearLayout layout = (LinearLayout) ((LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.security_settings_password_view, null);
        editText = (A4TVEditText) layout
                .findViewById(R.id.editTextFirstPassword);
        InputFilter maxLengthFilter = new InputFilter.LengthFilter(100);
        editText.setFilters(new InputFilter[] { maxLengthFilter });
        editText.setInputType(InputType.TYPE_CLASS_NUMBER
                | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        alertDialog.getPositiveButton().setEnabled(false);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    alertDialog.getPositiveButton().setEnabled(false);
                } else {
                    alertDialog.getPositiveButton().setEnabled(true);
                }
            }
        });
        alertDialog.setView(layout);
        // hide second password
        layout.findViewById(R.id.editTextSecondPassword).setVisibility(
                View.GONE);
        editText.setHint("Enter id");
    }

    @Override
    public void show() {
        fillViews();
        super.show();
    }

    /** Fill views with data */
    private void fillViews() {
    }

    @Override
    public void fillDialog() {
        View view = DialogManager.dialogCreator.fillDialogWithContents(
                contentList, contentListIDs, titleIDs, null, this, null);// ,
        // pictureBackgroundID);
        setContentView(view);
    }

    @Override
    public void setDialogAttributes() {
        getWindow().getAttributes().width = MainActivity.dialogWidth;
        getWindow().getAttributes().height = MainActivity.dialogHeight;
    }

    /**
     * Function that load theme
     * 
     * @param ctx
     * @return
     */
    private static int checkTheme(Context ctx) {
        TypedArray atts = ctx.getTheme().obtainStyledAttributes(
                new int[] { R.attr.A4TVDialog });
        int i = atts.getResourceId(0, 0);
        atts.recycle();
        return i;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case CI_INFO: {
                CICamInfoDialog ciDialog = MainActivity.activity
                        .getDialogManager().getCICamInfoDialog();
                if (ciDialog != null) {
                    ciDialog.show();
                }
                Log.d(TAG, "CI Info clicked");
                break;
            }
            case OPERATOR_INSTALL: {
                A4TVToast toast = new A4TVToast(getContext());
                toast.showToast(R.string.not_implemented);
                /** Start scan procedure */
                // ZORANA - temporary under comment
                /*
                 * try { int tunerType = -1; if
                 * (tunerTypeSpinner.getCHOOSEN_ITEM_INDEX() == TUNER_DVBT) {
                 * tunerType = ScanSignalType.SIGNAL_TYPE_TERRESTRIAL; } else if
                 * (tunerTypeSpinner.getCHOOSEN_ITEM_INDEX() == TUNER_DVBS) {
                 * tunerType = ScanSignalType.SIGNAL_TYPE_SATTELITE; } else if
                 * (tunerTypeSpinner.getCHOOSEN_ITEM_INDEX() == TUNER_DVBC) {
                 * tunerType = ScanSignalType.SIGNAL_TYPE_CABLE; } else if
                 * (tunerTypeSpinner.getCHOOSEN_ITEM_INDEX() == TUNER_IP) {
                 * tunerType = ScanSignalType.SIGNAL_TYPE_IP; }
                 * ChannelScanDialog.isScanning = MainActivity.service
                 * .getConditionalAccessControl().installOperatorProfile(
                 * tunerType, 0); } catch (Exception e) { e.printStackTrace(); }
                 */
                break;
            }
            case OPERATOR_REMOVE: {
                // ZORANA - temporary under comment
                /*
                 * MainActivity.activity.getDialogManager().getCiChooseDialog()
                 * .show(TypeOfContents.REMOVE);
                 */
                A4TVToast toast = new A4TVToast(getContext());
                toast.showToast(R.string.not_implemented);
                break;
            }
            case SET_CICAM_ID: {
                editText.setText("");
                alertDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        editText.requestFocus();
                    }
                }, 150);
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void returnArrayListsWithDialogContents(
            ArrayList<ArrayList<Integer>> contentList,
            ArrayList<ArrayList<Integer>> contentListIDs,
            ArrayList<Integer> titleIDs) {
        // clear old data in lists
        contentList.clear();
        contentListIDs.clear();
        titleIDs.clear();
        // title
        titleIDs.add(R.drawable.tv_menu_icon);
        titleIDs.add(R.drawable.ci_icon);
        titleIDs.add(R.string.tv_settings_menu_ci_settings);
        ArrayList<Integer> list;
        // ZORANA - temp removed
        // Tuner type******************************************
        // list.add(MainMenuContent.TAGA4TVTextView);
        // list.add(MainMenuContent.TAGA4TVSpinner);
        // contentList.add(list);
        //
        // list = new ArrayList<Integer>();
        // list.add(R.string.tv_menu_channel_installation_settings_tuner_type);
        // list.add(TUNER_TYPE);
        // contentListIDs.add(list);
        // CI info ******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_channel_installation_settings_ci_info);
        list.add(CI_INFO);
        contentListIDs.add(list);
        // Operator install******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_channel_installation_settings_operator_tunning);
        list.add(OPERATOR_INSTALL);
        contentListIDs.add(list);
        // Operator profile remove ******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_channel_installation_settings_ci_remove);
        list.add(OPERATOR_REMOVE);
        contentListIDs.add(list);
        // Set cicam id ******************************************
        // ZORANA - temporary removed
        /*
         * list = new ArrayList<Integer>();
         * list.add(MainMenuContent.TAGA4TVTextView);
         * list.add(MainMenuContent.TAGA4TVButton); contentList.add(list); list
         * = new ArrayList<Integer>();
         * list.add(R.string.tv_menu_channel_installation_settings_set_cicam_id
         * ); list.add(SET_CICAM_ID); contentListIDs.add(list);
         */
    }
}
