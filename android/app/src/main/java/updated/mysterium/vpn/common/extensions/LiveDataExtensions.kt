package updated.mysterium.vpn.common.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData

fun <T> liveDataResult(block: suspend () -> T): LiveData<Result<T>> {
    return liveData {
        try {
            emit(Result.success(block()))
        } catch (error: Throwable) {
            emit(Result.failure<T>(error))
        }
    }
}

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(value: T) {
            observer.onChanged(value)
            removeObserver(this)
        }
    })
}

