package data.scripts;

import com.fs.starfarer.api.Global;

import java.util.Arrays;

public class CrewReplacer_StringHelper {
    public static final boolean logs = Global.getSettings().getBoolean("crewReplacer_StringLogs");
    public static String modString = "crewReplacerString";
    public static String getString(String className, String function, int lineID){
        try {
            CrewReplacer_Log.loging("got string at: "+modString + "_" + className + "_" + function + "_" + lineID ,new CrewReplacer_StringHelper(),logs);
            return Global.getSettings().getString(modString + "_" + className + "_" + function + "_" + lineID);
        }catch (Exception e){
            CrewReplacer_Log.loging("failed to get string of ID: "+modString + "_" + className + "_" + function + "_" + lineID ,new CrewReplacer_StringHelper(),true);
            return "";
        }
    }
    public static String getString(String className, String function, int lineID,String... splits){
        return getSplitString(getString(className, function, lineID),splits);
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
        if (primary.charAt(primary.length() - 1) == 's' && primary.charAt(primary.length() - 2) == '%'){
            String[] b = a;
            a = new String[b.length+1];
            for (int c = 0; c < b.length; c++){
                a[c] = b[c];
            }
            a[a.length -1] = "";
        }
        try {
            for (int b = 0; b < a.length - 1; b++) {
                output.append(a[b]).append(secondary[b]);
            }
            output.append(a[a.length - 1]);
        }catch (Exception e){
            CrewReplacer_Log.loging("failed to get split string: "+primary+" , "+ Arrays.toString(secondary),new CrewReplacer_StringHelper(),true);
            return "";
        }
        return output.toString();
    }
}
