/*
 * Copyright (C) 2015 The Android Open Source Project
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

package tv.newtv.cboxtv.cms.special.util;

import android.content.Context;
import android.support.annotation.NonNull;


import tv.newtv.cboxtv.cms.special.data.SpecialRepository;
import tv.newtv.cboxtv.cms.special.data.local.LocalDataSource;
import tv.newtv.cboxtv.cms.special.data.remote.RemoteDataSource;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;


public class Injection {

    public static SpecialRepository provideTasksRepository(@NonNull Context context) {
        checkNotNull(context);
        return SpecialRepository.getInstance(RemoteDataSource.getInstance(),
                LocalDataSource.getInstance());
    }
}
