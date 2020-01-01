package nl.jochembroekhoff.motorscript.common.pack

class PackIndexBuilder {

    private val root: MutableMap<NamespaceTypeCombo, MutableMap<List<String>, PackEntry>> = HashMap()

    fun addEntry(entry: PackEntry) {
        root.computeIfAbsent(NamespaceTypeCombo(entry.namespace, entry.type)) { HashMap() }[entry.name] = entry
    }

    fun build(): PackIndex {
        return PackIndex(root)
    }
}
