package com.android.launcher3.testing;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.android.launcher3.testing.MemoryTracker.MemoryTrackerInterface;
import com.android.launcher3.testing.MemoryTracker.ProcessMemInfo;

public class WeightWatcher extends LinearLayout {
    private static final int BACKGROUND_COLOR = -1073741824;
    private static final int MSG_START = 1;
    private static final int MSG_STOP = 2;
    private static final int MSG_UPDATE = 3;
    private static final int RAM_GRAPH_PSS_COLOR = -6697984;
    private static final int RAM_GRAPH_RSS_COLOR = -6750208;
    private static final int TEXT_COLOR = -1;
    private static final int UPDATE_RATE = 5000;
    Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    WeightWatcher.this.mHandler.sendEmptyMessage(3);
                    return;
                case 2:
                    WeightWatcher.this.mHandler.removeMessages(3);
                    return;
                case 3:
                    int[] trackedProcesses = WeightWatcher.this.mMemoryService.getTrackedProcesses();
                    int childCount = WeightWatcher.this.getChildCount();
                    if (trackedProcesses.length != childCount) {
                        WeightWatcher.this.initViews();
                    } else {
                        int i = 0;
                        while (true) {
                            if (i < childCount) {
                                ProcessWatcher processWatcher = (ProcessWatcher) WeightWatcher.this.getChildAt(i);
                                if (WeightWatcher.indexOf(trackedProcesses, processWatcher.getPid()) < 0) {
                                    WeightWatcher.this.initViews();
                                } else {
                                    processWatcher.update();
                                    i++;
                                }
                            }
                        }
                    }
                    WeightWatcher.this.mHandler.sendEmptyMessageDelayed(3, 5000);
                    return;
                default:
                    return;
            }
        }
    };
    MemoryTracker mMemoryService;

    public class ProcessWatcher extends LinearLayout {
        ProcessMemInfo mMemInfo;
        int mPid;
        GraphView mRamGraph;
        TextView mText;

        public class GraphView extends View {
            Paint headPaint;
            Paint pssPaint;
            Paint ussPaint;

            public GraphView(Context context, AttributeSet attributeSet) {
                super(context, attributeSet);
                this.pssPaint = new Paint();
                this.pssPaint.setColor(WeightWatcher.RAM_GRAPH_PSS_COLOR);
                this.ussPaint = new Paint();
                this.ussPaint.setColor(WeightWatcher.RAM_GRAPH_RSS_COLOR);
                this.headPaint = new Paint();
                this.headPaint.setColor(-1);
            }

            public GraphView(ProcessWatcher processWatcher, Context context) {
                this(context, null);
            }

            public void onDraw(Canvas canvas) {
                int width = canvas.getWidth();
                int height = canvas.getHeight();
                if (ProcessWatcher.this.mMemInfo != null) {
                    int length = ProcessWatcher.this.mMemInfo.pss.length;
                    float f = ((float) width) / ((float) length);
                    float max = Math.max(1.0f, f);
                    float f2 = (float) height;
                    float f3 = f2 / ((float) ProcessWatcher.this.mMemInfo.max);
                    for (int i = 0; i < length; i++) {
                        float f4 = ((float) i) * f;
                        float f5 = f4 + max;
                        float f6 = f2;
                        canvas.drawRect(f4, f2 - (((float) ProcessWatcher.this.mMemInfo.pss[i]) * f3), f5, f6, this.pssPaint);
                        canvas.drawRect(f4, f2 - (((float) ProcessWatcher.this.mMemInfo.uss[i]) * f3), f5, f6, this.ussPaint);
                    }
                    float f7 = ((float) ProcessWatcher.this.mMemInfo.head) * f;
                    canvas.drawRect(f7, 0.0f, f7 + max, f2, this.headPaint);
                }
            }
        }

        public ProcessWatcher(WeightWatcher weightWatcher, Context context) {
            this(context, null);
        }

        public ProcessWatcher(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            float f = getResources().getDisplayMetrics().density;
            this.mText = new TextView(getContext());
            this.mText.setTextColor(-1);
            this.mText.setTextSize(0, 10.0f * f);
            this.mText.setGravity(19);
            int i = (int) (2.0f * f);
            setPadding(i, 0, i, 0);
            this.mRamGraph = new GraphView(this, getContext());
            LayoutParams layoutParams = new LayoutParams(0, (int) (14.0f * f), 1.0f);
            addView(this.mText, layoutParams);
            layoutParams.leftMargin = (int) (4.0f * f);
            layoutParams.weight = 0.0f;
            layoutParams.width = (int) (f * 200.0f);
            addView(this.mRamGraph, layoutParams);
        }

        public void setPid(int i) {
            this.mPid = i;
            this.mMemInfo = WeightWatcher.this.mMemoryService.getMemInfo(this.mPid);
            if (this.mMemInfo == null) {
                StringBuilder sb = new StringBuilder();
                sb.append("Missing info for pid ");
                sb.append(this.mPid);
                sb.append(", removing view: ");
                sb.append(this);
                Log.v("WeightWatcher", sb.toString());
                WeightWatcher.this.initViews();
            }
        }

        public int getPid() {
            return this.mPid;
        }

        public String getUptimeString() {
            long uptime = this.mMemInfo.getUptime() / 1000;
            StringBuilder sb = new StringBuilder();
            long j = uptime / 86400;
            if (j > 0) {
                uptime -= 86400 * j;
                sb.append(j);
                sb.append("d");
            }
            long j2 = uptime / 3600;
            if (j2 > 0) {
                uptime -= 3600 * j2;
                sb.append(j2);
                sb.append("h");
            }
            long j3 = uptime / 60;
            if (j3 > 0) {
                uptime -= 60 * j3;
                sb.append(j3);
                sb.append("m");
            }
            sb.append(uptime);
            sb.append("s");
            return sb.toString();
        }

        public void update() {
            TextView textView = this.mText;
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            sb.append(this.mPid);
            sb.append(this.mPid == Process.myPid() ? "/A" : "/S");
            sb.append(") up ");
            sb.append(getUptimeString());
            sb.append(" P=");
            sb.append(this.mMemInfo.currentPss);
            sb.append(" U=");
            sb.append(this.mMemInfo.currentUss);
            textView.setText(sb.toString());
            this.mRamGraph.invalidate();
        }
    }

    static int indexOf(int[] iArr, int i) {
        for (int i2 = 0; i2 < iArr.length; i2++) {
            if (iArr[i2] == i) {
                return i2;
            }
        }
        return -1;
    }

    public WeightWatcher(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        context.bindService(new Intent(context, MemoryTracker.class), new ServiceConnection() {
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                WeightWatcher.this.mMemoryService = ((MemoryTrackerInterface) iBinder).getService();
                WeightWatcher.this.initViews();
            }

            public void onServiceDisconnected(ComponentName componentName) {
                WeightWatcher.this.mMemoryService = null;
            }
        }, 1);
        setOrientation(1);
        setBackgroundColor(BACKGROUND_COLOR);
    }

    public void initViews() {
        removeAllViews();
        int[] trackedProcesses = this.mMemoryService.getTrackedProcesses();
        for (int pid : trackedProcesses) {
            ProcessWatcher processWatcher = new ProcessWatcher(this, getContext());
            processWatcher.setPid(pid);
            addView(processWatcher);
        }
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mHandler.sendEmptyMessage(1);
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mHandler.sendEmptyMessage(2);
    }
}
