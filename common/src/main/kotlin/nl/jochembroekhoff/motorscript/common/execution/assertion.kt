package nl.jochembroekhoff.motorscript.common.execution

import nl.jochembroekhoff.motorscript.common.messages.Attachable

inline fun internalAssert(
    condition: Boolean,
    description: String,
    attachmentsProvider: () -> List<Attachable>
) {
    if (!condition) {
        throw InternalAssertionExecutionException(description, attachmentsProvider())
    }
}

inline fun internalAssert(condition: Boolean, descriptionCreator: () -> String?) {
    if (!condition) {
        descriptionCreator().let {
            if (it == null) {
                throw InternalAssertionExecutionException()
            } else {
                throw InternalAssertionExecutionException(it)
            }
        }
    }
}
