package data.scripts;

import com.fs.starfarer.api.Global;

public class CrewReplacer_StringHelper {
    public static String modString = "crewReplacerString";
    public static String getString(String className, String function, int lineID){
        try {
            return Global.getSettings().getString(modString + "_" + className + "_" + function + "_" + lineID);
        }catch (Exception e){
            return "";
        }
    }
    public static String getString(String className, String function, int lineID,String... splits){
        return getSplitString(getLogString(className, function, lineID),splits);
    }

    public static String getLogString(String className, String function, int lineID){
        if (CrewReplacer_Log.logsActive) return getString(className, function, lineID);
        return "";
    }
    public static String getLogString(String className, String function, int lineID,boolean displayOverride){
        if (displayOverride) return getString(className, function, lineID);
        return "";
    }
    public static String getLogString(String className, String function, int lineID,String... splits){
        if (CrewReplacer_Log.logsActive) return getString(className, function, lineID,splits);
        return "";
    }
    public static String getLogString(String className, String function, int lineID,boolean displayOverride,String... splits){
        if (displayOverride) return getString(className, function, lineID,splits);
        return "";
    }

    protected static String getSplitString(String primary,String[] secondary){
        StringBuilder output = new StringBuilder();
        String[] a = primary.split("%s");
        for (int b = 0; b < a.length - 1; b++){
            output.append(a[b]).append(secondary[b]);
        }
        output.append(a[a.length - 1]);
        return output.toString();
    }
}
