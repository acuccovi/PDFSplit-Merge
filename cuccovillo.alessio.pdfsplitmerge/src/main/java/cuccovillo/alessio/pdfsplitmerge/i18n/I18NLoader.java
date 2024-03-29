/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cuccovillo.alessio.pdfsplitmerge.i18n;

import java.util.ResourceBundle;

public final class I18NLoader {

    private static final ResourceBundle I18N_BUNDLE;
    private static final I18NLoader INSTANCE;

    static {
        I18N_BUNDLE = ResourceBundle.getBundle("cuccovillo.alessio.pdfsplitmerge.i18n.pdfsplitandmerge");
        INSTANCE = new I18NLoader();
    }

    private I18NLoader() {
    }

    public static I18NLoader getInstance() {
        return INSTANCE;
    }

    public static String getString(String key) {
        return I18N_BUNDLE.getString(key);
    }
}
