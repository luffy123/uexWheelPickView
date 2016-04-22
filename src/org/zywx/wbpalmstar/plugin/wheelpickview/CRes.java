package org.zywx.wbpalmstar.plugin.wheelpickview;


import android.content.Context;
import android.content.res.Resources;

public class CRes{
    public static int app_name;
    private static boolean init;
    public static int plugin_wheelpickview_layout;
    public static int plugin_wheelpickview_complete;
    public static int plugin_wheelpickview_cancel;
    public static int plugin_wheelpickview_province;
    public static int plugin_wheelpickview_city;
    public static int plugin_wheelpickview_district;
    public static int plugin_wheelpickview_center_drawable;
    public static int plugin_wheelpickview_raw_acearea;

    public static boolean init(Context context){
        if(init){
            return init;
        }
        String packg = context.getPackageName();
        Resources res = context.getResources();
        plugin_wheelpickview_layout=res.getIdentifier("plugin_wheelpickview_layout", "layout", packg);
        
        plugin_wheelpickview_complete=res.getIdentifier("plugin_wheelpickview_complete", "id", packg);
        plugin_wheelpickview_cancel=res.getIdentifier("plugin_wheelpickview_cancel", "id", packg);
        plugin_wheelpickview_province=res.getIdentifier("plugin_wheelpickview_province", "id", packg);
        plugin_wheelpickview_city=res.getIdentifier("plugin_wheelpickview_city", "id", packg);
        plugin_wheelpickview_district=res.getIdentifier("plugin_wheelpickview_district", "id", packg);
        
        plugin_wheelpickview_center_drawable=res.getIdentifier("plugin_wheelpickview_center_drawable", "drawable", packg);

        plugin_wheelpickview_raw_acearea=res.getIdentifier("address_data", "raw", packg);
        app_name = res.getIdentifier("app_name", "string", packg);
        init = true;
        return true;
    }
}
