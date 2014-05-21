package com.iwedia.dlna;

public class DlnaStreamParam {
    protected String urlStreamParam;
    protected String mimeStreamParam;

    protected enum Dlna_item_profile {
        PROFILE_NOT_SUPPORTED, /** < Unknown profile */
        DLNA_ITEM_PROFILE_FIRST_IMAGE, /** < First image profile marker */
        PNG_TN, /** < PNG_TN */
        PNG_SM_ICO, /** < PNG_SM_ICO */
        PNG_LRG_ICO, /** < PNG_LRG_ICO */
        PNG_LRG, /** < PNG_LRG */
        JPEG_TN, /** < JPEG_TN */
        JPEG_SM_ICO, /** < JPEG_SM_ICO */
        JPEG_LRG_ICO, /** < JPEG_LRG_ICO */
        JPEG_SM, /** < JPEG_SM */
        JPEG_MED, /** < JPEG_MED */
        JPEG_LRG, /** < JPEG_LRG */
        DLNA_ITEM_PROFILE_LAST_IMAGE, /** < Last image profile marker */
        DLNA_ITEM_PROFILE_FIRST_AUDIO, /** < First audio profile marker */
        HEAAC_L3_ADTS, /** < HEAAC_L3_ADTS */
        AMR_3GPP, /** < AMR_3GPP */
        AAC_ADTS_320, /** < AAC_ADTS_320 */
        AAC_ISO_320, /** < AAC_ISO_320 */
        WMABASE, /** < WMABASE */
        WMAFULL, /** < WMAFULL */
        WMAPRO, /** < WMAPRO */
        LPCM_low, /** < LPCM_low */
        LPCM, /** < LPCM */
        MP3, /** < MP3 */
        DLNA_ITEM_PROFILE_LAST_AUDIO, /** < Last audio profile marker */
        DLNA_ITEM_PROFILE_FIRST_VIDEO, /** < First video profile marker */
        AVC_TS_BL_CIF15_AAC, /** < AVC_TS_BL_CIF15_AAC */
        AVC_MP4_BL_CIF30_AAC_MULT5, /** < AVC_MP4_BL_CIF30_AAC_MULT5 */
        AVC_MP4_BL_CIF15_AAC_520, /** < AVC_MP4_BL_CIF15_AAC_520 */
        AVC_3GPP_BL_QCIF15_AAC, /** < AVC_3GPP_BL_QCIF15_AAC */
        MPEG4_H263_MP4_P0_L10_AAC, /** < MPEG4_H263_MP4_P0_L10_AAC */
        MPEG4_H263_MP4_P0_L10_AAC_LTP, /** < MPEG4_H263_MP4_P0_L10_AAC_LTP */
        MPEG4_P2_MP4_SP_AAC, /** < MPEG4_P2_MP4_SP_AAC */
        MPEG4_P2_MP4_SP_AAC_LTP, /** < MPEG4_P2_MP4_SP_AAC_LTP */
        WMVHIGH_FULL, /** < WMVHIGH_FULL */
        WMVHIGH_PRO, /** < WMVHIGH_PRO */
        WMVMED_BASE, /** < WMVMED_BASE */
        WMVMED_FULL, /** < WMVMED_FULL */
        WMVMED_PRO, /** < WMVMED_PRO */
        MPEG_PS_PAL, /** < MPEG_PS_PAL */
        MPEG_PS_NTSC, /** < MPEG_PS_NTSC */
        MPEG_TS_SD_KO, /** < MPEG_TS_SD_KO */
        MPEG_TS_SD_KO_T, /** < MPEG_TS_SD_KO_T */
        MPEG_TS_HD_KO_T, /** < MPEG_TS_HD_KO_T */
        MPEG_TS_SD_EU, /** < MPEG_TS_SD_EU */
        MPEG_TS_SD_EU_T, /** < MPEG_TS_SD_EU_T */
        MPEG_TS_SD_EU_ISO, /** < MPEG_TS_SD_EU_ISO */
        MPEG_TS_JP_T, /** < MPEG_TS_JP_T */
        MPEG_TS_JP_ISO, /** < MPEG_TS_JP_ISO */
        MPEG_TS_HD_NA, /** < MPEG_TS_HD_NA */
        MPEG_TS_HD_NA_T, /** < MPEG_TS_HD_NA_T */
        MPEG_TS_HD_NA_ISO, /** < MPEG_TS_HD_NA_ISO */
        MPEG_TS_SD_NA, /** < MPEG_TS_SD_NA */
        MPEG_TS_SD_NA_T, /** < MPEG_TS_SD_NA_T */
        MPEG_TS_SD_NA_ISO, /** < MPEG_TS_SD_NA_ISO */
        DLNA_ITEM_PROFILE_LAST_VIDEO, /** < Last video profile marker */
        LAST_ITEM_PROFILE
        /** < Last profile marker */
    }

    private Dlna_item_profile dlna_item_profile;
    protected String dtcp_hostStreamParam;
    protected int portStreamParam;

    public String getUrlStreamParam() {
        return urlStreamParam;
    }

    public void setUrlStreamParam(String urlStreamParam) {
        this.urlStreamParam = urlStreamParam;
    }

    public String getMimeStreamParam() {
        return mimeStreamParam;
    }

    public void setMimeStreamParam(String mimeStreamParam) {
        this.mimeStreamParam = mimeStreamParam;
    }

    public int getProfileStreamParam() {
        System.out.println(" getProfileStreamParam "
                + dlna_item_profile.ordinal());
        return dlna_item_profile.ordinal();
    }

    public void setProfileStreamParam(Dlna_item_profile dlna_item_profile) {
        System.out.println(" setProfileStreamParam: " + dlna_item_profile);
        System.out.println(" setProfileStreamParam: "
                + dlna_item_profile.ordinal());
        this.dlna_item_profile = dlna_item_profile;
    }

    public String getDtcp_hostStreamParam() {
        return dtcp_hostStreamParam;
    }

    public void setDtcp_hostStreamParam(String dtcp_hostStreamParam) {
        this.dtcp_hostStreamParam = dtcp_hostStreamParam;
    }

    public int getPortStreamParam() {
        return portStreamParam;
    }

    public void setPortStreamParam(int portStreamParam) {
        this.portStreamParam = portStreamParam;
    }
}
