
package com.example.parkingslotfinder;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;

public class ReceiptActivity extends AppCompatActivity {

    private final int NAVY = Color.rgb(7, 26, 50);
    private final int BLUE = Color.rgb(25, 58, 90);
    private final int GREEN = Color.rgb(0, 255, 136);
    private final int LIGHT = Color.rgb(208, 216, 224);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String bookingId = getIntent().getStringExtra("bookingId");
        String receiptId = "PN-RCP-" + bookingId;
        String parkingLot = getIntent().getStringExtra("parkingLot");
        String slotNumber = getIntent().getStringExtra("slotNumber");
        String startTime = getIntent().getStringExtra("startTime");
        String endTime = getIntent().getStringExtra("endTime");
        String status = getIntent().getStringExtra("status");
        String totalCost = getIntent().getStringExtra("totalCost");


        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 100, 24, 24);
        root.setBackgroundColor(NAVY);

        TextView heading = new TextView(this);
        heading.setText("ParkNow");
        heading.setTextColor(GREEN);
        heading.setTextSize(28);
        heading.setTypeface(null, Typeface.BOLD);
        heading.setGravity(android.view.Gravity.CENTER);
        root.addView(heading);

        TextView subtitle = new TextView(this);
        subtitle.setText("BOOKING RECEIPT");
        subtitle.setTextColor(Color.WHITE);
        subtitle.setTextSize(16);
        subtitle.setGravity(android.view.Gravity.CENTER);
        subtitle.setPadding(0, 8, 0, 24);
        root.addView(subtitle);

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(20, 20, 20, 20);

        GradientDrawable background = new GradientDrawable();
        background.setColor(BLUE);
        background.setCornerRadius(24);
        card.setBackground(background);

        root.addView(card, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        addRow(card, "Receipt ID", receiptId);
        addRow(card, "Booking ID", value(bookingId));
        addRow(card, "Parking lot", value(parkingLot));
        addRow(card, "Slot number", value(slotNumber));
        addRow(card, "Start time", value(startTime));
        addRow(card, "End time", value(endTime));
        addRow(card, "Booking status", value(status));

        View divider = new View(this);
        divider.setBackgroundColor(LIGHT);
        LinearLayout.LayoutParams dividerParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 1);
        dividerParams.setMargins(0, 16, 0, 16);
        card.addView(divider, dividerParams);

        addRow(
                card,
                "Total amount",
                "₹" + value(totalCost)
        );

        Button downloadButton = new Button(this);
        downloadButton.setText("Download PDF Receipt");

        downloadButton.setOnClickListener(v -> {
            createPdf(
                    bookingId,
                    parkingLot,
                    slotNumber,
                    startTime,
                    endTime,
                    status,
                    totalCost
            );
        });

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        buttonParams.setMargins(0, 24, 0, 0);
        root.addView(downloadButton, buttonParams);



        setContentView(root);


    }

    private void addRow(
            LinearLayout parent,
            String label,
            String value
    ) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(0, 8, 0, 8);

        TextView labelView = new TextView(this);
        labelView.setText(label);
        labelView.setTextColor(LIGHT);
        labelView.setTextSize(13);

        TextView valueView = new TextView(this);
        valueView.setText(value);
        valueView.setTextColor(Color.WHITE);
        valueView.setTextSize(16);
        valueView.setTypeface(null, Typeface.BOLD);
        valueView.setPadding(0, 4, 0, 0);

        row.addView(labelView);
        row.addView(valueView);
        parent.addView(row);
    }

    private String value(String text) {
        return text == null || text.trim().isEmpty()
                ? "Not available"
                : text;
    }


    private void createPdf(
            String bookingId,
            String parkingLot,
            String slotNumber,
            String startTime,
            String endTime,
            String status,
            String totalCost
    ) {
        PdfDocument document = new PdfDocument();

        try {
            PdfDocument.PageInfo pageInfo =
                    new PdfDocument.PageInfo.Builder(595, 842, 1).create();

            PdfDocument.Page page = document.startPage(pageInfo);
            android.graphics.Canvas canvas = page.getCanvas();

            Paint paint = new Paint();
            paint.setAntiAlias(true);

            // White page background
            canvas.drawColor(android.graphics.Color.WHITE);

            // Receipt heading
            paint.setColor(android.graphics.Color.rgb(7, 26, 50));
            paint.setTextSize(30);
            paint.setTypeface(android.graphics.Typeface.create(
                    "sans-serif", android.graphics.Typeface.BOLD));
            canvas.drawText("ParkNow", 50, 75, paint);

            paint.setTextSize(17);
            canvas.drawText("BOOKING RECEIPT", 50, 110, paint);

            // Green divider
            paint.setColor(android.graphics.Color.rgb(0, 180, 100));
            paint.setStrokeWidth(3);
            canvas.drawLine(50, 135, 545, 135, paint);

            // Receipt details
            paint.setColor(android.graphics.Color.rgb(7, 26, 50));
            paint.setTextSize(15);
            paint.setTypeface(android.graphics.Typeface.create(
                    "sans-serif", android.graphics.Typeface.NORMAL));

            int y = 185;
            int spacing = 55;

            String[][] rows = {
                    {"Receipt ID", "PN-RCP-" + safe(bookingId)},
                    {"Booking ID", safe(bookingId)},
                    {"Parking lot", safe(parkingLot)},
                    {"Slot number", safe(slotNumber)},
                    {"Start time", safe(startTime)},
                    {"End time", safe(endTime)},
                    {"Booking status", safe(status)},
                    {"Total amount", "Rs. " + safe(totalCost)}
            };

            for (String[] row : rows) {
                paint.setTypeface(android.graphics.Typeface.create(
                        "sans-serif", android.graphics.Typeface.BOLD));
                canvas.drawText(row[0], 50, y, paint);

                paint.setTypeface(android.graphics.Typeface.create(
                        "sans-serif", android.graphics.Typeface.NORMAL));

                String value = row[1];
                if (paint.measureText(value) > 300) {
                    value = value.substring(0, Math.min(value.length(), 35)) + "...";
                }

                canvas.drawText(value, 245, y, paint);
                y += spacing;
            }

            paint.setColor(android.graphics.Color.GRAY);
            paint.setTextSize(16);
            canvas.drawText("Thank you for choosing ParkNow!", 50, 700, paint);

            document.finishPage(page);

            String fileName = "ParkNow_Receipt_" +
                    safe(bookingId).replaceAll("[^a-zA-Z0-9_-]", "_") +
                    ".pdf";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_DOWNLOADS);

                Uri uri = getContentResolver().insert(
                        MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                        values
                );

                if (uri == null) {
                    throw new IOException("Could not create the PDF file.");
                }

                try (OutputStream output =
                             getContentResolver().openOutputStream(uri)) {
                    if (output == null) {
                        throw new IOException("Could not open the PDF file.");
                    }
                    document.writeTo(output);
                }

            } else {
                File directory = getExternalFilesDir(
                        Environment.DIRECTORY_DOCUMENTS);

                if (directory == null) {
                    throw new IOException("Storage is unavailable.");
                }

                File file = new File(directory, fileName);

                try (FileOutputStream output = new FileOutputStream(file)) {
                    document.writeTo(output);
                }
            }

            Toast.makeText(
                    this,
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                            ? "Receipt saved to Downloads."
                            : "Receipt saved to app documents.",
                    Toast.LENGTH_LONG
            ).show();

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Could not create receipt: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
            android.util.Log.e("RECEIPT_PDF", "PDF creation failed", e);

        } finally {
            document.close();
        }
    }

    private String safe(String value) {
        return value == null || value.trim().isEmpty()
                ? "Not available"
                : value;
    }

}
