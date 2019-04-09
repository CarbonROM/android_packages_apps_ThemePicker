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
package com.android.customization.picker.clock;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.android.customization.model.CustomizationManager.Callback;
import com.android.customization.model.clock.ClockManager;
import com.android.customization.model.clock.Clockface;
import com.android.customization.module.ThemesUserEventLogger;
import com.android.customization.picker.BasePreviewAdapter;
import com.android.customization.picker.BasePreviewAdapter.PreviewPage;
import com.android.customization.widget.OptionSelectorController;
import com.android.customization.widget.PreviewPager;
import com.android.wallpaper.R;
import com.android.wallpaper.asset.Asset;
import com.android.wallpaper.module.InjectorProvider;
import com.android.wallpaper.picker.ToolbarFragment;

/**
 * Fragment that contains the main UI for selecting and applying a Clockface.
 */
public class ClockFragment extends ToolbarFragment {

    /**
     * Interface to be implemented by an Activity hosting a {@link ClockFragment}
     */
    public interface ClockFragmentHost {
        ClockManager getClockManager();
    }

    public static ClockFragment newInstance(CharSequence title) {
        ClockFragment fragment = new ClockFragment();
        fragment.setArguments(ToolbarFragment.createArguments(title));
        return fragment;
    }

    private RecyclerView mOptionsContainer;
    private OptionSelectorController<Clockface> mOptionsController;
    private Clockface mSelectedOption;
    private ClockManager mClockManager;
    private PreviewPager mPreviewPager;
    private ThemesUserEventLogger mEventLogger;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mClockManager = ((ClockFragmentHost) context).getClockManager();
        mEventLogger = (ThemesUserEventLogger)
                InjectorProvider.getInjector().getUserEventLogger(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.fragment_clock_picker, container, /* attachToRoot */ false);
        setUpToolbar(view);
        mPreviewPager = view.findViewById(R.id.clock_preview_pager);
        mOptionsContainer = view.findViewById(R.id.options_container);
        setUpOptions();
        view.findViewById(R.id.apply_button).setOnClickListener(v -> {
            mClockManager.apply(mSelectedOption, new Callback() {
                @Override
                public void onSuccess() {
                    getActivity().finish();
                }

                @Override
                public void onError(@Nullable Throwable throwable) {
                    //TODO(santie): handle
                }
            });

        });
        return view;
    }

    private void createAdapter() {
        mPreviewPager.setAdapter(new ClockPreviewAdapter(getContext(), mSelectedOption));
    }

    private void setUpOptions() {
        mClockManager.fetchOptions(options -> {
            mOptionsController = new OptionSelectorController<>(mOptionsContainer, options);

            mOptionsController.addListener(selected -> {
                mSelectedOption = (Clockface) selected;
                mEventLogger.logClockSelected(mSelectedOption);
                createAdapter();
            });
            mOptionsController.initOptions(mClockManager);
            for (Clockface option : options) {
                if (option.isActive(mClockManager)) {
                    mSelectedOption = option;
                }
            }
            // For development only, as there should always be a grid set.
            if (mSelectedOption == null) {
                mSelectedOption = options.get(0);
            }
            createAdapter();
        }, false);
    }

    private static class ClockfacePreviewPage extends PreviewPage {

        private final Asset mPreviewAsset;

        public ClockfacePreviewPage(String title, Asset previewAsset) {
            super(title);
            mPreviewAsset = previewAsset;
        }

        @Override
        public void bindPreviewContent() {
            ImageView previewImage = card.findViewById(R.id.clock_preview_image);
            Context context = previewImage.getContext();
            Resources res = previewImage.getResources();
            mPreviewAsset.loadDrawableWithTransition(context, previewImage,
                    100 /* transitionDurationMillis */,
                    null /* drawableLoadedListener */,
                    res.getColor(android.R.color.transparent, null) /* placeholderColor */);
        }
    }

    /**
     * Adapter class for mPreviewPager.
     * This is a ViewPager as it allows for a nice pagination effect (ie, pages snap on swipe,
     * we don't want to just scroll)
     */
    private static class ClockPreviewAdapter extends BasePreviewAdapter<ClockfacePreviewPage> {
        ClockPreviewAdapter(Context context, Clockface clockface) {
            super(context, R.layout.clock_preview_card);
            addPage(new ClockfacePreviewPage(clockface.getTitle(), clockface.getPreviewAsset()));
        }
    }
}
