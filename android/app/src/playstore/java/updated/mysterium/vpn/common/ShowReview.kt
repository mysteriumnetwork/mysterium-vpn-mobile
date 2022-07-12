package updated.mysterium.vpn.common

import android.app.Activity
import android.util.Log
import com.google.android.play.core.review.ReviewManagerFactory
import updated.mysterium.vpn.common.extensions.TAG

fun Activity.showReview() {
    val manager = ReviewManagerFactory.create(this)
    val request = manager.requestReviewFlow()
    request.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            manager.launchReviewFlow(this, task.result)
        } else {
            Log.e(TAG, task.exception?.localizedMessage ?: task.exception.toString())
        }
    }
}
