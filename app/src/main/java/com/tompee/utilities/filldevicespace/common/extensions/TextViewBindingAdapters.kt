package com.tompee.utilities.filldevicespace.common.extensions

import android.os.Build
import android.text.Html
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.databinding.BindingAdapter

/**
 * Converts a string resource into a string and styles it using HTML before setting it into an [TextView]
 * This variant accepts a text input and is normally used when the string has been manually resolved.
 * If argument is a string resource ID, use [setHtmlTextResource]
 *
 * @param string the string to style and set
 */
@Suppress("DEPRECATION")
@BindingAdapter("htmlText")
internal fun TextView.setHtmlText(string: String) {
    text = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
        Html.fromHtml(string, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(string)
    }
}

/**
 * Converts a string resource into a string and styles it using HTML before setting it into an [TextView]
 * This variant accepts a text input and is normally used when the string has been manually resolved
 * If argument is a string, use [setHtmlText]
 *
 * @param id the string resource ID to style and set
 */
@Suppress("DEPRECATION")
@BindingAdapter("htmlTextId")
internal fun TextView.setHtmlTextResource(@StringRes id: Int) {
    val resolvedText = context.getString(id)
    text = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
        Html.fromHtml(resolvedText, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(resolvedText)
    }
}