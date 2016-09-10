package com.ingloriousmind.android.imtimetracking.export;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;

import com.ingloriousmind.android.imtimetracking.R;
import com.ingloriousmind.android.imtimetracking.model.Tracking;
import com.ingloriousmind.android.imtimetracking.time.Tracker;
import com.ingloriousmind.android.imtimetracking.util.FileUtil;
import com.ingloriousmind.android.imtimetracking.util.TimeUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class PdfExporter implements Exporter {

    private Context ctx;
    private Tracker tracker;

    public PdfExporter(Context ctx, Tracker tracker) {
        this.ctx = ctx;
        this.tracker = tracker;
    }

    @Override
    public File export() {

        // get trackings
        List<Tracking> trackings = tracker.getTrackings();
        if (trackings == null || trackings.isEmpty())
            return null;
        Timber.d("exporting %d trackings to pdf", trackings.size());

        // create file
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayString = sdf.format(new Date(System.currentTimeMillis()));
        String pdfFileName = "im-timetracking-" + todayString + ".pdf";
        File pdfFile = new File(FileUtil.appDir, pdfFileName);

        // create pdf
        TextPaint p = new TextPaint();
        p.setTextSize(10);
        p.setColor(Color.BLACK);
        p.setTypeface(Typeface.MONOSPACE);
        p.setAntiAlias(true);
        TextPaint gp = new TextPaint(p);
        gp.setColor(ctx.getResources().getColor(R.color.im_green));
        gp.setFakeBoldText(true);
        PdfDocument doc = new PdfDocument();
        int a4Width = (int) (210 / 25.4 * 72);
        int a4Height = (int) (297 / 25.4 * 72);
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(a4Width, a4Height, 1).create();
        PdfDocument.Page page = doc.startPage(pageInfo);

        Canvas c = page.getCanvas();
        int padding = ctx.getResources().getInteger(R.integer.export_pdf_page_padding);
        String unnamed = ctx.getString(R.string.list_item_tracking_unnamed_title);
        StringBuilder sb = new StringBuilder();
        long totalDuration = 0;

        // prepare entries
        for (Tracking t : trackings) {
            sb.append(sdf.format(new Date(t.getCreated()))).append(" ");
            sb.append(TimeUtil.getTimeString(t.getDuration())).append(" | ");
            sb.append(TextUtils.isEmpty(t.getTitle()) ? unnamed : t.getTitle());
            sb.append("\n");
            totalDuration += t.getDuration();
        }

        // write entries - TODO care about pagination at some point
        c.save();
        c.translate(padding, padding);
        StaticLayout sl = new StaticLayout(sb.toString(), p, a4Width / 5 * 4, Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        sl.draw(c);

        // write total
        c.translate(0, sl.getHeight());
        sb.setLength(0);
        sb.append(ctx.getResources().getString(R.string.total));
        sb.append(TimeUtil.getTimeString(totalDuration));
        sl = new StaticLayout(sb.toString(), gp, a4Width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        sl.draw(c);
        c.restore();

        // draw icon
        c.save();
        c.translate(a4Width * 0.80f, a4Height * 0.85f);
        Paint iconPaint = new Paint();
        iconPaint.setAlpha(42);
        Bitmap appIcon = BitmapFactory.decodeResource(ctx.getResources(), R.mipmap.ic_launcher);
        appIcon = Bitmap.createScaledBitmap(appIcon, 80, 80, true);
        c.drawBitmap(appIcon, 0, 0, iconPaint);
        c.restore();
        appIcon.recycle();

        doc.finishPage(page);

        try {
            // write to file
            if (pdfFile.exists()) {
                pdfFile.delete();
            }
            Timber.v("writing pdf file %s", pdfFile.getAbsolutePath());
            doc.writeTo(new FileOutputStream(pdfFile));
        } catch (IOException e) {
            Timber.e(e, "failed writing pdf file");
            return null;
        } finally {
            doc.close();
        }

        return pdfFile;
    }

}
