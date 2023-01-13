package com.gjjy.googlebillinglib;

import android.content.Context;

public class GooglePlayProduct{
    private GooglePlayProduct() {}

    public static GooglePlayProduct get() {
        return HANDLER.I;
    }
    private static final class HANDLER {
        private static final GooglePlayProduct I = new GooglePlayProduct();
    }

    public Client create(Context context) { return new Client( context ); }
}
