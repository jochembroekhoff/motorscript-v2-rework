package nl.jochembroekhoff.motorscript.gen

import java.util.*

data class GenOutput(
    val element: GenElement,
    val content: MutableList<String>,
    val parent: GenOutput? = null
) {
    companion object {
        /**
         * Create a new root [GenOutput] instance. This accounts for the entry point of a function.
         */
        fun createRoot(): GenOutput {
            return GenOutput(GenElement("ep", -1), LinkedList())
        }
    }

    fun nest(dispatcher: Dispatcher): GenOutput {
        return copy(
            element = GenElement(
                "nest",
                dispatcher.elementCounter.next("nest")
            ),
            content = LinkedList(),
            parent = this
        )
    }
}
