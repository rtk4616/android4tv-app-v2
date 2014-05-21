package com.iwedia.service.system.application;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.util.Log;

import com.iwedia.comm.system.application.AppPermission;
import com.iwedia.service.R;

/**
 * This class contains the SecurityPermissions view implementation. Initially
 * the package's advanced or dangerous security permissions are displayed under
 * categorized groups. {@hide}
 */
public class AppSecurityPermissions {
    private enum State {
        NO_PERMS, DANGEROUS_ONLY, NORMAL_ONLY, BOTH
    }

    private final static String TAG = "AppSecurityPermissions";
    private boolean localLOGV = false;
    private Context mContext;
    private PackageManager mPm;
    private Map<String, String> mDangerousMap;
    private Map<String, String> mNormalMap;
    private List<PermissionInfo> mPermsList;
    private String mDefaultGrpLabel;
    private String mDefaultGrpName = "DefaultGrp";
    private String mPermFormat;
    private State mCurrentState;
    private HashMap<String, String> mGroupLabelCache;
    private List<AppPermission> appPermissions;

    public AppSecurityPermissions(Context context, List<PermissionInfo> permList) {
        mContext = context;
        mPm = mContext.getPackageManager();
        mPermsList = permList;
    }

    public AppSecurityPermissions(Context context, String packageName) {
        mContext = context;
        mPm = mContext.getPackageManager();
        mPermsList = new ArrayList<PermissionInfo>();
        appPermissions = new ArrayList<AppPermission>();
        Set<PermissionInfo> permSet = new HashSet<PermissionInfo>();
        PackageInfo pkgInfo;
        try {
            pkgInfo = mPm.getPackageInfo(packageName,
                    PackageManager.GET_PERMISSIONS);
        } catch (NameNotFoundException e) {
            Log.w(TAG, "Could'nt retrieve permissions for package:"
                    + packageName);
            return;
        }
        // Extract all user permissions
        if ((pkgInfo.applicationInfo != null)
                && (pkgInfo.applicationInfo.uid != -1)) {
            getAllUsedPermissions(pkgInfo.applicationInfo.uid, permSet);
        }
        for (PermissionInfo tmpInfo : permSet) {
            mPermsList.add(tmpInfo);
        }
    }

    private void getAllUsedPermissions(int sharedUid,
            Set<PermissionInfo> permSet) {
        String sharedPkgList[] = mPm.getPackagesForUid(sharedUid);
        if (sharedPkgList == null || (sharedPkgList.length == 0)) {
            return;
        }
        for (String sharedPkg : sharedPkgList) {
            getPermissionsForPackage(sharedPkg, permSet);
        }
    }

    private void getPermissionsForPackage(String packageName,
            Set<PermissionInfo> permSet) {
        PackageInfo pkgInfo;
        try {
            pkgInfo = mPm.getPackageInfo(packageName,
                    PackageManager.GET_PERMISSIONS);
        } catch (NameNotFoundException e) {
            Log.w(TAG, "Could'nt retrieve permissions for package:"
                    + packageName);
            return;
        }
        if ((pkgInfo != null) && (pkgInfo.requestedPermissions != null)) {
            extractPerms(pkgInfo.requestedPermissions, permSet);
        }
    }

    private void extractPerms(String strList[], Set<PermissionInfo> permSet) {
        if ((strList == null) || (strList.length == 0)) {
            return;
        }
        for (String permName : strList) {
            try {
                PermissionInfo tmpPermInfo = mPm.getPermissionInfo(permName, 0);
                if (tmpPermInfo != null) {
                    permSet.add(tmpPermInfo);
                }
            } catch (NameNotFoundException e) {
                Log.e(TAG, "Ignoring unknown permission:" + permName);
            }
        }
    }

    public int getPermissionCount() {
        return mPermsList.size();
    }

    public List<AppPermission> getPermissions() {
        mDefaultGrpLabel = "Default";
        mPermFormat = mContext.getString(R.string.permissions_format);
        setPermissions(mPermsList);
        return appPermissions;
    }

    /**
     * Canonicalizes the group description before it is displayed to the user.
     */
    private String canonicalizeGroupDesc(String groupDesc) {
        if ((groupDesc == null) || (groupDesc.length() == 0)) {
            return null;
        }
        // Both str1 and str2 are non-null and are non-zero in size.
        int len = groupDesc.length();
        if (groupDesc.charAt(len - 1) == '.') {
            groupDesc = groupDesc.substring(0, len - 1);
        }
        return groupDesc;
    }

    /**
     * Utility method that concatenates two strings defined by mPermFormat. a
     * null value is returned if both str1 and str2 are null, if one of the
     * strings is null the other non null value is returned without formatting
     * this is to placate initial error checks
     */
    private String formatPermissions(String groupDesc, CharSequence permDesc) {
        if (groupDesc == null) {
            if (permDesc == null) {
                return null;
            }
            return permDesc.toString();
        }
        groupDesc = canonicalizeGroupDesc(groupDesc);
        if (permDesc == null) {
            return groupDesc;
        }
        // groupDesc and permDesc are non null
        return String.format(mPermFormat, groupDesc, permDesc.toString());
    }

    private String getGroupLabel(String grpName) {
        if (grpName == null) {
            // return default label
            return mDefaultGrpLabel;
        }
        String cachedLabel = mGroupLabelCache.get(grpName);
        if (cachedLabel != null) {
            return cachedLabel;
        }
        PermissionGroupInfo pgi;
        try {
            pgi = mPm.getPermissionGroupInfo(grpName, 0);
        } catch (NameNotFoundException e) {
            Log.i(TAG, "Invalid group name:" + grpName);
            return null;
        }
        String label = pgi.loadLabel(mPm).toString();
        mGroupLabelCache.put(grpName, label);
        return label;
    }

    /**
     * Utility method that displays permissions from a map containing group name
     * and list of permission descriptions.
     */
    private List<AppPermission> displayPermissions(boolean dangerous) {
        Map<String, String> permInfoMap = dangerous ? mDangerousMap
                : mNormalMap;
        List<AppPermission> appPermissions = new ArrayList<AppPermission>();
        AppPermission appPermission;
        Set<String> permInfoStrSet = permInfoMap.keySet();
        for (String loopPermGrpInfoStr : permInfoStrSet) {
            appPermission = new AppPermission();
            String grpLabel = getGroupLabel(loopPermGrpInfoStr);
            Log.e(grpLabel, permInfoMap.get(loopPermGrpInfoStr));
            appPermission.setPermissionGroup(grpLabel);
            appPermission.setDescription(permInfoMap.get(loopPermGrpInfoStr));
            appPermissions.add(appPermission);
        }
        return appPermissions;
    }

    private void displayNoPermissions() {
        Log.e(TAG, "no perms");
    }

    private void showPermissions() {
        switch (mCurrentState) {
            case NO_PERMS:
                displayNoPermissions();
                break;
            case DANGEROUS_ONLY:
                appPermissions = displayPermissions(true);
                break;
            case NORMAL_ONLY:
                appPermissions = displayPermissions(false);
                break;
            case BOTH:
                appPermissions = displayPermissions(true);
                break;
        }
    }

    private boolean isDisplayablePermission(PermissionInfo pInfo) {
        if (pInfo.protectionLevel == PermissionInfo.PROTECTION_DANGEROUS
                || pInfo.protectionLevel == PermissionInfo.PROTECTION_NORMAL) {
            return true;
        }
        return false;
    }

    /*
     * Utility method that aggregates all permission descriptions categorized by
     * group Say group1 has perm11, perm12, perm13, the group description will
     * be perm11_Desc, perm12_Desc, perm13_Desc
     */
    private void aggregateGroupDescs(Map<String, List<PermissionInfo>> map,
            Map<String, String> retMap) {
        if (map == null) {
            return;
        }
        if (retMap == null) {
            return;
        }
        Set<String> grpNames = map.keySet();
        Iterator<String> grpNamesIter = grpNames.iterator();
        while (grpNamesIter.hasNext()) {
            String grpDesc = null;
            String grpNameKey = grpNamesIter.next();
            List<PermissionInfo> grpPermsList = map.get(grpNameKey);
            if (grpPermsList == null) {
                continue;
            }
            for (PermissionInfo permInfo : grpPermsList) {
                CharSequence permDesc = permInfo.loadLabel(mPm);
                grpDesc = formatPermissions(grpDesc, permDesc);
            }
            // Insert grpDesc into map
            if (grpDesc != null) {
                retMap.put(grpNameKey, grpDesc);
            }
        }
    }

    private static class PermissionInfoComparator implements
            Comparator<PermissionInfo> {
        private PackageManager mPm;
        private final Collator sCollator = Collator.getInstance();

        PermissionInfoComparator(PackageManager pm) {
            mPm = pm;
        }

        public final int compare(PermissionInfo a, PermissionInfo b) {
            CharSequence sa = a.loadLabel(mPm);
            CharSequence sb = b.loadLabel(mPm);
            return sCollator.compare(sa, sb);
        }
    }

    private void setPermissions(List<PermissionInfo> permList) {
        mGroupLabelCache = new HashMap<String, String>();
        // add the default label so that uncategorized permissions can go here
        mGroupLabelCache.put(mDefaultGrpName, mDefaultGrpLabel);
        // Map containing group names and a list of permissions under that group
        // categorized as dangerous
        mDangerousMap = new HashMap<String, String>();
        // Map containing group names and a list of permissions under that group
        // categorized as normal
        mNormalMap = new HashMap<String, String>();
        // Additional structures needed to ensure that permissions are unique
        // under
        // each group
        Map<String, List<PermissionInfo>> dangerousMap = new HashMap<String, List<PermissionInfo>>();
        Map<String, List<PermissionInfo>> normalMap = new HashMap<String, List<PermissionInfo>>();
        PermissionInfoComparator permComparator = new PermissionInfoComparator(
                mPm);
        if (permList != null) {
            // First pass to group permissions
            for (PermissionInfo pInfo : permList) {
                if (localLOGV) {
                    Log.e(TAG, "Processing permission:" + pInfo.name);
                }
                if (!isDisplayablePermission(pInfo)) {
                    if (localLOGV)
                        Log.e(TAG, "Permission:" + pInfo.name
                                + " is not displayable");
                    continue;
                }
                Map<String, List<PermissionInfo>> permInfoMap = (pInfo.protectionLevel == PermissionInfo.PROTECTION_DANGEROUS) ? dangerousMap
                        : normalMap;
                String grpName = (pInfo.group == null) ? mDefaultGrpName
                        : pInfo.group;
                if (localLOGV)
                    Log.i(TAG, "Permission:" + pInfo.name
                            + " belongs to group:" + grpName);
                List<PermissionInfo> grpPermsList = permInfoMap.get(grpName);
                if (grpPermsList == null) {
                    grpPermsList = new ArrayList<PermissionInfo>();
                    permInfoMap.put(grpName, grpPermsList);
                    grpPermsList.add(pInfo);
                } else {
                    int idx = Collections.binarySearch(grpPermsList, pInfo,
                            permComparator);
                    if (localLOGV)
                        Log.i(TAG,
                                "idx=" + idx + ", list.size="
                                        + grpPermsList.size());
                    if (idx < 0) {
                        idx = -idx - 1;
                        grpPermsList.add(idx, pInfo);
                    }
                }
            }
            // Second pass to actually form the descriptions
            // Look at dangerous permissions first
            aggregateGroupDescs(dangerousMap, mDangerousMap);
            aggregateGroupDescs(normalMap, mNormalMap);
        }
        mCurrentState = State.NO_PERMS;
        if (mDangerousMap.size() > 0) {
            mCurrentState = (mNormalMap.size() > 0) ? State.BOTH
                    : State.DANGEROUS_ONLY;
        } else if (mNormalMap.size() > 0) {
            mCurrentState = State.NORMAL_ONLY;
        }
        if (localLOGV) {
            Log.i(TAG, "mCurrentState=" + mCurrentState);
        }
        showPermissions();
    }
}