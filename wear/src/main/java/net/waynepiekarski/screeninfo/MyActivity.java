// ---------------------------------------------------------------------
// Copyright 2015 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// ---------------------------------------------------------------------

package net.waynepiekarski.screeninfo;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyActivity extends Activity implements View.OnClickListener {

    private TextView mTextView;
    private OverlayView mOverlayView;
    private MyOutputManager mMyOutputManager;
    private RelativeLayout mLayoutView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        mMyOutputManager = new MyOutputManager(this);

	mLayoutView = (RelativeLayout)findViewById(R.id.layout);
	mTextView = (TextView)findViewById(R.id.text);
	mOverlayView = (OverlayView)findViewById(R.id.overlay);
	mMyOutputManager.setTextView(mTextView);

	// Recursive add a listener for every View in the hierarchy, this is the only way to get all clicks
	ListenerHelper.recursiveSetOnClickListener((RelativeLayout)findViewById(R.id.layout), MyActivity.this);

	// Prevent display from sleeping
	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	// Allow OverlayView to send the Canvas and View dimensions to MyOutputManager
	mOverlayView.setMyOutputManager(mMyOutputManager);

	// setOnApplyWindowInsetsListener does not seem to ever work, so wait for the View to be attached
	// and then just grab the root window insets directly.
        mLayoutView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
	    public void onViewAttachedToWindow(View v) {
		WindowInsets windowInsets = getWindow().getDecorView().getRootWindowInsets();
		Logging.debug("onViewAttachedToWindow has WindowInsets=" + windowInsets);
		mMyOutputManager.handleWindowInsets(windowInsets);
		mOverlayView.setRound(windowInsets.isRound());
	    }
            @Override
	    public void onViewDetachedFromWindow(View v) {
	    }
        });
    }

    @Override
    public void onClick (View v) {
        mMyOutputManager.nextView();
    }
}
