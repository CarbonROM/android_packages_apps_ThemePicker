/*
 * Copyright (C) 2019 The Android Open Source Project
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
package com.android.customization.model.theme.custom;

import static com.android.customization.model.ResourceConstants.PRIMARY_COLOR_NAME;
import static com.android.customization.model.ResourceConstants.PRIMARY_COLOR_DEFAULT_DARK_NAME;
import static com.android.customization.model.ResourceConstants.PRIMARY_COLOR_DEFAULT_LIGHT_NAME;
import static com.android.customization.model.ResourceConstants.ANDROID_PACKAGE;
import static com.android.customization.model.ResourceConstants.OVERLAY_CATEGORY_ANDROID_THEME;
import static com.android.customization.model.ResourceConstants.OVERLAY_CATEGORY_PRIMARY;
import static com.android.customization.model.ResourceConstants.OVERLAY_CATEGORY_ICON_ANDROID;
import static com.android.customization.model.ResourceConstants.PATH_SIZE;
import static com.android.customization.model.ResourceConstants.SYSUI_PACKAGE;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.graphics.PathParser;

import com.android.customization.model.ResourceConstants;
import com.android.customization.model.theme.OverlayManagerCompat;
import com.android.customization.model.theme.custom.ThemeComponentOption.PrimaryOption;
import com.android.wallpaper.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link ThemeComponentOptionProvider} that reads {@link PrimaryOption}s from
 */
public class PrimaryOptionsProvider extends ThemeComponentOptionProvider<PrimaryOption> {

    private static final String TAG = "PrimaryOptionsProvider";
    private final CustomThemeManager mCustomThemeManager;
    private final String mDefaultThemePackage;

    public PrimaryOptionsProvider(Context context, OverlayManagerCompat manager,
            CustomThemeManager customThemeManager) {
        super(context, manager, OVERLAY_CATEGORY_PRIMARY);
        mCustomThemeManager = customThemeManager;
        // System color is set with a static overlay for android.theme category, so let's try to
        // find that first, and if that's not present, we'll default to System resources.
        // (see #addDefaults())
        List<String> themePackages = manager.getOverlayPackagesForCategory(
                OVERLAY_CATEGORY_ANDROID_THEME, UserHandle.myUserId(), ANDROID_PACKAGE);
        mDefaultThemePackage = themePackages.isEmpty() ? null : themePackages.get(0);
    }

    @Override
    protected void loadOptions() {
        Resources system = Resources.getSystem();
        int accentColor = mCustomThemeManager.resolveAccentColor(mContext.getResources());
        int defaultPrimaryColorLight = system.getColor(
                system.getIdentifier(ResourceConstants.PRIMARY_COLOR_DEFAULT_LIGHT_NAME, "color",
                ResourceConstants.ANDROID_PACKAGE), null);
        int defaultPrimaryColorDark = system.getColor(
                system.getIdentifier(ResourceConstants.PRIMARY_COLOR_DEFAULT_DARK_NAME, "color",
                ResourceConstants.ANDROID_PACKAGE), null);
        addDefaults(defaultPrimaryColorLight, defaultPrimaryColorDark);
        for (String overlayPackage : mOverlayPackages) {
            try {
                Resources overlayRes = getOverlayResources(overlayPackage);
                int primaryColorLight = overlayRes.getColor(
                        overlayRes.getIdentifier(PRIMARY_COLOR_DEFAULT_LIGHT_NAME, "color", overlayPackage),
                        null);
                int primaryColorDark = overlayRes.getColor(
                        overlayRes.getIdentifier(PRIMARY_COLOR_NAME, "color", overlayPackage),
                        null);
                PackageManager pm = mContext.getPackageManager();
                String label = pm.getApplicationInfo(overlayPackage, 0).loadLabel(pm).toString();
                if (primaryColorLight != defaultPrimaryColorLight) {
                    PrimaryOption option = new PrimaryOption(overlayPackage, label,
                            primaryColorLight, accentColor, PrimaryOption.LIGHT);
                    mOptions.add(option);
                }
                if (primaryColorDark != defaultPrimaryColorDark) {
                    PrimaryOption option = new PrimaryOption(overlayPackage, label,
                            primaryColorDark, accentColor, PrimaryOption.DARK);
                    mOptions.add(option);
                }
            } catch (NameNotFoundException | NotFoundException e) {
                Log.w(TAG, String.format("Couldn't load primary overlay %s, will skip it",
                        overlayPackage), e);
            }
        }
    }

    private void addDefaults(int primaryColorLight, int primaryColorDark) {
        int accentColor = mCustomThemeManager.resolveAccentColor(mContext.getResources());
        PrimaryOption option = new PrimaryOption(null,
                mContext.getString(R.string.default_theme_title), primaryColorLight,
                accentColor, PrimaryOption.LIGHT);
        mOptions.add(option);
        option = new PrimaryOption(null,
                mContext.getString(R.string.default_theme_title), primaryColorDark,
                accentColor, PrimaryOption.DARK);
        mOptions.add(option);
    }
}
