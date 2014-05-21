package com.iwedia.gui.config_handler;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.iwedia.gui.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Generate configuration file activity
 * 
 * @author Veljko Ilkic
 */
public class ConfigGeneratorActivity extends Activity {
    // TV_FEATURES:YES/NO
    // #
    // DVBT:YES/NO
    // #
    // DVBS:YES/NO
    // #
    // DVBC:YES/NO
    // #
    // IP:YES/NO
    // #
    // ATV:YES/NO
    // #
    // ATSC:YES/NO
    // #
    // DTMB:YES/NO
    // #
    // SAT2IP:YES/NO
    // #
    // DLNA:YES/NO
    // #
    // DLNA_DMS: YES/NO
    // #
    // APP_SETTINGS: YES/NO
    // #
    // HBB:YES/NO
    // #
    // MHEG:YES/NO
    // #
    // CI:YES/NO
    // #
    // COMPLEX_AUDIO:YES/NO
    // #
    // TIMESHIFT:YES/NO
    // #
    // PVR: YES/NO
    // #
    // PVR_STORAGE(nand/usb): NAND/USB
    // #
    // PVR_THRESHOLD(1-100): 1-100
    // #
    // SEEK_OFFSET: 1-10
    // #
    // USE_LCN:YES/NO
    private static String TAG = "ConfigGeneratorActivity";
    private String mDefaultPath = "/";
    private String[] mAvailableOptions = { "TV_FEATURES", "DVBT", "DVBS",
            "DVBC", "IP", "ATV", "ATSC", "DTMB", "SAT2IP", "DLNA", "DLNA_DMS",
            "APP_SETTINGS", "HBB", "MHEG", "CI", "COMPLEX_AUDIO", "TIMESHIFT",
            "PVR", "USE_LCN", "TVPLATFORM", "CURL_GRAPHIC_QUALITY",
            "PVR_STORAGE(nand/usb)", "PVR_THRESHOLD(1-100)", "SEEK_OFFSET" };
    private boolean[] mSTBDVBOptions = { false, true, false, false, false,
            false, false, false, false, true, false, true, true, false, false,
            false, true, true, false, false, false };
    private boolean[] mTVDVBOptions = { true, true, true, true, false, true,
            false, false, false, true, false, true, true, false, true, true,
            true, true, true, true, true };
    private boolean[] mTVATSCOptions = { true, true, false, true, false, true,
            true, false, false, false, false, true, false, false, false, false,
            true, true, true, true, true };
    private boolean[] mTVDTMBOptions = { true, true, true, true, false, true,
            false, false, false, true, false, true, true, false, true, true,
            true, true, true, true, true };
    private String mPvrStorageStringValue = "nand";
    private String mPvrTresholdStringValue = "90";
    private String mSeekOffsetStringValue = "2";
    private Boolean[] mCheckedOptions = new Boolean[mAvailableOptions.length];
    // //////////////////////////
    // Views
    // //////////////////////////
    private ListView mOptionsList;
    private EditText mFilePathEditText;
    private OptionListAdapter mOptionListAdapter;
    private LayoutInflater mLayoutInflater;
    private Dialog mOptionDialog;
    private SeekBar mTresholdSeekBar;
    private TextView mTresholdValue;
    private RadioButton mRadioButtonNand, mRadioButtonUsb;
    private SeekBar mSeekOffsetSeekBar;
    private TextView mSeekOffsetValue;
    private static ArrayList<Integer> sEnteredKeys = new ArrayList<Integer>(5);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_generator_activity_main);
        // Init Default Path
        ApplicationInfo lApplicationInfo = null;
        try {
            lApplicationInfo = getPackageManager().getApplicationInfo(
                    getPackageName(), PackageManager.GET_META_DATA);
            mDefaultPath = lApplicationInfo.dataDir;
        } catch (Exception e) {
        }
        // Init checked options array
        for (int i = 0; i < mCheckedOptions.length; i++) {
            mCheckedOptions[i] = false;
        }
        // Take references
        mOptionsList = (ListView) findViewById(R.id.optionsList);
        mOptionListAdapter = new OptionListAdapter();
        mOptionsList.setAdapter(mOptionListAdapter);
        mOptionsList.setOnItemClickListener(new OptionListOnItemClick());
        mFilePathEditText = (EditText) findViewById(R.id.filePathGeneration);
        mFilePathEditText.setText(mDefaultPath);
        // Create inflater
        mLayoutInflater = getLayoutInflater();
    }

    /** Create option dialog */
    private void createAndShowDialog(int typeOfDialog) {
        // ////////////////////////////////////
        // PVR STORAGE DIALOG
        // ////////////////////////////////////
        if (0 == typeOfDialog) {
            mOptionDialog = new Dialog(ConfigGeneratorActivity.this,
                    android.R.style.Theme_Holo_Dialog_NoActionBar_MinWidth);
            mOptionDialog
                    .setContentView(R.layout.config_generator_pvr_storage_dialog);
            mRadioButtonNand = (RadioButton) mOptionDialog
                    .findViewById(R.id.radiobutton1);
            mRadioButtonUsb = (RadioButton) mOptionDialog
                    .findViewById(R.id.radiobutton2);
            mOptionDialog.setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if (mRadioButtonUsb.isChecked()) {
                        mPvrStorageStringValue = "usb";
                    } else {
                        mPvrStorageStringValue = "nand";
                    }
                    mOptionListAdapter.notifyDataSetChanged();
                }
            });
            mOptionDialog.show();
        } else
        // ////////////////////////////////////
        // PVR THRESHOLD DIALOG
        // ////////////////////////////////////
        if (1 == typeOfDialog) {
            mOptionDialog = new Dialog(ConfigGeneratorActivity.this,
                    android.R.style.Theme_Holo_Dialog_NoActionBar_MinWidth);
            mOptionDialog
                    .setContentView(R.layout.config_generator_pvr_treshold_dialog);
            mOptionDialog.setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    mPvrTresholdStringValue = mTresholdSeekBar.getProgress()
                            + "";
                    mOptionListAdapter.notifyDataSetChanged();
                }
            });
            mTresholdSeekBar = (SeekBar) mOptionDialog
                    .findViewById(R.id.seekBar);
            mTresholdSeekBar.setMax(100);
            mTresholdSeekBar.setProgress(90);
            mTresholdValue = (TextView) mOptionDialog
                    .findViewById(R.id.seekValue);
            mTresholdValue.setText("90%");
            mTresholdSeekBar
                    .setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                        }

                        @Override
                        public void onProgressChanged(SeekBar seekBar,
                                int progress, boolean fromUser) {
                            mTresholdValue.setText(progress + "");
                        }
                    });
            mOptionDialog.show();
        }
        // //////////////////////////////////////////
        // SEEK OFFSET DIALOG
        // //////////////////////////////////////////
        else {
            mOptionDialog = new Dialog(ConfigGeneratorActivity.this,
                    android.R.style.Theme_Holo_Dialog_NoActionBar_MinWidth);
            mOptionDialog
                    .setContentView(R.layout.config_generator_pvr_treshold_dialog);
            mOptionDialog.setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    mSeekOffsetStringValue = mSeekOffsetSeekBar.getProgress()
                            + "";
                    mOptionListAdapter.notifyDataSetChanged();
                }
            });
            mSeekOffsetSeekBar = (SeekBar) mOptionDialog
                    .findViewById(R.id.seekBar);
            mSeekOffsetSeekBar.setMax(10);
            mSeekOffsetSeekBar.setProgress(2);
            mSeekOffsetValue = (TextView) mOptionDialog
                    .findViewById(R.id.seekValue);
            mSeekOffsetValue.setText("2");
            mSeekOffsetSeekBar
                    .setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                        }

                        @Override
                        public void onProgressChanged(SeekBar seekBar,
                                int progress, boolean fromUser) {
                            mSeekOffsetValue.setText(progress + "");
                        }
                    });
            mOptionDialog.show();
        }
    }

    /** Generate config file */
    private void generateConfigFile() {
        String configFileContent = "";
        // Generate text
        for (int i = 0; i < mAvailableOptions.length; i++) {
            if (0 == i) {
                configFileContent = configFileContent + mAvailableOptions[i]
                        + ":" + checkStatus(mCheckedOptions[i]) + "\n";
            } else
            // PVR STORAGE
            if (i == mAvailableOptions.length - 3) {
                configFileContent = configFileContent + "#\n"
                        + mAvailableOptions[i] + ":" + mPvrStorageStringValue
                        + "\n";
            } else
            // PVR THRESHOLD
            if (i == mAvailableOptions.length - 2) {
                configFileContent = configFileContent + "#\n"
                        + mAvailableOptions[i] + ":" + mPvrTresholdStringValue
                        + "\n";
            } else
            // SEEK OFFSET
            if (i == mAvailableOptions.length - 1) {
                configFileContent = configFileContent + "#\n"
                        + mAvailableOptions[i] + ":" + mSeekOffsetStringValue
                        + "\n";
            }
            // OTHERS
            else {
                configFileContent = configFileContent + "#\n"
                        + mAvailableOptions[i] + ":"
                        + checkStatus(mCheckedOptions[i]) + "\n";
            }
        }
        String filePath = getApplicationInfo().dataDir + "/"
                + "a4tv2.0_config.txt";
        try {
            File myFile = new File(filePath);
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(configFileContent);
            myOutWriter.close();
            fOut.close();
            Toast.makeText(getApplicationContext(),
                    "Done writing 'a4tv2.0_config.txt'", Toast.LENGTH_SHORT)
                    .show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(),
                    Toast.LENGTH_SHORT).show();
        }
        try {
            Runtime.getRuntime().exec(
                    "adb  push " + filePath + " "
                            + mFilePathEditText.getText().toString().trim());
        } catch (IOException e) {
        }
    }

    /**
     * Generates String (yes, no) from boolean state
     * 
     * @param status
     *        Value to convert
     * @return String representation of booolean
     */
    private String checkStatus(boolean status) {
        if (status) {
            return "yes";
        } else {
            return "no";
        }
    }

    /**
     * List adapter with configuration file options
     * 
     * @author Veljko Ilkic
     */
    private class OptionListAdapter extends BaseAdapter {
        private ArrayList<CheckBox> optionsCheckBoxes = new ArrayList<CheckBox>();

        @Override
        public int getCount() {
            return mAvailableOptions.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout optionView = null;
            if (position == mAvailableOptions.length - 3) {
                optionView = (LinearLayout) mLayoutInflater.inflate(
                        R.layout.config_generator_option_list_item2, null);
                TextView optionName = (TextView) optionView
                        .findViewById(R.id.label);
                optionName.setText(mAvailableOptions[position]);
                TextView optionValue = (TextView) optionView
                        .findViewById(R.id.value);
                optionValue.setText(mPvrStorageStringValue);
            } else if (position == mAvailableOptions.length - 2) {
                optionView = (LinearLayout) mLayoutInflater.inflate(
                        R.layout.config_generator_option_list_item2, null);
                TextView optionName = (TextView) optionView
                        .findViewById(R.id.label);
                optionName.setText(mAvailableOptions[position]);
                TextView optionValue = (TextView) optionView
                        .findViewById(R.id.value);
                optionValue.setText(mPvrTresholdStringValue);
            } else if (position == mAvailableOptions.length - 1) {
                optionView = (LinearLayout) mLayoutInflater.inflate(
                        R.layout.config_generator_option_list_item2, null);
                TextView optionName = (TextView) optionView
                        .findViewById(R.id.label);
                optionName.setText(mAvailableOptions[position]);
                TextView optionValue = (TextView) optionView
                        .findViewById(R.id.value);
                optionValue.setText(mSeekOffsetStringValue);
            } else {
                optionView = (LinearLayout) mLayoutInflater.inflate(
                        R.layout.config_generator_option_list_item, null);
                TextView optionName = (TextView) optionView
                        .findViewById(R.id.label);
                optionName.setText(mAvailableOptions[position]);
                CheckBox checkBox = (CheckBox) optionView
                        .findViewById(R.id.check);
                checkBox.setChecked(mCheckedOptions[position]);
                optionsCheckBoxes.add(checkBox);
            }
            return optionView;
        }

        public ArrayList<CheckBox> getOptionsCheckBoxes() {
            return optionsCheckBoxes;
        }
    }

    /**
     * Custom on item click listener for list view
     * 
     * @author Veljko Ilkic
     */
    private class OptionListOnItemClick implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                long arg3) {
            // Pvr storage
            if (arg2 == mAvailableOptions.length - 3) {
                createAndShowDialog(0);
            } else
            // Pvr threshold
            if (arg2 == mAvailableOptions.length - 2) {
                createAndShowDialog(1);
            }
            // Seek offset
            else if (arg2 == mAvailableOptions.length - 1) {
                createAndShowDialog(2);
            }
            // Others
            else {
                CheckBox checkBox = (CheckBox) arg1.findViewById(R.id.check);
                checkBox.setChecked(!checkBox.isChecked());
                mCheckedOptions[arg2] = checkBox.isChecked();
            }
        }
    }

    /** OnClick item for views */
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.generateButton: {
                generateConfigFile();
                break;
            }
            case R.id.stbButton: {
                setValues(mSTBDVBOptions);
                break;
            }
            case R.id.dvbButton: {
                setValues(mTVDVBOptions);
                break;
            }
            case R.id.atscButton: {
                setValues(mTVATSCOptions);
                break;
            }
            case R.id.dtmbButton: {
                setValues(mTVDTMBOptions);
                break;
            }
            default:
                break;
        }
    }

    private void setValues(boolean[] values) {
        if (null != mOptionsList) {
            for (int i = 0; i < values.length; i++) {
                mCheckedOptions[i] = values[i];
            }
            ((BaseAdapter) mOptionsList.getAdapter()).notifyDataSetChanged();
        }
    }

    /** Store entered key */
    public static void keyEnteredKey(int keyCode) {
        if (10 == sEnteredKeys.size()) {
            sEnteredKeys.remove(0);
        }
        sEnteredKeys.add(keyCode);
    }

    /** Check if config generator activity should be started */
    public static boolean checkForGeneratorStart() {
        boolean isStartGenerator = true;
        for (int i = 0; i < 10; i++) {
            try {
                if (sEnteredKeys.get(i) != KeyEvent.KEYCODE_DPAD_DOWN) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }
        sEnteredKeys.clear();
        return isStartGenerator;
    }
}
