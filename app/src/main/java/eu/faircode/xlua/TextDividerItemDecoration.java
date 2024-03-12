package eu.faircode.xlua;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TextDividerItemDecoration extends RecyclerView.ItemDecoration {
    public enum TextVerticalAlignment {
        TOP, CENTER, BOTTOM
    }

    private final Paint textPaint;
    private final Paint linePaint;
    private String text;
    private int textSize;
    private int textAlignment;
    private final int textPadding;
    private boolean showLineDivider;
    private int lineThickness;
    private boolean dividerAfterItem; // true for after, false for before

    private TextVerticalAlignment textVerticalAlignment = TextVerticalAlignment.CENTER;
    private int textToLinePadding = 0; // Custom padding between text and line

    private int leftBarToStartParentPadding = 0; // Padding from the left side of the parent to the start of the bar
    private int rightBarToEndParentPadding = 0; // Padding from the right side of the parent to the end of the bar

    public TextDividerItemDecoration(Context context) {
        this(context, "");
    }

    public TextDividerItemDecoration(Context context, String text) {
        this.text = text;

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setAntiAlias(true);
        setTextSize(40); // Default text size

        linePaint = new Paint();
        linePaint.setColor(Color.GRAY); // Set color for bar
        setLineThickness(2); // Default line thickness
        showLineDivider = false; // Divider is disabled by default

        this.textAlignment = Gravity.CENTER; // Default text alignment
        this.textPadding = 10; // Default text padding
        this.dividerAfterItem = true; // By default, place divider after the item
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTextVerticalAlignment(TextVerticalAlignment alignment) {
        this.textVerticalAlignment = alignment;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        textPaint.setTextSize(textSize);
    }

    public void setTextAlignment(int textAlignment) {
        this.textAlignment = textAlignment;
    }

    public void enableLineDivider(boolean show, int thickness) {
        this.showLineDivider = show;
        setLineThickness(thickness);
    }

    private void setLineThickness(int thickness) {
        this.lineThickness = thickness;
        linePaint.setStrokeWidth(thickness);
    }

    public void setDividerPosition(boolean afterItem) {
        this.dividerAfterItem = afterItem;
    }

    private int paddingLeft;
    private int paddingRight;
    private int paddingTop;
    private int paddingBottom;

    public void setTextPadding(int left, int right, int top, int bottom) {
        this.paddingLeft = left;
        this.paddingRight = right;
        this.paddingTop = top;
        this.paddingBottom = bottom;
    }

    public void setTextPaddingLeft(int textPaddingLeft) {
        this.paddingLeft = textPaddingLeft;
    }

    public void setTextPaddingRight(int textPaddingRight) {
        this.paddingRight = textPaddingRight;
    }

    public void setTextPaddingTop(int textPaddingTop) {
        this.paddingTop = textPaddingTop;
    }

    public void setTextPaddingBottom(int textPaddingBottom) {
        this.paddingBottom = textPaddingBottom;
    }

    public void setTextToLinePadding(int paddingSize) {
        this.textToLinePadding = paddingSize;
    }

    public void setLeftBarToStartParentPadding(int paddingSize) {
        this.leftBarToStartParentPadding = paddingSize;
    }

    public void setRightBarToEndParentPadding(int paddingSize) {
        this.rightBarToEndParentPadding = paddingSize;
    }

    // ... (previous code remains the same)

    private float barCornerRadius = 0f; // Default bar corner radius

    // ... (constructor and other methods remain the same)

    public void setBarCornerRadius(float radius) {
        this.barCornerRadius = radius;
    }
    private boolean useIndependentDividers = true; // Default to using independent dividers

    // ... (constructor and other methods remain the same)

    public void setUseIndependentDividers(boolean useIndependentDividers) {
        this.useIndependentDividers = useIndependentDividers;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (useIndependentDividers) {
            drawIndependentDividers(c, parent, state);
        }
    }

    private void drawIndependentDividers(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(child);

            if (position == RecyclerView.NO_POSITION) {
                continue;
            }

            if (showLineDivider && (dividerAfterItem || (!dividerAfterItem && position < parent.getAdapter().getItemCount() - 1))) {
                float totalSpace = textSize + 2 * textPadding + lineThickness;
                float lineY = dividerAfterItem ? child.getBottom() + totalSpace / 2 : child.getTop() - totalSpace / 2;

                RectF barRect = new RectF(leftBarToStartParentPadding, lineY - lineThickness / 2,
                        parent.getWidth() - rightBarToEndParentPadding, lineY + lineThickness / 2);
                c.drawRoundRect(barRect, barCornerRadius, barCornerRadius, linePaint);

                if (text != null && !text.isEmpty()) {
                    float textY;
                    switch (textVerticalAlignment) {
                        case TOP:
                            textY = lineY - totalSpace + lineThickness + paddingTop + textPaint.getFontMetrics().top + textToLinePadding;
                            break;
                        case CENTER:
                            float centerOffset = (textPaint.getFontMetrics().ascent + textPaint.getFontMetrics().descent) / 2;
                            textY = lineY - centerOffset - paddingBottom + paddingTop;
                            break;
                        case BOTTOM:
                        default:
                            textY = lineY + (lineThickness / 2) + paddingTop - paddingBottom - textPaint.getFontMetrics().bottom - textToLinePadding;
                            break;
                    }
                    float x = calculateTextPositionX(parent.getWidth(), textAlignment, textPaint.measureText(text)) + leftBarToStartParentPadding - paddingRight + paddingLeft;
                    c.drawText(text, x, textY, textPaint);
                }
            }
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (!useIndependentDividers) {
            drawLinkedDividers(c, parent, state);
        }
    }

    private void drawLinkedDividers(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int width = parent.getWidth();
        int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            float lineY, textY = 0;
            Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();

            if (showLineDivider) {
                float totalSpace = textSize + 2 * textPadding + lineThickness;
                float textLineCenter = dividerAfterItem ? child.getBottom() + totalSpace / 2 : child.getTop() - totalSpace / 2;
                lineY = textLineCenter - (totalSpace / 2) + (lineThickness / 2);

                RectF barRect = new RectF(leftBarToStartParentPadding, lineY - lineThickness / 2,
                        width - rightBarToEndParentPadding, lineY + lineThickness / 2);
                c.drawRoundRect(barRect, barCornerRadius, barCornerRadius, linePaint);

                if (text != null && !text.isEmpty()) {
                    switch (textVerticalAlignment) {
                        case TOP:
                            textY = lineY - totalSpace + lineThickness + paddingTop + fontMetrics.top + textToLinePadding;
                            break;
                        case CENTER:
                            float centerOffset = (fontMetrics.ascent + fontMetrics.descent) / 2;
                            textY = lineY - centerOffset - paddingBottom + paddingTop;
                            break;
                        case BOTTOM:
                            textY = lineY + (lineThickness / 2) + paddingTop - paddingBottom - fontMetrics.bottom - textToLinePadding;
                            break;
                    }
                    float x = calculateTextPositionX(width, textAlignment, textPaint.measureText(text)) + leftBarToStartParentPadding - paddingRight + paddingLeft;
                    c.drawText(text, x, textY, textPaint);
                }
            } else {
                if (text != null && !text.isEmpty()) {
                    textY = calculateTextYWithoutLine(child, fontMetrics) - paddingBottom + paddingTop;
                    float x = calculateTextPositionX(width, textAlignment, textPaint.measureText(text)) + leftBarToStartParentPadding - paddingRight + paddingLeft;
                    c.drawText(text, x, textY, textPaint);
                }
            }
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (!useIndependentDividers) {
            super.getItemOffsets(outRect, view, parent, state);
            int position = parent.getChildAdapterPosition(view);

            // Reset top offset for all items initially
            outRect.top = 0;

            if (showLineDivider) {
                // If the divider is 'after', only add top space for items that are not the first
                // If the divider is 'before', add top space for all items, including the first
                if (dividerAfterItem && position > 0) {
                    outRect.top = textSize + 2 * textPadding + lineThickness;
                } else if (!dividerAfterItem) {
                    outRect.top = textSize + 2 * textPadding + lineThickness;
                }
            }
        }
    }


    private float calculateTextPositionX(int width, int alignment, float textWidth) {
        switch (alignment) {
            case Gravity.LEFT:
                return paddingLeft;
            case Gravity.RIGHT:
                return width - paddingRight - textWidth - rightBarToEndParentPadding;
            case Gravity.CENTER:
            default:
                return (width - leftBarToStartParentPadding - rightBarToEndParentPadding) / 2f - textWidth / 2f - paddingRight + paddingLeft;
        }
    }

    private float calculateTextYWithoutLine(View child, Paint.FontMetrics fontMetrics) {
        float totalSpace = textSize + 2 * textPadding;
        if (!dividerAfterItem) {
            return child.getTop() - (totalSpace / 2) - (fontMetrics.ascent + fontMetrics.descent) / 2 - paddingBottom + paddingTop;
        } else {
            return child.getBottom() + (totalSpace / 2) - (fontMetrics.ascent + fontMetrics.descent) / 2 - paddingBottom + paddingTop;
        }
    }

    /*@Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);

        // Reset top offset for all items initially
        outRect.top = 0;

        if (showLineDivider) {
            // If the divider is 'after', only add top space for items that are not the first
            // If the divider is 'before', add top space for all items, including the first
            if (dividerAfterItem && position > 0) {
                outRect.top = textSize + 2 * textPadding + lineThickness;
            } else if (!dividerAfterItem) {
                outRect.top = textSize + 2 * textPadding + lineThickness;
            }
        }
    }*/
}