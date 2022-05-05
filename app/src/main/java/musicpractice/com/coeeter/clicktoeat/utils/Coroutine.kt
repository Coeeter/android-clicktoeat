package musicpractice.com.coeeter.clicktoeat.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

object Coroutine {

    fun main(lambda: suspend () -> Unit) =
        CoroutineScope(Dispatchers.Main).launch { lambda() }

}