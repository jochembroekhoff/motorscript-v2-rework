# Introduction

What is a reference? A reference is special kind of IR vertex that points to another piece of information somewhere.
This 'pointer' may or may not be concrete, i.e. a reference can be in multiple states: not resolved yet, unresolved,
and resolved.

# Different types of references

## Full reference

A reference that is 'full' contains a known namespace and a known name part. This basically means that such a reference
is context-free.

Full references are either specified with an explicit namespace (`<namespace>:<path>`), or an implicit namespace
(`:<name>`). The value of the implicit namespace is equivalent to the namespace of the file in which such a reference is
declared. A reference with an implicit part can be considered full, since the implicit namespace is already known at
parse time.

Examples: `minecraft:tick`, `:a\b`.

## Partial reference

References that are not context-free, i.e. non-full references, will depend on the context in which they are declared in
order to be resolved unambiguously.

An example of a partial reference is a reference to a local variable.
Consider this piece of MotorScript code:

```motorscript
func example -> Int
{
    var a = 1
    return ++a
}
``` 

The expression `++a` references the local variable `a`. There is simply only one character, namely _a_ that indicates
the reference. Since any identifier will be transformed into a reference, simply a non-keyword word is also a reference.
The point here is that there is no namespace associated with the reference (there is no colon nor text preceding a
colon) in this identifier. This is thus processed as a partial reference. The only known thing is that the concrete
reference ends with 'a'. Variables, and especially local variables, can't really be expressed as a full reference
anyways, as we'll see later. However, at this point it is still a partial reference and the piece 'a' can still refer to
anything, since it's not immediately known that this 'a' is meant to point to the local variable from the third line.

To give another example that involves invocation of a function, consider the following piece of MotorScript code:
```motorscript
use text\tellraw
user func hello
{
    tellraw(@s, "Hello!")
}
```

This piece of code actually has two references in it, namely `text\tellraw` (line 1) and `tellraw` (line 4).
Both references are processed in a different way (we'll get to that), but in the end they are both just plain partial
references.

The first reference exists of two path elements, `text` followed by `tellraw`. This means that it's referring to the
`tellraw` item that is a direct child of an item with the name `text`. By the rules for resolving import references,
this will be resolved to `minecraft:text\tellraw` (assuming no local override exist).

The second reference is just referring to something ending in the name `tellraw`. This is obviously pointing to the item
that was imported with the use-statement in line 1, but if that were not there, it might also just refer to a function
declared in the same file that happens to be called `tellraw`.

# Reference sources

There are quite a lot of place in which a reference can be declared. The following sub-sections cover all possible
places in which a reference is declared.

## Top-level declarations

Top-level declarations is one of the most obvious sources of reference declaration. It's simply all the functions and
containers that are declared at the top-level of a file. Something is at 'top-level' in a file when there exists no
wrapping scope for the item. Generally this just means that, if properly formatted, there is no indentation in front of
such an item.

From inside any function body, all top-level items in the same file are accessible. This is what one would expect.
The possibility to refer from inside a top-level expression to other top-level containers is limited. From such an
expression only top-level containers that were declared 'above' are reachable.
Cycles can be created if this were not the case. This is also why non-builtin functions are not available in top-level
expressions.

Reachability of a top-level item from other places depends on its access level. See (TODO) for more information about
access levels and more about modifiers in general.

One important modifier that's relevant to here is the `default` modifier. This modifier can be given to at most one
top-level item. This top-level item should be identified by the same name as the file in which it resides minus the
`.mos` extension. The item with this modifier will be exported under the full reference of the current file (whereas all
other non-private top-level items would be exported as a sub-item under the full reference of the file in which it is
declared).

## Local declarations

Any scope can have local declarations. These are (currently) limited to containers only.
Such containers can, as one would expect, only be reached after their initial declaration.

A locally declared container is available in all remaining statements in the scope, and it's available in sub-scopes.

## Standard library

There's not much to say about the standard library. It simply provides some references, depending on the targeted
platform and version of that platform. Items from the standard library are available in all places, they do still need
to be imported though.

## Dependencies

Practically identical to the standard library, since the standard library is also just a dependency (albeit an implied
one if not specified explicitly).

## Build configuration

**NOTE**: This is a planned feature.

The build configuration may contain a section that declares certain tags and resources.

# Reference meta data

As seen above, references pop up in a lot of places. But as described, not all references are reachable from all places.
This section tries to make clear how this differentiation is dealt with internally. It's a step up to the section about
resolving partial references.

...

# Resolving of partial references

Only partial references need resolving at all. However, the thing is that in practice most references are actually
partial.

...
