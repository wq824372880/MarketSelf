package com.zeekrlife.task.base.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RotateDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.zeekrlife.common.ext.CommExtKt;
import com.zeekrlife.task.base.R;

/**
 * @author
 */
public class DownloadProgressLayout extends FrameLayout {

    public static final int DOWNLOAD_STATUS_NONE = -1;

    public static final int DOWNLOAD_STATUS_WAITING = 0;

    public static final int DOWNLOAD_STATUS_LOADING = 1;

    public static final int DOWNLOAD_STATUS_PAUSE = 2;

    private ProgressBar indeterminateProgress;

    private Drawable loadingIndicator;

    private CircularProgressIndicator downloadProgress;

    private ImageView imgStatus;

    private int defaultTrackThickNess = 8;

    private int mCurrDownloadStatus = DOWNLOAD_STATUS_NONE;

    private boolean isDelayedUpdateLoadingStatus = false;

    public DownloadProgressLayout(@NonNull Context context) {
        this(context, null);
    }

    public DownloadProgressLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DownloadProgressLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.download_progress_layout, this, true);

        indeterminateProgress = v.findViewById(R.id.indicatorProgress);
        loadingIndicator = indeterminateProgress.getIndeterminateDrawable();

        downloadProgress = v.findViewById(R.id.downloadProgress);
        int trackThickness = downloadProgress.getTrackThickness();
        if (trackThickness > 0) {
            defaultTrackThickNess = trackThickness;
        }

        imgStatus = v.findViewById(R.id.downloadStatus);
    }

    public void setProgress(int progress) {
        if (downloadProgress != null) {
            downloadProgress.setProgress(progress);
        }
    }

    public int getProgress() {
        if (downloadProgress != null) {
            return downloadProgress.getProgress();
        }
        return 0;
    }

    public void setProgressStatus(int status) {
        if (mCurrDownloadStatus == status) {
            if (isDelayedUpdateLoadingStatus) {
                isDelayedUpdateLoadingStatus = false;
                removeCallbacks(delayedUpdateStatus);
            }
            return;
        }

        if (status == DOWNLOAD_STATUS_LOADING && mCurrDownloadStatus == DOWNLOAD_STATUS_WAITING) {
            if (isDelayedUpdateLoadingStatus) {
                return;
            }
            isDelayedUpdateLoadingStatus = true;
            removeCallbacks(delayedUpdateStatus);
            postDelayed(delayedUpdateStatus, 1500);
        } else {
            isDelayedUpdateLoadingStatus = false;
            removeCallbacks(delayedUpdateStatus);
            updateProgressStatus(status);
        }
    }

    private final Runnable delayedUpdateStatus = new Runnable() {
        @Override
        public void run() {
            updateProgressStatus(DOWNLOAD_STATUS_LOADING);
            isDelayedUpdateLoadingStatus = false;
        }
    };

    private void updateProgressStatus(int status) {
        mCurrDownloadStatus = status;
        int progress = getProgress();
        switch (status) {
            case DOWNLOAD_STATUS_WAITING:
                if (progress > 0 && loadingIndicator != null && loadingIndicator instanceof RotateDrawable) {
                    RotateDrawable rotateDrawable = (RotateDrawable) loadingIndicator;
                    float fromDegrees = 360 * downloadProgress.getProgress() / (100 * 1.0f) + 90;
                    rotateDrawable.setFromDegrees(fromDegrees);
                    rotateDrawable.setToDegrees(fromDegrees + 360 - 10);
                }
                visibleView(indeterminateProgress);
                invisibleView(downloadProgress);
                invisibleView(imgStatus);
                break;
            case DOWNLOAD_STATUS_LOADING:
            case DOWNLOAD_STATUS_PAUSE:
                invisibleView(indeterminateProgress);
                visibleView(downloadProgress);
                visibleView(imgStatus);
                if (imgStatus != null) {
                    Drawable drawable =
                        status == DOWNLOAD_STATUS_LOADING ? ContextCompat.getDrawable(getContext(), R.drawable.ic_download_progress_pause)
                            : ContextCompat.getDrawable(getContext(), R.drawable.ic_download_progress_loading);
                    imgStatus.setImageDrawable(drawable);
                }
                break;
            default:
        }
    }

    private void visibleView(View view) {
        try {
            if (view != null && view.getVisibility() != View.VISIBLE) {
                view.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            CommExtKt.logStackTrace(e);
        }
    }

    private void invisibleView(View view) {
        try {
            if (view != null && view.getVisibility() != View.INVISIBLE) {
                view.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            CommExtKt.logStackTrace(e);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        try {
            if (downloadProgress != null && loadingIndicator != null) {
                float ratio = 1.0f;
                if (getMeasuredWidth() > getMeasuredHeight()) {
                    ratio = getMeasuredHeight() / (loadingIndicator.getIntrinsicHeight() * 1.0f);
                    downloadProgress.setIndicatorSize(getMeasuredHeight());
                } else {
                    ratio = getMeasuredWidth() / (loadingIndicator.getIntrinsicWidth() * 1.0f);
                    downloadProgress.setIndicatorSize(getMeasuredWidth());
                }
                int trackThickness = Math.round(ratio * defaultTrackThickNess);
                downloadProgress.setTrackThickness(trackThickness);
                downloadProgress.setTrackCornerRadius(trackThickness / 2);
            }

            if (imgStatus != null) {
                ViewGroup.LayoutParams imgStatusLayoutParams = imgStatus.getLayoutParams();
                imgStatusLayoutParams.width = (int) (getMeasuredWidth() * 0.44);
                imgStatusLayoutParams.height = (int) (getMeasuredHeight() * 0.44);
                imgStatus.setLayoutParams(imgStatusLayoutParams);
            }
        } catch (Exception e) {
            CommExtKt.logStackTrace(e);
        }
    }

    private int dp2px(float dp) {
        try {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getMeasuredWidth(), getResources().getDisplayMetrics());
        } catch (Exception e) {
            CommExtKt.logStackTrace(e);
            return 0;
        }
    }
}
