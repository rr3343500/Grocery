package tech.iwish.onhome;

import android.text.TextPaint;
import android.text.style.URLSpan;

class URLSpanNoUnderline extends URLSpan {
    public URLSpanNoUnderline(String url) {
        super(url);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(false);
    }
}