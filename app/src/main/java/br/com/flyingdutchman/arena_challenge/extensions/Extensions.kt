package br.com.flyingdutchman.arena_challenge.extensions

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.*
import android.view.View
import android.widget.ImageView
import br.com.flyingdutchman.arena_challenge.R
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

import com.google.android.material.snackbar.Snackbar
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun spannable(func: () -> SpannableString) = func()
private fun span(s: CharSequence, o: Any) =
    (if (s is String) SpannableString(s) else s as? SpannableString
        ?: SpannableString("")).apply { setSpan(o, 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) }

operator fun SpannableString.plus(s: SpannableString) = SpannableString(TextUtils.concat(this, s))
operator fun SpannableString.plus(s: String) = SpannableString(TextUtils.concat(this, s))

fun bold(s: CharSequence) =
    span(
        s,
        StyleSpan(Typeface.BOLD)
    )

fun italic(s: CharSequence) =
    span(
        s,
        StyleSpan(Typeface.ITALIC)
    )

fun underline(s: CharSequence) =
    span(s, UnderlineSpan())

fun strike(s: CharSequence) =
    span(s, StrikethroughSpan())

fun sup(s: CharSequence) =
    span(s, SuperscriptSpan())

fun sub(s: CharSequence) =
    span(s, SubscriptSpan())

fun size(size: Float, s: CharSequence) =
    span(
        s,
        RelativeSizeSpan(size)
    )

fun color(color: Int, s: CharSequence) =
    span(
        s,
        ForegroundColorSpan(color)
    )

fun background(color: Int, s: CharSequence) =
    span(
        s,
        BackgroundColorSpan(color)
    )

fun url(url: String, s: CharSequence) =
    span(s, URLSpan(url))


fun ImageView.load(url: String?, circle: Boolean = false) {
    val options = RequestOptions()
        .priority(Priority.NORMAL)
        .diskCacheStrategy(DiskCacheStrategy.DATA)

    if (circle) {
        options.circleCrop()
    }

    Glide
        .with(this.context)
        .load(url)
        .apply(options)
        .into(this@load)
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.snackBar(
    snackBarText: CharSequence,
    duration: Int = Snackbar.LENGTH_LONG,
    messageAction: CharSequence = "Reload",
    listener: () -> Unit? = {}
): Snackbar {
    return Snackbar.make(this, snackBarText, duration)
        .setAction(messageAction) {
            listener.invoke()
        }
}

val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun String.parseIsoDateFormat(): Date? {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    try {
        return sdf.parse(this)
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    return null
}


fun Date.formatElapsedTime(context: Context): String {
    val to: Calendar = Calendar.getInstance()
    return when (val elapsedDays = elapsedDays(to)) {
        0 -> {
            val elapsedHours = elapsedHours(to)
            val elapsedMinutes = elapsedMinutes(to)
            when {
                elapsedHours > 0 -> context.getString(
                    R.string.text_elapsed_hours,
                    elapsedHours
                )
                elapsedMinutes > 0 -> context.getString(
                    R.string.text_elapsed_minutes,
                    elapsedMinutes
                )
                else -> context.getString(R.string.text_elapsed_now)
            }
        }
        1 -> context.getString(R.string.text_yesterday)
        in 2..7 -> context.getString(
            R.string.text_few_days,
            elapsedDays
        )
        else -> context.getString(
            R.string.text_many_days,
            asCalendar().getDisplayName(
                Calendar.MONTH,
                Calendar.SHORT,
                Locale.getDefault()
            ),
            asCalendar().get(Calendar.DAY_OF_MONTH)
        )
    }
}

fun Date.elapsedDays(to: Calendar): Int {
    return ((asCalendar().timeInMillis - to.timeInMillis) / (24 * 60 * 60 * 1000)).toInt()
}

fun Date.elapsedHours(to: Calendar) =
    (to.timeInMillis - asCalendar().timeInMillis).toInt() / (60 * 60 * 1000)

fun Date.elapsedMinutes(to: Calendar) =
    (to.timeInMillis - asCalendar().timeInMillis).toInt() / (60 * 1000)

fun Date.asCalendar(): Calendar =
    Calendar.getInstance().apply {
        time = this@asCalendar
    }


inline fun <reified T : Any> Activity.extra(key: String, defaultValue: T): T {
    return when (T::class) {
        String::class -> if (intent != null && intent.hasExtra(key)) intent.getStringExtra(key) as T else defaultValue
        Int::class -> intent?.getIntExtra(key, defaultValue as Int) as T
        Boolean::class -> intent?.getBooleanExtra(key, defaultValue as Boolean) as T
        Float::class -> intent?.getFloatExtra(key, defaultValue as Float) as T
        Long::class -> intent?.getLongExtra(key, defaultValue as Long) as T
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}