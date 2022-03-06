package net.foucry.pilldroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DrugDetailContract extends ActivityResultContract<Intent, Integer> {
    /**
     * Create an intent that can be used for {@link Activity#startActivityForResult}
     *
     * @param context Context
     * @param input Drug
     */
    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, Intent input) {
        Intent intent = new Intent(context, DrugDetailActivity.class);

        intent.putExtra("Drug", input.getExtras());
        return (intent);
    }

    /**
     * Convert result obtained from  to O
     * @param resultCode Integer
     * @param intent Intent
     * @return Integer
     */
    @Override
    public Integer parseResult(int resultCode, @Nullable Intent intent) {

        return resultCode;
    }
}
