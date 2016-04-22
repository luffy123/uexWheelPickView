/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.zywx.wbpalmstar.plugin.wheelpickview;


import android.content.Context;

import org.zywx.wbpalmstar.base.BUtility;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class WheelPickViewUtil {
    public final static String AREAPICKERVIEW_FUN_ON_CONFIRMCLICK = "uexWheelPickView.onConfirmClick";

    /**
     * 读取文本数据
     * 
     * @param context
     *            程序上下文
     * @return String, 读取到的文本内容，失败返回null
     */
    public static String readData(Context context, String dataPath) {
        InputStream is = null;
        String content = null;
        try {
            if (dataPath.startsWith(BUtility.F_Widget_RES_path)) {
                is = context.getAssets().open(dataPath);
            } else {
                is = new FileInputStream(dataPath);
            }
            if (is != null) {
                byte[] buffer = new byte[1024];
                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                while (true) {
                    int readLength = is.read(buffer);
                    if (readLength == -1)
                        break;
                    arrayOutputStream.write(buffer, 0, readLength);
                }
                is.close();
                arrayOutputStream.close();
                content = new String(arrayOutputStream.toByteArray());
            }
        } catch (Exception e) {
            e.printStackTrace();
            content = null;
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return content;
    }

}