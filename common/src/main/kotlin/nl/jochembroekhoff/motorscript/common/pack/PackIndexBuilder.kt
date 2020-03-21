package nl.jochembroekhoff.motorscript.common.pack

class PackIndexBuilder {

    private val root: MutableMap<NamespaceTypeCombo, MutableMap<List<String>, PackEntry>> = HashMap()

    fun addEntry(entry: PackEntry) {
        root.computeIfAbsent(NamespaceTypeCombo(entry.base.namespace, entry.type)) { HashMap() }[entry.base.name + entry.name] = entry
    }

    fun build(): PackIndex {
        return PackIndex(root)
    }
}
