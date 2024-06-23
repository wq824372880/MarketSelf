package widget;

import android.animation.AnimatorSet;
import android.content.Context;
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
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.zeekr.component.tv.TvExtKt;
import com.zeekrlife.common.ext.CommExtKt;
import com.zeekrlife.task.base.R;

/**
 * @author
 */
public class TVDownloadProgressLayout extends FrameLayout {

    public static final int DOWNLOAD_STATUS_NONE = -1;

    public static final int DOWNLOAD_STATUS_WAITING = 0;

    public static final int DOWNLOAD_STATUS_LOADING = 1;

    public static final int DOWNLOAD_STATUS_PAUSE = 2;

    private ProgressBar loading;

    private ProgressBar downloadProgress;

    private AppCompatTextView tvStatus;

    private int defaultTrackThickNess = 8;

    private int mCurrDownloadStatus = DOWNLOAD_STATUS_NONE;

    private boolean isDelayedUpdateLoadingStatus = false;

    private AnimatorSet shakeAnimatorSet = null;

    public TVDownloadProgressLayout(@NonNull Context context) {
        this(context, null);
    }

    public TVDownloadProgressLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TVDownloadProgressLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setFocusable(false);
        setFocusableInTouchMode(false);
        setDefaultFocusHighlightEnabled(false);
        View v = LayoutInflater.from(getContext()).inflate(R.layout.tv_download_progress_layout, this, true);
        loading = v.findViewById(R.id.loading);
        downloadProgress = v.findViewById(R.id.download_progress);
        tvStatus = v.findViewById(R.id.tv_status);
    }

    public void setDarkStyle() {
        tvStatus.setTextColor(ContextCompat.getColorStateList(getContext(), R.color.selector_text_color_btn_common_dark));
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
        switch (status) {
            case DOWNLOAD_STATUS_WAITING:
                visibleView(loading);
                visibleView(downloadProgress);
                invisibleView(tvStatus);
                break;
            case DOWNLOAD_STATUS_LOADING:
            case DOWNLOAD_STATUS_PAUSE:
                invisibleView(loading);
                visibleView(downloadProgress);
                visibleView(tvStatus);
                if (tvStatus != null) {
                    String str = status == DOWNLOAD_STATUS_LOADING ? getContext().getString(R.string.tv_pause) : getContext().getString(R.string.tv_go_on);
                    tvStatus.setText(str);
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

    private int dp2px(float dp) {
        try {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getMeasuredWidth(), getResources().getDisplayMetrics());
        } catch (Exception e) {
            CommExtKt.logStackTrace(e);
            return 0;
        }
    }

    @Override
    public boolean dispatchUnhandledMove(View focused, int direction) {
        shakeAnimatorSet = TvExtKt.doFocusShakeAnimate(focused,focused, direction,shakeAnimatorSet);
        return super.dispatchUnhandledMove(focused, direction);
    }

    @Override
    protected void onDetachedFromWindow() {
        if(shakeAnimatorSet != null){
            shakeAnimatorSet.cancel();
        }
        super.onDetachedFromWindow();
    }
}
