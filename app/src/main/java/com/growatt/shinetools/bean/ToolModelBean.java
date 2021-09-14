package com.growatt.shinetools.bean;

import java.util.LinkedHashMap;

/**
 * Created：2019/11/21 on 16:36
 * Author:gaideng on admin
 * Description:本地调试安规对应bean
 * EU Model , Australia Model ,UK Model ,General  Model
 * 欧洲机型，澳洲机型，英国机型，通用机型
 */
public class ToolModelBean {
    // == 0为独立机型
    public final static int ONESELF_MODEL = -1;//独立机型 暂时不用
    public final static int EU_MODEL = 0;//欧洲机型
    public final static int AUST_MODEL = 1;//澳洲机型
    public final static int UK_MODEL = 2;//英国机型
    public final static int TYPE_OLD_S = 0;//old号统计
    public final static int TYPE_3_15K = 1;//3-15K TL3-S Model号统计
    public final static int TYPE_30_50K = 2;//30-50K TL3-S Model号统计
    private int type = -1;//总类别  包括TYPE_3_15K TYPE_30_50K
    private int machineType = -2;//机型 包括独立机型 ONESELF_MODEL  EU_MODEL...
    private String safeCode;//安规标准
    private String country;//国家
    private String model;//MODEL号  如A0S0
    //获取当前所有
    private static String[][][][] allModes = {
            //TYPE_OLD_S
            {
                    //EU_MODEL
                    {
                            {"TUNISIA","A2S0"},
                            {"VDE0126","A2S1"},
                            {"G99","A2S5"},
                            {"Greece","A2S6"},
                            {"N4105","A2S7"},
                            {"G98","A2S8"},
                            {"EN50438","A2S9"},
                            {"EN50438_Default","A2SB"},
                            {"Belgium","A2SD"},
                            {"EN50438_Ireland","A3S3"},
                            {"EN50438_Sweden","A3S6"},
                            {"Franch","A3S9"},
                            {"DK1","A3SD"},
                            {"DK2","A3SE"}
                    },
                    //AUST_MODEL
                    {
                            {"cS_AU_Victoria","A4S1"},
                            {"Queensland","A4S2"},
                            {"AS4777","A4S3"},
                            {"cS_AU_Western","A4S4"},
                            {"cS_AU_Horizon","A4S5"},
                            {"cS_AU_Ausgrid","A4S6"},
                            {"cS_AU_Endeavour","A4S7"},
                            {"cS_AU_ErgonEnergy","A4S8"},
                            {"cS_AU_Energex","A4S9"},
                            {"cS_AU_SaNetwork","A4SA"},
                            {"NewZealand","A5S8"},
                            {"AS4777_EXTEND","A5SF"}
                    },
                    //UK_MODEL
                    {
                            {"G99","A0S5"},
                            {"G98","A0S8"},
                            {"Ireland","A1S3"}
                    }
            },
            //TYPE_3_15K
            {
                //EU_MODEL
                    {
                            {"TUNISIA","A2S0"},
                            {"VDE0126","A2S1"},
                            {"RD1663","A2S5"},
                            {"Greece","A2S6"},
                            {"N4105","A2S7"},
                            {"EN50438_Ireland","A2S9"},
                            {"EN50438_Default","A2SB"},
                            {"Belgium","A2SD"},
                            {"Demark_DK1","A3S0"},
                            {"EN50438_Sweden","A3S1"},
                            {"EN50438_Norway","A3S2"},
                            {"Demark_DK2","A3S3"},
                            {"VFR2014","A3S4"},
                            {"Dewa","A3SB"},
                            {"EN50549","A3SE"}
                    },
                //AUST_MODEL
                    {
                            {"cS_AU_Victoria","A4S1"},
                            {"cS_Queensland","A4S2"},
                            {"AS4777","A4S3"},
                            {"cS_AU_Western","A4S4"},
                            {"cS_AU_Horizon","A4S5"},
                            {"cS_AU_Ausgrid","A4S6"},
                            {"cS_AU_Endeavour","A4S7"},
                            {"cS_AU_ErgonEnergy","A4S8"},
                            {"cS_AU_Energex","A4S9"},
                            {"cS_AU_SaNetwork","A4SA"},
                            {"AS4777_New","A5S0"},
                            {"AS4777_EXTEND","A5SF"}
                    },
                //UK_MODEL
                    {
                            {"G99","A0S2"},
                            {"G98","A0S8"},
                            {"Ireland","A0S9"}
                    }
            },
            //TYPE_30_50K
            {
                //EU_MODEL
                    {
                            {"VDE0126","A0S1"},
                            {"RD1663","A0S5"},
                            {"Greece","A0S6"},
                            {"N4105","A0S7"},
                            {"EN50438_Default","A0SB"},
                            {"Belgium","A0SD"},
                            {"Demark_DK1","A1S3"},
                            {"EN50438_Sweden","A1S4"},
                            {"EN50438_Norway","A1S5"},
                            {"VFR2013","A1S6"},
                            {"VFR2014","A1S7"},
                            {"Dewa","A1SB"}
                    },
                //AUST_MODEL
                    {
                            {"AS4777","A0S3"},
                            {"AS4777_New","A1S0"}
                    },
                //UK_MODEL
                    {
                            {"G99","A0S2"},
                            {"G98","A0S8"},
                            {"Ireland","A0S9"}
                    }
            }
    };
    private static LinkedHashMap<Integer,LinkedHashMap<Integer,LinkedHashMap<String,String>>> oldInvModelMap;

    public static LinkedHashMap<Integer, LinkedHashMap<Integer, LinkedHashMap<String, String>>> getOldInvModelMap() {
        if (oldInvModelMap == null) {
            oldInvModelMap = new LinkedHashMap<>();
            for (int i = 0; i < allModes.length; i++) {
                LinkedHashMap<Integer, LinkedHashMap<String, String>> typeMap = new LinkedHashMap<>();
                String[][][] typeAllModel = allModes[i];
                for (int j = 0; j < typeAllModel.length; j++) {
                    String[][] mechAllModel = typeAllModel[j];
                    LinkedHashMap<String, String> siginMap = new LinkedHashMap<>();
                    for (String[] models : mechAllModel) {
                        siginMap.put(models[0], models[1]);
                    }
                    typeMap.put(j, siginMap);
                }
                oldInvModelMap.put(i, typeMap);
            }
        }
        return oldInvModelMap;
    }

    public String getSafeCode() {
        return safeCode;
    }

    public void setSafeCode(String safeCode) {
        this.safeCode = safeCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
